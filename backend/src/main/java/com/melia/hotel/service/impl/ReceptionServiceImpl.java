package com.melia.hotel.service.impl;

import com.melia.hotel.dto.response.BookingResponse;
import com.melia.hotel.dto.response.InvoiceResponse;
import com.melia.hotel.entity.*;
import com.melia.hotel.exception.BusinessException;
import com.melia.hotel.repository.*;
import com.melia.hotel.service.ReceptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ReceptionServiceImpl implements ReceptionService {

    private static final Logger log = LoggerFactory.getLogger(ReceptionServiceImpl.class);

    private final BookingRepository bookingRepo;
    private final RoomRepository phongRepo;
    private final ChiTietDatPhongRepository chiTietDatPhongRepo;
    private final ChiTietDichVuRepository chiTietDichVuRepo;
    private final NhanVienRepository nhanVienRepo;
    private final HoaDonRepository hoaDonRepo;

    public ReceptionServiceImpl(BookingRepository bookingRepo,
                                RoomRepository phongRepo,
                                ChiTietDatPhongRepository chiTietDatPhongRepo,
                                ChiTietDichVuRepository chiTietDichVuRepo,
                                NhanVienRepository nhanVienRepo,
                                HoaDonRepository hoaDonRepo) {
        this.bookingRepo = bookingRepo;
        this.phongRepo = phongRepo;
        this.chiTietDatPhongRepo = chiTietDatPhongRepo;
        this.chiTietDichVuRepo = chiTietDichVuRepo;
        this.nhanVienRepo = nhanVienRepo;
        this.hoaDonRepo = hoaDonRepo;
    }

    @Override
    @Transactional
    public void processCheckIn(String maDatPhong, String maPhongCuThe, String maNhanVien) {
        DonDatPhong booking = bookingRepo.findById(maDatPhong)
                .orElseThrow(() -> new BusinessException("Đơn đặt phòng không tồn tại: " + maDatPhong));

        if (!"CONFIRMED".equals(booking.getTrangThaiDon())) {
            throw new BusinessException("Đơn phòng chưa đủ điều kiện Check-in (Trạng thái hiện tại: " + booking.getTrangThaiDon() + ", yêu cầu CONFIRMED)");
        }

        Phong room = phongRepo.findById(maPhongCuThe)
                .orElseThrow(() -> new BusinessException("Phòng không tồn tại: " + maPhongCuThe));

        // Kiểm tra phòng được gán đúng loại phòng khách đã đặt
        if (booking.getLoaiPhong() != null && !room.getLoaiPhong().getMaLoaiPhong().equals(booking.getLoaiPhong().getMaLoaiPhong())) {
            throw new BusinessException("Phòng gán (" + room.getSoPhong() + " - " + room.getLoaiPhong().getTenLoaiPhong()
                    + ") không khớp với loại phòng khách đã đặt (" + booking.getLoaiPhong().getTenLoaiPhong() + ")!");
        }

        if (!"AVAILABLE".equals(room.getTrangThaiPhong())) {
            throw new BusinessException("Phòng " + room.getSoPhong() + " hiện không ở trạng thái sẵn sàng để nhận khách (Trạng thái: " + room.getTrangThaiPhong() + ")!");
        }

        ChiTietDatPhong ctdp = new ChiTietDatPhong(booking, room, room.getSoPhong());
        chiTietDatPhongRepo.save(ctdp);

        room.setTrangThaiPhong("OCCUPIED");
        booking.setTrangThaiDon("CHECKED_IN");
        if (maNhanVien != null && !maNhanVien.isBlank()) {
            booking.setNhanVien(nhanVienRepo.findById(maNhanVien).orElse(null));
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

        long soDem = ChronoUnit.DAYS.between(booking.getThoiGianNhanPhong(), booking.getThoiGianTraPhong());
        String maPhong = null;
        String soPhong = null;

        if (booking.getDanhSachChiTietDatPhong() != null && !booking.getDanhSachChiTietDatPhong().isEmpty()) {
            ChiTietDatPhong ctdp = booking.getDanhSachChiTietDatPhong().get(0);
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
