package com.example.receiptsbooks.room.bean;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "budget_date_table")
public class BudgetDateBean {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "budget_date_id")
    private int budgetDateId;
    @ColumnInfo(name = "total_budget")
    private double totalBudget;

    public int getBudgetDateId() {
        return budgetDateId;
    }

    public void setBudgetDateId(int budgetDateId) {
        this.budgetDateId = budgetDateId;
    }

    public double getTotalBudget() {
        return totalBudget;
    }

    public void setTotalBudget(double totalBudget) {
        this.totalBudget = totalBudget;
    }
}
