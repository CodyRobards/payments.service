package com.example.payments.service;

import com.example.payments.model.*;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PaymentService {

    private final Map<String, Payment> paymentStore = new ConcurrentHashMap<>();
    private final Map<String, String> authMap = new ConcurrentHashMap<>(); // authId → paymentId

    public String authorize(PaymentRequest request) {
        Payment payment = new Payment(request.buyerId, request.sellerId, request.amount);
        String authId = "AUTH-" + UUID.randomUUID();
        authMap.put(authId, payment.paymentId);
        paymentStore.put(payment.paymentId, payment);
        return authId;
    }

    public String capture(String authId) {
        String paymentId = authMap.get(authId);
        if (paymentId == null || !paymentStore.containsKey(paymentId)) {
            return "Authorization not found.";
        }

        Payment payment = paymentStore.get(paymentId);
        if (payment.status != PaymentStatus.AUTHORIZED) {
            return "Payment not in AUTHORIZED state.";
        }

        payment.status = PaymentStatus.CAPTURED;
        return "Captured payment: " + paymentId;
    }

    public String refund(RefundRequest request) {
        Payment payment = paymentStore.get(request.paymentId);
        if (payment == null) {
            return "Payment not found.";
        }

        if (payment.status != PaymentStatus.CAPTURED) {
            return "Refund only allowed after capture.";
        }

        payment.status = PaymentStatus.REFUNDED;
        return "Refunded $" + request.amount + " for payment: " + request.paymentId;
    }
}
