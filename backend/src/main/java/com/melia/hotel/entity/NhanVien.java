package com.melia.hotel.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "nhan_vien")
public class NhanVien {

    @Id
    @Column(name = "ma_nhan_vien", length = 20)
    private String maNhanVien;

    @Column(name = "ten_dang_nhap", nullable = false, unique = true, length = 50)
    private String tenDangNhap;

    @JsonIgnore
    @Column(name = "mat_khau", nullable = false)
    private String matKhau;

    @Column(name = "ho_ten", nullable = false, length = 100)
    private String hoTen;

    @Column(name = "so_dt", length = 15)
    private String soDt;

    @Column(name = "so_cccd", unique = true, length = 20)
    private String soCccd;

    @Column(name = "quyen_han", nullable = false, length = 30)
    private String quyenHan;

    @JsonIgnore
    @OneToMany(mappedBy = "nhanVien", fetch = FetchType.LAZY)
    private List<DonDatPhong> danhSachDonXuLy;

    public NhanVien() {}

    public String getMaNhanVien() { return maNhanVien; }
    public void setMaNhanVien(String maNhanVien) { this.maNhanVien = maNhanVien; }
    public String getTenDangNhap() { return tenDangNhap; }
    public void setTenDangNhap(String tenDangNhap) { this.tenDangNhap = tenDangNhap; }
    public String getMatKhau() { return matKhau; }
    public void setMatKhau(String matKhau) { this.matKhau = matKhau; }
    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    public String getSoDt() { return soDt; }
    public void setSoDt(String soDt) { this.soDt = soDt; }
    public String getSoCccd() { return soCccd; }
    public void setSoCccd(String soCccd) { this.soCccd = soCccd; }
    public String getQuyenHan() { return quyenHan; }
    public void setQuyenHan(String quyenHan) { this.quyenHan = quyenHan; }
    public List<DonDatPhong> getDanhSachDonXuLy() { return danhSachDonXuLy; }
    public void setDanhSachDonXuLy(List<DonDatPhong> danhSachDonXuLy) { this.danhSachDonXuLy = danhSachDonXuLy; }
}
