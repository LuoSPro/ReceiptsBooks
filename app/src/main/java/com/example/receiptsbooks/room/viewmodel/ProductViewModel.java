package com.example.receiptsbooks.room.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.receiptsbooks.room.bean.ProductBean;
import com.example.receiptsbooks.room.bean.ReceiptAndProduct;
import com.example.receiptsbooks.room.repository.ProductRepository;

import java.util.List;

public class ProductViewModel extends AndroidViewModel {
    private ProductRepository mProductRepository;
    private int receiptId;

    public ProductViewModel(@NonNull Application application) {
        super(application);
        mProductRepository = new ProductRepository(application);
    }

    public LiveData<List<ReceiptAndProduct>> getAllProductLive() {
        return mProductRepository.getAllProductLive();
    }

    public LiveData<ReceiptAndProduct> getReceiptInfoById(int receiptId) {
        return mProductRepository.getReceiptInfoById(receiptId);
    }

    public LiveData<List<ProductBean>> getProductFromType(String productType) {
        return mProductRepository.getProductFromType(productType);
    }

    public LiveData<List<ReceiptAndProduct>> getReceiptAndProductByType(String type) {
        return mProductRepository.getReceiptAndProductByTypeLive(type);
    }

    public LiveData<List<ProductBean>> getProductFromReceiptId(int receiptId) {
        return mProductRepository.getProductFromReceiptId(receiptId);
    }

    public void insertProduct(ProductBean...productBeans){
        mProductRepository.insertProduct(productBeans);
    }

    public void deleteProduct(ProductBean...productBeans){
        mProductRepository.deleteProduct(productBeans);
    }

    public void updateProduct(ProductBean...productBeans){
        mProductRepository.updateProduct(productBeans);
    }

    public LiveData<List<ReceiptAndProduct>> getReceiptAndProductByDate(String type,long beginDate, long endDate) {
        return mProductRepository.getReceiptAndProductByDateLive(type,beginDate,endDate);
    }

    public LiveData<List<ReceiptAndProduct>> getReceiptAndProductByDate(long beginDate, long endDate) {
        return mProductRepository.getReceiptAndProductByDateLive(beginDate,endDate);
    }

}
