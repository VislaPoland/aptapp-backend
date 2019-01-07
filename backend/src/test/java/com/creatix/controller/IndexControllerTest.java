package com.creatix.controller;

import com.creatix.controller.exception.AptValidationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * if someone would like to write test for whole {@link IndexController} it should be run with {@link SpringRunner}
 * insteand of {@link MockitoJUnitRunner} class
 */
@RunWith(MockitoJUnitRunner.class)
public class IndexControllerTest {

    private static final String MOCKED_VALUE = "<mocked value>";
    private static final String MOCKED_PATH = "mocked/path";
    private static final String BAD_PARAMETER = "Bad parameter";

    @Captor
    private ArgumentCaptor<IndexController.ErrorMessage> errorMessageArgumentCaptor;


    @Test
    public void aptValidationExceptionHandling() throws IOException {
        ObjectMapper objectMapperMock = mock(ObjectMapper.class);
        when(objectMapperMock.writeValueAsString(any())).thenReturn(MOCKED_VALUE);

        HttpServletRequest httpServletRequest = spy(new MockHttpServletRequest());
        when(httpServletRequest.getServletPath()).thenReturn(MOCKED_PATH);

        IndexController indexController = new IndexController(httpServletRequest, objectMapperMock);

        AptValidationException aptValidationException = new AptValidationException(BAD_PARAMETER);

        HttpServletResponse httpServletResponse = spy(new MockHttpServletResponse());
        ServletOutputStream outputStreamMock = mock(ServletOutputStream.class);
        when(httpServletResponse.getOutputStream()).thenReturn(outputStreamMock);
        doNothing().when(outputStreamMock).print(any());

        indexController.aptValidationExceptionHandling(aptValidationException, httpServletResponse);

        verify(objectMapperMock).writeValueAsString(errorMessageArgumentCaptor.capture());

        IndexController.ErrorMessage errorMessage = errorMessageArgumentCaptor.getValue();

        assertEquals(BAD_PARAMETER, errorMessage.getError());
        assertEquals(BAD_PARAMETER, errorMessage.getMessage());
        assertEquals(AptValidationException.class.getCanonicalName(), errorMessage.getException());
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorMessage.getStatus());
        assertEquals(MOCKED_PATH, errorMessage.getPath());

        verify(outputStreamMock).print(MOCKED_VALUE);
    }
}