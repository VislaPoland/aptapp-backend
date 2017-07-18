package com.creatix.domain.entity.store.attachment;

import com.creatix.domain.entity.store.business.BusinessProfile;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by Tomas Michalek on 19/04/2017.
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class BusinessProfilePhoto extends Attachment {

    @ManyToOne
    private BusinessProfile businessProfile;

    public BusinessProfilePhoto() {
        this.attachedEntityType = AttachedEntityType.BUSINESS_PROFILE;
    }

    @Override
    public void setAttachedEntityType(AttachedEntityType attachedEntityType) {}
}
