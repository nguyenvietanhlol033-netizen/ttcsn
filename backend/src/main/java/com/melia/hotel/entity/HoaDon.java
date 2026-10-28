package com.melia.hotel.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "hoa_don")
public class HoaDon {

    @Id
    @Column(name = "ma_hoa_don", length = 30)
    private String maHoaDon;

    @Column(name = "ngay_lap_hoa_don")
    private LocalDateTime ngayLapHoaDon;

    @Column(name = "tien_phong", nullable = false, precision = 14, scale = 2)
    private BigDecimal tienPhong;

    @Column(name = "tien_dich_vu", precision = 14, scale = 2)
    private BigDecimal tienDichVu = BigDecimal.ZERO;

    @Column(name = "phu_thu", precision = 12, scale = 2)
    private BigDecimal phuThu = BigDecimal.ZERO;

    @Column(name = "giam_gia", precision = 12, scale = 2)
    private BigDecimal giamGia = BigDecimal.ZERO;

    @Column(name = "tong_tien_thanh_toan", nullable = false, precision = 14, scale = 2)
    private BigDecimal tongTienThanhToan;

    @Column(name = "da_thanh_toan", precision = 14, scale = 2)
    private BigDecimal daThanhToan = BigDecimal.ZERO;

    @Column(name = "phuong_thuc_thanh_toan", length = 30)
    private String phuongThucThanhToan;

    @Column(name = "trang_thai_thanh_toan", nullable = false, length = 30)
    private String trangThaiThanhToan;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_dat_phong", nullable = false, unique = true)
    private DonDatPhong donDatPhong;

    public HoaDon() {}

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String maHoaDon;
        private LocalDateTime ngayLapHoaDon;
        private BigDecimal tienPhong;
        private BigDecimal tienDichVu;
        private BigDecimal phuThu;
        private BigDecimal giamGia;
        private BigDecimal tongTienThanhToan;
        private BigDecimal daThanhToan;
        private String phuongThucThanhToan;
        private String trangThaiThanhToan;
        private DonDatPhong donDatPhong;

        public Builder maHoaDon(String maHoaDon) { this.maHoaDon = maHoaDon; return this; }
        public Builder ngayLapHoaDon(LocalDateTime ngayLapHoaDon) { this.ngayLapHoaDon = ngayLapHoaDon; return this; }
        public Builder tienPhong(BigDecimal tienPhong) { this.tienPhong = tienPhong; return this; }
        public Builder tienDichVu(BigDecimal tienDichVu) { this.tienDichVu = tienDichVu; return this; }
        public Builder phuThu(BigDecimal phuThu) { this.phuThu = phuThu; return this; }
        public Builder giamGia(BigDecimal giamGia) { this.giamGia = giamGia; return this; }
        public Builder tongTienThanhToan(BigDecimal tongTienThanhToan) { this.tongTienThanhToan = tongTienThanhToan; return this; }
        public Builder daThanhToan(BigDecimal daThanhToan) { this.daThanhToan = daThanhToan; return this; }
        public Builder phuongThucThanhToan(String phuongThucThanhToan) { this.phuongThucThanhToan = phuongThucThanhToan; return this; }
        public Builder trangThaiThanhToan(String trangThaiThanhToan) { this.trangThaiThanhToan = trangThaiThanhToan; return this; }
        public Builder donDatPhong(DonDatPhong donDatPhong) { this.donDatPhong = donDatPhong; return this; }
        public HoaDon build() {
            HoaDon h = new HoaDon();
            h.maHoaDon = this.maHoaDon;
            h.ngayLapHoaDon = this.ngayLapHoaDon;
            h.tienPhong = this.tienPhong;
            h.tienDichVu = this.tienDichVu != null ? this.tienDichVu : BigDecimal.ZERO;
            h.phuThu = this.phuThu != null ? this.phuThu : BigDecimal.ZERO;
            h.giamGia = this.giamGia != null ? this.giamGia : BigDecimal.ZERO;
            h.tongTienThanhToan = this.tongTienThanhToan;
            h.daThanhToan = this.daThanhToan != null ? this.daThanhToan : BigDecimal.ZERO;
            h.phuongThucThanhToan = this.phuongThucThanhToan;
            h.trangThaiThanhToan = this.trangThaiThanhToan;
            h.donDatPhong = this.donDatPhong;
            return h;
        }
    }

    public String getMaHoaDon() { return maHoaDon; }
    public void setMaHoaDon(String maHoaDon) { this.maHoaDon = maHoaDon; }
    public LocalDateTime getNgayLapHoaDon() { return ngayLapHoaDon; }
    public void setNgayLapHoaDon(LocalDateTime ngayLapHoaDon) { this.ngayLapHoaDon = ngayLapHoaDon; }
    public BigDecimal getTienPhong() { return tienPhong; }
    public void setTienPhong(BigDecimal tienPhong) { this.tienPhong = tienPhong; }
    public BigDecimal getTienDichVu() { return tienDichVu; }
    public void setTienDichVu(BigDecimal tienDichVu) { this.tienDichVu = tienDichVu; }
    public BigDecimal getPhuThu() { return phuThu; }
    public void setPhuThu(BigDecimal phuThu) { this.phuThu = phuThu; }
    public BigDecimal getGiamGia() { return giamGia; }
    public void setGiamGia(BigDecimal giamGia) { this.giamGia = giamGia; }
    public BigDecimal getTongTienThanhToan() { return tongTienThanhToan; }
    public void setTongTienThanhToan(BigDecimal tongTienThanhToan) { this.tongTienThanhToan = tongTienThanhToan; }
    public BigDecimal getDaThanhToan() { return daThanhToan; }
    public void setDaThanhToan(BigDecimal daThanhToan) { this.daThanhToan = daThanhToan; }
    public String getPhuongThucThanhToan() { return phuongThucThanhToan; }
    public void setPhuongThucThanhToan(String phuongThucThanhToan) { this.phuongThucThanhToan = phuongThucThanhToan; }
    public String getTrangThaiThanhToan() { return trangThaiThanhToan; }
    public void setTrangThaiThanhToan(String trangThaiThanhToan) { this.trangThaiThanhToan = trangThaiThanhToan; }
    public DonDatPhong getDonDatPhong() { return donDatPhong; }
    public void setDonDatPhong(DonDatPhong donDatPhong) { this.donDatPhong = donDatPhong; }
}
