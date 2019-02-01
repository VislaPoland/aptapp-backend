package com.creatix.service.message;

import com.creatix.domain.dao.*;
import com.creatix.domain.entity.store.Property;
import com.creatix.domain.entity.store.account.Account;
import com.creatix.domain.entity.store.notification.PersonalMessage;
import com.creatix.domain.entity.store.notification.PersonalMessageGroup;
import com.creatix.domain.entity.store.notification.PersonalMessageNotification;
import com.creatix.domain.entity.store.notification.PersonalMessagePhoto;
import com.creatix.domain.enums.AccountRole;
import com.creatix.domain.enums.NotificationStatus;
import com.creatix.domain.enums.message.PersonalMessageDeleteStatus;
import com.creatix.domain.enums.message.PersonalMessageStatusType;
import com.creatix.message.PushNotificationTemplateProcessor;
import com.creatix.message.SmsMessageSender;
import com.creatix.message.push.GenericPushNotification;
import com.creatix.message.template.push.NewPersonalMessageTemplate;
import com.creatix.message.template.sms.TenantPersonalMessageTemplate;
import com.creatix.security.AuthorizationManager;
import com.creatix.security.RoleSecured;
import com.creatix.service.AttachmentService;
import freemarker.template.TemplateException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * Created by Tomas Michalek on 26/05/2017.
 */
@Service
@Transactional
public class PersonalMessageService {

    private static final Logger logger = LoggerFactory.getLogger(PersonalMessageService.class);

    @Autowired
    private PersonalMessageDao personalMessageDao;
    @Autowired
    private AuthorizationManager authorizationManager;
    @Autowired
    private PropertyDao propertyDao;
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private PushNotificationSender pushNotificationSender;
    @Autowired
    private PushNotificationTemplateProcessor templateProcessor;
    @Autowired
    private NotificationDao notificationDao;
    @Autowired
    private SmsMessageSender smsMessageSender;
    @Autowired
    private PersonalMessageGroupDao personalMessageGroupDao;
    @Autowired
    private AttachmentService attachmentService;

    public List<PersonalMessage> sendMessageToPropertyManagers(@NotNull Long propertyId, @NotNull final String title, @NotNull final String content) {
        Objects.requireNonNull(propertyId, "Property id can not be null");
        Objects.requireNonNull(title, "Title can not be null");
        Objects.requireNonNull(content, "Content can not be null");
        final Property property = propertyDao.findById(propertyId);
        if (null == property) {
            throw new EntityNotFoundException(String.format("Entity with id %d not found", propertyId));
        }


        //throws exception in case of security violation
        authorizationManager.checkRead(property);

        final PersonalMessageGroup personalMessageGroup = new PersonalMessageGroup();
        personalMessageGroupDao.persist(personalMessageGroup);

        final List<PersonalMessage> messages = property.getManagers().parallelStream().map(
                recipientAccount -> {
                    Account currentAccount = authorizationManager.getCurrentAccount();
                    PersonalMessage personalMessage = new PersonalMessage();
                    personalMessage.setFromAccount(currentAccount);
                    personalMessage.setToAccount(recipientAccount);
                    personalMessage.setTitle(title);
                    personalMessage.setContent(content);
                    personalMessage.setMessageStatus(PersonalMessageStatusType.PENDING);
                    personalMessageGroup.addMessage(personalMessage);
                    personalMessageDao.persist(personalMessage);

                    try {
                        dispatchPersonalMessage(personalMessage);
                    }
                    catch ( IOException | TemplateException e ) {
                        logger.error(String.format("Failed to dispatch message to account %d", recipientAccount.getId()), e);
                    }
                    
                    return personalMessage;
                }
        ).collect(Collectors.toList());

        Long notificationId = createPersonalMessageNotification(personalMessageGroup);
        messages.forEach(personalMessage -> personalMessage.setNotificationId(notificationId));

        return messages;
    }

