package com.creatix.controller;

import com.creatix.domain.Mapper;
import com.creatix.domain.dto.ApartmentDto;
import com.creatix.domain.dto.DataResponse;
import com.creatix.security.RoleSecured;
import com.creatix.service.ApartmentService;
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
@RequestMapping("/api/apartments")
public class ApartmentController {

    @Autowired
    private Mapper mapper;
    @Autowired
    private ApartmentService apartmentService;

    @ApiOperation(value = "Get apartment details")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(value = "/{apartmentId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<ApartmentDto> getApartment(@PathVariable Long apartmentId) {
        return new DataResponse<>(mapper.toApartmentDto(apartmentService.getApartment(apartmentId)));
    }
}
