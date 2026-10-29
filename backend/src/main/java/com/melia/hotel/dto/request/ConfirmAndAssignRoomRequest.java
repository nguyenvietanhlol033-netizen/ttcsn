package com.melia.hotel.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public class ConfirmAndAssignRoomRequest {

    @NotBlank(message = "Mã đặt phòng không được để trống")
    private String maDatPhong;

    @NotBlank(message = "Vui lòng chọn phòng vật lý cụ thể để gán cho khách")
    private String maPhongCuThe;

    private BigDecimal tienCoc;

    private String ghiChu;

    private String maNhanVien;

    public ConfirmAndAssignRoomRequest() {}

    public ConfirmAndAssignRoomRequest(String maDatPhong, String maPhongCuThe, BigDecimal tienCoc, String ghiChu) {
        this.maDatPhong = maDatPhong;
        this.maPhongCuThe = maPhongCuThe;
        this.tienCoc = tienCoc;
        this.ghiChu = ghiChu;
    }

    public String getMaDatPhong() { return maDatPhong; }
    public void setMaDatPhong(String maDatPhong) { this.maDatPhong = maDatPhong; }
    public String getMaPhongCuThe() { return maPhongCuThe; }
    public void setMaPhongCuThe(String maPhongCuThe) { this.maPhongCuThe = maPhongCuThe; }
    public BigDecimal getTienCoc() { return tienCoc; }
    public void setTienCoc(BigDecimal tienCoc) { this.tienCoc = tienCoc; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
    public String getMaNhanVien() { return maNhanVien; }
    public void setMaNhanVien(String maNhanVien) { this.maNhanVien = maNhanVien; }
}
