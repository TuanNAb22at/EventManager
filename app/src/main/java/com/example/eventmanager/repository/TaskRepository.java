package com.example.eventmanager.repository;

import com.example.eventmanager.database.dao.TaskDao;
import com.example.eventmanager.model.Task;
import java.util.List;

public class TaskRepository {
    private final TaskDao taskDao;

    public TaskRepository(TaskDao taskDao) {
        this.taskDao = taskDao;
    }

    public List<Task> getAllTasks() {
        return taskDao.getAllTasks();
    }

    public List<Task> getTasksByEventId(int eventId) {
        return taskDao.getTasksByEventId(eventId);
    }

    public Task getTaskById(int id) {
        return taskDao.getTaskById(id);
    }

    public void insert(Task task) {
        taskDao.insertTask(task);
    }

    public void update(Task task) {
        taskDao.updateTask(task);
    }

    public void delete(Task task) {
        taskDao.deleteTask(task);
    }
}
