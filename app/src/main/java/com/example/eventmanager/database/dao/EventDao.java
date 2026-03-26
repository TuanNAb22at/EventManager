package com.example.eventmanager.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.example.eventmanager.model.Event;
import java.util.List;

@Dao
public interface EventDao {
    @Query("SELECT * FROM event ORDER BY startAt ASC")
    LiveData<List<Event>> getAllEvents();

    @Query("SELECT * FROM event ORDER BY startAt ASC")
    List<Event> getAllEventsSync();

    @Query("SELECT DISTINCT eventType FROM event WHERE eventType IS NOT NULL")
    LiveData<List<String>> getAllEventTypes();

    @Query("SELECT * FROM event WHERE createdBy = :userId ORDER BY startAt ASC")
    LiveData<List<Event>> getEventsByUserId(int userId);

    @Query("SELECT * FROM event WHERE createdBy = :userId ORDER BY startAt ASC")
    List<Event> getEventsByUserIdSync(int userId);

    @Query("SELECT * FROM event WHERE id = :id")
    LiveData<Event> getEventById(int id);

    @Query("SELECT * FROM event WHERE id = :id")
    Event getEventByIdSync(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertEvent(Event event);

    @Update
    void updateEvent(Event event);

    @Delete
    void deleteEvent(Event event);
}
