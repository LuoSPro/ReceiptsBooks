package com.example.receiptsbooks.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.receiptsbooks.R;
import com.example.receiptsbooks.base.BaseActivity;
import com.example.receiptsbooks.base.State;
import com.example.receiptsbooks.model.domain.ReceiptInfo;
import com.example.receiptsbooks.presenter.IReceiptInfoPresenter;
import com.example.receiptsbooks.room.bean.IBaseProduct;
import com.example.receiptsbooks.ui.adapter.ReceiptInfoListAdapter;
import com.example.receiptsbooks.ui.custom.TextFlowLayout;
import com.example.receiptsbooks.utils.Constants;
import com.example.receiptsbooks.utils.DateUtils;
import com.example.receiptsbooks.utils.LogUtils;
import com.example.receiptsbooks.utils.PresenterManager;
import com.example.receiptsbooks.utils.SizeUtils;
import com.example.receiptsbooks.view.IReceiptInfoCallback;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import me.jessyan.autosize.AutoSizeCompat;
import me.jessyan.autosize.internal.CustomAdapt;

public class ReceiptInfoActivity extends BaseActivity implements IReceiptInfoCallback, CustomAdapt {

    @BindView(R.id.receipt_info_type_view)
    public TextFlowLayout mTypeView;

    @BindView(R.id.receipt_info_tv_total_price)
    public TextView mTotalPriceTv;

    @BindView(R.id.receipt_info_tv_date)
    public TextView mDateTv;

    @BindView(R.id.receipt_info_tv_count)
    public TextView mCountTv;

    @BindView(R.id.receipt_info_photo)
    public ImageView mReceiptPhoto;

    @BindView(R.id.receipt_info_rl_list)
    public RecyclerView mReceiptInfoList;

    @BindView(R.id.receipt_info_success)
    public ConstraintLayout mReceiptInfoSuccess;

    @BindView(R.id.receipt_info_analysis_error)
    public View mReceiptInfoAnalysisError;

    @BindView(R.id.receipt_info_empty)
    public View mReceiptInfoEmpty;

    @BindView(R.id.receipt_info_loading)
    public View mReceiptInfoLoading;

    @BindView(R.id.receipt_info_network_error)
    public View mReceiptInfoNetworkError;

    private ReceiptInfo mReceiptInfo;
    private State mCurrentState = State.NONE;
    private String mReceiptImagePath;
    private IReceiptInfoPresenter mReceiptInfoPresenter;
    private ReceiptInfoListAdapter mReceiptInfoListAdapter;
    private boolean mIsPreviewStatus;

    @OnClick(R.id.network_error_tips)
    public void retryLoading(){
        //点击重新上传
        onRetryClick();
    }

    @OnClick(R.id.analysis_error_tips)
    public void retryAnalysis(){
        //点击重新上传
        onRetryClick();
    }

    @OnClick(R.id.receipt_info_btn_cancel)
    public void cancelBtn(){
        finish();
    }

    @BindView(R.id.receipt_info_btn_retry)
    public Button mRetryBtn;

    @OnClick(R.id.receipt_info_btn_save)
    public void saveBtn(){
        onSaveClick();
    }

    @BindView(R.id.receipt_info_modify_one)
    public LinearLayout mModifyOne;

    @BindView(R.id.receipt_info_modify_two)
    public LinearLayout mModifyTwo;

    public void toReceiptDetailsActivity(){
        Intent intent = new Intent(ReceiptInfoActivity.this,ReceiptDetailsActivity.class);
        intent.putExtra(Constants.KEY_RECEIPT_DETAILS_RECEIPT_INFO,mReceiptInfo);
        intent.putExtra(Constants.KEY_RECEIPT_DETAILS_PRODUCTS,(Serializable) mReceiptInfo.getTotalProduct());
        startActivityForResult(intent,Constants.INFO_TO_DETAILS_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.INFO_TO_DETAILS_REQUEST_CODE&&resultCode == Constants.INFO_TO_DETAILS_REQUEST_CODE){
            Bundle extras = data.getExtras();
            if (extras != null){
                ReceiptInfo receiptInfo = extras.getParcelable(Constants.KEY_RECEIPT_DETAILS_RECEIPT_INFO);
                List<ReceiptInfo.Product> products = (List<ReceiptInfo.Product>) extras.getSerializable(Constants.KEY_RECEIPT_DETAILS_PRODUCTS);
                setDataToView(receiptInfo,products);
            }
        }
    }

    private void onSaveClick() {
        //保存数据
        //如果是手动记账，那么这里的receiptPhotoPath肯定为null
        if (mReceiptImagePath == null){
            mReceiptImagePath = "";
        }
        mReceiptInfo.setReceiptPhoto(mReceiptImagePath);
        mReceiptInfoPresenter.saveReceiptToDB(this,mReceiptInfo,mReceiptImagePath);
    }

    private void onRetryClick() {
        //网路错误，点击重试,
        //重新加载数据
        if (mReceiptInfoPresenter != null) {
            mReceiptInfoPresenter.getReceiptInfo(mReceiptImagePath,this);
        }
    }

