package com.example.payments.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class RefundRequest {

    @NotBlank(message = "Payment ID is required")
    public String paymentId;

    @Positive(message = "Refund amount must be greater than zero")
    public double amount;
}
