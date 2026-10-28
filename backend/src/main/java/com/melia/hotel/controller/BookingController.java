package com.melia.hotel.controller;

import com.melia.hotel.dto.request.BookingRequest;
import com.melia.hotel.dto.request.CouponApplyRequest;
import com.melia.hotel.dto.response.ApiResponse;
import com.melia.hotel.dto.response.BookingDetailResponse;
import com.melia.hotel.dto.response.BookingResponse;
import com.melia.hotel.security.UserPrincipal;
import com.melia.hotel.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
@Tag(name = "Booking Management", description = "Các API khởi tạo đơn đặt phòng và áp dụng khuyến mại")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @Operation(summary = "Khởi tạo đơn đặt phòng mới (Trạng thái PENDING, giữ chỗ 15 phút)")
    public ResponseEntity<ApiResponse<BookingDetailResponse>> createBooking(
            @Valid @RequestBody BookingRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        BookingDetailResponse response = bookingService.createBooking(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Khởi tạo đơn đặt phòng thành công", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Xem thông tin chi tiết một đơn đặt phòng (Có kiểm tra quyền sở hữu)")
    public ResponseEntity<ApiResponse<BookingResponse>> getBookingById(
            @PathVariable("id") String id,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        return ResponseEntity.ok(ApiResponse.success("Lấy thông tin đơn đặt phòng thành công",
                bookingService.getBookingById(id, currentUser)));
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Xem danh sách lịch sử đặt phòng của một khách hàng")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getCustomerBookings(
            @PathVariable("customerId") String customerId,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        return ResponseEntity.ok(ApiResponse.success("Lấy lịch sử đặt phòng thành công",
                bookingService.getBookingsByCustomer(customerId, currentUser)));
    }

    @PostMapping("/coupons/apply")
    @Operation(summary = "Kiểm tra tính hợp lệ và tính số tiền giảm của mã voucher")
    public ResponseEntity<ApiResponse<BigDecimal>> applyCoupon(@Valid @RequestBody CouponApplyRequest request) {
        BigDecimal discount = bookingService.applyCoupon(request);
        return ResponseEntity.ok(ApiResponse.success("Áp dụng mã giảm giá thành công", discount));
    }
}
