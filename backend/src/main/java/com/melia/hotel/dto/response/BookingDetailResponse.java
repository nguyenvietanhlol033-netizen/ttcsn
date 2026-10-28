package com.melia.hotel.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public class BookingDetailResponse {
    private String bookingId;
    private String roomTypeName;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Long nights;
    private BigDecimal roomPrice;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;
    private String status;
    private String qrCode;

    public BookingDetailResponse() {}

    public BookingDetailResponse(String bookingId, String roomTypeName, LocalDate checkInDate, LocalDate checkOutDate, Long nights, BigDecimal roomPrice, BigDecimal discountAmount, BigDecimal totalAmount, String status, String qrCode) {
        this.bookingId = bookingId;
        this.roomTypeName = roomTypeName;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.nights = nights;
        this.roomPrice = roomPrice;
        this.discountAmount = discountAmount;
        this.totalAmount = totalAmount;
        this.status = status;
        this.qrCode = qrCode;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String bookingId;
        private String roomTypeName;
        private LocalDate checkInDate;
        private LocalDate checkOutDate;
        private Long nights;
        private BigDecimal roomPrice;
        private BigDecimal discountAmount;
        private BigDecimal totalAmount;
        private String status;
        private String qrCode;

        public Builder bookingId(String bookingId) { this.bookingId = bookingId; return this; }
        public Builder roomTypeName(String roomTypeName) { this.roomTypeName = roomTypeName; return this; }
        public Builder checkInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; return this; }
        public Builder checkOutDate(LocalDate checkOutDate) { this.checkOutDate = checkOutDate; return this; }
        public Builder nights(Long nights) { this.nights = nights; return this; }
        public Builder roomPrice(BigDecimal roomPrice) { this.roomPrice = roomPrice; return this; }
        public Builder discountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; return this; }
        public Builder totalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; return this; }
        public Builder status(String status) { this.status = status; return this; }
        public Builder qrCode(String qrCode) { this.qrCode = qrCode; return this; }
        public BookingDetailResponse build() {
            return new BookingDetailResponse(bookingId, roomTypeName, checkInDate, checkOutDate, nights, roomPrice, discountAmount, totalAmount, status, qrCode);
        }
    }

    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }
    public String getRoomTypeName() { return roomTypeName; }
    public void setRoomTypeName(String roomTypeName) { this.roomTypeName = roomTypeName; }
    public LocalDate getCheckInDate() { return checkInDate; }
    public void setCheckInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; }
    public LocalDate getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(LocalDate checkOutDate) { this.checkOutDate = checkOutDate; }
    public Long getNights() { return nights; }
    public void setNights(Long nights) { this.nights = nights; }
    public BigDecimal getRoomPrice() { return roomPrice; }
    public void setRoomPrice(BigDecimal roomPrice) { this.roomPrice = roomPrice; }
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getQrCode() { return qrCode; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }
}
