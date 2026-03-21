package com.example.eventmanager.ui.main;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.eventmanager.R;
import com.example.eventmanager.databinding.ActivityMainBinding;
import com.example.eventmanager.ui.auth.ChangePasswordActivity;
import com.example.eventmanager.ui.auth.LoginActivity;
import com.example.eventmanager.ui.event.AddEventActivity;
import com.example.eventmanager.ui.profile.ProfileActivity;
import com.example.eventmanager.ui.vendor.VendorListActivity;
import com.example.eventmanager.utils.SessionManager;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        
        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initUI();
        setupWindowInsets();
        setupDrawer();
        setupBottomNavigation();
        setupFab();
        applyRolePermissions();
    }

    private void initUI() {
        Window window = getWindow();
        window.setStatusBarColor(Color.TRANSPARENT);
    }

    private void setupFab() {
        binding.fabAdd.setOnClickListener(v -> {
            if (sessionManager.canManageAll()) {
                startActivity(new Intent(MainActivity.this, AddEventActivity.class));
            } else {
                Snackbar.make(binding.getRoot(), "Bạn không có quyền thực hiện chức năng này!", Snackbar.LENGTH_LONG)
                        .setBackgroundTint(ContextCompat.getColor(this, android.R.color.holo_red_dark))
                        .show();
            }
        });
    }

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            
            binding.headerLayout.setPadding(
                binding.headerLayout.getPaddingLeft(),
                systemBars.top, 
                binding.headerLayout.getPaddingRight(),
                binding.headerLayout.getPaddingBottom()
            );

            binding.bottomNavContainer.setPadding(0, 0, 0, systemBars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });
    }

    private void setupDrawer() {
        binding.btnMenu.setOnClickListener(v -> binding.drawerLayout.openDrawer(GravityCompat.START));

        View headerView = binding.navigationView.getHeaderView(0);
        TextView tvHeaderName = headerView.findViewById(R.id.tvHeaderName);
        TextView tvHeaderRole = headerView.findViewById(R.id.tvHeaderRole);
        
        if (tvHeaderName != null) tvHeaderName.setText(sessionManager.getUsername());
        if (tvHeaderRole != null) tvHeaderRole.setText(getRoleDisplayName(sessionManager.getUserRole()));

        binding.navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
            } else if (id == R.id.nav_change_password) {
                startActivity(new Intent(this, ChangePasswordActivity.class));
            } else if (id == R.id.nav_logout) {
                sessionManager.logoutUser();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private String getRoleDisplayName(String role) {
        switch (role) {
            case SessionManager.ROLE_ORGANIZER: return "Người tổ chức";
            case SessionManager.ROLE_VENDOR: return "Nhà cung cấp";
            case SessionManager.ROLE_STAFF: return "Nhân viên";
            default: return "Người dùng";
        }
    }

    private void applyRolePermissions() {
        if (sessionManager.isVendor()) {
            binding.fabAdd.setVisibility(View.GONE);
        }
    }

    private void setupBottomNavigation() {
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_supplier) {
                startActivity(new Intent(MainActivity.this, VendorListActivity.class));
                return true;
            } else if (itemId == R.id.nav_budget) {
                return true;
            }
            return true;
        });
    }
}
