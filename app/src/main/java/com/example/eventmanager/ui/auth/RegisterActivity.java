package com.example.eventmanager.ui.auth;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
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
    private boolean isProcessing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnRegister.setOnClickListener(v -> handleRegister());
        binding.tvLogin.setOnClickListener(v -> finish());
    }

    private void handleRegister() {
        if (isProcessing) return;

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
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            binding.tilPassword.setError("Mật khẩu phải từ 6 ký tự");
            return;
        }
        if (!password.equals(confirmPassword)) {
            binding.tilConfirmPassword.setError("Mật khẩu xác thực không khớp");
            return;
        }

        setLoadingState(true);

        executorService.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            try {
                // Thao tác 1: Check user tồn tại
                User existingUser = db.userDao().getUserByUsername(username);
                
                if (existingUser != null) {
                    runOnUiThread(() -> {
                        setLoadingState(false);
                        showCustomMessage(binding.getRoot(), "Tên đăng nhập này đã tồn tại!", true);
                    });
                } else {
                    // Thao tác 2: Lấy Role ID cho STAFF (mặc định cho đăng ký mới)
                    Role role = db.roleDao().getRoleByName(SessionManager.ROLE_STAFF);
                    if (role == null) {
                        // Nếu chưa có role trong DB, tự tạo
                        db.roleDao().insertRole(new Role(SessionManager.ROLE_STAFF));
                        role = db.roleDao().getRoleByName(SessionManager.ROLE_STAFF);
                    }

                    // Thao tác 3: Insert user mới
                    String hashedPw = PasswordUtils.hashPassword(password);
                    User newUser = new User(fullName, username, hashedPw);
                    long userId = db.userDao().insertUser(newUser);
                    
                    // Thao tác 4: Gán role STAFF cho user
                    if (userId > 0 && role != null) {
                        db.userRoleDao().insertUserRole(new UserRole((int) userId, role.getId()));
                    }

                    runOnUiThread(() -> {
                        showCustomMessage(binding.getRoot(), "Đăng ký nhân viên thành công!", false);
                        binding.getRoot().postDelayed(this::finish, 1200);
                    });
                }
            } catch (Exception e) {
                runOnUiThread(() -> {
                    setLoadingState(false);
                    showCustomMessage(binding.getRoot(), "Lỗi: " + e.getMessage(), true);
                });
            }
        });
    }

    private void setLoadingState(boolean isLoading) {
        isProcessing = isLoading;
        binding.btnRegister.setEnabled(!isLoading);
        binding.btnRegister.setText(isLoading ? "ĐANG XỬ LÝ..." : "ĐĂNG KÝ");
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
