package com.example.eventmanager.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.eventmanager.model.Feedback;
import com.example.eventmanager.repository.FeedbackRepository;

import java.util.List;

public class FeedbackViewModel extends AndroidViewModel {
    private FeedbackRepository repository;

    public FeedbackViewModel(@NonNull Application application) {
        super(application);
        repository = new FeedbackRepository(application);
    }

    public LiveData<List<Feedback>> getFeedbacksForEvent(int eventId) {
        return repository.getFeedbacksByEventId(eventId);
    }

    public LiveData<Double> getAverageRatingForEvent(int eventId) {
        return repository.getAverageRatingForEvent(eventId);
    }

    public void insert(Feedback feedback) {
        repository.insert(feedback);
    }

    public void update(Feedback feedback) {
        repository.update(feedback);
    }

    public void delete(Feedback feedback) {
        repository.delete(feedback);
    }
}
