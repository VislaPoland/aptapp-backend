package com.creatix.domain.dao;

import com.creatix.domain.entity.store.notification.NotificationPhoto;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import static com.creatix.domain.entity.store.QNotificationPhoto.notificationPhoto;

@Repository
@Transactional
public class NotificationPhotoDao extends DaoBase<NotificationPhoto, Long> {

    public NotificationPhoto findByNotificationIdAndFileName(Long notificationId, String fileName) {
        return queryFactory.selectFrom(notificationPhoto)
                .where(notificationPhoto.notification.id.eq(notificationId).and(notificationPhoto.fileName.eq(fileName)))
                .fetchOne();
    }

}
