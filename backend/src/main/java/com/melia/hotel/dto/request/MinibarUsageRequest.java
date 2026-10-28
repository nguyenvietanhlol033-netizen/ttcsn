package com.melia.hotel.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class MinibarUsageRequest {

    @NotBlank(message = "Mã đặt phòng không được để trống")
    private String maDatPhong;

    @NotBlank(message = "Mã dịch vụ không được để trống")
    private String maDichVu;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 1, message = "Số lượng tiêu dùng phải lớn hơn hoặc bằng 1")
    private Integer soLuong;

    public MinibarUsageRequest() {}

    public String getMaDatPhong() { return maDatPhong; }
    public void setMaDatPhong(String maDatPhong) { this.maDatPhong = maDatPhong; }
    public String getMaDichVu() { return maDichVu; }
    public void setMaDichVu(String maDichVu) { this.maDichVu = maDichVu; }
    public Integer getSoLuong() { return soLuong; }
    public void setSoLuong(Integer soLuong) { this.soLuong = soLuong; }
}
