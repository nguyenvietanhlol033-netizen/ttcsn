package com.melia.hotel.service.impl;

import com.melia.hotel.dto.request.ReviewRequest;
import com.melia.hotel.entity.DanhGia;
import com.melia.hotel.entity.DonDatPhong;
import com.melia.hotel.entity.KhachHang;
import com.melia.hotel.exception.BusinessException;
import com.melia.hotel.repository.BookingRepository;
import com.melia.hotel.repository.DanhGiaRepository;
import com.melia.hotel.repository.KhachHangRepository;
import com.melia.hotel.security.UserPrincipal;
import com.melia.hotel.service.ReviewService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final DanhGiaRepository danhGiaRepo;
    private final BookingRepository bookingRepo;
    private final KhachHangRepository khachHangRepo;

    public ReviewServiceImpl(DanhGiaRepository danhGiaRepo,
                             BookingRepository bookingRepo,
                             KhachHangRepository khachHangRepo) {
        this.danhGiaRepo = danhGiaRepo;
        this.bookingRepo = bookingRepo;
        this.khachHangRepo = khachHangRepo;
    }

    @Override
    @Transactional
    public DanhGia createReview(ReviewRequest request, UserPrincipal currentUser) {
        if (currentUser == null) {
            throw new AccessDeniedException("Yêu cầu cần đăng nhập để gửi đánh giá!");
        }

        DonDatPhong booking = bookingRepo.findById(request.getMaDatPhong())
                .orElseThrow(() -> new BusinessException("Đơn đặt phòng không tồn tại: " + request.getMaDatPhong()));

        // Quy tắc bảo mật: Khách hàng chỉ được phép đánh giá đơn phòng của chính mình
        if (!booking.getKhachHang().getMaKhachHang().equals(currentUser.getId())) {
            throw new AccessDeniedException("Bạn không có quyền đánh giá đơn đặt phòng của người khác!");
        }

        // Quy tắc nghiệp vụ: Chỉ được đánh giá sau khi đã hoàn tất trả phòng (CHECKED_OUT)
        if (!"CHECKED_OUT".equals(booking.getTrangThaiDon())) {
            throw new BusinessException("Chỉ có thể gửi đánh giá sau khi đã hoàn tất trả phòng (Trạng thái đơn: CHECKED_OUT)!");
        }

        if (danhGiaRepo.findByDonDatPhong(booking).isPresent()) {
            throw new BusinessException("Đơn phòng này đã được gửi đánh giá trước đó, không thể gửi lại!");
        }

        KhachHang khachHang = khachHangRepo.findById(currentUser.getId())
                .orElseThrow(() -> new BusinessException("Khách hàng không tồn tại: " + currentUser.getId()));

        DanhGia danhGia = DanhGia.builder()
                .maDanhGia("DG" + System.currentTimeMillis())
                .soSaoDanhGia(request.getSoSao())
                .noiDungNx(request.getNoiDung())
                .thoiGianDanhGia(LocalDateTime.now())
                .donDatPhong(booking)
                .khachHang(khachHang)
                .build();

        return danhGiaRepo.save(danhGia);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DanhGia> getHighRatingReviews() {
        return danhGiaRepo.findTopHighRatingReviews();
    }
}
