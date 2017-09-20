package com.creatix.domain.dao;

import com.creatix.domain.entity.store.account.device.Device;
import com.creatix.domain.entity.store.account.device.QDevice;
import com.creatix.domain.enums.PlatformType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import java.util.Objects;

@Repository
@Transactional
public class DeviceDao extends AbstractDeviceDao<Device> {

    public Device findByUDIDAndPlatformType(@Nonnull String deviceUDID, @Nonnull PlatformType platformType) {
        Objects.requireNonNull(deviceUDID, "device UDID");
        Objects.requireNonNull(platformType, "platform type");

        final QDevice device = QDevice.device;
        return queryFactory
                .selectFrom(device)
                .where(device.udid.eq(deviceUDID).and(device.platform.eq(platformType)))
                .fetchOne();
    }

    public void clearInvalidToken(@Nonnull String token) {
        Objects.requireNonNull(token, "token");

        queryFactory
                .update(QDevice.device)
                .setNull(QDevice.device.pushToken)
                .where(QDevice.device.pushToken.toLowerCase().eq(token.toLowerCase()))
                .execute();
    }
}
