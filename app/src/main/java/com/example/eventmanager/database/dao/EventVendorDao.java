package com.example.eventmanager.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.eventmanager.model.EventVendor;
import com.example.eventmanager.model.Vendor;

import java.util.List;

@Dao
public interface EventVendorDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(EventVendor eventVendor);

    @Delete
    void delete(EventVendor eventVendor);

    @Query("DELETE FROM event_vendor WHERE eventId = :eventId AND vendorId = :vendorId")
    void deleteById(int eventId, int vendorId);

    @Query("DELETE FROM event_vendor WHERE eventId = :eventId")
    void deleteByEventId(int eventId);

    @Query("SELECT v.* FROM vendor v INNER JOIN event_vendor ev ON v.id = ev.vendorId WHERE ev.eventId = :eventId")
    LiveData<List<Vendor>> getVendorsForEvent(int eventId);

    @Query("SELECT * FROM event_vendor WHERE eventId = :eventId")
    List<EventVendor> getEventVendorsByEventId(int eventId);

    @Update
    void update(EventVendor eventVendor);
}
