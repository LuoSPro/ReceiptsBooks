package com.example.receiptsbooks.ui.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.receiptsbooks.R;
import com.example.receiptsbooks.base.BaseActivity;
import com.example.receiptsbooks.model.domain.TicketResult;
import com.example.receiptsbooks.presenter.ITicketPresenter;
import com.example.receiptsbooks.utils.LogUtils;
import com.example.receiptsbooks.utils.PresenterManager;
import com.example.receiptsbooks.utils.ToastUtil;
import com.example.receiptsbooks.view.ITicketPagerCallback;

import butterknife.BindView;
import butterknife.Unbinder;

public class TicketActivity extends BaseActivity implements ITicketPagerCallback {

    private ITicketPresenter mTicketPresenter;
    private Unbinder mBind;
    private boolean mHasTaoBaoApp = false;

    @BindView(R.id.ticket_back_press)
    public View backPress;

    @BindView(R.id.ticket_cover)
    public ImageView mCover;

    @BindView(R.id.ticket_code)
    public EditText mTicketCode;

    @BindView(R.id.ticket_copy_or_open_btn)
    public TextView mCopyOrOpenBtn ;

    @BindView(R.id.ticket_cover_loading)
    public View loadingView ;

    @BindView(R.id.ticket_load_retry)
    public TextView retryLoadText ;

    @Override
    protected void initPresenter() {
        mTicketPresenter = PresenterManager.getInstance().getTicketPresenter();
        if (mTicketPresenter != null) {
            mTicketPresenter.registerViewCallback(this);
        }
        //判断是否有安装淘宝
        //act=android.intent.action.MAIN
        // cat=[android.intent.category.LAUNCHER]
        // flg=0x30200000
        // cmp=com.taobao.taobao/com.taobao.tao.welcome.Welcome ————欢迎界面
        ////com.taobao.taobao/com.taobao.tao.TBMainActivity  ————主界面
        //包名：com.taobao.taobao
        //检查是否有安装淘宝应用
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo("com.taobao.taobao", PackageManager.MATCH_UNINSTALLED_PACKAGES);
            mHasTaoBaoApp = packageInfo != null;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            mHasTaoBaoApp = false;
        }
        LogUtils.d(this,"mHasTaoBaoApp ==> " + mHasTaoBaoApp);
        //根据这个结果去修改UI
        mCopyOrOpenBtn.setText(mHasTaoBaoApp ? "打开淘宝领券":"复制淘口令");
    }

    @Override
    protected void release() {
        if (mTicketPresenter != null) {
            mTicketPresenter.unregisterViewCallback(this);
        }
    }

    @Override
    protected void initEvent() {
        backPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mCopyOrOpenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //复制淘口令
                //拿到内容
                String ticketCode = mTicketCode.getText().toString().trim();
                LogUtils.d(TicketActivity.this,"ticketCode ==> " + ticketCode);
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                //复制到粘贴板
                ClipData clipData = ClipData.newPlainText("sob_taobao_ticket_code", ticketCode);
                cm.setPrimaryClip(clipData);
                //判断有没有淘宝
                if (mHasTaoBaoApp){
                    //如果有就打开淘宝
                    Intent taobaoIntent = new Intent();
                    //taobaoIntent.setAction("android.intent.action.MAIN");
                    //taobaoIntent.addCategory("android.intent.category.LAUNCHER");
                    //com.taobao.taobao/com.taobao.tao.TBMainActivity
                    ComponentName componentName = new ComponentName("com.taobao.taobao","com.taobao.tao.TBMainActivity");
                    taobaoIntent.setComponent(componentName);
                    startActivity(taobaoIntent);
                }else{
                    //没有就提示复制成功
                    ToastUtil.showToast("已经复制，粘贴分享，或打开淘宝");
                }
            }
        });
    }

    @Override
    protected void initView() {

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_ticket;
    }

    @Override
    public void onNetworkError() {
        if (loadingView != null) {
            loadingView.setVisibility(View.GONE);
        }
        if (retryLoadText != null) {
            retryLoadText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoading() {
        if (loadingView != null) {
            loadingView.setVisibility(View.VISIBLE);
        }
        if (retryLoadText != null) {
            retryLoadText.setVisibility(View.GONE);
        }
    }

    @Override
    public void onEmpty() {

    }

    @Override
    public void onTicketLoaded(String cover, TicketResult ticketResult) {
        //数据加载时，设置loading不可见
        if (loadingView != null) {
            loadingView.setVisibility(View.GONE);
        }
        if (mCover != null && !TextUtils.isEmpty(cover)) {
            Glide.with(this).load(cover).into(mCover);
        }
        if (TextUtils.isEmpty(cover)){
            mCover.setImageResource(R.mipmap.no_image);
        }
        if (ticketResult != null && ticketResult.getData().getTbk_tpwd_create_response() != null){
            mTicketCode.setText(ticketResult.getData().getTbk_tpwd_create_response().getData().getModel());
        }
    }
}
