package com.example.receiptsbooks.room.repository;

import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.receiptsbooks.room.bean.BudgetDateBean;
import com.example.receiptsbooks.room.dao.BudgetDateDao;
import com.example.receiptsbooks.room.database.ReceiptDatabase;

import java.util.List;

public class BudgetDateRepository {
    private BudgetDateDao mBudgetDateDao;

    public BudgetDateRepository(Context context){
        mBudgetDateDao = ReceiptDatabase.getDatabase(context).getBudgetDateDao();
    }

    public LiveData<List<BudgetDateBean>> queryAllBudgetDate(){
        return mBudgetDateDao.queryAllBudgetDate();
    }

    //给外界提供接口
    public void insertBudgetDate(BudgetDateBean...budgetDateBeans){
        new InsertAsyncTask(mBudgetDateDao).execute(budgetDateBeans);
    }

    public void updateBudgetDate(BudgetDateBean...budgetDateBeans){
        new UpdateAsyncTask(mBudgetDateDao).execute(budgetDateBeans);
    }

    public LiveData<Integer> queryBudgetDateSize() {
        return mBudgetDateDao.queryBudgetDateSize();
    }

    public LiveData<BudgetDateBean> queryBudgetDateById(int selectedDate) {
        return mBudgetDateDao.queryBudgetDateById(selectedDate);
    }


    //插入
    static class InsertAsyncTask extends AsyncTask<BudgetDateBean,Void,Void> {
        private BudgetDateDao mBudgetDateDao;

        private InsertAsyncTask(BudgetDateDao budgetDateDao) {
            mBudgetDateDao = budgetDateDao;
        }

        @Override
        protected Void doInBackground(BudgetDateBean...budgetDateBeans) {
            mBudgetDateDao.insertBudgetDate(budgetDateBeans);
            return null;
        }
    }

    //修改
    static class UpdateAsyncTask extends AsyncTask<BudgetDateBean,Void,Void> {
        private BudgetDateDao mBudgetDateDao;

        private UpdateAsyncTask(BudgetDateDao budgetDateDao) {
            mBudgetDateDao = budgetDateDao;
        }

        @Override
        protected Void doInBackground(BudgetDateBean...budgetDateBeans) {
            mBudgetDateDao.updateBudgetDate(budgetDateBeans);
            return null;
        }
    }
}
