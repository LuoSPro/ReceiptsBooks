package com.example.receiptsbooks.presenter;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import com.example.receiptsbooks.base.IBasePresenter;
import com.example.receiptsbooks.view.IHistoriesCallback;

public interface IHistoriesPresenter extends IBasePresenter<IHistoriesCallback> {

    /**
     * 得到全部历史
     */
    void getAllReceiptHistories(Fragment fragment, LifecycleOwner owner);

}
