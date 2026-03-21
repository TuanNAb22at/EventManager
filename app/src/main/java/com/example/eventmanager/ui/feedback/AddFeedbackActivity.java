package com.example.eventmanager.ui.feedback;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eventmanager.R;
import com.example.eventmanager.model.Feedback;
import com.example.eventmanager.repository.FeedbackRepository;
import com.example.eventmanager.utils.DateUtils;
import com.example.eventmanager.utils.SessionManager;

public class AddFeedbackActivity extends AppCompatActivity {
    private RatingBar ratingBar;
    private EditText commentsEditText;
    private Button submitButton;
    private FeedbackRepository feedbackRepository;
    private int eventId;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_feedback);

        eventId = getIntent().getIntExtra("eventId", -1);
        if (eventId == -1) {
            Toast.makeText(this, "Invalid event", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        SessionManager sessionManager = new SessionManager(this);
        userId = sessionManager.getUserId();

        feedbackRepository = new FeedbackRepository(getApplication());

        ratingBar = findViewById(R.id.ratingBar);
        commentsEditText = findViewById(R.id.commentsEditText);
        submitButton = findViewById(R.id.submitButton);

        submitButton.setOnClickListener(v -> submitFeedback());
    }

    private void submitFeedback() {
        int rating = (int) ratingBar.getRating();
        String comments = commentsEditText.getText().toString().trim();
        String dateSubmitted = DateUtils.getCurrentDate();

        if (rating == 0) {
            Toast.makeText(this, "Please provide a rating", Toast.LENGTH_SHORT).show();
            return;
        }

        Feedback feedback = new Feedback(eventId, userId, rating, comments, dateSubmitted);
        feedbackRepository.insert(feedback);
        Toast.makeText(this, "Feedback submitted", Toast.LENGTH_SHORT).show();
        finish();
    }
}
