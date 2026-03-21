package com.example.eventmanager.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.eventmanager.model.Schedule;

import java.util.List;

@Dao
public interface ScheduleDao {
    @Insert
    void insert(Schedule schedule);

    @Update
    void update(Schedule schedule);

    @Delete
    void delete(Schedule schedule);

    @Query("SELECT * FROM Schedule WHERE id = :id")
    Schedule getScheduleById(int id);

    @Query("SELECT * FROM Schedule WHERE eventId = :eventId ORDER BY time ASC")
    List<Schedule> getSchedulesByEventId(int eventId);

    @Query("SELECT * FROM Schedule")
    List<Schedule> getAllSchedules();
}
