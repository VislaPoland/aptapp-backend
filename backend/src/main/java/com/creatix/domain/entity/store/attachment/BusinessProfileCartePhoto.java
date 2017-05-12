package com.creatix.domain.entity.store.attachment;

import com.creatix.domain.entity.store.business.BusinessProfileCarteItem;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

/**
 * Created by Tomas Michalek on 19/04/2017.
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class BusinessProfileCartePhoto extends Attachment {


    @OneToOne(optional = false)
    private BusinessProfileCarteItem businessProfileCarteItem;

    public BusinessProfileCartePhoto() {
        this.setAttachedEntityType(AttachedEntityType.BUSINESS_PROFILE_CARTE);
    }

    @Override
    public void setAttachedEntityType(AttachedEntityType attachedEntityType) {}
}
