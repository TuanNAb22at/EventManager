package com.example.eventmanager.repository;

import com.example.eventmanager.database.dao.GuestDao;
import com.example.eventmanager.model.Guest;
import java.util.List;

public class GuestRepository {
    private final GuestDao guestDao;

    public GuestRepository(GuestDao guestDao) {
        this.guestDao = guestDao;
    }

    public List<Guest> getAllGuests() {
        return guestDao.getAllGuests();
    }

    public List<Guest> getGuestsByEventId(int eventId) {
        return guestDao.getGuestsByEventId(eventId);
    }

    public Guest getGuestById(int id) {
        return guestDao.getGuestById(id);
    }

    public void insert(Guest guest) {
        guestDao.insertGuest(guest);
    }

    public void update(Guest guest) {
        guestDao.updateGuest(guest);
    }

    public void delete(Guest guest) {
        guestDao.deleteGuest(guest);
    }
}
