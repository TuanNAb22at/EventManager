package com.example.eventmanager.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.eventmanager.database.dao.TaskDao;
import com.example.eventmanager.model.Task;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskRepository {
    private final TaskDao taskDao;
    private final ExecutorService executorService;

    public TaskRepository(TaskDao taskDao) {
        this.taskDao = taskDao;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Task>> getAllTasks() {
        return taskDao.getAllTasks();
    }

    public LiveData<List<Task>> getTasksByEventId(int eventId) {
        return taskDao.getTasksByEventId(eventId);
    }

    public LiveData<List<Task>> getTasksByAssignedTo(int userId) {
        return taskDao.getTasksByAssignedTo(userId);
    }

    public LiveData<List<Task>> getTasksByEventAndAssignedTo(int eventId, int userId) {
        return taskDao.getTasksByEventAndAssignedTo(eventId, userId);
    }

    public LiveData<Task> getTaskById(int id) {
        MutableLiveData<Task> data = new MutableLiveData<>();
        executorService.execute(() -> data.postValue(taskDao.getTaskById(id)));
        return data;
    }

    public void insert(Task task) {
        executorService.execute(() -> taskDao.insertTask(task));
    }

    public void update(Task task) {
        executorService.execute(() -> taskDao.updateTask(task));
    }

    public void delete(Task task) {
        executorService.execute(() -> taskDao.deleteTask(task));
    }
}
