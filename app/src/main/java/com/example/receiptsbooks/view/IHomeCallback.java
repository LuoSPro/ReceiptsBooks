package com.example.receiptsbooks.view;

import com.example.receiptsbooks.base.IBaseCallback;
import com.example.receiptsbooks.room.bean.ReceiptAndProduct;

import java.util.List;

public interface IHomeCallback extends IBaseCallback {

    /**
     * 总的开支到了
     * @param totalExpend
     */
    void onTotalExpendLoaded(double totalExpend);

    /**
     * 总的预算到了
     * @param totalBudget
     */
    void onTotalBudgetLoaded(double totalBudget);

    /**
     * 今天的小票清单来了
     * @param receiptAndProducts
     * @param todayExpend
     */
    void onReceiptInfosLoaded(List<ReceiptAndProduct> receiptAndProducts, double todayExpend);
}
