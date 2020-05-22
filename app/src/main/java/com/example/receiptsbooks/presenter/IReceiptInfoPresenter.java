package com.example.receiptsbooks.presenter;

import android.content.Context;

import androidx.fragment.app.FragmentActivity;

import com.example.receiptsbooks.base.IBasePresenter;
import com.example.receiptsbooks.model.domain.ReceiptInfo;
import com.example.receiptsbooks.view.IReceiptInfoCallback;

public interface IReceiptInfoPresenter extends IBasePresenter<IReceiptInfoCallback> {

    /**
     * 获取到小票信息的返回结果
     */
    void getReceiptInfo(String filePath, Context context);

    /**
     * 将小票信息保存到数据库中
     * @param receiptInfo 小票信息
     */
    void saveReceiptToDB(FragmentActivity activity, ReceiptInfo receiptInfo,String receiptPhotoPath);

}
