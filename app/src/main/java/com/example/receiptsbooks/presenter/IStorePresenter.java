package com.example.receiptsbooks.presenter;

import com.example.receiptsbooks.base.IBasePresenter;
import com.example.receiptsbooks.view.IStoreCallback;

public interface IStorePresenter extends IBasePresenter<IStoreCallback> {
    /**
     * 获取商品分类
     */
    void getCategories();
}