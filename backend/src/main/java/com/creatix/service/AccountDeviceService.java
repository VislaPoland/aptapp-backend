package com.creatix.service;

import com.creatix.configuration.DeviceProperties;
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
import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Service
@Transactional
public class AccountDeviceService {

    @Autowired
    private DeviceProperties deviceProperties;
    @Autowired
    private DeviceDao deviceDao;
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private AuthorizationManager authorizationManager;
    @Autowired
    private HttpSession httpSession;

    @NotNull
    public Device retrieveDevice(@NotNull String deviceUDID, @NotNull PlatformType platformType) {
        Objects.requireNonNull(deviceUDID, "Device UDID is null");
        Objects.requireNonNull(platformType, "Platform type is null");

        Device device = deviceDao.findByUDID(deviceUDID);
        if (device == null) {
            device = new Device();
            device.setUdid(deviceUDID);
            device.setPlatform(platformType);
        }
        // clear delete date
        device.setDeletedAt(null);
        deviceDao.persist(device);

        if ( this.authorizationManager.hasCurrentAccount() ) {
            final Account account = this.getAccount(this.authorizationManager.getCurrentAccount().getId());
            if (device.getAccount() == null || (device.getAccount() != null && device.getAccount().getId().equals(account.getId()))) {
                device.setAccount(account);
                deviceDao.persist(device);
            }
        }

        return device;
    }

    @RoleSecured
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

    @RoleSecured
    public Device assignDeviceToAccount(@NotNull Account account) {
        Objects.requireNonNull(account);

        PlatformType platformType = (PlatformType) httpSession.getAttribute(deviceProperties.getSessionKeyPlatform());
        if (platformType == PlatformType.Web) {
            return null;
        }

        Object deviceObject = httpSession.getAttribute(deviceProperties.getSessionKeyDevice());
        if ( !(deviceObject instanceof Device) ) {
            throw new SecurityException("Device is not recognized.");
        }

        final Device device = this.getDevice(((Device) deviceObject).getId());
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