# Hệ Thống Tính Thuế Thu Nhập Cá Nhân (PIT System)
> Cài đặt bằng **Java (JDK 23 / 17), Spring Boot 3.2.1, Maven và kiểm thử JUnit 5 + JaCoCo** phục vụ môn học **Kiểm thử Phần mềm**.

---

## 1. Giới thiệu & Phạm vi chức năng đã xây dựng

Hệ thống đã được cài đặt hoàn thiện 100% về mã nguồn, giao diện Web trực quan (Bootstrap 5, tiếng Việt), cơ chế bảo mật phiên làm việc và các REST API tương ứng:

### 3.1. Nhóm chức năng 1: Người nộp thuế (Taxpayer)
1. **Đăng ký tài khoản**: Đăng ký người nộp thuế mới với đầy đủ thông tin: Họ tên, Mã số thuế (MST 10 số), Căn cước công dân (CCCD 12 số), Số điện thoại, Email. Kiểm tra tính hợp lệ dữ liệu chặt chẽ (`ValidationUtil`).
2. **Khai báo thuế**: Kê khai tiền lương, tiền thưởng, thu nhập chịu thuế khác, thu nhập miễn thuế, số người phụ thuộc, đóng góp từ thiện nhân đạo theo kỳ tính thuế (tháng hoặc năm).
3. **Tính thuế tự động**: Áp dụng biểu thuế lũy tiến từng phần **7 bậc** theo Luật Thuế TNCN Việt Nam hiện hành; tự động tính giảm trừ bản thân (**11.000.000 VNĐ/tháng**), giảm trừ người phụ thuộc (**4.400.000 VNĐ/tháng/người**), bảo hiểm bắt buộc (**10.5%** gồm BHXH 8%, BHYT 1.5%, BHTN 1% có trần lương bảo hiểm). Hiển thị bảng diễn giải chi tiết từng bậc thuế và thu nhập thực nhận (Net).
4. **Đóng thuế trực tuyến**: Hỗ trợ nộp thuế điện tử qua các phương thức mô phỏng: Cổng quét mã **VNPAY-QR**, Chuyển khoản ngân hàng, Thẻ thanh toán. Cập nhật trạng thái tờ khai sang `PAID` và phát hành **Biên lai nộp thuế điện tử** có mã tra cứu và hỗ trợ in ấn.

### 3.2. Nhóm chức năng 2: Quản trị viên / Cán bộ thuế (Admin)
1. **Theo dõi danh sách tờ khai**: Bảng hiển thị toàn bộ tờ khai thuế của người dân trong hệ thống kèm 3 thẻ chỉ số thống kê thời gian thực (Tổng tờ khai, Tiền thuế đã thu vào KBNN, Tiền thuế còn nợ chờ nộp).
2. **Bộ lọc & Tìm kiếm nâng cao**: Lọc theo Năm kê khai, Tháng kê khai, Trạng thái (`Đã nộp`, `Chờ nộp`) và tìm kiếm theo Họ tên, Mã số thuế, CCCD.
3. **Xuất báo cáo quyết toán**: Nút **"Xuất Báo Cáo"** tải về file CSV mã hóa chuẩn UTF-8 BOM, đảm bảo mở trực tiếp bằng Microsoft Excel không bao giờ bị lỗi font tiếng Việt.
4. **Cấu hình tham số hệ thống**: Cho phép cán bộ thuế điều chỉnh linh hoạt mức giảm trừ gia cảnh (bản thân, người phụ thuộc), tỷ lệ bảo hiểm và trần lương khi nhà nước ban hành chính sách mới; kèm nút khôi phục cấu hình mặc định.

---

## 2. Hướng dẫn Chạy Hệ thống

