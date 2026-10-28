package com.melia.hotel.repository;

import com.melia.hotel.entity.PhieuGiamGia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<PhieuGiamGia, String> {
    Optional<PhieuGiamGia> findByMaPhieuGgIgnoreCase(String maPhieuGg);
}