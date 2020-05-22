package com.example.receiptsbooks.presenter.impl;

import com.example.receiptsbooks.presenter.IListPresenter;
import com.example.receiptsbooks.utils.Constants;
import com.example.receiptsbooks.view.IListCallback;

public class ListPresenterImpl implements IListPresenter {
    private IListCallback mCallback = null;

    @Override
    public void getCategories() {
        //加载分类数据
        if (mCallback != null){
            mCallback.onCategoriesLoaded(Constants.PRODUCT_TYPE_LIST);
        }
    }

    @Override
    public void registerViewCallback(IListCallback callback) {
        this.mCallback = callback;
    }

    @Override
    public void unregisterViewCallback(IListCallback callback) {
        this.mCallback = null;
    }
}
