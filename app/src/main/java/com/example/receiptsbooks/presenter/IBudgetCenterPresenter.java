package com.example.receiptsbooks.presenter;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import com.example.receiptsbooks.base.IBasePresenter;
import com.example.receiptsbooks.model.domain.BudgetInfo;
import com.example.receiptsbooks.room.bean.BudgetDateBean;
import com.example.receiptsbooks.view.IBudgetCenterCallback;

public interface IBudgetCenterPresenter extends IBasePresenter<IBudgetCenterCallback> {

    /**
     * 向数据库获取预算的信息
     */
    void getAllBudgetInfoFromDB(Fragment fragment, LifecycleOwner owner,int selectedDate);

    void updateBudgetItem(BudgetInfo budgetInfo);

    void updateTotalBudget(BudgetDateBean budgetDateBean);
}
