package com.example.eventmanager.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.eventmanager.R;
import com.example.eventmanager.model.Event;
import java.util.List;
import java.util.Map;

public class TaskOverviewAdapter extends RecyclerView.Adapter<TaskOverviewAdapter.ViewHolder> {
    private List<Event> events;
    private Map<Integer, Integer> taskCounts;
    private final OnEventClickListener listener;

    public interface OnEventClickListener {
        void onEventClick(Event event);
    }

    public TaskOverviewAdapter(List<Event> events, Map<Integer, Integer> taskCounts, OnEventClickListener listener) {
        this.events = events;
        this.taskCounts = taskCounts;
        this.listener = listener;
    }

    public void setData(List<Event> events, Map<Integer, Integer> taskCounts) {
        this.events = events;
        this.taskCounts = taskCounts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_task_overview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = events.get(position);
        holder.tvEventName.setText(event.getName());
        holder.tvEventDate.setText(event.getStartAt());
        
        Integer count = taskCounts.get(event.getId());
        holder.tvTaskCount.setText((count != null ? count : 0) + " việc");

        holder.itemView.setOnClickListener(v -> listener.onEventClick(event));
    }

    @Override
    public int getItemCount() {
        return events != null ? events.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvEventName, tvEventDate, tvTaskCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEventName = itemView.findViewById(R.id.tvEventName);
            tvEventDate = itemView.findViewById(R.id.tvEventDate);
            tvTaskCount = itemView.findViewById(R.id.tvTaskCount);
        }
    }
}
