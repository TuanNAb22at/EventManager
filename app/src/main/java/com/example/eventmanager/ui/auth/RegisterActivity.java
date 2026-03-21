package com.example.eventmanager.ui.auth;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import androidx.appcompat.app.AppCompatActivity;
import com.example.eventmanager.databinding.ActivityRegisterBinding;
import com.example.eventmanager.database.AppDatabase;
import com.example.eventmanager.model.User;
import com.example.eventmanager.model.Role;
import com.example.eventmanager.model.UserRole;
import com.example.eventmanager.utils.PasswordUtils;
import com.example.eventmanager.utils.SessionManager;
import com.google.android.material.snackbar.Snackbar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private String selectedRole = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupRoleDropdown();

        binding.btnRegister.setOnClickListener(v -> handleRegister());
        binding.tvLogin.setOnClickListener(v -> finish());
    }

    private void setupRoleDropdown() {
        String[] displayRoles = {"Người tổ chức", "Nhà cung cấp"};
        String[] actualRoles = {SessionManager.ROLE_ORGANIZER, SessionManager.ROLE_VENDOR};
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayRoles);
        binding.actvRole.setAdapter(adapter);
        
        binding.actvRole.setOnItemClickListener((parent, view, position, id) -> {
            selectedRole = actualRoles[position];
        });
    }

    private void handleRegister() {
        String fullName = binding.etFullName.getText().toString().trim();
        String username = binding.etUsername.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        String confirmPassword = binding.etConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(fullName)) {
            binding.tilFullName.setError("Vui lòng nhập tên đầy đủ");
            return;
        }
        if (TextUtils.isEmpty(username)) {
            binding.tilUsername.setError("Vui lòng nhập tên đăng nhập");
            return;
        }
        if (TextUtils.isEmpty(selectedRole)) {
            binding.tilRole.setError("Vui lòng chọn vai trò");
            return;
        } else {
            binding.tilRole.setError(null);
        }
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            binding.tilPassword.setError("Mật khẩu phải từ 6 ký tự");
            return;
        }
        if (!password.equals(confirmPassword)) {
            binding.tilConfirmPassword.setError("Mật khẩu xác thực không khớp");
            return;
        }

        binding.btnRegister.setEnabled(false);

        executorService.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            try {
                // Đảm bảo các role cơ bản tồn tại trong database
                ensureRolesExist(db);

                // Thao tác 1: Check user tồn tại
                User existingUser = db.userDao().getUserByUsername(username);
                
                if (existingUser != null) {
                    runOnUiThread(() -> {
                        binding.btnRegister.setEnabled(true);
                        showCustomMessage(binding.getRoot(), "Tên đăng nhập này đã tồn tại!", true);
                    });
                } else {
                    // Thao tác 2: Lấy Role ID
                    Role role = db.roleDao().getRoleByName(selectedRole);
                    if (role == null) {
                        runOnUiThread(() -> {
                            binding.btnRegister.setEnabled(true);
                            showCustomMessage(binding.getRoot(), "Lỗi: Vai trò '" + selectedRole + "' không tồn tại trong hệ thống", true);
                        });
                        return;
                    }

                    // Thao tác 3: Insert user mới
                    String hashedPw = PasswordUtils.hashPassword(password);
                    User newUser = new User(fullName, username, hashedPw);
                    long userId = db.userDao().insertUser(newUser);
                    
                    // Thao tác 4: Gán role cho user
                    if (userId > 0) {
                        db.userRoleDao().insertUserRole(new UserRole((int) userId, role.getId()));
                    }

                    runOnUiThread(() -> {
                        showCustomMessage(binding.getRoot(), "Đăng ký thành công!", false);
                        binding.getRoot().postDelayed(this::finish, 1200);
                    });
                }
            } catch (Exception e) {
                runOnUiThread(() -> {
                    binding.btnRegister.setEnabled(true);
                    showCustomMessage(binding.getRoot(), "Lỗi: " + e.getMessage(), true);
                });
            }
        });
    }

    private void ensureRolesExist(AppDatabase db) {
        if (db.roleDao().getRoleCount() == 0) {
            db.roleDao().insertRole(new Role(SessionManager.ROLE_ORGANIZER));
            db.roleDao().insertRole(new Role(SessionManager.ROLE_VENDOR));
            db.roleDao().insertRole(new Role(SessionManager.ROLE_STAFF));
        }
    }

    private void showCustomMessage(View view, String message, boolean isError) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        if (isError) {
            snackbar.setBackgroundTint(getResources().getColor(android.R.color.holo_red_dark));
        } else {
            snackbar.setBackgroundTint(getResources().getColor(com.google.android.material.R.color.design_default_color_primary));
        }
        snackbar.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
