package com.melia.hotel.controller;

import com.melia.hotel.dto.response.ApiResponse;
import com.melia.hotel.dto.response.RoomAvailabilityResponse;
import com.melia.hotel.entity.LoaiPhong;
import com.melia.hotel.entity.Phong;
import com.melia.hotel.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/rooms")
@Tag(name = "Room Management", description = "Các API tra cứu và quản lý thông tin phòng trống")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping("/available")
    @Operation(summary = "Tìm kiếm loại phòng còn trống theo ngày nhận, trả và số khách")
    public ResponseEntity<ApiResponse<List<RoomAvailabilityResponse>>> getAvailableRooms(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            @RequestParam(required = false, defaultValue = "2") Integer guests) {

        List<RoomAvailabilityResponse> data = roomService.findAvailableRoomTypes(checkIn, checkOut, guests);
        return ResponseEntity.ok(ApiResponse.success("Tra cứu danh sách phòng trống thành công", data));
    }

    @GetMapping("/types")
    @Operation(summary = "Xem danh sách tất cả các hạng phòng của khách sạn Meliá Hà Nội")
    public ResponseEntity<ApiResponse<List<LoaiPhong>>> getAllRoomTypes() {
        return ResponseEntity.ok(ApiResponse.success("Lấy danh mục loại phòng thành công", roomService.getAllRoomTypes()));
    }

    @GetMapping("/types/{id}")
    @Operation(summary = "Xem thông tin chi tiết một hạng phòng")
    public ResponseEntity<ApiResponse<LoaiPhong>> getRoomTypeById(@PathVariable("id") String id) {
        return ResponseEntity.ok(ApiResponse.success("Lấy thông tin loại phòng thành công", roomService.getRoomTypeById(id)));
    }

    @PostMapping("/types")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Thêm mới một hạng phòng (Chỉ dành cho Quản trị viên)")
    public ResponseEntity<ApiResponse<LoaiPhong>> createRoomType(@RequestBody LoaiPhong loaiPhong) {
        LoaiPhong created = roomService.createRoomType(loaiPhong);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Thêm mới loại phòng thành công", created));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Xem thông tin chi tiết một phòng vật lý cụ thể")
    public ResponseEntity<ApiResponse<Phong>> getRoomDetails(@PathVariable("id") String id) {
        return ResponseEntity.ok(ApiResponse.success("Lấy thông tin phòng thành công", roomService.getRoomById(id)));
    }
}
