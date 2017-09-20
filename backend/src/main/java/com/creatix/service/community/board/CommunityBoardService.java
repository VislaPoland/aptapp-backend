package com.creatix.service.community.board;

import com.creatix.domain.dao.NotificationDao;
import com.creatix.domain.dao.PropertyDao;
import com.creatix.domain.dao.community.board.CommunityBoardCategoryDao;
import com.creatix.domain.dao.community.board.CommunityBoardCommentDao;
import com.creatix.domain.dao.community.board.CommunityBoardItemDao;
import com.creatix.domain.dto.community.board.CommunityBoardCommentEditRequest;
import com.creatix.domain.dto.community.board.CommunityBoardItemEditRequest;
import com.creatix.domain.dto.community.board.SearchRequest;
import com.creatix.domain.entity.store.Property;
import com.creatix.domain.entity.store.account.Account;
import com.creatix.domain.entity.store.community.board.CommunityBoardCategory;
import com.creatix.domain.entity.store.community.board.CommunityBoardComment;
import com.creatix.domain.entity.store.community.board.CommunityBoardItem;
import com.creatix.domain.entity.store.community.board.CommunityBoardItemPhoto;
import com.creatix.domain.entity.store.notification.CommentNotification;
import com.creatix.domain.entity.store.notification.CommunityBoardItemUpdatedSubscriberNotification;
import com.creatix.domain.enums.NotificationStatus;
import com.creatix.domain.enums.community.board.CommunityBoardCommentStatusType;
import com.creatix.domain.enums.community.board.CommunityBoardStatusType;
import com.creatix.domain.mapper.CommunityBoardMapper;
import com.creatix.message.PushNotificationTemplateProcessor;
import com.creatix.message.push.GenericPushNotification;
import com.creatix.message.template.push.community.board.CommunityItemUpdatedSubscriberTemplate;
import com.creatix.message.template.push.community.board.NewCommunityItemCommentReplyTemplate;
import com.creatix.message.template.push.community.board.NewCommunityItemCommentTemplate;
import com.creatix.security.AuthorizationManager;
import com.creatix.security.RoleSecured;
import com.creatix.service.AttachmentService;
import com.creatix.service.message.PushNotificationService;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nonnull;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Tomas Michalek on 10/05/2017.
 */
@Service
@Transactional
public class CommunityBoardService {

    @Autowired
    private CommunityBoardItemDao communityBoardItemDao;
    @Autowired
    private CommunityBoardCommentDao communityBoardCommentDao;
    @Autowired
    private PropertyDao propertyDao;
    @Autowired
    private AuthorizationManager authorizationManager;
    @Autowired
    private CommunityBoardCategoryDao communityBoardCategoryDao;
    @Autowired
    private CommunityBoardMapper communityBoardMapper;
    @Autowired
    private AttachmentService attachmentService;
    @Autowired
    private PushNotificationTemplateProcessor templateProcessor;
    @Autowired
    private NotificationDao notificationDao;
    @Autowired
    private PushNotificationService pushNotificationService;

    public List<CommunityBoardItem> listBoardItemsForProperty(long propertyId, Long ownerId, List<CommunityBoardStatusType> statusTypes, Long startId, long pageSize) {
        Property property = getProperty(propertyId);

        return communityBoardItemDao.listByPropertyAndStatus(property, ownerId, statusTypes, startId, pageSize);
    }

    @Nonnull
    public List<CommunityBoardItem> listBoardItemsForPropertyAndCategory(long propertyId, Long ownerId, List<CommunityBoardStatusType> statusTypes, List<Long> categoryIdList, Long startId, long pageSize) {
        Property property = getProperty(propertyId);

        List<CommunityBoardCategory> categoryList = categoryIdList
                .stream()
                .map(categoryId -> communityBoardCategoryDao.findById(categoryId))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return communityBoardItemDao.listByPropertyAnCategories(property, ownerId, statusTypes, categoryList, startId, pageSize);
    }

    @Nonnull
    public List<CommunityBoardItem> searchBoardItemsForProperty(long propertyId, List<CommunityBoardStatusType> statusTypes, long pageSize, SearchRequest searchRequest) {
        Objects.requireNonNull(searchRequest, "Search request can not be null");

        Property property = getProperty(propertyId);
        CommunityBoardCategory communityBoardCategory = null;
        if (null != searchRequest.getCommunityBoardCategoryId()) {
            communityBoardCategory = communityBoardCategoryDao.findById(searchRequest.getCommunityBoardCategoryId());
        }
        return communityBoardItemDao.searchFromRequest(property, statusTypes, pageSize, searchRequest, communityBoardCategory);
    }

