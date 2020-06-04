package com.example.receiptsbooks.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.receiptsbooks.R;
import com.example.receiptsbooks.utils.LogUtils;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public abstract class BaseFragment extends Fragment{
    private View mSuccessView;
    private View mErrorView;
    private View mLoadingView;
    private View mEmptyView;

    private Unbinder mBind;
    private ViewGroup mBaseContainer;

    @OnClick(R.id.network_error_tips)
    public void retry(){
        //点击了重新加载内容
        LogUtils.d(this,"on click。。。");
        onRetryClick();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //设置一个base布局，然后放后面的内容来填坑
        View rootView = loadRootView(inflater,container);
        mBaseContainer = rootView.findViewById(R.id.base_container);
        //加载View应该在bind之前，不然子View的空间不能被绑定
        loadStatusView(inflater,container);
        //初始化View应该在bind之后，不然会出现空指针异常
        mBind = ButterKnife.bind(this, rootView);
        initView(rootView);

        initPresenter();
        loadData();
        initListener();
        return rootView;
    }

    /**
     * 加载各种状态的View
     */
    private void loadStatusView(LayoutInflater inflater, ViewGroup container) {
        //成功的view
        mSuccessView = loadSuccessView(inflater, container);
        mBaseContainer.addView(mSuccessView);
        //判断状态

        //loading的view
        mLoadingView = loadLoadingView(inflater, container);
        mBaseContainer.addView(mLoadingView);
        //错误页面
        mErrorView = loadErrorView(inflater, container);
        mBaseContainer.addView(mErrorView);
        //内容为空的页面
        mEmptyView = loadEmptyView(inflater, container);
        mBaseContainer.addView(mEmptyView);
        //一开始必须全部设置为NONE，不然当请求成功后，界面会出现多个View重叠
        setUpState(State.NONE);
    }

    /**
     * 子类通过这个方法来切换状态页面即可
     */
    public void setUpState(State state){
        //当前状态
        mSuccessView.setVisibility(state == State.SUCCESS ? View.VISIBLE : View.GONE);
        mLoadingView.setVisibility(state == State.LOADING ? View.VISIBLE : View.GONE);
        mErrorView.setVisibility(state == State.NETWORK_ERROR ? View.VISIBLE : View.GONE);
        mEmptyView.setVisibility(state == State.EMPTY ? View.VISIBLE : View.GONE);
    }

    /**
     * 1.因为每个子类的Fragment都有onCreateView，所以把里面的代码抽取到BaseFragment中，可以节省代码
     * 2.如果把这里的方法设置成抽象类，让子类覆写，那么子类都要去inflate，还不如就直接在baseFragment中做了，方便管理
     * @return view
     */
    private View loadSuccessView(LayoutInflater inflater, ViewGroup container){
        int resId = getRootViewResId();
        return inflater.inflate(resId,container,false);
    }

    /**
     * 加载loading界面
     */
    protected View loadLoadingView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_loading,container,false);
    }

    /**
     * 网络错误
     */
    protected View loadEmptyView(LayoutInflater inflater, ViewGroup container){
        return inflater.inflate(R.layout.fragment_empty,container,false);
    }

    /**
     * 内容为空
     */
    private View loadErrorView(LayoutInflater inflater, ViewGroup container){
        return inflater.inflate(R.layout.fragment_network_error,container,false);
    }

    protected View loadRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.base_fragment_layout,container,false);
    }

    protected void loadData() {
        //加载数据
    }

    /**
     * 如果子fragment需要知道网络错误以后得点击，那覆盖此方法即可
     */
    protected void onRetryClick() {

    }

    protected void initPresenter() {
        //创建Presenter
    }

    /**
     * 如果子类需要去设置相关的事件，覆盖此方法
     */
    protected void initListener() {

    }

    protected void initView(View rootView){

    }

    /**
     * 这里直接让子类覆写，返回各自的布局id，这都是原子操作，比在子类中使用inflate性能强
     */
    protected abstract int getRootViewResId();

    /**
     * 销毁View的时候释放资源
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mBind != null) {
            mBind.unbind();
        }
        release();
    }

    protected void release() {
        //释放资源，因为之前注册了，如果不取消注册的话，会造成内存泄漏
    }
}
