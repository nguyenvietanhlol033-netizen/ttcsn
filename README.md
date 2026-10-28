# HỆ THỐNG ĐẶT PHÒNG VÀ QUẢN LÝ KHÁCH SẠN 5 SAO MELIÁ HÀ NỘI
## ĐỒ ÁN THỰC TẬP CƠ SỞ NGÀNH - NHÓM 11

Dự án hoàn chỉnh kết hợp giữa **Backend Spring Boot 3 (Java 17 LTS)** và **Frontend Liquid Glass UI/UX Pro Max Standard**, hỗ trợ khởi chạy tức thì qua **Docker & Docker Compose** với 1-click script.

---

## 1. CẤU TRÚC THƯ MỤC DỰ ÁN

```plaintext
ttcsn/
├── backend/                     # Mã nguồn Backend Spring Boot 3.2.4
│   ├── src/                     # Cấu trúc Controller, Service, DTO, Entity, Security, Util
│   ├── melia_hotel_db.sql       # Script DDL 11 bảng chuẩn quan hệ & Seed Data mẫu
│   ├── pom.xml                  # Quản lý phụ thuộc Maven
│   ├── Dockerfile               # Cấu hình container Backend (Eclipse Temurin Java 17 Alpine)
│   ├── .dockerignore            # Tối ưu hóa build context Docker
│   ├── run.bat                  # Script chạy trực tiếp Backend trên Windows (không qua Docker)
│   └── README.md                # Tài liệu chi tiết nghiệp vụ Backend
├── frontend/                    # Giao diện người dùng Web (Liquid Glass UI/UX Pro Max)
│   ├── index.html               # Cổng thông tin khách hàng & đặt phòng trực tuyến
│   ├── admin.html               # Cổng quản trị tác nghiệp đa vai trò (Lễ tân, Buồng phòng, Admin)
│   ├── assets/                  # CSS tokens, Glassmorphism và các modules JavaScript API
│   ├── Dockerfile               # Cấu hình container Frontend (Nginx Alpine)
│   ├── nginx.conf               # Cấu hình Nginx nén Gzip & Reverse Proxy /api/
│   ├── run_frontend.bat         # Script chạy nhanh Frontend trên Windows (Local Web Server)
│   └── README.md                # Tài liệu chi tiết giao diện Frontend
├── docker-compose.yml           # Khởi chạy đồng bộ toàn bộ hệ thống qua Docker Compose
├── docker-start.bat             # Kịch bản 1-click: Bật toàn bộ hệ thống & mở trình duyệt
├── docker-stop.bat              # Kịch bản 1-click: Dừng toàn bộ các container Docker
├── .gitignore                   # Danh sách file loại trừ khỏi Git
└── README.md                    # Tài liệu tổng quan toàn bộ dự án
```

---

## 2. HƯỚNG DẪN KHỞI CHẠY NHANH

