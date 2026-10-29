package com.melia.hotel.controller;

import com.melia.hotel.dto.request.CheckInRequest;
import com.melia.hotel.dto.request.CheckOutRequest;
import com.melia.hotel.dto.request.ConfirmAndAssignRoomRequest;
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

import java.util.List;

@RestController
@RequestMapping("/api/v1/reception")
@Tag(name = "Reception Management", description = "Các API phục vụ nghiệp vụ quầy Lễ tân (Tiếp nhận đơn, Gán phòng, Xác nhận, Check-in, Check-out)")
public class ReceptionController {

    private final ReceptionService receptionService;

    public ReceptionController(ReceptionService receptionService) {
        this.receptionService = receptionService;
    }

    @GetMapping("/bookings")
    @Operation(summary = "Xem danh sách các đơn đặt phòng tiếp nhận tại quầy (có thể lọc theo trạng thái PENDING, CONFIRMED, CHECKED_IN, CHECKED_OUT và tìm kiếm)")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getAllBookings(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword) {
        List<BookingResponse> list = receptionService.getAllBookings(status, keyword);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách đơn đặt phòng thành công", list));
    }

    @GetMapping("/bookings/{id}")
    @Operation(summary = "Tra cứu thông tin đơn đặt phòng bằng Booking ID hoặc số CCCD của khách")
    public ResponseEntity<ApiResponse<BookingResponse>> getBookingForReception(@PathVariable("id") String id) {
        BookingResponse response = receptionService.getBookingForReception(id);
        return ResponseEntity.ok(ApiResponse.success("Tra cứu đơn đặt phòng thành công", response));
    }

    @PostMapping("/confirm")
    @Operation(summary = "Lễ tân tiếp nhận đơn PENDING, kiểm tra thông tin, gán phòng cụ thể còn trống và bấm Xác nhận đặt phòng (CONFIRMED)")
    public ResponseEntity<ApiResponse<BookingResponse>> confirmAndAssignRoom(
            @Valid @RequestBody ConfirmAndAssignRoomRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        String maNhanVien = currentUser != null ? currentUser.getId() : request.getMaNhanVien();
        BookingResponse response = receptionService.confirmAndAssignRoom(request, maNhanVien);
        return ResponseEntity.ok(ApiResponse.success("Xác nhận đặt phòng và gán phòng thành công", response));
    }

    @PostMapping("/check-in")
    @Operation(summary = "Làm thủ tục Check-in khi khách đến khách sạn để vào ở (chuyển phòng sang OCCUPIED, đơn sang CHECKED_IN)")
    public ResponseEntity<ApiResponse<String>> checkIn(
            @Valid @RequestBody CheckInRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        String maNhanVien = currentUser != null ? currentUser.getId() : request.getMaNhanVien();
        receptionService.processCheckIn(request.getMaDatPhong(), request.getMaPhongCuThe(), maNhanVien);
        return ResponseEntity.ok(ApiResponse.success("Thủ tục Check-in thành công! Khách đã nhận phòng và vào ở.", request.getMaPhongCuThe()));
    }

    @PostMapping("/check-out")
    @Operation(summary = "Làm thủ tục Check-out, tổng hợp tiền minibar, trừ tiền trả trước và quyết toán hóa đơn")
    public ResponseEntity<ApiResponse<InvoiceResponse>> checkOut(@Valid @RequestBody CheckOutRequest request) {
        InvoiceResponse response = receptionService.processCheckOut(
                request.getMaDatPhong(), request.getPhuThuPhatSinh(), request.getPhuongThucThanhToan());
        return ResponseEntity.ok(ApiResponse.success("Thủ tục Check-out và thanh toán hoàn tất", response));
    }
}
