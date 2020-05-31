package com.example.receiptsbooks.room.bean;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "budget_table")
public class BudgetBean {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "budget_id")
    private int id;
    @ColumnInfo(name = "budget_date_id")
    private int dateId;
    @ColumnInfo(name = "product_type")
    private String mBudgetTitle;
    @ColumnInfo(name = "product_budget")
    private double mBudgetMoney;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDateId() {
        return dateId;
    }

    public void setDateId(int dateId) {
        this.dateId = dateId;
    }

    public String getBudgetTitle() {
        return mBudgetTitle;
    }

    public void setBudgetTitle(String budgetTitle) {
        mBudgetTitle = budgetTitle;
    }

    public double getBudgetMoney() {
        return mBudgetMoney;
    }

    public void setBudgetMoney(double budgetMoney) {
        mBudgetMoney = budgetMoney;
    }
}
