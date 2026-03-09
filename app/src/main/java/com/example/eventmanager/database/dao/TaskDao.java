package com.example.eventmanager.database.dao;

import androidx.room.*;
import com.example.eventmanager.model.Task;
import java.util.List;

@Dao
public interface TaskDao {
    @Query("SELECT * FROM task")
    List<Task> getAllTasks();

    @Query("SELECT * FROM task WHERE eventId = :eventId")
    List<Task> getTasksByEventId(int eventId);

    @Query("SELECT * FROM task WHERE id = :id")
    Task getTaskById(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTask(Task task);

    @Update
    void updateTask(Task task);

    @Delete
    void deleteTask(Task task);
}
