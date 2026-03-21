package com.example.eventmanager.database.dao;

import androidx.room.*;
import com.example.eventmanager.model.Event;
import java.util.List;

@Dao
public interface EventDao {
    @Query("SELECT * FROM events")
    List<Event> getAllEvents();

    @Query("SELECT * FROM events WHERE createdBy = :userId")
    List<Event> getEventsByUserId(int userId);

    @Query("SELECT * FROM events WHERE id = :id")
    Event getEventById(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertEvent(Event event);

    @Update
    void updateEvent(Event event);

    @Delete
    void deleteEvent(Event event);
}
