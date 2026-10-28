package com.melia.hotel.service;

import com.melia.hotel.dto.request.BookingRequest;
import com.melia.hotel.dto.request.CouponApplyRequest;
import com.melia.hotel.dto.response.BookingDetailResponse;
import com.melia.hotel.dto.response.BookingResponse;
import com.melia.hotel.entity.DonDatPhong;
import com.melia.hotel.security.UserPrincipal;

import java.math.BigDecimal;
import java.util.List;

public interface BookingService {
    BookingDetailResponse createBooking(BookingRequest request, UserPrincipal currentUser);
    BookingResponse getBookingById(String maDatPhong, UserPrincipal currentUser);
    DonDatPhong getBookingEntityById(String maDatPhong);
    List<BookingResponse> getBookingsByCustomer(String maKhachHang, UserPrincipal currentUser);
    BigDecimal applyCoupon(CouponApplyRequest request);
}
