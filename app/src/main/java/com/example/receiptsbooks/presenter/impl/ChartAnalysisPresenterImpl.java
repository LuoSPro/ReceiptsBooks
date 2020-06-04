package com.example.receiptsbooks.presenter.impl;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;

import com.example.receiptsbooks.model.domain.BudgetInfo;
import com.example.receiptsbooks.model.domain.ProductType;
import com.example.receiptsbooks.presenter.IChartAnalysisPresenter;
import com.example.receiptsbooks.room.bean.ProductBean;
import com.example.receiptsbooks.room.bean.ReceiptAndProduct;
import com.example.receiptsbooks.room.viewmodel.ProductViewModel;
import com.example.receiptsbooks.utils.DateUtils;
import com.example.receiptsbooks.utils.ProductTypeUtils;
import com.example.receiptsbooks.view.IChartAnalysisCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChartAnalysisPresenterImpl implements IChartAnalysisPresenter {
    private IChartAnalysisCallback mCallBack;
    private ProductViewModel mProductViewModel;
    private double mTotalExpend;

    @Override
    public void getProductInfoFromDb(Fragment fragment, LifecycleOwner owner,int selectedDate) {
        if (mCallBack != null) {
            mCallBack.onLoading();
        }
        if (mProductViewModel == null) {
            mProductViewModel = ViewModelProviders.of(fragment).get(ProductViewModel.class);
        }
        LiveData<List<ReceiptAndProduct>> selectedDataLiveData;
        if (selectedDate == 1){
            //今天
            selectedDataLiveData = mProductViewModel.getReceiptAndProductByDate(DateUtils.getTodayStartTime(), DateUtils.getTodayEndTime());
        }else if (selectedDate == 2){
            //本周
            selectedDataLiveData = mProductViewModel.getReceiptAndProductByDate(DateUtils.getTimesWeekMorning().getTime(), DateUtils.getTimesWeekNight().getTime());
        }else if (selectedDate == 3){
            //本月
            selectedDataLiveData = mProductViewModel.getReceiptAndProductByDate(DateUtils.getTimesMonthMorning().getTime(), DateUtils.getTimesMonthnight().getTime());
        }else if (selectedDate == 4){
            //本季
            selectedDataLiveData = mProductViewModel.getReceiptAndProductByDate(DateUtils.getCurrentQuarterStartTime().getTime(),DateUtils.getCurrentQuarterEndTime().getTime());
        }else {
            //本年
            selectedDataLiveData = mProductViewModel.getReceiptAndProductByDate(DateUtils.getCurrentYearStartTime().getTime(), DateUtils.getCurrentYearEndTime().getTime());
        }
        selectedDataLiveData.observe(owner, receiptAndProducts -> {
            if (mCallBack != null) {
                List<BudgetInfo> budgetInfos = changeBudgetBean(receiptAndProducts);
                mCallBack.onProductInfoLoaded(budgetInfos,mTotalExpend,receiptAndProducts);
            }
        });
    }

    private List<BudgetInfo> changeBudgetBean(List<ReceiptAndProduct> receiptAndProducts){
        List<BudgetInfo> budgetInfos = new ArrayList<>();
        List<ProductType> productTypes = ProductTypeUtils.getInstance().getProductTypes();
        //每次把数据归0，否则数据会保留之前的
        mTotalExpend = 0;
        for (int i = 0; i < productTypes.size(); i++) {
            //不能全部都使用一个new的对象，否则后面的数据都是操作在一个堆内存上面的
            BudgetInfo budgetInfo = new BudgetInfo();
            //每个选项
            //拼凑其他部分
            budgetInfo.setBudgetTitle(productTypes.get(i).getTitle());
            budgetInfo.setBudgetIcon(productTypes.get(i).getIcon());
            //处理支付金额数据
            for (int j = 0; j < receiptAndProducts.size(); j++) {
                //每张小票
                List<ProductBean> productBeans = receiptAndProducts.get(j).getProductBean();
                for (int k = 0; k < productBeans.size(); k++) {
                    ProductBean productBean = productBeans.get(k);
                    if (productBean.getType().equals(budgetInfo.getBudgetTitle())) {
                        //把商品的钱都加在一起
                        budgetInfo.setBudgetBalance(budgetInfo.getBudgetBalance() + productBean.getPrice());
                        mTotalExpend += productBean.getPrice();
                    }
                }
            }
            if (budgetInfo.getBudgetBalance() != 0){
                //只有支出不为0的类型才有显示
                budgetInfos.add(budgetInfo);
            }
        }
        //排序
        Collections.sort(budgetInfos);
        return budgetInfos;
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
