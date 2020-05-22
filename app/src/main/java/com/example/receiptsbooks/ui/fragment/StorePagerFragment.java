package com.example.receiptsbooks.ui.fragment;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.receiptsbooks.R;
import com.example.receiptsbooks.base.BaseFragment;
import com.example.receiptsbooks.base.State;
import com.example.receiptsbooks.model.domain.Categories;
import com.example.receiptsbooks.model.domain.IBaseInfo;
import com.example.receiptsbooks.model.domain.StorePagerContent;
import com.example.receiptsbooks.presenter.IStoreCategoryPagerPresenter;
import com.example.receiptsbooks.ui.adapter.LinearItemContentAdapter;
import com.example.receiptsbooks.ui.adapter.StoreLooperPagerAdapter;
import com.example.receiptsbooks.ui.custom.AutoLoopViewPager;
import com.example.receiptsbooks.utils.Constants;
import com.example.receiptsbooks.utils.LogUtils;
import com.example.receiptsbooks.utils.PresenterManager;
import com.example.receiptsbooks.utils.SizeUtils;
import com.example.receiptsbooks.utils.TicketUtil;
import com.example.receiptsbooks.utils.ToastUtil;
import com.example.receiptsbooks.view.IStoreCategoryPagerCallback;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.views.TbNestedScrollView;

import java.util.List;

import butterknife.BindView;

public class StorePagerFragment extends BaseFragment implements IStoreCategoryPagerCallback, LinearItemContentAdapter.OnListenItemClickListener, StoreLooperPagerAdapter.onLooperPagerItemClickListener {

    private IStoreCategoryPagerPresenter mCategoryPagerPresenter = null;
    private int mMaterialId;
    private LinearItemContentAdapter mContentAdapter;
    private StoreLooperPagerAdapter mLooperPagerAdapter;

