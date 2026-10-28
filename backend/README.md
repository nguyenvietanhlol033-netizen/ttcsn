# HỆ THỐNG ĐẶT PHÒNG VÀ QUẢN LÝ KHÁCH SẠN MELIÁ HÀ NỘI
## BACKEND SPRING BOOT (ĐỒ ÁN THỰC TẬP CƠ SỞ NGÀNH - NHÓM 11)

Dự án Backend hoàn chỉnh cho hệ thống Khách sạn Meliá Hà Nội theo kiến trúc phân tầng Layered Architecture chuẩn, tích hợp Spring Security 6 với JWT Token, kiểm soát phân quyền RBAC đa vai trò, thuật toán chống trùng phòng (Overbooking) với khóa bi quan (`PESSIMISTIC_WRITE`), trừ tiền trả trước khi check-out, cổng thanh toán VNPay Sandbox có kiểm tra số tiền và chống xử lý lặp (Idempotent), mã QR Base64 tự động và email thông báo bất đồng bộ.

---

## 1. YÊU CẦU MÔI TRƯỜNG HỆ THỐNG
* **Hệ điều hành**: Windows 10/11, Linux hoặc macOS.
* **Java SDK**: Java 17 hoặc Java 21 LTS (Khuyên dùng Eclipse Temurin / OpenJDK 17 hoặc 21).
* **Cơ sở dữ liệu**: MySQL Server 8.0 trở lên (bảng mã `utf8mb4`).
* **Build tool**: Apache Maven 3.9+ hoặc Maven Wrapper (`mvnw` / `mvnw.cmd` kèm theo dự án).

---

## 2. HƯỚNG DẪN CÀI ĐẶT CƠ SỞ DỮ LIỆU (MYSQL)

### Bước 2.1: Nhập Cơ sở Dữ liệu & Dữ liệu Mẫu
Mở MySQL Command Line Client hoặc MySQL Workbench / Navicat / DBeaver, sau đó thực thi file SQL:
```bash
mysql -u root -p < melia_hotel_db.sql
```
Hoặc mở file `melia_hotel_db.sql` trực tiếp bằng MySQL Workbench/HeidiSQL và bấm **Execute (Run All)**.
Script này sẽ tự động:
1. Tạo Database `melia_hotel_db` chuẩn `utf8mb4_unicode_ci`.
2. Khởi tạo đầy đủ 11 bảng quan hệ khóa ngoại (ERD), bao gồm `ma_loai_phong` trong `don_dat_phong` và `da_thanh_toan` trong `hoa_don`.
3. Nạp sẵn Seed Data mẫu (khách hàng, nhân viên 4 vai trò, loại phòng Deluxe/Premium/Suite, phòng thực tế, voucher giảm giá, dịch vụ minibar/giặt là/spa).

### Bước 2.2: Cấu hình kết nối MySQL trong ứng dụng
Mở file `src/main/resources/application.yml` và chỉnh sửa lại `username` và `password` MySQL của máy bạn:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/melia_hotel_db?useSSL=false&serverTimezone=Asia/Ho_Chi_Minh&allowPublicKeyRetrieval=true&characterEncoding=UTF-8
    username: root
    password: your_mysql_password # <-- Thay bằng mật khẩu MySQL của bạn (ví dụ: 123456, root, admin...)
