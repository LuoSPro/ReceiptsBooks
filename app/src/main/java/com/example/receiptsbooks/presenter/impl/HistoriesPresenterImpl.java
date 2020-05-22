package com.example.receiptsbooks.presenter.impl;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.receiptsbooks.presenter.IHistoriesPresenter;
import com.example.receiptsbooks.room.bean.ReceiptAndProduct;
import com.example.receiptsbooks.room.viewmodel.ProductViewModel;
import com.example.receiptsbooks.view.IHistoriesCallback;

import java.util.List;

public class HistoriesPresenterImpl implements IHistoriesPresenter {
    private IHistoriesCallback mCallback;
    private ProductViewModel mProductViewModel;

    @Override
    public void getAllReceiptHistories(Fragment fragment, LifecycleOwner owner) {
        if (mCallback != null) {
            mCallback.onLoading();
        }
        mProductViewModel = ViewModelProviders.of(fragment).get(ProductViewModel.class);
        mProductViewModel.getAllProductLive().observe(owner, new Observer<List<ReceiptAndProduct>>() {
            @Override
            public void onChanged(List<ReceiptAndProduct> receiptAndProducts) {
                mCallback.onAllReceiptHistoriesLoaded(receiptAndProducts);
            }
        });
    }

    @Override
    public void registerViewCallback(IHistoriesCallback callback) {
        this.mCallback = callback;
    }

    @Override
    public void unregisterViewCallback(IHistoriesCallback callback) {
        mCallback = null;
    }
}
