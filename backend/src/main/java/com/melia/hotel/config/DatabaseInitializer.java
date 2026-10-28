package com.melia.hotel.config;

import com.melia.hotel.entity.*;
import com.melia.hotel.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseInitializer.class);

    private final NhanVienRepository nhanVienRepo;
    private final KhachHangRepository khachHangRepo;
    private final LoaiPhongRepository loaiPhongRepo;
    private final RoomRepository phongRepo;
    private final CouponRepository phieuGiamGiaRepo;
    private final DichVuRepository dichVuRepo;
    private final BookingRepository donDatPhongRepo;
    private final ChiTietDatPhongRepository ctdpRepo;
    private final HoaDonRepository hoaDonRepo;
    private final DanhGiaRepository danhGiaRepo;
    private final PasswordEncoder passwordEncoder;

    public DatabaseInitializer(NhanVienRepository nhanVienRepo,
                               KhachHangRepository khachHangRepo,
                               LoaiPhongRepository loaiPhongRepo,
                               RoomRepository phongRepo,
                               CouponRepository phieuGiamGiaRepo,
                               DichVuRepository dichVuRepo,
                               BookingRepository donDatPhongRepo,
                               ChiTietDatPhongRepository ctdpRepo,
                               HoaDonRepository hoaDonRepo,
                               DanhGiaRepository danhGiaRepo,
                               PasswordEncoder passwordEncoder) {
        this.nhanVienRepo = nhanVienRepo;
        this.khachHangRepo = khachHangRepo;
        this.loaiPhongRepo = loaiPhongRepo;
        this.phongRepo = phongRepo;
        this.phieuGiamGiaRepo = phieuGiamGiaRepo;
        this.dichVuRepo = dichVuRepo;
        this.donDatPhongRepo = donDatPhongRepo;
        this.ctdpRepo = ctdpRepo;
        this.hoaDonRepo = hoaDonRepo;
        this.danhGiaRepo = danhGiaRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (nhanVienRepo.count() > 0) {
            log.info("Cơ sở dữ liệu đã có dữ liệu khởi tạo, bỏ qua bước nạp seed data.");
            return;
        }

        log.info("Đang khởi tạo dữ liệu mẫu chuẩn cho Khách sạn Meliá Hà Nội...");

        // 1. Nhân viên
        createNhanVien("NV001", "admin", "123456", "Nguyễn Văn An", "0901234567", "001090000001", "ROLE_ADMIN");
        NhanVien nv2 = createNhanVien("NV002", "reception01", "123456", "Trần Thị Bình", "0902345678", "001090000002", "ROLE_RECEPTIONIST");
        createNhanVien("NV003", "housekeeping01", "123456", "Lê Văn Cường", "0903456789", "001090000003", "ROLE_HOUSEKEEPING");
        createNhanVien("NV004", "accountant01", "123456", "Phạm Thị Dung", "0904567890", "001090000004", "ROLE_ACCOUNTANT");

        // 2. Khách hàng
        KhachHang kh1 = khachHangRepo.save(KhachHang.builder()
                .maKhachHang("KH001").hoTen("Nguyễn Văn An").ngaySinh(LocalDate.of(1990, 5, 15))
                .soDienThoai("0912345678").email("an.nguyen@gmail.com")
                .matKhau(passwordEncoder.encode("123456")).soCccd("001202000001").trangThaiTk("ACTIVE").build());

        KhachHang kh2 = khachHangRepo.save(KhachHang.builder()
                .maKhachHang("KH002").hoTen("Trần Thị Bình").ngaySinh(LocalDate.of(1995, 8, 20))
                .soDienThoai("0987654321").email("binh.tran@gmail.com")
                .matKhau(passwordEncoder.encode("123456")).soCccd("001202000002").trangThaiTk("ACTIVE").build());

        // 3. Loại phòng
        LoaiPhong lp1 = createLoaiPhong("LP01", "Phòng Deluxe King", new BigDecimal("2500000.00"), 2,
                "Phòng Deluxe 35m2 với giường King size, bồn tắm nằm đá cẩm thạch",
                "https://images.unsplash.com/photo-1618773928121-c32242e63f39?w=800&q=80",
                "Trải nghiệm không gian nghỉ dưỡng thanh lịch tại trung tâm thủ đô. Đầy đủ tiện nghi cao cấp, ban công ngắm nhìn phố cổ Hà Nội và phục vụ trà chiều miễn phí.");

        LoaiPhong lp2 = createLoaiPhong("LP02", "Phòng Premium Executive", new BigDecimal("3500000.00"), 2,
                "Phòng Premium tầng cao 45m2, quyền vào Melia Executive Lounge",
                "https://images.unsplash.com/photo-1590490360182-c33d57733427?w=800&q=80",
                "Đặc quyền sử dụng phòng chờ VIP, bữa sáng tự chọn quốc tế, cocktail buổi tối thượng hạng cùng dịch vụ check-in riêng biệt.");

        LoaiPhong lp3 = createLoaiPhong("LP03", "Phòng Grand Suite", new BigDecimal("5000000.00"), 4,
                "Căn hộ Suite sang trọng 75m2 với phòng khách và phòng ngủ riêng",
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=800&q=80",
                "Không gian nghỉ dưỡng gia đình hoàn hảo với tầm nhìn ôm trọn Hồ Tây, nội thất gỗ óc chó tự nhiên và phòng tắm jacuzzi.");

        LoaiPhong lp4 = createLoaiPhong("LP04", "Phòng Tổng Thống (Presidential Suite)", new BigDecimal("12000000.00"), 4,
                "Biệt thự trên không 150m2, quản gia riêng 24/7 và quầy bar độc quyền",
                "https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=800&q=80",
                "Đỉnh cao của sự xa hoa và riêng tư dành riêng cho nguyên thủ quốc gia và giới thượng lưu. Phòng ăn 10 khách và hệ thống âm thanh vòm Bang & Olufsen.");

        // 4. Phòng vật lý
        createPhong("P101", "101", 1, "AVAILABLE", lp1);
        createPhong("P102", "102", 1, "AVAILABLE", lp1);
        createPhong("P103", "103", 1, "AVAILABLE", lp1);
        createPhong("P104", "104", 1, "DIRTY", lp1);

        createPhong("P201", "201", 2, "AVAILABLE", lp2);
        Phong p202 = createPhong("P202", "202", 2, "OCCUPIED", lp2);
        createPhong("P203", "203", 2, "AVAILABLE", lp2);

        createPhong("P301", "301", 3, "AVAILABLE", lp3);
        createPhong("P302", "302", 3, "MAINTENANCE", lp3);

        createPhong("P401", "401", 4, "AVAILABLE", lp4);

        // 5. Phiếu giảm giá
        PhieuGiamGia pgg1 = createPhieuGiamGia("MELIAWELCOME", new BigDecimal("200000.00"), "FIXED", 100);
        createPhieuGiamGia("SUMMER2026", new BigDecimal("10.00"), "PERCENT", 50);
        createPhieuGiamGia("VIPMELIA", new BigDecimal("500000.00"), "FIXED", 30);

        // 6. Dịch vụ
        createDichVu("DV001", "Nước khoáng cao cấp Evian 500ml", new BigDecimal("60000.00"), "Nước khoáng thiên nhiên nhập khẩu từ dãy Alps");
        createDichVu("DV002", "Bia lon Heineken / Nước ngọt", new BigDecimal("45000.00"), "Đồ uống ướp lạnh trong minibar");
        createDichVu("DV003", "Hạt điều sấy / Snack cao cấp", new BigDecimal("50000.00"), "Đồ ăn nhẹ minibar thượng hạng");
        createDichVu("DV004", "Rượu vang đỏ Chianti Riserva (Chai 750ml)", new BigDecimal("650000.00"), "Rượu vang Ý hảo hạng phục vụ tại phòng");
        createDichVu("DV005", "Dịch vụ giặt ủi lấy nhanh 24h", new BigDecimal("120000.00"), "Giặt hấp và ủi phẳng trang phục");
        createDichVu("DV006", "Liệu trình Massage & Spa toàn thân 60p", new BigDecimal("850000.00"), "Thư giãn tại Melia YHI Spa tiêu chuẩn 5 sao");

        // 7. Đơn đặt phòng mẫu & Hóa đơn
        DonDatPhong dp1 = donDatPhongRepo.save(DonDatPhong.builder()
                .maDatPhong("DP2026102801")
                .ngayDat(LocalDateTime.now().minusDays(1))
                .thoiGianNhanPhong(LocalDate.now())
                .thoiGianTraPhong(LocalDate.now().plusDays(2))
                .tongTien(new BigDecimal("4800000.00"))
                .trangThaiDon("CONFIRMED")
                .yeuCauDb("Phòng tầng cao, view thoáng đãng không hút thuốc")
                .khachHang(kh1)
                .loaiPhong(lp1)
                .phieuGiamGia(pgg1)
                .qrCode("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNk+A8AAQUBAScY42YAAAAASUVORK5CYII=")
                .build());

        hoaDonRepo.save(HoaDon.builder()
                .maHoaDon("HD2026102801")
                .ngayLapHoaDon(LocalDateTime.now().minusDays(1))
                .donDatPhong(dp1)
                .tienPhong(new BigDecimal("5000000.00"))
                .tienDichVu(BigDecimal.ZERO)
                .phuThu(BigDecimal.ZERO)
                .giamGia(new BigDecimal("200000.00"))
                .tongTienThanhToan(new BigDecimal("4800000.00"))
                .daThanhToan(new BigDecimal("4800000.00"))
                .phuongThucThanhToan("VNPAY")
                .trangThaiThanhToan("PAID")
                .build());

        DonDatPhong dp2 = donDatPhongRepo.save(DonDatPhong.builder()
                .maDatPhong("DP2026102802")
                .ngayDat(LocalDateTime.now().minusDays(2))
                .thoiGianNhanPhong(LocalDate.now().minusDays(1))
                .thoiGianTraPhong(LocalDate.now().plusDays(1))
                .tongTien(new BigDecimal("7000000.00"))
                .trangThaiDon("CHECKED_IN")
                .yeuCauDb("Chuẩn bị thêm 01 bộ áo choàng tắm")
                .khachHang(kh2)
                .loaiPhong(lp2)
                .nhanVien(nv2)
                .build());

        ctdpRepo.save(new ChiTietDatPhong(dp2, p202, "202"));

        hoaDonRepo.save(HoaDon.builder()
                .maHoaDon("HD2026102802")
                .ngayLapHoaDon(LocalDateTime.now().minusDays(2))
                .donDatPhong(dp2)
                .tienPhong(new BigDecimal("7000000.00"))
                .tienDichVu(BigDecimal.ZERO)
                .phuThu(BigDecimal.ZERO)
                .giamGia(BigDecimal.ZERO)
                .tongTienThanhToan(new BigDecimal("7000000.00"))
                .daThanhToan(new BigDecimal("3500000.00"))
                .phuongThucThanhToan("VNPAY")
                .trangThaiThanhToan("UNPAID")
                .build());

        // 8. Đánh giá mẫu
        danhGiaRepo.save(DanhGia.builder()
                .maDanhGia("DG001")
                .donDatPhong(dp1)
                .khachHang(kh1)
                .soSaoDanhGia(5)
                .noiDungNx("Trải nghiệm nghỉ dưỡng tuyệt vời! Dịch vụ 5 sao chuẩn quốc tế, phòng ốc sạch sẽ tinh tươm và nhân viên phục vụ tận tâm.")
                .thoiGianDanhGia(LocalDateTime.now().minusHours(5))
                .build());

        danhGiaRepo.save(DanhGia.builder()
                .maDanhGia("DG002")
                .donDatPhong(dp2)
                .khachHang(kh2)
                .soSaoDanhGia(5)
                .noiDungNx("Khách sạn Meliá Hà Nội luôn là lựa chọn hàng đầu của gia đình mình. Ẩm thực xuất sắc, hồ bơi và spa rất thư giãn.")
                .thoiGianDanhGia(LocalDateTime.now().minusHours(2))
                .build());

        log.info("Khởi tạo dữ liệu mẫu thành công 100%!");
    }

    private NhanVien createNhanVien(String ma, String user, String pass, String ten, String sdt, String cccd, String role) {
        NhanVien nv = new NhanVien();
        nv.setMaNhanVien(ma);
        nv.setTenDangNhap(user);
        nv.setMatKhau(passwordEncoder.encode(pass));
        nv.setHoTen(ten);
        nv.setSoDt(sdt);
        nv.setSoCccd(cccd);
        nv.setQuyenHan(role);
        return nhanVienRepo.save(nv);
    }

    private LoaiPhong createLoaiPhong(String ma, String ten, BigDecimal gia, Integer sucChua, String info, String img, String mota) {
        LoaiPhong lp = new LoaiPhong();
        lp.setMaLoaiPhong(ma);
        lp.setTenLoaiPhong(ten);
        lp.setGiaPhongCoBan(gia);
        lp.setSucChua(sucChua);
        lp.setThongTin(info);
        lp.setHinhAnhMinhHoa(img);
        lp.setMoTa(mota);
        return loaiPhongRepo.save(lp);
    }

    private Phong createPhong(String ma, String soPhong, Integer tang, String trangThai, LoaiPhong lp) {
        Phong p = new Phong();
        p.setMaPhong(ma);
        p.setSoPhong(soPhong);
        p.setTang(tang);
        p.setTrangThaiPhong(trangThai);
        p.setLoaiPhong(lp);
        return phongRepo.save(p);
    }

    private PhieuGiamGia createPhieuGiamGia(String ma, BigDecimal giaTri, String loai, Integer sl) {
        PhieuGiamGia p = new PhieuGiamGia();
        p.setMaPhieuGg(ma);
        p.setGiaTriGiam(giaTri);
        p.setLoaiGiam(loai);
        p.setThoiGianBatDau(LocalDateTime.of(2026, 1, 1, 0, 0));
        p.setThoiGianKetThuc(LocalDateTime.of(2026, 12, 31, 23, 59));
        p.setSoLuongPhieu(sl);
        return phieuGiamGiaRepo.save(p);
    }

    private void createDichVu(String ma, String ten, BigDecimal gia, String mota) {
        DichVu dv = new DichVu();
        dv.setMaDichVu(ma);
        dv.setTenDv(ten);
        dv.setGiaDv(gia);
        dv.setMoTaDv(mota);
        dichVuRepo.save(dv);
    }
}
