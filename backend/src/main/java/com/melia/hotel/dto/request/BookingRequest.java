package com.melia.hotel.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class BookingRequest {

    private String customerId; // Có thể null khi khách gửi request, backend lấy từ JWT Principal

    @NotBlank(message = "Mã loại phòng không được để trống")
    private String roomTypeId;

    @NotNull(message = "Ngày nhận phòng không được để trống")
    @FutureOrPresent(message = "Ngày nhận phòng phải là ngày hiện tại hoặc tương lai")
    private LocalDate checkInDate;

    @NotNull(message = "Ngày trả phòng không được để trống")
    @Future(message = "Ngày trả phòng phải trong tương lai")
    private LocalDate checkOutDate;

    private String couponCode;
    private String specialRequests;

    public BookingRequest() {}

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public String getRoomTypeId() { return roomTypeId; }
    public void setRoomTypeId(String roomTypeId) { this.roomTypeId = roomTypeId; }
    public LocalDate getCheckInDate() { return checkInDate; }
    public void setCheckInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; }
    public LocalDate getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(LocalDate checkOutDate) { this.checkOutDate = checkOutDate; }
    public String getCouponCode() { return couponCode; }
    public void setCouponCode(String couponCode) { this.couponCode = couponCode; }
    public String getSpecialRequests() { return specialRequests; }
    public void setSpecialRequests(String specialRequests) { this.specialRequests = specialRequests; }
}
