package com.melia.hotel.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "chi_tiet_dich_vu")
public class ChiTietDichVu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_dat_phong", nullable = false)
    private DonDatPhong donDatPhong;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ma_dich_vu", nullable = false)
    private DichVu dichVu;

    @Column(name = "so_luong", nullable = false)
    private Integer soLuong;

    @Column(name = "thoi_gian_sd")
    private LocalDateTime thoiGianSd;

    @Column(name = "thanh_tien", nullable = false, precision = 12, scale = 2)
    private BigDecimal thanhTien;

    public ChiTietDichVu() {}

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private DonDatPhong donDatPhong;
        private DichVu dichVu;
        private Integer soLuong;
        private LocalDateTime thoiGianSd;
        private BigDecimal thanhTien;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder donDatPhong(DonDatPhong donDatPhong) { this.donDatPhong = donDatPhong; return this; }
        public Builder dichVu(DichVu dichVu) { this.dichVu = dichVu; return this; }
        public Builder soLuong(Integer soLuong) { this.soLuong = soLuong; return this; }
        public Builder thoiGianSd(LocalDateTime thoiGianSd) { this.thoiGianSd = thoiGianSd; return this; }
        public Builder thanhTien(BigDecimal thanhTien) { this.thanhTien = thanhTien; return this; }
        public ChiTietDichVu build() {
            ChiTietDichVu c = new ChiTietDichVu();
            c.id = this.id;
            c.donDatPhong = this.donDatPhong;
            c.dichVu = this.dichVu;
            c.soLuong = this.soLuong;
            c.thoiGianSd = this.thoiGianSd;
            c.thanhTien = this.thanhTien;
            return c;
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public DonDatPhong getDonDatPhong() { return donDatPhong; }
    public void setDonDatPhong(DonDatPhong donDatPhong) { this.donDatPhong = donDatPhong; }
    public DichVu getDichVu() { return dichVu; }
    public void setDichVu(DichVu dichVu) { this.dichVu = dichVu; }
    public Integer getSoLuong() { return soLuong; }
    public void setSoLuong(Integer soLuong) { this.soLuong = soLuong; }
    public LocalDateTime getThoiGianSd() { return thoiGianSd; }
    public void setThoiGianSd(LocalDateTime thoiGianSd) { this.thoiGianSd = thoiGianSd; }
    public BigDecimal getThanhTien() { return thanhTien; }
    public void setThanhTien(BigDecimal thanhTien) { this.thanhTien = thanhTien; }
}
