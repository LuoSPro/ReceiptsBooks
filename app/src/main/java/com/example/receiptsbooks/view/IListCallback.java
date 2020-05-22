package com.example.receiptsbooks.view;

import java.util.List;

public interface IListCallback {
    /**
     * 数据
     */
    void onCategoriesLoaded(List<String> categories);
}
