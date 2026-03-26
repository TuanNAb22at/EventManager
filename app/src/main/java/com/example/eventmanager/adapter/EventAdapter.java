package com.example.eventmanager.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.eventmanager.R;
import com.example.eventmanager.model.Event;
import java.util.ArrayList;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private List<Event> events = new ArrayList<>();
    private final OnItemClickListener onItemClick;

    public interface OnItemClickListener {
        void onItemClick(Event event);
    }

    public EventAdapter(List<Event> events, OnItemClickListener onItemClick) {
        this.events = events != null ? events : new ArrayList<>();
        this.onItemClick = onItemClick;
    }

    public void setEvents(List<Event> events) {
        this.events = events != null ? events : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Sử dụng item_event_card để đồng bộ giao diện đẹp hơn
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_card, parent, false);
        
        // Điều chỉnh lại width thành MATCH_PARENT để hiển thị tốt trong danh sách dọc
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams != null) {
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            view.setLayoutParams(layoutParams);
        }
        
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = events.get(position);
        
        if (holder.nameTextView != null) {
            holder.nameTextView.setText(event.getName());
        }
        
        // Hiển thị trạng thái/địa điểm
        if (holder.tvLocation != null) {
            holder.tvLocation.setText(event.getStatus());
        }

        // Parse ngày tháng (định dạng dd/MM/yyyy HH:mm)
        if (event.getStartAt() != null && !event.getStartAt().isEmpty()) {
            try {
                String datePart = event.getStartAt().split(" ")[0];
                String[] dateSplit = datePart.split("/");
                if (dateSplit.length >= 2) {
                    if (holder.tvDateDay != null) holder.tvDateDay.setText(dateSplit[0]);
                    if (holder.tvDateMonth != null) holder.tvDateMonth.setText("THÁNG " + dateSplit[1]);
                }
            } catch (Exception e) {
                if (holder.tvDateDay != null) holder.tvDateDay.setText("--");
                if (holder.tvDateMonth != null) holder.tvDateMonth.setText("THÁNG --");
            }
        }

        // Load ảnh bìa bằng Glide
        if (event.getBannerUri() != null && !event.getBannerUri().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                .load(event.getBannerUri())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .centerCrop()
                .into(holder.ivEventImage);
        } else {
            holder.ivEventImage.setImageResource(R.drawable.ic_launcher_background);
        }

        holder.itemView.setOnClickListener(v -> {
            if (onItemClick != null) {
                onItemClick.onItemClick(event);
            }
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView tvDateDay;
        TextView tvDateMonth;
        TextView tvLocation;
        ImageView ivEventImage;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.tvEventTitle);
            tvDateDay = itemView.findViewById(R.id.tvDateDay);
            tvDateMonth = itemView.findViewById(R.id.tvDateMonth);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            ivEventImage = itemView.findViewById(R.id.ivEventImage);
        }
    }
}
