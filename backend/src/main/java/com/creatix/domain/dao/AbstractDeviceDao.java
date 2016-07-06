package com.creatix.domain.dao;

import com.creatix.domain.entity.account.device.Device;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Component
@Transactional
public abstract class AbstractDeviceDao<T extends Device> extends DaoBase<T, Long> {

    @Override
    public void persist(T device) {
        if ( device.getCreatedAt() == null ) {
            device.setCreatedAt(new Date());
            device.setUpdatedAt(new Date());
        }
        else {
            device.setUpdatedAt(new Date());
        }

        super.persist(device);
    }

}
