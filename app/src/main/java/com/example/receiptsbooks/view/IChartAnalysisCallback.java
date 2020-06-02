package com.example.receiptsbooks.view;

import com.example.receiptsbooks.base.IBaseCallback;
import com.example.receiptsbooks.model.domain.BudgetInfo;
import com.example.receiptsbooks.room.bean.ReceiptAndProduct;

import java.util.List;

public interface IChartAnalysisCallback extends IBaseCallback {

    void onProductInfoLoaded(List<BudgetInfo> budgetInfos,double totalPrice,List<ReceiptAndProduct> receiptAndProducts);

}
