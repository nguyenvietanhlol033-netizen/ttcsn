package com.melia.hotel.repository;

import com.melia.hotel.entity.ChiTietDichVu;
import com.melia.hotel.entity.DonDatPhong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ChiTietDichVuRepository extends JpaRepository<ChiTietDichVu, Long> {
    List<ChiTietDichVu> findByDonDatPhong(DonDatPhong donDatPhong);

    @Query("SELECT COALESCE(SUM(c.thanhTien), 0) FROM ChiTietDichVu c WHERE c.donDatPhong.maDatPhong = :maDatPhong")
    BigDecimal sumThanhTienByDonDatPhong(@Param("maDatPhong") String maDatPhong);
}
