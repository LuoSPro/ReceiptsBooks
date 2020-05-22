package com.example.receiptsbooks.ui.fragment;

import android.graphics.Rect;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.receiptsbooks.R;
import com.example.receiptsbooks.base.BaseFragment;
import com.example.receiptsbooks.base.State;
import com.example.receiptsbooks.model.domain.Histories;
import com.example.receiptsbooks.model.domain.IBaseInfo;
import com.example.receiptsbooks.model.domain.SearchRecommend;
import com.example.receiptsbooks.model.domain.SearchResult;
import com.example.receiptsbooks.presenter.ISearchPresenter;
import com.example.receiptsbooks.ui.activity.IMainActivity;
import com.example.receiptsbooks.ui.activity.MainActivity;
import com.example.receiptsbooks.ui.adapter.LinearItemContentAdapter;
import com.example.receiptsbooks.ui.custom.TextFlowLayout;
import com.example.receiptsbooks.utils.KeyboardUtil;
import com.example.receiptsbooks.utils.LogUtils;
import com.example.receiptsbooks.utils.PresenterManager;
import com.example.receiptsbooks.utils.SizeUtils;
import com.example.receiptsbooks.utils.TicketUtil;
import com.example.receiptsbooks.utils.ToastUtil;
import com.example.receiptsbooks.view.ISearchPageCallback;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class SearchFragment extends BaseFragment implements ISearchPageCallback, TextFlowLayout.OnFlowTextItemClickListener {

    @BindView(R.id.search_history_view)
    public TextFlowLayout mHistoryView;

    @BindView(R.id.search_recommend_view)
    public TextFlowLayout mRecommendView;

    @BindView(R.id.search_history_container)
    public View mHistoryContainer;

    @BindView(R.id.search_recommend_container)
    public View mRecommendContainer;

    @BindView(R.id.search_history_delete)
    public View mHistoryDelete;

    @BindView(R.id.search_result_list)
    public RecyclerView mSearchList;

    @BindView(R.id.search_result_container)
    public TwinklingRefreshLayout mRefreshContainer;

    @BindView(R.id.search_clear_btn)
    public ImageView mClearInputBtn;

    @BindView(R.id.search_input_box)
    public EditText mSearchInputBox;

    @BindView(R.id.search_btn)
    public TextView mSearchBtn;

    private ISearchPresenter mSearchPresenter;
    private LinearItemContentAdapter mSearchResultAdapter;


    @Override
    protected void initPresenter() {
        super.initPresenter();
        //通过Presenter去管理Presenter(使用单例，保证全局使用一个此类型的Presenter)
        mSearchPresenter = PresenterManager.getInstance().getSearchPresenter();
        //然后马上注册，同时，把注销也做了
        mSearchPresenter.registerViewCallback(this);
        //获取搜索推荐词,调用这个方法之后，会从onRecommendWordsLoaded这个回调中回来
        mSearchPresenter.getRecommendWords();
        //调用查询方法，调用之后，结果会从onSearchSuccess回来(测试用的假数据)
        //mSearchPresenter.doSearch("毛线");
        //获取历史记录，数据会从onHistoriesLoaded回来
        mSearchPresenter.getHistory();
        //设置刷新控件
        mRefreshContainer.setEnableLoadmore(true);
        mRefreshContainer.setEnableRefresh(false);
        mRefreshContainer.setEnableOverScroll(true);
    }

    @Override
    protected void onRetryClick() {
        //重新加载内容
        if (mSearchPresenter != null) {
            mSearchPresenter.research();
        }
    }

    @Override
    protected void initListener() {
        //关键词和历史记录的点击事件（这里不用匿名内部类的原因是因为，我们可以用同一段代码处理推荐和历史记录的点击）
        mHistoryView.setOnFlowTextItemClickListener(this);
        mRecommendView.setOnFlowTextItemClickListener(this);
        //发起搜索
        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果有内容搜索
                if (hasInput(false)){
                    //发起搜索
                    if (mSearchPresenter != null) {
                        //mSearchPresenter.doSearch(mSearchInputBox.getText().toString().trim());
                        toSearch(mSearchInputBox.getText().toString().trim());
                        KeyboardUtil.hide(getContext(),v);
                    }
                }else{
                    //隐藏键盘
                    KeyboardUtil.hide(getContext(),v);
                }
                //如果输入库没有内容，则取消
                if ("".equals(mSearchInputBox.getText().toString())){
                    //回到领卷界面
                    FragmentActivity activity = getActivity();
                    if (activity instanceof IMainActivity) {
                        ((MainActivity)activity).switch2Store();
                    }
                }
            }
        });
        //清楚输入框的内容
        mClearInputBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchInputBox.setText("");
                //回到历史记录界面
                switch2HistoryPage();
            }
        });
        //监听输入框的内容变化
        mSearchInputBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //变化时候的通知
                LogUtils.d(SearchFragment.this,"input text ==> " + s);
                //如果长度不为，那么显示删除按钮
                //否则隐藏删除按钮
                mClearInputBtn.setVisibility(hasInput(true) ? View.VISIBLE : View.GONE);
                mSearchBtn.setText(hasInput(false) ? "搜索" : "取消");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mSearchInputBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //LogUtils.d(SearchFragment.this,"actionId ==> " + actionId);
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    String keyword = v.getText().toString().trim();
                    if (TextUtils.isEmpty(keyword)) {//搜索为空，直接返回
                        return false;
                    }
                    //判断拿到的内容是否为空
                    LogUtils.d(SearchFragment.this,"input text ==> " + v.getText().toString());
                    //发起搜索
                    toSearch(keyword);
                    //mSearchPresenter.doSearch(keyword);
                }
                return false;
            }
        });
        mHistoryDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //删除历史记录
                mSearchPresenter.delHistory();
            }
        });

        mRefreshContainer.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                //去加载更多内容
                if (mSearchPresenter != null) {
                    mSearchPresenter.loaderMore();
                }
            }
        });

        mSearchResultAdapter.setOnListenItemClickListener(new LinearItemContentAdapter.OnListenItemClickListener() {
            @Override
            public void onItemClick(IBaseInfo item) {
                //搜索列表内容被点击了
                TicketUtil.toTicketPage(getContext(),item);
            }
        });
    }

    /**
     * 切换到历史和推荐界面
     */
    private void switch2HistoryPage() {
        //先将ViewGroup上的item全部删除，再去通过历史数据去拿，这样才不会造成item重复
        // (这个逻辑最好放到View里面去，不然其他地方调用也会出问题)
        //mHistoryView.removeAllViews();
        //每次切换前都先要获取一次刚才搜索的历史
        if (mSearchPresenter != null) {
            mSearchPresenter.getHistory();
        }
        mHistoryContainer.setVisibility(mHistoryView.getContentSize() != 0 ? View.VISIBLE : View.GONE);
        mRecommendContainer.setVisibility(mRecommendView.getContentSize() != 0 ? View.VISIBLE : View.GONE);
        //内容要隐藏
        mRefreshContainer.setVisibility(View.GONE);
    }

    /**
     * 用来判断是否输入了内容
     * @param containerSpace 表示需要监听空格输入
     * @return 是否有内容输入
     */
    private boolean hasInput(boolean containerSpace){
        if (containerSpace){
            return mSearchInputBox.getText().toString().length() > 0;
        }else{
            return mSearchInputBox.getText().toString().trim().length() > 0;
        }
    }


    @Override
    protected void release() {
        mSearchPresenter.unregisterViewCallback(this);
    }

    @Override
    protected View loadRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_search_layout, container, false);
    }

    @Override
    protected int getRootViewResId() {
        return R.layout.fragment_search;
    }

    @Override
    protected void initView(View rootView) {
        //设置布局管理器
        mSearchList.setLayoutManager(new LinearLayoutManager(getContext()));
        //设置适配器
        //适配器设置成成员，这样才方便我们在别处设置数据
        mSearchResultAdapter = new LinearItemContentAdapter();
        mSearchList.setAdapter(mSearchResultAdapter);
        //把设置间距放到initView里面，这样只会调用一次
        mSearchList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = SizeUtils.dip2px(getContext(), 1.5f);
                outRect.bottom = SizeUtils.dip2px(getContext(), 1.5f);
            }
        });
    }

    @Override
    public void onHistoriesLoaded(Histories histories) {
        setUpState(State.SUCCESS);
        //因为histories内部是用List保存的，所以每次把提交的搜索词传过去之后，如果之前没有搜索过，就新增，否则，就加入替换
        LogUtils.d(this, "histories ==> " + histories);
        if (histories == null || histories.getHistories().size() == 0) {
            mHistoryContainer.setVisibility(View.GONE);
        } else {
            mHistoryContainer.setVisibility(View.VISIBLE);
            mHistoryView.setTextList(histories.getHistories(),true);
        }
    }

    @Override
    public void onHistoriesDeleted() {
        //Presenter删完历史之后回调这里，更新历史记录
        //因为过去之后会发现History中没有数据了，这个时候历史记录那一栏就会隐藏
        mSearchPresenter.getHistory();
    }

    @Override
    public void onSearchSuccess(SearchResult result) {
        setUpState(State.SUCCESS);
        //LogUtils.d(this, "search result ==> " + result);
        //隐藏掉推荐和历史搜索
        mHistoryContainer.setVisibility(View.GONE);
        mRecommendContainer.setVisibility(View.GONE);
        //显示搜索界面
        mRefreshContainer.setVisibility(View.VISIBLE);
        //设置数据
        mSearchResultAdapter.setData(result.getData().getTbk_dg_material_optional_response().getResult_list().getMap_data());
    }

    @Override
    public void onMoreLoaded(SearchResult result) {
        //先结束再去处理
        mRefreshContainer.finishLoadmore();
        //加载到更多的结果
        //拿到结果，添加到适配器的尾部
        List<SearchResult.DataBean.TbkDgMaterialOptionalResponseBean.ResultListBean.MapDataBean> moreData = result.getData().getTbk_dg_material_optional_response().getResult_list().getMap_data();
        mSearchResultAdapter.addDate(moreData);
        //提示用户加载到更多内容
        ToastUtil.showToast("加载到了" + moreData.size() + "条数据");
    }

    @Override
    public void onMoreLoadedError() {
        ToastUtil.showToast("网络异常，请稍后重试");
    }

    @Override
    public void onMoreLoadedEmpty() {
        ToastUtil.showToast("没有更多数据");
    }

    @Override
    public void onRecommendWordsLoaded(List<SearchRecommend.DataBean> recommendWords) {
        LogUtils.d(this, "Recommend words size ==> " + recommendWords.size());
        List<String> recommendKeywords = new ArrayList<>();
        for (SearchRecommend.DataBean item : recommendWords) {
            recommendKeywords.add(item.getKeyword());
        }
        if (recommendWords.size() == 0) {
            mRecommendContainer.setVisibility(View.GONE);
        } else {
            mRecommendContainer.setVisibility(View.VISIBLE);
            mRecommendView.setTextList(recommendKeywords,false);
        }
    }

    @Override
    public void onNetworkError() {
        setUpState(State.ERROR);
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
    public void onFlowItemClick(String text) {
        //发起搜索
        toSearch(text);
    }

    /**
     * 用于重构代码，并且保证每次搜索后都是滑动到顶部
     * @param text
     */
    private void toSearch(String text) {
        if (mSearchPresenter != null) {
            //搜索后将list滑动到顶部
            mSearchList.scrollToPosition(0);
            //将推荐设置到输入框中
            mSearchInputBox.setText(text);
            mSearchInputBox.setFocusable(true);
            mSearchInputBox.requestFocus();
            //只有输入英文之后可以把光标设置到最后
            //mSearchInputBox.setSelection(text.length());
            //输入内容之后，把光标放到最后
            mSearchInputBox.setSelection(text.length(),text.length());
            mSearchPresenter.doSearch(text);
        }
    }
}
