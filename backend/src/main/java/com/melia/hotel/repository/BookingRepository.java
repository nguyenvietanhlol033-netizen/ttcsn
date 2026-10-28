package com.melia.hotel.repository;

import com.melia.hotel.entity.DonDatPhong;
import com.melia.hotel.entity.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<DonDatPhong, String> {
    List<DonDatPhong> findByKhachHang(KhachHang khachHang);
    List<DonDatPhong> findByKhachHang_MaKhachHang(String maKhachHang);
    List<DonDatPhong> findByTrangThaiDon(String trangThaiDon);
    
    @Query("""
        SELECT COUNT(d) FROM DonDatPhong d 
        WHERE d.loaiPhong.maLoaiPhong = :maLoaiPhong 
        AND d.thoiGianNhanPhong < :checkOut 
        AND d.thoiGianTraPhong > :checkIn 
        AND (d.trangThaiDon IN ('CONFIRMED', 'CHECKED_IN') 
             OR (d.trangThaiDon = 'PENDING' AND d.ngayDat >= :holdDeadline))
    """)
    long countActiveOverlappingBookings(
            @Param("maLoaiPhong") String maLoaiPhong,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut,
            @Param("holdDeadline") LocalDateTime holdDeadline);

    @Query("""
        SELECT d FROM DonDatPhong d 
        WHERE d.maDatPhong = :keyword 
        OR d.khachHang.soCccd = :keyword 
        OR d.khachHang.soDienThoai = :keyword
    """)
    Optional<DonDatPhong> findBookingForReception(@Param("keyword") String keyword);

    @Query("""
        SELECT d FROM DonDatPhong d 
        WHERE d.thoiGianNhanPhong <= :endDate 
        AND d.thoiGianTraPhong >= :startDate
    """)
    List<DonDatPhong> findBookingsInRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("""
        SELECT d FROM DonDatPhong d 
        WHERE d.thoiGianNhanPhong <= :endDate 
        AND d.thoiGianTraPhong >= :startDate 
        AND d.trangThaiDon IN ('CONFIRMED', 'CHECKED_IN', 'CHECKED_OUT')
    """)
    List<DonDatPhong> findOverlappingActiveBookings(
            @Param("startDate") LocalDate startDate, 
            @Param("endDate") LocalDate endDate);
}
