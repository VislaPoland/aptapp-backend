package com.creatix.domain.entity.store.attachment;

import com.creatix.domain.entity.store.EventSlot;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class EventPhoto extends Attachment {

    @ManyToOne
    private EventSlot eventSlot;

    public EventPhoto() {
        this.attachedEntityType = AttachedEntityType.EVENT_PHOTO;
    }

    @Override
    public void setAttachedEntityType(AttachedEntityType attachedEntityType) {}
}
