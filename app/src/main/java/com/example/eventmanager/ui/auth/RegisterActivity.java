package com.example.eventmanager.ui.auth;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.eventmanager.databinding.ActivityRegisterBinding;
import com.example.eventmanager.database.AppDatabase;
import com.example.eventmanager.model.User;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnRegister.setOnClickListener(v -> handleRegister());
        
        binding.tvLogin.setOnClickListener(v -> finish());
    }

    private void handleRegister() {
        String fullName = binding.etFullName.getText().toString().trim();
        String username = binding.etUsername.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        String confirmPassword = binding.etConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(username) || 
            TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Mật khẩu xác thực không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        executorService.execute(() -> {
            // Kiểm tra username tồn tại
            User existingUser = AppDatabase.getInstance(this).userDao().getUserByUsername(username);
            
            if (existingUser != null) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Tên đăng nhập này đã tồn tại!", Toast.LENGTH_SHORT).show();
                });
            } else {
                User newUser = new User(fullName, username, password, "user");
                try {
                    AppDatabase.getInstance(this).userDao().insertUser(newUser);
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                } catch (Exception e) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Lỗi đăng ký: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }
}
