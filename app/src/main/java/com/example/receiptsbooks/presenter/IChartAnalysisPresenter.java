package com.example.receiptsbooks.presenter;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import com.example.receiptsbooks.base.IBasePresenter;
import com.example.receiptsbooks.view.IChartAnalysisCallback;

public interface IChartAnalysisPresenter extends IBasePresenter<IChartAnalysisCallback> {

    /**
     * 向数据库请求数据
     */
    void getProductInfoFromDb(Fragment fragment, LifecycleOwner owner,int selectedDate);

}
