package com.example.eventmanager.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.eventmanager.database.dao.BudgetDao;
import com.example.eventmanager.model.Budget;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BudgetRepository {
    private final BudgetDao budgetDao;
    private final ExecutorService executorService;

    public BudgetRepository(BudgetDao budgetDao) {
        this.budgetDao = budgetDao;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Budget>> getAllBudgets() {
        MutableLiveData<List<Budget>> data = new MutableLiveData<>();
        executorService.execute(() -> data.postValue(budgetDao.getAllBudgets()));
        return data;
    }

    public LiveData<List<Budget>> getBudgetsByEventId(int eventId) {
        MutableLiveData<List<Budget>> data = new MutableLiveData<>();
        executorService.execute(() -> data.postValue(budgetDao.getBudgetsByEventId(eventId)));
        return data;
    }

    public LiveData<Budget> getBudgetById(int id) {
        MutableLiveData<Budget> data = new MutableLiveData<>();
        executorService.execute(() -> data.postValue(budgetDao.getBudgetById(id)));
        return data;
    }

    public void insert(Budget budget) {
        executorService.execute(() -> budgetDao.insertBudget(budget));
    }

    public void update(Budget budget) {
        executorService.execute(() -> budgetDao.updateBudget(budget));
    }

    public void delete(Budget budget) {
        executorService.execute(() -> budgetDao.deleteBudget(budget));
    }
}
