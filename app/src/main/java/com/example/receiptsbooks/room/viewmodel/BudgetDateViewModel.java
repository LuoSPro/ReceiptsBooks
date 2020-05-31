package com.example.receiptsbooks.room.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.receiptsbooks.room.bean.BudgetDateBean;
import com.example.receiptsbooks.room.repository.BudgetDateRepository;

import java.util.List;

public class BudgetDateViewModel extends AndroidViewModel {
    private BudgetDateRepository mBudgetDateRepository;

    public BudgetDateViewModel(@NonNull Application application) {
        super(application);
        mBudgetDateRepository = new BudgetDateRepository(application);
    }

    public LiveData<List<BudgetDateBean>> queryAllBudgetDate(){
        return mBudgetDateRepository.queryAllBudgetDate();
    }

    public void insertDateBean(BudgetDateBean...budgetDateBeans){
        mBudgetDateRepository.insertBudgetDate(budgetDateBeans);
    }

    public void updateDateBean(BudgetDateBean...budgetDateBeans){
        mBudgetDateRepository.updateBudgetDate(budgetDateBeans);
    }

    public LiveData<Integer> queryBudgetDateSize() {
        return mBudgetDateRepository.queryBudgetDateSize();
    }

    public LiveData<BudgetDateBean> queryBudgetDateById(int selectedDate) {
        return mBudgetDateRepository.queryBudgetDateById(selectedDate);
    }
}
