package com.example.payments.controller;

import com.example.payments.model.PaymentRequest;
import com.example.payments.model.RefundRequest;
import com.example.payments.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @Operation(
            summary = "Authorize a payment",
            description = "Authorizes a payment between a buyer and a seller before funds are captured.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Payment information required to authorize a transaction",
                    content = @Content(schema = @Schema(implementation = PaymentRequest.class))
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment successfully authorized"),
            @ApiResponse(responseCode = "400", description = "Invalid payment request supplied")
    })
    @PostMapping("/authorize")
    public String authorize(@Valid @RequestBody PaymentRequest request) {
        return service.authorize(request);
    }

    @Operation(
            summary = "Capture an authorized payment",
            description = "Captures funds for a previously authorized transaction."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment successfully captured"),
            @ApiResponse(responseCode = "404", description = "Authorization not found"),
            @ApiResponse(responseCode = "400", description = "Capture request is invalid")
    })
    @PostMapping("/capture")
    public String capture(
            @Parameter(description = "Authorization identifier returned from the authorize endpoint", example = "auth_123")
            @RequestParam String authId) {
        return service.capture(authId);
    }

    @Operation(
            summary = "Refund a payment",
            description = "Issues a refund for a captured payment.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Details of the payment and amount to refund",
                    content = @Content(schema = @Schema(implementation = RefundRequest.class))
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Refund successfully processed"),
            @ApiResponse(responseCode = "404", description = "Payment not found"),
            @ApiResponse(responseCode = "400", description = "Refund request is invalid")
    })
    @PostMapping("/refund")
    public String refund(@Valid @RequestBody RefundRequest request) {
        return service.refund(request);
    }
}
