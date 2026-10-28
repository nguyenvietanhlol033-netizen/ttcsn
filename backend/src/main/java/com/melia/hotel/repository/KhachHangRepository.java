package com.melia.hotel.repository;

import com.melia.hotel.entity.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KhachHangRepository extends JpaRepository<KhachHang, String> {
    Optional<KhachHang> findBySoDienThoai(String soDienThoai);
    Optional<KhachHang> findByEmail(String email);
    Optional<KhachHang> findBySoCccd(String soCccd);
    boolean existsByEmail(String email);
    boolean existsBySoDienThoai(String soDienThoai);
    boolean existsBySoCccd(String soCccd);
}