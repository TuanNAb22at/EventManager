package com.example.eventmanager.database.dao;

import androidx.room.*;
import com.example.eventmanager.model.Vendor;
import java.util.List;

@Dao
public interface VendorDao {
    @Query("SELECT * FROM vendor")
    List<Vendor> getAllVendors();

    @Query("SELECT * FROM vendor WHERE createdBy = :userId")
    List<Vendor> getVendorsByUserId(int userId);

    @Query("SELECT * FROM vendor WHERE id = :id")
    Vendor getVendorById(int id);

    @Query("SELECT DISTINCT serviceType FROM vendor WHERE serviceType IS NOT NULL AND serviceType != ''")
    List<String> getDistinctServiceTypes();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertVendor(Vendor vendor);

    @Update
    void updateVendor(Vendor vendor);

    @Delete
    void deleteVendor(Vendor vendor);
}
