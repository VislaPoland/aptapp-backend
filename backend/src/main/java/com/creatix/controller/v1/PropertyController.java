package com.creatix.controller.v1;

import com.creatix.configuration.versioning.ApiVersion;
import com.creatix.domain.Mapper;
import com.creatix.domain.dto.ApplicationFeatureDto;
import com.creatix.domain.dto.DataResponse;
import com.creatix.domain.dto.Views;
import com.creatix.domain.dto.property.*;
import com.creatix.domain.entity.store.PropertyPhoto;
import com.creatix.domain.enums.AccountRole;
import com.creatix.security.RoleSecured;
import com.creatix.service.AccountService;
import com.creatix.service.ApplicationFeatureService;
import com.creatix.service.SlotService;
import com.creatix.service.property.PropertyService;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Transactional
@RequestMapping(path = {"/api/properties", "/api/v1/properties"})
@ApiVersion(1.0)
public class PropertyController {
    @Autowired
    private Mapper mapper;
    @Autowired
    private PropertyService propertyService;

    @Autowired
    private AccountService accountService;
    @Autowired
    private SlotService slotService;
    @Autowired
    private ApplicationFeatureService applicationFeatureService;

    @ApiOperation(value = "Get all properties")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
    })
    @JsonView(Views.Public.class)
    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured({AccountRole.Administrator, AccountRole.PropertyOwner, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.Security, AccountRole.Maintenance})
    public DataResponse<List<PropertyDto>> getAllProperties() {
        return new DataResponse<>(propertyService.getAllProperties().stream()
                .map(p -> mapper.toPropertyDto(p))
                .collect(Collectors.toList()));
    }

    @ApiOperation(value = "Get property detail")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(value = "/{propertyId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured({AccountRole.Administrator, AccountRole.PropertyOwner, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.Security, AccountRole.Maintenance})
    public DataResponse<PropertyDto> getPropertyDetails(@PathVariable long propertyId) {
        return new DataResponse<>(mapper.toPropertyDto(propertyService.getProperty(propertyId)));
    }

    @ApiOperation(value = "Get property accounts to csv")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(value = "/{propertyId}/csv", method = RequestMethod.GET, produces = "text/csv")
    @RoleSecured({AccountRole.Administrator, AccountRole.PropertyOwner, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.Security, AccountRole.Maintenance})
    public ResponseEntity<String> getPropertyAccounts(final HttpServletResponse response, @PathVariable long propertyId) {
        response.setHeader("Content-Disposition", "attachment; filename=property_"+propertyId+"_accounts.csv");
        response.setContentType("text/csv");
        String csvResponse = propertyService.generateCsvResponse(propertyId);
        return new ResponseEntity<>(csvResponse, HttpStatus.OK);

    }

    @JsonView(Views.Public.class)
    @RequestMapping(value = "/{propertyId}/xlsx", method = RequestMethod.GET, produces = "text/xlsx")
    @RoleSecured({AccountRole.Administrator, AccountRole.PropertyOwner, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.Security, AccountRole.Maintenance})
    public void getPropertyAccountsXlsx(final HttpServletResponse response, @PathVariable long propertyId) {
        response.setHeader("Content-Disposition", "attachment; filename=property_"+propertyId+"_accounts.xlsx");
        response.setContentType("text/xlsx");
        Workbook wb = propertyService.generateXlsxResponse(propertyId);
        try {
            wb.write(response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @ApiOperation(value = "Create new property")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured({AccountRole.Administrator})
    public DataResponse<PropertyDto> createProperty(@Valid @RequestBody CreatePropertyRequest request) {
        return new DataResponse<>(mapper.toPropertyDto(propertyService.createFromRequest(request)));
    }

    @ApiOperation(value = "Update property", notes = "Update existing property. This endpoint can only be called by account with Administrator role.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(value = "/{propertyId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured({AccountRole.PropertyOwner, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager})
    public DataResponse<PropertyDto> updateProperty(@PathVariable Long propertyId, @Valid @RequestBody UpdatePropertyRequest request) {
        return new DataResponse<>(mapper.toPropertyDto(propertyService.updateFromRequest(propertyId, request)));
    }

    @ApiOperation(value = "Delete property", notes = "Delete existing property.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(value = "/{propertyId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured({AccountRole.Administrator})
    public DataResponse<PropertyDto> deleteProperty(@PathVariable Long propertyId) {
        return new DataResponse<>(mapper.toPropertyDto(propertyService.deleteProperty(propertyId)));
    }

    @ApiOperation(value = "Upload property photo")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(value = "/{propertyId}/photos", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<PropertyDto> storePropertyPhotos(@RequestParam MultipartFile[] files, @PathVariable long propertyId) throws IOException {
        return new DataResponse<>(mapper.toPropertyDto(propertyService.storePropertyPhotos(files, propertyId)));
    }

    @ApiOperation(value = "Delete property photo")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(value = "/{propertyId}/photos/{photoId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured({AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.PropertyOwner, AccountRole.Administrator})
    public DataResponse<PropertyPhotoDto> deletePropertyPhoto(@PathVariable Long propertyId, @PathVariable Long photoId) throws IOException {
        return new DataResponse<>(mapper.toPropertyPhotoDto(propertyService.deletePropertyPhoto(photoId)));
    }

    @ApiOperation(value = "Download property photo")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(value = "/{propertyId}/photos/{fileName:.+}", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> getFile(@PathVariable Long propertyId, @PathVariable String fileName) throws IOException {
        final PropertyPhoto photo = propertyService.getPropertyPhoto(propertyId, fileName);
        final File photoFile = new File(photo.getFilePath());
        if (photoFile.exists()) {
            try {
                final byte[] photoFileData = FileUtils.readFileToByteArray(photoFile);

                final HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.valueOf(Files.probeContentType(photoFile.toPath())));
                headers.setContentLength(photoFileData.length);

                return new HttpEntity<>(photoFileData, headers);
            } catch (IOException exception) {
                throw new EntityNotFoundException("Unable to locate photo file");
            }
        }

        throw new EntityNotFoundException("Unable to locate photo file");
    }


    @ApiOperation(value = "Get application features configuration for property")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(path = "/{propertyId}/features", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<List<ApplicationFeatureDto>> listFeaturesForProperty(@PathVariable Long propertyId) {
        return new DataResponse<>(applicationFeatureService.listFeaturesByProperty(propertyId).stream().map(
                applicationFeature -> mapper.toApplicationFeatureDto(applicationFeature)
        ).collect(Collectors.toList()));
    }

    @ApiOperation(value = "Get property stats")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @GetMapping(path = "/{propertyId}/stats", produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured({AccountRole.PropertyOwner, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager})
    public DataResponse<PropertyStatsDto> getPropertyStats(@PathVariable Long propertyId) {
        return new DataResponse<>(propertyService.getPropertyStats(propertyId));
    }
}
