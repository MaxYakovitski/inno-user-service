package com.innowise.userservice.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import static org.assertj.core.api.Assertions.*;

class UserServiceExceptionHandlerTest {

    private final UserServiceExceptionHandler handler = new UserServiceExceptionHandler();

    @Test
    void handleUnexpected_should_return_internal_server_error_problem_detail() {
        Exception e = new RuntimeException("Something went wrong");

        ProblemDetail problemDetail = handler.handleUnexpected(e);

        assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(problemDetail.getTitle()).isEqualTo("Internal Server Error");
        assertThat(problemDetail.getProperties()).containsEntry("code", "internal_error");
    }
}