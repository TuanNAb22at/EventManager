package com.example.eventmanager.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventmanager.R;
import com.example.eventmanager.model.Feedback;

import java.util.List;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder> {
    private List<Feedback> feedbacks;
    private OnItemClickListener onItemClick;

    public interface OnItemClickListener {
        void onItemClick(Feedback feedback);
    }

    public FeedbackAdapter(List<Feedback> feedbacks, OnItemClickListener onItemClick) {
        this.feedbacks = feedbacks;
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public FeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feedback, parent, false);
        return new FeedbackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedbackViewHolder holder, int position) {
        Feedback feedback = feedbacks.get(position);
        holder.ratingBar.setRating(feedback.rating);
        holder.commentsTextView.setText(feedback.comments);
        holder.dateTextView.setText(feedback.dateSubmitted);
        holder.itemView.setOnClickListener(v -> onItemClick.onItemClick(feedback));
    }

    @Override
    public int getItemCount() {
        return feedbacks.size();
    }

    public void updateFeedbacks(List<Feedback> newFeedbacks) {
        this.feedbacks = newFeedbacks;
        notifyDataSetChanged();
    }

    static class FeedbackViewHolder extends RecyclerView.ViewHolder {
        RatingBar ratingBar;
        TextView commentsTextView;
        TextView dateTextView;

        public FeedbackViewHolder(@NonNull View itemView) {
            super(itemView);
            ratingBar = itemView.findViewById(R.id.feedbackRatingBar);
            commentsTextView = itemView.findViewById(R.id.feedbackCommentsTextView);
            dateTextView = itemView.findViewById(R.id.feedbackDateTextView);
        }
    }
}
