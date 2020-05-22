package com.example.receiptsbooks.presenter;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import com.example.receiptsbooks.base.IBasePresenter;
import com.example.receiptsbooks.view.IListCategoryPagerCallback;

import java.util.Date;

public interface IListCategoryPagerPresenter extends IBasePresenter<IListCategoryPagerCallback> {

    void getContentByCategory(Fragment fragment, LifecycleOwner owner, String category,Date beginDate,Date endDate);

    void loadMore(String Category);

    void reload(String Category);

}
