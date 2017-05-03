package com.creatix.domain.dao;

import com.creatix.domain.entity.store.PropertyPhoto;
import com.creatix.domain.entity.store.QPropertyPhoto;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class PropertyPhotoDao extends DaoBase<PropertyPhoto, Long> {


    public PropertyPhoto findByPropertyIdAndFileName(Long propertyId, String fileName) {
        final QPropertyPhoto photo = QPropertyPhoto.propertyPhoto;
        return queryFactory.selectFrom(photo)
                .where(photo.property.id.eq(propertyId)
                        .and(photo.fileName.eq(fileName)))
                .fetchOne();
    }
}
