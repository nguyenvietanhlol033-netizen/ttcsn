package com.melia.hotel.service.impl;

import com.melia.hotel.dto.request.BookingRequest;
import com.melia.hotel.dto.request.CouponApplyRequest;
import com.melia.hotel.dto.response.BookingDetailResponse;
import com.melia.hotel.dto.response.BookingResponse;
import com.melia.hotel.entity.*;
import com.melia.hotel.exception.BusinessException;
import com.melia.hotel.repository.*;
import com.melia.hotel.security.UserPrincipal;
import com.melia.hotel.service.BookingService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepo;
    private final KhachHangRepository khachHangRepo;
    private final LoaiPhongRepository loaiPhongRepo;
    private final RoomRepository roomRepo;
    private final CouponRepository couponRepo;
    private final HoaDonRepository hoaDonRepo;

    public BookingServiceImpl(BookingRepository bookingRepo,
                              KhachHangRepository khachHangRepo,
                              LoaiPhongRepository loaiPhongRepo,
                              RoomRepository roomRepo,
                              CouponRepository couponRepo,
                              HoaDonRepository hoaDonRepo) {
        this.bookingRepo = bookingRepo;
        this.khachHangRepo = khachHangRepo;
        this.loaiPhongRepo = loaiPhongRepo;
        this.roomRepo = roomRepo;
        this.couponRepo = couponRepo;
        this.hoaDonRepo = hoaDonRepo;
    }

    @Override
    @Transactional
    public BookingDetailResponse createBooking(BookingRequest request, UserPrincipal currentUser) {
        String targetCustomerId;
        boolean isStaff = currentUser != null && currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_RECEPTIONIST") || a.getAuthority().equals("ROLE_ADMIN"));

        if (isStaff) {
            if (request.getCustomerId() == null || request.getCustomerId().isBlank()) {
                throw new BusinessException("Nhân viên đặt phòng tại quầy cần cung cấp mã khách hàng!");
            }
            targetCustomerId = request.getCustomerId();
        } else {
            if (currentUser == null) {
                throw new AccessDeniedException("Yêu cầu cần đăng nhập để đặt phòng!");
            }
            targetCustomerId = currentUser.getId();
        }

        KhachHang khachHang = khachHangRepo.findById(targetCustomerId)
                .orElseThrow(() -> new BusinessException("Khách hàng không tồn tại: " + targetCustomerId));

        // Khóa bi quan (PESSIMISTIC_WRITE) dòng LoaiPhong để tránh Race Condition khi 2 khách đặt phòng cuối cùng
        LoaiPhong loaiPhong = loaiPhongRepo.findByIdWithLock(request.getRoomTypeId())
                .orElseThrow(() -> new BusinessException("Loại phòng không hợp lệ: " + request.getRoomTypeId()));

        if (request.getCheckInDate() == null || request.getCheckOutDate() == null) {
            throw new BusinessException("Ngày nhận phòng và ngày trả phòng không được để trống!");
        }

        long soDem = ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate());
        if (soDem <= 0) {
            throw new BusinessException("Ngày trả phòng phải sau ngày nhận phòng ít nhất 1 ngày!");
        }

        LocalDateTime holdDeadline = LocalDateTime.now().minusMinutes(15);
        long operationalRooms = roomRepo.countOperationalRoomsByLoaiPhong(loaiPhong.getMaLoaiPhong());
        long bookedRooms = bookingRepo.countActiveOverlappingBookings(
                loaiPhong.getMaLoaiPhong(), request.getCheckInDate(), request.getCheckOutDate(), holdDeadline);
        long availableRooms = operationalRooms - bookedRooms;

        if (availableRooms <= 0) {
            throw new BusinessException("Loại phòng " + loaiPhong.getTenLoaiPhong() + " đã hết phòng trống trong khoảng thời gian đã chọn!");
        }

        BigDecimal tienPhong = loaiPhong.getGiaPhongCoBan().multiply(BigDecimal.valueOf(soDem));
        BigDecimal tienGiam = BigDecimal.ZERO;
        PhieuGiamGia voucher = null;

        if (request.getCouponCode() != null && !request.getCouponCode().trim().isEmpty()) {
            voucher = couponRepo.findById(request.getCouponCode().trim().toUpperCase())
                    .orElseThrow(() -> new BusinessException("Mã giảm giá không hợp lệ: " + request.getCouponCode()));

            LocalDateTime now = LocalDateTime.now();
            if (now.isBefore(voucher.getThoiGianBatDau()) || now.isAfter(voucher.getThoiGianKetThuc())) {
                throw new BusinessException("Mã giảm giá đã hết hạn sử dụng!");
            }
            if (voucher.getSoLuongPhieu() <= 0) {
                throw new BusinessException("Mã giảm giá đã hết lượt sử dụng!");
            }

            if ("PERCENT".equalsIgnoreCase(voucher.getLoaiGiam())) {
                tienGiam = tienPhong.multiply(voucher.getGiaTriGiam()).divide(BigDecimal.valueOf(100));
            } else {
                tienGiam = voucher.getGiaTriGiam();
            }

            if (tienGiam.compareTo(tienPhong) > 0) {
                tienGiam = tienPhong;
            }

            voucher.setSoLuongPhieu(voucher.getSoLuongPhieu() - 1);
            couponRepo.save(voucher);
        }

        BigDecimal tongTien = tienPhong.subtract(tienGiam);
        String maDatPhong = "DP" + System.currentTimeMillis();

        DonDatPhong donDatPhong = DonDatPhong.builder()
                .maDatPhong(maDatPhong)
                .ngayDat(LocalDateTime.now())
                .thoiGianNhanPhong(request.getCheckInDate())
                .thoiGianTraPhong(request.getCheckOutDate())
                .tongTien(tongTien)
                .trangThaiDon("PENDING")
                .yeuCauDb(request.getSpecialRequests())
                .khachHang(khachHang)
                .loaiPhong(loaiPhong)
                .phieuGiamGia(voucher)
                .build();

        bookingRepo.save(donDatPhong);

        HoaDon hoaDon = HoaDon.builder()
                .maHoaDon("HD" + maDatPhong)
                .ngayLapHoaDon(LocalDateTime.now())
                .tienPhong(tienPhong)
                .tienDichVu(BigDecimal.ZERO)
                .phuThu(BigDecimal.ZERO)
                .giamGia(tienGiam)
                .tongTienThanhToan(tongTien)
                .daThanhToan(BigDecimal.ZERO)
                .trangThaiThanhToan("UNPAID")
                .donDatPhong(donDatPhong)
                .build();

        hoaDonRepo.save(hoaDon);

        return BookingDetailResponse.builder()
                .bookingId(maDatPhong)
                .roomTypeName(loaiPhong.getTenLoaiPhong())
                .checkInDate(donDatPhong.getThoiGianNhanPhong())
                .checkOutDate(donDatPhong.getThoiGianTraPhong())
                .nights(soDem)
                .roomPrice(tienPhong)
                .discountAmount(tienGiam)
                .totalAmount(tongTien)
                .status("PENDING")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getBookingById(String maDatPhong, UserPrincipal currentUser) {
        DonDatPhong booking = bookingRepo.findById(maDatPhong)
                .orElseThrow(() -> new BusinessException("Không tìm thấy đơn đặt phòng: " + maDatPhong));

        boolean isStaff = currentUser != null && currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_RECEPTIONIST") || a.getAuthority().equals("ROLE_ADMIN"));

        if (!isStaff && (currentUser == null || !currentUser.getId().equals(booking.getKhachHang().getMaKhachHang()))) {
            throw new AccessDeniedException("Bạn không có quyền truy cập thông tin đơn đặt phòng này!");
        }

        return mapToBookingResponse(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public DonDatPhong getBookingEntityById(String maDatPhong) {
        return bookingRepo.findById(maDatPhong)
                .orElseThrow(() -> new BusinessException("Không tìm thấy đơn đặt phòng: " + maDatPhong));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByCustomer(String maKhachHang, UserPrincipal currentUser) {
        boolean isStaff = currentUser != null && currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_RECEPTIONIST") || a.getAuthority().equals("ROLE_ADMIN"));

        if (!isStaff && (currentUser == null || !currentUser.getId().equals(maKhachHang))) {
            throw new AccessDeniedException("Bạn không có quyền truy cập lịch sử đặt phòng của khách hàng khác!");
        }

        KhachHang khachHang = khachHangRepo.findById(maKhachHang)
                .orElseThrow(() -> new BusinessException("Khách hàng không tồn tại: " + maKhachHang));

        List<DonDatPhong> list = bookingRepo.findByKhachHang(khachHang);
        List<BookingResponse> responses = new ArrayList<>();
        for (DonDatPhong b : list) {
            responses.add(mapToBookingResponse(b));
        }
        return responses;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal applyCoupon(CouponApplyRequest request) {
        PhieuGiamGia voucher = couponRepo.findById(request.getCouponCode().trim().toUpperCase())
                .orElseThrow(() -> new BusinessException("Mã giảm giá không hợp lệ: " + request.getCouponCode()));

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(voucher.getThoiGianBatDau()) || now.isAfter(voucher.getThoiGianKetThuc())) {
            throw new BusinessException("Mã giảm giá đã hết hạn sử dụng!");
        }
        if (voucher.getSoLuongPhieu() <= 0) {
            throw new BusinessException("Mã giảm giá đã hết lượt sử dụng!");
        }

        BigDecimal tienGiam;
        if ("PERCENT".equalsIgnoreCase(voucher.getLoaiGiam())) {
            tienGiam = request.getOriginalAmount().multiply(voucher.getGiaTriGiam()).divide(BigDecimal.valueOf(100));
        } else {
            tienGiam = voucher.getGiaTriGiam();
        }

        if (tienGiam.compareTo(request.getOriginalAmount()) > 0) {
            tienGiam = request.getOriginalAmount();
        }
        return tienGiam;
    }

    private BookingResponse mapToBookingResponse(DonDatPhong b) {
        long soDem = ChronoUnit.DAYS.between(b.getThoiGianNhanPhong(), b.getThoiGianTraPhong());
        String maPhong = null;
        String soPhong = null;

        if (b.getDanhSachChiTietDatPhong() != null && !b.getDanhSachChiTietDatPhong().isEmpty()) {
            ChiTietDatPhong ctdp = b.getDanhSachChiTietDatPhong().get(0);
            maPhong = ctdp.getPhong() != null ? ctdp.getPhong().getMaPhong() : null;
            soPhong = ctdp.getSoPhong();
        }

        return BookingResponse.builder()
                .maDatPhong(b.getMaDatPhong())
                .ngayDat(b.getNgayDat())
                .thoiGianNhanPhong(b.getThoiGianNhanPhong())
                .thoiGianTraPhong(b.getThoiGianTraPhong())
                .soDem(soDem)
                .maLoaiPhong(b.getLoaiPhong() != null ? b.getLoaiPhong().getMaLoaiPhong() : null)
                .tenLoaiPhong(b.getLoaiPhong() != null ? b.getLoaiPhong().getTenLoaiPhong() : null)
                .tongTien(b.getTongTien())
                .trangThaiDon(b.getTrangThaiDon())
                .yeuCauDb(b.getYeuCauDb())
                .maKhachHang(b.getKhachHang() != null ? b.getKhachHang().getMaKhachHang() : null)
                .tenKhachHang(b.getKhachHang() != null ? b.getKhachHang().getHoTen() : null)
                .soDienThoaiKhachHang(b.getKhachHang() != null ? b.getKhachHang().getSoDienThoai() : null)
                .emailKhachHang(b.getKhachHang() != null ? b.getKhachHang().getEmail() : null)
                .maPhieuGg(b.getPhieuGiamGia() != null ? b.getPhieuGiamGia().getMaPhieuGg() : null)
                .maPhong(maPhong)
                .soPhong(soPhong)
                .qrCode(b.getQrCode())
                .build();
    }
}
