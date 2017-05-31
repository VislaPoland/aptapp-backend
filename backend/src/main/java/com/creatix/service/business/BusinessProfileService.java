package com.creatix.service.business;

import com.creatix.domain.dao.PropertyDao;
import com.creatix.domain.dao.business.BusinessCategoryDao;
import com.creatix.domain.dao.business.BusinessProfileDao;
import com.creatix.domain.dto.business.BusinessProfileDto;
import com.creatix.domain.entity.store.Property;
import com.creatix.domain.entity.store.attachment.Attachment;
import com.creatix.domain.entity.store.business.BusinessCategory;
import com.creatix.domain.entity.store.business.BusinessProfile;
import com.creatix.domain.entity.store.business.BusinessProfileCarteItem;
import com.creatix.domain.entity.store.business.DiscountCoupon;
import com.creatix.domain.entity.store.attachment.BusinessProfilePhoto;
import com.creatix.domain.enums.AccountRole;
import com.creatix.domain.mapper.BusinessMapper;
import com.creatix.security.AuthorizationManager;
import com.creatix.security.RoleSecured;
import com.creatix.service.AttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * Created by Tomas Michalek on 12/04/2017.
 */
@Service
@Transactional
public class BusinessProfileService {

    @Autowired
    private BusinessCategoryDao businessCategoryDao;
    @Autowired
    private PropertyDao propertyDao;
    @Autowired
    private BusinessProfileDao businessProfileDao;
    @Autowired
    private AuthorizationManager authorizationManager;
    @Autowired
    private BusinessMapper businessMapper;
    @Autowired
    private BusinessNotificationExecutor businessNotificationExecutor;
    @Autowired
    private AttachmentService attachmentService;

    public List<BusinessProfile> listBusinessProfilesForProperty(long propertyId) {
        Property property = findPropertyById(propertyId);

        authorizationManager.checkRead(property);

        return businessProfileDao.listBusinessesForProperty(property);
    }

    public List<BusinessProfile> listBusinessesForPropertyAndCategory(long propertyId, long businessCategoryId) {
        Property property = findPropertyById(propertyId);

        authorizationManager.checkRead(property);

        BusinessCategory category = businessCategoryDao.findById(businessCategoryId);
        if (null == category) {
            throw new EntityNotFoundException(String.format("Category %d not found", businessCategoryId));
        }

        return businessProfileDao.listBusinessesForPropertyAndCategory(property, category);
    }

    public List<BusinessProfile> searchBusinesses(long propertyId, @NotNull String name, long businessCategoryId) {
        Objects.requireNonNull(name);

        Property property = findPropertyById(propertyId);

        authorizationManager.checkRead(property);

        BusinessCategory category = businessCategoryDao.findById(businessCategoryId);

        return businessProfileDao.searchBusinesses(property, name, category);
    }

    /**
     * Finds business profile by id, or throws {@link EntityNotFoundException} if not found
     *
     * @param businessProfileId
     * @return
     */
    @NotNull
    public BusinessProfile getById(long businessProfileId) {
        BusinessProfile profile = findBusinessProfileById(businessProfileId);
        authorizationManager.checkRead(profile.getProperty());
        return profile;
    }


    @NotNull
    @RoleSecured({AccountRole.Administrator, AccountRole.PropertyOwner, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager})
    public BusinessProfile createBusinessProfileFromRequest(@NotNull BusinessProfileDto businessProfileDto, long propertyId) {
        Objects.requireNonNull(businessProfileDto, "Business profile must not be null");

        Property property = findPropertyById(propertyId);

        if (authorizationManager.canWrite(property)) {
            BusinessProfile businessProfile = businessMapper.toBusinessProfile(businessProfileDto);
            businessProfile.setProperty(property);
            businessProfileDao.persist(businessProfile);
            return businessProfile;
        }

        throw new SecurityException(String.format("You are not eligible to update info about property with id=%d", property.getId()));
    }


    @NotNull
    @RoleSecured({AccountRole.Administrator, AccountRole.PropertyOwner, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager})
    public BusinessProfile updateBusinessProfileFromRequest(@NotNull BusinessProfileDto businessProfileDto) {
        Objects.requireNonNull(businessProfileDto, "Business profile must not be null");

        BusinessProfile storedProfile = findBusinessProfileById(businessProfileDto.getId());

        if (authorizationManager.canWrite(storedProfile.getProperty())) {
            businessMapper.map(businessProfileDto, storedProfile);
            businessProfileDao.persist(storedProfile);
            return storedProfile;
        }

        throw new SecurityException(String.format("You are not eligible to update info about property with id=%d", storedProfile.getProperty().getId()));
    }

