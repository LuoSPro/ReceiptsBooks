package com.example.receiptsbooks.room.repository;

import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.receiptsbooks.room.bean.ProductBean;
import com.example.receiptsbooks.room.bean.ReceiptAndProduct;
import com.example.receiptsbooks.room.dao.ProductDao;
import com.example.receiptsbooks.room.database.ReceiptDatabase;

import java.util.List;

public class ProductRepository {
    //LiveData默认就将他的操作放在主线程上执行，所以这里不用AsyncTask去开辟一个线程去执行这个操作
    private LiveData<List<ReceiptAndProduct>> allProductLive;
    private ProductDao mProductDao;

    public ProductRepository(Context context) {
        ReceiptDatabase productDatabase = ReceiptDatabase.getDatabase(context.getApplicationContext());
        mProductDao = productDatabase.getProductDao();
        allProductLive = mProductDao.getAllProduct();
    }

    public LiveData<List<ReceiptAndProduct>> getAllProductLive() {
        return allProductLive;
    }

    public LiveData<ReceiptAndProduct> getReceiptInfoById(int receiptId) {
        return mProductDao.getReceiptInfoById(receiptId);
    }

    public LiveData<List<ReceiptAndProduct>> getReceiptAndProductByDateLive(String type,long beginDate, long endDate) {
        return mProductDao.getReceiptAndProductByDate(type,beginDate,endDate);
    }

    public LiveData<List<ReceiptAndProduct>> getReceiptAndProductByDateLive(long beginDate, long endDate) {
        return mProductDao.getReceiptAndProductByDate(beginDate,endDate);
    }

    public LiveData<List<ProductBean>> getProductFromType(String productType){
        return mProductDao.getProductFromType(productType);
    }

    public LiveData<List<ReceiptAndProduct>> getReceiptAndProductByTypeLive(String type) {
        return mProductDao.getReceiptAndProductByType(type);
    }

    public LiveData<List<ProductBean>> getProductFromReceiptId(int receiptId){
        return mProductDao.getProductFromReceiptId(receiptId);
    }

    //给外界提供接口
    public void insertProduct(ProductBean...productBeans){
        new InsertAsyncTask(mProductDao).execute(productBeans);
    }

    public void deleteProduct(ProductBean...productBeans){
        new DeleteAsyncTask(mProductDao).execute(productBeans);
    }

    public void updateProduct(ProductBean...productBeans){
        new UpdateAsyncTask(mProductDao).execute(productBeans);
    }

    public void queryProductFromReceipt(int receiptId){

    }



    //插入
    static class InsertAsyncTask extends AsyncTask<ProductBean,Void,Void> {
        private ProductDao mProductDao;

        private InsertAsyncTask(ProductDao productDao) {
            mProductDao = productDao;
        }

        @Override
        protected Void doInBackground(ProductBean...productBeans) {
            mProductDao.insertProduct(productBeans);
            return null;
        }
    }

    //删除
    static class DeleteAsyncTask extends AsyncTask<ProductBean,Void,Void>{
        private ProductDao mProductDao;

        private DeleteAsyncTask(ProductDao productDao) {
            mProductDao = productDao;
        }

        @Override
        protected Void doInBackground(ProductBean...productBeans) {
            mProductDao.deleteProduct(productBeans);
            return null;
        }
    }

    //修改
    static class UpdateAsyncTask extends AsyncTask<ProductBean,Void,Void>{
        private ProductDao mProductDao;

        private UpdateAsyncTask(ProductDao productDao) {
            mProductDao = productDao;
        }

        @Override
        protected Void doInBackground(ProductBean...productBeans) {
            mProductDao.updateProduct(productBeans);
            return null;
        }
    }
}
