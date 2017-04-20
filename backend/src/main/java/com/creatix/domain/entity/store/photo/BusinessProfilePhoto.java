package com.creatix.domain.entity.store.photo;

import com.creatix.domain.entity.store.business.BusinessProfile;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by kvimbi on 19/04/2017.
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class BusinessProfilePhoto extends GenericPhotoStore {

    @ManyToOne(optional = false)
    private BusinessProfile businessProfile;

    public BusinessProfilePhoto() {
        this.setStoredPhotoType(StoredPhotoType.BUSINESS_PROFILE);
    }

    @Override
    public void setStoredPhotoType(StoredPhotoType storedPhotoType) {}

}
