package com.creatix.service;

import com.creatix.domain.dao.AccountDao;
import com.creatix.domain.dao.DeviceDao;
import com.creatix.domain.dto.account.device.AccountDeviceDto;
import com.creatix.domain.entity.store.account.Account;
import com.creatix.domain.entity.store.account.device.Device;
import com.creatix.domain.enums.PlatformType;
import com.creatix.security.AuthorizationManager;
import com.creatix.security.RoleSecured;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Service
@Transactional
public class AccountDeviceService {

    @Autowired
    private DeviceDao deviceDao;
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private AuthorizationManager authorizationManager;

    @RoleSecured()
    public Device getOrCreateDevice(@NotNull String deviceUDID, @NotNull PlatformType platformType) {
        Objects.requireNonNull(deviceUDID);
        Objects.requireNonNull(platformType);

        Device device = deviceDao.findByUDID(deviceUDID);
        if (device == null) {
            device = new Device();
            device.setUdid(deviceUDID);
            device.setPlatform(platformType);
            deviceDao.persist(device);
        }

        return device;
    }

    @RoleSecured()
    public Device register(@NotNull Long accountId, @NotNull Long deviceId, @NotNull AccountDeviceDto request) {
        Objects.requireNonNull(accountId);
        Objects.requireNonNull(deviceId);
        Objects.requireNonNull(request);
        Objects.requireNonNull(request.getPushToken());

        final Device device = this.getDevice(deviceId);
        final Account account = this.getAccount(accountId);
        authorizationManager.checkAccess(device, account);

        device.setPushToken(request.getPushToken());
        device.setAccount(account);
        deviceDao.persist(device);

        return device;
    }

    private Device getDevice(@NotNull Long deviceId) {
        Objects.requireNonNull(deviceId);
        final Device device = this.deviceDao.findById(deviceId);
        if ( device == null ) {
            throw new EntityNotFoundException(String.format("Device id=%d not found", deviceId));
        }
        return device;
    }

    private Account getAccount(@NotNull Long accountId) {
        Objects.requireNonNull(accountId);
        final Account account = this.accountDao.findById(accountId);
        if ( account == null ) {
            throw new EntityNotFoundException(String.format("Account id=%d not found", accountId));
        }
        return account;
    }

}