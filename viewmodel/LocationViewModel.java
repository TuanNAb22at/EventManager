package com.example.eventmanager.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.eventmanager.database.AppDatabase;
import com.example.eventmanager.model.Location;
import com.example.eventmanager.repository.LocationRepository;
import java.util.List;

public class LocationViewModel extends AndroidViewModel {
    private final LocationRepository repository;

    public LocationViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getInstance(application);
        repository = new LocationRepository(db.locationDao());
    }

    public LiveData<List<Location>> getAllLocations() {
        return repository.getAllLocations();
    }

    public LiveData<Location> getLocationById(int id) {
        return repository.getLocationById(id);
    }

    public void insert(Location location) {
        repository.insert(location);
    }

    public void update(Location location) {
        repository.update(location);
    }

    public void delete(Location location) {
        repository.delete(location);
    }
}
