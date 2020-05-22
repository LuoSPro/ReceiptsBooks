package com.example.receiptsbooks.presenter;

import com.example.receiptsbooks.base.IBasePresenter;
import com.example.receiptsbooks.view.IListCallback;

public interface IListPresenter extends IBasePresenter<IListCallback> {

    /**
     * 获取商品分类
     */
    void getCategories();
}
