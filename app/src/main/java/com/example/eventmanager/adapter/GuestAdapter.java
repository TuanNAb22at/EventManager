package com.example.eventmanager.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.eventmanager.R;
import com.example.eventmanager.model.Guest;
import java.util.List;

public class GuestAdapter extends RecyclerView.Adapter<GuestAdapter.GuestViewHolder> {
    private List<Guest> guests;
    private OnItemClickListener onItemClick;

    public interface OnItemClickListener {
        void onItemClick(Guest guest);
    }

    public GuestAdapter(List<Guest> guests, OnItemClickListener onItemClick) {
        this.guests = guests;
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public GuestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_guest, parent, false);
        return new GuestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GuestViewHolder holder, int position) {
        Guest guest = guests.get(position);
        holder.nameTextView.setText(guest.name);
        holder.emailTextView.setText(guest.email);
        holder.statusTextView.setText(guest.status);
        holder.itemView.setOnClickListener(v -> onItemClick.onItemClick(guest));
    }

    @Override
    public int getItemCount() {
        return guests.size();
    }

    public static class GuestViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView emailTextView;
        TextView statusTextView;

        public GuestViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.guestNameTextView);
            emailTextView = itemView.findViewById(R.id.guestEmailTextView);
            statusTextView = itemView.findViewById(R.id.guestStatusTextView);
        }
    }
}
