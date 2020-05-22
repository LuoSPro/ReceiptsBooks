package com.example.receiptsbooks.view;

import com.example.receiptsbooks.base.IBaseCallback;
import com.example.receiptsbooks.model.domain.ReceiptInfo;

public interface IReceiptInfoCallback extends IBaseCallback {

    /**
     * 小票信息的结果加载到了
     * @param receiptInfo
     */
    void onResultLoaded(ReceiptInfo receiptInfo);

    /**
     * 图片解析发生错误
     */
    void onAnalysisError();

    /**
     * 加载中
     */
    void onLoading();

    /**
     * 返回数据为空
     */
    void onEmpty();
}
