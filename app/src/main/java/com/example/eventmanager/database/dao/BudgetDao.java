package com.example.eventmanager.database.dao;

import androidx.room.*;
import com.example.eventmanager.model.Budget;
import java.util.List;

/**
 * DAO (Data Access Object) cho bảng Budget.
 * Interface này định nghĩa các thao tác với database liên quan đến Budget.
 */
@Dao
public interface BudgetDao {

    /**
     * Lấy tất cả các budget trong database
     */
    @Query("SELECT * FROM budget")
    List<Budget> getAllBudgets();

    /**
     * Lấy danh sách budget theo eventId
     */
    @Query("SELECT * FROM budget WHERE eventId = :eventId")
    List<Budget> getBudgetsByEventId(int eventId);

    /**
     * Lấy một budget theo id
     */
    @Query("SELECT * FROM budget WHERE id = :id")
    Budget getBudgetById(int id);

    /**
     * Lấy một budget theo id  (phiên bản đồng bộ)
     */
    @Query("SELECT * FROM budget WHERE id = :id")
    Budget getBudgetByIdSync(int id);

    /**
     * Thêm budget mới vào database
     * Nếu bị trùng thì sẽ ghi đè dữ liệu cũ
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBudget(Budget budget);

    /**
     * Cập nhật budget
     */
    @Update
    void updateBudget(Budget budget);

    /**
     * Xóa budget khỏi database
     */
    @Delete
    void deleteBudget(Budget budget);
}