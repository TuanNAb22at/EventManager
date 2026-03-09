package com.example.eventmanager.repository;

import com.example.eventmanager.database.dao.EventDao;
import com.example.eventmanager.model.Event;
import java.util.List;

public class EventRepository {
    private final EventDao eventDao;

    public EventRepository(EventDao eventDao) {
        this.eventDao = eventDao;
    }

    public List<Event> getAllEvents() {
        return eventDao.getAllEvents();
    }

    public Event getEventById(int id) {
        return eventDao.getEventById(id);
    }

    public void insert(Event event) {
        eventDao.insertEvent(event);
    }

    public void update(Event event) {
        eventDao.updateEvent(event);
    }

    public void delete(Event event) {
        eventDao.deleteEvent(event);
    }
}
