package com.melia.hotel.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "danh_gia")
public class DanhGia {

    @Id
    @Column(name = "ma_danh_gia", length = 30)
    private String maDanhGia;

    @Column(name = "so_sao_danh_gia", nullable = false)
    private Integer soSaoDanhGia;

    @Column(name = "noi_dung_nx", columnDefinition = "TEXT")
    private String noiDungNx;

    @Column(name = "thoi_gian_danh_gia")
    private LocalDateTime thoiGianDanhGia;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_dat_phong", nullable = false, unique = true)
    private DonDatPhong donDatPhong;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_khach_hang", nullable = false)
    private KhachHang khachHang;

    public DanhGia() {}

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String maDanhGia;
        private Integer soSaoDanhGia;
        private String noiDungNx;
        private LocalDateTime thoiGianDanhGia;
        private DonDatPhong donDatPhong;
        private KhachHang khachHang;

        public Builder maDanhGia(String maDanhGia) { this.maDanhGia = maDanhGia; return this; }
        public Builder soSaoDanhGia(Integer soSaoDanhGia) { this.soSaoDanhGia = soSaoDanhGia; return this; }
        public Builder noiDungNx(String noiDungNx) { this.noiDungNx = noiDungNx; return this; }
        public Builder thoiGianDanhGia(LocalDateTime thoiGianDanhGia) { this.thoiGianDanhGia = thoiGianDanhGia; return this; }
        public Builder donDatPhong(DonDatPhong donDatPhong) { this.donDatPhong = donDatPhong; return this; }
        public Builder khachHang(KhachHang khachHang) { this.khachHang = khachHang; return this; }
        public DanhGia build() {
            DanhGia d = new DanhGia();
            d.maDanhGia = this.maDanhGia;
            d.soSaoDanhGia = this.soSaoDanhGia;
            d.noiDungNx = this.noiDungNx;
            d.thoiGianDanhGia = this.thoiGianDanhGia;
            d.donDatPhong = this.donDatPhong;
            d.khachHang = this.khachHang;
            return d;
        }
    }

    public String getMaDanhGia() { return maDanhGia; }
    public void setMaDanhGia(String maDanhGia) { this.maDanhGia = maDanhGia; }
    public Integer getSoSaoDanhGia() { return soSaoDanhGia; }
    public void setSoSaoDanhGia(Integer soSaoDanhGia) { this.soSaoDanhGia = soSaoDanhGia; }
    public String getNoiDungNx() { return noiDungNx; }
    public void setNoiDungNx(String noiDungNx) { this.noiDungNx = noiDungNx; }
    public LocalDateTime getThoiGianDanhGia() { return thoiGianDanhGia; }
    public void setThoiGianDanhGia(LocalDateTime thoiGianDanhGia) { this.thoiGianDanhGia = thoiGianDanhGia; }
    public DonDatPhong getDonDatPhong() { return donDatPhong; }
    public void setDonDatPhong(DonDatPhong donDatPhong) { this.donDatPhong = donDatPhong; }
    public KhachHang getKhachHang() { return khachHang; }
    public void setKhachHang(KhachHang khachHang) { this.khachHang = khachHang; }
}
