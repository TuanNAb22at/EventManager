package com.example.eventmanager.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.eventmanager.database.AppDatabase;
import com.example.eventmanager.model.Feedback;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FeedbackRepository {
    private final AppDatabase database;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public FeedbackRepository(Application application) {
        database = AppDatabase.getInstance(application);
    }

    public void insert(Feedback feedback) {
        executorService.execute(() -> database.feedbackDao().insert(feedback));
    }

    public void update(Feedback feedback) {
        executorService.execute(() -> database.feedbackDao().update(feedback));
    }

    public void delete(Feedback feedback) {
        executorService.execute(() -> database.feedbackDao().delete(feedback));
    }

    public LiveData<List<Feedback>> getFeedbacksByEventId(int eventId) {
        MutableLiveData<List<Feedback>> liveData = new MutableLiveData<>();
        executorService.execute(() -> {
            List<Feedback> feedbacks = database.feedbackDao().getFeedbacksByEventId(eventId);
            liveData.postValue(feedbacks);
        });
        return liveData;
    }

    public LiveData<List<Feedback>> getFeedbacksByUserId(int userId) {
        MutableLiveData<List<Feedback>> liveData = new MutableLiveData<>();
        executorService.execute(() -> {
            List<Feedback> feedbacks = database.feedbackDao().getFeedbacksByUserId(userId);
            liveData.postValue(feedbacks);
        });
        return liveData;
    }

    public LiveData<Double> getAverageRatingForEvent(int eventId) {
        MutableLiveData<Double> liveData = new MutableLiveData<>();
        executorService.execute(() -> {
            double average = database.feedbackDao().getAverageRatingForEvent(eventId);
            liveData.postValue(average);
        });
        return liveData;
    }
}
