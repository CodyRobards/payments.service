package com.example.payments.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class PaymentRequest {

    @NotBlank(message = "Buyer ID is required")
    public String buyerId;

    @NotBlank(message = "Seller ID is required")
    public String sellerId;

    @Positive(message = "Amount must be greater than zero")
    public double amount;
}
