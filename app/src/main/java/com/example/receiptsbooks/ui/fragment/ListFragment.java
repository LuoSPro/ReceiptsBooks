package com.example.receiptsbooks.ui.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.LifecycleOwner;
import androidx.viewpager.widget.ViewPager;

import com.example.receiptsbooks.R;
import com.example.receiptsbooks.base.BaseFragment;
import com.example.receiptsbooks.base.State;
import com.example.receiptsbooks.presenter.IListCategoryPagerPresenter;
import com.example.receiptsbooks.presenter.IListPresenter;
import com.example.receiptsbooks.room.bean.ReceiptAndProduct;
import com.example.receiptsbooks.ui.adapter.ListPagerAdapter;
import com.example.receiptsbooks.ui.custom.TextFlowLayout;
import com.example.receiptsbooks.ui.custom.datepicker.CustomDatePicker;
import com.example.receiptsbooks.ui.custom.datepicker.DateFormatUtils;
import com.example.receiptsbooks.utils.DateUtils;
import com.example.receiptsbooks.utils.PresenterManager;
import com.example.receiptsbooks.utils.ToastUtil;
import com.example.receiptsbooks.view.IListCallback;
import com.example.receiptsbooks.view.IListCategoryPagerCallback;
import com.google.android.material.tabs.TabLayout;

import java.util.Date;
import java.util.List;

import butterknife.BindView;

public class ListFragment extends BaseFragment implements IListCallback, IListCategoryPagerCallback, LifecycleOwner {

    private IListPresenter mListPresenter = null;
    private IListCategoryPagerPresenter mListCategoryPagerPresenter = null;
    @BindView(R.id.list_btn_tab)
    public TabLayout mTabLayout;

    @BindView(R.id.list_drawer)
    public DrawerLayout mDrawerLayout;

    @BindView(R.id.list_btn_filter)
    public ImageView mFilterBtn;

    @BindView(R.id.list_product_type)
    public TextFlowLayout mProductType;

    @BindView(R.id.store_pager_title)
    public TextView mFilterTitle;

    @BindView(R.id.list_tv_filter_begin_time)
    public TextView mBeginTimeTv;

    @BindView(R.id.list_tv_filter_end_time)
    public TextView mEndTimeTv;

    @BindView(R.id.list_filter_btn_reset)
    public TextView mResetBtn;

    @BindView(R.id.list_filter_btn_confirm)
    public TextView mConfirmBtn;

    @BindView(R.id.list_pager)
    public ViewPager listPager;
    private ListPagerAdapter mListPagerAdapter;
    private List<String> mCategories;
    private CustomDatePicker mDatePickerBegin;
    private CustomDatePicker mDatePickerEnd;
    private long mBeginTimestamp;

    @Override
    protected int getRootViewResId() {
        return R.layout.fragment_list;
    }

    @Override
    protected void initView(View rootView) {
        //初始化View
        mTabLayout.setupWithViewPager(listPager);
        //给ViewPager设置适配器
        mListPagerAdapter = new ListPagerAdapter(getChildFragmentManager());
        listPager.setAdapter(mListPagerAdapter);
        //设置筛选栏标题
        mFilterTitle.setText("筛选");
        //设置drawerLayout禁用侧边滑动打开或关闭
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        //TODO:设置起始时间————最好用一个SP去保存最早的时间，然后每次都拿到这里来
        mBeginTimestamp = DateFormatUtils.str2Long("2018-01-01", false);
    }

    @Override
    protected View loadRootView(LayoutInflater inflater, ViewGroup container) {
        //由于采用了“填坑”的方式，所以这里要换成自己的父布局
        return inflater.inflate(R.layout.base_list_fragment_layout,container,false);
    }

    @Override
    protected void initPresenter() {
        //创建Presenter
        mListPresenter = PresenterManager.getInstance().getListPresenter();
        mListPresenter.registerViewCallback(this);
        mListCategoryPagerPresenter = PresenterManager.getInstance().getListCategoryPagerPresenter();
        mListCategoryPagerPresenter.registerViewCallback(this);
    }

    @Override
    protected void release() {
        //取消注册
        if (mListPresenter != null) {
            mListPresenter.unregisterViewCallback(this);
        }
        if (mListCategoryPagerPresenter != null) {
            mListCategoryPagerPresenter.unregisterViewCallback(this);
        }
    }

