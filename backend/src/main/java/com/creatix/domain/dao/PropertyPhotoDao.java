package com.creatix.domain.dao;

import com.creatix.domain.entity.store.PropertyPhoto;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class PropertyPhotoDao extends DaoBase<PropertyPhoto, Long> {


}
