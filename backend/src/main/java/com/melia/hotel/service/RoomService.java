package com.melia.hotel.service;

import com.melia.hotel.dto.response.RoomAvailabilityResponse;
import com.melia.hotel.entity.LoaiPhong;
import com.melia.hotel.entity.Phong;

import java.time.LocalDate;
import java.util.List;

public interface RoomService {
    List<RoomAvailabilityResponse> findAvailableRoomTypes(LocalDate checkIn, LocalDate checkOut, Integer guests);
    List<LoaiPhong> getAllRoomTypes();
    LoaiPhong getRoomTypeById(String maLoaiPhong);
    LoaiPhong createRoomType(LoaiPhong loaiPhong);
    Phong getRoomById(String maPhong);
    List<Phong> getAvailableRoomsByLoaiPhong(String maLoaiPhong, LocalDate checkIn, LocalDate checkOut);
}
