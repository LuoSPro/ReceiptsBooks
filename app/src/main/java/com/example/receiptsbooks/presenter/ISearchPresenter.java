package com.example.receiptsbooks.presenter;

import com.example.receiptsbooks.base.IBasePresenter;
import com.example.receiptsbooks.view.ISearchPageCallback;

public interface ISearchPresenter extends IBasePresenter<ISearchPageCallback> {

    /**
     * 获取历史内容
     */
    void getHistory();

    /**
     * 删除搜索历史
     */
    void delHistory();

    /**
     * 搜索
     * @param keyword
     */
    void doSearch(String keyword);


    /**
     * 重新搜索
     */
    void research();

    /**
     * 获取更多的搜索结果
     */
    void loaderMore();

    /**
     *获取推荐词
     */
    void getRecommendWords();

}