```

---

## 3. HƯỚNG DẪN KHỞI CHẠY HỆ THỐNG BACKEND

Bạn có thể chạy dự án bằng **1 trong 3 cách** cực kỳ thuận tiện sau:

### Cách 1: Chạy nhanh bằng file Batch (Khuyên dùng trên Windows)
Chỉ cần nhấp đúp (Double-click) vào file **`run.bat`** tại thư mục gốc `backend_ttcsn`. File này sẽ tự động khởi chạy file JAR đã được biên dịch sẵn.

### Cách 2: Chạy trực tiếp từ file JAR đã đóng gói sẵn
Mở Terminal/PowerShell tại thư mục `backend_ttcsn` và chạy:
```bash
java -jar target/melia-hotel-backend-1.0.0.jar
```

### Cách 3: Chạy từ mã nguồn thông qua Maven Wrapper
Mở PowerShell tại thư mục `backend_ttcsn` và chạy:
```powershell
.\mvnw.cmd spring-boot:run
```
*(Nếu muốn build lại từ đầu: `.\mvnw.cmd clean package -DskipTests`)*

---

## 4. TÀI LIỆU API & GIAO DIỆN KIỂM THỬ (SWAGGER UI)

Sau khi server khởi động thành công trên cổng **8080**, truy cập trình duyệt:
* **Giao diện kiểm thử trực quan Swagger UI**:
  👉 [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
* **OpenAPI JSON Specs**:
  👉 [http://localhost:8080/api-docs](http://localhost:8080/api-docs)

Trên giao diện Swagger UI, bạn có thể thực hiện đăng nhập để lấy JWT Token, sau đó bấm nút **Authorize (hình ổ khóa)** ở góc trên bên phải, nhập `Bearer <token>` để gọi tất cả các API nghiệp vụ có bảo mật.

---

## 5. DANH SÁCH TÀI KHOẢN MẪU ĐỂ TEST ĐĂNG NHẬP

Tất cả tài khoản dưới đây đã được mã hóa BCrypt an toàn trong Database với **mật khẩu mặc định là: `123456`**

| Vai trò (Role) | Tên đăng nhập (Username/Email) | Mật khẩu | Quyền hạn & Chức năng kiểm thử |
| :--- | :--- | :--- | :--- |
| **Quản trị viên (ADMIN)** | `admin` | `123456` | Toàn quyền hệ thống, quản lý hạng phòng, xem báo cáo doanh thu & tỷ lệ lấp đầy RevPAR |
| **Lễ tân (RECEPTIONIST)** | `reception01` | `123456` | Tra cứu đơn qua ID/CCCD, đặt phòng tại quầy, Check-in gán phòng, Check-out thanh toán |
| **Buồng phòng (HOUSEKEEPING)** | `housekeeping01` | `123456` | Tra cứu danh sách phòng, cập nhật tình trạng phòng, báo cáo minibar phát sinh |
| **Kế toán (ACCOUNTANT)** | `accountant01` | `123456` | Quản lý hóa đơn, đối soát thu chi, xuất báo cáo tài chính |
| **Khách hàng (CUSTOMER 1)** | `an.nguyen@gmail.com` | `123456` | Tra cứu phòng trống, đặt phòng, áp voucher, đánh giá 5 sao sau khi check-out |
| **Khách hàng (CUSTOMER 2)** | `mai.tran@gmail.com` | `123456` | Khách hàng thân thiết có lịch sử đặt phòng đã hoàn tất |

---

## 6. DANH MỤC CÁC ENDPOINT RESTful CHÍNH

### 1. Xác thực & Phân quyền (Auth)
* `POST /api/v1/auth/login`: Đăng nhập (hỗ trợ cả Khách hàng bằng Email/SĐT và Nhân viên bằng Username), trả về JWT Token và thông tin vai trò.
* `POST /api/v1/auth/register`: Đăng ký tài khoản khách hàng mới (kiểm tra trùng lặp email, số điện thoại, số CCCD và hỗ trợ ngày sinh).

### 2. Tra cứu & Quản lý Phòng (Rooms)
* `GET /api/v1/rooms/available?checkIn=...&checkOut=...&guests=...`: Tìm kiếm loại phòng còn phòng trống theo thuật toán: `Số phòng khả dụng = (Tổng phòng - Phòng bảo trì) - (Đơn CONFIRMED/CHECKED_IN + PENDING trong 15 phút)`.
* `GET /api/v1/rooms/types`: Lấy danh sách tất cả các hạng phòng (Deluxe, Premium, Suite...).
* `GET /api/v1/rooms/types/{id}`: Xem chi tiết hạng phòng.
* `POST /api/v1/rooms/types`: Thêm mới hạng phòng (Quyền `ROLE_ADMIN`).
* `GET /api/v1/rooms/{id}`: Xem chi tiết thông tin một phòng vật lý cụ thể.

### 3. Đặt phòng & Ưu đãi (Bookings & Coupons)
* `POST /api/v1/bookings`: Tạo đơn đặt phòng mới (Trạng thái mặc định: `PENDING`). Sử dụng `@Lock(PESSIMISTIC_WRITE)` trên `LoaiPhong` chống đặt trùng phòng đồng thời. Mã khách hàng được trích xuất an toàn từ JWT Token.
* `GET /api/v1/bookings/{id}`: Xem chi tiết đơn đặt phòng bằng DTO an toàn (kiểm tra quyền sở hữu của khách hàng hoặc nhân viên lễ tân/admin).
* `GET /api/v1/bookings/customer/{customerId}`: Xem lịch sử đặt phòng của khách hàng.
* `POST /api/v1/bookings/coupons/apply`: Kiểm tra tính hợp lệ và áp dụng mã giảm giá (`MELIAWELCOME`, `SUMMER2026`, `VIPMELIA`).
* `POST /api/v1/coupons/apply`: Endpoint bí danh tương đương phục vụ tích hợp.

### 4. Cổng thanh toán trực tuyến (VNPay Gateway)
* `POST /api/v1/payments/create-url`: Khởi tạo liên kết thanh toán sang Cổng VNPay Sandbox với mã băm HMAC SHA-512.
* `GET /api/v1/payments/vnpay-callback`: Endpoint tiếp nhận IPN / phản hồi thanh toán từ VNPay. Đã xử lý kiểm tra số tiền `vnp_Amount`, chống xử lý lặp (Idempotent), nếu thanh toán thất bại chuyển đơn về `CANCELLED`, hóa đơn `FAILED` và tự động hoàn trả số lượng voucher.

### 5. Nghiệp vụ Lễ tân (Reception Desk)
* `GET /api/v1/reception/bookings/{id}`: Tra cứu đơn đặt phòng theo mã đơn hoặc số CCCD/số điện thoại của khách hàng.
* `POST /api/v1/reception/check-in`: Tiếp đón khách, kiểm tra phòng vật lý gán đúng loại phòng khách đặt và đang `AVAILABLE`. ID nhân viên được tự động lấy từ JWT Token. Chuyển đơn sang `CHECKED_IN`, phòng sang `OCCUPIED`.
* `POST /api/v1/reception/check-out`: Trả phòng, tự động tổng kết tiền phòng, dịch vụ minibar, phụ thu; **khấu trừ số tiền khách đã thanh toán trước (`da_thanh_toan`)**, tính `con_phai_tra`, chốt hóa đơn `PAID` và cập nhật phòng sang `DIRTY`.

### 6. Nghiệp vụ Buồng phòng (Housekeeping)
* `GET /api/v1/housekeeping/rooms`: Tra cứu danh sách phòng thực tế (có thể lọc theo trạng thái `AVAILABLE`, `DIRTY`, `MAINTENANCE`).
* `PUT /api/v1/housekeeping/rooms/{id}/status`: Cập nhật trạng thái phòng (`AVAILABLE`, `DIRTY`, `MAINTENANCE`).
* `POST /api/v1/housekeeping/minibar-usage`: Ghi nhận các món minibar khách đã dùng trong kỳ lưu trú để tự động tính vào hóa đơn khi check-out.

### 7. Quản trị & Báo cáo Thống kê (Reports & Analytics)
* `GET /api/v1/admin/reports/occupancy?fromDate=...&toDate=...`: Báo cáo tỷ lệ lấp đầy phòng theo kỳ (Occupancy Rate) chuẩn: `Tổng đêm phòng có khách thực tế / (Số phòng khả dụng x Số ngày)`.
* `GET /api/v1/admin/reports/revenue?fromDate=...&toDate=...`: Báo cáo chi tiết doanh thu phòng, doanh thu dịch vụ minibar và chỉ số RevPAR chuẩn: `Doanh thu phòng / (Số phòng khả dụng x Số ngày)`.

### 8. Đánh giá chất lượng (Customer Reviews)
* `POST /api/v1/reviews`: Khách hàng gửi đánh giá (1 - 5 sao). Đã kiểm tra quyền sở hữu đơn đặt phòng và chỉ cho phép đánh giá sau khi đã Check-out.
* `GET /api/v1/reviews/featured`: Hiển thị công khai các đánh giá tích cực (từ 4 sao trở lên).

---

## 7. CẤU TRÚC MÃ NGUỒN DỰ ÁN

```plaintext
backend_ttcsn/
├── pom.xml                               # Cấu hình phụ thuộc Spring Boot 3.2.4 & Maven UTF-8
├── run.bat                               # Kịch bản khởi chạy 1-click trên Windows
├── melia_hotel_db.sql                    # Script DDL 11 bảng chuẩn ERD & DML Seed Data mẫu
├── README.md                             # Tài liệu hướng dẫn sử dụng chi tiết
├── target/
│   └── melia-hotel-backend-1.0.0.jar     # File thực thi JAR đã đóng gói sẵn (~60MB)
└── src/main/
    ├── resources/
    │   └── application.yml               # Cấu hình Port, MySQL (ddl-auto: validate), JWT, VNPay, Mail
    └── java/com/melia/hotel/
        ├── MeliaHotelApplication.java    # Class khởi chạy Spring Boot
        ├── config/                       # SecurityConfig, VNPayConfig, SwaggerConfig
        ├── controller/                   # 8 REST Controllers đầy đủ nghiệp vụ
        ├── dto/
        │   ├── request/                  # BookingRequest, CheckInRequest, CheckOutRequest, RegisterRequest...
        │   └── response/                 # ApiResponse, BookingResponse, InvoiceResponse, ReportResponse...
        ├── entity/                       # 11 JPA Entities tương ứng 11 bảng ERD
        ├── exception/                    # GlobalExceptionHandler, BusinessException
        ├── repository/                   # 11 Spring Data JPA Repositories
        ├── security/                     # JwtTokenProvider, JwtAuthenticationFilter, JwtAuthenticationEntryPoint
        ├── service/                      # Interfaces cho Booking, Room, Payment, Reception, Housekeeping, Report...
        ├── service/impl/                 # Triển khai chi tiết toàn bộ nghiệp vụ
        └── util/                         # VNPayUtil (HMAC SHA512), QRCodeUtil (ZXing)
```

---
*Chúc các bạn bảo vệ thành công đồ án Thực tập cơ sở ngành!*