    //初始化日期选择器
    private void initDatePicker(CustomDatePicker datePicker,int titleId) {
        // 不允许点击屏幕或物理返回键关闭
        datePicker.setCancelable(true);
        // 不显示时和分
        datePicker.setCanShowPreciseTime(false);
        // 不允许循环滚动
        datePicker.setScrollLoop(false);
        // 不允许滚动动画
        datePicker.setCanShowAnim(false);
        //设置标题
        datePicker.setDatePickerTitle(titleId);
        //显示Dialog
        datePicker.show(mBeginTimeTv.getText().toString());
    }

    @Override
    protected void initListener() {
        mBeginTimeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //初始化日期选择器
                // 通过时间戳初始化日期，毫秒级别
                mDatePickerBegin = new CustomDatePicker(getContext(), new CustomDatePicker.Callback() {
                    @Override
                    public void onTimeSelected(long timestamp) {
                        mBeginTimeTv.setText(DateFormatUtils.long2Str(timestamp, false));
                    }
                }, mBeginTimestamp, System.currentTimeMillis());
                //设置属性
                initDatePicker(mDatePickerBegin,R.string.text_list_filter_begin_date_hint);
            }
        });
        mEndTimeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //初始化日期选择器
                mDatePickerEnd = new CustomDatePicker(getContext(), new CustomDatePicker.Callback() {
                    @Override
                    public void onTimeSelected(long timestamp) {
                        mEndTimeTv.setText(DateFormatUtils.long2Str(timestamp, false));
                    }
                }, mBeginTimestamp, System.currentTimeMillis());
                //设置属性
                initDatePicker(mDatePickerEnd,R.string.text_list_filter_end_date_hint);
            }
        });
        mProductType.setOnFlowTextItemClickListener(new TextFlowLayout.OnFlowTextItemClickListener() {
            @Override
            public void onFlowItemClick(String text) {
                //将TabLayout跳转到选中的那一页
                mTabLayout.getTabAt(mCategories.indexOf(text)).select();
            }
        });
        //筛选按钮
        mFilterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProductType.setTextList(mCategories,false);
                mDrawerLayout.openDrawer(GravityCompat.END);
            }
        });
        //重置按钮
        mResetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBeginTimeTv.setText(R.string.text_list_filter_begin_date_hint);
                mEndTimeTv.setText(R.string.text_list_filter_end_date_hint);
                //重置时，返回没有筛选的信息
                mListCategoryPagerPresenter.getContentByCategory(ListFragment.this,ListFragment.this,mCategories.get(mTabLayout.getSelectedTabPosition()),null,null);
            }
        });
        //确认按钮
        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date beginDate = DateUtils.stringToData(mBeginTimeTv.getText().toString().trim());
                Date endDate = DateUtils.stringToData(mEndTimeTv.getText().toString().trim());
                if (beginDate == null){
                    ToastUtil.showToast("起始时间不能为空");
                    return;
                }
                if (endDate == null){
                    ToastUtil.showToast("截止时间不能为空");
                    return;
                }
                if (endDate.getTime() <= beginDate.getTime()){
                    ToastUtil.showToast("起始时间大于截止时间,请重新选择");
                    return;
                }
                //mListCategoryPagerPresenter
                mListCategoryPagerPresenter.getContentByCategory(ListFragment.this,ListFragment.this,mCategories.get(mTabLayout.getSelectedTabPosition()),beginDate,endDate);
            }
        });
    }

    @Override
    protected void loadData() {
        //加载数据
        mListPresenter.getCategories();
    }

    @Override
    public void onCategoriesLoaded(List<String> categories) {
        setUpState(State.SUCCESS);
        this.mCategories = categories;
        mListPagerAdapter.setCategories(categories);
    }

    @Override
    public void onContentLoaded(List<ReceiptAndProduct> contents) {

    }

    @Override
    public String getCurrentCategory() {
        return mCategories.get(mTabLayout.getSelectedTabPosition());
    }

    @Override
    public void onLoadMoreError() {

    }

    @Override
    public void onLoadMoreEmpty() {

    }

    @Override
    public void onLoadMoreLoaded(List<ReceiptAndProduct> contents) {

    }

    @Override
    public void onNetworkError() {

    }

    @Override
    public void onLoading() {

    }

    @Override
    public void onEmpty() {

    }
}
