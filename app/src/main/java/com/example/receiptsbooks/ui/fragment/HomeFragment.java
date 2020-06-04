package com.example.receiptsbooks.ui.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.receiptsbooks.R;
import com.example.receiptsbooks.base.BaseFragment;
import com.example.receiptsbooks.base.State;
import com.example.receiptsbooks.model.domain.ReceiptInfo;
import com.example.receiptsbooks.presenter.IHomePresenter;
import com.example.receiptsbooks.presenter.IReceiptInfoPresenter;
import com.example.receiptsbooks.room.bean.ReceiptAndProduct;
import com.example.receiptsbooks.ui.activity.IMainActivity;
import com.example.receiptsbooks.ui.activity.MainActivity;
import com.example.receiptsbooks.ui.activity.ReceiptDetailsActivity;
import com.example.receiptsbooks.ui.activity.ReceiptInfoActivity;
import com.example.receiptsbooks.ui.adapter.HistoryContentAdapter;
import com.example.receiptsbooks.ui.adapter.HomeLooperPagerAdapter;
import com.example.receiptsbooks.utils.CameraFilePathUtil;
import com.example.receiptsbooks.utils.Constants;
import com.example.receiptsbooks.utils.LogUtils;
import com.example.receiptsbooks.utils.PresenterManager;
import com.example.receiptsbooks.utils.SizeUtils;
import com.example.receiptsbooks.utils.permission.PermissionHelper;
import com.example.receiptsbooks.utils.permission.PermissionInterface;
import com.example.receiptsbooks.view.IHistoriesCallback;
import com.example.receiptsbooks.view.IHomeCallback;
import com.example.receiptsbooks.view.IReceiptInfoCallback;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;
import com.zhihu.matisse.internal.utils.MediaStoreCompat;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import me.jessyan.autosize.internal.CustomAdapt;

public class HomeFragment extends BaseFragment implements PermissionInterface, IHomeCallback, CustomAdapt, IReceiptInfoCallback, IHistoriesCallback, HomeLooperPagerAdapter.OnHomeLooperClickListener, HistoryContentAdapter.OnListContentItemClickListener {

    @BindView(R.id.home_looper_pager)
    public ViewPager mLooperPager;

    @BindView(R.id.home_point_container)
    public LinearLayout mPaintContainer;

    @BindView(R.id.home_tv_total_expend)
    public TextView mTotalExpendTv;

    @BindView(R.id.home_tv_total_budget)
    public TextView mTotalBudgetTv;

    @BindView(R.id.home_tv_cur_month_expend_text)
    public TextView mCurMonthExpendTv;

    @BindView(R.id.home_tv_cur_month_budget_text)
    public TextView mCurMonthBudgetTv;

    @BindView(R.id.home_tv_today_total_expend)
    public TextView mTodayTotalExpend;

    @BindView(R.id.home_rv_today_record_list)
    public RecyclerView mTodayReceiptList;

    @BindView(R.id.home_ll_expend)
    public LinearLayout mExpendBtn;

    @BindView(R.id.home_ll_budget)
    public LinearLayout mBudgetBtn;

    private IHomePresenter mHomePresenter = null;
    private MediaStoreCompat mMediaStoreCompat;
    private IReceiptInfoPresenter mReceiptInfoPresenter = null;
    private HomeLooperPagerAdapter mHomeLooperPagerAdapter;
    private HistoryContentAdapter mContentAdapter;
    //选择上传图片的集合(方便多张图片上传)
    private List<Uri> mSelected;
    //权限工具类
    private PermissionHelper mPermissionHelper;
    //选择照片后的Code
    private static final int REQUEST_CODE_CHOOSE = 1000;
    private static final int PERMISSIONS_REQUEST_CODE = 10002;
    private double mTotalExpend;

    @Override
    protected int getRootViewResId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void loadData() {
        //加载数据
        mHomePresenter.getCurMonthTotalExpend(this,this);
        mHomePresenter.getTodayReceiptInfos(this,this);
    }

