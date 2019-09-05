package com.creatix.controller.v1;

import com.creatix.configuration.versioning.ApiVersion;
import com.creatix.domain.Mapper;
import com.creatix.domain.dto.ApplicationFeatureDto;
import com.creatix.domain.dto.DataResponse;
import com.creatix.domain.dto.PageableWithTotalCountDataResponse;
import com.creatix.domain.dto.Views;
import com.creatix.domain.dto.property.*;
import com.creatix.domain.entity.store.PropertyLogo;
import com.creatix.domain.entity.store.PropertyPhoto;
import com.creatix.domain.enums.AccountRole;
import com.creatix.security.RoleSecured;
import com.creatix.service.ApplicationFeatureService;
import com.creatix.service.property.PropertyService;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
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
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Transactional
@RequestMapping(path = {"/api/properties", "/api/v1/properties"})
@ApiVersion(1.0)
@Slf4j
public class PropertyController {

    private static final String CSV_MIME_TYPE = "text/csv";

    @Autowired
    private Mapper mapper;
    @Autowired
    private PropertyService propertyService;
    @Autowired
    private ApplicationFeatureService applicationFeatureService;

    private void generateFileOutputResponse(String outputString, final HttpServletResponse response, String contentType) {
        response.setHeader("Content-Disposition", "attachment");
        response.setContentType(contentType + "; charset=UTF-8");
        try (final OutputStream out = response.getOutputStream()) {
            out.write(outputString.getBytes(StandardCharsets.UTF_8));
            out.flush();
        } catch (IOException e) {
            log.error("Unable to write csv data to output stream", e);
        }
    }

    @ApiOperation(value = "Get all properties")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
    })
    @JsonView(Views.Public.class)
    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping(params = { "page", "size" })
    @RoleSecured({AccountRole.Administrator, AccountRole.PropertyOwner, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.Security, AccountRole.Maintenance})
    public PageableWithTotalCountDataResponse<List<PropertyDto>> getAllProperties(@RequestParam(value="page",required=false) Integer page, 
    		  @RequestParam(value="size",required=false) Integer size, @RequestParam(value="keywords",required=false) String keywords) {
    	PageableWithTotalCountDataResponse<List<PropertyDto>> ret;
    	ret = new PageableWithTotalCountDataResponse<List<PropertyDto>>(propertyService.getAllProperties(page, size, keywords).stream()
                .map(p -> mapper.toPropertyDto(p)).collect(Collectors.toList()),size,page,propertyService.getAllPropertiesCount(keywords),10);
    	return ret;
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
    @RequestMapping(value = "/{propertyId}/csv", method = RequestMethod.GET, produces = CSV_MIME_TYPE)
    @RoleSecured({AccountRole.Administrator, AccountRole.PropertyOwner, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.Security, AccountRole.Maintenance})
    public void getPropertyAccounts(final HttpServletResponse response, @PathVariable long propertyId) {
        String csvResponse = propertyService.generateCsvResponse(propertyId);
        generateFileOutputResponse(csvResponse, response, CSV_MIME_TYPE);
    }

    @ApiOperation(value = "Get users all properties accounts to csv")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(value = "/all/csv", method = RequestMethod.GET, produces = CSV_MIME_TYPE)
    @RoleSecured({AccountRole.Administrator})
    public void getAllPropertyAccounts(final HttpServletResponse response) {
        String csvResponse = propertyService.generateAllCsvResponse();
        generateFileOutputResponse(csvResponse, response, CSV_MIME_TYPE);
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
    @RoleSecured({AccountRole.Administrator, AccountRole.PropertyOwner, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager})
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

                if (photoFile.toPath().toString().toUpperCase().endsWith(".JPEG")) {
                    headers.setContentType(MediaType.IMAGE_JPEG);
                } else if (photoFile.toPath().toString().toUpperCase().endsWith(".GIF")) {
                    headers.setContentType(MediaType.IMAGE_GIF);
                } else {
                    headers.setContentType(MediaType.IMAGE_PNG);
                }
                headers.setContentLength(photoFileData.length);

                return new HttpEntity<>(photoFileData, headers);
            } catch (IOException exception) {
                throw new EntityNotFoundException("Unable to locate photo file");
            }
        }

        throw new EntityNotFoundException("Unable to locate photo file");
    }

    @ApiOperation(value = "Upload property logo")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(value = "/{propertyId}/logo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<PropertyDto> storePropertyLogo(@RequestParam MultipartFile file, @PathVariable long propertyId) throws IOException {
        return new DataResponse<>(mapper.toPropertyDto(propertyService.storePropertyLogo(file, propertyId)));
    }

    @ApiOperation(value = "Delete property logo")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(value = "/{propertyId}/logo", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured({AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.PropertyOwner, AccountRole.Administrator})
    public DataResponse<PropertyLogoDto> deletePropertyLogo(@PathVariable Long propertyId) throws IOException {
        return new DataResponse<>(mapper.toPropertyLogoDto(propertyService.deletePropertyLogo(propertyId)));
    }

    @ApiOperation(value = "Download property logo")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @JsonView(Views.Public.class)
    @RequestMapping(value = "/{propertyId}/logo", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> getFile(@PathVariable Long propertyId) throws IOException {
        final PropertyLogo logo = propertyService.getPropertyLogo(propertyId);
        final File logoFile = new File(logo.getFilePath());
        if (logoFile.exists()) {
            try {
                final byte[] logoFileData = FileUtils.readFileToByteArray(logoFile);

                final HttpHeaders headers = new HttpHeaders();

                if (logoFile.toPath().toString().toUpperCase().endsWith(".JPEG")) {
                    headers.setContentType(MediaType.IMAGE_JPEG);
                } else if (logoFile.toPath().toString().toUpperCase().endsWith(".GIF")) {
                    headers.setContentType(MediaType.IMAGE_GIF);
                } else {
                    headers.setContentType(MediaType.IMAGE_PNG);
                }
                headers.setContentLength(logoFileData.length);

                return new HttpEntity<>(logoFileData, headers);
            } catch (IOException exception) {
                throw new EntityNotFoundException("Unable to locate logo file");
            }
        }

        throw new EntityNotFoundException("Unable to locate logo file");
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
    @RoleSecured({AccountRole.Administrator, AccountRole.PropertyOwner, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager})
    public DataResponse<PropertyStatsDto> getPropertyStats(@PathVariable Long propertyId) {
        return new DataResponse<>(propertyService.getPropertyStats(propertyId));
    }
}
