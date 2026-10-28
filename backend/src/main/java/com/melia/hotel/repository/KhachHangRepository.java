package com.melia.hotel.repository;

import com.melia.hotel.entity.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KhachHangRepository extends JpaRepository<KhachHang, String> {
    Optional<KhachHang> findByTenDangNhap(String tenDangNhap);
    Optional<KhachHang> findBySoDienThoai(String soDienThoai);
    Optional<KhachHang> findByEmail(String email);
    Optional<KhachHang> findBySoCccd(String soCccd);
    boolean existsByTenDangNhap(String tenDangNhap);
    boolean existsByEmail(String email);
    boolean existsBySoDienThoai(String soDienThoai);
    boolean existsBySoCccd(String soCccd);
    List<KhachHang> findByHoTenIgnoreCase(String hoTen);
    List<KhachHang> findByEmailStartingWith(String prefix);
}
