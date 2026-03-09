package com.example.eventmanager.repository;

import com.example.eventmanager.database.dao.VendorDao;
import com.example.eventmanager.model.Vendor;
import java.util.List;

public class VendorRepository {
    private final VendorDao vendorDao;

    public VendorRepository(VendorDao vendorDao) {
        this.vendorDao = vendorDao;
    }

    public List<Vendor> getAllVendors() {
        return vendorDao.getAllVendors();
    }

    public Vendor getVendorById(int id) {
        return vendorDao.getVendorById(id);
    }

    public void insert(Vendor vendor) {
        vendorDao.insertVendor(vendor);
    }

    public void update(Vendor vendor) {
        vendorDao.updateVendor(vendor);
    }

    public void delete(Vendor vendor) {
        vendorDao.deleteVendor(vendor);
    }
}
