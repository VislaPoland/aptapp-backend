package com.creatix.service;

import com.creatix.configuration.FileUploadProperties;
import com.creatix.domain.dao.AttachmentDao;
import com.creatix.domain.dao.NotificationDao;
import com.creatix.domain.dao.NotificationPhotoDao;
import com.creatix.domain.entity.store.attachment.Attachment;
import com.creatix.domain.entity.store.attachment.AttachmentId;
import com.creatix.domain.entity.store.attachment.AttachmentMediaType;
import com.creatix.domain.entity.store.attachment.AttachmentObjectFactory;
import com.creatix.domain.entity.store.notification.Notification;
import com.creatix.domain.entity.store.notification.NotificationPhoto;
import com.creatix.domain.enums.util.ImageSize;
import com.creatix.util.ImageUtil;
import lombok.Getter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Entity;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.FileOutputStream;
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
@Transactional
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
            String[] fileExtensions = file.getOriginalFilename().split("\\.");
            if (fileExtensions.length == 0) {
                throw new IllegalArgumentException("Invalid file name, unrecognized extension");
            }
            // move uploaded file to file repository
            final String fileName = String.format("%d-%s.%s", fkId, UUID.randomUUID().toString(), fileExtensions[fileExtensions.length - 1]);
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
        return downloadAttachment(attachmentId, fileName, ImageSize.ORIGINAL);
    }

    public DownloadAttachment downloadAttachment(long attachmentId, @NotNull String fileName, ImageSize imageSize) throws IOException {
        Attachment attachment = attachmentDao.findById(attachmentId);

        if (attachment != null && fileName.equals(attachment.getFileName())) {

            String filePath = attachment.getFilePath();
            if (imageSize != ImageSize.ORIGINAL) {
                filePath += "_" + imageSize.name();
            }
            final File attachmentFile = new File(filePath);

            // Create resized version?
            if (! attachmentFile.exists() && imageSize != ImageSize.ORIGINAL) {
                final File originalAttachmentFile = new File(attachment.getFilePath());
                if (originalAttachmentFile.exists()) {
                    byte[] resizeImageToJpeg = ImageUtil.resizeImageToJpeg(
                            FileUtils.readFileToByteArray(originalAttachmentFile),
                            imageSize
                    );
                    IOUtils.write(resizeImageToJpeg, new FileOutputStream(attachmentFile));
                } else {
                    throw new EntityNotFoundException(String.format("Attachment id=%s not found", fileName));
                }
            }

            if (attachmentFile.exists()) {
                try {
                    return new DownloadAttachment(
                            FileUtils.readFileToByteArray(attachmentFile),
                            MediaType.valueOf(Files.probeContentType(attachmentFile.toPath()))
                    );
                } catch (IOException e) {
                    throw new EntityNotFoundException(String.format("Unable to read attachments file %s", fileName));
                } catch (InvalidMediaTypeException invalidMediaTypeEx) {
                    return new DownloadAttachment(
                            FileUtils.readFileToByteArray(attachmentFile),
                            MediaType.APPLICATION_OCTET_STREAM
                    );
                }
            } else {
                throw new EntityNotFoundException(String.format("Unable to locate attachments file %s", fileName));
            }
        }

        throw new EntityNotFoundException(String.format("Attachment id=%s not found", fileName));
    }

    public Attachment deleteAttachmentById(long attachmentId) {
        Attachment attachment = attachmentDao.findById(attachmentId);
        if (null == attachment) {
            throw new EntityNotFoundException(String.format("Attachment id=%d not found", attachmentId));
        }

        return deleteAttachment(attachment);
    }


    public Attachment deleteAttachment(@NotNull Attachment attachment) {
        Objects.requireNonNull(attachment, "Attachment object can not be null");
        if (null != attachment.getFilePath()) {
            File file = new File(attachment.getFilePath());
            if (file.exists()) {
                boolean delete = file.delete();
            }
        }
        attachmentDao.delete(attachment);

        return attachment;
    }

    public Attachment findById(long attachmentId) {
        return attachmentDao.findById(attachmentId);
    }

}
