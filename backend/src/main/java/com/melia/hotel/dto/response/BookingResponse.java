package com.melia.hotel.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class BookingResponse {
    private String maDatPhong;
    private LocalDateTime ngayDat;
    private LocalDate thoiGianNhanPhong;
    private LocalDate thoiGianTraPhong;
    private Long soDem;
    private String maLoaiPhong;
    private String tenLoaiPhong;
    private BigDecimal tongTien;
    private String trangThaiDon;
    private String yeuCauDb;
    private String maKhachHang;
    private String tenKhachHang;
    private String soDienThoaiKhachHang;
    private String emailKhachHang;
    private String maPhieuGg;
    private String maPhong;
    private String soPhong;
    private String qrCode;

    public BookingResponse() {}

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String maDatPhong;
        private LocalDateTime ngayDat;
        private LocalDate thoiGianNhanPhong;
        private LocalDate thoiGianTraPhong;
        private Long soDem;
        private String maLoaiPhong;
        private String tenLoaiPhong;
        private BigDecimal tongTien;
        private String trangThaiDon;
        private String yeuCauDb;
        private String maKhachHang;
        private String tenKhachHang;
        private String soDienThoaiKhachHang;
        private String emailKhachHang;
        private String maPhieuGg;
        private String maPhong;
        private String soPhong;
        private String qrCode;

        public Builder maDatPhong(String maDatPhong) { this.maDatPhong = maDatPhong; return this; }
        public Builder ngayDat(LocalDateTime ngayDat) { this.ngayDat = ngayDat; return this; }
        public Builder thoiGianNhanPhong(LocalDate thoiGianNhanPhong) { this.thoiGianNhanPhong = thoiGianNhanPhong; return this; }
        public Builder thoiGianTraPhong(LocalDate thoiGianTraPhong) { this.thoiGianTraPhong = thoiGianTraPhong; return this; }
        public Builder soDem(Long soDem) { this.soDem = soDem; return this; }
        public Builder maLoaiPhong(String maLoaiPhong) { this.maLoaiPhong = maLoaiPhong; return this; }
        public Builder tenLoaiPhong(String tenLoaiPhong) { this.tenLoaiPhong = tenLoaiPhong; return this; }
        public Builder tongTien(BigDecimal tongTien) { this.tongTien = tongTien; return this; }
        public Builder trangThaiDon(String trangThaiDon) { this.trangThaiDon = trangThaiDon; return this; }
        public Builder yeuCauDb(String yeuCauDb) { this.yeuCauDb = yeuCauDb; return this; }
        public Builder maKhachHang(String maKhachHang) { this.maKhachHang = maKhachHang; return this; }
        public Builder tenKhachHang(String tenKhachHang) { this.tenKhachHang = tenKhachHang; return this; }
        public Builder soDienThoaiKhachHang(String soDienThoaiKhachHang) { this.soDienThoaiKhachHang = soDienThoaiKhachHang; return this; }
        public Builder emailKhachHang(String emailKhachHang) { this.emailKhachHang = emailKhachHang; return this; }
        public Builder maPhieuGg(String maPhieuGg) { this.maPhieuGg = maPhieuGg; return this; }
        public Builder maPhong(String maPhong) { this.maPhong = maPhong; return this; }
        public Builder soPhong(String soPhong) { this.soPhong = soPhong; return this; }
        public Builder qrCode(String qrCode) { this.qrCode = qrCode; return this; }

        public BookingResponse build() {
            BookingResponse r = new BookingResponse();
            r.maDatPhong = this.maDatPhong;
            r.ngayDat = this.ngayDat;
            r.thoiGianNhanPhong = this.thoiGianNhanPhong;
            r.thoiGianTraPhong = this.thoiGianTraPhong;
            r.soDem = this.soDem;
            r.maLoaiPhong = this.maLoaiPhong;
            r.tenLoaiPhong = this.tenLoaiPhong;
            r.tongTien = this.tongTien;
            r.trangThaiDon = this.trangThaiDon;
            r.yeuCauDb = this.yeuCauDb;
            r.maKhachHang = this.maKhachHang;
            r.tenKhachHang = this.tenKhachHang;
            r.soDienThoaiKhachHang = this.soDienThoaiKhachHang;
            r.emailKhachHang = this.emailKhachHang;
            r.maPhieuGg = this.maPhieuGg;
            r.maPhong = this.maPhong;
            r.soPhong = this.soPhong;
            r.qrCode = this.qrCode;
            return r;
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
    public Long getSoDem() { return soDem; }
    public void setSoDem(Long soDem) { this.soDem = soDem; }
    public String getMaLoaiPhong() { return maLoaiPhong; }
    public void setMaLoaiPhong(String maLoaiPhong) { this.maLoaiPhong = maLoaiPhong; }
    public String getTenLoaiPhong() { return tenLoaiPhong; }
    public void setTenLoaiPhong(String tenLoaiPhong) { this.tenLoaiPhong = tenLoaiPhong; }
    public BigDecimal getTongTien() { return tongTien; }
    public void setTongTien(BigDecimal tongTien) { this.tongTien = tongTien; }
    public String getTrangThaiDon() { return trangThaiDon; }
    public void setTrangThaiDon(String trangThaiDon) { this.trangThaiDon = trangThaiDon; }
    public String getYeuCauDb() { return yeuCauDb; }
    public void setYeuCauDb(String yeuCauDb) { this.yeuCauDb = yeuCauDb; }
    public String getMaKhachHang() { return maKhachHang; }
    public void setMaKhachHang(String maKhachHang) { this.maKhachHang = maKhachHang; }
    public String getTenKhachHang() { return tenKhachHang; }
    public void setTenKhachHang(String tenKhachHang) { this.tenKhachHang = tenKhachHang; }
    public String getSoDienThoaiKhachHang() { return soDienThoaiKhachHang; }
    public void setSoDienThoaiKhachHang(String soDienThoaiKhachHang) { this.soDienThoaiKhachHang = soDienThoaiKhachHang; }
    public String getEmailKhachHang() { return emailKhachHang; }
    public void setEmailKhachHang(String emailKhachHang) { this.emailKhachHang = emailKhachHang; }
    public String getMaPhieuGg() { return maPhieuGg; }
    public void setMaPhieuGg(String maPhieuGg) { this.maPhieuGg = maPhieuGg; }
    public String getMaPhong() { return maPhong; }
    public void setMaPhong(String maPhong) { this.maPhong = maPhong; }
    public String getSoPhong() { return soPhong; }
    public void setSoPhong(String soPhong) { this.soPhong = soPhong; }
    public String getQrCode() { return qrCode; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }

    // JSON Aliases for Frontend compatibility
    public String getBookingId() { return maDatPhong; }
    public LocalDateTime getBookingDate() { return ngayDat; }
    public LocalDate getCheckInDate() { return thoiGianNhanPhong; }
    public LocalDate getCheckOutDate() { return thoiGianTraPhong; }
    public Long getNights() { return soDem; }
    public String getRoomTypeId() { return maLoaiPhong; }
    public String getRoomTypeName() { return tenLoaiPhong; }
    public BigDecimal getTotalAmount() { return tongTien; }
    public String getStatus() { return trangThaiDon; }
    public String getSpecialRequests() { return yeuCauDb; }
    public String getCustomerId() { return maKhachHang; }
    public String getCustomerName() { return tenKhachHang; }
    public String getGuestName() { return tenKhachHang; }
    public String getCustomerPhone() { return soDienThoaiKhachHang; }
    public String getGuestPhone() { return soDienThoaiKhachHang; }
    public String getCustomerEmail() { return emailKhachHang; }
    public String getGuestEmail() { return emailKhachHang; }
    public String getCouponCode() { return maPhieuGg; }
    public String getRoomId() { return maPhong; }
    public String getAssignedRoomNumber() { return soPhong; }
    public String getQrCodeBase64() { return qrCode; }
}
