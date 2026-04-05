# Event Manager

Ứng dụng Android hỗ trợ quản lý sự kiện theo mô hình phân quyền, tập trung vào các nghiệp vụ vận hành như tạo sự kiện, phân công công việc, quản lý ngân sách, nhà cung cấp, khách mời và địa điểm tổ chức.

## Tổng quan

`Event Manager` là một dự án Android Native được xây dựng bằng `Java` và `Android SDK`, sử dụng `Room` làm lớp lưu trữ dữ liệu cục bộ, kết hợp `LiveData/ViewModel`, `ViewBinding`, `DataBinding`, `Material Design` và `Glide`.

Ứng dụng được thiết kế theo hướng phục vụ quy trình tổ chức sự kiện đầu cuối:

- Quản lý sự kiện và thông tin chi tiết của từng event
- Quản lý danh sách địa điểm tổ chức
- Quản lý vendor theo loại dịch vụ
- Quản lý khách mời và trạng thái tham gia
- Phân công task cho nhân sự
- Theo dõi ngân sách và chi phí theo từng event
- Phân quyền người dùng giữa `Organizer` và `Staff`

## Điểm nổi bật

- Xây dựng mô hình dữ liệu quan hệ với `Room`, bao phủ các thực thể như `Event`, `User`, `Role`, `Vendor`, `Guest`, `Location`, `Task`, `Budget`, `Schedule`, `Feedback`
- Hỗ trợ các quan hệ nhiều-nhiều thông qua các bảng trung gian như `event_vendor`, `event_guest`, `task_assignee`, `user_role`
- Áp dụng phân quyền theo vai trò để kiểm soát khả năng xem, sửa, xóa và thực hiện nghiệp vụ trong ứng dụng
- Tích hợp quản lý task theo event, trạng thái hoàn thành, độ ưu tiên và người được giao
- Tích hợp quản lý ngân sách, chi phí phát sinh, phần trăm sử dụng ngân sách và phần ngân sách còn lại
- Cho phép tìm kiếm và lọc dữ liệu ở nhiều màn hình như event, venue, vendor, user, guest và task
- Hỗ trợ chọn ảnh từ thiết bị cho banner sự kiện, avatar người dùng và hình ảnh địa điểm
- Có cơ chế khởi tạo dữ liệu ban đầu cho role và tài khoản Organizer mặc định

## Tính năng chính

### 1. Xác thực và phân quyền

- Đăng nhập, đăng ký, đổi mật khẩu
- Ghi nhớ phiên đăng nhập với `SharedPreferences`
- Quản lý vai trò `ORGANIZER` và `STAFF`
- Điều hướng và ẩn/hiện tính năng theo quyền của người dùng

### 2. Quản lý sự kiện

- Tạo, cập nhật, xóa sự kiện
- Gán loại sự kiện, thời gian bắt đầu/kết thúc, mô tả, banner và địa điểm
- Hiển thị danh sách sự kiện sắp tới kèm tìm kiếm và lọc theo loại
- Xem chi tiết sự kiện và truy cập nhanh đến các module liên quan

### 3. Quản lý địa điểm

- Thêm, sửa, xóa địa điểm tổ chức
- Lưu thông tin như tên, địa chỉ, sức chứa, diện tích, giá thuê, mô tả, ảnh
- Tìm kiếm theo tên/địa chỉ
- Lọc địa điểm theo sức chứa và sắp xếp theo giá

### 4. Quản lý nhà cung cấp

- Thêm, sửa, xóa vendor
- Phân loại vendor theo `serviceType`
- Tìm kiếm và lọc theo loại dịch vụ
- Gán vendor vào từng sự kiện thông qua bảng liên kết

### 5. Quản lý khách mời

- Thêm khách vào hệ thống
- Quản lý danh sách khách đã mời và chưa mời theo từng event
- Tìm kiếm khách và lọc theo trạng thái mời
- Theo dõi số lượng khách tham gia của từng sự kiện

### 6. Quản lý công việc

- Tạo task theo event
- Gán task cho nhân sự `Staff`
- Quản lý deadline, mức ưu tiên và trạng thái hoàn thành
- Tính toán số lượng task pending theo event và theo người dùng
- Staff chỉ thấy và cập nhật các task được giao cho mình

### 7. Quản lý ngân sách

