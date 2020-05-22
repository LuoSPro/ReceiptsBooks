package com.example.receiptsbooks.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;

import androidx.lifecycle.LifecycleOwner;

import com.example.receiptsbooks.R;
import com.example.receiptsbooks.base.BaseFragment;
import com.example.receiptsbooks.base.State;
import com.example.receiptsbooks.presenter.IListCategoryPagerPresenter;
import com.example.receiptsbooks.room.bean.ReceiptAndProduct;
import com.example.receiptsbooks.ui.activity.ReceiptDetailsActivity;
import com.example.receiptsbooks.ui.adapter.HistoryContentAdapter;
import com.example.receiptsbooks.ui.adapter.ListPagerContentAdapter;
import com.example.receiptsbooks.utils.Constants;
import com.example.receiptsbooks.utils.LogUtils;
import com.example.receiptsbooks.utils.PresenterManager;
import com.example.receiptsbooks.view.IListCategoryPagerCallback;

import java.util.List;

import butterknife.BindView;

public class ListPagerFragment extends BaseFragment implements IListCategoryPagerCallback, LifecycleOwner, HistoryContentAdapter.OnListContentItemClickListener {

    private IListCategoryPagerPresenter mListCategoryPagerPresenter;
    private String mCategory;
    private ListPagerContentAdapter mContentAdapter;

    public static ListPagerFragment newInstance(String productTypeCategory){
        ListPagerFragment listPagerFragment = new ListPagerFragment();
        //传递数据
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY_LIST_PAGER_CATEGORY,productTypeCategory);
        listPagerFragment.setArguments(bundle);
        return listPagerFragment;
    }

    @Override
    protected void initPresenter() {
        mListCategoryPagerPresenter = PresenterManager.getInstance().getListCategoryPagerPresenter();
        mListCategoryPagerPresenter.registerViewCallback(this);
    }

    @Override
    protected void release() {
        if (mListCategoryPagerPresenter != null) {
            mListCategoryPagerPresenter.unregisterViewCallback(this);
        }
    }

    @Override
    protected void loadData() {
        Bundle arguments = getArguments();
        mCategory = arguments.getString(Constants.KEY_LIST_PAGER_CATEGORY);
        //加载数据
        LogUtils.d(this,"category ==> " + mCategory);
        mListCategoryPagerPresenter.getContentByCategory(this,this, mCategory,null,null);
    }

    @BindView(R.id.list_pager_content_list)
        public ExpandableListView mContentList;

    @Override
    protected int getRootViewResId() {
        return R.layout.fragment_list_pager;
    }

    @Override
    protected void initView(View rootView) {
        //创建适配器
        mContentAdapter = new ListPagerContentAdapter();
        //设置适配器
        mContentList.setAdapter(mContentAdapter);
    }

    @Override
    protected void initListener() {
        mContentAdapter.setOnClickViewDetailsListener(new ListPagerContentAdapter.OnClickViewDetailsListener() {
            @Override
            public void onViewDetails(ReceiptAndProduct receiptAndProduct) {
                LogUtils.d(ListPagerFragment.this,"view details is click======================");
                int receiptId = receiptAndProduct.getReceiptInfoBean().getId();
                //列表内容被点击
                Intent intent = new Intent(getContext(), ReceiptDetailsActivity.class);
                intent.putExtra(Constants.KEY_RECEIPT_DETAILS_RECEIPT_ID,receiptId);
                startActivity(intent);
            }
        });
        mContentList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return false;
            }
        });
        //子视图的点击事件
        mContentList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                return false;
            }
        });
        //用于当组项折叠时的通知。
        mContentList.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                //mContentList.setBackground(getContext().getResources().getDrawable(R.drawable.shape_list_bg_item_collapse,null));
            }
        });

        //用于当组项展开时的通知。
        mContentList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                //mContentList.setBackground(getContext().getResources().getDrawable(R.drawable.shape_list_bg_item_collapse,null));
            }
        });
    }

    @Override
    public void onContentLoaded(List<ReceiptAndProduct> contents) {
        setUpState(State.SUCCESS);
        //根据category获取的数据传送回来
        mContentAdapter.setGroupData(contents,mCategory);
    }

    @Override
    public String getCurrentCategory() {
        return mCategory;
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
        setUpState(State.LOADING);
    }

    @Override
    public void onEmpty() {
        setUpState(State.EMPTY);
    }

    @Override
    public void onItemClick(ReceiptAndProduct item) {
        int receiptId = item.getReceiptInfoBean().getId();
        //列表内容被点击
        Intent intent = new Intent(getContext(), ReceiptDetailsActivity.class);
        intent.putExtra(Constants.KEY_RECEIPT_DETAILS_RECEIPT_ID,receiptId);
        startActivity(intent);
    }

}
