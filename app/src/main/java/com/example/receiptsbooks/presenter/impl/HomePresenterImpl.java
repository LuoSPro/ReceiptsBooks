package com.example.receiptsbooks.presenter.impl;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;

import com.example.receiptsbooks.presenter.IHomePresenter;
import com.example.receiptsbooks.room.bean.BudgetDateBean;
import com.example.receiptsbooks.room.bean.ReceiptAndProduct;
import com.example.receiptsbooks.room.viewmodel.BudgetDateViewModel;
import com.example.receiptsbooks.room.viewmodel.ProductViewModel;
import com.example.receiptsbooks.utils.DateUtils;
import com.example.receiptsbooks.view.IHomeCallback;

import java.util.List;

public class HomePresenterImpl implements IHomePresenter {
    private IHomeCallback mCallback = null;
    private ProductViewModel mProductViewModel;
    private BudgetDateViewModel mBudgetDateViewModel;

    @Override
    public void getCurMonthTotalExpend(Fragment fragment, LifecycleOwner owner) {
        if (mProductViewModel == null) {
            mProductViewModel = ViewModelProviders.of(fragment).get(ProductViewModel.class);
        }
        if (mBudgetDateViewModel == null) {
            mBudgetDateViewModel = ViewModelProviders.of(fragment).get(BudgetDateViewModel.class);
        }
        LiveData<List<ReceiptAndProduct>> receiptAndProductLiveDate = mProductViewModel.getReceiptAndProductByDate(DateUtils.getTimesMonthMorning().getTime(), DateUtils.getTimesMonthnight().getTime());
        //LogUtils.d(HomePresenterImpl.this,"begin time ==========> " + DateUtils.getTimesMonthMorning().getTime());
        //LogUtils.d(HomePresenterImpl.this,"begin time ==========> " + DateUtils.getTimesMonthnight().getTime());
        receiptAndProductLiveDate.observe(owner, receiptAndProducts -> {
            double totalExpend = 0;
            for (int i = 0; i < receiptAndProducts.size(); i++) {
                //LogUtils.d(HomePresenterImpl.this,"selectedDate ======> " + receiptAndProducts.get(i).getReceiptInfoBean().getReceiptDate().getTime());
                //LogUtils.d(HomePresenterImpl.this,"selectedDate ======> " + receiptAndProducts.get(i).getReceiptInfoBean().getReceiptDate().getTime());
                totalExpend += receiptAndProducts.get(i).getReceiptInfoBean().getTotalPrice();
            }
            mCallback.onTotalExpendLoaded(totalExpend);
            LiveData<BudgetDateBean> budgetDateBeanLiveData = mBudgetDateViewModel.queryBudgetDateById(3);
            budgetDateBeanLiveData.observe(owner, budgetDateBean -> {
                if (budgetDateBean != null) {
                    mCallback.onTotalBudgetLoaded(budgetDateBean.getTotalBudget());
                }
            });
        });

    }

    @Override
    public void getTodayReceiptInfos(Fragment fragment, LifecycleOwner owner) {
        if (mCallback != null) {
            mCallback.onLoading();
        }
        if (mProductViewModel == null) {
            mProductViewModel = ViewModelProviders.of(fragment).get(ProductViewModel.class);
        }
        LiveData<List<ReceiptAndProduct>> receiptAndProductLiveDate = mProductViewModel.getReceiptAndProductByDate(DateUtils.getTodayStartTime(), DateUtils.getTodayEndTime());
        //LogUtils.d(HomePresenterImpl.this,"today begin time ==========> " + DateUtils.getTodayStartTime());
        //LogUtils.d(HomePresenterImpl.this,"today begin time ==========> " + DateUtils.getTodayEndTime());
        receiptAndProductLiveDate.observe(owner, receiptAndProducts -> {
            double todayExpend = 0;
            for (int i = 0; i < receiptAndProducts.size(); i++) {
                todayExpend += receiptAndProducts.get(i).getReceiptInfoBean().getTotalPrice();
            }
            if (receiptAndProducts.size() == 0){
                mCallback.onEmpty();
            }else{
                mCallback.onReceiptInfosLoaded(receiptAndProducts,todayExpend);
            }
        });
    }

    /**
     * 注册UI通知接口
     */
    @Override
    public void registerViewCallback(IHomeCallback callback) {
        //设置UI的引用
        this.mCallback = callback;
    }

    /**
     * 取消UI通知的接口
     */
    @Override
    public void unregisterViewCallback(IHomeCallback callback) {
        //取消UI的引用，避免引起内存泄漏
        this.mCallback = null;
    }
}
