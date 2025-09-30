package com.example.payments.web;

import jakarta.servlet.RequestDispatcher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AppStatusController.class)
class AppStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET / should return health payload")
    void rootReturnsHealthPayload() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.message").value("Payments Service is running"));
    }

    @Test
    @DisplayName("/error should map request attributes to ProblemDetail")
    void errorReturnsProblemDetail() throws Exception {
        mockMvc.perform(get("/error")
                        .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, HttpStatus.NOT_FOUND.value())
                        .requestAttr(RequestDispatcher.ERROR_MESSAGE, "Resource not found")
                        .requestAttr(RequestDispatcher.ERROR_REQUEST_URI, "/missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Unexpected error"))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.detail").value("Resource not found"))
                .andExpect(jsonPath("$.path").value("/missing"));
    }
}
