package com.example.receiptsbooks.view;

import com.example.receiptsbooks.base.IBaseCallback;
import com.example.receiptsbooks.room.bean.ReceiptAndProduct;

public interface IReceiptDetailsCallback extends IBaseCallback {

    /**
     * 小票数据传到了
     * @param receiptInfo
     */
    void onReceiptAndProductLoaded(ReceiptAndProduct receiptInfo);

}
