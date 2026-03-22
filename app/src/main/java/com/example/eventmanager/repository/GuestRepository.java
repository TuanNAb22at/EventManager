package com.example.eventmanager.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.eventmanager.database.dao.GuestDao;
import com.example.eventmanager.model.Guest;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GuestRepository {
    private final GuestDao guestDao;
    private final ExecutorService executorService;

    public GuestRepository(GuestDao guestDao) {
        this.guestDao = guestDao;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Guest>> getAllGuests() {
        MutableLiveData<List<Guest>> data = new MutableLiveData<>();
        executorService.execute(() -> data.postValue(guestDao.getAllGuests()));
        return data;
    }

    public LiveData<List<Guest>> getGuestsByEventId(int eventId) {
        MutableLiveData<List<Guest>> data = new MutableLiveData<>();
        executorService.execute(() -> data.postValue(guestDao.getGuestsByEventId(eventId)));
        return data;
    }

    public LiveData<Guest> getGuestById(int id) {
        MutableLiveData<Guest> data = new MutableLiveData<>();
        executorService.execute(() -> data.postValue(guestDao.getGuestById(id)));
        return data;
    }

    public void insert(Guest guest) {
        executorService.execute(() -> guestDao.insertGuest(guest));
    }

    public void update(Guest guest) {
        executorService.execute(() -> guestDao.updateGuest(guest));
    }

    public void delete(Guest guest) {
        executorService.execute(() -> guestDao.deleteGuest(guest));
    }
}
