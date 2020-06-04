package com.example.receiptsbooks.room.repository;

import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.receiptsbooks.model.domain.ReceiptInfo;
import com.example.receiptsbooks.room.bean.ReceiptInfoBean;
import com.example.receiptsbooks.room.dao.ReceiptInfoDao;
import com.example.receiptsbooks.room.database.ReceiptDatabase;
import com.example.receiptsbooks.utils.DateUtils;

import java.util.List;

public class ReceiptInfoRepository {
    private LiveData<List<ReceiptInfoBean>> allReceiptInfoLive;
    private LiveData<List<ReceiptInfoBean>> allReceiptInfoByDateLive;
    private ReceiptInfoDao mInfoDao;

    public ReceiptInfoRepository(Context context) {
        ReceiptDatabase infoDatabase = ReceiptDatabase.getDatabase(context.getApplicationContext());
        mInfoDao = infoDatabase.getReceiptInfoDao();
        allReceiptInfoLive = mInfoDao.getAllReceiptInfo();
        allReceiptInfoByDateLive = mInfoDao.getAllReceiptInfoByDate();
    }

    public LiveData<List<ReceiptInfoBean>> getAllReceiptInfoLive() {
        return allReceiptInfoLive;
    }

    public LiveData<List<ReceiptInfoBean>> getAllReceiptInfoByDateLive() {
        return allReceiptInfoByDateLive;
    }

    //给外界提供接口
    public void insertReceiptInfo(ResponseCallback responseCallback, ReceiptInfoBean... receiptInfoBeans){
        new InsertAsyncTask(mInfoDao,responseCallback).execute(receiptInfoBeans);
    }

    public void deleteReceiptInfo(ReceiptInfoBean...receiptInfoBeans){
        new DeleteAsyncTask(mInfoDao).execute(receiptInfoBeans);
    }

    public void updateReceiptInfo(ReceiptInfoBean...receiptInfoBeans){
        new UpdateAsyncTask(mInfoDao).execute(receiptInfoBeans);
    }

    public LiveData<List<ReceiptInfoBean>> queryReceiptExists(ReceiptInfo receiptInfo) {
        return mInfoDao.queryReceiptExists(DateUtils.stringToData(receiptInfo.getDate()),receiptInfo.getTotalPrice(),receiptInfo.getReceiptPhoto());
    }

    //插入
    static class InsertAsyncTask extends AsyncTask<ReceiptInfoBean, Void, Void> {
        private ReceiptInfoDao mInfoDao;
        private ResponseCallback mResponseCallback;

        private InsertAsyncTask(ReceiptInfoDao receiptInfoDao, ResponseCallback responseCallback) {
            mInfoDao = receiptInfoDao;
            mResponseCallback = responseCallback;
        }

        @Override
        protected Void doInBackground(ReceiptInfoBean...receiptInfoBeans) {
            List<Long> receiptId = mInfoDao.insertReceiptInfo(receiptInfoBeans);
            if (mResponseCallback != null) {
                mResponseCallback.onRespond(receiptId);
            }
            return null;
        }
    }

    //删除
    static class DeleteAsyncTask extends AsyncTask<ReceiptInfoBean,Void,Void>{
        private ReceiptInfoDao mInfoDao;

        private DeleteAsyncTask(ReceiptInfoDao receiptInfoDao) {
            mInfoDao = receiptInfoDao;
        }

        @Override
        protected Void doInBackground(ReceiptInfoBean...receiptInfoBeans) {
            mInfoDao.deleteReceiptInfo(receiptInfoBeans);
            return null;
        }
    }

    //修改
    static class UpdateAsyncTask extends AsyncTask<ReceiptInfoBean,Void,Void>{
        private ReceiptInfoDao mInfoDao;

        private UpdateAsyncTask(ReceiptInfoDao receiptInfoDao) {
            mInfoDao = receiptInfoDao;
        }

        @Override
        protected Void doInBackground(ReceiptInfoBean...receiptInfoBeans) {
            mInfoDao.updateReceiptInfo(receiptInfoBeans);
            return null;
        }
    }

    public interface ResponseCallback{
        void onRespond(List<Long> receiptId);
    }
}
