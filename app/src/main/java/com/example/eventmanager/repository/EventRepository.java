package com.example.eventmanager.repository;

import androidx.lifecycle.LiveData;
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
        return eventDao.getAllEvents();
    }

    public LiveData<Event> getEventById(int id) {
        return eventDao.getEventById(id);
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
