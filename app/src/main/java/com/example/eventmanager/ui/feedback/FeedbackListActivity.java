package com.example.eventmanager.ui.feedback;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventmanager.R;
import com.example.eventmanager.adapter.FeedbackAdapter;
import com.example.eventmanager.model.Feedback;
import com.example.eventmanager.viewmodel.FeedbackViewModel;

import java.util.ArrayList;
import java.util.List;

public class FeedbackListActivity extends AppCompatActivity implements FeedbackAdapter.OnItemClickListener {
    private RecyclerView recyclerView;
    private FeedbackAdapter adapter;
    private FeedbackViewModel viewModel;
    private Button addButton;
    private TextView averageRatingTextView;
    private int eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_list);

        eventId = getIntent().getIntExtra("eventId", -1);
        if (eventId == -1) {
            finish();
            return;
        }

        recyclerView = findViewById(R.id.feedbackRecyclerView);
        addButton = findViewById(R.id.addFeedbackButton);
        averageRatingTextView = findViewById(R.id.averageRatingTextView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FeedbackAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(FeedbackViewModel.class);
        viewModel.getFeedbacksForEvent(eventId).observe(this, this::updateFeedbacks);
        viewModel.getAverageRatingForEvent(eventId).observe(this, average -> {
            if (average != null) {
                averageRatingTextView.setText("Average Rating: " + String.format("%.1f", average));
            }
        });

        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddFeedbackActivity.class);
            intent.putExtra("eventId", eventId);
            startActivity(intent);
        });
    }

    private void updateFeedbacks(List<Feedback> feedbacks) {
        adapter.updateFeedbacks(feedbacks);
    }

    @Override
    public void onItemClick(Feedback feedback) {
        // Handle item click
    }
}
