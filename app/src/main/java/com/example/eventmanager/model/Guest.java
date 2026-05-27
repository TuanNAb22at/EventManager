package com.example.eventmanager.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Entity Guest đại diện cho bảng guest trong cơ sở dữ liệu Room.
 *
 * Bảng này dùng để lưu thông tin khách mời trong hệ thống quản lý sự kiện.
 * Mỗi khách mời có các thông tin cơ bản như tên, email, số điện thoại,
 * trạng thái và thời gian tạo/cập nhật.
 */
@Entity(tableName = "guest")
public class Guest {
    /**
     * Khóa chính của bảng guest.
     * autoGenerate = true nghĩa là Room sẽ tự động tăng id
     * mỗi khi thêm một khách mời mới.
     */
    @PrimaryKey(autoGenerate = true)
    private int id;

    /**
     * Id của sự kiện mà khách mời đang được gắn vào.
     *
     * Trường này có thể null nếu khách mời chưa được mời vào sự kiện nào.
     * Trong hệ thống, khách mời cũng có thể được liên kết với sự kiện
     * thông qua bảng trung gian EventGuest.
     */
    private Integer eventId; // Để null nếu chưa được mời vào sự kiện nào

    private String name;
    private String email;
    private String phone;
    private String status;
    
    private long createdAt;
    private long updatedAt;
    /**
     * Constructor mặc định.
     * Khi tạo mới một khách mời, hệ thống tự động gán thời gian tạo
     * và thời gian cập nhật bằng thời điểm hiện tại.
     */
    public Guest() {
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Integer getEventId() { return eventId; }
    public void setEventId(Integer eventId) { this.eventId = eventId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    /**
     * Lấy trạng thái khách mời.
     */
    public String getStatus() { return status; }
    /**
     * Gán trạng thái khách mời.
     */
    public void setStatus(String status) { this.status = status; }
    /**
     * Lấy thời điểm tạo khách mời.
     */
    public long getCreatedAt() { return createdAt; }
    /**
     * Gán thời điểm tạo khách mời.
     */
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    /**
     * Lấy thời điểm cập nhật khách mời.
     */
    public long getUpdatedAt() { return updatedAt; }
    /**
     * Gán thời điểm cập nhật khách mời.
     */
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}
