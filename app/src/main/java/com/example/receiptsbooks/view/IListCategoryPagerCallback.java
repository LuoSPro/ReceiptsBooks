package com.example.receiptsbooks.view;

import com.example.receiptsbooks.base.IBaseCallback;
import com.example.receiptsbooks.room.bean.ReceiptAndProduct;

import java.util.List;

public interface IListCategoryPagerCallback extends IBaseCallback {

    /**
     * 数据加载回来
     * @param contents 加载回来的数据
     */
    void onContentLoaded(List<ReceiptAndProduct> contents);

    /**
     * 返回当前的category
     * @return
     */
    String getCurrentCategory();

    /**
     * 加载更多发生错误
     */
    void onLoadMoreError();

    /**
     * 加载更多时，没有更多了
     */
    void onLoadMoreEmpty();

    /**
     * 加载更多数据成功
     * @param contents 更多的数据
     */
    void onLoadMoreLoaded(List<ReceiptAndProduct> contents);
}
