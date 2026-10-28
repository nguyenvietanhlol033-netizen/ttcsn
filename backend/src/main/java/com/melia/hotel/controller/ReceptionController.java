package com.melia.hotel.controller;

import com.melia.hotel.dto.request.CheckInRequest;
import com.melia.hotel.dto.request.CheckOutRequest;
import com.melia.hotel.dto.response.ApiResponse;
import com.melia.hotel.dto.response.BookingResponse;
import com.melia.hotel.dto.response.InvoiceResponse;
import com.melia.hotel.security.UserPrincipal;
import com.melia.hotel.service.ReceptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reception")
@Tag(name = "Reception Management", description = "Các API phục vụ nghiệp vụ quầy Lễ tân (Check-in, Check-out, tra cứu đơn)")
public class ReceptionController {

    private final ReceptionService receptionService;

    public ReceptionController(ReceptionService receptionService) {
        this.receptionService = receptionService;
    }

    @GetMapping("/bookings/{id}")
    @Operation(summary = "Tra cứu thông tin đơn đặt phòng bằng Booking ID hoặc số CCCD của khách")
    public ResponseEntity<ApiResponse<BookingResponse>> getBookingForReception(@PathVariable("id") String id) {
        BookingResponse response = receptionService.getBookingForReception(id);
        return ResponseEntity.ok(ApiResponse.success("Tra cứu đơn đặt phòng thành công", response));
    }

    @PostMapping("/check-in")
    @Operation(summary = "Làm thủ tục Check-in và gán phòng vật lý sạch cho khách (Lấy ID nhân viên từ token)")
    public ResponseEntity<ApiResponse<String>> checkIn(
            @Valid @RequestBody CheckInRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        String maNhanVien = currentUser != null ? currentUser.getId() : request.getMaNhanVien();
        receptionService.processCheckIn(request.getMaDatPhong(), request.getMaPhongCuThe(), maNhanVien);
        return ResponseEntity.ok(ApiResponse.success("Thủ tục Check-in và gán phòng thành công", request.getMaPhongCuThe()));
    }

    @PostMapping("/check-out")
    @Operation(summary = "Làm thủ tục Check-out, tổng hợp tiền minibar, trừ tiền trả trước và quyết toán hóa đơn")
    public ResponseEntity<ApiResponse<InvoiceResponse>> checkOut(@Valid @RequestBody CheckOutRequest request) {
        InvoiceResponse response = receptionService.processCheckOut(
                request.getMaDatPhong(), request.getPhuThuPhatSinh(), request.getPhuongThucThanhToan());
        return ResponseEntity.ok(ApiResponse.success("Thủ tục Check-out và thanh toán hoàn tất", response));
    }
}
