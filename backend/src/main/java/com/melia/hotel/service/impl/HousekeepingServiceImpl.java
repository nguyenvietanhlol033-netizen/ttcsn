package com.melia.hotel.service.impl;

import com.melia.hotel.dto.request.MinibarUsageRequest;
import com.melia.hotel.entity.*;
import com.melia.hotel.exception.BusinessException;
import com.melia.hotel.repository.*;
import com.melia.hotel.service.HousekeepingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class HousekeepingServiceImpl implements HousekeepingService {

    private static final Logger log = LoggerFactory.getLogger(HousekeepingServiceImpl.class);

    private final RoomRepository roomRepo;
    private final BookingRepository bookingRepo;
    private final DichVuRepository dichVuRepo;
    private final ChiTietDichVuRepository chiTietDichVuRepo;

    public HousekeepingServiceImpl(RoomRepository roomRepo,
                                   BookingRepository bookingRepo,
                                   DichVuRepository dichVuRepo,
                                   ChiTietDichVuRepository chiTietDichVuRepo) {
        this.roomRepo = roomRepo;
        this.bookingRepo = bookingRepo;
        this.dichVuRepo = dichVuRepo;
        this.chiTietDichVuRepo = chiTietDichVuRepo;
    }

    @Override
    @Transactional
    public void updateRoomStatus(String maPhong, String status) {
        Phong room = roomRepo.findById(maPhong)
                .orElseThrow(() -> new BusinessException("Phòng không tồn tại: " + maPhong));

        String upperStatus = status.toUpperCase();
        if (!List.of("AVAILABLE", "OCCUPIED", "DIRTY", "MAINTENANCE").contains(upperStatus)) {
            throw new BusinessException("Trạng thái phòng không hợp lệ: " + status + ". Chỉ chấp nhận: AVAILABLE, OCCUPIED, DIRTY, MAINTENANCE");
        }

        room.setTrangThaiPhong(upperStatus);
        roomRepo.save(room);
        log.info("Nhân viên buồng phòng cập nhật phòng {} sang trạng thái {}", maPhong, upperStatus);
    }

    @Override
    @Transactional
    public void reportMinibarUsage(MinibarUsageRequest request) {
        DonDatPhong booking = bookingRepo.findById(request.getMaDatPhong())
                .orElseThrow(() -> new BusinessException("Đơn đặt phòng không tồn tại: " + request.getMaDatPhong()));

        DichVu dichVu = dichVuRepo.findById(request.getMaDichVu())
                .orElseThrow(() -> new BusinessException("Dịch vụ minibar không tồn tại: " + request.getMaDichVu()));

        if (request.getSoLuong() == null || request.getSoLuong() <= 0) {
            throw new BusinessException("Số lượng sử dụng phải lớn hơn 0!");
        }

        BigDecimal thanhTien = dichVu.getGiaDv().multiply(BigDecimal.valueOf(request.getSoLuong()));

        ChiTietDichVu ctdv = ChiTietDichVu.builder()
                .donDatPhong(booking)
                .dichVu(dichVu)
                .soLuong(request.getSoLuong())
                .thoiGianSd(LocalDateTime.now())
                .thanhTien(thanhTien)
                .build();

        chiTietDichVuRepo.save(ctdv);
        log.info("Ghi nhận phát sinh minibar cho đơn {}: {} x {} = {}",
                request.getMaDatPhong(), dichVu.getTenDv(), request.getSoLuong(), thanhTien);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Phong> getAllRooms(String status) {
        if (status != null && !status.isBlank()) {
            return roomRepo.findByTrangThaiPhong(status.toUpperCase());
        }
        return roomRepo.findAll();
    }
}
