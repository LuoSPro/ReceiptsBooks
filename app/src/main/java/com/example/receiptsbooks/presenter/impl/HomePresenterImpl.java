package com.example.receiptsbooks.presenter.impl;

import com.example.receiptsbooks.presenter.IHomePresenter;
import com.example.receiptsbooks.view.IHomeCallback;

public class HomePresenterImpl implements IHomePresenter {
    private IHomeCallback mCallback = null;

    /**
     * 注册UI通知接口
     * @param callback
     */
    @Override
    public void registerViewCallback(IHomeCallback callback) {
        //设置UI的引用
        this.mCallback = callback;
    }

    /**
     * 取消UI通知的接口
     * @param callback
     */
    @Override
    public void unregisterViewCallback(IHomeCallback callback) {
        //取消UI的引用，避免引起内存泄漏
        this.mCallback = null;
    }
}
