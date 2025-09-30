package com.example.payments.model;

import java.util.UUID;

public class Payment {
    public String paymentId;
    public String buyerId;
    public String sellerId;
    public double amount;
    public PaymentStatus status;

    public Payment(String buyerId, String sellerId, double amount) {
        this.paymentId = "PAY-" + UUID.randomUUID();
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.amount = amount;
        this.status = PaymentStatus.AUTHORIZED;
    }
}
