package com.example.receiptsbooks.presenter;

import com.example.receiptsbooks.base.IBasePresenter;
import com.example.receiptsbooks.view.IStoreCategoryPagerCallback;

public interface IStoreCategoryPagerPresenter extends IBasePresenter<IStoreCategoryPagerCallback> {
    /**
     * 根据分类id去获取内容
     *
     * @param categoryId
     */
    void getContentByCategoryId(int categoryId);

    void loaderMore(int categoryId);

    void reload(int categoryId);
}
