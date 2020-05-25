package com.example.receiptsbooks.model.domain;

public class BudgetItem {
    private String mBudgetTitle;
    private Double mBudgetMoney;
    private BudgetStatus mBudgetStatus;
    private int mBudgetProgress;
    private int mBudgetIcon;
    private double mBudgetBalance;

    public String getBudgetTitle() {
        return mBudgetTitle;
    }

    public void setBudgetTitle(String budgetTitle) {
        mBudgetTitle = budgetTitle;
    }

    public Double getBudgetMoney() {
        return mBudgetMoney;
    }

    public void setBudgetMoney(Double budgetMoney) {
        mBudgetMoney = budgetMoney;
    }

    public String getBudgetStatus() {
        if (mBudgetStatus == BudgetStatus.BALANCE){
            return "余额";
        }else if (mBudgetStatus == BudgetStatus.SPEND){
            return "支出";
        }else if (mBudgetStatus == BudgetStatus.OVERSPEND){
            return "超支";
        }
        return "";
    }

    public void setBudgetStatus(BudgetStatus budgetStatus) {
        mBudgetStatus = budgetStatus;
    }

    public int getBudgetProgress() {
        return (int) ((mBudgetMoney-mBudgetBalance)/mBudgetMoney*100);
    }

    public void setBudgetProgress(int budgetProgress) {
        mBudgetProgress = budgetProgress;
    }

    public int getBudgetIcon() {
        return mBudgetIcon;
    }

    public void setBudgetIcon(int budgetIcon) {
        mBudgetIcon = budgetIcon;
    }

    public double getBudgetBalance() {
        return mBudgetBalance;
    }

    public void setBudgetBalance(double budgetBalance) {
        mBudgetBalance = budgetBalance;
    }

    public enum BudgetStatus{
        BALANCE,SPEND,OVERSPEND
    }
}
