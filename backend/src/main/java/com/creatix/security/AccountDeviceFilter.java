package com.creatix.security;

import com.creatix.configuration.DeviceProperties;
import com.creatix.domain.entity.store.account.device.Device;
import com.creatix.domain.enums.PlatformType;
import com.creatix.service.AccountDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class AccountDeviceFilter extends GenericFilterBean {

    @Autowired
    private DeviceProperties deviceProperties;

    @Autowired
    private AccountDeviceService accountDeviceService;

    @Autowired
    private HttpSession httpSession;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        final HttpServletRequest httpRequest = (HttpServletRequest) request;


        String platformString = httpRequest.getHeader(this.deviceProperties.getPlatformHeader());
        if ( platformString == null ) {
            platformString = PlatformType.Web.toString();
        }
        PlatformType platformType = PlatformType.valueOf(platformString);
        httpSession.setAttribute(this.deviceProperties.getSessionKeyPlatform(), platformType);

        if ( platformType != PlatformType.Web ) {
            String deviceUDID = httpRequest.getHeader(this.deviceProperties.getUdidHeader());
            if ( deviceUDID == null ) {
                throw new SecurityException("Device identifier is required for all requests.");
            }

            final Device device = accountDeviceService.retrieveDevice(deviceUDID, platformType);
            this.httpSession.setAttribute(this.deviceProperties.getSessionKeyDevice(), device);
        }

        filterChain.doFilter(request, response);
    }

}
