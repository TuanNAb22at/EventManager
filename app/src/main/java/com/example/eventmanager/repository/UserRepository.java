package com.example.eventmanager.repository;

import com.example.eventmanager.database.dao.UserDao;
import com.example.eventmanager.model.User;
import java.util.List;

public class UserRepository {
    private final UserDao userDao;

    public UserRepository(UserDao userDao) {
        this.userDao = userDao;
    }

    public List<User> getAllUsers() {
        return userDao.getAllUsers();
    }

    public User getUserById(int id) {
        return userDao.getUserById(id);
    }

    public User getUserByUsernameAndPassword(String username, String password) {
        return userDao.getUserByUsernameAndPassword(username, password);
    }

    public User getUserByUsername(String username) {
        return userDao.getUserByUsername(username);
    }

    public void insert(User user) {
        userDao.insertUser(user);
    }

    public void update(User user) {
        userDao.updateUser(user);
    }

    public void delete(User user) {
        userDao.deleteUser(user);
    }
}
