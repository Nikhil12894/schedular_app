package com.nk.schedular.controller.advice;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.nk.schedular.dto.WebResponse;
import com.nk.schedular.exception.BadRequestException;
import com.nk.schedular.exception.DataConflictException;
import com.nk.schedular.exception.DuplicateTransactionException;
import com.nk.schedular.exception.ForbiddenException;
import com.nk.schedular.exception.InternalServerException;
import com.nk.schedular.exception.MethodNotAllowedException;
import com.nk.schedular.exception.NoContentException;
import com.nk.schedular.exception.NotFoundException;
import com.nk.schedular.exception.UnAuthorizedException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class TaskControllerAdvice {
    /**
     * Handles DuplicateConfigurationNameException and returns an HTTP
     * response with a message and status code of 409 (Conflict).
     */
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DuplicateTransactionException.class)
    @ResponseBody
    public ResponseEntity<WebResponse<Object>> handleDDuplicateTransactionException(DuplicateTransactionException ex) {
        WebResponse<Object> response = new WebResponse<>();
        response.setMessage(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    /**
     * Response for a bad request.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    @ResponseBody
    public ResponseEntity<WebResponse<Object>> handleBadRequestFormatException(BadRequestException ex) {
        WebResponse<Object> response = new WebResponse<>();
        response.setMessage(ex.getMessage());
        if (ObjectUtils.isEmpty(ex.getErrors())) {
            response.setErrors(ex.getErrors());
        }
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Response for a any server error.
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(InternalServerException.class)
    @ResponseBody
    public ResponseEntity<WebResponse<Object>> handleInternalServerException(InternalServerException ex) {
        WebResponse<Object> response = new WebResponse<>();
        response.setMessage(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Response for any data not found error.
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    @ResponseBody
    public ResponseEntity<WebResponse<Object>> handleNotFoundException(NotFoundException ex) {
        WebResponse<Object> response = new WebResponse<>();
        response.setMessage(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * Response for any method not allowed error.
     */
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(MethodNotAllowedException.class)
    @ResponseBody
    public ResponseEntity<WebResponse<Object>> handleMethodNotAllowedException(MethodNotAllowedException ex) {
        WebResponse<Object> response = new WebResponse<>();
        response.setMessage(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);
    }

    /**
     * Response for any forbidden error.
     */
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(ForbiddenException.class)
    @ResponseBody
    public ResponseEntity<WebResponse<Object>> handleForbiddenException(ForbiddenException ex) {
        WebResponse<Object> response = new WebResponse<>();
        response.setMessage(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    /**
     * Response for any unauthorized error.
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnAuthorizedException.class)
    @ResponseBody
    public ResponseEntity<WebResponse<Object>> handleUnauthorizedException(UnAuthorizedException ex) {
        WebResponse<Object> response = new WebResponse<>();
        response.setMessage(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Response for any data conflict.
     */
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DataConflictException.class)
    @ResponseBody
    public ResponseEntity<WebResponse<Object>> handleDataConflictException(DataConflictException ex) {
        WebResponse<Object> response = new WebResponse<>();
        response.setMessage(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    /**
     * Reports the result of constraint violations.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public ResponseEntity<WebResponse<Object>> handleConstraintViolationError(ConstraintViolationException ex) {
        WebResponse<Object> response = new WebResponse<>();
        List<String> errorMessages = ex.getConstraintViolations().stream().map(ConstraintViolation::getMessage)
                .toList();
        response.setMessage(String.join(",", errorMessages));
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Reports No Content.
     */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ExceptionHandler(NoContentException.class)
    @ResponseBody
    public ResponseEntity<WebResponse<Object>> handleNoContentError(NoContentException ex) {
        WebResponse<Object> response = new WebResponse<>();
        response.setMessage(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }

    /**
     * Handles HttpMessageNotReadableException and returns an HTTP response with a
     * message and status code of 400 (Bad Request).
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ HttpMessageNotReadableException.class })
    public ResponseEntity<WebResponse<Object>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        WebResponse<Object> response = new WebResponse<>();
        response.setMessage(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Response for request when validation on an argument annotated with @Valid
     * fails.
     */
    @SuppressWarnings("null")
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<WebResponse<Object>> handleMissingFieldError(MethodArgumentNotValidException ex) {
        WebResponse<Object> response = new WebResponse<>();
        List<String> requiredFields = ex.getBindingResult().getFieldErrors().stream().map(FieldError::getField)
                .toList();
        List<String> requiredFields1 = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .toList();
        if (requiredFields.isEmpty() && requiredFields1.isEmpty()) {
            response.setMessage(ex.getBindingResult().getGlobalError().getDefaultMessage());
        } else {
            response.setMessage(String.format("The %s field is required. Please include it in the request or %s",
                    String.join(",", requiredFields), String.join(",", requiredFields1)));
        }
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}