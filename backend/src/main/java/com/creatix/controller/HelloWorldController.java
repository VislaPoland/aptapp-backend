package com.creatix.controller;

import com.creatix.domain.dto.DataResponse;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Transactional
@RequestMapping("/api/hello")
class HelloWorldController {

    @ApiOperation(value = "hello world")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not found")})
    @RequestMapping(value = "/world", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<String> getEmployers() {
        return new DataResponse<>("Hello world!");
    }

}
