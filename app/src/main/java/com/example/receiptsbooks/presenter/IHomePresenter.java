package com.example.receiptsbooks.presenter;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import com.example.receiptsbooks.base.IBasePresenter;
import com.example.receiptsbooks.view.IHomeCallback;

public interface IHomePresenter extends IBasePresenter<IHomeCallback> {

    /**
     * 获取当月的总支出
     */
    void getCurMonthTotalExpend(Fragment fragment, LifecycleOwner owner);

    /**
     * 获取当天的记账记录
     */
    void getTodayReceiptInfos(Fragment fragment, LifecycleOwner owner);
}
