package com.example.receiptsbooks.presenter.impl;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;

import com.example.receiptsbooks.model.domain.BudgetInfo;
import com.example.receiptsbooks.model.domain.ProductType;
import com.example.receiptsbooks.presenter.IBudgetCenterPresenter;
import com.example.receiptsbooks.room.bean.BudgetBean;
import com.example.receiptsbooks.room.bean.BudgetDateBean;
import com.example.receiptsbooks.room.bean.ProductBean;
import com.example.receiptsbooks.room.bean.ReceiptAndProduct;
import com.example.receiptsbooks.room.viewmodel.BudgetDateViewModel;
import com.example.receiptsbooks.room.viewmodel.BudgetViewModel;
import com.example.receiptsbooks.room.viewmodel.ProductViewModel;
import com.example.receiptsbooks.utils.DateUtils;
import com.example.receiptsbooks.utils.ProductTypeUtils;
import com.example.receiptsbooks.view.IBudgetCenterCallback;

import java.util.ArrayList;
import java.util.List;

public class BudgetCenterPresenterImpl implements IBudgetCenterPresenter {
    private IBudgetCenterCallback mCallback = null;
    private BudgetDateViewModel mBudgetDateViewModel;
    private BudgetViewModel mBudgetViewModel;
    private ProductViewModel mProductViewModel;
    private LiveData<List<ReceiptAndProduct>> mSelectedExpendData;
    private double mTotalExpend = 0;
    private int mCurrentSelectedDate;

    @Override
    public void getAllBudgetInfoFromDB(Fragment fragment, LifecycleOwner owner,int selectedDate) {
        mCurrentSelectedDate = selectedDate;
        if (mCallback != null){
            mCallback.onLoading();
        }
        //查看数据库是否有数据
        if (mBudgetDateViewModel == null) {
            mBudgetDateViewModel = ViewModelProviders.of(fragment).get(BudgetDateViewModel.class);
        }
        if (mBudgetViewModel == null) {
            mBudgetViewModel = ViewModelProviders.of(fragment).get(BudgetViewModel.class);
        }
        if (mProductViewModel == null) {
            mProductViewModel = ViewModelProviders.of(fragment).get(ProductViewModel.class);
        }
        //如何才能很好的通知到数据库有没有数据呢
        LiveData<Integer> budgetDateSize = mBudgetDateViewModel.queryBudgetDateSize();
        budgetDateSize.observe(owner, integer -> {
            if(integer == 0){
                //如果数据库没有数据，则加入数据进去
                for (int i = 0; i < 5; i++) {
                    mBudgetDateViewModel.insertDateBean(new BudgetDateBean());
                    insertBudgetToDB(i+1);
                }
            }
            if (integer >= 5){
                getBudgetDateFromDB(owner);
            }
        });


    }