    @RoleSecured({AccountRole.Administrator, AccountRole.PropertyOwner, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.Security, AccountRole.Maintenance})
    public List<PersonalMessage> sendMessageToTenant(@NotNull List<Long> tenantAccountIdList, @NotNull final String title, @NotNull final String content) {
        Objects.requireNonNull(tenantAccountIdList, "Tenant account id list can not be null");
        Objects.requireNonNull(content, "Content can not be null");

        final PersonalMessageGroup personalMessageGroup = new PersonalMessageGroup();
        personalMessageGroupDao.persist(personalMessageGroup);

        final List<PersonalMessage> messages = new ArrayList<>(tenantAccountIdList.size());
        for ( Long tenantAccountId : tenantAccountIdList ) {

            final Account recipientAccount = accountDao.findById(tenantAccountId);
            if ( null == recipientAccount ) {
                throw new EntityNotFoundException(String.format("Tenant with id %d not found", tenantAccountId));
            }
            if ( (recipientAccount.getRole() != AccountRole.Tenant) && (recipientAccount.getRole() != AccountRole.SubTenant) ) {
                throw new SecurityException(String.format("Recipient %d must be tenant or sub-tenant", recipientAccount.getId()));
            }

            if ( authorizationManager.canSendMessageTo(recipientAccount) ) {
                final PersonalMessage personalMessage = new PersonalMessage();
                personalMessage.setFromAccount(authorizationManager.getCurrentAccount());
                personalMessage.setToAccount(recipientAccount);
                personalMessage.setTitle(title);
                personalMessage.setContent(content);
                personalMessage.setMessageStatus(PersonalMessageStatusType.PENDING);
                personalMessageGroup.addMessage(personalMessage);
                personalMessageDao.persist(personalMessage);

                try {
                    dispatchPersonalMessage(personalMessage);
                }
                catch ( IOException | TemplateException e ) {
                    logger.error(String.format("Failed to dispatch message to account %d", recipientAccount.getId()), e);
                }

                if ( StringUtils.isNotBlank(recipientAccount.getPrimaryPhone()) ) {
                    try {
                        smsMessageSender.send(new TenantPersonalMessageTemplate(recipientAccount.getPrimaryPhone(), recipientAccount.getProperty().getName()));
                    }
                    catch ( Exception e ) {
                        logger.error(String.format("Failed to send sms for account %d to phone number %s", recipientAccount.getId(), recipientAccount.getPrimaryPhone()), e);
                    }
                }

                messages.add(personalMessage);
            }
            else {
                throw new SecurityException(String.format("You are not allowed to sent message to user %d", tenantAccountId));
            }
        }

        Long notificationId = createPersonalMessageNotification(personalMessageGroup);
        messages.forEach(personalMessage -> personalMessage.setNotificationId(notificationId));

        return messages;
    }

    private void dispatchPersonalMessage(@NotNull PersonalMessage personalMessage) throws IOException, TemplateException {
        Objects.requireNonNull(personalMessage, "Personal message can not be null!");
        Objects.requireNonNull(personalMessage.getToAccount(), "Recipient is missing");
        Objects.requireNonNull(personalMessage.getFromAccount(), "Sender is missing");
        if (personalMessage.getMessageStatus() != PersonalMessageStatusType.PENDING) {
            throw new IllegalArgumentException("Message is in invalid state " + personalMessage.getMessageStatus());
        }

        final GenericPushNotification notification = new GenericPushNotification();
        notification.setMessage(templateProcessor.processTemplate(new NewPersonalMessageTemplate(personalMessage)));
        notification.setBadgeCount(1);
        notification.setTitle("New personal message");

        pushNotificationSender.sendNotification(notification, personalMessage.getToAccount());

        personalMessage.setMessageStatus(PersonalMessageStatusType.DELIVERED);
        personalMessageDao.persist(personalMessage);
    }

    private Long createPersonalMessageNotification(@NotNull PersonalMessageGroup personalMessageGroup) {
        final int maxDescriptionLength = 100;
        final PersonalMessage message = personalMessageGroup.getMessages().stream().findFirst().orElseThrow(() -> new IllegalStateException("Message group is empty"));
        final PersonalMessageNotification personalMessageNotification = new PersonalMessageNotification();
        personalMessageNotification.setPersonalMessageGroup(personalMessageGroup);
        personalMessageNotification.setAuthor(authorizationManager.getCurrentAccount());
        personalMessageNotification.setRecipient(message.getToAccount());
        personalMessageNotification.setDescription(StringUtils.abbreviate(message.getContent().trim(), maxDescriptionLength));
        personalMessageNotification.setStatus(NotificationStatus.Pending);
        personalMessageNotification.setTitle(message.getTitle());
        personalMessageNotification.setProperty(message.getToAccount().getProperty());
        notificationDao.persist(personalMessageNotification);

        return personalMessageNotification.getId();
    }

