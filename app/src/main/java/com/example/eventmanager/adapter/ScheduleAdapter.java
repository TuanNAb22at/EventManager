package com.example.eventmanager.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventmanager.R;
import com.example.eventmanager.model.Schedule;

import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {
    private List<Schedule> schedules;
    private OnItemClickListener onItemClick;

    public interface OnItemClickListener {
        void onItemClick(Schedule schedule);
    }

    public ScheduleAdapter(List<Schedule> schedules, OnItemClickListener onItemClick) {
        this.schedules = schedules;
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_schedule, parent, false);
        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        Schedule schedule = schedules.get(position);
        holder.titleTextView.setText(schedule.getTitle());
        holder.timeTextView.setText(schedule.getTime());
        holder.descriptionTextView.setText(schedule.getDescription());
        holder.statusTextView.setText(schedule.getStatus());
        holder.itemView.setOnClickListener(v -> onItemClick.onItemClick(schedule));
    }

    @Override
    public int getItemCount() {
        return schedules.size();
    }

    public void updateSchedules(List<Schedule> newSchedules) {
        this.schedules = newSchedules;
        notifyDataSetChanged();
    }

    static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView timeTextView;
        TextView descriptionTextView;
        TextView statusTextView;

        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.scheduleTitleTextView);
            timeTextView = itemView.findViewById(R.id.scheduleTimeTextView);
            descriptionTextView = itemView.findViewById(R.id.scheduleDescriptionTextView);
            statusTextView = itemView.findViewById(R.id.scheduleStatusTextView);
        }
    }
}
