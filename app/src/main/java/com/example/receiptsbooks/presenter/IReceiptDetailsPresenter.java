package com.example.receiptsbooks.presenter;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import com.example.receiptsbooks.base.IBasePresenter;
import com.example.receiptsbooks.room.bean.ProductBean;
import com.example.receiptsbooks.room.bean.ReceiptInfoBean;
import com.example.receiptsbooks.view.IReceiptDetailsCallback;

public interface IReceiptDetailsPresenter extends IBasePresenter<IReceiptDetailsCallback> {

    /**
     * 传过来的小票数据的ID，这样减少了对象之间的序列化操作，也不会使两个组件同时持有一个对象的引用所造成的高耦合度
     * @param receiptId
     */
    void getReceiptInfoById(Fragment fragment, LifecycleOwner owner, int receiptId);

    void updateReceiptToDB(Fragment fragment,ReceiptInfoBean receiptInfoBean);

    void updateProductToDB(Fragment fragment,ProductBean product);

    void deleteProductToDB(Fragment fragment,ProductBean product);

    void insertProductToDB(Fragment fragment,ProductBean product);
}
