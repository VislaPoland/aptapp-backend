package com.creatix.service;

import com.creatix.configuration.FileUploadProperties;
import com.creatix.domain.dao.NotificationDao;
import com.creatix.domain.dao.NotificationPhotoDao;
import com.creatix.domain.dao.PhotoStoreDao;
import com.creatix.domain.entity.store.notification.Notification;
import com.creatix.domain.entity.store.notification.NotificationPhoto;
import com.creatix.domain.entity.store.photo.GenericPhotoStore;
import com.creatix.domain.entity.store.photo.PhotoObjectFactory;
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
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Created by kvimbi on 19/04/2017.
 */
@Service
public class StoredFilesService {


    @Autowired
    private PhotoStoreDao photoStoreDao;
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

    public <T extends GenericPhotoStore> List<T> storePhotos(@NotNull MultipartFile[] files,
                                                         @NotNull PhotoObjectFactory<T> photoObjectFactory,
                                                         @NotNull Object fkObject,
                                                         Class<T> clazz) throws IOException {
        Objects.requireNonNull(files, "Files array is null");
        Objects.requireNonNull(photoObjectFactory);
        Objects.requireNonNull(fkObject);

        Entity entityAnnotation = fkObject.getClass().getAnnotation(Entity.class);
        Objects.requireNonNull(entityAnnotation, "Foreign key must be annotated with @Entity");
        Long fkId;
        try {
            fkId = (Long) fkObject.getClass().getMethod("getId").invoke(null);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException("Foreign key object does not have getId() method, or failed to invoke", e);
        }

        ArrayList<T> storedPhotoList = new ArrayList<>(files.length);

        for ( MultipartFile file : files ) {
            // move uploaded file to file repository
            final String fileName = String.format("%d-%s", fkId, UUID.randomUUID().toString());
            final Path photoFilePath = Paths.get(uploadProperties.getRepositoryPath(), fileName);
            Files.createDirectories(photoFilePath.getParent());
            file.transferTo(photoFilePath.toFile());

            T photo = photoObjectFactory.createPhotoObject(fkObject);
            photo.setFileName(fileName);
            photo.setFilePath(photoFilePath.toString());
            photoStoreDao.persist(photo);

            storedPhotoList.add(photo);

        }

        return storedPhotoList;
    }

    public NotificationPhoto getNotificationPhoto(Long notificationId, String fileName) {

        final NotificationPhoto photo = notificationPhotoDao.findByNotificationIdAndFileName(notificationId, fileName);
        if ( photo == null ) {
            throw new EntityNotFoundException(String.format("Photo id=%s not found", fileName));
        }

        return photo;
    }

    @Getter
    public static class DownloadPhotoResult {
        private final byte[] photoData;
        private final MediaType mediaType;

        DownloadPhotoResult(byte[] photoData, MediaType mediaType) {
            this.photoData = photoData;
            this.mediaType = mediaType;
        }
    }

    public DownloadPhotoResult downloadPhoto(long photoId, @NotNull String fileName) throws IOException {
        GenericPhotoStore photo = photoStoreDao.findById(photoId);

        if (photo != null && fileName.equals(photo.getFileName())) {
            final File photoFile = new File(photo.getFilePath());
            return new DownloadPhotoResult(
                    FileUtils.readFileToByteArray(photoFile),
                    MediaType.valueOf(Files.probeContentType(photoFile.toPath()))
            );
        }

        throw new EntityNotFoundException(String.format("Photo id=%s not found", fileName));
    }

}