    public static StorePagerFragment newInstance(Categories.DataBean category) {
        StorePagerFragment homePagerFragment = new StorePagerFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY_STORE_PAGER_TITLE, category.getTitle());
        bundle.putInt(Constants.KEY_STORE_PAGER_ID, category.getId());
        homePagerFragment.setArguments(bundle);
        return homePagerFragment;
    }

    @Override
    protected int getRootViewResId() {
        return R.layout.fragment_store_pager;
    }

    @Override
    protected void initView(View rootView) {
        //设置布局管理器
        mContentList.setLayoutManager(new LinearLayoutManager(getContext()));
        mContentList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = SizeUtils.dip2px(getContext(),1.5f);
                outRect.bottom = SizeUtils.dip2px(getContext(),1.5f);
            }
        });
        //创建适配器
        mContentAdapter = new LinearItemContentAdapter();
        //设置设配器
        mContentList.setAdapter(mContentAdapter);
        //创建轮播图适配器
        mLooperPagerAdapter = new StoreLooperPagerAdapter();
        //设置适配器
        looperPager.setAdapter(mLooperPagerAdapter);
        //设置Refresh相关属性
        //不允许下拉刷新
        mTwinklingRefreshLayout.setEnableRefresh(false);
        //允许上拉加载更多
        mTwinklingRefreshLayout.setEnableLoadmore(true);
        //设置是否可以上拉超过，默认为true
        mTwinklingRefreshLayout.setEnableOverScroll(true);
    }

    @Override
    protected void initPresenter() {
        mCategoryPagerPresenter = PresenterManager.getInstance().getStoreCategoryPagerPresenter();
        mCategoryPagerPresenter.registerViewCallback(this);
    }

    @Override
    protected void initListener() {
        //给轮播图的item设置监听
        mLooperPagerAdapter.setOnLooperPagerItemClickListener(this);
        //给recyclerView内容item设置监听
        mContentAdapter.setOnListenItemClickListener(this);
        //由于RecyclerView是放在NestedScrollView中的，并且是match_parent，所以每次数据都是获取得一次完整请求得数据量
        //而不是一个屏幕对应得数据量，所以，这里处理之后，能把每次获取得数据量控制在一个屏幕范围内
        homePagerParent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (homePagerContainer == null) {
                    return;
                }
                int headerHeight = homePagerContainer.getMeasuredHeight();
                //LogUtils.d(HomePagerFragment.this,"headerHeight ==> " + headerHeight);
                //这里是在动态的设置NestedView的HeaderHeight高度，只有这样，才能使滑动RecyclerView的时候，先去
                //滑动NestedView(上面的轮播图部分(HeaderView)+recyclerView)，然后当滑动距离超过HeaderView的时候，
                //再去滑动RecyclerView，从而实现解决滑动冲突
                homePagerNestedView.setHeaderHeight(headerHeight);//动态设置，根据不同的手机设置不同的值
                int measuredHeight = homePagerParent.getMeasuredHeight();
                //LogUtils.d(HomePagerFragment.this,"measuredHeight ==> " +measuredHeight );
                ViewGroup.LayoutParams layoutParams = mContentList.getLayoutParams();
                layoutParams.height = measuredHeight;
                mContentList.setLayoutParams(layoutParams);
                if (measuredHeight != 0){
                    //这样就不会频繁去调用这个监听事件（移除）
                    homePagerParent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });
        looperPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (mLooperPagerAdapter.getDataSize() == 0){
                    return;
                }
                int targetPosition = position % mLooperPagerAdapter.getDataSize();
                //切换指示器
                updateLooperIndicator(targetPosition);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mTwinklingRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                LogUtils.d(StorePagerFragment.this,"触发了加载更多");
                mCategoryPagerPresenter.loaderMore(mMaterialId);
            }
        });
    }

    /**
     * 切换指示器
     * @param targetPosition
     */
    private void updateLooperIndicator(int targetPosition) {
        for (int i = 0; i < looperPointContainer.getChildCount(); i++) {
            View point = looperPointContainer.getChildAt(i);
            if (i == targetPosition) {
                point.setBackgroundResource(R.drawable.shape_indicator_point_selected);
            } else {
                point.setBackgroundResource(R.drawable.shape_indicator_point_normal);
            }
        }
    }

    @Override
    protected void loadData() {
        Bundle arguments = getArguments();
        String title = arguments.getString(Constants.KEY_STORE_PAGER_TITLE);
        mMaterialId = arguments.getInt(Constants.KEY_STORE_PAGER_ID);
        LogUtils.d(this, "title ==> " + title + " id ==> " + mMaterialId);
        //加载数据
        if (mCategoryPagerPresenter != null) {
            mCategoryPagerPresenter.getContentByCategoryId(mMaterialId);
        }
        if (currentCategoryTitleTv != null) {
            currentCategoryTitleTv.setText(title);
        }
    }

    @BindView(R.id.store_pager_content_list)
    public RecyclerView mContentList;

    @BindView(R.id.lopper_pager)
    public AutoLoopViewPager looperPager;

    @BindView(R.id.store_pager_title)
    public TextView currentCategoryTitleTv;

    @BindView(R.id.looper_point_container)
    public LinearLayout looperPointContainer;

    @BindView(R.id.store_pager_nested_scroller)
    public TbNestedScrollView homePagerNestedView;

    @BindView(R.id.store_pager_header_container)
    public LinearLayout homePagerContainer;

    @BindView(R.id.store_pager_refresh)
    public TwinklingRefreshLayout mTwinklingRefreshLayout;

    @BindView(R.id.home_pager_parent)
    public LinearLayout homePagerParent;

    @Override
    public void onResume() {
        super.onResume();
        //可见的时候我们去调用开始轮播
        looperPager.startLoop();
        LogUtils.d(this,"onResume...");
    }

    @Override
    public void onPause() {
        super.onPause();
        //不可见时暂停
        looperPager.stopLoop();
        LogUtils.d(this,"onPause...");
    }

    @Override
    public void onContentLoaded(List<StorePagerContent.DataBean> contents) {
        //数据列表加载到了
        mContentAdapter.setData(contents);
        setUpState(State.SUCCESS);
    }

    @Override
    public void onLoaderMoreError() {
        ToastUtil.showToast("网络异常，请稍后重试");
        if (mTwinklingRefreshLayout != null) {
            //注：这里不能使用finishRefreshing，否者之后再刷新的时候，刷新事件不再受监听，所以正确的是用finishLoadMore()
//            mTwinklingRefreshLayout.finishRefreshing();
            mTwinklingRefreshLayout.finishLoadmore();
        }
    }

    @Override
    public void onLoaderMoreEmpty() {
        ToastUtil.showToast("没有更多商品");
        if (mTwinklingRefreshLayout != null) {
            mTwinklingRefreshLayout.finishLoadmore();
        }
    }

    @Override
    public void onLoaderMoreLoaded(List<StorePagerContent.DataBean> contents) {
        //添加到适配器数据的底部
        mContentAdapter.addDate(contents);
        if (mTwinklingRefreshLayout != null) {
            mTwinklingRefreshLayout.finishLoadmore();
        }
        ToastUtil.showToast("加载了" + contents.size() + "条数据");
    }

    @Override
    public void onLooperListLoaded(List<StorePagerContent.DataBean> contents) {
        LogUtils.d(this, "looper size ==> " + contents.size());
        mLooperPagerAdapter.setData(contents);
        //中间点%数据的size不一定为0，所以显示的不是第一个
        //处理
        int dx = (Integer.MAX_VALUE / 2) % contents.size();
        //设置到中间点
        int targetCenterPosition = (Integer.MAX_VALUE / 2) - dx;
        if (looperPager == null){
            return;
        }
        looperPager.setCurrentItem(targetCenterPosition);
        LogUtils.d(this, "url ==> " + contents.get(0).getPict_url());
        if (looperPointContainer != null) {
            looperPointContainer.removeAllViews();
        }
        Context context = getContext();
        //添加轮播图得点
        for (int i = 0; i < contents.size(); i++) {
            View point = new View(context);
            int size = SizeUtils.dip2px(context, 8);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(size, size);
            layoutParams.leftMargin = SizeUtils.dip2px(context, 5);
            layoutParams.rightMargin = SizeUtils.dip2px(context, 5);
            point.setLayoutParams(layoutParams);
            if (i == 0) {
                point.setBackgroundResource(R.drawable.shape_indicator_point_selected);
            } else {
                point.setBackgroundResource(R.drawable.shape_indicator_point_normal);
            }
            looperPointContainer.addView(point);
        }
    }

    @Override
    public int getCategoryId() {
        return mMaterialId;
    }

    @Override
    public void onLoading() {
        setUpState(State.LOADING);
    }

    public void onNetworkError() {
        //网络错误
        setUpState(State.ERROR);
    }

    @Override
    public void onEmpty() {

    }

    @Override
    public void onItemClick(IBaseInfo item) {
        //列表内容被点击了
        LogUtils.d(this,"item click ==> " + item.getTitle());
        handleItemClick(item);
    }

    private void handleItemClick(IBaseInfo item) {
        TicketUtil.toTicketPage(getContext(),item);
    }

    @Override
    public void onLooperItemClick(StorePagerContent.DataBean item) {
        LogUtils.d(this,"looper item click ==> " + item.getTitle());
        handleItemClick(item);
    }
}
