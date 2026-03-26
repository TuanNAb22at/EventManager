package com.example.eventmanager.ui.budget;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.eventmanager.R;
import com.example.eventmanager.database.AppDatabase;
import com.example.eventmanager.databinding.ActivityAddBudgetItemBinding;
import com.example.eventmanager.model.Budget;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddBudgetItemActivity extends AppCompatActivity {
    private ActivityAddBudgetItemBinding binding;
    private int eventId;
    private int budgetId = -1;
    private Budget existingBudget;
    private final Calendar calendar = Calendar.getInstance();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final DecimalFormat decimalFormat = new DecimalFormat("#,###");
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddBudgetItemBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        eventId = getIntent().getIntExtra("EVENT_ID", -1);
        budgetId = getIntent().getIntExtra("BUDGET_ID", -1);

        setupDropdown();
        setupDatePicker();
        setupAmountFormatting();

        if (budgetId != -1) {
            loadExistingBudget();
            binding.tvToolbarTitle.setText("Chỉnh sửa chi phí");
            binding.btnDeleteExpense.setVisibility(View.VISIBLE);
        } else {
            binding.tvToolbarTitle.setText("Thêm chi phí mới");
            binding.btnDeleteExpense.setVisibility(View.GONE);
        }

        binding.btnCancel.setOnClickListener(v -> finish());
        binding.btnSaveExpense.setOnClickListener(v -> saveExpense());
        binding.btnDeleteExpense.setOnClickListener(v -> deleteExpense());
    }

    private void setupDropdown() {
        List<CategoryItem> categoryItems = new ArrayList<>();
        categoryItems.add(new CategoryItem("Đồ ăn", R.drawable.ic_food, R.drawable.bg_icon_soft_red));
        categoryItems.add(new CategoryItem("Trang trí", R.drawable.ic_decor, R.drawable.bg_icon_soft_blue));
        categoryItems.add(new CategoryItem("Âm nhạc", R.drawable.ic_music, R.drawable.bg_icon_soft_green));
        categoryItems.add(new CategoryItem("Quà tặng", R.drawable.img_gift, R.drawable.bg_icon_soft_yellow));
        categoryItems.add(new CategoryItem("Địa điểm", R.drawable.ic_location, R.drawable.bg_icon_soft_blue));
        categoryItems.add(new CategoryItem("Khác", R.drawable.ic_category, R.drawable.bg_icon_soft_green));

        CategoryAdapter adapter = new CategoryAdapter(this, categoryItems);
        binding.actvCategory.setAdapter(adapter);
        
        if (budgetId == -1) {
            binding.actvCategory.setText("Khác", false);
        }
    }

    private void setupDatePicker() {
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

    private void setupAmountFormatting() {
        binding.etAmount.addTextChangedListener(new TextWatcher() {
            private String current = "";
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    binding.etAmount.removeTextChangedListener(this);
                    String cleanString = s.toString().replaceAll("[^\\d]", "");
                    if (!cleanString.isEmpty()) {
                        double parsed = Double.parseDouble(cleanString);
                        String formatted = decimalFormat.format(parsed);
                        current = formatted;
                        binding.etAmount.setText(formatted);
                        binding.etAmount.setSelection(formatted.length());
                    } else {
                        current = "";
                        binding.etAmount.setText("");
                    }
                    binding.etAmount.addTextChangedListener(this);
                }
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void loadExistingBudget() {
        executorService.execute(() -> {
            existingBudget = AppDatabase.getInstance(this).budgetDao().getBudgetByIdSync(budgetId);
            if (existingBudget != null) {
                runOnUiThread(() -> {
                    binding.etTitle.setText(existingBudget.getTitle());
                    binding.etAmount.setText(decimalFormat.format(existingBudget.getAmount()));
                    String category = existingBudget.getCategory();
                    if (category != null && !category.isEmpty()) {
                        category = category.substring(0, 1).toUpperCase() + category.substring(1).toLowerCase();
                    }
                    binding.actvCategory.setText(category, false);
                    if (existingBudget.getDate() != null) {
                        calendar.setTime(existingBudget.getDate());
                        binding.etDate.setText(sdf.format(existingBudget.getDate()));
                    }
                });
            }
        });
    }

    private void saveExpense() {
        String title = binding.etTitle.getText().toString().trim();
        String amountStr = binding.etAmount.getText().toString().trim().replaceAll("[^\\d]", "");
        String category = binding.actvCategory.getText().toString().toLowerCase().trim();
        
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
            AppDatabase db = AppDatabase.getInstance(this);
            if (budgetId != -1 && existingBudget != null) {
                existingBudget.setTitle(title);
                existingBudget.setAmount(amount);
                existingBudget.setCategory(category);
                existingBudget.setDate(date);
                db.budgetDao().updateBudget(existingBudget);
                runOnUiThread(() -> { finish(); });
            } else {
                Budget budget = new Budget(eventId, title, amount, category, date, "");
                db.budgetDao().insertBudget(budget);
                runOnUiThread(() -> { finish(); });
            }
        });
    }

    private void deleteExpense() {
        if (existingBudget != null) {
            executorService.execute(() -> {
                AppDatabase.getInstance(this).budgetDao().deleteBudget(existingBudget);
                runOnUiThread(() -> { finish(); });
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }

    private static class CategoryItem {
        String name;
        int iconRes;
        int backgroundRes;

        CategoryItem(String name, int iconRes, int backgroundRes) {
            this.name = name;
            this.iconRes = iconRes;
            this.backgroundRes = backgroundRes;
        }

        @Override public String toString() { return name; }
    }

    private static class CategoryAdapter extends ArrayAdapter<CategoryItem> {
        public CategoryAdapter(@NonNull Context context, @NonNull List<CategoryItem> items) {
            super(context, 0, items);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return createView(position, convertView, parent);
        }

        @Override
        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return createView(position, convertView, parent);
        }

        private View createView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_category_dropdown, parent, false);
            }

            CategoryItem item = getItem(position);
            ImageView iconView = convertView.findViewById(R.id.ivCategoryIcon);
            TextView textView = convertView.findViewById(R.id.tvCategoryName);
            FrameLayout container = convertView.findViewById(R.id.flIconContainer);

            if (item != null) {
                iconView.setImageResource(item.iconRes);
                textView.setText(item.name);
                container.setBackgroundResource(item.backgroundRes);
            }

            return convertView;
        }
    }
}
