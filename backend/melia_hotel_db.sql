-- =====================================================================================
-- HỆ THỐNG ĐẶT PHÒNG VÀ QUẢN LÝ KHÁCH SẠN MELIÁ HÀ NỘI
-- Database: melia_hotel_db
-- Thiết kế: Nhóm 11 - Thực tập cơ sở ngành
-- =====================================================================================

CREATE DATABASE IF NOT EXISTS melia_hotel_db 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE melia_hotel_db;

-- Xóa bảng cũ nếu tồn tại theo thứ tự ràng buộc khóa ngoại
DROP TABLE IF EXISTS danh_gia;
DROP TABLE IF EXISTS hoa_don;
DROP TABLE IF EXISTS chi_tiet_dich_vu;
DROP TABLE IF EXISTS chi_tiet_dat_phong;
DROP TABLE IF EXISTS don_dat_phong;
DROP TABLE IF EXISTS dich_vu;
DROP TABLE IF EXISTS phieu_giam_gia;
DROP TABLE IF EXISTS phong;
DROP TABLE IF EXISTS loai_phong;
DROP TABLE IF EXISTS nhan_vien;
DROP TABLE IF EXISTS khach_hang;

-- -------------------------------------------------------------------------------------
-- 1. Bảng Khách hàng (khach_hang)
-- Lưu trữ thông tin tài khoản và định danh khách hàng đặt phòng trực tuyến hoặc tại quầy
-- -------------------------------------------------------------------------------------
CREATE TABLE khach_hang (
    ma_khach_hang VARCHAR(20) NOT NULL,
    ho_ten VARCHAR(100) NOT NULL,
    ngay_sinh DATE NULL,
    so_dien_thoai VARCHAR(15) NOT NULL,
    email VARCHAR(100) NOT NULL,
    mat_khau VARCHAR(255) NOT NULL,
    so_cccd VARCHAR(20) NULL,
    trang_thai_tk VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, LOCKED
    PRIMARY KEY (ma_khach_hang),
    UNIQUE KEY uq_khachhang_sdt (so_dien_thoai),
    UNIQUE KEY uq_khachhang_email (email),
    UNIQUE KEY uq_khachhang_cccd (so_cccd)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------------------------------------
-- 2. Bảng Nhân viên (nhan_vien)
-- Lưu trữ tài khoản và vai trò của cán bộ nhân viên thuộc khách sạn Meliá Hà Nội
-- -------------------------------------------------------------------------------------
CREATE TABLE nhan_vien (
    ma_nhan_vien VARCHAR(20) NOT NULL,
    ten_dang_nhap VARCHAR(50) NOT NULL,
    mat_khau VARCHAR(255) NOT NULL,
    ho_ten VARCHAR(100) NOT NULL,
    so_dt VARCHAR(15) NULL,
    so_cccd VARCHAR(20) NULL,
    quyen_han VARCHAR(30) NOT NULL, -- ROLE_ADMIN, ROLE_RECEPTIONIST, ROLE_HOUSEKEEPING, ROLE_ACCOUNTANT
    PRIMARY KEY (ma_nhan_vien),
    UNIQUE KEY uq_nhanvien_username (ten_dang_nhap),
    UNIQUE KEY uq_nhanvien_cccd (so_cccd)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------------------------------------
-- 3. Bảng Loại phòng (loai_phong)
-- Danh mục loại phòng tại Meliá Hà Nội (Deluxe, Premium, Suite...) kèm đơn giá niêm yết
-- -------------------------------------------------------------------------------------
CREATE TABLE loai_phong (
    ma_loai_phong VARCHAR(20) NOT NULL,
    ten_loai_phong VARCHAR(100) NOT NULL,
    gia_phong_co_ban DECIMAL(12, 2) NOT NULL,
    suc_chua INT NOT NULL DEFAULT 2,
    thong_tin TEXT NULL,
    hinh_anh_minh_hoa VARCHAR(255) NULL,
    mo_ta TEXT NULL,
    PRIMARY KEY (ma_loai_phong)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------------------------------------
-- 4. Bảng Phòng thực tế (phong)
-- Quản lý phòng vật lý cụ thể, số phòng, tầng lầu và trạng thái sẵn sàng phục vụ
-- -------------------------------------------------------------------------------------
CREATE TABLE phong (
    ma_phong VARCHAR(20) NOT NULL,
    so_phong VARCHAR(10) NOT NULL,
    tang INT NOT NULL,
    trang_thai_phong VARCHAR(30) NOT NULL DEFAULT 'AVAILABLE', -- AVAILABLE, OCCUPIED, DIRTY, MAINTENANCE
    ma_loai_phong VARCHAR(20) NOT NULL,
    PRIMARY KEY (ma_phong),
    UNIQUE KEY uq_phong_sophong (so_phong),
    KEY idx_phong_trangthai (trang_thai_phong),
    KEY idx_phong_loaiphong (ma_loai_phong),
    CONSTRAINT fk_phong_loai_phong FOREIGN KEY (ma_loai_phong) 
        REFERENCES loai_phong(ma_loai_phong) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------------------------------------
-- 5. Bảng Phiếu giảm giá (phieu_giam_gia)
-- Quản lý các voucher, chiến dịch khuyến mại giá phòng tại khách sạn
-- -------------------------------------------------------------------------------------
CREATE TABLE phieu_giam_gia (
    ma_phieu_gg VARCHAR(30) NOT NULL,
    gia_tri_giam DECIMAL(12, 2) NOT NULL,
    loai_giam VARCHAR(20) NOT NULL DEFAULT 'FIXED', -- PERCENT (%), FIXED (VND)
    thoi_gian_bat_dau DATETIME NOT NULL,
    thoi_gian_ket_thuc DATETIME NOT NULL,
    so_luong_phieu INT NOT NULL DEFAULT 0,
    PRIMARY KEY (ma_phieu_gg)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------------------------------------
-- 6. Bảng Dịch vụ (dich_vu)
-- Danh mục tiện ích đi kèm: Minibar, Spa trị liệu, Ăn uống, Giặt ủi...
-- -------------------------------------------------------------------------------------
CREATE TABLE dich_vu (
    ma_dich_vu VARCHAR(20) NOT NULL,
    ten_dv VARCHAR(100) NOT NULL,
    gia_dv DECIMAL(12, 2) NOT NULL,
    mo_ta_dv TEXT NULL,
    PRIMARY KEY (ma_dich_vu)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------------------------------------
-- 7. Bảng Đơn đặt phòng (don_dat_phong)
-- Thực thể trung tâm lưu trữ thông tin giao dịch đặt phòng của khách hàng
-- ma_loai_phong lưu loại phòng khách chọn đặt trước khi check-in gán phòng vật lý
-- -------------------------------------------------------------------------------------
CREATE TABLE don_dat_phong (
    ma_dat_phong VARCHAR(30) NOT NULL,
    ngay_dat DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    thoi_gian_nhan_phong DATE NOT NULL,
    thoi_gian_tra_phong DATE NOT NULL,
    tong_tien DECIMAL(14, 2) NOT NULL,
    trang_thai_don VARCHAR(30) NOT NULL DEFAULT 'PENDING', -- PENDING, CONFIRMED, CHECKED_IN, CHECKED_OUT, CANCELLED
    yeu_cau_db TEXT NULL,
    ma_khach_hang VARCHAR(20) NOT NULL,
    ma_loai_phong VARCHAR(20) NOT NULL,
    ma_phieu_gg VARCHAR(30) NULL,
    ma_nhan_vien VARCHAR(20) NULL,
    qr_code MEDIUMTEXT NULL,
    PRIMARY KEY (ma_dat_phong),
    KEY idx_datphong_ngaynhan_ngaytra (thoi_gian_nhan_phong, thoi_gian_tra_phong),
    KEY idx_datphong_trangthai (trang_thai_don),
    KEY idx_datphong_khachhang (ma_khach_hang),
    KEY idx_datphong_loaiphong (ma_loai_phong),
    CONSTRAINT fk_datphong_khachhang FOREIGN KEY (ma_khach_hang) 
        REFERENCES khach_hang(ma_khach_hang) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_datphong_loaiphong FOREIGN KEY (ma_loai_phong) 
        REFERENCES loai_phong(ma_loai_phong) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_datphong_phieugg FOREIGN KEY (ma_phieu_gg) 
        REFERENCES phieu_giam_gia(ma_phieu_gg) ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT fk_datphong_nhanvien FOREIGN KEY (ma_nhan_vien) 
        REFERENCES nhan_vien(ma_nhan_vien) ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------------------------------------
-- 8. Chi tiết đặt phòng (chi_tiet_dat_phong)
-- Liên kết đơn đặt phòng với từng phòng cụ thể được gán tại thời điểm Check-in
-- -------------------------------------------------------------------------------------
CREATE TABLE chi_tiet_dat_phong (
    ma_dat_phong VARCHAR(30) NOT NULL,
    ma_phong VARCHAR(20) NOT NULL,
    so_phong VARCHAR(10) NULL,
    gia_tai_thoi_diem_dat DECIMAL(12, 2) NULL,
    PRIMARY KEY (ma_dat_phong, ma_phong),
    KEY idx_ctdp_phong (ma_phong),
    CONSTRAINT fk_ctdp_datphong FOREIGN KEY (ma_dat_phong) 
        REFERENCES don_dat_phong(ma_dat_phong) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_ctdp_phong FOREIGN KEY (ma_phong) 
        REFERENCES phong(ma_phong) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------------------------------------
-- 9. Chi tiết dịch vụ sử dụng (chi_tiet_dich_vu)
-- Ghi nhận các món minibar, giặt ủi, đồ ăn/uống phát sinh trong suốt kỳ lưu trú
-- -------------------------------------------------------------------------------------
CREATE TABLE chi_tiet_dich_vu (
    id BIGINT AUTO_INCREMENT NOT NULL,
    ma_dat_phong VARCHAR(30) NOT NULL,
    ma_dich_vu VARCHAR(20) NOT NULL,
    so_luong INT NOT NULL DEFAULT 1,
    thoi_gian_sd DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    thanh_tien DECIMAL(12, 2) NOT NULL,
    PRIMARY KEY (id),
    KEY idx_ctdv_datphong (ma_dat_phong),
    KEY idx_ctdv_dichvu (ma_dich_vu),
    CONSTRAINT fk_ctdv_datphong FOREIGN KEY (ma_dat_phong) 
        REFERENCES don_dat_phong(ma_dat_phong) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_ctdv_dichvu FOREIGN KEY (ma_dich_vu) 
        REFERENCES dich_vu(ma_dich_vu) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------------------------------------
-- 10. Bảng Hóa đơn thanh toán (hoa_don)
-- Quyết toán tài chính khi khách check-out hoặc thanh toán online thành công
-- -------------------------------------------------------------------------------------
CREATE TABLE hoa_don (
    ma_hoa_don VARCHAR(30) NOT NULL,
    ngay_lap_hoa_don DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    tien_phong DECIMAL(14, 2) NOT NULL,
    tien_dich_vu DECIMAL(14, 2) NOT NULL DEFAULT 0.00,
    phu_thu DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    giam_gia DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    tong_tien_thanh_toan DECIMAL(14, 2) NOT NULL,
    da_thanh_toan DECIMAL(14, 2) NOT NULL DEFAULT 0.00,
    phuong_thuc_thanh_toan VARCHAR(30) NULL, -- CASH, VNPAY, CREDIT_CARD
    trang_thai_thanh_toan VARCHAR(30) NOT NULL DEFAULT 'UNPAID', -- UNPAID, PAID, FAILED
    ma_dat_phong VARCHAR(30) NOT NULL,
    PRIMARY KEY (ma_hoa_don),
    UNIQUE KEY uq_hoadon_datphong (ma_dat_phong),
    CONSTRAINT fk_hoadon_datphong FOREIGN KEY (ma_dat_phong) 
        REFERENCES don_dat_phong(ma_dat_phong) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------------------------------------
-- 11. Bảng Đánh giá (danh_gia)
-- Tiếp nhận phản hồi đánh giá chất lượng phục vụ từ 1 đến 5 sao của khách hàng
-- -------------------------------------------------------------------------------------
CREATE TABLE danh_gia (
    ma_danh_gia VARCHAR(30) NOT NULL,
    so_sao_danh_gia INT NOT NULL,
    noi_dung_nx TEXT NULL,
    thoi_gian_danh_gia DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ma_dat_phong VARCHAR(30) NOT NULL,
    ma_khach_hang VARCHAR(20) NOT NULL,
    PRIMARY KEY (ma_danh_gia),
    UNIQUE KEY uq_danhgia_datphong (ma_dat_phong),
    KEY idx_danhgia_khachhang (ma_khach_hang),
    CONSTRAINT chk_so_sao CHECK (so_sao_danh_gia BETWEEN 1 AND 5),
    CONSTRAINT fk_danhgia_datphong FOREIGN KEY (ma_dat_phong) 
        REFERENCES don_dat_phong(ma_dat_phong) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_danhgia_khachhang FOREIGN KEY (ma_khach_hang) 
        REFERENCES khach_hang(ma_khach_hang) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================================================
-- SEED DATA MẪU KIỂM THỬ TOÀN DIỆN (DML SCRIPT)
-- Mật khẩu mặc định cho các tài khoản là: 123456
-- BCrypt Hash: $2a$10$XFQTXnEmJZGLijilmDXw9e6ldDyI0vUdLHsmFbV08AjN3IG8xGahi
-- =====================================================================================

-- 1. Seed Khách hàng
INSERT INTO khach_hang (ma_khach_hang, ho_ten, ngay_sinh, so_dien_thoai, email, mat_khau, so_cccd, trang_thai_tk) VALUES
('KH001', 'Nguyễn Văn An', '1990-05-15', '0912345678', 'an.nguyen@gmail.com', '$2a$10$XFQTXnEmJZGLijilmDXw9e6ldDyI0vUdLHsmFbV08AjN3IG8xGahi', '001090123456', 'ACTIVE'),
('KH002', 'Trần Thị Mai', '1995-08-20', '0987654321', 'mai.tran@gmail.com', '$2a$10$XFQTXnEmJZGLijilmDXw9e6ldDyI0vUdLHsmFbV08AjN3IG8xGahi', '001095654321', 'ACTIVE'),
('KH003', 'Lê Hoàng Long', '1988-12-10', '0903112233', 'long.le@gmail.com', '$2a$10$XFQTXnEmJZGLijilmDXw9e6ldDyI0vUdLHsmFbV08AjN3IG8xGahi', '001088778899', 'ACTIVE');

-- 2. Seed Nhân viên theo các vai trò (RBAC)
INSERT INTO nhan_vien (ma_nhan_vien, ten_dang_nhap, mat_khau, ho_ten, so_dt, so_cccd, quyen_han) VALUES
('NV_ADMIN', 'admin', '$2a$10$XFQTXnEmJZGLijilmDXw9e6ldDyI0vUdLHsmFbV08AjN3IG8xGahi', 'Quản trị viên Hệ thống', '0901000001', '001000111222', 'ROLE_ADMIN'),
('NV_LETAN', 'reception01', '$2a$10$XFQTXnEmJZGLijilmDXw9e6ldDyI0vUdLHsmFbV08AjN3IG8xGahi', 'Nguyễn Thu Thủy (Lễ tân)', '0901000002', '001000111223', 'ROLE_RECEPTIONIST'),
('NV_BUONGPHONG', 'housekeeping01', '$2a$10$XFQTXnEmJZGLijilmDXw9e6ldDyI0vUdLHsmFbV08AjN3IG8xGahi', 'Phạm Văn Hùng (Buồng phòng)', '0901000003', '001000111224', 'ROLE_HOUSEKEEPING'),
('NV_KETOAN', 'accountant01', '$2a$10$XFQTXnEmJZGLijilmDXw9e6ldDyI0vUdLHsmFbV08AjN3IG8xGahi', 'Lê Thị Phương (Kế toán)', '0901000004', '001000111225', 'ROLE_ACCOUNTANT');

-- 3. Seed Loại phòng Meliá Hà Nội
INSERT INTO loai_phong (ma_loai_phong, ten_loai_phong, gia_phong_co_ban, suc_chua, thong_tin, hinh_anh_minh_hoa, mo_ta) VALUES
('DELUXE', 'Deluxe King / Twin Room', 2500000.00, 2, 'Diện tích 32m2, hướng phố Lý Thường Kiệt, bồn tắm nằm cao cấp', 'https://melia.com/images/deluxe.jpg', 'Phòng tiêu chuẩn 5 sao với thiết kế trang nhã, giường King cỡ lớn, sàn gỗ tự nhiên.'),
('PREMIUM', 'Premium King City View', 3500000.00, 2, 'Diện tích 38m2, tầng cao ngắm nhìn toàn cảnh trung tâm Hà Nội', 'https://melia.com/images/premium.jpg', 'Nội thất sang trọng phong cách đương đại Tây Ban Nha kết hợp nét tinh hoa Hà thành.'),
('SUITE', 'The Level Suite', 6500000.00, 3, 'Diện tích 68m2, bao gồm phòng khách tách biệt và quyền lợi The Level Lounge', 'https://melia.com/images/suite.jpg', 'Trải nghiệm đỉnh cao thượng lưu với dịch vụ quản gia riêng và quầy lounge miễn phí.'),
('GRAND_SUITE', 'Presidential Grand Suite', 15000000.00, 4, 'Diện tích 140m2, phòng họp riêng, phòng ăn lớn, tầm nhìn 360 độ', 'https://melia.com/images/president.jpg', 'Không gian nghỉ dưỡng tuyệt đối dành cho các chính khách và đại biểu cấp cao.');

-- 4. Seed Phòng vật lý thực tế
INSERT INTO phong (ma_phong, so_phong, tang, trang_thai_phong, ma_loai_phong) VALUES
('P101', '101', 1, 'AVAILABLE', 'DELUXE'),
('P102', '102', 1, 'AVAILABLE', 'DELUXE'),
('P103', '103', 1, 'AVAILABLE', 'DELUXE'),
('P201', '201', 2, 'AVAILABLE', 'PREMIUM'),
('P202', '202', 2, 'DIRTY', 'PREMIUM'),
('P203', '203', 2, 'AVAILABLE', 'PREMIUM'),
('P301', '301', 3, 'AVAILABLE', 'SUITE'),
('P302', '302', 3, 'MAINTENANCE', 'SUITE'),
('P401', '401', 4, 'AVAILABLE', 'GRAND_SUITE');

-- 5. Seed Phiếu giảm giá (Vouchers)
INSERT INTO phieu_giam_gia (ma_phieu_gg, gia_tri_giam, loai_giam, thoi_gian_bat_dau, thoi_gian_ket_thuc, so_luong_phieu) VALUES
('MELIAWELCOME', 200000.00, 'FIXED', '2026-01-01 00:00:00', '2026-12-31 23:59:59', 100),
('SUMMER2026', 15.00, 'PERCENT', '2026-06-01 00:00:00', '2026-08-31 23:59:59', 50),
('VIPMELIA', 500000.00, 'FIXED', '2026-01-01 00:00:00', '2026-12-31 23:59:59', 30);

-- 6. Seed Danh mục Dịch vụ & Minibar
INSERT INTO dich_vu (ma_dich_vu, ten_dv, gia_dv, mo_ta_dv) VALUES
('DV_MINIBAR_AQUA', 'Nước khoáng cao cấp Acqua Panna (Minibar)', 65000.00, 'Chai thủy tinh 500ml nhập khẩu Ý đặt tại tủ minibar'),
('DV_MINIBAR_HEINEKEN', 'Bia Heineken lon (Minibar)', 80000.00, 'Lon 330ml ướp lạnh'),
('DV_MINIBAR_WINE', 'Rượu vang đỏ Chianti Classico (Minibar)', 1250000.00, 'Chai 750ml tiêu chuẩn khách sạn 5 sao'),
('DV_SPA_BODY', 'Gói trị liệu toàn thân YHI Spa (60 phút)', 1500000.00, 'Massage thư giãn tinh dầu thảo mộc thiên nhiên'),
('DV_BUFFET_SANG', 'Buffet sáng quốc tế El Meliá', 450000.00, 'Vé buffet dành cho 1 người lớn tại nhà hàng tầng 1'),
('DV_GIAT_LA', 'Dịch vụ giặt ủi lấy nhanh trong ngày', 120000.00, 'Giặt hấp ủi quần áo comple, váy dạ hội');

-- 7. Seed Đơn đặt phòng mẫu
INSERT INTO don_dat_phong (ma_dat_phong, ngay_dat, thoi_gian_nhan_phong, thoi_gian_tra_phong, tong_tien, trang_thai_don, yeu_cau_db, ma_khach_hang, ma_loai_phong, ma_phieu_gg, ma_nhan_vien, qr_code) VALUES
('DP2026102701', '2026-10-27 10:00:00', '2026-10-27', '2026-10-29', 7000000.00, 'CHECKED_OUT', 'Phòng tầng cao không hút thuốc', 'KH001', 'PREMIUM', NULL, 'NV_LETAN', 'MELIA-CHECKIN-QR-DP2026102701'),
('DP2026102702', '2026-10-27 14:30:00', '2026-11-01', '2026-11-03', 5000000.00, 'CONFIRMED', 'Kỷ niệm ngày cưới, chuẩn bị hoa tươi', 'KH002', 'DELUXE', 'MELIAWELCOME', NULL, 'MELIA-CHECKIN-QR-DP2026102702');

-- 8. Seed Chi tiết đặt phòng
INSERT INTO chi_tiet_dat_phong (ma_dat_phong, ma_phong, so_phong, gia_tai_thoi_diem_dat) VALUES
('DP2026102701', 'P202', '202', 3500000.00);

-- 9. Seed Chi tiết dịch vụ sử dụng
INSERT INTO chi_tiet_dich_vu (id, ma_dat_phong, ma_dich_vu, so_luong, thoi_gian_sd, thanh_tien) VALUES
(1, 'DP2026102701', 'DV_MINIBAR_HEINEKEN', 2, '2026-10-27 20:15:00', 160000.00),
(2, 'DP2026102701', 'DV_BUFFET_SANG', 1, '2026-10-28 07:30:00', 450000.00);

-- 10. Seed Hóa đơn thanh toán
INSERT INTO hoa_don (ma_hoa_don, ngay_lap_hoa_don, tien_phong, tien_dich_vu, phu_thu, giam_gia, tong_tien_thanh_toan, da_thanh_toan, phuong_thuc_thanh_toan, trang_thai_thanh_toan, ma_dat_phong) VALUES
('HD2026102701', '2026-10-27 10:05:00', 7000000.00, 610000.00, 0.00, 0.00, 7610000.00, 7610000.00, 'VNPAY', 'PAID', 'DP2026102701'),
('HD2026102702', '2026-10-27 14:35:00', 5000000.00, 0.00, 0.00, 200000.00, 4800000.00, 4800000.00, 'VNPAY', 'PAID', 'DP2026102702');

-- 11. Seed Đánh giá
INSERT INTO danh_gia (ma_danh_gia, so_sao_danh_gia, noi_dung_nx, thoi_gian_danh_gia, ma_dat_phong, ma_khach_hang) VALUES
('DG001', 5, 'Dịch vụ tại Meliá Hà Nội tuyệt vời, nhân viên lễ tân nhiệt tình và thủ tục check-in bằng mã QR rất tiện lợi!', '2026-10-29 14:00:00', 'DP2026102701', 'KH001');