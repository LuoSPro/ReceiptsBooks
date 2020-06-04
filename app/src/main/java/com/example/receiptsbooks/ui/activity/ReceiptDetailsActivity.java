package com.example.receiptsbooks.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.receiptsbooks.R;
import com.example.receiptsbooks.base.BaseActivity;
import com.example.receiptsbooks.base.BaseFragment;
import com.example.receiptsbooks.model.domain.ReceiptInfo;
import com.example.receiptsbooks.room.bean.IBaseProduct;
import com.example.receiptsbooks.ui.fragment.ModifyProductFragment;
import com.example.receiptsbooks.ui.fragment.ReceiptDetailsFragment;
import com.example.receiptsbooks.utils.Constants;
import com.example.receiptsbooks.utils.LogUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ReceiptDetailsActivity extends BaseActivity implements IReceiptDetailsActivity{

    private FragmentManager mFm;
    private ReceiptDetailsFragment mReceiptDetailsFragment;
    private ModifyProductFragment mModifyProductFragment;
    //放在Activity里面，方便fragment之间传递(因为我的fragment不是每次都创建，而是隐藏着的)
    private IBaseProduct mProductItem;
    //保存position，便于修改后更新recyclerview
    private int mItemPosition;

    @Override
    protected void initPresenter() {

    }

    public IBaseProduct getProductItem() {
        return mProductItem;
    }

    @Override
    public void setItemPosition(int position) {
        this.mItemPosition = position;
    }

    @Override
    public int getItemPosition() {
        return mItemPosition;
    }

    public void setProductItem(IBaseProduct productItem) {
        mProductItem = productItem;
    }

    @Override
    protected void release() {

    }

    @Override
    protected void initView() {
        //接收传过来的receiptId
        Intent intent = getIntent();
        int receiptId = intent.getIntExtra(Constants.KEY_RECEIPT_DETAILS_RECEIPT_ID, -1);
        if (receiptId == -1){
            ReceiptInfo receiptInfo = intent.getParcelableExtra(Constants.KEY_RECEIPT_DETAILS_RECEIPT_INFO);
            boolean isAddAccount = false;
            if (receiptInfo == null){
                receiptInfo = new ReceiptInfo();
                isAddAccount = true;
            }
            List<ReceiptInfo.Product> products = (List<ReceiptInfo.Product>) intent.getSerializableExtra(Constants.KEY_RECEIPT_DETAILS_PRODUCTS);
            if (products == null){
                products = new ArrayList<>();
            }
            LogUtils.d(this,"receiptInfo ===> "+receiptInfo);
            LogUtils.d(this,"products.size() ===> "+products.size());
            initFragment(receiptInfo,products,isAddAccount);
        }else{
            //将数据传给Fragment
            initFragment(receiptId);
        }
    }

    /**
     * 监听返回键
     */
    @Override
    public void onBackPressed() {
        if(lastOneFragment == mModifyProductFragment) {
            if (mModifyProductFragment.isModifyData()) {
                mModifyProductFragment.alterDialogShow();
            }else{
                switch2ReceiptDetailsFragment(false,true);
            }
        } else {
            if (mReceiptDetailsFragment.isEditStatus()){
                mReceiptDetailsFragment.alterDialogShow();
            }else{
                Intent intent = new Intent();
                if (mReceiptDetailsFragment.getReceiptInfo() != null){
                    //返回数据
                    intent.putExtra(Constants.KEY_RECEIPT_DETAILS_RECEIPT_INFO,mReceiptDetailsFragment.getReceiptInfo());
                    intent.putExtra(Constants.KEY_RECEIPT_DETAILS_PRODUCTS,(Serializable) mReceiptDetailsFragment.getProducts());
                    setResult(Constants.INFO_TO_DETAILS_REQUEST_CODE,intent);
                }
                finish();
            }
        }
    }

    private void initFragment(ReceiptInfo receiptInfo, List<ReceiptInfo.Product> products, boolean isAddAccount) {
        if (isAddAccount){
            //添加账单时，数据都为空，就不需要传数据到fragment里面去了，不然浪费时间
            mReceiptDetailsFragment = ReceiptDetailsFragment.newInstance(true);
        }else{
            //如果是修改小票信息，就把数据通过bundle传过去
            mReceiptDetailsFragment = ReceiptDetailsFragment.newInstance(receiptInfo,products);
        }
        mModifyProductFragment = ModifyProductFragment.newInstance();
        mFm = getSupportFragmentManager();
        switchFragment(mReceiptDetailsFragment);//默认详情页面
    }

    private void initFragment(int receiptId) {
        //获取ReceiptDetailsFragment的实例，并把receiptId传到Fragment去
        mReceiptDetailsFragment = ReceiptDetailsFragment.newInstance(receiptId);
        mModifyProductFragment = ModifyProductFragment.newInstance();
        mFm = getSupportFragmentManager();
        switchFragment(mReceiptDetailsFragment);//默认详情页面
    }

    /**
     * 上一次显示的fragment
     */
    private BaseFragment lastOneFragment = null;

    private void switchFragment(BaseFragment targetFragment) {
        //修改成add和hide的方式控制Fragment的切换
        FragmentTransaction transaction = mFm.beginTransaction();
        if (!targetFragment.isAdded()){
            transaction.add(R.id.receipt_details_container,targetFragment);
        }else {
            transaction.show(targetFragment);
        }
        if (lastOneFragment != null){
            transaction.hide(lastOneFragment);
        }
        lastOneFragment = targetFragment;
        transaction.commit();//记得提交事务
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected int getLayoutResId() {
        //判断SDK的版本
        //标题栏的上方显示了一块空白区域，感觉像是没有隐藏Toolbar导致的，但是试了之后发现
        //不是Toolbar的问题，是版本问题。我的是android4.2.2的在android5.0就会这样
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark,null));//改变状态栏颜色（可以和应用的标题颜色一样）
        window.setNavigationBarColor(Color.TRANSPARENT);
        //在设置setContentView之前调用
        return R.layout.activity_receipt_details;
    }

    @Override
    public void switch2ModifyProductFragment(IBaseProduct item) {
        switchFragment(mModifyProductFragment);
        mModifyProductFragment.setFragmentData(item);
    }

    @Override
    public void switch2ReceiptDetailsFragment(boolean isModify,boolean modifyOrAdd) {
        switchFragment(mReceiptDetailsFragment);
        if (isModify){
            if (modifyOrAdd){
                //说明之前的操作是修改信息
                //若修改，则把position连同数据拿去一起更新
                mReceiptDetailsFragment.updateProductItem(mItemPosition,mProductItem);
            }else{
                //说明之前的操作是添加信息
                mReceiptDetailsFragment.addProductItem(mItemPosition,mProductItem);
            }
        }
    }
}
