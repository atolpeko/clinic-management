/*
 * Copyright 2002-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package clinicservice.web;

import clinicservice.service.exception.IllegalModificationException;
import clinicservice.service.exception.RemoteResourceException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@ControllerAdvice
@ResponseBody
public class ExceptionInterceptor {
    private static final Logger logger = LogManager.getLogger(ExceptionInterceptor.class);

    @ExceptionHandler({ NoSuchElementException.class, MethodArgumentTypeMismatchException.class })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleNotFoundException(Exception e) {
        logger.error(e.getMessage());
    }

    @ExceptionHandler({ HttpMessageNotReadableException.class,
            UnsatisfiedServletRequestParameterException.class,
            MissingServletRequestParameterException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBadRequestException(Exception e) {
        logger.error(e.getMessage());
        return e.getMessage();
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public String handleNotSupportedException(HttpRequestMethodNotSupportedException e) {
        logger.error(e.getMessage());
        return e.getMessage();
    }

    @ExceptionHandler(IllegalModificationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalModificationException(IllegalModificationException e) {
        logger.error(e.getMessage());
        return e.getMessage();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMsg = error.getDefaultMessage();
            errors.put(fieldName, errorMsg);
        });

        logger.error(errors);
        return errors;
    }

    @ExceptionHandler(RemoteResourceException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleRemoteResourceException(RemoteResourceException e) {
        logger.error(e.getMessage());
        return e.getMessage();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleUnknownException(Exception e) {
        logger.error(e.getMessage());
        return "Unknown error";
    }
}
