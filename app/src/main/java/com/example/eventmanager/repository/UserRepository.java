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

    // Xóa phương thức lỗi và thay bằng phương thức lấy User theo Username
    // Việc kiểm tra mật khẩu sẽ thực hiện ở Service/Activity bằng PasswordUtils
    public User getUserByUsername(String username) {
        return userDao.getUserByUsername(username);
    }

    // Thêm phương thức hỗ trợ Role-Based Access Control
    public User getUserByUsernameAndRole(String username, String role) {
        return userDao.getUserByUsernameAndRole(username, role);
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