### Cách 1: Khởi chạy 1-Click bằng Docker (Khuyên dùng)
* **Khởi chạy**: Nhấp đúp vào file **[`docker-start.bat`](file:///E:/Thực%20tập%20cơ%20sở%20nghành/ttcsn/docker-start.bat)**.
  * Tự động kiểm tra Docker Desktop.
  * Tự động build và chạy cả Backend (cổng `8080`) và Frontend (cổng `3000`).
  * Tự động mở trình duyệt web tới trang chủ hệ thống.
* **Tắt hệ thống**: Nhấp đúp vào file **[`docker-stop.bat`](file:///E:/Thực%20tập%20cơ%20sở%20nghành/ttcsn/docker-stop.bat)**.

Hoặc sử dụng lệnh dòng lệnh (Command Line):
```bash
# Khởi chạy nền với H2 In-Memory DB (Mặc định, không cần cài đặt MySQL)
docker compose up -d

# Nếu muốn chạy cùng container MySQL 8.0:
docker compose --profile with-mysql up -d

# Xem log các dịch vụ
docker compose logs -f

# Dừng hệ thống
docker compose down
```

---

### Cách 2: Khởi chạy thủ công trên máy (Không dùng Docker)
1. **Chạy Backend**:
   * Vào thư mục `backend`, nhấp đúp file **`run.bat`** (yêu cầu máy có cài Java 17 trở lên).
   * Backend sẽ chạy tại cổng `http://localhost:8080`.
2. **Chạy Frontend**:
   * Vào thư mục `frontend`, nhấp đúp file **`run_frontend.bat`** hoặc mở trực tiếp `index.html` bằng trình duyệt web.

---

## 3. CÁC ĐƯỜNG DẪN TRUY CẬP HỆ THỐNG

| Thành phần | URL Truy cập | Chức năng chính |
| :--- | :--- | :--- |
| **Cổng Khách hàng (Guest Portal)** | [http://localhost:3000](http://localhost:3000) | Tra cứu phòng trống, Đặt phòng Wizard, Áp mã voucher, Thanh toán VNPay, Xem mã QR Check-in |
| **Cổng Quản trị (Staff & Admin)** | [http://localhost:3000/admin.html](http://localhost:3000/admin.html) | Lễ tân (Check-in gán phòng, Check-out trừ cọc, In biên lai A4), Buồng phòng, Báo cáo RevPAR |
| **Tài liệu API Swagger UI** | [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) | Giao diện kiểm thử trực quan 20+ RESTful APIs |
| **H2 Database Console** | [http://localhost:8080/h2-console](http://localhost:8080/h2-console) | Quản trị CSDL bộ nhớ (JDBC URL: `jdbc:h2:mem:melia_hotel_db`) |

---

## 4. DANH SÁCH TÀI KHOẢN MẪU ĐỂ TEST ĐĂNG NHẬP

Tất cả tài khoản dưới đây sử dụng **mật khẩu mặc định là: `123456`**

| Vai trò (Role) | Tên đăng nhập (Username / Email) | Mật khẩu | Quyền hạn và Chức năng kiểm thử |
| :--- | :--- | :--- | :--- |
| **Quản trị viên (ADMIN)** | `admin` | `123456` | Toàn quyền hệ thống, xem báo cáo doanh thu & RevPAR, quản lý hạng phòng |
| **Lễ tân (RECEPTIONIST)** | `reception01` | `123456` | Tra cứu đơn qua CCCD/Mã đơn, Check-in gán phòng, Check-out trừ cọc và in hóa đơn A4 |
| **Buồng phòng (HOUSEKEEPING)** | `housekeeping01` | `123456` | Xem sơ đồ trạng thái phòng theo tầng, đổi phòng bẩn sang sạch, ghi nhận Minibar |
| **Kế toán (ACCOUNTANT)** | `accountant01` | `123456` | Quản lý hóa đơn lưu trú, theo dõi báo cáo tài chính và đối soát thu chi |
| **Khách hàng (CUSTOMER)** | `an.nguyen@gmail.com` | `123456` | Đặt phòng trực tuyến, áp voucher `MELIAWELCOME`, thanh toán và đánh giá dịch vụ |

---

## 5. CÔNG NGHỆ VÀ TÍNH NĂNG NỔI BẬT

1. **Kiến trúc Layered Architecture chuẩn**: Tách bạch Controller, Service, Repository, DTO và Entity.
2. **Thuật toán Chống Trùng Phòng (Anti-Overbooking)**:
   * Áp dụng khóa bi quan `@Lock(LockModeType.PESSIMISTIC_WRITE)` trên bảng `LoaiPhong`.
   * Tính toán buồng phòng khả dụng theo công thức: `(Tổng phòng - Bảo trì) - (Đơn CONFIRMED + CHECKED_IN + PENDING trong 15 phút)`.
3. **Quyết toán Chính xác**:
   * Khi khách Check-out, hệ thống tự động khấu trừ khoản tiền đã cọc/trả trước (`da_thanh_toan`), ngăn chặn triệt để lỗi thu tiền hai lần.
4. **Cổng thanh toán VNPay Sandbox**:
   * Sinh URL thanh toán có mã băm HMAC-SHA512.
   * Xử lý callback Idempotent chống xử lý lặp giao dịch.
5. **Mã QR Base64**:
   * Tự động sinh mã QR Check-in ngay sau khi đặt phòng thành công qua thư viện ZXing.
6. **Frontend Liquid Glass UI/UX**:
   * Thiết kế chuẩn khách sạn 5 sao cao cấp (Navy `#1E3A8A` & Gold `#D4AF37`).
   * Tích hợp biểu đồ phân tích Chart.js và hỗ trợ in hóa đơn trực tiếp `@media print`.

---
*Chúc các bạn bảo vệ thành công đồ án Thực tập cơ sở ngành!*
