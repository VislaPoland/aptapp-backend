package com.creatix.controller.account.device;

import com.creatix.configuration.DeviceProperties;
import com.creatix.domain.dto.DataResponse;
import com.creatix.domain.dto.account.device.AccountDeviceDto;
import com.creatix.domain.entity.store.account.device.Device;
import com.creatix.security.RoleSecured;
import com.creatix.service.AccountDeviceService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@RestController
@Transactional
@RequestMapping("/api/users/{accountId}/devices")
public class AccountDeviceController {

    @Autowired
    private DeviceProperties deviceProperties;

    @Autowired
    private AccountDeviceService accountDeviceService;

    @Autowired
    private HttpSession httpSession;

    @ApiOperation(value = "Register device to push notifications")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @RoleSecured()
    @RequestMapping(value = "/notifications", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<Boolean> register(@PathVariable Long accountId, @RequestBody @Valid AccountDeviceDto request) {
        Object deviceObject = httpSession.getAttribute(this.deviceProperties.getSessionKeyDevice());
        if (deviceObject instanceof Device == false) {
            throw new SecurityException("Device is not recognized.");
        }

        return new DataResponse<>(accountDeviceService.register(accountId, ((Device) deviceObject).getId(), request) != null);
    }
    
}
