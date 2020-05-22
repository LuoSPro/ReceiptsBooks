package com.example.receiptsbooks.base;

public interface IBaseCallback {
    /**
     * 网络发生错误
     */
    void onNetworkError();

    void onLoading();

    void onEmpty();
}
