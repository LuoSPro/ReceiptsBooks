package com.example.receiptsbooks.presenter.impl;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProviders;

import com.example.receiptsbooks.presenter.IHistoriesPresenter;
import com.example.receiptsbooks.room.viewmodel.ProductViewModel;
import com.example.receiptsbooks.view.IHistoriesCallback;

public class HistoriesPresenterImpl implements IHistoriesPresenter {
    private IHistoriesCallback mCallback;
    private ProductViewModel mProductViewModel;

    @Override
    public void getAllReceiptHistories(Fragment fragment, LifecycleOwner owner) {
        if (mCallback != null) {
            mCallback.onLoading();
        }
        if (mProductViewModel == null) {
            mProductViewModel = ViewModelProviders.of(fragment).get(ProductViewModel.class);
        }
        mProductViewModel.getAllProductLive().observe(owner, receiptAndProducts -> {
            if (mCallback != null) {
                if (receiptAndProducts.size() == 0){
                    mCallback.onEmpty();
                }else {
                    mCallback.onAllReceiptHistoriesLoaded(receiptAndProducts);
                }
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