    @Nonnull
    public CommunityBoardItem createNewBoardItemFromRequest(long propertyId, CommunityBoardItemEditRequest request) {
        if (null != request.getId()) {
            throw new IllegalArgumentException(String.format("Request ID must be null, got %d instead", request.getId()));
        }

        Property property = getProperty(propertyId);

        CommunityBoardItem boardItem = communityBoardMapper.toCommunityBoardItem(request);
        boardItem.setProperty(property);
        boardItem.setAccount(authorizationManager.getCurrentAccount());

        communityBoardItemDao.persist(boardItem);

        return boardItem;
    }

    @Nonnull
    public CommunityBoardItem updateBoardItemFromRequest(CommunityBoardItemEditRequest request) {
        Objects.requireNonNull(request.getId(), "ID must not be null");

        CommunityBoardItem existingItem = getBoardItemById(request.getId());

        authorizationManager.checkCommunityBoardModifyAccess(
                existingItem.getAccount().getId(),
                existingItem.getProperty(),
                authorizationManager.getCurrentAccount()
        );

        communityBoardMapper.map(request, existingItem);
        communityBoardItemDao.persist(existingItem);

        dispatchSubscriberNotification(
                existingItem,
                null,
                new CommunityItemUpdatedSubscriberTemplate(existingItem, CommunityItemUpdatedSubscriberTemplate.EventType.UPDATED)
        );

        return existingItem;
    }

    @NotNull
    public CommunityBoardItem getBoardItemById(long itemId) throws SecurityException {
        CommunityBoardItem existingItem = getCommunityBoardItem(itemId);

        //Throws security exception
        authorizationManager.checkRead(existingItem.getProperty());

        return existingItem;
    }

    public CommunityBoardItem deleteBoardItemById(long itemId) {
        CommunityBoardItem communityBoardItem = getCommunityBoardItem(itemId);
        authorizationManager.checkCommunityBoardModifyAccess(
                communityBoardItem.getAccount().getId(),
                communityBoardItem.getProperty(),
                authorizationManager.getCurrentAccount()
        );

        dispatchSubscriberNotification(
                communityBoardItem,
                null,
                new CommunityItemUpdatedSubscriberTemplate(communityBoardItem, CommunityItemUpdatedSubscriberTemplate.EventType.DELETED)
        );

        communityBoardItem.setCommunityBoardStatus(CommunityBoardStatusType.DELETED);
        communityBoardItemDao.persist(communityBoardItem);



        return communityBoardItem;
    }

    @Nonnull
    private Property getProperty(long propertyId) throws SecurityException {
        Property property = propertyDao.findById(propertyId);
        if (null == property) {
            throw new EntityNotFoundException(String.format("Property %d does not exists", propertyId));
        }

        //Throws security exception
        authorizationManager.checkRead(property);
        return property;
    }

    @NotNull
    private CommunityBoardItem getCommunityBoardItem(long itemId) {
        CommunityBoardItem existingItem = communityBoardItemDao.findById(itemId);

        if (null == existingItem || existingItem.getCommunityBoardStatus() == CommunityBoardStatusType.DELETED) {
            throw new EntityNotFoundException(String.format("Entity %d not found", itemId));
        }
        //TODO: check security regarding the status of item
        return existingItem;
    }

    public CommunityBoardItem storeBoardItemPhotos(MultipartFile[] files, long itemId) {
        final CommunityBoardItem communityBoardItem = getCommunityBoardItem(itemId);
        List<CommunityBoardItemPhoto> photoList;
        try {
            photoList = attachmentService.storeAttachments(files, foreignKeyObject -> {
                CommunityBoardItemPhoto photo = new CommunityBoardItemPhoto();
                photo.setCommunityBoardItem(communityBoardItem);
                return photo;
            }, communityBoardItem, CommunityBoardItemPhoto.class);
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to store photo for business profile", e);
        }

        communityBoardItem.getPhotoList().addAll(photoList);
        communityBoardItemDao.persist(communityBoardItem);

        return communityBoardItem;
    }

