# GIAO DIỆN FRONTEND KHÁCH SẠN MELIÁ HÀ NỘI (UI/UX PRO MAX STANDARD)
## ĐỒ ÁN THỰC TẬP CƠ SỞ NGÀNH - NHÓM 11

Bộ giao diện Frontend hoàn chỉnh, hiện đại dành cho **Hệ thống Quản lý và Đặt phòng Khách sạn 5 sao Meliá Hà Nội**, được xây dựng dựa trên tiêu chuẩn thiết kế cao cấp từ bộ tài liệu **`ui-ux-pro-max-skill`** (Chuyên mục Hospitality / Hotel 5-Star), khớp nối **100%** với hệ thống Backend Spring Boot 3 (`backend_ttcsn`).

---

## 1. TIÊU CHUẨN THIẾT KẾ UI/UX PRO MAX (HOSPITALITY LUXURY)

Tuân thủ nghiêm ngặt hồ sơ thiết kế khách sạn 5 sao trong `ui-ux-pro-max-skill-main`:
* **Triết lý giao diện**: **Liquid Glass + Swiss Minimalism & Glassmorphism**.
  * Hiệu ứng kính mờ chiều sâu đa tầng (`backdrop-filter: blur(16px)`).
  * Thẻ nổi góc bo lớn (`rounded-3xl`), viền sáng vi mô (`border-slate-200/80`), đổ bóng mềm mượt (`shadow-xl`).
* **Hệ màu nhận diện thượng lưu (Luxury Palette)**:
  * **Màu chủ đạo (Primary Navy)**: `#1E3A8A` (Deep Luxury Melia Navy) & `#0F172A` (Slate Noir).
  * **Màu điểm nhấn (Accent Gold)**: `#D4AF37` / `#B45309` (Imperial Gold hoàng gia).
  * **Màu nền (Background Porcelain)**: `#F8FAFC` (Trắng sứ tinh khôi).
  * **Màu trạng thái buồng phòng chuẩn quốc tế**:
    * `AVAILABLE` (Phòng sạch - Sẵn sàng đón khách): Xanh ngọc lục bảo `#10B981`.
    * `OCCUPIED` (Phòng đang có khách lưu trú): Xanh hoàng gia `#3B82F6`.
    * `DIRTY` (Phòng bẩn cần dọn dẹp): Cam hổ phách `#F59E0B`.
    * `MAINTENANCE` (Phòng đang bảo trì kỹ thuật): Đỏ san hô `#EF4444`.
* **Hệ thống Typography chuẩn quốc tế**:
  * **Tiêu đề & Hạng phòng (Headings)**: Font có chân cổ điển `Playfair Display` (Serif).
  * **Nội dung, biểu mẫu & dữ liệu (Body & UI)**: Font không chân công thái học `Inter` (Sans-serif) với độ tương phản WCAG AAA.
* **Biểu đồ phân tích (Analytics Charts)**: Tích hợp thư viện `Chart.js` hiển thị trực quan tỷ lệ lấp đầy buồng phòng và cơ cấu doanh thu.
* **Hóa đơn in ấn (Printable Invoice)**: Cấu hình sẵn CSS chuyên biệt `@media print` giúp nhân viên lễ tân in trực tiếp hóa đơn quyết toán A4 sắc nét chỉ với 1 cú click.

---

## 2. KIẾN TRÚC THƯ MỤC DỰ ÁN

```
frontend_ttcsn/
├── index.html               # Cổng thông tin khách hàng & đặt phòng trực tuyến (Guest Booking Portal)
├── admin.html               # Cổng tác nghiệp quản trị đa vai trò (Staff & Admin Operations Portal)
├── run_frontend.bat         # Script khởi chạy nhanh giao diện trên trình duyệt
├── README.md                # Tài liệu hướng dẫn sử dụng và kiểm thử chi tiết
└── assets/
    ├── css/
    │   └── style.css        # Hệ thống Design Tokens, Liquid Glass, Glassmorphism, Print invoice
    └── js/
        ├── api.js           # API Client tập trung, đính kèm JWT Bearer, Toast notifications, Currency format
        ├── auth.js          # Quản lý phiên đăng nhập/đăng ký, giải mã vai trò RBAC
        ├── guest.js         # Tìm kiếm phòng realtime, Wizard đặt phòng, Áp mã voucher, VNPay Gateway, QR check-in, Reviews
        ├── reception.js     # Nghiệp vụ Lễ tân: Tra cứu đơn, Check-in gán phòng chuẩn loại, Check-out trừ tiền cọc (da_thanh_toan)
        ├── housekeeping.js  # Sơ đồ buồng phòng trực quan theo tầng, đổi phòng bẩn sang sạch, ghi nhận Minibar
        └── admin.js         # Báo cáo Occupancy Rate, Doanh thu & RevPAR (Chart.js), Quản lý hạng phòng
```

---

## 3. TÍCH HỢP 100% CÁC API BACKEND

