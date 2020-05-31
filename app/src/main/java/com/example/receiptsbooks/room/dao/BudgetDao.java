package com.example.receiptsbooks.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.receiptsbooks.room.bean.BudgetBean;

import java.util.List;

@Dao
public interface BudgetDao {
    @Insert
    void insertBudget(BudgetBean...budgetBeans);

    @Update
    void updateBudget(BudgetBean...budgetBeans);

    @Query("SELECT * FROM budget_table")
    LiveData<List<BudgetBean>> queryAllBudgetInfo();

    @Query("SELECT * FROM budget_table WHERE budget_date_id = :dateId")
    LiveData<List<BudgetBean>> queryBudgetInfoByDateId(int dateId);
}
