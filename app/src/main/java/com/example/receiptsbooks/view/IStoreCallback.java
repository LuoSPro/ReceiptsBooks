package com.example.receiptsbooks.view;

import com.example.receiptsbooks.base.IBaseCallback;
import com.example.receiptsbooks.model.domain.Categories;

public interface IStoreCallback extends IBaseCallback {

    /**
     * 通知UI更新
     */
    void onCategoriesLoaded(Categories categories);

}
