package com.melia.hotel.controller;

import com.melia.hotel.dto.response.ApiResponse;
import com.melia.hotel.dto.response.OccupancyReportResponse;
import com.melia.hotel.dto.response.RevenueReportResponse;
import com.melia.hotel.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/admin/reports")
@Tag(name = "Admin Reports", description = "Các API phục vụ Ban Quản lý Khách sạn xem báo cáo thống kê")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/occupancy")
    @Operation(summary = "Thống kê tỷ lệ lấp đầy phòng (Occupancy Rate) theo kỳ")
    public ResponseEntity<ApiResponse<OccupancyReportResponse>> getOccupancyReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        OccupancyReportResponse response = reportService.calculateOccupancyRate(fromDate, toDate);
        return ResponseEntity.ok(ApiResponse.success("Lấy báo cáo tỷ lệ lấp đầy thành công", response));
    }

    @GetMapping("/revenue")
    @Operation(summary = "Thống kê doanh thu tiền phòng, dịch vụ và chỉ số RevPAR")
    public ResponseEntity<ApiResponse<RevenueReportResponse>> getRevenueReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        RevenueReportResponse response = reportService.calculateRevenueReport(fromDate, toDate);
        return ResponseEntity.ok(ApiResponse.success("Lấy báo cáo doanh thu RevPAR thành công", response));
    }
}
