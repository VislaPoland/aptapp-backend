package com.creatix.service.property;

import com.creatix.configuration.FileUploadProperties;
import com.creatix.domain.Mapper;
import com.creatix.domain.dao.DaoBase;
import com.creatix.domain.dao.PropertyDao;
import com.creatix.domain.dao.PropertyOwnerDao;
import com.creatix.domain.dao.PropertyPhotoDao;
import com.creatix.domain.dto.property.CreatePropertyRequest;
import com.creatix.domain.dto.property.UpdatePropertyRequest;
import com.creatix.domain.entity.store.Property;
import com.creatix.domain.entity.store.PropertyPhoto;
import com.creatix.domain.entity.store.account.PropertyOwner;
import com.creatix.domain.enums.AccountRole;
import com.creatix.domain.enums.PropertyStatus;
import com.creatix.security.AuthorizationManager;
import com.creatix.security.RoleSecured;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;
import java.util.stream.Collectors;

@Service
@Transactional
public class PropertyService {
    @Autowired
    private PropertyDao propertyDao;
    @Autowired
    private PropertyOwnerDao propertyOwnerDao;

    @Autowired
    private Mapper mapper;
    @Autowired
    private AuthorizationManager authorizationManager;
    @Autowired
    private FileUploadProperties uploadProperties;
    @Autowired
    private PropertyPhotoDao propertyPhotoDao;


    private <T, ID> T getOrElseThrow(ID id, DaoBase<T, ID> dao, EntityNotFoundException ex) {
        final T item = dao.findById(id);
        if ( item == null ) {
            throw ex;
        }
        return item;
    }

    @RoleSecured
    public List<Property> getAllProperties() {
        return propertyDao.findAll().stream()
                .filter(p -> authorizationManager.canRead(p))
                .collect(Collectors.toList());
    }

    @RoleSecured(AccountRole.Administrator)
    public Property createFromRequest(@NotNull CreatePropertyRequest request) {
        Objects.requireNonNull(request);

        final Property property = mapper.toProperty(request);
        final PropertyOwner propertyOwner = getOrElseThrow(request.getPropertyOwnerId(), propertyOwnerDao,
                new EntityNotFoundException(String.format("Property owner %d not found", request.getPropertyOwnerId())));
        property.setOwner(propertyOwner);
        property.setStatus(PropertyStatus.Draft);
        if ( StringUtils.isBlank(property.getTimeZone()) ) {
            property.setTimeZone(TimeZone.getDefault().toString());
        }
        propertyDao.persist(property);
        return property;
    }

    @RoleSecured({AccountRole.PropertyOwner, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager})
    public Property updateFromRequest(Long propertyId, @NotNull UpdatePropertyRequest request) {
        Objects.requireNonNull(request);

        final Property property = getOrElseThrow(propertyId, propertyDao, new EntityNotFoundException(String.format("Property %d not found", propertyId)));

        if ( authorizationManager.canUpdateProperty(property) ) {
            if ( request.getPropertyOwnerId() != null ) {
                final PropertyOwner propertyOwner = propertyOwnerDao.findById(request.getPropertyOwnerId());
                if ( propertyOwner == null ) {
                    throw new EntityNotFoundException(String.format("Property owner %d not found", request.getPropertyOwnerId()));
                }
                property.setOwner(propertyOwner);
            }
            mapper.fillProperty(request, property);
            if ( StringUtils.isBlank(property.getTimeZone()) ) {
                property.setTimeZone(TimeZone.getDefault().toString());
            }
            propertyDao.persist(property);
            return property;
        }

        throw new SecurityException(String.format("You are not eligible to update info about property with id=%d", propertyId));
    }

    @RoleSecured(AccountRole.Administrator)
    public Property deleteProperty(long propertyId) {
        final Property property = getOrElseThrow(propertyId, propertyDao, new EntityNotFoundException(String.format("Property %d not found", propertyId)));
        property.setDeleteDate(new Date());
        propertyDao.persist(property);
        return property;
    }

    @RoleSecured
    public Property getProperty(@NotNull Long propertyId) {
        Objects.requireNonNull(propertyId);
        final Property property = this.propertyDao.findById(propertyId);
        if ( property == null ) {
            throw new EntityNotFoundException(String.format("Property id=%d not found", propertyId));
        }
        authorizationManager.checkRead(property);

        return property;
    }


    public Property storePropertyPhotos(MultipartFile[] files, long propertyId) throws IOException {

        final Property property = getProperty(propertyId);

        for ( MultipartFile file : files ) {

            // move uploaded file to file repository
            final String fileName = String.format("%d-%d-%s", property.getId(), property.getPhotos().size(), file.getOriginalFilename());
            final Path photoFilePath = Paths.get(uploadProperties.getRepositoryPath(), fileName);
            Files.createDirectories(photoFilePath.getParent());
            file.transferTo(photoFilePath.toFile());

            final PropertyPhoto photo = new PropertyPhoto();
            photo.setProperty(property);
            photo.setFileName(fileName);
            photo.setFilePath(photoFilePath.toString());
            propertyPhotoDao.persist(photo);

            property.getPhotos().add(photo);
        }

        return property;
    }

    public PropertyPhoto getPropertyPhoto(@NotNull Long propertyId, @NotNull String fileName) {
        Objects.requireNonNull(propertyId, "Property id is null");
        Objects.requireNonNull(fileName, "File name is null");

        final PropertyPhoto photo = propertyPhotoDao.findByPropertyIdAndFileName(propertyId, fileName);
        if ( photo == null ) {
            throw new EntityNotFoundException(String.format("Photo id=%s not found", fileName));
        }

        return photo;
    }

    @RoleSecured({AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.PropertyOwner, AccountRole.Administrator})
    public PropertyPhoto deletePropertyPhoto(Long propertyPhotoId) throws IOException {
        final PropertyPhoto photo = propertyPhotoDao.findById(propertyPhotoId);
        if ( photo == null ) {
            throw new EntityNotFoundException(String.format("Photo id=%d not found", propertyPhotoId));
        }

        propertyPhotoDao.delete(photo);

        Files.deleteIfExists(new File(photo.getFilePath()).toPath());

        return photo;
    }

}
