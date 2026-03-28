package com.example.eventmanager.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.eventmanager.R;
import com.example.eventmanager.model.User;
import com.example.eventmanager.utils.SessionManager;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<User> users;
    private final OnUserActionListener listener;

    public interface OnUserActionListener {
        void onEdit(User user);
        void onDelete(User user);
        void onResetPassword(User user);
    }

    public UserAdapter(List<User> users, OnUserActionListener listener) {
        this.users = users;
        this.listener = listener;
    }

    public void setUsers(List<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);
        holder.tvUserName.setText(user.getFullName());
        
        // Chuyển đổi tên vai trò sang tiếng Việt
        String displayRole = "Nhân viên";
        if (SessionManager.ROLE_ORGANIZER.equals(user.getRole())) {
            displayRole = "Người tổ chức";
            holder.tvUserRole.setBackgroundResource(R.drawable.bg_priority_high); // Màu đỏ/cam cho Admin
            holder.tvUserRole.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.red_tag));
        } else {
            displayRole = "Nhân viên";
            holder.tvUserRole.setBackgroundResource(R.drawable.bg_role_tag); // Màu xanh cho Staff
            holder.tvUserRole.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.primary_blue));
        }
        holder.tvUserRole.setText(displayRole);

        holder.btnEdit.setOnClickListener(v -> listener.onEdit(user));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(user));
        holder.btnResetPassword.setOnClickListener(v -> listener.onResetPassword(user));
        
        // Bảo vệ tài khoản gốc: Không cho phép Sửa hoặc Xóa admin hệ thống mặc định (nguyentuan)
        if ("nguyentuan".equals(user.getUsername())) {
            holder.btnDelete.setVisibility(View.GONE);
            holder.btnEdit.setVisibility(View.GONE);
            holder.btnResetPassword.setVisibility(View.GONE);
        } else {
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnResetPassword.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return users != null ? users.size() : 0;
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvUserRole;
        ImageView btnEdit, btnDelete, btnResetPassword;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvUserRole = itemView.findViewById(R.id.tvUserRole);
            btnEdit = itemView.findViewById(R.id.btnEditUser);
            btnDelete = itemView.findViewById(R.id.btnDeleteUser);
            btnResetPassword = itemView.findViewById(R.id.btnResetPassword);
        }
    }
}
