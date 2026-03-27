package com.example.eventmanager.database.dao;

import androidx.room.*;
import com.example.eventmanager.model.TaskAssignee;
import com.example.eventmanager.model.User;
import java.util.List;

@Dao
public interface TaskAssigneeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TaskAssignee taskAssignee);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<TaskAssignee> assignees);

    @Query("DELETE FROM task_assignee WHERE taskId = :taskId")
    void deleteByTaskId(int taskId);

    @Query("SELECT u.* FROM user u " +
           "JOIN task_assignee ta ON u.id = ta.userId " +
           "WHERE ta.taskId = :taskId")
    List<User> getAssigneesForTask(int taskId);
}
