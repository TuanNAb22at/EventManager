package com.example.eventmanager.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.eventmanager.database.AppDatabase;
import com.example.eventmanager.model.Schedule;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScheduleRepository {
    private final AppDatabase database;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public ScheduleRepository(Application application) {
        database = AppDatabase.getInstance(application);
    }

    public void insert(Schedule schedule) {
        executorService.execute(() -> database.scheduleDao().insert(schedule));
    }

    public void update(Schedule schedule) {
        executorService.execute(() -> database.scheduleDao().update(schedule));
    }

    public void delete(Schedule schedule) {
        executorService.execute(() -> database.scheduleDao().delete(schedule));
    }

    public LiveData<List<Schedule>> getSchedulesByEventId(int eventId) {
        MutableLiveData<List<Schedule>> liveData = new MutableLiveData<>();
        executorService.execute(() -> {
            List<Schedule> schedules = database.scheduleDao().getSchedulesByEventId(eventId);
            liveData.postValue(schedules);
        });
        return liveData;
    }

    public LiveData<List<Schedule>> getAllSchedules() {
        MutableLiveData<List<Schedule>> liveData = new MutableLiveData<>();
        executorService.execute(() -> {
            List<Schedule> schedules = database.scheduleDao().getAllSchedules();
            liveData.postValue(schedules);
        });
        return liveData;
    }
}