    public List<BusinessCategory> listBusinessCategories() {
        return businessCategoryDao.listAllCategories();
    }

    @NotNull
    @RoleSecured(AccountRole.Administrator)
    public BusinessCategory createOrUpdateBusinessCategory(@NotNull BusinessCategory businessCategory) {
        Objects.requireNonNull(businessCategory);

        if (null == businessCategory.getId()) {
            businessCategoryDao.persist(businessCategory);
            return businessCategory;
        } else {
            BusinessCategory storedCategory = businessCategoryDao.findById(businessCategory.getId());
            if (null == storedCategory) {
                throw new EntityNotFoundException(String.format("Business category %d not found", businessCategory.getId()));
            }
            businessMapper.map(businessCategory, storedCategory);
            businessCategoryDao.persist(storedCategory);
            return storedCategory;
        }
    }

    @NotNull
    @RoleSecured(AccountRole.Administrator)
    public BusinessCategory deleteBusinessCategory(long businessCategoryId) {
        BusinessCategory category = businessCategoryDao.findById(businessCategoryId);
        if (null == category) {
            throw new EntityNotFoundException(String.format("Business category %d not found", businessCategoryId));
        }

        businessCategoryDao.delete(category);
        return category;
    }

    @NotNull
    public List<DiscountCoupon> listBusinessDiscountCoupons(long businessProfileId) {
        BusinessProfile businessProfile = findBusinessProfileById(businessProfileId);

        authorizationManager.checkRead(businessProfile.getProperty());

        return businessProfile.getDiscountCouponList();
    }

    @RoleSecured({AccountRole.Administrator, AccountRole.PropertyOwner, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager})
    public void sendNotification(long businessProfileId) {
        final BusinessProfile businessProfile = findBusinessProfileById(businessProfileId);
        businessNotificationExecutor.sendNotification(businessProfile);
    }

    public List<BusinessProfileCarteItem> listCarte(Long businessProfileId) {
        return findBusinessProfileById(businessProfileId).getBusinessProfileCarte();
    }


    private BusinessProfile findBusinessProfileById(long businessProfileId) {
        BusinessProfile businessProfile = businessProfileDao.findById(businessProfileId);

        if (null == businessProfile) {
            throw new EntityNotFoundException(String.format("Business profile %d not found", businessProfileId));
        }

        authorizationManager.checkRead(businessProfile.getProperty());

        return businessProfile;
    }

    private Property findPropertyById(long propertyId) {
        Property property = propertyDao.findById(propertyId);

        if (null == property) {
            throw new EntityNotFoundException(String.format("Property %d not found", propertyId));
        }
        return property;
    }

    public BusinessProfile storeBusinessProfilePhotos(MultipartFile[] files, long businessProfileId) {
        final BusinessProfile businessProfile = findBusinessProfileById(businessProfileId);
        List<BusinessProfilePhoto> photoStoreList;
        try {
            photoStoreList = attachmentService.storeAttachments(files, foreignKeyObject -> {
                BusinessProfilePhoto businessProfilePhoto = new BusinessProfilePhoto();
                businessProfilePhoto.setBusinessProfile(businessProfile);
                return businessProfilePhoto;
            }, businessProfile, BusinessProfilePhoto.class);
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to store photo for business profile", e);
        }
        businessProfile.getBusinessProfilePhotoList().addAll(photoStoreList);
        businessProfileDao.persist(businessProfile);

        return businessProfile;
    }

    @RoleSecured({AccountRole.Administrator, AccountRole.PropertyOwner, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager})
    public BusinessProfile deleteBusinessProfile(long businessProfileId) {
        BusinessProfile businessProfile = findBusinessProfileById(businessProfileId);
        if (authorizationManager.canWrite(businessProfile.getProperty())) {
            attachmentService.deleteAttachmentFiles(businessProfile.getBusinessProfilePhotoList());
            businessProfileDao.delete(businessProfile);
            return businessProfile;
        }

        throw new SecurityException(String.format("You are not eligible to delete business profile with id=%d",
                businessProfile.getId()));
    }

    @RoleSecured({AccountRole.Administrator, AccountRole.PropertyOwner, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager})
    public BusinessProfilePhoto deleteBusinessProfilePhoto(long photoId) {
        BusinessProfilePhoto attachment = (BusinessProfilePhoto) attachmentService.findById(photoId);

        if (authorizationManager.canWrite(attachment.getBusinessProfile().getProperty())) {
            return (BusinessProfilePhoto) attachmentService.deleteAttachment(attachment);
        }

        throw new SecurityException(String.format("You are not eligible to update business profile with id=%d",
                attachment.getBusinessProfile().getId()));
    }
}
