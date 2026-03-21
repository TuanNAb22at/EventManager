package com.example.eventmanager.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.eventmanager.model.Feedback;

import java.util.List;

@Dao
public interface FeedbackDao {
    @Insert
    void insert(Feedback feedback);

    @Update
    void update(Feedback feedback);

    @Delete
    void delete(Feedback feedback);

    @Query("SELECT * FROM Feedback WHERE id = :id")
    Feedback getFeedbackById(int id);

    @Query("SELECT * FROM Feedback WHERE eventId = :eventId ORDER BY dateSubmitted DESC")
    List<Feedback> getFeedbacksByEventId(int eventId);

    @Query("SELECT * FROM Feedback WHERE userId = :userId ORDER BY dateSubmitted DESC")
    List<Feedback> getFeedbacksByUserId(int userId);

    @Query("SELECT * FROM Feedback")
    List<Feedback> getAllFeedbacks();

    @Query("SELECT AVG(rating) FROM Feedback WHERE eventId = :eventId")
    double getAverageRatingForEvent(int eventId);
}
