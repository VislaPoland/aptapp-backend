package com.creatix.controller.v1;

import com.creatix.configuration.versioning.ApiVersion;
import com.creatix.controller.exception.AptValidationException;
import com.creatix.domain.dto.Views;
import com.creatix.domain.enums.AccountRole;
import com.creatix.message.MessageDeliveryException;
import com.creatix.security.RoleSecured;
import com.creatix.service.property.PropertyFloorPlanService;
import com.fasterxml.jackson.annotation.JsonView;
import freemarker.template.TemplateException;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;

@RestController
@Transactional
@RequestMapping(path = {"/api/properties/{propertyId}/plan", "/api/v1/properties/{propertyId}/plan"})
@ApiVersion(1.0)
@RequiredArgsConstructor
public class PropertyFloorPlanController {

    private final PropertyFloorPlanService propertyFloorPlanService;

    @ApiOperation(value = "Create floor plan for property from csv file")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(value = "", method = RequestMethod.POST)
    @RoleSecured({AccountRole.Administrator})
    public ResponseEntity<String> createFloorPlan(@PathVariable Long propertyId, @RequestPart MultipartFile csvFile) throws AptValidationException, IOException, MessageDeliveryException, TemplateException, MessagingException {
        propertyFloorPlanService.createFloorPlanFromCsv(propertyId, csvFile);

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
