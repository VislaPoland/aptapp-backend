package com.creatix.domain.entity.store.photo;

/**
 * Created by kvimbi on 19/04/2017.
 */
public interface PhotoObjectFactory<T extends GenericPhotoStore> {

    T createPhotoObject(Object foreignKeyObject);

}
