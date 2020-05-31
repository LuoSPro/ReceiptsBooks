package com.example.receiptsbooks.room.repository;

import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.receiptsbooks.room.bean.BudgetBean;
import com.example.receiptsbooks.room.dao.BudgetDao;
import com.example.receiptsbooks.room.database.ReceiptDatabase;

import java.util.List;

public class BudgetRepository {

    private BudgetDao mBudgetDao;

    public BudgetRepository(Context context){
        mBudgetDao = ReceiptDatabase.getDatabase(context).getBudgetDao();
    }

    public LiveData<List<BudgetBean>> queryBudgetInfoByDateId(int id){
        return mBudgetDao.queryBudgetInfoByDateId(id);
    }

    //给外界提供接口
    public void insertBudget(BudgetBean... budgetBeans){
        new InsertAsyncTask(mBudgetDao).execute(budgetBeans);
    }

    public void updateBudget(BudgetBean...budgetBeans){
        new UpdateAsyncTask(mBudgetDao).execute(budgetBeans);
    }


    //插入
    static class InsertAsyncTask extends AsyncTask<BudgetBean,Void,Void> {
        private BudgetDao mBudgetDao;

        private InsertAsyncTask(BudgetDao budgetDao) {
            mBudgetDao = budgetDao;
        }

        @Override
        protected Void doInBackground(BudgetBean...budgetBeans) {
            mBudgetDao.insertBudget(budgetBeans);
            return null;
        }
    }

    //修改
    static class UpdateAsyncTask extends AsyncTask<BudgetBean,Void,Void> {
        private BudgetDao mBudgetDao;

        private UpdateAsyncTask(BudgetDao budgetDao) {
            mBudgetDao = budgetDao;
        }

        @Override
        protected Void doInBackground(BudgetBean...budgetBeans) {
            mBudgetDao.updateBudget(budgetBeans);
            return null;
        }
    }
}
