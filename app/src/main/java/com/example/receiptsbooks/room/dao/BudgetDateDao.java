package com.example.receiptsbooks.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.receiptsbooks.room.bean.BudgetDateBean;

import java.util.List;

@Dao
public interface BudgetDateDao {
    @Insert
    void insertBudgetDate(BudgetDateBean...budgetDateBeans);

    @Update
    void updateBudgetDate(BudgetDateBean...budgetDateBeans);

    @Query("SELECT * FROM budget_date_table")
    LiveData<List<BudgetDateBean>> queryAllBudgetDate();

    @Query("SELECT total_budget FROM budget_date_table WHERE budget_date_id =:id")
    double queryTotalBudgetMoneyById(int id);

    @Query("SELECT count(*) FROM budget_date_table WHERE 1")
    LiveData<Integer> queryBudgetDateSize();

    @Query("SELECT * FROM budget_date_table WHERE budget_date_id = :selectedDate")
    LiveData<BudgetDateBean> queryBudgetDateById(int selectedDate);
}
