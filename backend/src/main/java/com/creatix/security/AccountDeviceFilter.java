package com.creatix.security;

import com.creatix.domain.dao.DeviceDao;
import com.creatix.domain.entity.store.account.device.Device;
import com.creatix.domain.enums.PlatformType;
import com.creatix.service.AccountDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class AccountDeviceFilter extends GenericFilterBean {

    @Value("${device.platform.header}")
    private String platformHeader;

    @Value("${device.udid.header}")
    private String deviceHeader;

    @Autowired
    private AccountDeviceService accountDeviceService;

    @Autowired
    private DeviceDao deviceDao;

    @Autowired
    private AuthorizationManager authorizationManager;

    @Autowired
    private HttpSession httpSession;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        final HttpServletRequest httpRequest = (HttpServletRequest) request;

        if (httpRequest.getRequestURI().contains("/api/") &&
                (httpRequest.getRequestURI().contains("/api/auth/verify-code") == false || httpRequest.getRequestURI().contains("/api/auth/attempt") == false)) {
            String platformString = httpRequest.getHeader(this.platformHeader);
            if (platformString == null) {
                throw new SecurityException("Platform type is required for all requests.");
            }
            PlatformType platformType = PlatformType.valueOf(platformString);
            if (platformType == null) {
                throw new SecurityException("Platform type is required in valid format for all requests.");
            }

            if (platformType != PlatformType.Web) {
                String deviceUDID = httpRequest.getHeader(this.deviceHeader);
                if (deviceUDID == null) {
                    throw new SecurityException("Device identifier is required for all requests.");
                }

                //final Device device = accountDeviceService.getOrCreateDevice(deviceUDID, platformType);
                Device device = deviceDao.findByUDID(deviceUDID);
                if (device == null) {
                    device = new Device();
                    device.setUdid(deviceUDID);
                    device.setPlatform(platformType);
                    deviceDao.persist(device);
                }
                if ((device.getAccount() == null && this.authorizationManager.getCurrentAccount() != null) ||
                        (device.getAccount() != null && this.authorizationManager.getCurrentAccount() != null &&
                                device.getAccount().getId() != this.authorizationManager.getCurrentAccount().getId())) {
                    device.setAccount(this.authorizationManager.getCurrentAccount());
                    deviceDao.persist(device);
                }
                this.httpSession.setAttribute("device", device);
            }
        }

        filterChain.doFilter(request, response);
    }

}