### Cách 1: Chạy nhanh bằng file `.bat`
- **Khởi động Web App**: Nhấp đúp vào file [`run.bat`](file:///c:/Users/icomh/Desktop/test/run.bat)
- **Chạy 98 Unit Tests & JaCoCo Coverage**: Nhấp đúp vào file [`test.bat`](file:///c:/Users/icomh/Desktop/test/test.bat)

### Cách 2: Chạy bằng dòng lệnh (Command Line)
```powershell
# Chạy ứng dụng Spring Boot
java -jar target\tax-system-1.0.0.jar

# Hoặc chạy bằng Maven
& "C:\Program Files\NetBeans-25\netbeans\java\maven\bin\mvn.cmd" spring-boot:run
```

Sau khi khởi động, truy cập trình duyệt tại địa chỉ:
👉 **`http://localhost:8080`**

---

## 3. Tài khoản Đăng nhập Mẫu (Seed Data)

Hệ thống đã nạp sẵn các tài khoản thử nghiệm kèm nút điền sẵn 1-click trên giao diện:

| Vai trò | Tên đăng nhập | Mật khẩu | Chức năng chính |
| :--- | :--- | :--- | :--- |
| **Người nộp thuế** | `nguyenvana` | `User@123` | Kê khai thu nhập, tính thuế, đóng thuế, xem biên lai |
| **Cán bộ thuế / Admin** | `admin` | `Admin@123` | Theo dõi danh sách, xuất báo cáo CSV, cấu hình mức giảm trừ |

---

## 4. Kết quả Kiểm thử Đơn vị & Độ phủ (JUnit 5 + JaCoCo)

Hệ thống đã tích hợp sẵn bộ kiểm thử đơn vị tự động với **98 Test Cases** sử dụng **JUnit 5**:
- `TaxCalculatorServiceTest.java`: 29 ca kiểm thử bao phủ toàn bộ 7 bậc thuế lũy tiến, các mốc biên (5tr, 10tr, 18tr, 32tr, 52tr, 80tr), kiểm thử tham số hóa `@ParameterizedTest`, đối chiếu trùng khớp 100% giữa phương pháp lũy tiến và công thức rút gọn.
- `ValidationUtilTest.java`: 32 ca kiểm thử dữ liệu biên (CCCD 12 số, MST 10 số, SĐT 10 số, Email, Lương âm, Người phụ thuộc).
- `UserServiceTest.java`: 5 ca kiểm thử đăng ký, đăng nhập, trùng tên, phân quyền.
- `DeclarationServiceTest.java`: 6 ca kiểm thử tạo tờ khai, lọc, tìm kiếm, xuất CSV.
- `PaymentServiceTest.java`: 4 ca kiểm thử đóng thuế, chặn nộp trùng, tra cứu biên lai.
- `AuthControllerTest.java`: 5 ca kiểm thử REST API xác thực và session.
- `TaxApiControllerTest.java`: 5 ca kiểm thử REST API tính thuế và nộp thuế.
- `AdminApiControllerTest.java`: 6 ca kiểm thử REST API quản trị, bảo mật phân quyền (chặn 403 nếu không phải Admin).
- `ModelTest.java`: 6 ca kiểm thử getters/setters.

### Kết quả đo lường độ phủ mã nguồn:
- **Trạng thái**: `Tests run: 98, Failures: 0, Errors: 0, Skipped: 0` (PASS 100%)
- **Độ phủ mã nguồn (JaCoCo Code Coverage)**: Đạt **90%** toàn bộ dự án (Tầng Model 99%, Tầng Util 97%, Tầng Service 93%).
- File báo cáo độ phủ chi tiết: [`target/site/jacoco/index.html`](file:///c:/Users/icomh/Desktop/test/target/site/jacoco/index.html).

---

## 5. Cấu trúc Thư mục Dự án

```
c:\Users\icomh\Desktop\test\
├── pom.xml                               # Cấu hình Maven (Spring Boot 3.2.1, JUnit 5, JaCoCo)
├── run.bat                               # Script khởi động ứng dụng Web bằng 1 click
├── test.bat                              # Script chạy 98 Unit tests và xuất báo cáo JaCoCo
├── src/
│   ├── main/
│   │   ├── java/com/pit/
│   │   │   ├── PitApplication.java       # Entry point khởi chạy Spring Boot
│   │   │   ├── model/                    # Các thực thể dữ liệu
│   │   │   │   ├── User.java             # Tài khoản Taxpayer / Admin
│   │   │   │   ├── TaxDeclaration.java   # Tờ khai thuế TNCN
│   │   │   │   ├── TaxBracket.java       # Bậc thuế lũy tiến
│   │   │   │   ├── SystemConfig.java     # Tham số giảm trừ & bảo hiểm
│   │   │   │   ├── PaymentReceipt.java   # Biên lai nộp thuế điện tử
│   │   │   │   └── TaxCalculationResult.java # Kết quả tính thuế chi tiết 7 bậc
│   │   │   ├── service/                  # Tầng nghiệp vụ xử lý logic
│   │   │   │   ├── TaxCalculatorService.java  # Logic tính thuế TNCN cốt lõi
│   │   │   │   ├── UserService.java      # Quản lý người dùng & xác thực
│   │   │   │   ├── DeclarationService.java # Quản lý tờ khai & xuất CSV
│   │   │   │   ├── PaymentService.java   # Xử lý đóng thuế & cấp biên lai
│   │   │   │   └── ConfigService.java    # Cấu hình tham số hệ thống
│   │   │   ├── controller/               # Các REST API Controllers
│   │   │   │   ├── AuthController.java   # /api/auth (Login, Register, Logout)
│   │   │   │   ├── TaxApiController.java   # /api/tax (Calculate, Declare, Pay)
│   │   │   │   └── AdminApiController.java # /api/admin (List, Export, Config)
│   │   │   └── util/
│   │   │       └── ValidationUtil.java   # Kiểm tra tính hợp lệ dữ liệu
│   │   └── resources/
│   │       ├── application.properties
│   │       └── static/                   # Giao diện Web HTML5/CSS3/JS tiếng Việt
│   │           ├── index.html            # Trang chủ, tính nhẩm, đăng ký/đăng nhập
│   │           ├── dashboard.html        # Kê khai thuế, đóng thuế trực tuyến, xem biên lai
│   │           ├── admin.html            # Theo dõi danh sách, xuất báo cáo CSV, cấu hình
│   │           ├── css/style.css
│   │           └── js/app.js
│   └── test/
│       └── java/com/pit/                 # 98 Test Cases JUnit 5
│           ├── TaxCalculatorServiceTest.java
│           ├── ValidationUtilTest.java
│           ├── UserServiceTest.java
│           ├── DeclarationServiceTest.java
│           ├── PaymentServiceTest.java
│           ├── AuthControllerTest.java
│           ├── TaxApiControllerTest.java
│           ├── AdminApiControllerTest.java
│           ├── ModelTest.java
│           └── MockHttpSession.java
└── target/
    ├── tax-system-1.0.0.jar              # File thực thi độc lập của hệ thống
    └── site/jacoco/index.html            # Báo cáo độ phủ mã nguồn JaCoCo HTML
```
