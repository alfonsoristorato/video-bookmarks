package com.alfonsoristorato.bookmarksproducer.service.exceptions;

import com.alfonsoristorato.common.utils.exceptions.GlobalExceptionHandler;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.ControllerAdvice;


@ControllerAdvice
@Import(GlobalExceptionHandler.class)
public class ControllerExceptionHandler {

}
