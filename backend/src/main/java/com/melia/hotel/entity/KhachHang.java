package com.melia.hotel.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "khach_hang")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "danhSachDatPhong", "danhSachDanhGia"})
public class KhachHang {

    @Id
    @Column(name = "ma_khach_hang", length = 20)
    private String maKhachHang;

    @Column(name = "ten_dang_nhap", length = 50, unique = true)
    private String tenDangNhap;

    @Column(name = "ho_ten", nullable = false, length = 100)
    private String hoTen;

    @Column(name = "ngay_sinh")
    private LocalDate ngaySinh;

    @Column(name = "so_dien_thoai", nullable = false, unique = true, length = 15)
    private String soDienThoai;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @JsonIgnore
    @Column(name = "mat_khau", nullable = false)
    private String matKhau;

    @Column(name = "so_cccd", unique = true, length = 20)
    private String soCccd;

    @Column(name = "trang_thai_tk", length = 20)
    private String trangThaiTk;

    @JsonIgnore
    @OneToMany(mappedBy = "khachHang", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DonDatPhong> danhSachDatPhong;

    @JsonIgnore
    @OneToMany(mappedBy = "khachHang", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DanhGia> danhSachDanhGia;

    public KhachHang() {}

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String maKhachHang;
        private String tenDangNhap;
        private String hoTen;
        private LocalDate ngaySinh;
        private String soDienThoai;
        private String email;
        private String matKhau;
        private String soCccd;
        private String trangThaiTk;

        public Builder maKhachHang(String maKhachHang) { this.maKhachHang = maKhachHang; return this; }
        public Builder tenDangNhap(String tenDangNhap) { this.tenDangNhap = tenDangNhap; return this; }
        public Builder hoTen(String hoTen) { this.hoTen = hoTen; return this; }
        public Builder ngaySinh(LocalDate ngaySinh) { this.ngaySinh = ngaySinh; return this; }
        public Builder soDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder matKhau(String matKhau) { this.matKhau = matKhau; return this; }
        public Builder soCccd(String soCccd) { this.soCccd = soCccd; return this; }
        public Builder trangThaiTk(String trangThaiTk) { this.trangThaiTk = trangThaiTk; return this; }
        public KhachHang build() {
            KhachHang k = new KhachHang();
            k.maKhachHang = this.maKhachHang;
            k.tenDangNhap = this.tenDangNhap;
            k.hoTen = this.hoTen;
            k.ngaySinh = this.ngaySinh;
            k.soDienThoai = this.soDienThoai;
            k.email = this.email;
            k.matKhau = this.matKhau;
            k.soCccd = this.soCccd;
            k.trangThaiTk = this.trangThaiTk;
            return k;
        }
    }

    public String getMaKhachHang() { return maKhachHang; }
    public void setMaKhachHang(String maKhachHang) { this.maKhachHang = maKhachHang; }
    public String getTenDangNhap() { return tenDangNhap; }
    public void setTenDangNhap(String tenDangNhap) { this.tenDangNhap = tenDangNhap; }
    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    public LocalDate getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(LocalDate ngaySinh) { this.ngaySinh = ngaySinh; }
    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getMatKhau() { return matKhau; }
    public void setMatKhau(String matKhau) { this.matKhau = matKhau; }
    public String getSoCccd() { return soCccd; }
    public void setSoCccd(String soCccd) { this.soCccd = soCccd; }
    public String getTrangThaiTk() { return trangThaiTk; }
    public void setTrangThaiTk(String trangThaiTk) { this.trangThaiTk = trangThaiTk; }
    public List<DonDatPhong> getDanhSachDatPhong() { return danhSachDatPhong; }
    public void setDanhSachDatPhong(List<DonDatPhong> danhSachDatPhong) { this.danhSachDatPhong = danhSachDatPhong; }
    public List<DanhGia> getDanhSachDanhGia() { return danhSachDanhGia; }
    public void setDanhSachDanhGia(List<DanhGia> danhSachDanhGia) { this.danhSachDanhGia = danhSachDanhGia; }
}
