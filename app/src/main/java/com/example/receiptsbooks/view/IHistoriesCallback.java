package com.example.receiptsbooks.view;

import com.example.receiptsbooks.base.IBaseCallback;
import com.example.receiptsbooks.room.bean.ReceiptAndProduct;

import java.util.List;

public interface IHistoriesCallback extends IBaseCallback {

    /**
     * 数据到了
     * @param receiptAndProducts
     */
    void onAllReceiptHistoriesLoaded(List<ReceiptAndProduct> receiptAndProducts);

}
