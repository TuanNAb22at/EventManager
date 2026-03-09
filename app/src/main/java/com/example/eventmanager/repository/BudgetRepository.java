package com.example.eventmanager.repository;

import com.example.eventmanager.database.dao.BudgetDao;
import com.example.eventmanager.model.Budget;
import java.util.List;

public class BudgetRepository {
    private final BudgetDao budgetDao;

    public BudgetRepository(BudgetDao budgetDao) {
        this.budgetDao = budgetDao;
    }

    public List<Budget> getAllBudgets() {
        return budgetDao.getAllBudgets();
    }

    public List<Budget> getBudgetsByEventId(int eventId) {
        return budgetDao.getBudgetsByEventId(eventId);
    }

    public Budget getBudgetById(int id) {
        return budgetDao.getBudgetById(id);
    }

    public void insert(Budget budget) {
        budgetDao.insertBudget(budget);
    }

    public void update(Budget budget) {
        budgetDao.updateBudget(budget);
    }

    public void delete(Budget budget) {
        budgetDao.deleteBudget(budget);
    }
}