    /**
     * 将文件上次给服务器解析
     */
    protected void loadData() {
        //数据已经在HomeFragment中进行预加载了，所以就不再到这里才进行数据申请(有助于提高响应速度)
        //mReceiptInfoPresenter.getReceiptInfo(mReceiptImagePath,this);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在此将屏幕适配尺寸重新设置为全局的，而不是我们的自定义
        AutoSizeCompat.autoConvertDensityOfGlobal((super.getResources()));//如果没有自定义需求用这个方法
        //销毁View的时候取消注册,判空
        if (mReceiptInfoPresenter != null) {
            mReceiptInfoPresenter.unregisterViewCallback(this);
        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_receipt_info;
    }

    @Override
    protected void initPresenter() {
        mReceiptInfoPresenter = PresenterManager.getInstance().getReceiptInfoPresenter();
        mReceiptInfoPresenter.registerViewCallback(this);
    }

    private void setUpStatus(State state){
        this.mCurrentState = state;
        //这里的INVISIBLE会保留mReceiptInfoSuccess的区域大小，方便其他结果的展示
        mReceiptInfoSuccess.setVisibility(mCurrentState==State.SUCCESS ? View.VISIBLE : View.INVISIBLE);
        mReceiptInfoAnalysisError.setVisibility(mCurrentState==State.ERROR ? View.VISIBLE : View.GONE);
        mReceiptInfoNetworkError.setVisibility(mCurrentState==State.NETWORK_ERROR ? View.VISIBLE : View.GONE);
        mReceiptInfoEmpty.setVisibility(mCurrentState==State.EMPTY ? View.VISIBLE : View.GONE);
        mReceiptInfoLoading.setVisibility(mCurrentState==State.LOADING ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void initEvent() {
        mRetryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击了重新加载内容
                onRetryClick();
            }
        });
        mModifyOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toReceiptDetailsActivity();
            }
        });
        mModifyTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toReceiptDetailsActivity();
            }
        });
    }

    @Override
    protected void initView() {
        setUpStatus(State.LOADING);
        //把设置间距放到initView里面，这样只会调用一次
        mReceiptInfoList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = SizeUtils.dip2px(ReceiptInfoActivity.this, 2f);
                outRect.bottom = SizeUtils.dip2px(ReceiptInfoActivity.this, 2f);
            }
        });
        //设置适配器
        mReceiptInfoListAdapter = new ReceiptInfoListAdapter();
        //必须要设置这个，否则结果出不来
        mReceiptInfoList.setLayoutManager(new LinearLayoutManager(this));
        mReceiptInfoList.setAdapter(mReceiptInfoListAdapter);
        //获取数据
        Intent intent = getIntent();
        mReceiptImagePath = intent.getStringExtra(Constants.KEY_RECEIPT_PHOTO);
        mIsPreviewStatus = intent.getBooleanExtra(Constants.KEY_RECEIPT_INFO_IS_PREVIEW,false);
        ReceiptInfo receiptInfo = intent.getParcelableExtra(Constants.KEY_RECEIPT_DETAILS_RECEIPT_INFO);
        List<ReceiptInfo.Product> products = (List<ReceiptInfo.Product>) intent.getSerializableExtra(Constants.KEY_RECEIPT_DETAILS_PRODUCTS);
        if (mIsPreviewStatus){
            setUpStatus(State.SUCCESS);
            setDataToView(receiptInfo,products);
            //设置控件的可见性
            mRetryBtn.setVisibility(View.GONE);
            mModifyOne.setVisibility(View.GONE);
            mModifyTwo.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResultLoaded(ReceiptInfo receiptInfo) {
        setUpStatus(State.SUCCESS);
        setDataToView(receiptInfo,receiptInfo.getTotalProduct());
    }

    @SuppressLint("SetTextI18n")
    private void setDataToView(ReceiptInfo receiptInfo, List<? extends IBaseProduct> products) {
        this.mReceiptInfo = receiptInfo;
        //设置图片
        mReceiptInfo.setReceiptPhoto(mReceiptImagePath);
        mReceiptInfo.setTotalProduct((List<ReceiptInfo.Product>) products);
        List<String> list = new ArrayList();
        for (int i = 0; i < products.size(); i++) {
            String produceType = products.get(i).getType();
            if (!list.contains(produceType)){
                list.add(produceType);
            }
        }
        mTypeView.setTextList(list,false);
        mTotalPriceTv.setText("总价: " + new DecimalFormat("0.00").format(receiptInfo.getTotalPrice()));
        mDateTv.setText("时间: " + DateUtils.dateToString(DateUtils.stringToData(receiptInfo.getDate()),false));
        LogUtils.d(ReceiptInfoActivity.this,"receipt save date ============> " + DateUtils.stringToData(receiptInfo.getDate()).getTime());
        mCountTv.setText("商品数量: " + products.size());
        //使用Glide框架加载图片
        if (!mIsPreviewStatus){
            Glide.with(this).load(receiptInfo.getReceiptPhoto()).into(mReceiptPhoto);
        }
        mReceiptInfoListAdapter.setData(products);
    }

    @Override
    public void onNetworkError() {
        setUpStatus(State.NETWORK_ERROR);
    }

    @Override
    public void onAnalysisError() {
        setUpStatus(State.ERROR);
    }

    @Override
    public void onLoading() {
        setUpStatus(State.LOADING);
    }

    @Override
    public void onEmpty() {
        setUpStatus(State.EMPTY);
    }

    @Override
    public boolean isBaseOnWidth() {
        return true;
    }

    @Override
    public float getSizeInDp() {
        return 430;
    }
}
