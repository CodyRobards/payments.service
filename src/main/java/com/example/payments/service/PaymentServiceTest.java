// package com.example.payments.service;

// import com.example.payments.model.Payment;
// import com.example.payments.model.PaymentRequest;
// import com.example.payments.model.PaymentStatus;
// import com.example.payments.model.RefundRequest;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;

// import java.lang.reflect.Field;
// import java.util.Map;

// import static org.assertj.core.api.Assertions.assertThat;

// class PaymentServiceTest {

//     private PaymentService service;

//     @BeforeEach
//     void setUp() {
//         service = new PaymentService();
//     }

//     @Test
//     void authorizeShouldPersistAuthorizedPayment() {
//         PaymentRequest request = buildPaymentRequest(125.50);

//         String authId = service.authorize(request);

//         Map<String, Payment> store = getPaymentStore();
//         assertThat(authId).startsWith("AUTH-");
//         assertThat(store).hasSize(1);

//         Payment stored = store.values().iterator().next();
//         assertThat(stored.status).isEqualTo(PaymentStatus.AUTHORIZED);
//         assertThat(stored.amount).isEqualTo(request.amount);
//         assertThat(stored.buyerId).isEqualTo(request.buyerId);
//         assertThat(stored.sellerId).isEqualTo(request.sellerId);
//     }

//     @Test
//     void captureShouldUpdateStatusAndReturnMessage() {
//         PaymentRequest request = buildPaymentRequest(42.25);
//         String authId = service.authorize(request);
//         Payment payment = getPaymentStore().values().iterator().next();

//         String result = service.capture(authId);

//         assertThat(result).isEqualTo("Captured payment: " + payment.paymentId);
//         assertThat(payment.status).isEqualTo(PaymentStatus.CAPTURED);
//     }

//     @Test
//     void captureWithMissingAuthorizationReturnsHelpfulMessage() {
//         String result = service.capture("AUTH-missing");

//         assertThat(result).isEqualTo("Authorization not found.");
//     }

//     @Test
//     void captureWhenNotAuthorizedReturnsError() {
//         PaymentRequest request = buildPaymentRequest(51.00);
//         String authId = service.authorize(request);
//         service.capture(authId); // move to CAPTURED

//         String result = service.capture(authId);

//         assertThat(result).isEqualTo("Payment not in AUTHORIZED state.");
//     }

//     @Test
//     void refundShouldUpdateStatusAndReturnMessage() {
//         PaymentRequest request = buildPaymentRequest(75.0);
//         String authId = service.authorize(request);
//         Payment payment = getPaymentStore().values().iterator().next();
//         service.capture(authId);

//         RefundRequest refundRequest = new RefundRequest();
//         refundRequest.paymentId = payment.paymentId;
//         refundRequest.amount = 50.0;

//         String result = service.refund(refundRequest);

//         assertThat(result).isEqualTo("Refunded $" + refundRequest.amount + " for payment: " + payment.paymentId);
//         assertThat(payment.status).isEqualTo(PaymentStatus.REFUNDED);
//     }

//     @Test
//     void refundBeforeCaptureReturnsHelpfulMessage() {
//         PaymentRequest request = buildPaymentRequest(33.33);
//         service.authorize(request);
//         Payment payment = getPaymentStore().values().iterator().next();

//         RefundRequest refundRequest = new RefundRequest();
//         refundRequest.paymentId = payment.paymentId;
//         refundRequest.amount = 10.0;

//         String result = service.refund(refundRequest);

//         assertThat(result).isEqualTo("Refund only allowed after capture.");
//         assertThat(payment.status).isEqualTo(PaymentStatus.AUTHORIZED);
//     }

//     @Test
//     void refundWithUnknownPaymentReturnsMessage() {
//         RefundRequest refundRequest = new RefundRequest();
//         refundRequest.paymentId = "PAY-unknown";
//         refundRequest.amount = 12.0;

//         String result = service.refund(refundRequest);

//         assertThat(result).isEqualTo("Payment not found.");
//     }

//     private PaymentRequest buildPaymentRequest(double amount) {
//         PaymentRequest request = new PaymentRequest();
//         request.buyerId = "buyer-123";
//         request.sellerId = "seller-456";
//         request.amount = amount;
//         return request;
//     }

//     @SuppressWarnings("unchecked")
//     private Map<String, Payment> getPaymentStore() {
//         try {
//             Field field = PaymentService.class.getDeclaredField("paymentStore");
//             field.setAccessible(true);
//             return (Map<String, Payment>) field.get(service);
//         } catch (NoSuchFieldException | IllegalAccessException e) {
//             throw new IllegalStateException("Unable to access payment store", e);
//         }
//     }
// }