package com.example.eventmanager.database.dao;

import androidx.room.*;
import com.example.eventmanager.model.Budget;
import java.util.List;

@Dao
public interface BudgetDao {
    @Query("SELECT * FROM budget")
    List<Budget> getAllBudgets();

    @Query("SELECT * FROM budget WHERE eventId = :eventId")
    List<Budget> getBudgetsByEventId(int eventId);

    @Query("SELECT * FROM budget WHERE id = :id")
    Budget getBudgetById(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBudget(Budget budget);

    @Update
    void updateBudget(Budget budget);

    @Delete
    void deleteBudget(Budget budget);
}
