package com.melia.hotel.repository;

import com.melia.hotel.entity.NhanVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NhanVienRepository extends JpaRepository<NhanVien, String> {
    Optional<NhanVien> findByTenDangNhap(String tenDangNhap);
    List<NhanVien> findByQuyenHan(String quyenHan);
    boolean existsByTenDangNhap(String tenDangNhap);
}