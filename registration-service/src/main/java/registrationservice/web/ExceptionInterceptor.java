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

package registrationservice.web;

import registrationservice.service.exception.IllegalModificationException;
import registrationservice.service.exception.RemoteResourceException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@ControllerAdvice
@ResponseBody
public class ExceptionInterceptor {
    private static final Logger logger = LogManager.getLogger(ExceptionInterceptor.class);

    public static class JsonErrorMessage implements Serializable {
        private final LocalDateTime timestamp;
        private final int status;
        private final String error;
        private final String path;

        public JsonErrorMessage(String error, String path, HttpStatus status) {
            this.timestamp = LocalDateTime.now();
            this.status = status.value();
            this.error = error;
            this.path = path;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public int getStatus() {
            return status;
        }

        public String getError() {
            return error;
        }

        public String getPath() {
            return path;
        }
    }

    @ExceptionHandler({ NoSuchElementException.class, MethodArgumentTypeMismatchException.class })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public JsonErrorMessage handleNotFoundException(Exception e, HttpServletRequest request) {
        String msg = e.getMessage();
        String path = request.getServletPath();
        logger.error(msg + ". Path:" + path);
        return new JsonErrorMessage(msg, path, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({ HttpMessageNotReadableException.class,
            HttpMediaTypeNotSupportedException.class,
            UnsatisfiedServletRequestParameterException.class,
            MissingServletRequestParameterException.class,
            RequestRejectedException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public JsonErrorMessage handleBadRequestException(Exception e, HttpServletRequest request) {
        String msg = e.getMessage();
        String path = request.getServletPath();
        logger.error(msg + ". Path:" + path);
        return new JsonErrorMessage(msg, path, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public JsonErrorMessage handleNotSupportedException(HttpRequestMethodNotSupportedException e,
                                                        HttpServletRequest request) {
        String msg = e.getMessage();
        String path = request.getServletPath();
        logger.error(msg + ". Path:" + path);
        return new JsonErrorMessage(msg, path, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public JsonErrorMessage handleAccessDeniedException(AccessDeniedException e,
                                                        HttpServletRequest request) {
        String msg = e.getMessage();
        String path = request.getServletPath();
        logger.error(msg + ". Path: " + path);
        return new JsonErrorMessage(msg, path, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(IllegalModificationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public JsonErrorMessage handleModificationException(IllegalModificationException e,
                                                        HttpServletRequest request) {
        String msg = e.getMessage();
        String path = request.getServletPath();
        logger.error(msg + ". Path: " + path);
        return new JsonErrorMessage(msg, path, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public JsonErrorMessage handleValidationException(MethodArgumentNotValidException e,
                                                      HttpServletRequest request) {
        StringBuilder builder = new StringBuilder();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String errorMsg = error.getDefaultMessage();
            builder.append(errorMsg).append(", ");
        });
        builder.delete(builder.lastIndexOf(","), builder.length());

        String path = request.getServletPath();
        logger.error(builder + ". Path: " + path);
        return new JsonErrorMessage(builder.toString(), path, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RemoteResourceException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public JsonErrorMessage handleRemoteResourceException(RemoteResourceException e,
                                                          HttpServletRequest request) {
        String msg = e.getMessage();
        String path = request.getServletPath();
        logger.error(msg + ". Path: " + path);
        return new JsonErrorMessage(msg, path, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public JsonErrorMessage handleUnknownException(Exception e, HttpServletRequest request) {
        String path = request.getServletPath();
        logger.error(e.getMessage() + ". Path: " + path);
        return new JsonErrorMessage("Unknown error", path, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
