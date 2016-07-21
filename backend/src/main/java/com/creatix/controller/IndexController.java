package com.creatix.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.Data;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Controller for HTML pages.
 */
@RestController
@ControllerAdvice
class IndexController {

    private final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private HttpServletRequest httpServletRequest;
    @Autowired
    private ObjectMapper jsonMapper;

    @ApiOperation(value = "Download static file")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found")})
    @RequestMapping(value = "/static/{fileName:.+}", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> downloadNotificationPhoto(@PathVariable @NotEmpty String fileName) throws IOException {
        final ClassLoader classLoader = getClass().getClassLoader();
        final Path absolutePath = Paths.get("static", fileName);
        final String strAbsolutePath = absolutePath.toString();
        final byte[] fileData;
        try ( final InputStream templateResourceStream = classLoader.getResourceAsStream(strAbsolutePath) ) {
            if ( templateResourceStream == null ) {
                throw new IOException("Resource " + strAbsolutePath + " not found.");
            }

            fileData = IOUtils.toByteArray(templateResourceStream);
        }

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(Files.probeContentType(absolutePath)));
        headers.setContentLength(fileData.length);

        return new HttpEntity<>(fileData, headers);
    }

    @RequestMapping(value = { "/app", "/app/" }, method = RequestMethod.GET)
    public RedirectView redirectApp() {
        return new RedirectView("/app/index.html");
    }

    @ExceptionHandler({Throwable.class, Exception.class})
    public void serverError(Exception ex, HttpServletResponse response) throws IOException {
        handleException(ex, HttpStatus.INTERNAL_SERVER_ERROR, response);
    }

    @ExceptionHandler({AuthenticationException.class, UsernameNotFoundException.class})
    public void unauthorized(Exception ex, HttpServletResponse response) throws IOException {
        handleException(ex, HttpStatus.UNAUTHORIZED, response);
    }

    @ExceptionHandler({SecurityException.class, AccessDeniedException.class})
    public void forbidden(Exception ex, HttpServletResponse response) throws IOException {
        handleException(ex, HttpStatus.FORBIDDEN, response);
    }

    @ExceptionHandler({EntityNotFoundException.class})
    public void notFound(Exception ex, HttpServletResponse response) throws IOException {
        handleException(ex, HttpStatus.NOT_FOUND, response);
    }

    @ExceptionHandler({NullPointerException.class, DataIntegrityViolationException.class, ConstraintViolationException.class, IllegalArgumentException.class, IllegalStateException.class, MethodArgumentNotValidException.class})
    public void integrityViolation(Exception ex, HttpServletResponse response) throws IOException {
        handleException(ex, HttpStatus.UNPROCESSABLE_ENTITY, response);
    }

    private void handleException(Exception ex, HttpStatus status, HttpServletResponse resp) throws IOException {
        logger.error("Error processing request", ex);
        resp.setStatus(status.value());
        resp.setContentType(MediaType.APPLICATION_JSON_VALUE);

        final String exceptionMessage = StringUtils.trim(StringUtils.isEmpty(ex.getMessage()) ? status.getReasonPhrase() : ex.getMessage());
        final ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setError(exceptionMessage);
        errorMessage.setException(ex.getClass().getCanonicalName());
        errorMessage.setMessage(exceptionMessage);
        errorMessage.setStatus(status.value());
        errorMessage.setTimestamp(System.currentTimeMillis());
        errorMessage.setPath(httpServletRequest.getServletPath());
        resp.getOutputStream().print(jsonMapper.writeValueAsString(errorMessage));
    }

    @Data
    private static class ErrorMessage {
        private long timestamp;
        private int status;
        private String error;
        private String exception;
        private String message;
        private String path;
    }
}
