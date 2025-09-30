package com.example.payments.web;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Objects;

@RestController
public class AppStatusController implements ErrorController {

    @GetMapping("/")
    public ResponseEntity<Map<String, String>> root() {
        Map<String, String> payload = Map.of(
                "status", "UP",
                "message", "Payments Service is running"
        );
        return ResponseEntity.ok(payload);
    }

    @Hidden
    @RequestMapping("/error")
    public ResponseEntity<ProblemDetail> handleError(HttpServletRequest request) {
        HttpStatus status = resolveStatus(request);
        String detail = Objects.toString(
                request.getAttribute(RequestDispatcher.ERROR_MESSAGE),
                status.getReasonPhrase()
        );

        ProblemDetail problemDetail = ProblemDetail.forStatus(status);
        problemDetail.setTitle("Unexpected error");
        problemDetail.setDetail(detail);

        Object path = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        if (path != null) {
            problemDetail.setProperty("path", path);
        }

        return ResponseEntity.status(status).body(problemDetail);
    }

    private HttpStatus resolveStatus(HttpServletRequest request) {
        Object statusCode = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (statusCode instanceof Integer code) {
            HttpStatus resolved = HttpStatus.resolve(code);
            if (resolved != null) {
                return resolved;
            }
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
