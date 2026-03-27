package com.example.eventmanager.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.example.eventmanager.model.Task;
import java.util.List;

@Dao
public interface TaskDao {
    @Query("SELECT * FROM task ORDER BY createdAt DESC")
    LiveData<List<Task>> getAllTasks();

    @Query("SELECT * FROM task WHERE eventId = :eventId ORDER BY priority DESC, createdAt DESC")
    LiveData<List<Task>> getTasksByEventId(int eventId);

    @Query("SELECT * FROM task WHERE assignedTo = :userId ORDER BY priority DESC, createdAt DESC")
    LiveData<List<Task>> getTasksByAssignedTo(int userId);

    @Query("SELECT * FROM task WHERE eventId = :eventId AND assignedTo = :userId ORDER BY priority DESC, createdAt DESC")
    LiveData<List<Task>> getTasksByEventAndAssignedTo(int eventId, int userId);

    @Query("SELECT * FROM task WHERE id = :id")
    Task getTaskById(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertTask(Task task);

    @Update
    void updateTask(Task task);

    @Delete
    void deleteTask(Task task);
}
