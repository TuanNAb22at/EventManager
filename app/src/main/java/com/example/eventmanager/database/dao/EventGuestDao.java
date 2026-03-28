package com.example.eventmanager.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.example.eventmanager.model.EventGuest;
import com.example.eventmanager.model.Guest;
import java.util.List;

@Dao
public interface EventGuestDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(EventGuest eventGuest);

    @Delete
    void delete(EventGuest eventGuest);

    @Query("DELETE FROM event_guest WHERE eventId = :eventId")
    void deleteByEventId(int eventId);

    @Query("SELECT g.* FROM guest g INNER JOIN event_guest eg ON g.id = eg.guestId WHERE eg.eventId = :eventId")
    List<Guest> getGuestsByEventId(int eventId);

    @Query("SELECT COUNT(*) FROM event_guest WHERE eventId = :eventId")
    int getGuestCountByEventIdSync(int eventId);

    @Query("SELECT COUNT(*) FROM event_guest WHERE eventId = :eventId")
    LiveData<Integer> getGuestCountByEventId(int eventId);
}
