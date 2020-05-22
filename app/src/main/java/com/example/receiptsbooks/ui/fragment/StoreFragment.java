package com.example.receiptsbooks.ui.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.receiptsbooks.R;
import com.example.receiptsbooks.base.BaseFragment;
import com.example.receiptsbooks.base.State;
import com.example.receiptsbooks.model.domain.Categories;
import com.example.receiptsbooks.presenter.IStorePresenter;
import com.example.receiptsbooks.ui.activity.IMainActivity;
import com.example.receiptsbooks.ui.activity.MainActivity;
import com.example.receiptsbooks.ui.adapter.StorePagerAdapter;
import com.example.receiptsbooks.utils.LogUtils;
import com.example.receiptsbooks.utils.PresenterManager;
import com.example.receiptsbooks.view.IStoreCallback;
import com.google.android.material.tabs.TabLayout;

import butterknife.BindView;

public class StoreFragment extends BaseFragment implements IStoreCallback{
    @BindView(R.id.store_indicator)
    public TabLayout mTabLayout;

    //这里使用接口定义，是为了从外面进来的时候，不能看到我们方法的实现，而只能看到我们接口中动议的方法
    private IStorePresenter mStorePresenter;

    @BindView(R.id.store_pager)
    public ViewPager mStorePager;

    @BindView(R.id.store_search_input_box)
    public View mSearchInputBox;

    @BindView(R.id.scan_icon)
    public View scanBtn;

    private StorePagerAdapter mStorePagerAdapter;

    @Override
    protected int getRootViewResId() {
        return R.layout.fragment_store;
    }

    @Override
    protected void initView(View rootView) {
        //设置开始的ViewPager
        mTabLayout.setupWithViewPager(mStorePager);
        //给ViewPager设置一个适配器
        //因为之前是在Activity中，可以直接用FragmentManager，但是现在在fragment中，所以只能用getChildFragmentManager()去获得FragmentManager
        mStorePagerAdapter = new StorePagerAdapter(getChildFragmentManager());
        //设置适配器
        mStorePager.setAdapter(mStorePagerAdapter);
    }

    @Override
    protected void initListener() {
        mSearchInputBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到搜索界面
                FragmentActivity activity = getActivity();
                if (activity instanceof IMainActivity) {
                    ((MainActivity)activity).switch2Search();
                }
            }
        });

        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到扫码界面
//                startActivity(new Intent(getContext(), ScanQrCodeActivity.class));
            }
        });
    }

    @Override
    public void onDestroyView() {
        LogUtils.d(this,"on destroy view...");
        super.onDestroyView();
    }

    /**
     * 覆写父类中得base_fragment_layout，就调用适合自己得base_store_fragment_layout
     * @param inflater
     * @param container
     * @return
     */
    @Override
    protected View loadRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.base_store_fragment_layourt,container,false);
    }

    @Override
    protected void loadData() {
        //加载数据
        mStorePresenter.getCategories();
    }

    @Override
    protected void initPresenter() {
        //创建Presenter
        mStorePresenter = PresenterManager.getInstance().getStorePresenter();
        mStorePresenter.registerViewCallback(this);
    }

    @Override
    public void onCategoriesLoaded(Categories categories) {
        setUpState(State.SUCCESS);
        LogUtils.d(this,"onCategoriesLoaded...");
        //加载的数据就会从这里回来
        if (mStorePagerAdapter != null) {
            //设置预加载数量,但是一般不需要修改，因为用户一般不会怎么往后滑
//            homePager.setOffscreenPageLimit(categories.getData().size());
            mStorePagerAdapter.setCategories(categories);
        }
    }

    public void onNetworkError() {
        setUpState(State.NETWORK_ERROR);
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
    protected void release() {
        //释放资源
        if (mStorePresenter != null){
            mStorePresenter.unregisterViewCallback(this);//取消注册
        }
    }

    @Override
    protected void onRetryClick() {
        //网络错误，点击重试
        //重新加载分类内容
        if (mStorePresenter != null) {
            mStorePresenter.getCategories();
        }
    }


}
