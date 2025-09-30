package com.example.payments;

import com.example.payments.model.PaymentRequest;
import com.example.payments.model.RefundRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void authorizeCaptureAndRefundEndToEnd() throws Exception {
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.buyerId = "buyer-1";
        paymentRequest.sellerId = "seller-1";
        paymentRequest.amount = 120.0;

        MvcResult authorizeResult = mockMvc.perform(post("/api/payments/authorize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String authId = authorizeResult.getResponse().getContentAsString();
        assertThat(authId).startsWith("AUTH-");

        MvcResult captureResult = mockMvc.perform(post("/api/payments/capture")
                        .param("authId", authId))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Captured payment: ")))
                .andReturn();

        String captureMessage = captureResult.getResponse().getContentAsString();
        String paymentId = captureMessage.replace("Captured payment: ", "").trim();
        assertThat(paymentId).startsWith("PAY-");

        RefundRequest refundRequest = new RefundRequest();
        refundRequest.paymentId = paymentId;
        refundRequest.amount = 65.0;

        mockMvc.perform(post("/api/payments/refund")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refundRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("Refunded $65.0 for payment: " + paymentId));
    }
}