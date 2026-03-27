package com.example.eventmanager.database.dao;

import androidx.room.*;
import com.example.eventmanager.model.Guest;
import java.util.List;

@Dao
public interface GuestDao {
    @Query("SELECT * FROM guest")
    List<Guest> getAllGuests();

    @Query("SELECT * FROM guest WHERE eventId = :eventId")
    List<Guest> getGuestsByEventId(int eventId);

    @Query("SELECT * FROM guest WHERE id = :id")
    Guest getGuestById(int id);

    @Query("SELECT COUNT(*) FROM guest WHERE eventId = :eventId")
    int getGuestCountByEventId(int eventId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertGuest(Guest guest);

    @Update
    void updateGuest(Guest guest);

    @Delete
    void deleteGuest(Guest guest);
}
