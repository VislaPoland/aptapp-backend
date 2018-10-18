package com.creatix.domain.entity.store.notification;

import com.creatix.domain.entity.store.attachment.AttachedEntityType;
import com.creatix.domain.entity.store.attachment.Attachment;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class PersonalMessagePhoto extends Attachment {

    @ManyToOne
    private PersonalMessage personalMessage;

    public PersonalMessagePhoto() {
        this.attachedEntityType = AttachedEntityType.PERSONAL_MESSAGE;
    }

    @Override
    public void setAttachedEntityType(AttachedEntityType attachedEntityType) {}
}
