package com.example.eventmanager.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
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
        MutableLiveData<List<Location>> data = new MutableLiveData<>();
        executorService.execute(() -> data.postValue(locationDao.getAllLocations()));
        return data;
    }

    public LiveData<Location> getLocationById(int id) {
        MutableLiveData<Location> data = new MutableLiveData<>();
        executorService.execute(() -> data.postValue(locationDao.getLocationById(id)));
        return data;
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