    @Override
    protected void initView(View rootView) {
        //这里是因为没找到解决办法才放在这里，因为如果把loading放到presenter层，当数据库插入失败时，界面任然会loading
        //因为liveData无法观察到数据的变化，所以不会去调用success界面    ---------不是这个原因，是因为这里实现了ReceiptInfoCallback，那边loading，这边也会loading
        //setUpState(State.LOADING);
        //设置布局管理器
        mTodayReceiptList.setLayoutManager(new LinearLayoutManager(getContext()));
        //设置间距
        mTodayReceiptList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = SizeUtils.dip2px(getContext(),1f);
            }
        });
        //创建适配器
        mContentAdapter = new HistoryContentAdapter();
        //设置适配器
        mTodayReceiptList.setAdapter(mContentAdapter);
        //初始化并发起权限申请
        mPermissionHelper = new PermissionHelper(Objects.requireNonNull(getActivity()), this);
        mPermissionHelper.requestPermissions();
        //创建轮播图适配器
        mHomeLooperPagerAdapter = new HomeLooperPagerAdapter();
        ArrayList<Integer> iconList = new ArrayList<Integer>() {{
            add(R.mipmap.camera);
            add(R.mipmap.account);
        }};
        //设置数据
        mHomeLooperPagerAdapter.setData(iconList);
        //设置适配器
        mLooperPager.setAdapter(mHomeLooperPagerAdapter);
        //设置drawable
        GradientDrawable normal = (GradientDrawable) getContext().getDrawable(R.drawable.shape_indicator_point_normal);
        GradientDrawable selected = (GradientDrawable) getContext().getDrawable(R.drawable.shape_indicator_point_selected);
        //设置ViewPager的点
        Context context = getContext();
        for (int i = 0; i < iconList.size(); i++) {
            View point = new View(context);
            int size = SizeUtils.dip2px(context, 8);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(size,size);
            layoutParams.leftMargin = SizeUtils.dip2px(context,5);
            layoutParams.rightMargin = SizeUtils.dip2px(context,5);
            if (i == 0){
                point.setBackground(normal);
            }else{
                point.setBackground(selected);
            }
            point.setLayoutParams(layoutParams);
            mPaintContainer.addView(point);
        }
    }

    @Override
    protected void initPresenter() {
        //创建Presenter
        mHomePresenter = PresenterManager.getInstance().getHomePresenter();
        mHomePresenter.registerViewCallback(this);
        mReceiptInfoPresenter = PresenterManager.getInstance().getReceiptInfoPresenter();
        mReceiptInfoPresenter.registerViewCallback(this);
    }

    @Override
    public void release() {
        //销毁View的时候取消注册,判空
        if (mHomePresenter != null) {
            mHomePresenter.unregisterViewCallback(this);
        }
        if (mReceiptInfoPresenter != null) {
            mReceiptInfoPresenter.unregisterViewCallback(this);
        }
    }

    @Override
    protected View loadRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.base_home_fragment_layout,container,false);
    }

    /**
     * 监听事件
     */
    @Override
    protected void initListener() {
        mContentAdapter.setOnListContentItemClickListener(this);
        mLooperPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int dataSize = mHomeLooperPagerAdapter.getDataSize();
                if (dataSize == 0){
                    return;
                }
                int targetPosition = position % dataSize;
                //切换指示器
                updateLooperIndicator(targetPosition);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mHomeLooperPagerAdapter.setOnHomeLooperClickListener(this);
        mExpendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentActivity activity = getActivity();
                if (activity instanceof IMainActivity){
                    ((IMainActivity)activity).homeToChartAnalysis();
                }
            }
        });
        mBudgetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentActivity activity = getActivity();
                if (activity instanceof IMainActivity){
                    ((IMainActivity)activity).homeToBudgetCenter();
                }
            }
        });
    }

    private void updateLooperIndicator(int targetPosition) {
        for (int i = 0; i < mPaintContainer.getChildCount(); i++) {
            View point = mPaintContainer.getChildAt(i);
            if (i == targetPosition) {
                point.setBackgroundResource(R.drawable.shape_indicator_point_normal);
            } else {
                point.setBackgroundResource(R.drawable.shape_indicator_point_selected);
            }
        }
    }

    @OnClick(R.id.home_tv_histories)
    public void viewHistories(){
        //跳转到搜索界面
        FragmentActivity activity = getActivity();
        if (activity instanceof IMainActivity) {
            ((MainActivity)activity).switch2Histories();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK){
            if (requestCode == REQUEST_CODE_CHOOSE) {
                mSelected = Matisse.obtainResult(data);//获取选择的图片的路径
                //图片结果回来，上传至服务器
                String realFilePath = CameraFilePathUtil.getRealFilePath(getContext(), mSelected.get(0));
                LogUtils.d(HomeFragment.this,"realFilePath ==> " + realFilePath);
                toReceiptInfoActivity(realFilePath);
            }
        }
    }

    /**
     * 跳转到ReceiptInfoActivity
     * @param realFilePath 图片路径
     */
    private void toReceiptInfoActivity(String realFilePath){
        //跳转之前对小票上的数据进行预加载
        mReceiptInfoPresenter.getReceiptInfo(realFilePath,getContext());
        //跳转到ReceiptInfoActivity
        Intent intent = new Intent(getContext(), ReceiptInfoActivity.class);
        intent.putExtra(Constants.KEY_RECEIPT_PHOTO,realFilePath);
        startActivity(intent);
    }

    @Override
    public int getPermissionsRequestCode() {
        return PERMISSIONS_REQUEST_CODE;
    }

    @Override
    public String[] getPermissions() {
        //设置所需的权限
        return new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET,
                Manifest.permission.CAMERA};
    }

    @Override
    public void requestPermissionsSuccess() {
        //权限通过
    }

    @Override
    public void requestPermissionsFail() {
        //获取权限失败

    }

    @Override
    public boolean isBaseOnWidth() {
        return true;
    }

    @Override
    public float getSizeInDp() {
        return 360;
    }

    @Override
    public void onResultLoaded(ReceiptInfo receiptInfo) {

    }

    @Override
    public void onAnalysisError() {

    }

    @Override
    public void onNetworkError() {

    }

    @Override
    public void onLoading() {
        setUpState(State.LOADING);
    }

    @Override
    protected View loadLoadingView(LayoutInflater inflater, ViewGroup container) {
        //改变加载的布局
        return inflater.inflate(R.layout.fragment_home_loading,container,false);
    }

    @Override
    public void onEmpty() {
        setUpState(State.EMPTY);
    }

    @Override
    protected View loadEmptyView(LayoutInflater inflater, ViewGroup container) {
        //改变空的布局
        return inflater.inflate(R.layout.fragment_home_empty,container,false);
    }

    @Override
    public void onAllReceiptHistoriesLoaded(List<ReceiptAndProduct> receiptAndProducts) {

    }

    @Override
    public void onLooperClick(int position) {
        if (position == 0){
            //选择相册同时也能调用相机
            Matisse.from(getActivity())
                    .choose(MimeType.ofImage(), false)//ofAll:图片和视频   ofImage:只有图片   ofVideo：视频
                    .countable(false)//有序图片,当选择时，会根据选择的顺序标明1234...
                    .capture(true)//使用拍照功能，下面两行必须连用
                    .captureStrategy(
                            new CaptureStrategy(true, "com.example.receiptbooks.fileprovider", "test"))//存储路径
                    .maxSelectable(1)//最多选择个数
                    .gridExpectedSize(
                            getResources().getDimensionPixelSize(R.dimen.grid_expected_size))//便于展示界面适配不同屏幕的手机
                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                    .thumbnailScale(0.85f)//缩略图与原图的比例
                    .imageEngine(new GlideEngine())//映像引擎
                    .setOnSelectedListener((uriList, pathList) -> {
                        Log.e("onSelected", "onSelected: pathList=" + pathList);//用户对展示界面进行操作时
                    })
                    .showSingleMediaType(true)//是否只能选择图片和视频的一种
                    .originalEnable(true)//用户能否选择原图
                    .maxOriginalSize(10)//原图最大为多少MB
                    .autoHideToolbarOnSingleTap(true)//是否隐藏toolbar
                    .setOnCheckedListener(isChecked -> {
                        Log.e("isChecked", "onCheck: isChecked=" + isChecked);//用户选着原始图片时回调
                    })
                    .forResult(REQUEST_CODE_CHOOSE);//与onActivityResult中的判断相对应
        }else{
            Intent intent = new Intent(getContext(), ReceiptDetailsActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onTotalExpendLoaded(double totalExpend) {
        this.mTotalExpend = totalExpend;
        mTotalExpendTv.setText(new DecimalFormat("0.00").format(totalExpend));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onTotalBudgetLoaded(double totalBudget) {
        mTotalBudgetTv.setTextColor(Color.WHITE);
        if (totalBudget == 0){
            mTotalBudgetTv.setText("未设置");
        }else if (totalBudget-mTotalExpend>0){
            mTotalBudgetTv.setText(new DecimalFormat("0.00").format(totalBudget-mTotalExpend));
        }else{
            mTotalBudgetTv.setText("已超支"+new DecimalFormat("0.00").format(mTotalExpend-totalBudget));
            mTotalBudgetTv.setTextColor(getContext().getResources().getColor(R.color.colorExcBudget,null));
        }
    }

    @Override
    public void onReceiptInfosLoaded(List<ReceiptAndProduct> receiptAndProducts, double todayExpend) {
        mTodayTotalExpend.setText(new DecimalFormat("0.00").format(todayExpend));
        mContentAdapter.setData(receiptAndProducts,"");
        setUpState(State.SUCCESS);
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
