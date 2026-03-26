package com.example.eventmanager.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.eventmanager.database.AppDatabase;
import com.example.eventmanager.model.Event;
import java.util.List;

public class EventViewModel extends AndroidViewModel {
    private final LiveData<List<Event>> allEvents;
    private final AppDatabase database;

    public EventViewModel(@NonNull Application application) {
        super(application);
        database = AppDatabase.getInstance(application);
        allEvents = database.eventDao().getAllEvents();
    }

    public LiveData<List<Event>> getAllEvents() {
        return allEvents;
    }

    public LiveData<List<Event>> getMyEvents(int userId) {
        return database.eventDao().getEventsByUserId(userId);
    }

    public LiveData<List<String>> getAllEventTypes() {
        return database.eventDao().getAllEventTypes();
    }
}
