package com.example.eventmanager.repository;

import androidx.lifecycle.LiveData;
import com.example.eventmanager.database.dao.LocationDao;
import com.example.eventmanager.model.Location;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LocationRepository {
    private final LocationDao locationDao;
    private final ExecutorService executorService;

    public LocationRepository(LocationDao locationDao) {
        this.locationDao = locationDao;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Location>> getAllLocations() {
        return locationDao.getAllLocations();
    }

    public LiveData<Location> getLocationById(int id) {
        return locationDao.getLocationById(id);
    }

    public void insert(Location location) {
        executorService.execute(() -> locationDao.insertLocation(location));
    }

    public void update(Location location) {
        executorService.execute(() -> locationDao.updateLocation(location));
    }

    public void delete(Location location) {
        executorService.execute(() -> locationDao.deleteLocation(location));
    }
}
