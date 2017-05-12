package com.creatix.service;

import com.creatix.configuration.FileUploadProperties;
import com.creatix.domain.dao.NotificationDao;
import com.creatix.domain.dao.NotificationPhotoDao;
import com.creatix.domain.dao.AttachmentDao;
import com.creatix.domain.entity.store.notification.Notification;
import com.creatix.domain.entity.store.notification.NotificationPhoto;
import com.creatix.domain.entity.store.attachment.Attachment;
import com.creatix.domain.entity.store.attachment.AttachmentId;
import com.creatix.domain.entity.store.attachment.AttachmentMediaType;
import com.creatix.domain.entity.store.attachment.AttachmentObjectFactory;
import lombok.Getter;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Entity;
import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Created by Tomas Michalek on 19/04/2017.
 */
@Service
public class AttachmentService {


    @Autowired
    private AttachmentDao attachmentDao;
    @Autowired
    private FileUploadProperties uploadProperties;
    @Autowired
    private NotificationDao notificationDao;
    @Autowired
    private NotificationPhotoDao notificationPhotoDao;

    public Notification storeNotificationPhotos(@NotNull MultipartFile[] files, long notificationId) throws IOException {
        Objects.requireNonNull(files, "Files array is null");

        final Notification notification = notificationDao.findById(notificationId);
        if ( notification == null ) {
            throw new EntityNotFoundException(String.format("Notification id=%d not found", notificationId));
        }

        for ( MultipartFile file : files ) {

            // move uploaded file to file repository
            final String fileName = String.format("%d-%d-%s", notification.getId(), notification.getPhotos().size(), file.getOriginalFilename());
            final Path photoFilePath = Paths.get(uploadProperties.getRepositoryPath(), fileName);
            Files.createDirectories(photoFilePath.getParent());
            file.transferTo(photoFilePath.toFile());

            final NotificationPhoto photo = new NotificationPhoto();
            photo.setNotification(notification);
            photo.setFileName(fileName);
            photo.setFilePath(photoFilePath.toString());
            notificationPhotoDao.persist(photo);

            notification.getPhotos().add(photo);
        }

        return notification;
    }

    public NotificationPhoto getNotificationPhoto(Long notificationId, String fileName) {

        final NotificationPhoto photo = notificationPhotoDao.findByNotificationIdAndFileName(notificationId, fileName);
        if ( photo == null ) {
            throw new EntityNotFoundException(String.format("Photo id=%s not found", fileName));
        }

        return photo;
    }

    public <T extends Attachment> List<T> storeAttachments(@NotNull MultipartFile[] files,
                                                           @NotNull AttachmentObjectFactory<T> attachmentObjectFactory,
                                                           @NotNull AttachmentId fkObject,
                                                           Class<T> clazz) throws IOException {
        Objects.requireNonNull(files, "Files array is null");
        Objects.requireNonNull(attachmentObjectFactory);
        Objects.requireNonNull(fkObject);

        Entity entityAnnotation = fkObject.getClass().getAnnotation(Entity.class);
        Objects.requireNonNull(entityAnnotation, "Foreign key must be annotated with @Entity");
        Long fkId = fkObject.getId();

        ArrayList<T> storedAttachmentsList = new ArrayList<>(files.length);

        for ( MultipartFile file : files ) {
            // move uploaded file to file repository
            final String fileName = String.format("%d-%s", fkId, UUID.randomUUID().toString());
            final Path attachmentFilePath = Paths.get(uploadProperties.getRepositoryPath(), fileName);
            Files.createDirectories(attachmentFilePath.getParent());
            file.transferTo(attachmentFilePath.toFile());

            T attachment = attachmentObjectFactory.createAttachment(fkObject);
            attachment.setFileName(fileName);
            attachment.setFilePath(attachmentFilePath.toString());
            attachment.setAttachmentMediaType(AttachmentMediaType.IMAGE);
            attachmentDao.persist(attachment);

            storedAttachmentsList.add(attachment);

        }

        return storedAttachmentsList;
    }

    @Getter
    public static class DownloadAttachment {
        private final byte[] fileContent;
        private final MediaType mediaType;

        DownloadAttachment(byte[] fileContent, MediaType mediaType) {
            this.fileContent = fileContent;
            this.mediaType = mediaType;
        }
    }

    public DownloadAttachment downloadAttachment(long attachmentId, @NotNull String fileName) throws IOException {
        Attachment attachment = attachmentDao.findById(attachmentId);

        if (attachment != null && fileName.equals(attachment.getFileName())) {
            final File attachmentFile = new File(attachment.getFilePath());
            if (attachmentFile.exists()) {
                try {
                    return new DownloadAttachment(
                            FileUtils.readFileToByteArray(attachmentFile),
                            MediaType.valueOf(Files.probeContentType(attachmentFile.toPath()))
                    );
                } catch (IOException e) {
                    throw new EntityNotFoundException(String.format("Unable to read attachments file %s", fileName));
                }
            } else {
                throw new EntityNotFoundException(String.format("Unable to locate attachments file %s", fileName));
            }
        }

        throw new EntityNotFoundException(String.format("Attachment id=%s not found", fileName));
    }

}
