package com.example.receiptsbooks.base;

public interface IBasePresenter<T> {

    /**
     * 注册UI通知接口
     */
    void registerViewCallback(T callback);

    /**
     * 取消UI通知的接口
     */
    void unregisterViewCallback(T callback);

}