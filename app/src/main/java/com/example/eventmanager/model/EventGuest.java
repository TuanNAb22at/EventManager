package com.example.eventmanager.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
/**
 * Entity EventGuest đại diện cho bảng event_guest trong cơ sở dữ liệu Room.
 * */
@Entity(
    tableName = "event_guest",
    primaryKeys = {"eventId", "guestId"},
 foreignKeys = {
         /**
          * Khóa ngoại eventId tham chiếu đến id của bảng Event.
          **/
        @ForeignKey(
            entity = Event.class,
            parentColumns = "id",
            childColumns = "eventId",
            onDelete = ForeignKey.CASCADE
        ),
         /**
          * Khóa ngoại guestId tham chiếu đến id của bảng Guest.
          * */
        @ForeignKey(
            entity = Guest.class,
            parentColumns = "id",
            childColumns = "guestId",
            onDelete = ForeignKey.CASCADE
        )
    },
        /**
         * Tạo index cho guestId và eventId để tăng tốc truy vấn.
         * Điều này hữu ích khi cần tìm danh sách khách mời theo sự kiện
         * hoặc kiểm tra một khách mời đã thuộc sự kiện nào.
        */
    indices = {@Index("guestId"), @Index("eventId")}
)
public class EventGuest {
    private int eventId;
    private int guestId;
    private String status; // e.g., "INVITED", "CONFIRMED", "CANCELLED"
/**
 * Constructor dùng để tạo một bản ghi liên kết giữa sự kiện và khách mời.
 **/
    public EventGuest(int eventId, int guestId, String status) {
        this.eventId = eventId;
        this.guestId = guestId;
        this.status = status;
    }

    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }
    public int getGuestId() { return guestId; }
    public void setGuestId(int guestId) { this.guestId = guestId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
