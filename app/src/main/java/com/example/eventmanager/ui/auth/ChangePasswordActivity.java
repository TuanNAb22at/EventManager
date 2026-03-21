package com.example.eventmanager.ui.auth;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.eventmanager.database.AppDatabase;
import com.example.eventmanager.databinding.ActivityChangePasswordBinding;
import com.example.eventmanager.model.User;
import com.example.eventmanager.utils.PasswordUtils;
import com.example.eventmanager.utils.SessionManager;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChangePasswordActivity extends AppCompatActivity {
    private ActivityChangePasswordBinding binding;
    private SessionManager sessionManager;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);

        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnChangePassword.setOnClickListener(v -> {
            String currentPass = binding.etCurrentPassword.getText().toString().trim();
            String newPass = binding.etNewPassword.getText().toString().trim();
            String confirmPass = binding.etConfirmPassword.getText().toString().trim();

            if (currentPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPass.equals(confirmPass)) {
                Toast.makeText(this, "Mật khẩu mới không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            updatePassword(currentPass, newPass);
        });
    }

    private void updatePassword(String currentPass, String newPass) {
        executorService.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            User user = db.userDao().getUserById(sessionManager.getUserId());

            // Sử dụng PasswordUtils để kiểm tra mật khẩu đã mã hóa
            if (user != null && PasswordUtils.verifyPassword(currentPass, user.getPassword())) {
                // Mã hóa mật khẩu mới trước khi lưu
                String hashedNewPass = PasswordUtils.hashPassword(newPass);
                user.setPassword(hashedNewPass);
                db.userDao().updateUser(user);
                
                runOnUiThread(() -> {
                    Toast.makeText(this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                    finish();
                });
            } else {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Mật khẩu hiện tại không chính xác", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
