package com.example.receiptsbooks.presenter.impl;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.receiptsbooks.presenter.IChartAnalysisPresenter;
import com.example.receiptsbooks.room.bean.ProductBean;
import com.example.receiptsbooks.room.bean.ReceiptAndProduct;
import com.example.receiptsbooks.room.viewmodel.ProductViewModel;
import com.example.receiptsbooks.utils.DateUtils;
import com.example.receiptsbooks.view.IChartAnalysisCallback;

import java.util.ArrayList;
import java.util.List;

public class ChartAnalysisPresenterImpl implements IChartAnalysisPresenter {
    private IChartAnalysisCallback mCallBack;
    private ProductViewModel mProductViewModel;
    private LiveData<List<ReceiptAndProduct>> mSelectedData;

    @Override
    public void getProductInfoFromDb(Fragment fragment, LifecycleOwner owner,int selectedDate) {
        if (mCallBack != null) {
            mCallBack.onLoading();
        }
        if (mProductViewModel == null) {
            mProductViewModel = ViewModelProviders.of(fragment).get(ProductViewModel.class);
        }
        if (selectedDate == 0){
            //今天
            mSelectedData = mProductViewModel.getReceiptAndProductByDate(System.currentTimeMillis(), System.currentTimeMillis());
        }else if (selectedDate == 1){
            //本周
            mSelectedData = mProductViewModel.getReceiptAndProductByDate(DateUtils.getTimesWeekMorning().getTime(), DateUtils.getTimesWeekNight().getTime());
        }else if (selectedDate == 2){
            //本月
            mSelectedData = mProductViewModel.getReceiptAndProductByDate(DateUtils.getTimesMonthMorning().getTime(), DateUtils.getTimesMonthnight().getTime());
        }else if (selectedDate == 3){
            //本季
            mSelectedData = mProductViewModel.getReceiptAndProductByDate(DateUtils.getCurrentQuarterStartTime().getTime(),DateUtils.getCurrentQuarterEndTime().getTime());
        }else {
            //本年
            mSelectedData = mProductViewModel.getReceiptAndProductByDate(DateUtils.getCurrentYearStartTime().getTime(), DateUtils.getCurrentYearEndTime().getTime());
        }
        mSelectedData.observe(owner, new Observer<List<ReceiptAndProduct>>() {
            @Override
            public void onChanged(List<ReceiptAndProduct> receiptAndProducts) {
                List<ProductBean> mProductBeans = new ArrayList<>();
                for (int i = 0; i < receiptAndProducts.size(); i++) {
                    mProductBeans.addAll(receiptAndProducts.get(i).getProductBean());
                }
            }
        });
    }

    @Override
    public void registerViewCallback(IChartAnalysisCallback callback) {
        this.mCallBack = callback;
    }

    @Override
    public void unregisterViewCallback(IChartAnalysisCallback callback) {
        mCallBack = null;
    }
}