- Thiết lập tổng ngân sách cho event
- Thêm các khoản chi theo danh mục
- Tính tổng đã chi, ngân sách còn lại và phần trăm sử dụng
- Tách riêng danh sách event đã có ngân sách để theo dõi

### 8. Hồ sơ người dùng và quản trị tài khoản

- Cập nhật hồ sơ cá nhân, avatar, phần giới thiệu và sở thích
- Organizer có thể quản lý danh sách tài khoản
- Hỗ trợ tạo user mới, đổi quyền và reset mật khẩu mặc định

## Công nghệ sử dụng

- Ngôn ngữ: `Java 11`
- Nền tảng: `Android SDK`
- UI: `Material Design`, `ViewBinding`, `DataBinding`, `ConstraintLayout`
- Local database: `Room`
- State / lifecycle: `LiveData`, `ViewModel`
- Ảnh: `Glide`
- Location: `Google Play Services Location`
- Build tool: `Gradle Kotlin DSL`

## Kiến trúc và thiết kế

Project được tổ chức theo hướng phân tách trách nhiệm:

- `model`: định nghĩa entity và domain model
- `database/dao`: quản lý truy vấn dữ liệu với `Room`
- `repository`: lớp trung gian giữa data source và UI
- `viewmodel`: cung cấp dữ liệu cho màn hình theo lifecycle
- `ui`: nhóm các màn hình theo module nghiệp vụ
- `adapter`: adapter cho danh sách và thành phần hiển thị
- `utils`: tiện ích cho session, validation, date, password

Các module nghiệp vụ chính hiện có:

- `auth`
- `main`
- `event`
- `location`
- `vendor`
- `guest`
- `task`
- `budget`
- `profile`
- `schedule`
- `feedback`

## Cấu trúc dữ liệu chính

Một số thực thể và quan hệ nổi bật:

- `Event` liên kết với `Location` và `User`
- `Task` liên kết với `Event`, hỗ trợ gán người thực hiện qua `TaskAssignee`
- `Budget` gắn với từng `Event`
- `Vendor` được gán cho `Event` qua `EventVendor`
- `Guest` được gán cho `Event` qua `EventGuest`
- `User` nhận quyền thông qua `UserRole`

Thiết kế này giúp ứng dụng mô phỏng khá sát quy trình vận hành thực tế của một hệ thống quản lý sự kiện.

## Tài khoản mặc định

Khi ứng dụng khởi tạo database lần đầu, hệ thống seed sẵn:

- Username: `nguyentuan`
- Password: `123456`
- Role: `ORGANIZER`

Ngoài ra, người dùng mới đăng ký từ màn hình đăng ký sẽ được gán role `STAFF`.

## Cách chạy dự án

### Yêu cầu

- `Android Studio`
- `Android SDK` với `minSdk 24`
- Thiết bị thật hoặc Android Emulator

### Các bước

1. Clone repository:

```bash
git clone https://github.com/TuanNAb22at/EventManager.git
```

2. Mở project bằng `Android Studio`

3. Sync `Gradle`

4. Chạy ứng dụng trên emulator hoặc thiết bị Android

Hoặc build bằng command line:

```bash
./gradlew assembleDebug
```

Trên Windows:

```powershell
.\gradlew.bat assembleDebug
```

## Giá trị kỹ thuật của dự án

Dự án này thể hiện các điểm mạnh chính:

- Thiết kế và quản lý dữ liệu quan hệ trên mobile app
- Tổ chức code theo module nghiệp vụ rõ ràng
- Xây dựng workflow CRUD tương đối đầy đủ cho nhiều domain khác nhau
- Áp dụng phân quyền trong ứng dụng Android
- Kết hợp xử lý dữ liệu, UI state và business rule trong bối cảnh app thực tế

## Hướng phát triển

- Tích hợp backend/API để đồng bộ dữ liệu thay vì chỉ lưu local
- Hoàn thiện màn hình bản đồ cho địa điểm
- Bổ sung test cho DAO, repository và business rules
- Tối ưu hóa UI/UX và chuẩn hóa kiến trúc theo MVVM hoàn chỉnh hơn

## Tác giả

**Nguyễn Anh Tuấn**  
GitHub: [TuanNAb22at](https://github.com/TuanNAb22at)
