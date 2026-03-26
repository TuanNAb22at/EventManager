package com.example.eventmanager.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.eventmanager.R;
import com.example.eventmanager.databinding.ItemEventCardBinding;
import com.example.eventmanager.model.Event;
import java.util.ArrayList;
import java.util.List;

public class UpcomingEventAdapter extends RecyclerView.Adapter<UpcomingEventAdapter.ViewHolder> {
    private List<Event> events = new ArrayList<>();
    private final OnEventClickListener listener;

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
        holder.binding.tvLocation.setText("Đang cập nhật địa điểm");
        
        // Parse date for display
        if (event.getStartAt() != null && event.getStartAt().contains(" ")) {
            String[] parts = event.getStartAt().split(" ");
            String dateStr = parts[0]; // dd/MM/yyyy
            String[] dateParts = dateStr.split("/");
            if (dateParts.length == 3) {
                holder.binding.tvDateDay.setText(dateParts[0]);
                holder.binding.tvDateMonth.setText("THÁNG " + dateParts[1]);
            }
        }

        // Load banner image
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
