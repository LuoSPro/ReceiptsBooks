package com.example.receiptsbooks.view;

import com.example.receiptsbooks.base.IBaseCallback;
import com.example.receiptsbooks.model.domain.BudgetInfo;
import com.example.receiptsbooks.room.bean.BudgetDateBean;

import java.util.List;

public interface IBudgetCenterCallback extends IBaseCallback {

    /**
     * 向数据库获取的内容的到了
     * @param budgetDateBean
     */
    void onBudgetDataLoaded(BudgetDateBean budgetDateBean);

    /**
     * 向数据库获取的预算内容内容的到了
     */
    void onCurBudgetInfoLoaded(List<BudgetInfo> budgetInfos);

    /**
     * 该时间段的所有商品开支到了
     * @param totalExpend
     */
    void onTotalExpendLoaded(double totalExpend);
}