| Phân hệ API | Phương thức | Endpoint Backend | Mô tả chức năng trên Frontend |
| :--- | :--- | :--- | :--- |
| **Xác thực** | `POST` | `/api/v1/auth/login` | Đăng nhập cấp JWT Token 24h cho Khách & Nhân viên |
| **Xác thực** | `POST` | `/api/v1/auth/register` | Đăng ký tài khoản khách mới (có kiểm tra CCCD, ngày sinh) |
| **Phòng** | `GET` | `/api/v1/rooms/available` | Tra cứu loại phòng còn trống theo ngày nhận, trả và số khách |
| **Phòng** | `GET` | `/api/v1/rooms/types` | Lấy danh mục tất cả hạng phòng 5 sao |
| **Phòng** | `POST` | `/api/v1/rooms/types` | Thêm mới hạng phòng kinh doanh (chỉ dành cho Admin) |
| **Đặt phòng** | `POST` | `/api/v1/bookings` | Khởi tạo đơn đặt phòng mới (giữ chỗ 15 phút, sinh QR Code) |
| **Đặt phòng** | `GET` | `/api/v1/bookings/{id}` | Xem chi tiết đơn phòng và ảnh QR Code check-in |
| **Đặt phòng** | `GET` | `/api/v1/bookings/customer/{id}` | Lịch sử các kỳ nghỉ của khách hàng đăng nhập |
| **Voucher** | `POST` | `/api/v1/coupons/apply` | Áp dụng mã giảm giá và tính trừ tiền tức thời trên giao diện |
| **Thanh toán** | `POST` | `/api/v1/payments/create-url` | Sinh URL thanh toán chuyển tiếp qua cổng VNPay Sandbox |
| **Thanh toán** | `GET` | `/api/v1/payments/vnpay-callback` | Nhận phản hồi IPN xác nhận đơn đã thanh toán thành công |
| **Lễ tân** | `GET` | `/api/v1/reception/bookings/{id}` | Tra cứu hồ sơ đơn theo Booking ID hoặc số CCCD |
| **Lễ tân** | `POST` | `/api/v1/reception/check-in` | Làm thủ tục check-in, gán số phòng sạch (đổi phòng sang OCCUPIED) |
| **Lễ tân** | `POST` | `/api/v1/reception/check-out` | Quyết toán: tổng hợp minibar, trừ tiền trả trước `da_thanh_toan`, in hóa đơn |
| **Buồng phòng** | `GET` | `/api/v1/housekeeping/rooms` | Sơ đồ buồng phòng lọc theo trạng thái AVAILABLE, DIRTY, OCCUPIED... |
| **Buồng phòng** | `PUT` | `/api/v1/housekeeping/rooms/{id}/status` | Đổi trạng thái phòng (ví dụ: DIRTY -> AVAILABLE sau khi dọn) |
| **Buồng phòng** | `POST` | `/api/v1/housekeeping/minibar-usage` | Ghi nhận đồ uống / minibar khách đã dùng trong phòng |
| **Báo cáo** | `GET` | `/api/v1/admin/reports/occupancy` | Báo cáo tỷ lệ lấp đầy buồng phòng (Occupancy Rate) |
| **Báo cáo** | `GET` | `/api/v1/admin/reports/revenue` | Báo cáo doanh thu tiền phòng, dịch vụ và chỉ số **RevPAR** |
| **Đánh giá** | `POST` | `/api/v1/reviews` | Khách gửi nhận xét 1 - 5 sao sau khi check-out |
| **Đánh giá** | `GET` | `/api/v1/reviews/featured` | Hiển thị các đánh giá tiêu biểu từ 4 sao trở lên trên trang chủ |

---

## 4. TÀI KHOẢN MẪU KIỂM THỬ HỆ THỐNG

| Vai trò người dùng | Tên đăng nhập | Mật khẩu | Phạm vi quyền hạn trên giao diện |
| :--- | :--- | :--- | :--- |
| **Quản trị viên (ADMIN)** | `admin` | `123456` | Toàn quyền: Xem báo cáo doanh thu & RevPAR, Quản lý hạng phòng, Check-in/out |
| **Lễ tân (RECEPTIONIST)** | `reception01` | `123456` | Tra cứu đơn bằng Booking ID / CCCD, Check-in gán phòng, Check-out & In hóa đơn |
| **Buồng phòng (HOUSEKEEPING)** | `housekeeping01` | `123456` | Xem sơ đồ phòng theo tầng, đổi phòng bẩn sang sạch, ghi nhận tiêu thụ minibar |
| **Kế toán (ACCOUNTANT)** | `accountant01` | `123456` | Theo dõi báo cáo tài chính, doanh thu tiền phòng, dịch vụ và chỉ số RevPAR |
| **Khách hàng (CUSTOMER)** | `an.nguyen@gmail.com` | `123456` | Tìm phòng, đặt phòng, áp mã voucher, thanh toán VNPay, xem mã QR, gửi đánh giá |

---

## 5. HƯỚNG DẪN KHỞI CHẠY GIAO DIỆN