    @RoleSecured
    @Nonnull
    public CommunityBoardComment createNewCommentFromRequest(long boardId, @NotNull CommunityBoardCommentEditRequest request) {
        Objects.requireNonNull(request, "Request must not be null");

        if (null != request.getId()) {
            throw new IllegalArgumentException(String.format("Request ID must be null, got %d instead", request.getId()));
        }

        // Throws entity not found
        CommunityBoardItem boardItem = getCommunityBoardItem(boardId);

        // Check permissions
        authorizationManager.checkRead(boardItem.getProperty());

        // Find parent comment
        CommunityBoardComment parentComment = null;
        if (null != request.getParentCommentId()) {
            parentComment = communityBoardCommentDao.findById(request.getParentCommentId());
            if (null == parentComment || parentComment.getStatus() == CommunityBoardCommentStatusType.DELETED) {
                throw new EntityNotFoundException(String.format("Parent comment with ID %d not found", request.getParentCommentId()));
            }

            if (! parentComment.getCommunityBoardItem().getId().equals(boardId)) {
                throw new IllegalArgumentException(String.format("Parent comment %d is not from board item %d", request.getParentCommentId(), boardItem.getId()));
            }
        }

        CommunityBoardComment comment = communityBoardMapper.toCommunityBoardComment(request);
        comment.setCommunityBoardItem(boardItem);
        comment.setAuthor(authorizationManager.getCurrentAccount());
        comment.setParentComment(parentComment);
        comment.setStatus(CommunityBoardCommentStatusType.APPROVED);

        communityBoardCommentDao.persist(comment);

        try {
            this.dispatchNotifications(comment);
        } catch (IOException | TemplateException e) {
            //TODO: log error
            e.printStackTrace();
        }

        return comment;

    }

    private Set<Account> getSubscribers(List<CommunityBoardComment> commentList) {
        // Guard
        if (null == commentList) return new HashSet<>();

        HashSet<Account> subscribers = new HashSet<>();
        commentList.stream()
                .filter(comment -> comment.getStatus() != CommunityBoardCommentStatusType.DELETED)
                .forEach(comment -> {
                    subscribers.add(comment.getAuthor());
                    subscribers.addAll(getSubscribers(comment.getChildComments()));
                });
        return subscribers;
    }

