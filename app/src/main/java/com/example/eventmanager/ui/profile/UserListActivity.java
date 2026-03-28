package com.example.eventmanager.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.eventmanager.R;
import com.example.eventmanager.adapter.UserAdapter;
import com.example.eventmanager.database.AppDatabase;
import com.example.eventmanager.databinding.ActivityUserListBinding;
import com.example.eventmanager.model.Role;
import com.example.eventmanager.model.User;
import com.example.eventmanager.model.UserRole;
import com.example.eventmanager.utils.PasswordUtils;
import com.example.eventmanager.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserListActivity extends AppCompatActivity implements UserAdapter.OnUserActionListener {

    private ActivityUserListBinding binding;
    private UserAdapter adapter;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        if (!sessionManager.canManageAll()) {
            Toast.makeText(this, "Bạn không có quyền truy cập!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupRecyclerView();
        loadUsers();

        binding.toolbar.setNavigationOnClickListener(v -> finish());
        binding.fabAddUser.setOnClickListener(v -> showAddUserDialog());
    }

    private void setupRecyclerView() {
        adapter = new UserAdapter(new ArrayList<>(), this);
        binding.rvUsers.setLayoutManager(new LinearLayoutManager(this));
        binding.rvUsers.setAdapter(adapter);
    }

    private void loadUsers() {
        executorService.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            List<User> users = db.userDao().getAllUsers();
            for (User user : users) {
                List<String> roles = db.userDao().getUserRolesByUsername(user.getUsername());
                user.setRole(roles != null && !roles.isEmpty() ? roles.get(0) : "USER");
            }
            runOnUiThread(() -> adapter.setUsers(users));
        });
    }

    private void showAddUserDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thêm tài khoản mới");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_user, null);
        EditText etFullName = view.findViewById(R.id.etFullName);
        EditText etUsername = view.findViewById(R.id.etUsername);
        Spinner spRole = view.findViewById(R.id.spRole);

        String[] roles = {"ORGANIZER", "STAFF"};
        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, roles);
        spRole.setAdapter(roleAdapter);

        builder.setView(view);
        builder.setPositiveButton("Thêm", (dialog, which) -> {
            String fullName = etFullName.getText().toString().trim();
            String username = etUsername.getText().toString().trim();
            String roleName = spRole.getSelectedItem().toString();

            if (fullName.isEmpty() || username.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            saveUser(fullName, username, roleName);
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void saveUser(String fullName, String username, String roleName) {
        executorService.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            if (db.userDao().getUserByUsername(username) != null) {
                runOnUiThread(() -> Toast.makeText(this, "Tên đăng nhập đã tồn tại", Toast.LENGTH_SHORT).show());
                return;
            }

            User user = new User(fullName, username, PasswordUtils.hashPassword("123456"));
            long userId = db.userDao().insertUser(user);
            
            Role role = db.roleDao().getRoleByName(roleName);
            if (role != null && userId > 0) {
                db.userRoleDao().insertUserRole(new UserRole((int) userId, role.getId()));
            }

            loadUsers();
            runOnUiThread(() -> Toast.makeText(this, "Đã thêm tài khoản thành công (Pass: 123456)", Toast.LENGTH_LONG).show());
        });
    }

    @Override
    public void onEdit(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sửa quyền tài khoản");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_user_role, null);
        Spinner spRole = view.findViewById(R.id.spRole);

        String[] roles = {"ORGANIZER", "STAFF"};
        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, roles);
        spRole.setAdapter(roleAdapter);
        
        // Set current selection
        for(int i=0; i<roles.length; i++) {
            if(roles[i].equals(user.getRole())) {
                spRole.setSelection(i);
                break;
            }
        }

        builder.setView(view);
        builder.setPositiveButton("Cập nhật", (dialog, which) -> {
            String newRoleName = spRole.getSelectedItem().toString();
            updateUserRole(user, newRoleName);
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void updateUserRole(User user, String newRoleName) {
        executorService.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            Role role = db.roleDao().getRoleByName(newRoleName);
            if (role != null) {
                db.userRoleDao().insertUserRole(new UserRole(user.getId(), role.getId()));
                loadUsers();
                runOnUiThread(() -> Toast.makeText(this, "Đã cập nhật quyền", Toast.LENGTH_SHORT).show());
            }
        });
    }

    @Override
    public void onResetPassword(User user) {
        new AlertDialog.Builder(this)
                .setTitle("Khôi phục mật khẩu")
                .setMessage("Bạn có chắc chắn muốn đặt lại mật khẩu cho tài khoản '" + user.getUsername() + "' về mặc định (123456) không?")
                .setPositiveButton("Đặt lại", (dialog, which) -> {
                    executorService.execute(() -> {
                        AppDatabase db = AppDatabase.getInstance(this);
                        user.setPassword(PasswordUtils.hashPassword("123456"));
                        db.userDao().updateUser(user);
                        runOnUiThread(() -> Toast.makeText(this, "Đã đặt lại mật khẩu về 123456", Toast.LENGTH_SHORT).show());
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onDelete(User user) {
        if (user.getUsername().equals("nguyentuan")) {
            Toast.makeText(this, "Không thể xóa tài khoản Admin hệ thống", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Xóa tài khoản")
                .setMessage("Bạn có chắc chắn muốn xóa tài khoản '" + user.getUsername() + "'?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    executorService.execute(() -> {
                        AppDatabase db = AppDatabase.getInstance(this);
                        db.userDao().deleteUser(user);
                        loadUsers();
                        runOnUiThread(() -> Toast.makeText(this, "Đã xóa tài khoản", Toast.LENGTH_SHORT).show());
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
