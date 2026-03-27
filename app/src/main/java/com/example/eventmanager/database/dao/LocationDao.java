package com.example.eventmanager.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.example.eventmanager.model.Location;
import java.util.List;

@Dao
public interface LocationDao {
    @Query("SELECT * FROM location ORDER BY createdAt DESC")
    LiveData<List<Location>> getAllLocations();

    @Query("SELECT * FROM location ORDER BY createdAt DESC")
    List<Location> getAllLocationsSync();

    @Query("SELECT * FROM location WHERE id = :id")
    LiveData<Location> getLocationById(int id);

    @Query("SELECT * FROM location WHERE id = :id")
    Location getLocationByIdSync(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertLocation(Location location);

    @Update
    void updateLocation(Location location);

    @Delete
    void deleteLocation(Location location);
}
