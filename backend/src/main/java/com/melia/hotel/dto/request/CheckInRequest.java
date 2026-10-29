package com.melia.hotel.dto.request;

import jakarta.validation.constraints.NotBlank;

public class CheckInRequest {

    @NotBlank(message = "Mã đặt phòng không được để trống")
    private String maDatPhong;

    private String maPhongCuThe; // Tùy chọn nếu đơn đã được lễ tân gán phòng trước đó ở bước Xác nhận

    private String maNhanVien; // Tùy chọn, hệ thống tự động trích xuất từ JWT Token của nhân viên đăng nhập

    public CheckInRequest() {}

    public String getMaDatPhong() { return maDatPhong; }
    public void setMaDatPhong(String maDatPhong) { this.maDatPhong = maDatPhong; }
    public String getMaPhongCuThe() { return maPhongCuThe; }
    public void setMaPhongCuThe(String maPhongCuThe) { this.maPhongCuThe = maPhongCuThe; }
    public String getMaNhanVien() { return maNhanVien; }
    public void setMaNhanVien(String maNhanVien) { this.maNhanVien = maNhanVien; }
}
