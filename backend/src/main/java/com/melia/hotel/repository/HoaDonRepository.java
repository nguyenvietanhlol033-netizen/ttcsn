package com.melia.hotel.repository;

import com.melia.hotel.entity.DonDatPhong;
import com.melia.hotel.entity.HoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface HoaDonRepository extends JpaRepository<HoaDon, String> {
    Optional<HoaDon> findByDonDatPhong(DonDatPhong donDatPhong);
    Optional<HoaDon> findByDonDatPhong_MaDatPhong(String maDatPhong);
    List<HoaDon> findByNgayLapHoaDonBetweenAndTrangThaiThanhToan(
            LocalDateTime start, 
            LocalDateTime end, 
            String trangThaiThanhToan
    );
}
