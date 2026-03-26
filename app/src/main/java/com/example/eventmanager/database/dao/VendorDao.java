package com.example.eventmanager.database.dao;

import androidx.room.*;
import com.example.eventmanager.model.Vendor;
import java.util.List;

@Dao
public interface VendorDao {
    @Query("SELECT * FROM vendor ORDER BY createdAt DESC")
    List<Vendor> getAllVendors();

    @Query("SELECT * FROM vendor WHERE createdBy = :userId ORDER BY createdAt DESC")
    List<Vendor> getVendorsByUserId(int userId);

    @Query("SELECT * FROM vendor WHERE id = :id")
    Vendor getVendorById(int id);

    @Query("SELECT v.* FROM vendor v INNER JOIN event_vendor ev ON v.id = ev.vendorId WHERE ev.eventId = :eventId")
    List<Vendor> getVendorsByEventIdSync(int eventId);

    @Query("SELECT DISTINCT serviceType FROM vendor WHERE serviceType IS NOT NULL AND serviceType != ''")
    List<String> getDistinctServiceTypes();

    @Query("SELECT * FROM vendor WHERE (name LIKE :query OR phone LIKE :query OR email LIKE :query) " +
           "AND (:serviceType = '' OR serviceType = :serviceType) " +
           "AND createdBy = :userId " +
           "ORDER BY createdAt DESC")
    List<Vendor> searchAndFilterVendors(int userId, String query, String serviceType);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertVendor(Vendor vendor);

    @Update
    void updateVendor(Vendor vendor);

    @Delete
    void deleteVendor(Vendor vendor);
}
