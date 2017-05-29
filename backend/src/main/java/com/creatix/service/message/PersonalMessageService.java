package com.creatix.service.message;

import com.creatix.domain.dao.AccountDao;
import com.creatix.domain.dao.NotificationDao;
import com.creatix.domain.dao.PersonalMessageDao;
import com.creatix.domain.dao.PropertyDao;
import com.creatix.domain.entity.store.Property;
import com.creatix.domain.entity.store.account.Account;
import com.creatix.domain.entity.store.notification.PersonalMessage;
import com.creatix.domain.entity.store.notification.PersonalMessageNotification;
import com.creatix.domain.enums.AccountRole;
import com.creatix.domain.enums.NotificationStatus;
import com.creatix.domain.enums.message.PersonalMessageDeleteStatus;
import com.creatix.domain.enums.message.PersonalMessageStatusType;
import com.creatix.message.PushNotificationTemplateProcessor;
import com.creatix.message.push.GenericPushNotification;
import com.creatix.message.template.push.NewPersonalMessageTemplate;
import com.creatix.security.AuthorizationManager;
import com.creatix.security.RoleSecured;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.io.IOException;
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

    @Autowired
    private PersonalMessageDao personalMessageDao;
    @Autowired
    private AuthorizationManager authorizationManager;
    @Autowired
    private PropertyDao propertyDao;
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private PushNotificationService pushNotificationService;
    @Autowired
    private PushNotificationTemplateProcessor templateProcessor;
    @Autowired
    private NotificationDao notificationDao;

    public List<PersonalMessage> sendMessageToPropertyManagers(long propertyId, @NotNull final String title, @NotNull final String content) {
        Objects.requireNonNull(title, "Title can not be null");
        Objects.requireNonNull(content, "Content can not be null");
        Property property = propertyDao.findById(propertyId);
        if (null == property) {
            throw new EntityNotFoundException(String.format("Entity with id %d not found", propertyId));
        }

        //throws exception in case of security violation
        authorizationManager.checkRead(property);

        return property.getManagers().parallelStream().map(
                propertyManager -> {
                    Account currentAccount = authorizationManager.getCurrentAccount();
                    PersonalMessage personalMessage = new PersonalMessage();
                    personalMessage.setFromAccount(currentAccount);
                    personalMessage.setToAccount(propertyManager);
                    personalMessage.setTitle(title);
                    personalMessage.setContent(content);
                    personalMessage.setMessageStatus(PersonalMessageStatusType.PENDING);
                    personalMessageDao.persist(personalMessage);

                    try {
                        dispatchPersonalMessage(personalMessage);
                    } catch (IOException | TemplateException e) {
                        //TODO: log error
                    }

                    return personalMessage;
                }
        ).collect(Collectors.toList());
    }

    @RoleSecured({AccountRole.Administrator, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager})
    public PersonalMessage sendMessageToTenant(long tenantAccountId, @NotNull final String title, @NotNull final String content) {
        Objects.requireNonNull(title, "Title can not be null");
        Objects.requireNonNull(content, "Content can not be null");

        Account recipientAccount = accountDao.findById(tenantAccountId);
        if (null == recipientAccount) {
            throw new EntityNotFoundException(String.format("Tenant with id %d not found", tenantAccountId));
        }

        Account currentAccount = authorizationManager.getCurrentAccount();
        if (authorizationManager.canSendMessage(currentAccount, recipientAccount)) {
            PersonalMessage personalMessage = new PersonalMessage();
            personalMessage.setFromAccount(currentAccount);
            personalMessage.setToAccount(recipientAccount);
            personalMessage.setTitle(title);
            personalMessage.setContent(content);
            personalMessage.setMessageStatus(PersonalMessageStatusType.PENDING);
            personalMessageDao.persist(personalMessage);

            try {
                dispatchPersonalMessage(personalMessage);
            } catch (IOException | TemplateException e) {
                //TODO: log error
            }

            return personalMessage;
        }

        throw new SecurityException(String.format("You are not allowed to sent message to user %d", tenantAccountId));
    }

    public void dispatchPersonalMessage(@NotNull PersonalMessage personalMessage) throws IOException, TemplateException {
        Objects.requireNonNull(personalMessage, "Personal message can not be null!");
        if (personalMessage.getMessageStatus() != PersonalMessageStatusType.PENDING) {
            throw new IllegalArgumentException("Message is in invalid state " + personalMessage.getMessageStatus());
        }

        final GenericPushNotification notification = new GenericPushNotification();
        notification.setMessage(templateProcessor.processTemplate(new NewPersonalMessageTemplate(personalMessage)));
        notification.setTitle("You have new personal message");

        PersonalMessageNotification personalMessageNotification = new PersonalMessageNotification();
        personalMessageNotification.setPersonalMessage(personalMessage);
        personalMessageNotification.setAuthor(personalMessage.getFromAccount());
        personalMessageNotification.setDescription(notification.getMessage());
        personalMessageNotification.setStatus(NotificationStatus.Pending);
        personalMessageNotification.setTitle(notification.getTitle());
        notificationDao.persist(personalMessageNotification);

        pushNotificationService.sendNotification(notification, personalMessage.getToAccount());

        personalMessage.setMessageStatus(PersonalMessageStatusType.DELIVERED);
        personalMessageDao.persist(personalMessage);
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
                    if (null == messageById.getDeleteStatus()) {
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


}