    private void dispatchSubscriberNotification(final CommunityBoardItem communityBoardItem, final CommunityBoardComment communityBoardComment, final CommunityItemUpdatedSubscriberTemplate template) {
        Objects.requireNonNull(communityBoardItem, "Board item can not be null!");
        Objects.requireNonNull(template, "Template can not be null!");

        final GenericPushNotification pushNotification = new GenericPushNotification();
        pushNotification.setTitle(template.getTitle());
        try {
            pushNotification.setMessage(templateProcessor.processTemplate(template));
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }

        Set<Account> subscribers = getSubscribers(this.listCommentsForBoardItem(communityBoardItem.getId()));
        subscribers.stream()
                .filter(account -> ! (account.equals(communityBoardItem.getAccount()) || account.equals(authorizationManager.getCurrentAccount())))
                .forEach(account -> {
                    CommunityBoardItemUpdatedSubscriberNotification  storedNotification = new CommunityBoardItemUpdatedSubscriberNotification();
                    storedNotification.setCommunityBoardItem(communityBoardItem);
                    storedNotification.setCommunityBoardComment(communityBoardComment);
                    storedNotification.setAuthor(communityBoardItem.getAccount());
                    storedNotification.setProperty(communityBoardItem.getProperty());
                    storedNotification.setRecipient(account);
                    storedNotification.setDescription(pushNotification.getMessage());
                    storedNotification.setStatus(NotificationStatus.Pending);
                    storedNotification.setTitle(pushNotification.getTitle());
                    notificationDao.persist(storedNotification);

                    try {
                        pushNotificationService.sendNotification(pushNotification, account);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });


    }

    private void dispatchNotificationToItemOwner(@NotNull CommunityBoardComment comment) throws IOException, TemplateException {
        final GenericPushNotification pushNotification = new GenericPushNotification();
        pushNotification.setMessage(templateProcessor.processTemplate(new NewCommunityItemCommentTemplate(comment)));
        pushNotification.setTitle("New comment");

        CommentNotification storedNotification = new CommentNotification();
        storedNotification.setCommunityBoardComment(comment);
        storedNotification.setAuthor(comment.getAuthor());
        storedNotification.setProperty(comment.getCommunityBoardItem().getProperty());
        storedNotification.setRecipient(comment.getCommunityBoardItem().getAccount());
        storedNotification.setDescription(pushNotification.getMessage());
        storedNotification.setStatus(NotificationStatus.Pending);
        storedNotification.setTitle(pushNotification.getTitle());
        notificationDao.persist(storedNotification);

        pushNotificationService.sendNotification(pushNotification, comment.getCommunityBoardItem().getAccount());
    }

    private void dispatchNotificationOfCommentReply(@NotNull CommunityBoardComment comment) throws IOException, TemplateException {
        final GenericPushNotification pushNotification = new GenericPushNotification();
        pushNotification.setMessage(templateProcessor.processTemplate(new NewCommunityItemCommentReplyTemplate((comment))));
        pushNotification.setTitle("New comment reply");

        CommentNotification storedNotification = new CommentNotification();
        storedNotification.setCommunityBoardComment(comment);
        storedNotification.setAuthor(comment.getAuthor());
        storedNotification.setProperty(comment.getCommunityBoardItem().getProperty());
        storedNotification.setRecipient(comment.getParentComment().getAuthor());
        storedNotification.setDescription(pushNotification.getMessage());
        storedNotification.setStatus(NotificationStatus.Pending);
        storedNotification.setTitle(pushNotification.getTitle());
        notificationDao.persist(storedNotification);

        pushNotificationService.sendNotification(pushNotification, comment.getCommunityBoardItem().getAccount());
    }

    private void dispatchNotifications(@NotNull CommunityBoardComment comment) throws IOException, TemplateException {
        Objects.requireNonNull(comment, "Comment can not be null!");

        if (null == comment.getParentComment()) {
            // root comment notify all people
            dispatchSubscriberNotification(
                    comment.getCommunityBoardItem(),
                    comment,
                    new CommunityItemUpdatedSubscriberTemplate(comment.getCommunityBoardItem(), CommunityItemUpdatedSubscriberTemplate.EventType.NEW_COMMENT)
            );
            dispatchNotificationToItemOwner(comment);
        } else {
            dispatchNotificationOfCommentReply(comment);
            if ( ! comment.getParentComment().getAuthor().equals(comment.getCommunityBoardItem().getAccount())) {
                dispatchNotificationToItemOwner(comment);
            }
        }

    }

    @RoleSecured
    public CommunityBoardComment updateCommentFromRequest(CommunityBoardCommentEditRequest request) {
        Objects.requireNonNull(request, "Request object must not be null");
        Objects.requireNonNull(request.getId(), "ID of object for edditing must not be null");

        CommunityBoardComment existingComment = getCommentById(request.getId());

        authorizationManager.checkCommunityBoardModifyAccess(
                existingComment.getAuthor().getId(),
                existingComment.getCommunityBoardItem().getProperty(),
                authorizationManager.getCurrentAccount()
        );

        communityBoardMapper.map(request, existingComment);
        communityBoardCommentDao.persist(existingComment);

        return existingComment;
    }

    private CommunityBoardComment getCommentById(long commentId) {
        CommunityBoardComment comment = communityBoardCommentDao.findById(commentId);
        if (null == comment || comment.getStatus() == CommunityBoardCommentStatusType.DELETED) {
            throw new EntityNotFoundException(String.format("Comment %d not found", commentId));
        }

        // Check access
        authorizationManager.checkRead(comment.getCommunityBoardItem().getProperty());

        return comment;
    }

    @RoleSecured
    public CommunityBoardComment deleteCommentById(long commentId) {
        CommunityBoardComment commentById = getCommentById(commentId);

        // Check permissions
        authorizationManager.checkCommunityBoardModifyAccess(
                commentById.getAuthor().getId(),
                commentById.getCommunityBoardItem().getProperty(),
                authorizationManager.getCurrentAccount()
        );

        commentById.setStatus(CommunityBoardCommentStatusType.DELETED);
        communityBoardCommentDao.persist(commentById);

        return commentById;
    }

    @RoleSecured
    public List<CommunityBoardComment> listCommentsForBoardItem(long boardItemId) {
        CommunityBoardItem boardItem = getBoardItemById(boardItemId);
        return communityBoardCommentDao.listParentComments(boardItem);
    }


    public CommunityBoardItemPhoto deleteCommunityItemPhoto(long photoId) {
        CommunityBoardItemPhoto attachment = (CommunityBoardItemPhoto) attachmentService.findById(photoId);

        if (authorizationManager.canWrite(attachment.getCommunityBoardItem().getProperty())) {
            return (CommunityBoardItemPhoto) attachmentService.deleteAttachment(attachment);
        }

        throw new SecurityException(String.format("You are not eligible to update item with id=%d",
                attachment.getCommunityBoardItem().getId()));
    }

    public List<CommunityBoardCategory> listCategories() {
        return communityBoardCategoryDao.listAll();
    }
}
