package com.example.payments.controller;

import com.example.payments.model.PaymentRequest;
import com.example.payments.model.RefundRequest;
import com.example.payments.service.PaymentService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @PostMapping("/authorize")
    public String authorize(@RequestBody PaymentRequest request) {
        return service.authorize(request);
    }

    @PostMapping("/capture")
    public String capture(@RequestParam String authId) {
        return service.capture(authId);
    }

    @PostMapping("/refund")
    public String refund(@RequestBody RefundRequest request) {
        return service.refund(request);
    }
}
