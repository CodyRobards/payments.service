package com.example.payments.service;

import com.example.payments.model.PaymentRequest;
import com.example.payments.model.RefundRequest;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PaymentService {

    public String authorize(PaymentRequest request) {
        return "AUTH-" + UUID.randomUUID();
    }

    public String capture(String authId) {
        return "CAPTURED for " + authId;
    }

    public String refund(RefundRequest request) {
        return "REFUND issued for " + request.paymentId + " with amount $" + request.amount;
    }
}
