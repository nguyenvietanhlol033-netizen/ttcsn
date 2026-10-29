package com.melia.hotel.service;

import com.melia.hotel.dto.request.ConfirmAndAssignRoomRequest;
import com.melia.hotel.dto.response.BookingResponse;
import com.melia.hotel.dto.response.InvoiceResponse;

import java.math.BigDecimal;
import java.util.List;

public interface ReceptionService {
    List<BookingResponse> getAllBookings(String status, String keyword);
    BookingResponse confirmAndAssignRoom(ConfirmAndAssignRoomRequest request, String maNhanVien);
    void processCheckIn(String maDatPhong, String maPhongCuThe, String maNhanVien);
    InvoiceResponse processCheckOut(String maDatPhong, BigDecimal phuThuPhatSinh, String phuongThucThanhToan);
    BookingResponse getBookingForReception(String keyword);
}
