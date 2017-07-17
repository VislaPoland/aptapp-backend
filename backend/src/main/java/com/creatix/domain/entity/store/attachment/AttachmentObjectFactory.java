package com.creatix.domain.entity.store.attachment;

/**
 * Created by Tomas Michalek on 19/04/2017.
 */
public interface AttachmentObjectFactory<T extends Attachment> {

    T createAttachment(Object foreignKeyObject);

}
