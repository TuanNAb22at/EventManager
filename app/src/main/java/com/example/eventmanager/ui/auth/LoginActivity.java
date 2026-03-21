package com.example.eventmanager.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.example.eventmanager.databinding.ActivityLoginBinding;
import com.example.eventmanager.database.AppDatabase;
import com.example.eventmanager.model.User;
import com.example.eventmanager.ui.main.MainActivity;
import com.example.eventmanager.utils.PasswordUtils;
import com.example.eventmanager.utils.SessionManager;
import com.google.android.material.snackbar.Snackbar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private SessionManager sessionManager;
    private boolean isProcessing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        sessionManager = new SessionManager(this);
        // Nếu đã ghi nhớ đăng nhập thì vào thẳng MainActivity
        if (sessionManager.isLoggedIn()) {
            navigateToMain();
            return;
        }

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnLogin.setOnClickListener(v -> handleLogin());
        binding.tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }

    private void handleLogin() {
        if (isProcessing) return;

        String username = binding.etUsername.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            binding.tilUsername.setError("Vui lòng nhập tên đăng nhập");
            return;
        } else {
            binding.tilUsername.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            binding.tilPassword.setError("Vui lòng nhập mật khẩu");
            return;
        } else {
            binding.tilPassword.setError(null);
        }

        setLoadingState(true);

        executorService.execute(() -> {
            try {
                User user = AppDatabase.getInstance(this).userDao().getUserByUsername(username);
                
                runOnUiThread(() -> {
                    if (user != null && PasswordUtils.verifyPassword(password, user.getPassword())) {
                        
                        // Lấy role của user từ database
                        executorService.execute(() -> {
                            List<String> roles = AppDatabase.getInstance(this).userDao().getUserRolesByUsername(username);
                            String primaryRole = (roles != null && !roles.isEmpty()) ? roles.get(0) : "USER";
                            
                            runOnUiThread(() -> {
                                // Truyền đủ 4 tham số: userId, username, role, rememberMe
                                sessionManager.createLoginSession(
                                    user.getId(), 
                                    user.getUsername(), 
                                    primaryRole, 
                                    binding.swRememberMe.isChecked()
                                );
                                
                                showCustomMessage(binding.getRoot(), "Đăng nhập thành công!", false);
                                
                                binding.getRoot().postDelayed(this::navigateToMain, 500);
                            });
                        });
                    } else {
                        setLoadingState(false);
                        showCustomMessage(binding.getRoot(), "Tên đăng nhập hoặc mật khẩu không chính xác", true);
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    setLoadingState(false);
                    showCustomMessage(binding.getRoot(), "Lỗi hệ thống: " + e.getMessage(), true);
                });
            }
        });
    }

    private void setLoadingState(boolean isLoading) {
        isProcessing = isLoading;
        binding.btnLogin.setEnabled(!isLoading);
        binding.btnLogin.setText(isLoading ? "ĐANG XỬ LÝ..." : "ĐĂNG NHẬP");
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
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
