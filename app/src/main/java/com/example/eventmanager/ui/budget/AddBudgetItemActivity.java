package com.example.eventmanager.ui.budget;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.eventmanager.database.AppDatabase;
import com.example.eventmanager.databinding.ActivityAddBudgetItemBinding;
import com.example.eventmanager.model.Budget;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddBudgetItemActivity extends AppCompatActivity {
    private ActivityAddBudgetItemBinding binding;
    private int eventId;
    private final Calendar calendar = Calendar.getInstance();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddBudgetItemBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        eventId = getIntent().getIntExtra("EVENT_ID", -1);

        setupDropdown();
        setupDatePicker();

        binding.btnCancel.setOnClickListener(v -> finish());
        binding.btnSaveExpense.setOnClickListener(v -> saveExpense());
    }

    private void setupDropdown() {
        String[] categories = {"Thuê mặt bằng", "Ăn uống", "Trang trí", "Âm thanh ánh sáng", "Quà tặng", "Marketing", "Khác"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, categories);
        binding.actvCategory.setAdapter(adapter);
    }

    private void setupDatePicker() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        binding.etDate.setText(sdf.format(new Date()));

        binding.etDate.setOnClickListener(v -> {
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                binding.etDate.setText(sdf.format(calendar.getTime()));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    private void saveExpense() {
        String title = binding.etTitle.getText().toString().trim();
        String amountStr = binding.etAmount.getText().toString().trim();
        String category = binding.actvCategory.getText().toString();
        
        if (title.isEmpty()) {
            binding.etTitle.setError("Vui lòng nhập tiêu đề");
            return;
        }
        if (amountStr.isEmpty()) {
            binding.etAmount.setError("Vui lòng nhập số tiền");
            return;
        }

        double amount = Double.parseDouble(amountStr);
        Date date = calendar.getTime();

        executorService.execute(() -> {
            Budget budget = new Budget(eventId, title, amount, category, date, "");
            AppDatabase.getInstance(this).budgetDao().insertBudget(budget);
            runOnUiThread(() -> {
                Toast.makeText(this, "Đã lưu khoản chi mới", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
