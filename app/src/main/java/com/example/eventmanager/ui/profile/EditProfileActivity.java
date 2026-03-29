package com.example.eventmanager.ui.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.eventmanager.R;
import com.example.eventmanager.database.AppDatabase;
import com.example.eventmanager.databinding.ActivityEditProfileBinding;
import com.example.eventmanager.model.User;
import com.example.eventmanager.utils.SessionManager;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EditProfileActivity extends AppCompatActivity {

    private ActivityEditProfileBinding binding;
    private SessionManager sessionManager;
    private User currentUser;
    private String selectedAvatarUri = null;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        
        setupToolbar();
        loadUserData();
        setupImagePicker();

        binding.btnSaveProfile.setOnClickListener(v -> saveProfile());
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void loadUserData() {
        executorService.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            currentUser = db.userDao().getUserById(sessionManager.getUserId());
            if (currentUser != null) {
                runOnUiThread(() -> {
                    binding.etFullName.setText(currentUser.getFullName());
                    binding.etAboutMe.setText(currentUser.getAboutMe());
                    binding.etInterests.setText(currentUser.getInterests());
                    
                    if (currentUser.getAvatarUri() != null && !currentUser.getAvatarUri().isEmpty()) {
                        selectedAvatarUri = currentUser.getAvatarUri();
                        Glide.with(this)
                            .load(selectedAvatarUri)
                            .placeholder(R.drawable.ic_user)
                            .into(binding.ivEditAvatar);
                    }
                });
            }
        });
    }

    private void setupImagePicker() {
        binding.btnChangeAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                try {
                    getContentResolver().takePersistableUriPermission(imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    selectedAvatarUri = imageUri.toString();
                    Glide.with(this).load(imageUri).into(binding.ivEditAvatar);
                } catch (Exception e) {
                    Toast.makeText(this, "Lỗi chọn ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void saveProfile() {
        String fullName = binding.etFullName.getText().toString().trim();
        String aboutMe = binding.etAboutMe.getText().toString().trim();
        String interests = binding.etInterests.getText().toString().trim();

        if (fullName.isEmpty()) {
            binding.etFullName.setError("Vui lòng nhập họ tên");
            return;
        }

        executorService.execute(() -> {
            if (currentUser != null) {
                currentUser.setFullName(fullName);
                currentUser.setAboutMe(aboutMe);
                currentUser.setInterests(interests);
                currentUser.setAvatarUri(selectedAvatarUri);

                AppDatabase.getInstance(this).userDao().updateUser(currentUser);
                
                runOnUiThread(() -> {
                    Toast.makeText(this, "Cập nhật hồ sơ thành công", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
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
