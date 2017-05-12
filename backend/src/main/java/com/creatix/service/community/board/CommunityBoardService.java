package com.creatix.service.community.board;

import com.creatix.domain.dao.PropertyDao;
import com.creatix.domain.dao.community.board.CommunityBoardCategoryDao;
import com.creatix.domain.dao.community.board.CommunityBoardItemDao;
import com.creatix.domain.dto.community.board.CommunityBoardItemDto;
import com.creatix.domain.dto.community.board.SearchRequest;
import com.creatix.domain.entity.store.Property;
import com.creatix.domain.entity.store.account.Account;
import com.creatix.domain.entity.store.community.board.CommunityBoardCategory;
import com.creatix.domain.entity.store.community.board.CommunityBoardItem;
import com.creatix.domain.entity.store.community.board.CommunityBoardItemPhoto;
import com.creatix.domain.mapper.CommunityBoardMapper;
import com.creatix.security.AuthorizationManager;
import com.creatix.service.AttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * Created by kvimbi on 10/05/2017.
 */
@Service
public class CommunityBoardService {

    @Autowired
    private CommunityBoardItemDao communityBoardItemDao;
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

    public List<CommunityBoardItem> listItemsForProperty(long propertyId, long offset, long limit) {
        Property property = getProperty(propertyId);

        return communityBoardItemDao.listByProperty(property, offset, limit);
    }

    public List<CommunityBoardItem> searchItemsForProperty(long propertyId, SearchRequest searchRequest) {
        Objects.requireNonNull(searchRequest, "Serach request can not be null");

        Property property = getProperty(propertyId);
        CommunityBoardCategory communityBoardCategory = null;
        if (null != searchRequest.getCommunityBoardCategoryId()) {
            communityBoardCategory = communityBoardCategoryDao.findById(searchRequest.getCommunityBoardCategoryId());
        }
        return communityBoardItemDao.searchFromRequest(property, searchRequest, communityBoardCategory);
    }

    public CommunityBoardItem createNewFromRequest(long propertyId, CommunityBoardItemDto request) {
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

    public CommunityBoardItem updateItemFromRequest(CommunityBoardItemDto request) {
        Objects.requireNonNull(request.getId(), "ID must not be null");

        CommunityBoardItem existingItem = communityBoardItemDao.findById(request.getId());
        if (null == existingItem) {
            throw new EntityNotFoundException(String.format("Item with id %d not found", request.getId()));
        }

        checkItemAccess(existingItem);

        communityBoardMapper.map(request, existingItem);
        communityBoardItemDao.persist(existingItem);

        return existingItem;
    }

    public CommunityBoardItem getById(long itemId) throws SecurityException {
        CommunityBoardItem existingItem = getCommunityBoardItem(itemId);

        //Throws security exception
        authorizationManager.checkRead(existingItem.getProperty());

        return existingItem;
    }

    public CommunityBoardItem deleteById(long itemId) {
        CommunityBoardItem communityBoardItem = getCommunityBoardItem(itemId);
        checkItemAccess(communityBoardItem);
        communityBoardItemDao.delete(communityBoardItem);
        return communityBoardItem;
    }

    private Property getProperty(long propertyId) throws SecurityException {
        Property property = propertyDao.findById(propertyId);
        if (null == property) {
            throw new EntityNotFoundException(String.format("Property %d does not exists", propertyId));
        }

        //Throws security exception
        authorizationManager.checkRead(property);
        return property;
    }

    private CommunityBoardItem getCommunityBoardItem(long itemId) {
        CommunityBoardItem existingItem = communityBoardItemDao.findById(itemId);

        if (null == existingItem) {
            throw new EntityNotFoundException(String.format("Entity %d not found", itemId));
        }
        return existingItem;
    }


    private void checkItemAccess(CommunityBoardItem existingItem) {
        Account currentAccount = authorizationManager.getCurrentAccount();
        switch (currentAccount.getRole()) {
            case Tenant:
            case SubTenant:
                if (!Objects.equals(existingItem.getAccount().getId(), currentAccount.getId())) {
                    throw new SecurityException("You can not modify this item");
                }
                break;
            case PropertyOwner:
            case PropertyManager:
            case AssistantPropertyManager:
                if (!authorizationManager.canWrite(existingItem.getProperty())) {
                    throw new SecurityException("You can not modify this item");
                }
                break;
            case Maintenance:
            case Security:
                throw new SecurityException("You can not modify this item");
            case Administrator:
                // he can do anything
                break;
        }
    }

    public CommunityBoardItem storeBusinessProfilePhotos(MultipartFile[] files, long itemId) {
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
}
