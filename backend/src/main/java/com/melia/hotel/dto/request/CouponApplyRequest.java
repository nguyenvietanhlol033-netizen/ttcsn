package com.melia.hotel.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public class CouponApplyRequest {

    @NotBlank(message = "Mã giảm giá không được để trống")
    private String couponCode;

    @NotNull(message = "Số tiền gốc không được để trống")
    @Positive(message = "Số tiền gốc phải lớn hơn 0")
    private BigDecimal originalAmount;

    public CouponApplyRequest() {}

    public String getCouponCode() { return couponCode; }
    public void setCouponCode(String couponCode) { this.couponCode = couponCode; }
    public BigDecimal getOriginalAmount() { return originalAmount; }
    public void setOriginalAmount(BigDecimal originalAmount) { this.originalAmount = originalAmount; }
}
