package com.example.eventmanager.database.dao;

import androidx.room.*;
import com.example.eventmanager.model.User;
import java.util.List;

@Dao
public interface UserDao {
    @Query("SELECT * FROM user")
    List<User> getAllUsers();

    @Query("SELECT * FROM user WHERE id = :id")
    User getUserById(int id);

    @Query("SELECT * FROM user WHERE username = :username LIMIT 1")
    User getUserByUsername(String username);

    // Lấy danh sách Role Name của một User dựa trên username
    @Query("SELECT r.roleName FROM roles r " +
           "JOIN user_roles ur ON r.id = ur.roleId " +
           "JOIN user u ON u.id = ur.userId " +
           "WHERE u.username = :username")
    List<String> getUserRolesByUsername(String username);

    @Query("SELECT u.* FROM user u " +
           "JOIN user_roles ur ON u.id = ur.userId " +
           "JOIN roles r ON r.id = ur.roleId " +
           "WHERE u.username = :username AND r.roleName = :role LIMIT 1")
    User getUserByUsernameAndRole(String username, String role);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertUser(User user);

    @Update
    void updateUser(User user);

    @Delete
    void deleteUser(User user);
}
