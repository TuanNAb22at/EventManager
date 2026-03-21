package com.example.eventmanager.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.eventmanager.model.Schedule;
import com.example.eventmanager.repository.ScheduleRepository;

import java.util.List;

public class ScheduleViewModel extends AndroidViewModel {
    private ScheduleRepository repository;

    public ScheduleViewModel(@NonNull Application application) {
        super(application);
        repository = new ScheduleRepository(application);
    }

    public LiveData<List<Schedule>> getSchedulesForEvent(int eventId) {
        return repository.getSchedulesByEventId(eventId);
    }

    public void insert(Schedule schedule) {
        repository.insert(schedule);
    }

    public void update(Schedule schedule) {
        repository.update(schedule);
    }

    public void delete(Schedule schedule) {
        repository.delete(schedule);
    }
}
