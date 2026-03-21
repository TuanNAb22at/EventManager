package com.example.eventmanager.database.dao;

import androidx.room.*;
import com.example.eventmanager.model.Location;
import java.util.List;

@Dao
public interface LocationDao {
    @Query("SELECT * FROM location")
    List<Location> getAllLocations();

    @Query("SELECT * FROM location WHERE id = :id")
    Location getLocationById(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertLocation(Location location);

    @Update
    void updateLocation(Location location);

    @Delete
    void deleteLocation(Location location);
}
