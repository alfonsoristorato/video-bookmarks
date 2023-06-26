package com.alfonsoristorato.bookmarksproducer.app.exceptions;

import com.alfonsoristorato.common.utils.exceptions.BadRequestError;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class ControllerExceptionHandlerTest {

    @InjectMocks
    private ControllerExceptionHandler controllerExceptionHandler;

    @Mock
    private HttpInputMessage httpInputMessage;

    @Test
    void httpMessageNotReadableException_shouldReturnExpectedResponseEntityAnd400() {
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("something", httpInputMessage);

        ResponseEntity<BadRequestError> response = controllerExceptionHandler.httpMessageNotReadableException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo(BadRequestError.BAD_REQUEST_ERROR("Required request body is missing."));
    }


}
