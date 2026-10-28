package com.melia.hotel.controller;

import com.melia.hotel.dto.response.ApiResponse;
import com.melia.hotel.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@Tag(name = "Payment Management", description = "Các API thanh toán VNPay và tiếp nhận IPN callback")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create-url")
    @Operation(summary = "Tạo URL thanh toán chuyển hướng qua cổng VNPay Sandbox")
    public ResponseEntity<ApiResponse<String>> createPaymentUrl(
            @RequestParam("bookingId") String bookingId,
            HttpServletRequest request) {

        String paymentUrl = paymentService.createVNPayPaymentUrl(bookingId, request);
        return ResponseEntity.ok(ApiResponse.success("Tạo URL thanh toán VNPay thành công", paymentUrl));
    }

    @GetMapping("/vnpay-callback")
    @Operation(summary = "Tiếp nhận dữ liệu phản hồi từ VNPay sau khi khách thực hiện thanh toán")
    public ResponseEntity<ApiResponse<String>> handleVNPayCallback(@RequestParam Map<String, String> allParams) {
        String message = paymentService.handleVNPayCallback(allParams);
        String responseCode = allParams.get("vnp_ResponseCode");

        if ("00".equals(responseCode)) {
            return ResponseEntity.ok(ApiResponse.success(message, "PAID"));
        } else {
            return ResponseEntity.ok(ApiResponse.error(message, "FAILED"));
        }
    }
}
