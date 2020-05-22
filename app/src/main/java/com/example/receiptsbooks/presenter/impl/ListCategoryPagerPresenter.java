package com.example.receiptsbooks.presenter.impl;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.receiptsbooks.presenter.IListCategoryPagerPresenter;
import com.example.receiptsbooks.room.bean.ReceiptAndProduct;
import com.example.receiptsbooks.room.viewmodel.ProductViewModel;
import com.example.receiptsbooks.room.viewmodel.ReceiptInfoViewModel;
import com.example.receiptsbooks.utils.DateConverterUtil;
import com.example.receiptsbooks.view.IListCategoryPagerCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ListCategoryPagerPresenter implements IListCategoryPagerPresenter {
    private ReceiptInfoViewModel mReceiptInfoViewModel;
    private ProductViewModel mProductViewModel;

    @Override
    public void getContentByCategory(Fragment fragment, LifecycleOwner owner, String category,Date beginDate,Date endDate) {
        //将对应的页面进行操作
        for (IListCategoryPagerCallback callback : mCallbacks) {
            if (callback.getCurrentCategory().equals(category)){
                callback.onLoading();
            }
        }
        mReceiptInfoViewModel = ViewModelProviders.of(fragment).get(ReceiptInfoViewModel.class);
        mProductViewModel = ViewModelProviders.of(fragment).get(ProductViewModel.class);
        if (beginDate == null && endDate == null){
            LiveData<List<ReceiptAndProduct>> receiptAndProductFromType = mProductViewModel.getReceiptAndProductByType(category);
            receiptAndProductFromType.observe(owner, new Observer<List<ReceiptAndProduct>>() {
                @Override
                public void onChanged(List<ReceiptAndProduct> receiptAndProducts) {
                    //通知UI层更新数据
                    for (IListCategoryPagerCallback callback : mCallbacks) {
                        if (callback.getCurrentCategory().equals(category)){
                            //数据为空
                            if (receiptAndProducts.size() == 0){
                                callback.onEmpty();
                            }else{
                                callback.onContentLoaded(receiptAndProducts);
                            }
                        }
                    }
                }
            });
        }else{
            //根据时间筛选
            LiveData<List<ReceiptAndProduct>> receiptAndProductFromDate = mProductViewModel.getReceiptAndProductByDate(DateConverterUtil.converterDate(beginDate),DateConverterUtil.converterDate(endDate));
            receiptAndProductFromDate.observe(owner, new Observer<List<ReceiptAndProduct>>() {
                @Override
                public void onChanged(List<ReceiptAndProduct> receiptAndProducts) {
                    //通知UI层更新数据
                    for (IListCategoryPagerCallback callback : mCallbacks) {
                        if (callback.getCurrentCategory().equals(category)){
                            //数据为空
                            if (receiptAndProducts.size() == 0){
                                callback.onEmpty();
                            }else{
                                callback.onContentLoaded(receiptAndProducts);
                            }
                        }
                    }
                }
            });
        }
    }

    @Override
    public void loadMore(String Category) {

    }

    @Override
    public void reload(String Category) {

    }

    private List<IListCategoryPagerCallback> mCallbacks = new ArrayList<>();

    @Override
    public void registerViewCallback(IListCategoryPagerCallback callback) {
        if (!mCallbacks.contains(callback)){
            mCallbacks.add(callback);
        }
    }

    @Override
    public void unregisterViewCallback(IListCategoryPagerCallback callback) {
        mCallbacks.remove(callback);
    }
}