    private void getBudgetDateFromDB(LifecycleOwner owner) {
        //获得当前要选择的时间段的预算在数据库中的数据
        LiveData<BudgetDateBean> budgetDateBeans = mBudgetDateViewModel.queryBudgetDateById(mCurrentSelectedDate);
        budgetDateBeans.observe(owner, budgetDateBean -> mCallback.onBudgetDataLoaded(budgetDateBean));
        //获得对应时间下的预算列表
        LiveData<List<BudgetBean>> selectedBudgetList = mBudgetViewModel.queryBudgetInfoByDateId(mCurrentSelectedDate);
        selectedBudgetList.observe(owner, budgetBeans -> {
            //TODO:这里每次都会刷新两次，并且其中一次的budgetBeans的dateId和mCurrentSelectedDate不一样，所以这里才加了这个判断，希望后面能来搞清楚为什么会这样
            if (budgetBeans.get(0).getDateId() == mCurrentSelectedDate){
                //根据selectedDate去查询这个时间段的账单数据
                //首先判断这是selectedDate是哪个时间段
                if (mCurrentSelectedDate == 1){
                    //今天
                    mSelectedExpendData = mProductViewModel.getReceiptAndProductByDate(DateUtils.getTodayStartTime(), DateUtils.getTodayEndTime());
                }else if (mCurrentSelectedDate == 2){
                    //本周
                    mSelectedExpendData = mProductViewModel.getReceiptAndProductByDate(DateUtils.getTimesWeekMorning().getTime(), DateUtils.getTimesWeekNight().getTime());
                }else if (mCurrentSelectedDate == 3){
                    //本月
                    mSelectedExpendData = mProductViewModel.getReceiptAndProductByDate(DateUtils.getTimesMonthMorning().getTime(), DateUtils.getTimesMonthnight().getTime());
                }else if (mCurrentSelectedDate == 4){
                    //本季
                    mSelectedExpendData = mProductViewModel.getReceiptAndProductByDate(DateUtils.getCurrentQuarterStartTime().getTime(),DateUtils.getCurrentQuarterEndTime().getTime());
                }else {
                    //本年
                    mSelectedExpendData = mProductViewModel.getReceiptAndProductByDate(DateUtils.getCurrentYearStartTime().getTime(), DateUtils.getCurrentYearEndTime().getTime());
                }
                mSelectedExpendData.observe(owner, receiptAndProducts -> {
                    //数据到了,组装数据
                    List<BudgetInfo> budgetInfos = changeBudgetBean(budgetBeans, receiptAndProducts);
                    mCallback.onCurBudgetInfoLoaded(budgetInfos);
                    mCallback.onTotalExpendLoaded(mTotalExpend);
                });
            }

        });
    }

    @Override
    public void updateBudgetItem(BudgetInfo budgetInfo) {
        BudgetBean budgetBean = new BudgetBean();
        budgetBean.setId(budgetInfo.getId());
        budgetBean.setDateId(budgetInfo.getDateId());
        budgetBean.setBudgetMoney(budgetInfo.getBudgetMoney());
        budgetBean.setBudgetTitle(budgetInfo.getBudgetTitle());
        mBudgetViewModel.updateProduct(budgetBean);
    }

    @Override
    public void updateTotalBudget(BudgetDateBean budgetDateBean) {
        mBudgetDateViewModel.updateDateBean(budgetDateBean);
    }

    private void insertBudgetToDB(int dateId) {
        List<ProductType> budgetList = ProductTypeUtils.getInstance().getProductTypes();
        for (int i = 0; i < budgetList.size(); i++) {
            BudgetBean budgetBean = new BudgetBean();
            budgetBean.setBudgetTitle(budgetList.get(i).getTitle());
            budgetBean.setBudgetMoney(0.0);
            budgetBean.setDateId(dateId);
            mBudgetViewModel.insertProduct(budgetBean);
        }
    }

    private List <BudgetInfo> changeBudgetBean(List<BudgetBean> budgetBeans, List<ReceiptAndProduct> receiptAndProducts){
        List<BudgetInfo> budgetInfos = new ArrayList<>();
        List<ProductType> productTypes = ProductTypeUtils.getInstance().getProductTypes();
        //每次把数据归0，否则数据会保留之前的
        mTotalExpend = 0;
        for (int i = 0; i < budgetBeans.size(); i++) {
            //不能全部都使用一个new的对象，否则后面的数据都是操作在一个堆内存上面的
            BudgetInfo budgetInfo = new BudgetInfo();
            BudgetBean budgetBean = budgetBeans.get(i);
            //为了之后的数据库更新，必须把id留下来
            budgetInfo.setId(budgetBean.getId());
            budgetInfo.setDateId(budgetBean.getDateId());
            //每个选项
            //拼凑其他部分
            budgetInfo.setBudgetMoney(budgetBean.getBudgetMoney());
            budgetInfo.setBudgetTitle(budgetBean.getBudgetTitle());
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
            budgetInfo.setSetting(budgetInfo.getBudgetMoney()!=0);
            budgetInfos.add(budgetInfo);
        }
        return budgetInfos;
    }

    @Override
    public void registerViewCallback(IBudgetCenterCallback callback) {
        this.mCallback = callback;
    }

    @Override
    public void unregisterViewCallback(IBudgetCenterCallback callback) {
        this.mCallback = null;
    }
}
