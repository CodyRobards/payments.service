package com.example.payments.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@Schema(description = "Payload describing a refund request")
public class RefundRequest {

    @Schema(description = "Identifier of the payment to refund", example = "pay_456")
    @NotBlank(message = "Payment ID is required")
    public String paymentId;

    @Schema(description = "Amount to refund in USD", example = "25.00")
    @Positive(message = "Refund amount must be greater than zero")
    public double amount;
}
