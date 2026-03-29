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

    @Query("SELECT COUNT(*) FROM task t " +
           "INNER JOIN task_assignee ta ON t.id = ta.taskId " +
           "WHERE ta.userId = :userId AND t.status != 'DONE'")
    int getPendingTaskCountForUserSync(int userId);

    @Query("SELECT COUNT(*) FROM task WHERE status != 'DONE'")
    int getPendingTaskCountAllSync();

    @Query("SELECT COUNT(*) FROM task t " +
           "INNER JOIN task_assignee ta ON t.id = ta.taskId " +
           "WHERE ta.userId = :userId AND t.eventId = :eventId AND t.status != 'DONE'")
    int getPendingTaskCountForUserByEventSync(int userId, int eventId);

    @Query("SELECT COUNT(*) FROM task WHERE eventId = :eventId AND status != 'DONE'")
    int getPendingTaskCountByEventSync(int eventId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertTask(Task task);

    @Update
    void updateTask(Task task);

    @Delete
    void deleteTask(Task task);
}
