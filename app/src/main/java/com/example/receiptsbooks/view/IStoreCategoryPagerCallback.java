package com.example.receiptsbooks.view;

import com.example.receiptsbooks.base.IBaseCallback;
import com.example.receiptsbooks.model.domain.StorePagerContent;

import java.util.List;

public interface IStoreCategoryPagerCallback extends IBaseCallback {
    /**
     * 数据加载回来
     *
     * @param contents
     */
    void onContentLoaded(List<StorePagerContent.DataBean> contents);

    /**
     * 返回当前的categoryId
     * @return
     */
    int getCategoryId();

    /**
     * 加载更多时网络错误
     */
    void onLoaderMoreError();

    /**
     * 没有更多内容
     */
    void onLoaderMoreEmpty();

    /**
     *
     * @param contents
     */
    void onLoaderMoreLoaded(List<StorePagerContent.DataBean> contents);

    /**
     * 轮播图内容加载到了
     * @param contents
     */
    void onLooperListLoaded(List<StorePagerContent.DataBean> contents);
}
