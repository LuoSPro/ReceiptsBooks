package com.example.receiptsbooks.model.domain;

public class BudgetInfo implements Comparable<BudgetInfo>{
    private int id;
    private int dateId;
    private String mBudgetTitle;
    private double mBudgetMoney;
    private BudgetStatus mBudgetStatus;
    private int mBudgetProgress;
    private int mBudgetIcon;
    private double mBudgetBalance;
    private boolean mIsSetting;

    public BudgetInfo() {
    }

    public int getDateId() {
        return dateId;
    }

    public void setDateId(int dateId) {
        this.dateId = dateId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isSetting() {
        return mIsSetting;
    }

    public void setSetting(boolean setting) {
        mIsSetting = setting;
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

    public String getBudgetStatus() {
        if (mBudgetStatus == BudgetStatus.BALANCE){
            return "余额";
        }else if (mBudgetStatus == BudgetStatus.EXPEND){
            return "支出";
        }else if (mBudgetStatus == BudgetStatus.OVERSPEND){
            return "超支";
        }
        return "支出";
    }

    public void setBudgetStatus(BudgetStatus budgetStatus) {
        mBudgetStatus = budgetStatus;
    }

    public int getBudgetProgress() {
        if (mBudgetMoney == 0.0){//分母不能为0
            return 0;
        }
        return (int) ((mBudgetMoney-mBudgetBalance)/mBudgetMoney*100);
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

    @Override
    public int compareTo(BudgetInfo budgetInfo) {
        //倒序排序
        if (this.mBudgetBalance < budgetInfo.getBudgetBalance()){
            return 1;
        }else if(this.mBudgetBalance > budgetInfo.getBudgetBalance()){
            return -1;
        }else{
            return 0;
        }
    }

    public enum BudgetStatus{
        BALANCE, EXPEND,OVERSPEND
    }
}
