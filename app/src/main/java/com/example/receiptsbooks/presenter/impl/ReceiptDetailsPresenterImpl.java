package com.example.receiptsbooks.presenter.impl;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProviders;

import com.example.receiptsbooks.presenter.IReceiptDetailsPresenter;
import com.example.receiptsbooks.room.bean.ProductBean;
import com.example.receiptsbooks.room.bean.ReceiptInfoBean;
import com.example.receiptsbooks.room.viewmodel.ProductViewModel;
import com.example.receiptsbooks.room.viewmodel.ReceiptInfoViewModel;
import com.example.receiptsbooks.utils.DateUtils;
import com.example.receiptsbooks.view.IReceiptDetailsCallback;

public class ReceiptDetailsPresenterImpl implements IReceiptDetailsPresenter {
    private IReceiptDetailsCallback mReceiptDetailsCallback = null;
    private ProductViewModel mProductViewModel;
    private ReceiptInfoViewModel mReceiptInfoViewModel;

    @Override
    public void getReceiptInfoById(Fragment fragment, LifecycleOwner owner, int receiptId) {
        if (mReceiptDetailsCallback != null) {
            mReceiptDetailsCallback.onLoading();
        }
        if (mProductViewModel == null) {
            createProductViewModel(fragment);
        }
        mProductViewModel.getReceiptInfoById(receiptId).observe(owner, receiptAndProduct -> {
            if (mReceiptDetailsCallback != null){
                mReceiptDetailsCallback.onReceiptAndProductLoaded(receiptAndProduct);
            }
        });
    }

    @Override
    public void updateReceiptToDB(Fragment fragment,ReceiptInfoBean receiptInfoBean) {
        if (mReceiptInfoViewModel == null){
            createReceiptInfoViewModel(fragment);
        }
        receiptInfoBean.setSaveData(DateUtils.revertDate(System.currentTimeMillis()));
        mReceiptInfoViewModel.updateReceiptInfo(receiptInfoBean);
    }

    @Override
    public void updateProductToDB(Fragment fragment, ProductBean product) {
        if (mProductViewModel == null){
            createProductViewModel(fragment);
        }
        mProductViewModel.updateProduct(product);
    }

    @Override
    public void deleteProductToDB(Fragment fragment, ProductBean product) {
        if (mProductViewModel == null){
            createProductViewModel(fragment);
        }
        mProductViewModel.deleteProduct(product);
    }

    @Override
    public void insertProductToDB(Fragment fragment, ProductBean product) {
        if (mProductViewModel == null){
            createProductViewModel(fragment);
        }
        mProductViewModel.insertProduct(product);
    }

    private void createProductViewModel(Fragment fragment){
        mProductViewModel = ViewModelProviders.of(fragment).get(ProductViewModel.class);
    }

    private void createReceiptInfoViewModel(Fragment fragment){
        mReceiptInfoViewModel = ViewModelProviders.of(fragment).get(ReceiptInfoViewModel.class);
    }


    @Override
    public void registerViewCallback(IReceiptDetailsCallback callback) {
        this.mReceiptDetailsCallback = callback;
    }

    @Override
    public void unregisterViewCallback(IReceiptDetailsCallback callback) {
        mReceiptDetailsCallback = null;
    }
}
