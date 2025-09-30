package com.example.payments.controller;

import com.example.payments.GlobalExceptionHandler;
import com.example.payments.model.PaymentRequest;
import com.example.payments.model.RefundRequest;
import com.example.payments.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MissingServletRequestParameterException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
@Import(GlobalExceptionHandler.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PaymentService paymentService;

    @Test
    @DisplayName("POST /authorize returns authorization id")
    void authorizeReturnsAuthId() throws Exception {
        Mockito.when(paymentService.authorize(any(PaymentRequest.class))).thenReturn("AUTH-999");

        PaymentRequest request = new PaymentRequest();
        request.buyerId = "buyer";
        request.sellerId = "seller";
        request.amount = 15.5;

        mockMvc.perform(post("/api/payments/authorize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("AUTH-999"));
    }

    @Test
    @DisplayName("POST /authorize returns validation errors")
    void authorizeReturnsValidationErrors() throws Exception {
        PaymentRequest request = new PaymentRequest();
        request.buyerId = "buyer";
        request.amount = 20.0; // missing sellerId

        mockMvc.perform(post("/api/payments/authorize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.sellerId").value("Seller ID is required"));
    }

    @Test
    @DisplayName("POST /capture returns service message")
    void captureReturnsServiceMessage() throws Exception {
        Mockito.when(paymentService.capture("AUTH-123"))
                .thenReturn("Captured payment: PAY-321");

        mockMvc.perform(post("/api/payments/capture").param("authId", "AUTH-123"))
                .andExpect(status().isOk())
                .andExpect(content().string("Captured payment: PAY-321"));
    }

    @Test
    @DisplayName("POST /capture requires authId parameter")
    void captureRequiresAuthId() throws Exception {
        mockMvc.perform(post("/api/payments/capture"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException())
                        .isInstanceOf(MissingServletRequestParameterException.class)
                        .hasMessageContaining("Required request parameter 'authId'"));
    }

    @Test
    @DisplayName("POST /refund returns service message")
    void refundReturnsServiceMessage() throws Exception {
        Mockito.when(paymentService.refund(any(RefundRequest.class)))
                .thenReturn("Refunded $10.0 for payment: PAY-555");

        RefundRequest request = new RefundRequest();
        request.paymentId = "PAY-555";
        request.amount = 10.0;

        mockMvc.perform(post("/api/payments/refund")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Refunded $10.0 for payment: PAY-555"));
    }

    @Test
    @DisplayName("POST /refund validates request body")
    void refundValidatesRequestBody() throws Exception {
        RefundRequest request = new RefundRequest();
        request.paymentId = ""; // fails @NotBlank
        request.amount = 0; // fails @Positive

        mockMvc.perform(post("/api/payments/refund")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.paymentId").value("Payment ID is required"))
                .andExpect(jsonPath("$.amount").value("Refund amount must be greater than zero"));
    }
}
