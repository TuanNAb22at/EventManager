package com.example.eventmanager.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.eventmanager.R;
import com.example.eventmanager.model.Location;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VenueAdapter extends RecyclerView.Adapter<VenueAdapter.ViewHolder> {

    private List<Location> locations = new ArrayList<>();
    private final OnVenueClickListener listener;

    public interface OnVenueClickListener {
        void onVenueClick(Location location);
    }

    public VenueAdapter(OnVenueClickListener listener) {
        this.listener = listener;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_venue_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Location location = locations.get(position);
        holder.tvVenueName.setText(location.getName());
        holder.tvAddress.setText(location.getAddress());
        holder.tvRating.setText(String.format(Locale.getDefault(), "%.1f", location.getRating()));
        holder.tvCapacity.setText("Lên đến " + location.getCapacity());
        holder.tvArea.setText(location.getArea() + " m²");
        
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        holder.tvPrice.setText(formatter.format(location.getPrice()) + " / ngày");

        holder.tvPremiumTag.setVisibility(location.isPremium() ? View.VISIBLE : View.GONE);
        
        // Load image using Glide
        if (location.getImageUrl() != null && !location.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(location.getImageUrl())
                    .placeholder(R.drawable.ic_event_placeholder)
                    .error(R.drawable.ic_event_placeholder)
                    .centerCrop()
                    .into(holder.ivVenueImage);
        } else {
            holder.ivVenueImage.setImageResource(R.drawable.ic_event_placeholder);
        }
        
        holder.btnDetails.setOnClickListener(v -> listener.onVenueClick(location));
        holder.itemView.setOnClickListener(v -> listener.onVenueClick(location));
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvVenueName, tvAddress, tvRating, tvCapacity, tvArea, tvPrice, tvPremiumTag;
        ImageView ivVenueImage;
        Button btnDetails;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvVenueName = itemView.findViewById(R.id.tvVenueName);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvCapacity = itemView.findViewById(R.id.tvCapacity);
            tvArea = itemView.findViewById(R.id.tvArea);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvPremiumTag = itemView.findViewById(R.id.tvPremiumTag);
            ivVenueImage = itemView.findViewById(R.id.ivVenueImage);
            btnDetails = itemView.findViewById(R.id.btnDetails);
        }
    }
}
