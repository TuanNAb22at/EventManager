package com.example.eventmanager.repository;

import com.example.eventmanager.database.dao.LocationDao;
import com.example.eventmanager.model.Location;

import java.util.List;

public class LocationRepository {
    private final LocationDao locationDao;

    public LocationRepository(LocationDao locationDao) {
        this.locationDao = locationDao;
    }

    public Location getLocationById(int id) {
        return locationDao.getLocationById(id);
    }

    public void insert(Location location) {
        locationDao.insertLocation(location);
    }


    public void delete(Location location) {
        locationDao.deleteLocation(location);
    }
}
