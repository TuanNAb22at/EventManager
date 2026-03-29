package com.example.eventmanager.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.eventmanager.R;
import com.example.eventmanager.database.AppDatabase;
import com.example.eventmanager.databinding.ActivityProfileBinding;
import com.example.eventmanager.model.User;
import com.example.eventmanager.utils.SessionManager;
import com.google.android.material.chip.Chip;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProfileActivity extends AppCompatActivity {
    private ActivityProfileBinding binding;
    private SessionManager sessionManager;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private static final int REQUEST_EDIT_PROFILE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        
        loadUserData();

        binding.btnBack.setOnClickListener(v -> finish());
        
        binding.btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditProfileActivity.class);
            startActivityForResult(intent, REQUEST_EDIT_PROFILE);
        });
    }

    private void loadUserData() {
        executorService.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            User user = db.userDao().getUserById(sessionManager.getUserId());
            if (user != null) {
                List<String> roles = db.userDao().getUserRolesByUsername(user.getUsername());
                String primaryRole = (roles != null && !roles.isEmpty()) ? roles.get(0) : "USER";
                user.setRole(primaryRole);

                runOnUiThread(() -> {
                    binding.tvProfileName.setText(user.getFullName());
                    binding.tvRoleTag.setText(getRoleDisplayName(user.getRole()));
                    
                    if (user.getAboutMe() != null && !user.getAboutMe().isEmpty()) {
                        binding.tvAboutMe.setText(user.getAboutMe());
                    } else {
                        binding.tvAboutMe.setText("Chưa có thông tin giới thiệu.");
                    }

                    binding.chipGroupInterests.removeAllViews();
                    if (user.getInterests() != null && !user.getInterests().isEmpty()) {
                        String[] interests = user.getInterests().split(",");
                        for (String interest : interests) {
                            addInterestChip(interest.trim());
                        }
                    }

                    if (user.getAvatarUri() != null && !user.getAvatarUri().isEmpty()) {
                        Glide.with(this)
                            .load(user.getAvatarUri())
                            .placeholder(R.drawable.ic_user)
                            .into(binding.ivAvatar);
                    } else {
                        binding.ivAvatar.setImageResource(R.drawable.ic_user);
                    }
                });
            }
        });
    }

    private void addInterestChip(String interest) {
        Chip chip = new Chip(this);
        chip.setText(interest);
        chip.setChipBackgroundColorResource(R.color.primary_blue);
        chip.setTextColor(getResources().getColor(R.color.white));
        binding.chipGroupInterests.addView(chip);
    }

    private String getRoleDisplayName(String role) {
        if (role == null) return "Người dùng";
        switch (role) {
            case SessionManager.ROLE_ORGANIZER: return "Người tổ chức";
            case SessionManager.ROLE_STAFF: return "Nhân viên";
            default: return "Người dùng";
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_EDIT_PROFILE && resultCode == RESULT_OK) {
            loadUserData();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
