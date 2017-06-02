package com.creatix.service.community.board;

import com.creatix.domain.dao.PropertyDao;
import com.creatix.domain.dao.community.board.CommunityBoardCategoryDao;
import com.creatix.domain.dao.community.board.CommunityBoardCommentDao;
import com.creatix.domain.dao.community.board.CommunityBoardItemDao;
import com.creatix.domain.dto.community.board.CommunityBoardCommentDto;
import com.creatix.domain.dto.community.board.CommunityBoardItemDto;
import com.creatix.domain.dto.community.board.SearchRequest;
import com.creatix.domain.entity.store.Property;
import com.creatix.domain.entity.store.attachment.BusinessProfilePhoto;
import com.creatix.domain.entity.store.community.board.CommunityBoardCategory;
import com.creatix.domain.entity.store.community.board.CommunityBoardComment;
import com.creatix.domain.entity.store.community.board.CommunityBoardItem;
import com.creatix.domain.entity.store.community.board.CommunityBoardItemPhoto;
import com.creatix.domain.enums.community.board.CommunityBoardCommentStatusType;
import com.creatix.domain.enums.community.board.CommunityBoardStatusType;
import com.creatix.domain.mapper.CommunityBoardMapper;
import com.creatix.security.AuthorizationManager;
import com.creatix.service.AttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * Created by Tomas Michalek on 10/05/2017.
 */
@Service
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

    public List<CommunityBoardItem> listBoardItemsForProperty(long propertyId, long offset, long limit) {
        Property property = getProperty(propertyId);

        return communityBoardItemDao.listByProperty(property, offset, limit);
    }

    public List<CommunityBoardItem> searchBoardItemsForProperty(long propertyId, SearchRequest searchRequest) {
        Objects.requireNonNull(searchRequest, "Serach request can not be null");

        Property property = getProperty(propertyId);
        CommunityBoardCategory communityBoardCategory = null;
        if (null != searchRequest.getCommunityBoardCategoryId()) {
            communityBoardCategory = communityBoardCategoryDao.findById(searchRequest.getCommunityBoardCategoryId());
        }
        return communityBoardItemDao.searchFromRequest(property, searchRequest, communityBoardCategory);
    }

    public CommunityBoardItem createNewBoardItemFromRequest(long propertyId, CommunityBoardItemDto request) {
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

    public CommunityBoardItem updateBoardItemFromRequest(CommunityBoardItemDto request) {
        Objects.requireNonNull(request.getId(), "ID must not be null");

        CommunityBoardItem existingItem = getBoardItemById(request.getId());

        authorizationManager.checkCommunityBoardModifyAccess(
                existingItem.getAccount().getId(),
                existingItem.getProperty(),
                authorizationManager.getCurrentAccount()
        );

        communityBoardMapper.map(request, existingItem);
        communityBoardItemDao.persist(existingItem);

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
        communityBoardItem.setCommunityBoardStatus(CommunityBoardStatusType.DELETED);
        communityBoardItemDao.persist(communityBoardItem);
        return communityBoardItem;
    }

    @NotNull
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

    @NotNull
    public CommunityBoardComment createNewCommentFromRequest(long boardId, @NotNull CommunityBoardCommentDto request) {
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

        communityBoardCommentDao.persist(comment);

        return comment;

    }

    public CommunityBoardComment updateCommentFromRequest(CommunityBoardCommentDto request) {
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
