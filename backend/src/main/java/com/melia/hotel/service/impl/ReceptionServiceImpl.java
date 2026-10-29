package com.melia.hotel.service.impl;

import com.melia.hotel.dto.request.ConfirmAndAssignRoomRequest;
import com.melia.hotel.dto.response.BookingResponse;
import com.melia.hotel.dto.response.InvoiceResponse;
import com.melia.hotel.entity.*;
import com.melia.hotel.exception.BusinessException;
import com.melia.hotel.repository.*;
import com.melia.hotel.service.ReceptionService;
import com.melia.hotel.util.QRCodeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReceptionServiceImpl implements ReceptionService {

    private static final Logger log = LoggerFactory.getLogger(ReceptionServiceImpl.class);

    private final BookingRepository bookingRepo;
    private final RoomRepository phongRepo;
    private final ChiTietDatPhongRepository chiTietDatPhongRepo;
    private final ChiTietDichVuRepository chiTietDichVuRepo;
    private final NhanVienRepository nhanVienRepo;
    private final HoaDonRepository hoaDonRepo;
    private final QRCodeUtil qrCodeUtil;

    public ReceptionServiceImpl(BookingRepository bookingRepo,
                                RoomRepository phongRepo,
                                ChiTietDatPhongRepository chiTietDatPhongRepo,
                                ChiTietDichVuRepository chiTietDichVuRepo,
                                NhanVienRepository nhanVienRepo,
                                HoaDonRepository hoaDonRepo,
                                QRCodeUtil qrCodeUtil) {
        this.bookingRepo = bookingRepo;
        this.phongRepo = phongRepo;
        this.chiTietDatPhongRepo = chiTietDatPhongRepo;
        this.chiTietDichVuRepo = chiTietDichVuRepo;
        this.nhanVienRepo = nhanVienRepo;
        this.hoaDonRepo = hoaDonRepo;
        this.qrCodeUtil = qrCodeUtil;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getAllBookings(String status, String keyword) {
        List<DonDatPhong> bookings;
        if (status != null && !status.isBlank() && !"ALL".equalsIgnoreCase(status)) {
            bookings = bookingRepo.findByTrangThaiDonOrderByNgayDatDesc(status.toUpperCase());
        } else {
            bookings = bookingRepo.findAllByOrderByNgayDatDesc();
        }

        if (keyword != null && !keyword.isBlank()) {
            String kw = keyword.trim().toLowerCase();
            bookings = bookings.stream()
                    .filter(b -> (b.getMaDatPhong() != null && b.getMaDatPhong().toLowerCase().contains(kw))
                            || (b.getKhachHang() != null && b.getKhachHang().getHoTen() != null && b.getKhachHang().getHoTen().toLowerCase().contains(kw))
                            || (b.getKhachHang() != null && b.getKhachHang().getSoDienThoai() != null && b.getKhachHang().getSoDienThoai().contains(kw))
                            || (b.getKhachHang() != null && b.getKhachHang().getSoCccd() != null && b.getKhachHang().getSoCccd().contains(kw)))
                    .collect(Collectors.toList());
        }

        return bookings.stream().map(this::mapToBookingResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookingResponse confirmAndAssignRoom(ConfirmAndAssignRoomRequest request, String maNhanVien) {
        DonDatPhong booking = bookingRepo.findById(request.getMaDatPhong())
                .orElseThrow(() -> new BusinessException("Đơn đặt phòng không tồn tại: " + request.getMaDatPhong()));

        if (!"PENDING".equalsIgnoreCase(booking.getTrangThaiDon()) && !"CONFIRMED".equalsIgnoreCase(booking.getTrangThaiDon())) {
            throw new BusinessException("Chỉ có thể xác nhận & gán phòng cho đơn ở trạng thái PENDING hoặc CONFIRMED (Hiện tại: " + booking.getTrangThaiDon() + ")");
        }

        Phong room = phongRepo.findById(request.getMaPhongCuThe())
                .orElseThrow(() -> new BusinessException("Phòng vật lý không tồn tại: " + request.getMaPhongCuThe()));

        // Kiểm tra loại phòng phải khớp chính xác loại phòng khách đặt (VD: Khách đặt Phòng Tổng Thống LP04 thì chỉ được gán phòng LP04)
        if (booking.getLoaiPhong() != null && !room.getLoaiPhong().getMaLoaiPhong().equals(booking.getLoaiPhong().getMaLoaiPhong())) {
            throw new BusinessException("Phòng gán (" + room.getSoPhong() + " - " + room.getLoaiPhong().getTenLoaiPhong()
                    + ") không khớp với loại phòng khách đã đặt (" + booking.getLoaiPhong().getTenLoaiPhong() + ")!");
        }

        // Kiểm tra trạng thái phòng phải là AVAILABLE
        if (!"AVAILABLE".equalsIgnoreCase(room.getTrangThaiPhong())) {
            throw new BusinessException("Phòng " + room.getSoPhong() + " hiện không ở trạng thái sẵn sàng để gán (Trạng thái: " + room.getTrangThaiPhong() + ")!");
        }

        // Lưu hoặc cập nhật chi tiết đặt phòng
        List<ChiTietDatPhong> existingCtdp = chiTietDatPhongRepo.findByDonDatPhong(booking);
        if (existingCtdp != null && !existingCtdp.isEmpty()) {
            chiTietDatPhongRepo.deleteAll(existingCtdp);
        }
        ChiTietDatPhong ctdp = new ChiTietDatPhong(booking, room, room.getSoPhong());
        chiTietDatPhongRepo.save(ctdp);

        // Chuyển trạng thái đơn thành CONFIRMED
        booking.setTrangThaiDon("CONFIRMED");

        // Gán nhân viên tiếp nhận
        if (maNhanVien != null && !maNhanVien.isBlank()) {
            nhanVienRepo.findById(maNhanVien).ifPresent(booking::setNhanVien);
        }

        // Sinh QR Code nếu chưa có
        if (booking.getQrCode() == null || booking.getQrCode().isBlank()) {
            try {
                String qrBase64 = qrCodeUtil.generateQRCodeImageBase64("MELIA-CHECKIN-" + booking.getMaDatPhong());
                booking.setQrCode(qrBase64);
            } catch (Exception e) {
                log.warn("Không thể sinh QR Code cho đơn {}: {}", booking.getMaDatPhong(), e.getMessage());
            }
        }

        // Xử lý tiền cọc / thanh toán nếu có
        if (request.getTienCoc() != null && request.getTienCoc().compareTo(BigDecimal.ZERO) > 0) {
            hoaDonRepo.findByDonDatPhong(booking).ifPresent(hoaDon -> {
                BigDecimal currentPaid = hoaDon.getDaThanhToan() != null ? hoaDon.getDaThanhToan() : BigDecimal.ZERO;
                hoaDon.setDaThanhToan(currentPaid.add(request.getTienCoc()));
                if (hoaDon.getDaThanhToan().compareTo(hoaDon.getTongTienThanhToan()) >= 0) {
                    hoaDon.setTrangThaiThanhToan("PAID");
                } else {
                    hoaDon.setTrangThaiThanhToan("DEPOSITED");
                }
                hoaDonRepo.save(hoaDon);
            });
        }

        if (request.getGhiChu() != null && !request.getGhiChu().isBlank()) {
            String currentYc = booking.getYeuCauDb() != null ? booking.getYeuCauDb() + " | " : "";
            booking.setYeuCauDb(currentYc + "[Lễ tân ghi chú]: " + request.getGhiChu());
        }

        bookingRepo.save(booking);
        log.info("Lễ tân {} đã xác nhận & gán phòng {} cho đơn {}", maNhanVien, room.getSoPhong(), booking.getMaDatPhong());

        return mapToBookingResponse(booking);
    }

    @Override
    @Transactional
    public void processCheckIn(String maDatPhong, String maPhongCuThe, String maNhanVien) {
        DonDatPhong booking = bookingRepo.findById(maDatPhong)
                .orElseThrow(() -> new BusinessException("Đơn đặt phòng không tồn tại: " + maDatPhong));

        if (!"CONFIRMED".equalsIgnoreCase(booking.getTrangThaiDon()) && !"PENDING".equalsIgnoreCase(booking.getTrangThaiDon())) {
            throw new BusinessException("Đơn phòng không thể Check-in (Trạng thái hiện tại: " + booking.getTrangThaiDon() + ", yêu cầu CONFIRMED hoặc PENDING)");
        }

        Phong room = null;
        if (maPhongCuThe != null && !maPhongCuThe.isBlank()) {
            room = phongRepo.findById(maPhongCuThe)
                    .orElseThrow(() -> new BusinessException("Phòng không tồn tại: " + maPhongCuThe));
        } else {
            // Lấy phòng đã gán trước đó từ ChiTietDatPhong
            List<ChiTietDatPhong> ctdpList = chiTietDatPhongRepo.findByDonDatPhong(booking);
            if (ctdpList != null && !ctdpList.isEmpty()) {
                room = ctdpList.get(0).getPhong();
            }
        }

        if (room == null) {
            throw new BusinessException("Đơn phòng chưa được gán số phòng cụ thể. Vui lòng chọn phòng để Check-in!");
        }

        // Kiểm tra loại phòng phải khớp chính xác loại phòng khách đã đặt
        if (booking.getLoaiPhong() != null && !room.getLoaiPhong().getMaLoaiPhong().equals(booking.getLoaiPhong().getMaLoaiPhong())) {
            throw new BusinessException("Phòng gán (" + room.getSoPhong() + " - " + room.getLoaiPhong().getTenLoaiPhong()
                    + ") không khớp với loại phòng khách đã đặt (" + booking.getLoaiPhong().getTenLoaiPhong() + ")!");
        }

        if (!"AVAILABLE".equalsIgnoreCase(room.getTrangThaiPhong()) && !"OCCUPIED".equalsIgnoreCase(room.getTrangThaiPhong())) {
            throw new BusinessException("Phòng " + room.getSoPhong() + " hiện không ở trạng thái sẵn sàng để nhận khách (Trạng thái: " + room.getTrangThaiPhong() + ")!");
        }

        // Đảm bảo ChiTietDatPhong được lưu
        List<ChiTietDatPhong> ctdpList = chiTietDatPhongRepo.findByDonDatPhong(booking);
        if (ctdpList == null || ctdpList.isEmpty()) {
            ChiTietDatPhong ctdp = new ChiTietDatPhong(booking, room, room.getSoPhong());
            chiTietDatPhongRepo.save(ctdp);
        }

        room.setTrangThaiPhong("OCCUPIED");
        booking.setTrangThaiDon("CHECKED_IN");
        if (maNhanVien != null && !maNhanVien.isBlank()) {
            nhanVienRepo.findById(maNhanVien).ifPresent(booking::setNhanVien);
        }

        phongRepo.save(room);
        bookingRepo.save(booking);

        log.info("Lễ tân {} đã làm thủ tục Check-in cho đơn {} vào phòng {}", maNhanVien, maDatPhong, room.getSoPhong());
    }

    @Override
    @Transactional
    public InvoiceResponse processCheckOut(String maDatPhong, BigDecimal phuThuPhatSinh, String phuongThucThanhToan) {
        DonDatPhong booking = bookingRepo.findById(maDatPhong)
                .orElseThrow(() -> new BusinessException("Đơn đặt phòng không tồn tại: " + maDatPhong));

        if (!"CHECKED_IN".equals(booking.getTrangThaiDon())) {
            throw new BusinessException("Đơn phòng chưa ở trạng thái Check-in nên không thể làm thủ tục Check-out (Trạng thái: " + booking.getTrangThaiDon() + ")!");
        }

        List<ChiTietDichVu> dsDichVu = chiTietDichVuRepo.findByDonDatPhong(booking);
        BigDecimal tongTienDichVu = dsDichVu.stream()
                .map(ChiTietDichVu::getThanhTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        HoaDon hoaDon = hoaDonRepo.findByDonDatPhong(booking)
                .orElseThrow(() -> new BusinessException("Không tìm thấy hóa đơn cho đơn đặt phòng: " + maDatPhong));

        BigDecimal phuThu = (phuThuPhatSinh != null && phuThuPhatSinh.compareTo(BigDecimal.ZERO) > 0) ? phuThuPhatSinh : BigDecimal.ZERO;
        hoaDon.setTienDichVu(tongTienDichVu);
        hoaDon.setPhuThu(phuThu);

        BigDecimal giamGia = hoaDon.getGiamGia() != null ? hoaDon.getGiamGia() : BigDecimal.ZERO;
        BigDecimal tongThanhToanMoi = hoaDon.getTienPhong()
                .add(tongTienDichVu)
                .add(phuThu)
                .subtract(giamGia);

        if (tongThanhToanMoi.compareTo(BigDecimal.ZERO) < 0) {
            tongThanhToanMoi = BigDecimal.ZERO;
        }

        // Tính toán trừ đi số tiền khách đã thanh toán trước (VD: qua VNPay lúc đặt phòng)
        BigDecimal daThanhToan = hoaDon.getDaThanhToan() != null ? hoaDon.getDaThanhToan() : BigDecimal.ZERO;
        BigDecimal conPhaiTra = tongThanhToanMoi.subtract(daThanhToan);
        if (conPhaiTra.compareTo(BigDecimal.ZERO) < 0) {
            conPhaiTra = BigDecimal.ZERO;
        }

        hoaDon.setTongTienThanhToan(tongThanhToanMoi);
        hoaDon.setNgayLapHoaDon(LocalDateTime.now());

        // Khi lễ tân thu đủ phần tiền còn lại tại quầy, cập nhật da_thanh_toan = tong_moi
        hoaDon.setDaThanhToan(tongThanhToanMoi);
        hoaDon.setTrangThaiThanhToan("PAID");
        if (phuongThucThanhToan != null && !phuongThucThanhToan.isBlank()) {
            hoaDon.setPhuongThucThanhToan(phuongThucThanhToan);
        }
        hoaDonRepo.save(hoaDon);

        // Chuyển tất cả phòng vật lý của đơn sang DIRTY để buồng phòng dọn dẹp
        List<ChiTietDatPhong> chiTietList = chiTietDatPhongRepo.findByDonDatPhong(booking);
        for (ChiTietDatPhong ctdp : chiTietList) {
            Phong phong = ctdp.getPhong();
            phong.setTrangThaiPhong("DIRTY");
            phongRepo.save(phong);
        }

        booking.setTrangThaiDon("CHECKED_OUT");
        bookingRepo.save(booking);

        log.info("Lễ tân hoàn tất Check-out cho đơn {}. Tổng tiền mới: {}, Đã trả trước: {}, Thu thêm lúc check-out: {}",
                maDatPhong, tongThanhToanMoi, daThanhToan, conPhaiTra);

        return InvoiceResponse.builder()
                .invoiceId(hoaDon.getMaHoaDon())
                .bookingId(booking.getMaDatPhong())
                .issueDate(hoaDon.getNgayLapHoaDon())
                .roomCharge(hoaDon.getTienPhong())
                .serviceCharge(hoaDon.getTienDichVu())
                .surcharge(hoaDon.getPhuThu())
                .discount(hoaDon.getGiamGia())
                .finalAmount(hoaDon.getTongTienThanhToan())
                .daThanhToan(daThanhToan)
                .conPhaiTra(conPhaiTra)
                .paymentMethod(hoaDon.getPhuongThucThanhToan())
                .paymentStatus(hoaDon.getTrangThaiThanhToan())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getBookingForReception(String keyword) {
        DonDatPhong booking = bookingRepo.findBookingForReception(keyword)
                .orElseThrow(() -> new BusinessException("Không tìm thấy đơn đặt phòng với từ khóa: " + keyword));

        return mapToBookingResponse(booking);
    }

    private BookingResponse mapToBookingResponse(DonDatPhong booking) {
        long soDem = ChronoUnit.DAYS.between(booking.getThoiGianNhanPhong(), booking.getThoiGianTraPhong());
        String maPhong = null;
        String soPhong = null;

        List<ChiTietDatPhong> chiTietList = chiTietDatPhongRepo.findByDonDatPhong(booking);
        if (chiTietList != null && !chiTietList.isEmpty()) {
            ChiTietDatPhong ctdp = chiTietList.get(0);
            maPhong = ctdp.getPhong() != null ? ctdp.getPhong().getMaPhong() : null;
            soPhong = ctdp.getSoPhong();
        }

        return BookingResponse.builder()
                .maDatPhong(booking.getMaDatPhong())
                .ngayDat(booking.getNgayDat())
                .thoiGianNhanPhong(booking.getThoiGianNhanPhong())
                .thoiGianTraPhong(booking.getThoiGianTraPhong())
                .soDem(soDem)
                .maLoaiPhong(booking.getLoaiPhong() != null ? booking.getLoaiPhong().getMaLoaiPhong() : null)
                .tenLoaiPhong(booking.getLoaiPhong() != null ? booking.getLoaiPhong().getTenLoaiPhong() : null)
                .tongTien(booking.getTongTien())
                .trangThaiDon(booking.getTrangThaiDon())
                .yeuCauDb(booking.getYeuCauDb())
                .maKhachHang(booking.getKhachHang() != null ? booking.getKhachHang().getMaKhachHang() : null)
                .tenKhachHang(booking.getKhachHang() != null ? booking.getKhachHang().getHoTen() : null)
                .soDienThoaiKhachHang(booking.getKhachHang() != null ? booking.getKhachHang().getSoDienThoai() : null)
                .emailKhachHang(booking.getKhachHang() != null ? booking.getKhachHang().getEmail() : null)
                .maPhieuGg(booking.getPhieuGiamGia() != null ? booking.getPhieuGiamGia().getMaPhieuGg() : null)
                .maPhong(maPhong)
                .soPhong(soPhong)
                .qrCode(booking.getQrCode())
                .build();
    }
}
