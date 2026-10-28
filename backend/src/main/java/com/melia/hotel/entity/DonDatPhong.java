package com.melia.hotel.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "don_dat_phong")
public class DonDatPhong {

    @Id
    @Column(name = "ma_dat_phong", length = 30)
    private String maDatPhong;

    @Column(name = "ngay_dat", nullable = false)
    private LocalDateTime ngayDat;

    @Column(name = "thoi_gian_nhan_phong", nullable = false)
    private LocalDate thoiGianNhanPhong;

    @Column(name = "thoi_gian_tra_phong", nullable = false)
    private LocalDate thoiGianTraPhong;

    @Column(name = "tong_tien", nullable = false, precision = 14, scale = 2)
    private BigDecimal tongTien;

    @Column(name = "trang_thai_don", nullable = false, length = 30)
    private String trangThaiDon;

    @Column(name = "yeu_cau_db", columnDefinition = "TEXT")
    private String yeuCauDb;

    @Column(name = "qr_code", columnDefinition = "MEDIUMTEXT")
    private String qrCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_khach_hang", nullable = false)
    private KhachHang khachHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_loai_phong", nullable = false)
    private LoaiPhong loaiPhong;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_phieu_gg")
    private PhieuGiamGia phieuGiamGia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_nhan_vien")
    private NhanVien nhanVien;

    @OneToMany(mappedBy = "donDatPhong", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChiTietDatPhong> danhSachChiTietDatPhong;

    @OneToMany(mappedBy = "donDatPhong", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChiTietDichVu> danhSachChiTietDichVu;

    @JsonIgnore
    @OneToOne(mappedBy = "donDatPhong", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private HoaDon hoaDon;

    public DonDatPhong() {}

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String maDatPhong;
        private LocalDateTime ngayDat;
        private LocalDate thoiGianNhanPhong;
        private LocalDate thoiGianTraPhong;
        private BigDecimal tongTien;
        private String trangThaiDon;
        private String yeuCauDb;
        private String qrCode;
        private KhachHang khachHang;
        private LoaiPhong loaiPhong;
        private PhieuGiamGia phieuGiamGia;
        private NhanVien nhanVien;

        public Builder maDatPhong(String maDatPhong) { this.maDatPhong = maDatPhong; return this; }
        public Builder ngayDat(LocalDateTime ngayDat) { this.ngayDat = ngayDat; return this; }
        public Builder thoiGianNhanPhong(LocalDate thoiGianNhanPhong) { this.thoiGianNhanPhong = thoiGianNhanPhong; return this; }
        public Builder thoiGianTraPhong(LocalDate thoiGianTraPhong) { this.thoiGianTraPhong = thoiGianTraPhong; return this; }
        public Builder tongTien(BigDecimal tongTien) { this.tongTien = tongTien; return this; }
        public Builder trangThaiDon(String trangThaiDon) { this.trangThaiDon = trangThaiDon; return this; }
        public Builder yeuCauDb(String yeuCauDb) { this.yeuCauDb = yeuCauDb; return this; }
        public Builder qrCode(String qrCode) { this.qrCode = qrCode; return this; }
        public Builder khachHang(KhachHang khachHang) { this.khachHang = khachHang; return this; }
        public Builder loaiPhong(LoaiPhong loaiPhong) { this.loaiPhong = loaiPhong; return this; }
        public Builder phieuGiamGia(PhieuGiamGia phieuGiamGia) { this.phieuGiamGia = phieuGiamGia; return this; }
        public Builder nhanVien(NhanVien nhanVien) { this.nhanVien = nhanVien; return this; }
        public DonDatPhong build() {
            DonDatPhong d = new DonDatPhong();
            d.maDatPhong = this.maDatPhong;
            d.ngayDat = this.ngayDat;
            d.thoiGianNhanPhong = this.thoiGianNhanPhong;
            d.thoiGianTraPhong = this.thoiGianTraPhong;
            d.tongTien = this.tongTien;
            d.trangThaiDon = this.trangThaiDon;
            d.yeuCauDb = this.yeuCauDb;
            d.qrCode = this.qrCode;
            d.khachHang = this.khachHang;
            d.loaiPhong = this.loaiPhong;
            d.phieuGiamGia = this.phieuGiamGia;
            d.nhanVien = this.nhanVien;
            return d;
        }
    }

    public String getMaDatPhong() { return maDatPhong; }
    public void setMaDatPhong(String maDatPhong) { this.maDatPhong = maDatPhong; }
    public LocalDateTime getNgayDat() { return ngayDat; }
    public void setNgayDat(LocalDateTime ngayDat) { this.ngayDat = ngayDat; }
    public LocalDate getThoiGianNhanPhong() { return thoiGianNhanPhong; }
    public void setThoiGianNhanPhong(LocalDate thoiGianNhanPhong) { this.thoiGianNhanPhong = thoiGianNhanPhong; }
    public LocalDate getThoiGianTraPhong() { return thoiGianTraPhong; }
    public void setThoiGianTraPhong(LocalDate thoiGianTraPhong) { this.thoiGianTraPhong = thoiGianTraPhong; }
    public BigDecimal getTongTien() { return tongTien; }
    public void setTongTien(BigDecimal tongTien) { this.tongTien = tongTien; }
    public String getTrangThaiDon() { return trangThaiDon; }
    public void setTrangThaiDon(String trangThaiDon) { this.trangThaiDon = trangThaiDon; }
    public String getYeuCauDb() { return yeuCauDb; }
    public void setYeuCauDb(String yeuCauDb) { this.yeuCauDb = yeuCauDb; }
    public String getQrCode() { return qrCode; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }
    public KhachHang getKhachHang() { return khachHang; }
    public void setKhachHang(KhachHang khachHang) { this.khachHang = khachHang; }
    public LoaiPhong getLoaiPhong() { return loaiPhong; }
    public void setLoaiPhong(LoaiPhong loaiPhong) { this.loaiPhong = loaiPhong; }
    public PhieuGiamGia getPhieuGiamGia() { return phieuGiamGia; }
    public void setPhieuGiamGia(PhieuGiamGia phieuGiamGia) { this.phieuGiamGia = phieuGiamGia; }
    public NhanVien getNhanVien() { return nhanVien; }
    public void setNhanVien(NhanVien nhanVien) { this.nhanVien = nhanVien; }
    public List<ChiTietDatPhong> getDanhSachChiTietDatPhong() { return danhSachChiTietDatPhong; }
    public void setDanhSachChiTietDatPhong(List<ChiTietDatPhong> danhSachChiTietDatPhong) { this.danhSachChiTietDatPhong = danhSachChiTietDatPhong; }
    public List<ChiTietDichVu> getDanhSachChiTietDichVu() { return danhSachChiTietDichVu; }
    public void setDanhSachChiTietDichVu(List<ChiTietDichVu> danhSachChiTietDichVu) { this.danhSachChiTietDichVu = danhSachChiTietDichVu; }
    public HoaDon getHoaDon() { return hoaDon; }
    public void setHoaDon(HoaDon hoaDon) { this.hoaDon = hoaDon; }
}
