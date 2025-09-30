package com.example.payments.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@Schema(description = "Payload describing a payment to authorize")
public class PaymentRequest {

    @Schema(description = "Unique identifier of the buyer initiating the payment", example = "buyer_001")
    @NotBlank(message = "Buyer ID is required")
    public String buyerId;

    @Schema(description = "Unique identifier of the seller receiving the payment", example = "seller_987")
    @NotBlank(message = "Seller ID is required")
    public String sellerId;

    @Schema(description = "Amount of the payment in USD", example = "49.99")
    @Positive(message = "Amount must be greater than zero")
    public double amount;
}
