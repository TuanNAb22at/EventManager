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
    void insertLocation(Location location);

//    @Update("UPDATE location SET name = :name, address = :address WHERE id = :id")
//    void updateLocation(int id, String name, String address);

    @Delete
    void deleteLocation(Location location);
}
