package com.example.eventmanager.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.eventmanager.database.dao.EventDao;
import com.example.eventmanager.model.Event;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventRepository {
    private final EventDao eventDao;
    private final ExecutorService executorService;

    public EventRepository(EventDao eventDao) {
        this.eventDao = eventDao;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Event>> getAllEvents() {
        MutableLiveData<List<Event>> data = new MutableLiveData<>();
        executorService.execute(() -> data.postValue(eventDao.getAllEvents()));
        return data;
    }

    public LiveData<Event> getEventById(int id) {
        MutableLiveData<Event> data = new MutableLiveData<>();
        executorService.execute(() -> data.postValue(eventDao.getEventById(id)));
        return data;
    }

    public void insert(Event event) {
        executorService.execute(() -> eventDao.insertEvent(event));
    }

    public void update(Event event) {
        executorService.execute(() -> eventDao.updateEvent(event));
    }

    public void delete(Event event) {
        executorService.execute(() -> eventDao.deleteEvent(event));
    }
}
