package com.melia.hotel.service;

import com.melia.hotel.dto.response.BookingResponse;
import com.melia.hotel.dto.response.InvoiceResponse;

import java.math.BigDecimal;

public interface ReceptionService {
    void processCheckIn(String maDatPhong, String maPhongCuThe, String maNhanVien);
    InvoiceResponse processCheckOut(String maDatPhong, BigDecimal phuThuPhatSinh, String phuongThucThanhToan);
    BookingResponse getBookingForReception(String keyword);
}
