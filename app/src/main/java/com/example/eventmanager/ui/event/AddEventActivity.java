package com.example.eventmanager.ui.event;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.eventmanager.databinding.ActivityAddEventBinding;

public class AddEventActivity extends AppCompatActivity {
    
    private ActivityAddEventBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Xử lý nút quay lại trên Toolbar
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        // Sau này bạn sẽ thêm logic xử lý chọn ngày, giờ và lưu sự kiện tại đây
        binding.btnCreateEvent.setOnClickListener(v -> {
            // Logic lưu sự kiện
            finish();
        });
    }
}
