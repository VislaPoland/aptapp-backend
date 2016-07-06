package com.creatix.domain.dao;

import com.creatix.domain.entity.account.device.Device;
import com.creatix.domain.entity.account.device.QDevice;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Repository
@Transactional
public class DeviceDao extends AbstractDeviceDao<Device> {

    public Device findByUDID(String udid) {
        Objects.requireNonNull(udid);

        final QDevice device = QDevice.device;
        return queryFactory
                .selectFrom(device)
                .where(device.udid.eq(udid))
                .fetchOne();
    }

}
