package com.creatix.service.business;

import com.creatix.domain.dao.business.BusinessProfileCarteItemDao;
import com.creatix.domain.dto.business.BusinessProfileCarteItemDto;
import com.creatix.domain.entity.store.business.BusinessProfile;
import com.creatix.domain.entity.store.business.BusinessProfileCarteItem;
import com.creatix.domain.entity.store.photo.BusinessProfileCartePhoto;
import com.creatix.domain.mapper.BusinessMapper;
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
 * Created by kvimbi on 20/04/2017.
 */
@Service
public class BusinessProfileCarteService {

    @Autowired
    private BusinessProfileCarteItemDao businessProfileCarteItemDao;
    @Autowired
    private AuthorizationManager authorizationManager;
    @Autowired
    private BusinessProfileService businessProfileService;
    @Autowired
    private BusinessMapper businessMapper;
    @Autowired
    private AttachmentService attachmentService;

    public BusinessProfileCarteItem createFromRequest(@NotNull BusinessProfileCarteItemDto request, long businessProfileId) {
        Objects.requireNonNull(request, "Request object can not be null");
        BusinessProfile businessProfile = businessProfileService.getById(businessProfileId);

        if (authorizationManager.canWrite(businessProfile.getProperty())) {
            BusinessProfileCarteItem entity = businessMapper.toBusinessProfileCarteItem(request);
            entity.setBusinessProfile(businessProfile);
            businessProfileCarteItemDao.persist(entity);

            return entity;
        }

        throw new SecurityException(String.format("You are not eligible to write info to property with id=%d", businessProfile.getProperty().getId()));
    }

    public BusinessProfileCarteItem updateFromRequest(@NotNull BusinessProfileCarteItemDto request) {
        Objects.requireNonNull(request, "Request object can not be null");
        Objects.requireNonNull(request.getId(), "Id must not be null!");

        BusinessProfileCarteItem businessProfileCarteItem = businessProfileCarteItemDao.findById(request.getId());

        if (authorizationManager.canWrite(businessProfileCarteItem.getBusinessProfile().getProperty())) {
            businessMapper.map(request, businessProfileCarteItem);
            businessProfileCarteItemDao.persist(businessProfileCarteItem);

            return businessProfileCarteItem;
        }

        throw new SecurityException(String.format("You are not eligible to write info to property with id=%d",
                businessProfileCarteItem.getBusinessProfile().getProperty().getId()));
    }

    public BusinessProfileCarteItem deleteCarteItem(long propertyId) {
        BusinessProfileCarteItem carteItem = businessProfileCarteItemDao.findById(propertyId);
        if (null == carteItem) {
            throw new EntityNotFoundException("Cart item not found");
        }

        if (authorizationManager.canWrite(carteItem.getBusinessProfile().getProperty())) {
            businessProfileCarteItemDao.delete(carteItem);
            return carteItem;
        }

        throw new SecurityException(String.format("You are not eligible to write info to property with id=%d",
                carteItem.getBusinessProfile().getProperty().getId()));
    }

    public BusinessProfileCarteItem storeBusinessProfilePhotos(@NotNull MultipartFile[] files, long businessProfileCartItemId) {
        Objects.requireNonNull(files);
        if (files.length != 1) {
            throw new IllegalArgumentException(String.format("Cart item can contain only single photo %d provided", files.length));
        }

        final BusinessProfileCarteItem businessProfileCarteItem = businessProfileCarteItemDao.findById(businessProfileCartItemId);

        if (null == businessProfileCarteItem) {
            throw new EntityNotFoundException(String.format("Cart item %d not not found", businessProfileCartItemId));
        }

        List<BusinessProfileCartePhoto> photoStoreList;
        try {
            photoStoreList = attachmentService.storeAttachments(files, foreignKeyObject -> {
                BusinessProfileCartePhoto businessProfilePhoto = new BusinessProfileCartePhoto();
                businessProfilePhoto.setBusinessProfileCarteItem(businessProfileCarteItem);
                return businessProfilePhoto;
            }, businessProfileCarteItem, BusinessProfileCartePhoto.class);
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to store photo for business profile", e);
        }

        if (photoStoreList.size() != 1) {
            throw new IllegalArgumentException("Unable to store photo for business profile");
        }

        businessProfileCarteItem.setBusinessProfileCartePhoto(photoStoreList.get(0));
        businessProfileCarteItemDao.persist(businessProfileCarteItem);

        return businessProfileCarteItem;
    }
}
