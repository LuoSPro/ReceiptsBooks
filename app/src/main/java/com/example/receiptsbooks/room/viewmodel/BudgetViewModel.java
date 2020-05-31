package com.example.receiptsbooks.room.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.receiptsbooks.room.bean.BudgetBean;
import com.example.receiptsbooks.room.repository.BudgetRepository;

import java.util.List;

public class BudgetViewModel extends AndroidViewModel {
    private BudgetRepository mBudgetRepository;

    public BudgetViewModel(@NonNull Application application) {
        super(application);
        mBudgetRepository = new BudgetRepository(application);
    }

    public LiveData<List<BudgetBean>> queryBudgetInfoByDateId(int id){
        return mBudgetRepository.queryBudgetInfoByDateId(id);
    }

    public void insertProduct(BudgetBean...budgetBeans){
        mBudgetRepository.insertBudget(budgetBeans);
    }

    public void updateProduct(BudgetBean...budgetBeans){
        mBudgetRepository.updateBudget(budgetBeans);
    }
}
