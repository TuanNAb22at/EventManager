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

/**
 * Activity xử lý chức năng đăng nhập hệ thống.
 * Người dùng nhập username và password, hệ thống kiểm tra thông tin,
 * xác thực mật khẩu và lưu phiên đăng nhập nếu hợp lệ.
 */
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
    /**
     * Xử lý luồng đăng nhập:
     * - Kiểm tra dữ liệu đầu vào
     * - Tìm tài khoản theo username
     * - Xác thực mật khẩu
     * - Lấy vai trò người dùng
     * - Lưu phiên đăng nhập và chuyển sang màn hình chính
     */
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
                // Lấy thông tin người dùng từ database theo username đã nhập
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
    /**
     * Cập nhật trạng thái giao diện khi hệ thống đang xử lý đăng nhập.
     * Khi isLoading = true, nút đăng nhập bị vô hiệu hóa để tránh người dùng bấm nhiều lần.
     *
     * @param isLoading true nếu đang xử lý đăng nhập, false nếu xử lý xong
     */
    private void setLoadingState(boolean isLoading) {
        isProcessing = isLoading;
        binding.btnLogin.setEnabled(!isLoading);
        binding.btnLogin.setText(isLoading ? "ĐANG XỬ LÝ..." : "ĐĂNG NHẬP");
    }

    /**
     * Chuyển người dùng sang màn hình chính sau khi đăng nhập thành công.
     * FLAG_ACTIVITY_CLEAR_TASK giúp xóa các màn hình trước đó để người dùng
     * không quay lại màn hình đăng nhập bằng nút Back.
     */
    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    /**
     * Hiển thị thông báo kết quả đăng nhập bằng Snackbar.
     *
     * @param view view gốc dùng để hiển thị Snackbar
     * @param message nội dung thông báo
     * @param isError true nếu là thông báo lỗi, false nếu là thông báo thành công
     */
    private void showCustomMessage(View view, String message, boolean isError) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        if (isError) {
            snackbar.setBackgroundTint(getResources().getColor(android.R.color.holo_red_dark));
        } else {
            snackbar.setBackgroundTint(getResources().getColor(com.google.android.material.R.color.design_default_color_primary));
        }
        snackbar.show();
    }
    /**
     * Giải phóng ExecutorService khi Activity bị hủy
     * để tránh rò rỉ tài nguyên hoặc tiếp tục chạy task nền không cần thiết.
     */

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
