package com.creatix.controller.v1;

import com.creatix.configuration.versioning.ApiVersion;
import com.creatix.domain.dto.DataResponse;
import com.creatix.domain.dto.Views;
import com.creatix.domain.dto.property.BasicAccountDto;
import com.creatix.domain.mapper.PropertyMapper;
import com.creatix.security.RoleSecured;
import com.creatix.service.property.PropertyEmployeeService;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Transactional
@RequestMapping(path = {"/api/properties/{propertyId}/employees", "/api/v1/properties/{propertyId}/employees"})
@ApiVersion(1.0)
public class PropertyEmployeeController {

    @Autowired
    private PropertyMapper mapper;
    @Autowired
    private PropertyEmployeeService propertyEmployeeService;

    @ApiOperation(value = "Remove property employee from property")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(value = "/{employeeId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<BasicAccountDto> removePropertyEmployee(@PathVariable Long propertyId, @PathVariable Long employeeId) {
        return new DataResponse<>(mapper.toBasicAccount(propertyEmployeeService.delete(propertyId, employeeId)));
    }

}
