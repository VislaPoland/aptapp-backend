package com.creatix.service.message;

import com.creatix.configuration.FileUploadProperties;
import com.creatix.domain.dao.PredefinedMessageDao;
import com.creatix.domain.dao.PredefinedMessagePhotoDao;
import com.creatix.domain.dao.PropertyDao;
import com.creatix.domain.dto.property.message.CreatePredefinedMessageRequest;
import com.creatix.domain.dto.property.message.PredefinedMessageDto;
import com.creatix.domain.entity.store.PredefinedMessage;
import com.creatix.domain.entity.store.PredefinedMessagePhoto;
import com.creatix.domain.entity.store.Property;
import com.creatix.domain.enums.AccountRole;
import com.creatix.security.AuthorizationManager;
import com.creatix.security.RoleSecured;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

/**
 * Created by Tomas Sedlak on 21.8.2017.
 */
@Service
public class PredefinedMessageService {

    @Autowired
    private PropertyDao propertyDao;
    @Autowired
    private PredefinedMessageDao predefinedMessageDao;
    @Autowired
    private AuthorizationManager authorization;
    @Autowired
    private FileUploadProperties uploadProperties;
    @Autowired
    private PredefinedMessagePhotoDao predefinedMessagePhotoDao;

    @Transactional
    @RoleSecured
    public List<PredefinedMessage> getPredefinedMessages(@NotNull Long propertyId) {
        Objects.requireNonNull(propertyId, "property id");
        final Property property = propertyDao.findById(propertyId);
        authorization.checkRead(property);
        return property.getPredefinedMessages();
    }

    @Transactional
    @RoleSecured({AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.PropertyOwner, AccountRole.Administrator})
    public PredefinedMessage createFromRequest(@NotNull CreatePredefinedMessageRequest req, @NotNull Long propertyId) {
        Objects.requireNonNull(req, "create new redefined message request");
        Objects.requireNonNull(propertyId, "property id");

        final Property property = propertyDao.findById(propertyId);
        if ( property == null ) {
            throw new EntityNotFoundException(String.format("Property id=%d not found", propertyId));
        }
        authorization.checkWrite(property);

        final PredefinedMessage message = new PredefinedMessage();
        message.setBody(req.getBody());
        message.setProperty(property);
        predefinedMessageDao.persist(message);

        return message;
    }

    @Transactional
    @RoleSecured({AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.PropertyOwner, AccountRole.Administrator})
    public PredefinedMessage updateFromRequest(PredefinedMessageDto dto) {
        Objects.requireNonNull(dto, "predefined message dto");

        final PredefinedMessage message = predefinedMessageDao.findById(dto.getId());
        if ( message == null ) {
            throw new EntityNotFoundException(String.format("Predefined message id=%d nto found", dto.getId()));
        }

        authorization.checkWrite(message.getProperty());
        message.setBody(dto.getBody());
        predefinedMessageDao.persist(message);

        return message;
    }

    @Transactional
    @RoleSecured({AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.PropertyOwner, AccountRole.Administrator})
    public PredefinedMessage deleteById(@NotNull Long messageId) {
        Objects.requireNonNull(messageId, "predefined message id");

        final PredefinedMessage message = predefinedMessageDao.findById(messageId);
        if ( message == null ) {
            throw new EntityNotFoundException(String.format("Predefined message id=%d nto found", messageId));
        }

        authorization.checkWrite(message.getProperty());
        predefinedMessageDao.delete(message);

        return message;
    }

    /**
     * Save photo for predefined message.
     * @param files
     * @param notificationId
     * @return
     * @throws IOException
     */
    public PredefinedMessage storePredefinedMessagePhotos(@NotNull MultipartFile[] files, long notificationId) throws IOException {
        Objects.requireNonNull(files, "Files array is null");

        final PredefinedMessage predefinedMessage = predefinedMessageDao.findById(notificationId);
        if ( predefinedMessage == null ) {
            throw new EntityNotFoundException(String.format("Notification id=%d not found", notificationId));
        }

        for ( MultipartFile file : files ) {

            // move uploaded file to file repository
            final String fileName = String.format("%d-%d-%s", predefinedMessage.getId(), predefinedMessage.getPhotos().size(), file.getOriginalFilename());
            final Path photoFilePath = Paths.get(uploadProperties.getRepositoryPath(), fileName);
            Files.createDirectories(photoFilePath.getParent());
            file.transferTo(photoFilePath.toFile());

            final PredefinedMessagePhoto photo = new PredefinedMessagePhoto();
            photo.setPredefinedMessage(predefinedMessage);
            photo.setFileName(fileName);
            photo.setFilePath(photoFilePath.toString());
            predefinedMessagePhotoDao.persist(photo);

            predefinedMessage.getPhotos().add(photo);
        }

        return predefinedMessage;
    }

    /**
     * Get photo of predefined message.
     * @param predefinedMessageId
     * @param fileName
     * @return
     */
    public PredefinedMessagePhoto getPredefinedMessagePhoto(Long predefinedMessageId, String fileName) {

        final PredefinedMessagePhoto photo = predefinedMessagePhotoDao.findByPredefinedMessageIdAndFileName(predefinedMessageId, fileName);
        if ( photo == null ) {
            throw new EntityNotFoundException(String.format("Photo id=%s not found", fileName));
        }

        return photo;
    }
}
