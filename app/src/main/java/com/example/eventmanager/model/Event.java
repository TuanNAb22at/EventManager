package com.example.eventmanager.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
/**
 * Entity Event đại diện cho bảng event trong cơ sở dữ liệu Room.
 **/
@Entity(
    tableName = "event",
    foreignKeys = {
/**
 * Khóa ngoại locationId tham chiếu đến bảng Location.
 **/
        @ForeignKey(
            entity = Location.class,
            parentColumns = "id",
            childColumns = "locationId",
            onDelete = ForeignKey.SET_NULL
        ),
/**
 * Khóa ngoại createdBy tham chiếu đến bảng User.
 **/
        @ForeignKey(
            entity = User.class,
            parentColumns = "id",
            childColumns = "createdBy",
            onDelete = ForeignKey.SET_NULL
        )
    }
)
public class Event {
    /**
     * Khóa chính của bảng event.
     * autoGenerate = true nghĩa là Room tự động tăng id
     * khi thêm một sự kiện mới.
     */
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String description;
    private String eventType;
    private String startAt;
    private String endAt;
    private String bannerUri; // Đường dẫn ảnh bìa sự kiện
/**
 * Id địa điểm tổ chức sự kiện.
 *
 * @ColumnInfo(index = true) giúp tạo chỉ mục cho cột này,
 * hỗ trợ truy vấn nhanh hơn khi cần lấy sự kiện theo địa điểm.
 */
    @ColumnInfo(index = true)
    private Integer locationId;
/**
 * Id người dùng tạo sự kiện.
 */
    @ColumnInfo(index = true)
    private Integer createdBy;

    private String status;
    private double totalBudget;
    private int totalGuests;
    
    private long createdAt;
    private long updatedAt;
    /**
     * Constructor mặc định.
     *
     * Khi tạo sự kiện mới, hệ thống tự động gán:
     * - thời điểm tạo là thời điểm hiện tại
     * - thời điểm cập nhật là thời điểm hiện tại
     * - trạng thái mặc định là "Đang lên kế hoạch"
     */
    public Event() {
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.status = "Đang lên kế hoạch";
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public String getStartAt() { return startAt; }
    public void setStartAt(String startAt) { this.startAt = startAt; }
    public String getEndAt() { return endAt; }
    public void setEndAt(String endAt) { this.endAt = endAt; }
    public String getBannerUri() { return bannerUri; }
    public void setBannerUri(String bannerUri) { this.bannerUri = bannerUri; }
    public Integer getLocationId() { return locationId; }
    public void setLocationId(Integer locationId) { this.locationId = locationId; }
    public Integer getCreatedBy() { return createdBy; }
    public void setCreatedBy(Integer createdBy) { this.createdBy = createdBy; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public double getTotalBudget() { return totalBudget; }
    public void setTotalBudget(double totalBudget) { this.totalBudget = totalBudget; }
    public int getTotalGuests() { return totalGuests; }
    public void setTotalGuests(int totalGuests) { this.totalGuests = totalGuests; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}
