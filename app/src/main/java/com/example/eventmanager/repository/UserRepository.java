package com.example.eventmanager.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.eventmanager.database.dao.UserDao;
import com.example.eventmanager.model.User;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserRepository {
    private final UserDao userDao;
    private final ExecutorService executorService;

    public UserRepository(UserDao userDao) {
        this.userDao = userDao;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<User>> getAllUsers() {
        MutableLiveData<List<User>> data = new MutableLiveData<>();
        executorService.execute(() -> data.postValue(userDao.getAllUsers()));
        return data;
    }

    public LiveData<User> getUserById(int id) {
        MutableLiveData<User> data = new MutableLiveData<>();
        executorService.execute(() -> data.postValue(userDao.getUserById(id)));
        return data;
    }

    public LiveData<User> getUserByUsername(String username) {
        MutableLiveData<User> data = new MutableLiveData<>();
        executorService.execute(() -> data.postValue(userDao.getUserByUsername(username)));
        return data;
    }

    public LiveData<User> getUserByUsernameAndRole(String username, String role) {
        MutableLiveData<User> data = new MutableLiveData<>();
        executorService.execute(() -> data.postValue(userDao.getUserByUsernameAndRole(username, role)));
        return data;
    }

    public void insert(User user) {
        executorService.execute(() -> userDao.insertUser(user));
    }

    public void update(User user) {
        executorService.execute(() -> userDao.updateUser(user));
    }

    public void delete(User user) {
        executorService.execute(() -> userDao.deleteUser(user));
    }
}