### Cách 1: Chạy tự động bằng file .bat (Khuyên dùng)
1. Hãy đảm bảo Backend đã chạy ở cổng `http://localhost:8080`.
2. Vào thư mục `frontend_ttcsn` và click đúp vào file **`run_frontend.bat`**.
3. Hệ thống sẽ tự động khởi động server và mở ngay trang chủ trên trình duyệt web.

### Cách 2: Chạy bằng Visual Studio Code Live Server
1. Mở thư mục `frontend_ttcsn` trong VS Code.
2. Click chuột phải vào `index.html` chọn **Open with Live Server**.

### Cách 3: Chạy bằng dòng lệnh Python hoặc Node
```bash
# Sử dụng Python
cd "E:\Thực tập cơ sở nghành\frontend_ttcsn"
python -m http.server 3000

# Hoặc sử dụng Node.js
npx serve -l 3000 .
```
Truy cập:
* Trang chủ khách hàng: `http://localhost:3000/index.html`
* Cổng quản trị nhân viên: `http://localhost:3000/admin.html`

---

## 6. KỊCH BẢN KIỂM THỬ MẪU (TEST SCENARIOS)

### Kịch bản 1: Khách hàng đặt phòng trực tuyến & Áp dụng Voucher
1. Truy cập `index.html`, bấm **Đăng nhập** với tài khoản `an.nguyen@gmail.com` / `123456`.
2. Tại thanh tìm kiếm, chọn ngày nhận phòng, ngày trả phòng và bấm **Tìm phòng trống**.
3. Bấm **Đặt phòng ngay** tại hạng phòng **Phòng Deluxe King**.
4. Tại ô mã giảm giá, nhập **`MELIAWELCOME`** và bấm **Áp dụng** ➔ Tiền phòng tự động giảm ngay 200.000 ₫!
5. Chọn hình thức **VNPay Trực Tuyến** và bấm **Xác nhận Đặt phòng**.
6. Tại màn hình mô phỏng VNPay Sandbox, bấm **Xác nhận Đã Chuyển Khoản Thành Công**.
7. Màn hình chúc mừng xuất hiện hiển thị **Mã QR Code Check-in** độc quyền được sinh tự động từ Backend.

### Kịch bản 2: Lễ tân làm thủ tục Check-in cho khách
1. Mở `admin.html`, đăng nhập bằng tài khoản lễ tân `reception01` / `123456`.
2. Tại tab **Quầy Lễ Tân**, nhập mã đơn phòng `DP2026102801` (hoặc CCCD `001202000001`) và bấm **Tìm Kiếm Đơn**.
3. Thông tin đơn phòng hiện ra ở trạng thái `CONFIRMED`. Bấm **Làm thủ tục Check-in**.
4. Chọn một số phòng sạch trống (ví dụ: `P101`) và bấm **Xác Nhận Check-in & Gán Phòng**.
5. Đơn phòng chuyển ngay sang trạng thái `CHECKED_IN`, và phòng `P101` tự động đổi sang `OCCUPIED`.

### Kịch bản 3: Buồng phòng ghi nhận tiêu thụ Minibar
1. Tại `admin.html`, chuyển sang tab **Bộ Phận Buồng Phòng**.
2. Tìm đến phòng `202` (đang có khách lưu trú), bấm **Ghi nhận Minibar**.
3. Nhập mã đơn phòng `DP2026102802`, chọn mặt hàng **Nước khoáng Evian 500ml**, số lượng `2` chai và bấm **Xác Nhận**.
4. Tiền dịch vụ (120.000 ₫) được tự động ghi nhận vào hóa đơn của đơn phòng này.

### Kịch bản 4: Lễ tân làm thủ tục Check-out & Quyết toán (Trừ tiền trả trước)
1. Tại tab **Quầy Lễ Tân**, tra cứu đơn `DP2026102802`.
2. Bấm **Làm thủ tục Check-out & Quyết toán**.
3. Hệ thống tự động tính:
   * Tiền phòng: `7.000.000 ₫`
   * Tiền dịch vụ minibar: `120.000 ₫`
   * **Đã thanh toán trước (Cọc qua VNPay)**: `-3.500.000 ₫`
   * **Số tiền khách thực tế còn phải trả tại quầy**: `3.620.000 ₫` (Chống hoàn toàn lỗi thu tiền hai lần!).
4. Bấm **Xác Nhận Thu Đủ Tiền & Hoàn Tất Check-out**.
5. Hóa đơn điện tử xuất hiện hoàn chỉnh với đầy đủ thông tin. Bấm nút **In Hóa Đơn Này** để in biên lai A4 chuẩn 5 sao.

### Kịch bản 5: Ban Giám Đốc theo dõi Báo cáo Công suất & RevPAR
1. Đăng nhập bằng tài khoản `admin` / `123456`.
2. Chuyển sang tab **Báo Cáo & Quản Trị**.
3. Xem biểu đồ Donut trực quan hiển thị **Tỷ lệ lấp đầy phòng (Occupancy Rate)** và biểu đồ Cột hiển thị **Doanh thu & Chỉ số RevPAR**.
4. Quản lý danh mục loại phòng và thử bấm **+ Thêm Hạng Phòng Mới** để mở rộng quy mô kinh doanh.
