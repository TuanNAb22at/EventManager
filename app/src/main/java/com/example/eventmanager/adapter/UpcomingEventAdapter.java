package com.example.eventmanager.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.eventmanager.R;
import com.example.eventmanager.database.AppDatabase;
import com.example.eventmanager.databinding.ItemEventCardBinding;
import com.example.eventmanager.model.Event;
import com.example.eventmanager.model.Location;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UpcomingEventAdapter extends RecyclerView.Adapter<UpcomingEventAdapter.ViewHolder> {
    private List<Event> events = new ArrayList<>();
    private final OnEventClickListener listener;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public interface OnEventClickListener {
        void onEventClick(Event event);
    }

    public UpcomingEventAdapter(OnEventClickListener listener) {
        this.listener = listener;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemEventCardBinding binding = ItemEventCardBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = events.get(position);
        holder.binding.tvEventTitle.setText(event.getName());

        // Hiển thị tên địa điểm thực tế
        if (event.getLocationId() != null) {
            executorService.execute(() -> {
                Location location = AppDatabase.getInstance(holder.itemView.getContext())
                        .locationDao().getLocationByIdSync(event.getLocationId());
                if (location != null) {
                    holder.itemView.post(() -> holder.binding.tvLocation.setText(location.getName()));
                } else {
                    holder.itemView.post(() -> holder.binding.tvLocation.setText("Địa điểm chưa xác định"));
                }
            });
        } else {
            holder.binding.tvLocation.setText("Chưa chọn địa điểm");
        }
        
        // Parse ngày tháng hiển thị
        if (event.getStartAt() != null && event.getStartAt().contains(" ")) {
            try {
                String[] parts = event.getStartAt().split(" ");
                String dateStr = parts[0];
                String[] dateParts = dateStr.split("/");
                if (dateParts.length == 3) {
                    holder.binding.tvDateDay.setText(dateParts[0]);
                    holder.binding.tvDateMonth.setText("THÁNG " + dateParts[1]);
                }
            } catch (Exception e) {
                holder.binding.tvDateDay.setText("--");
                holder.binding.tvDateMonth.setText("THÁNG --");
            }
        }

        // Load ảnh bìa
        if (event.getBannerUri() != null && !event.getBannerUri().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                .load(event.getBannerUri())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .centerCrop()
                .into(holder.binding.ivEventImage);
        } else {
            holder.binding.ivEventImage.setImageResource(R.drawable.ic_launcher_background);
        }

        holder.itemView.setOnClickListener(v -> listener.onEventClick(event));
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ItemEventCardBinding binding;
        ViewHolder(ItemEventCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
