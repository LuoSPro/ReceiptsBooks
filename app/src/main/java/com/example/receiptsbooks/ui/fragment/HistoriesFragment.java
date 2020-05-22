package com.example.receiptsbooks.ui.fragment;

import android.content.Intent;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.receiptsbooks.R;
import com.example.receiptsbooks.base.BaseFragment;
import com.example.receiptsbooks.base.State;
import com.example.receiptsbooks.presenter.impl.HistoriesPresenterImpl;
import com.example.receiptsbooks.room.bean.ReceiptAndProduct;
import com.example.receiptsbooks.ui.activity.IMainActivity;
import com.example.receiptsbooks.ui.activity.MainActivity;
import com.example.receiptsbooks.ui.activity.ReceiptDetailsActivity;
import com.example.receiptsbooks.ui.adapter.HistoryContentAdapter;
import com.example.receiptsbooks.utils.Constants;
import com.example.receiptsbooks.utils.PresenterManager;
import com.example.receiptsbooks.utils.SizeUtils;
import com.example.receiptsbooks.view.IHistoriesCallback;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class HistoriesFragment extends BaseFragment implements IHistoriesCallback, HistoryContentAdapter.OnListContentItemClickListener {

    @BindView(R.id.histories_list)
    public RecyclerView mContentList;
    private HistoriesPresenterImpl mHistoriesPresenter;
    private HistoryContentAdapter mContentAdapter;

    @Override
    protected int getRootViewResId() {
        return R.layout.fragment_histories;
    }

    @Override
    protected View loadRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.base_histories_fragment_layout,container,false);
    }

    @Override
    protected void initPresenter() {
        mHistoriesPresenter = PresenterManager.getInstance().getHistoriesPresenter();
        mHistoriesPresenter.registerViewCallback(this);
    }

    @Override
    protected void release() {
        mHistoriesPresenter.unregisterViewCallback(this);
    }

    @Override
    protected void initListener() {
        mContentAdapter.setOnListContentItemClickListener(this);
    }

    @OnClick(R.id.histories_back)
    public void historiesBack(){
        FragmentActivity activity = getActivity();
        if (activity instanceof IMainActivity) {
            ((MainActivity)activity).switch2Home();
        }
    }

    @Override
    protected void initView(View rootView) {
        //设置布局管理器
        mContentList.setLayoutManager(new LinearLayoutManager(getContext()));
        //设置间距
        mContentList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = SizeUtils.dip2px(getContext(),1.5f);
            }
        });
        //创建适配器
        mContentAdapter = new HistoryContentAdapter();
        //设置适配器
        mContentList.setAdapter(mContentAdapter);
    }

    @Override
    public void onAllReceiptHistoriesLoaded(List<ReceiptAndProduct> receiptAndProducts) {
        setUpState(State.SUCCESS);
        //这里直接使用List界面的适配器，所以分类就设置为""，这时只要去适配器里面加一个空字符判断即可
        mContentAdapter.setData(receiptAndProducts,"");
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