    @RoleSecured
    public List<PersonalMessage> listSentMessagesForCurrentUser(long offset, long limit) {
        return personalMessageDao.listUserSentMessage(authorizationManager.getCurrentAccount(), offset, limit);
    }

    @RoleSecured
    public List<PersonalMessage> listReceivedMessagesForCurrentUser(long offset, long limit) {
        return personalMessageDao.listUserReceivedMessage(authorizationManager.getCurrentAccount(), offset, limit);
    }

    @RoleSecured
    public PersonalMessage getMessageById(long messageId) {
        PersonalMessage personalMessage = personalMessageDao.findById(messageId);
        if (null == personalMessage) {
            throw new EntityNotFoundException(String.format("Entity with id %d not found", messageId));
        }

        Account currentAccount = authorizationManager.getCurrentAccount();

        // If sender or recipient, and not deleted
        if (
                personalMessage.getDeleteStatus() != PersonalMessageDeleteStatus.BOTH && (
                    (currentAccount.equals(personalMessage.getFromAccount()) && personalMessage.getDeleteStatus() != PersonalMessageDeleteStatus.SENDER) ||
                    (currentAccount.equals(personalMessage.getToAccount()) && personalMessage.getDeleteStatus() != PersonalMessageDeleteStatus.RECIPIENT)
                )
           ) {

            // mark the message as read?
            if (currentAccount.equals(personalMessage.getFromAccount()) && personalMessage.getMessageStatus() != PersonalMessageStatusType.READ) {
                personalMessage.setMessageStatus(PersonalMessageStatusType.READ);
                personalMessageDao.persist(personalMessage);
            }

            return personalMessage;
        }

        throw new EntityNotFoundException(String.format("Entity with id %d not found", messageId));
    }

    private ConcurrentMap<Long, Long> deleteLock = new ConcurrentHashMap<>();

    @RoleSecured
    public PersonalMessage deleteMessage(long messageId) {
        synchronized (getLockObject(messageId)) {
            try {
                PersonalMessage messageById = getMessageById(messageId);
                if (null != messageById) {
                    Account currentAccount = authorizationManager.getCurrentAccount();
                    if (messageById.getDeleteStatus() == PersonalMessageDeleteStatus.NONE) {
                        //No one deleted message yet, so set one of them
                        if (messageById.getFromAccount().equals(currentAccount)) {
                            messageById.setDeleteStatus(PersonalMessageDeleteStatus.SENDER);
                        } else {
                            messageById.setDeleteStatus(PersonalMessageDeleteStatus.RECIPIENT);
                        }
                    } else {
                        // one of them already deleted message. So set to both
                        messageById.setDeleteStatus(PersonalMessageDeleteStatus.BOTH);
                    }

                    personalMessageDao.persist(messageById);
                }

                return messageById;
            } finally {
                deleteLock.remove(messageId);
            }
        }
    }

    private synchronized Object getLockObject(Long key) {
        deleteLock.putIfAbsent(key, key);
        return deleteLock.get(key);
    }

    public PersonalMessage storePersonalMessagePhotos(MultipartFile[] files, long personalMessageId) {
        final PersonalMessage personalMessage = personalMessageDao.findById(personalMessageId);
        List<PersonalMessagePhoto> photoStoreList;
        try {
            photoStoreList = attachmentService.storeAttachments(files, foreignKeyObject -> {
                PersonalMessagePhoto personalMessagePhoto = new PersonalMessagePhoto();
                personalMessagePhoto.setPersonalMessage(personalMessage);
                return personalMessagePhoto;
            }, personalMessage, PersonalMessagePhoto.class);
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to store photo for personal message.", e);
        }

        personalMessage.getPersonalMessagePhotos().addAll(photoStoreList);
        personalMessageDao.persist(personalMessage);

        return personalMessage;
    }
}
