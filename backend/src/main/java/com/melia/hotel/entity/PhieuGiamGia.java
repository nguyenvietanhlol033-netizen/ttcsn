package com.melia.hotel.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "phieu_giam_gia")
public class PhieuGiamGia {

    @Id
    @Column(name = "ma_phieu_gg", length = 30)
    private String maPhieuGg;

    @Column(name = "gia_tri_giam", nullable = false, precision = 12, scale = 2)
    private BigDecimal giaTriGiam;

    @Column(name = "loai_giam", length = 20)
    private String loaiGiam;

    @Column(name = "thoi_gian_bat_dau", nullable = false)
    private LocalDateTime thoiGianBatDau;

    @Column(name = "thoi_gian_ket_thuc", nullable = false)
    private LocalDateTime thoiGianKetThuc;

    @Column(name = "so_luong_phieu", nullable = false)
    private Integer soLuongPhieu;

    public PhieuGiamGia() {}

    public String getMaPhieuGg() { return maPhieuGg; }
    public void setMaPhieuGg(String maPhieuGg) { this.maPhieuGg = maPhieuGg; }
    public BigDecimal getGiaTriGiam() { return giaTriGiam; }
    public void setGiaTriGiam(BigDecimal giaTriGiam) { this.giaTriGiam = giaTriGiam; }
    public String getLoaiGiam() { return loaiGiam; }
    public void setLoaiGiam(String loaiGiam) { this.loaiGiam = loaiGiam; }
    public LocalDateTime getThoiGianBatDau() { return thoiGianBatDau; }
    public void setThoiGianBatDau(LocalDateTime thoiGianBatDau) { this.thoiGianBatDau = thoiGianBatDau; }
    public LocalDateTime getThoiGianKetThuc() { return thoiGianKetThuc; }
    public void setThoiGianKetThuc(LocalDateTime thoiGianKetThuc) { this.thoiGianKetThuc = thoiGianKetThuc; }
    public Integer getSoLuongPhieu() { return soLuongPhieu; }
    public void setSoLuongPhieu(Integer soLuongPhieu) { this.soLuongPhieu = soLuongPhieu; }
}
