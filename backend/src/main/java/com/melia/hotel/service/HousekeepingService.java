package com.melia.hotel.service;

import com.melia.hotel.dto.request.MinibarUsageRequest;
import com.melia.hotel.entity.Phong;

import java.util.List;

public interface HousekeepingService {
    void updateRoomStatus(String maPhong, String status);
    void reportMinibarUsage(MinibarUsageRequest request);
    List<Phong> getAllRooms(String status);
}
