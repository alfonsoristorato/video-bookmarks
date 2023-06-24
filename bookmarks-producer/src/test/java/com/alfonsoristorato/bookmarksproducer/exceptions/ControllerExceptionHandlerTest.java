package com.alfonsoristorato.bookmarksproducer.exceptions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.servlet.NoHandlerFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class ControllerExceptionHandlerTest {

    @InjectMocks
    private ControllerExceptionHandler controllerExceptionHandler;

    @Mock
    private MethodParameter methodParameter;

    @Test
    void missingHeaderException_shouldReturnExpectedResponseEntityAnd400(){
        doReturn(MissingRequestHeaderException.class).when(methodParameter).getNestedParameterType();
        MissingRequestHeaderException ex = new MissingRequestHeaderException("headerName",methodParameter);

        ResponseEntity<BadRequestError> response = controllerExceptionHandler.missingHeaderException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo(BadRequestError.BAD_REQUEST_ERROR("Required request header 'headerName' missing."));
    }

    @Test
    void badRequestException_shouldReturnExpectedResponseEntityAnd400(){
        BadRequestException ex = new BadRequestException(BadRequestError.BAD_REQUEST_ERROR("some error"));

        ResponseEntity<BadRequestError> response = controllerExceptionHandler.badRequestException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo(BadRequestError.BAD_REQUEST_ERROR("some error"));
    }

    @Test
    void methodNotSupportedException_shouldReturnExpectedResponseEntityAnd405(){
        HttpRequestMethodNotSupportedException ex = new HttpRequestMethodNotSupportedException(HttpMethod.GET.toString());

        ResponseEntity<BadRequestError> response = controllerExceptionHandler.methodNotSupportedException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
        assertThat(response.getBody()).isEqualTo(BadRequestError.BAD_REQUEST_ERROR(ex.getMessage()));
    }

    @Test
    void endpointNotHandledException_shouldReturnExpectedResponseEntityAnd405(){
        NoHandlerFoundException ex = new NoHandlerFoundException(HttpMethod.GET.toString(),"url", HttpHeaders.EMPTY);

        ResponseEntity<BadRequestError> response = controllerExceptionHandler.endpointNotHandledException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isEqualTo(BadRequestError.BAD_REQUEST_ERROR(ex.getMessage()));
    }


}
