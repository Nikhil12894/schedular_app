package com.nk.schedular.controller.advice;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.nk.schedular.dto.WebResponse;
import com.nk.schedular.exception.BadRequestException;
import com.nk.schedular.exception.DataConflictException;
import com.nk.schedular.exception.ForbiddenException;
import com.nk.schedular.exception.InternalServerException;
import com.nk.schedular.exception.MethodNotAllowedException;
import com.nk.schedular.exception.NotFoundException;


@ExtendWith({SpringExtension.class})
class TaskControllerAdviceTest {

    private TaskControllerAdvice taskControllerAdvice;

    @BeforeEach
    public void setup() {
        taskControllerAdvice = new TaskControllerAdvice();
    }
    @Test
  void test_HandleBadRequestFormatException() {
    WebResponse<Object> response =  WebResponse.builder().message("BAD REQUEST").build();
    ResponseEntity<WebResponse<Object>> expectedResponse = new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    ResponseEntity<WebResponse<Object>> actualResponse = taskControllerAdvice.handleBadRequestFormatException(new BadRequestException("BAD REQUEST"));
    assertThat(actualResponse).isEqualTo(expectedResponse);
  }

  @Test
  void test_HandleInternalServerException() {
    WebResponse<Object> response =  WebResponse.builder().message("error").build();
    ResponseEntity<WebResponse<Object>> expectedResponse = new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    ResponseEntity<WebResponse<Object>> actualResponse = taskControllerAdvice.handleInternalServerException(new InternalServerException("error"));
    assertThat(actualResponse).isEqualTo(expectedResponse);
    // Verify that an error log is written
    verify(mockLogger).error(contains("Internal server error occurred"));
  }

  @Test
  void test_HandleNotFoundException() {
    WebResponse<Object> response =  WebResponse.builder().message("Not Found").build();
    ResponseEntity<WebResponse<Object>> expectedResponse = new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    ResponseEntity<WebResponse<Object>> actualResponse = taskControllerAdvice.handleNotFoundException(new NotFoundException("Not Found"));
    assertThat(actualResponse).isEqualTo(expectedResponse);
  }

  @Test
  void test_HandleForbiddenException() {
    WebResponse<Object> response =  WebResponse.builder().message("Forbidden").build();
    ResponseEntity<WebResponse<Object>> expectedResponse = new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    ResponseEntity<WebResponse<Object>> actualResponse = taskControllerAdvice.handleForbiddenException(new ForbiddenException("Forbidden"));
    assertThat(actualResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    assertThat(actualResponse.getBody().getMessage()).contains("Forbidden");
  }

  @Test
  void test_DataConflictException() {
    WebResponse<Object> response =  WebResponse.builder().message("conflict").build();
    ResponseEntity<WebResponse<Object>> expectedResponse = new ResponseEntity<>(response, HttpStatus.CONFLICT);
    ResponseEntity<WebResponse<Object>> actualResponse = taskControllerAdvice.handleDataConflictException(new DataConflictException("conflict"));
    assertThat(actualResponse).isEqualTo(expectedResponse);
  }
  @Test
  void test_HandleMethodNotAllowedException() {
    WebResponse<Object> response =  WebResponse.builder().message("Task is frozen and cannot be changed").build();
    ResponseEntity<WebResponse<Object>> expectedResponse = new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);
    ResponseEntity<WebResponse<Object>> actualResponse = taskControllerAdvice.handleMethodNotAllowedException(new MethodNotAllowedException("Task is frozen and cannot be changed"));
    assertThat(actualResponse).isEqualTo(expectedResponse);
  }
}
