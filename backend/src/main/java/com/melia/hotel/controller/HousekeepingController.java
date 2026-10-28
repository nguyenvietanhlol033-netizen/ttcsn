package com.melia.hotel.controller;

import com.melia.hotel.dto.request.MinibarUsageRequest;
import com.melia.hotel.dto.request.RoomStatusUpdateRequest;
import com.melia.hotel.dto.response.ApiResponse;
import com.melia.hotel.entity.Phong;
import com.melia.hotel.service.HousekeepingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/housekeeping")
@Tag(name = "Housekeeping Management", description = "Các API phục vụ bộ phận Buồng phòng")
public class HousekeepingController {

    private final HousekeepingService housekeepingService;

    public HousekeepingController(HousekeepingService housekeepingService) {
        this.housekeepingService = housekeepingService;
    }

    @GetMapping("/rooms")
    @Operation(summary = "Xem danh sách phòng thực tế (có thể lọc theo trạng thái AVAILABLE, DIRTY, OCCUPIED, MAINTENANCE)")
    public ResponseEntity<ApiResponse<List<Phong>>> getRooms(@RequestParam(required = false) String status) {
        List<Phong> rooms = housekeepingService.getAllRooms(status);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách phòng thành công", rooms));
    }

    @PutMapping("/rooms/{id}/status")
    @Operation(summary = "Cập nhật trạng thái phòng (AVAILABLE, DIRTY, MAINTENANCE)")
    public ResponseEntity<ApiResponse<String>> updateRoomStatus(
            @PathVariable("id") String id, 
            @Valid @RequestBody RoomStatusUpdateRequest request) {
        housekeepingService.updateRoomStatus(id, request.getStatus());
        return ResponseEntity.ok(ApiResponse.success("Cập nhật trạng thái phòng thành công", request.getStatus()));
    }

    @PostMapping("/minibar-usage")
    @Operation(summary = "Báo cáo số lượng đồ uống / thức ăn minibar khách đã dùng khi dọn phòng")
    public ResponseEntity<ApiResponse<String>> reportMinibarUsage(@Valid @RequestBody MinibarUsageRequest request) {
        housekeepingService.reportMinibarUsage(request);
        return ResponseEntity.ok(ApiResponse.success("Ghi nhận tiêu thụ minibar thành công", request.getMaDatPhong()));
    }
}
