package com.melia.hotel.controller;

import com.melia.hotel.dto.request.CouponApplyRequest;
import com.melia.hotel.dto.response.ApiResponse;
import com.melia.hotel.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/coupons")
@Tag(name = "Coupon Management", description = "Các API kiểm tra và áp dụng mã giảm giá ưu đãi")
public class CouponController {

    private final BookingService bookingService;

    public CouponController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/apply")
    @Operation(summary = "Kiểm tra tính hợp lệ và áp dụng mã giảm giá voucher")
    public ResponseEntity<ApiResponse<BigDecimal>> applyCoupon(@Valid @RequestBody CouponApplyRequest request) {
        BigDecimal discount = bookingService.applyCoupon(request);
        return ResponseEntity.ok(ApiResponse.success("Áp dụng mã giảm giá thành công", discount));
    }
}
