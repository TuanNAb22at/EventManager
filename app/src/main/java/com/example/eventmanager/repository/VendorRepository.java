package com.example.eventmanager.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.eventmanager.database.dao.VendorDao;
import com.example.eventmanager.model.Vendor;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VendorRepository {
    private final VendorDao vendorDao;
    private final ExecutorService executorService;

    public VendorRepository(VendorDao vendorDao) {
        this.vendorDao = vendorDao;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Vendor>> getAllVendors() {
        MutableLiveData<List<Vendor>> data = new MutableLiveData<>();
        executorService.execute(() -> data.postValue(vendorDao.getAllVendors()));
        return data;
    }

    public LiveData<Vendor> getVendorById(int id) {
        MutableLiveData<Vendor> data = new MutableLiveData<>();
        executorService.execute(() -> data.postValue(vendorDao.getVendorById(id)));
        return data;
    }

    public void insert(Vendor vendor) {
        executorService.execute(() -> vendorDao.insertVendor(vendor));
    }

    public void update(Vendor vendor) {
        executorService.execute(() -> vendorDao.updateVendor(vendor));
    }

    public void delete(Vendor vendor) {
        executorService.execute(() -> vendorDao.deleteVendor(vendor));
    }
}
