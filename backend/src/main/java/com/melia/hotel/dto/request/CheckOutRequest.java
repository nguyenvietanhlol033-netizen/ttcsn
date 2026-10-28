package com.melia.hotel.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public class CheckOutRequest {

    @NotBlank(message = "Mã đặt phòng không được để trống")
    private String maDatPhong;

    private BigDecimal phuThuPhatSinh = BigDecimal.ZERO;
    private String phuongThucThanhToan; // CASH, VNPAY, CREDIT_CARD

    public CheckOutRequest() {}

    public String getMaDatPhong() { return maDatPhong; }
    public void setMaDatPhong(String maDatPhong) { this.maDatPhong = maDatPhong; }
    public BigDecimal getPhuThuPhatSinh() { return phuThuPhatSinh; }
    public void setPhuThuPhatSinh(BigDecimal phuThuPhatSinh) { this.phuThuPhatSinh = phuThuPhatSinh; }
    public String getPhuongThucThanhToan() { return phuongThucThanhToan; }
    public void setPhuongThucThanhToan(String phuongThucThanhToan) { this.phuongThucThanhToan = phuongThucThanhToan; }
}
