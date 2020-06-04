package com.example.receiptsbooks.ui.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.receiptsbooks.R;
import com.example.receiptsbooks.base.BaseFragment;
import com.example.receiptsbooks.base.State;
import com.example.receiptsbooks.model.domain.ReceiptInfo;
import com.example.receiptsbooks.model.domain.UserViewInfo;
import com.example.receiptsbooks.presenter.IReceiptDetailsPresenter;
import com.example.receiptsbooks.room.bean.IBaseProduct;
import com.example.receiptsbooks.room.bean.ProductBean;
import com.example.receiptsbooks.room.bean.ReceiptAndProduct;
import com.example.receiptsbooks.room.bean.ReceiptInfoBean;
import com.example.receiptsbooks.ui.activity.IReceiptDetailsActivity;
import com.example.receiptsbooks.ui.activity.ReceiptInfoActivity;
import com.example.receiptsbooks.ui.adapter.ReceiptInfoListAdapter;
import com.example.receiptsbooks.ui.custom.ImageLoader;
import com.example.receiptsbooks.utils.AppBarStateChangeListener;
import com.example.receiptsbooks.utils.Constants;
import com.example.receiptsbooks.utils.DateUtils;
import com.example.receiptsbooks.utils.LogUtils;
import com.example.receiptsbooks.utils.OnClickUtils;
import com.example.receiptsbooks.utils.PresenterManager;
import com.example.receiptsbooks.utils.SizeUtils;
import com.example.receiptsbooks.utils.ToastUtil;
import com.example.receiptsbooks.view.IReceiptDetailsCallback;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.previewlibrary.GPreviewBuilder;
import com.previewlibrary.ZoomMediaLoader;

import java.io.File;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;

public class ReceiptDetailsFragment extends BaseFragment implements IReceiptDetailsCallback {

    @BindView(R.id.receipt_details_photo)
    public ImageView mReceiptPhotoIv;

    @BindView(R.id.receipt_details_date)
    public TextView mReceiptDateTv;

    @BindView(R.id.receipt_details_total_price)
    public TextView mReceiptTotalPriceTv;

    @BindView(R.id.receipt_details_product_list)
    public RecyclerView mProductListRv;

    @BindView(R.id.receipt_details_toolbar)
    public Toolbar mToolbar;

    @BindView(R.id.receipt_details_app_bar)
    public AppBarLayout mAppBar;

    @BindView(R.id.receipt_details_modify_btn)
    public FloatingActionButton mModifyBtn;

    @BindView(R.id.receipt_details_collapsing_toolbar)
    public CollapsingToolbarLayout mCollapsingToolbarLayout;

    @BindView(R.id.receipt_details_modify_operation_hint)
    public TextView mOperationHint;

    @BindView(R.id.receipt_details_modify_add)
    public ImageView mModifyAddBtn;

    private String mReceiptPhotoPath;
    private Menu mToolBarMenu;
    private IReceiptDetailsPresenter mReceiptDetailsPresenter = null;
    private ReceiptInfoListAdapter mListAdapter;
    //标明现在是否是在可编辑状态下
    private boolean mIsEditStatus = false;
    //标明toolbar的enum是否隐藏
    private boolean mEnumHide = true;
    private AppBarStateChangeListener.AppBarState mCurrentAppBarState = AppBarStateChangeListener.AppBarState.EXPANDED;
    private List<? extends IBaseProduct> mProducts;
    private ItemTouchHelper mItemTouchHelper;
    private boolean mIsModifyData = false;
    private ReceiptInfoBean mReceiptInfoBean;
    private int mReceiptId;
    private ReceiptInfo mReceiptInfo = new ReceiptInfo();
    private boolean mDataIsLegal = true;
    private Date mReceiptDate;
    private double mReceiptTotalPrice;
    private boolean mIsAddAccountStatus = false;

    public boolean isEditStatus() {
        return mIsEditStatus;
    }

    public ReceiptInfo getReceiptInfo() {
        //没有修改数据，则直接返回
        if (!mIsModifyData){
            return null;
        }
        mReceiptInfo.setTotalPrice(mReceiptTotalPrice);
        mReceiptInfo.setReceiptDate(DateUtils.dateToString(mReceiptDate,false));
        return mReceiptInfo;
    }

    public List<? extends IBaseProduct> getProducts() {
        return mProducts;
    }

    /**
     * 用于标明用户是通过点击记账进来的
     */
    public static ReceiptDetailsFragment newInstance(boolean isAddAccount){
        //设置单例，并传递数据
        ReceiptDetailsFragment receiptDetailsFragment = new ReceiptDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.KEY_RECEIPT_DETAILS_IS_ADD_ACCOUNT,isAddAccount);
        bundle.putInt(Constants.KEY_RECEIPT_DETAILS_RECEIPT_ID,-1);
        receiptDetailsFragment.setArguments(bundle);
        return receiptDetailsFragment;
    }

    /**
     * 用于标明用户是通过查询的数据点击进来的
     */
    public static ReceiptDetailsFragment newInstance(int receiptId){
        //设置单例，并传递数据
        ReceiptDetailsFragment receiptDetailsFragment = new ReceiptDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KEY_RECEIPT_DETAILS_RECEIPT_ID,receiptId);
        receiptDetailsFragment.setArguments(bundle);
        return receiptDetailsFragment;
    }

    /**
     * 用于标明用户是通过修改小票信息进来的
     */
    public static ReceiptDetailsFragment newInstance(ReceiptInfo receiptInfo,List<ReceiptInfo.Product> products){
        //设置单例，并传递数据
        ReceiptDetailsFragment receiptDetailsFragment = new ReceiptDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.KEY_RECEIPT_DETAILS_RECEIPT_INFO,receiptInfo);
        bundle.putSerializable(Constants.KEY_RECEIPT_DETAILS_PRODUCTS,(Serializable) products);
        bundle.putInt(Constants.KEY_RECEIPT_DETAILS_RECEIPT_ID,-1);
        receiptDetailsFragment.setArguments(bundle);
        return receiptDetailsFragment;
    }

    @Override
    protected int getRootViewResId() {
        return R.layout.fragment_receipt_details;
    }

    @Override
    protected void initPresenter() {
        mReceiptDetailsPresenter = PresenterManager.getInstance().getReceiptDetailsPresenter();
        mReceiptDetailsPresenter.registerViewCallback(this);
    }

    @Override
    protected void release() {
        if (mReceiptDetailsPresenter != null) {
            mReceiptDetailsPresenter.unregisterViewCallback(this);
        }
    }

    //初始化View
    @Override
    protected void initView(View rootView) {
        //初始化图片预览界面
        ZoomMediaLoader.getInstance().init(new ImageLoader());

        //初始化界面
        //设置recyclerView不可点击
        mProductListRv.setEnabled(false);
        //设置editText不可编辑
        mReceiptDateTv.setEnabled(false);
        mReceiptTotalPriceTv.setEnabled(false);
        //告诉fragment我们有菜单的,如果不设置这个属性，fragment中的onCreateOptionsMenu()回调不会被执行！
        setHasOptionsMenu(true);
        //上面两句话替代了下面这句话，并且解决了下面这句话在fragment中menu时有时无的bug
        //如果不设置这句话，actionBar始终为null
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);

        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);//设置HomeAsUp，默认箭头返回
        }else{
            LogUtils.d(this,"actionBar ===> is null" );
        }
        //设置编辑界面的标题
        mCollapsingToolbarLayout.setTitle("编辑信息");
        //设置商品列表
        mListAdapter = new ReceiptInfoListAdapter();
        mProductListRv.setLayoutManager(new LinearLayoutManager(getContext()));
        mProductListRv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = SizeUtils.dip2px(getContext(), 2f);
                outRect.bottom = SizeUtils.dip2px(getContext(), 2f);
            }
        });
        //商品列表，设置适配器
        mProductListRv.setAdapter(mListAdapter);
        //增加滑动删除功能
        mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.START | ItemTouchHelper.END) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                IBaseProduct product = mProducts.get(position);
                mListAdapter.notifyProductItemDelete(position);
                mProducts = mListAdapter.getData();
                if (mReceiptId != -1){
                    mReceiptDetailsPresenter.deleteProductToDB(ReceiptDetailsFragment.this,(ProductBean) product);
                }
                updateTotalPrice(mListAdapter.getAllProductPrice());
                //需要配合CoordinatorLayout使用，不然Snackbar弹起的时候会挡住上面的空间
                Snackbar.make(requireActivity().findViewById(R.id.receipt_details_fragment_view), "删除了一个商品", Snackbar.LENGTH_SHORT)
                        .setAction("撤销", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (OnClickUtils.isFastClick()){
                                    updateTotalPrice(mListAdapter.getAllProductPrice());
                                    //点击的item的position和实际的不一致——已解决
                                    mListAdapter.notifyProductItemInsert(position, product);
                                    mProducts = mListAdapter.getData();
                                    if (mReceiptId != -1){
                                        mReceiptDetailsPresenter.insertProductToDB(ReceiptDetailsFragment.this,(ProductBean) product);
                                    }
                                }
                            }
                        })
                        .show();
            }

            Drawable icon = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_delete_forever_black_24dp);
            Drawable background = new ColorDrawable(Color.LTGRAY);

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                View itemView = viewHolder.itemView;
                int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;

                int iconLeft, iconRight, iconTop, iconBottom;
                int backTop, backBottom, backLeft, backRight;
                backTop = itemView.getTop();
                backBottom = itemView.getBottom();
                iconTop = itemView.getTop() + iconMargin;
                iconBottom = iconTop + icon.getIntrinsicHeight();
                if (dX > 0) {
                    backLeft = itemView.getLeft();
                    backRight = backLeft + (int) dX;
                    background.setBounds(backLeft, backTop, backRight, backBottom);
                    iconLeft = itemView.getLeft() + iconMargin;
                    iconRight = iconLeft + icon.getIntrinsicWidth();
                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                } else if (dX < 0) {
                    backRight = itemView.getRight();
                    backLeft = backRight + (int) dX;
                    background.setBounds(backLeft, backTop, backRight, backBottom);
                    iconRight = itemView.getRight() - iconMargin;
                    iconLeft = iconRight - icon.getIntrinsicWidth();
                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                } else {
                    background.setBounds(0, 0, 0, 0);
                    icon.setBounds(0, 0, 0, 0);
                }
                background.draw(c);
                icon.draw(c);
            }
        });
    }

    //创建菜单
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        //加载菜单
        mToolBarMenu = mToolbar.getMenu();
        inflater.inflate(R.menu.toolbar,mToolBarMenu);
        //得到menu对象
        super.onCreateOptionsMenu(menu, inflater);
    }

    //显示界面中心对话框
    public void alterDialogShow(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("你要放弃本次编辑吗？");
        builder.setCancelable(true);
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getActivity().finish();
            }
        });
        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }

    //菜单事件监听
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                if (mIsEditStatus){
                    alterDialogShow();
                }else{
                    //返回数据
                    getActivity().onBackPressed();
                }
                break;
            case R.id.receipt_details_save_and_modify:
                extractDataAndRefreshView();
                LogUtils.d(this,"receipt_details_save_and_modify ===> is click");
                break;
        }
        return true;
    }

    //提取数据和刷新界面
    private void extractDataAndRefreshView() {
        this.mIsModifyData = true;
        if (mIsEditStatus){
            //初始化数据合法
            mDataIsLegal = true;
            //获取数据
            extractDataFromView();
            //如何数据不合法，保存失败
            if (!mDataIsLegal){
                return;
            }
            //保存数据
            if (mReceiptId != -1){
                //id不为-1，代表数据是从数据库中拿出来的，现在保存到数据库中去
                saveDataToDB();
            }else if (mIsAddAccountStatus){
                //如果是记账功能，那么这里就是跳转到预览界面
                toReceiptInfoActivity();
            }
        }
        //刷新界面
        refreshFragmentEditStatus();
    }

    //跳转到预览界面
    private void toReceiptInfoActivity() {
        Intent intent = new Intent(getContext(), ReceiptInfoActivity.class);
        intent.putExtra(Constants.KEY_RECEIPT_DETAILS_RECEIPT_INFO,getReceiptInfo());
        intent.putExtra(Constants.KEY_RECEIPT_DETAILS_PRODUCTS,(Serializable) getProducts());
        intent.putExtra(Constants.KEY_RECEIPT_INFO_IS_PREVIEW,true);
        startActivity(intent);
    }

    //提取数据
    private void extractDataFromView(){
        mReceiptDate = DateUtils.stringToData(mReceiptDateTv.getText().toString().trim());
        if (mReceiptDate == null){
            mReceiptDateTv.setText(DateUtils.dateToString(DateUtils.revertDate(System.currentTimeMillis()),false));
            ToastUtil.showToast("提取数据失败，时间格式不正确");
            mDataIsLegal = false;
            return;
        }
        LogUtils.d(this,"mReceiptDate ==> " + DateUtils.dateToString(mReceiptDate,false));
        mReceiptTotalPrice = -1;
        try {
            mReceiptTotalPrice = Double.parseDouble(mReceiptTotalPriceTv.getText().toString().trim());
        } catch (NumberFormatException e) {
            mReceiptTotalPriceTv.setText("0.0");
            ToastUtil.showToast("提取数据失败，价格格式不正确");
            mDataIsLegal = false;
        }
    }

    private void saveDataToDB() {
        if (mReceiptDate != mReceiptInfoBean.getReceiptDate()||mReceiptTotalPrice != mReceiptInfoBean.getTotalPrice()){
            mReceiptInfoBean.setReceiptDate(mReceiptDate);
            mReceiptInfoBean.setTotalPrice(mReceiptTotalPrice);
            mReceiptDetailsPresenter.updateReceiptToDB(this,mReceiptInfoBean);
        }
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        if (mIsEditStatus){
            //可编辑状态
            if (mIsAddAccountStatus){
                menu.findItem(R.id.receipt_details_save_and_modify).setTitle("预览");
            }else{
                menu.findItem(R.id.receipt_details_save_and_modify).setTitle("保存");
            }
        }else{
            //不可编辑状态
            menu.findItem(R.id.receipt_details_save_and_modify).setTitle("修改");
        }
        if (mEnumHide){
            menu.findItem(R.id.receipt_details_save_and_modify).setVisible(false);
        }else{
            if (mCurrentAppBarState == AppBarStateChangeListener.AppBarState.IDLE || mCurrentAppBarState == AppBarStateChangeListener.AppBarState.EXPANDED){
                //闲置或扩展状态,任然不可见
                menu.findItem(R.id.receipt_details_save_and_modify).setVisible(false);
            }else{
                //收拢状态可见
                menu.findItem(R.id.receipt_details_save_and_modify).setVisible(true);
            }
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void loadData() {
        //从ReceiptDetailsActivity通过Bundle传递过来的receiptId
        Bundle arguments = getArguments();
        mReceiptId = arguments.getInt(Constants.KEY_RECEIPT_DETAILS_RECEIPT_ID);
        //通过这个id去查询小票数据
        if (mReceiptId != -1){
            mReceiptDetailsPresenter.getReceiptInfoById(this,this, mReceiptId);
        }else{
            mIsAddAccountStatus = arguments.getBoolean(Constants.KEY_RECEIPT_DETAILS_IS_ADD_ACCOUNT);
            if (mIsAddAccountStatus){
                //设置编辑界面的标题
                mCollapsingToolbarLayout.setTitle("手动记账");
                //设置小票图片得iv不可点击
                mReceiptPhotoIv.setEnabled(false);
                mProducts = new ArrayList<>();
                mReceiptInfo = new ReceiptInfo();
            }else{
                mProducts = (List<ReceiptInfo.Product>) arguments.getSerializable(Constants.KEY_RECEIPT_DETAILS_PRODUCTS);
                mReceiptInfo = arguments.getParcelable(Constants.KEY_RECEIPT_DETAILS_RECEIPT_INFO);
            }
            setReceiptInfoToFragment();
        }
    }

    @SuppressLint("SetTextI18n")
    private void setReceiptInfoToFragment() {
        setUpState(State.SUCCESS);
        if (mReceiptInfo != null){
            //图片路径
            mReceiptPhotoPath = mReceiptInfo.getReceiptPhoto();
            //LogUtils.d(this,"ReceiptPhotoPath"+ mReceiptPhotoPath);
            Glide.with(this).load(mReceiptPhotoPath).into(mReceiptPhotoIv);
            //总金额
            double totalPrice = mReceiptInfo.getTotalPrice();
            mReceiptTotalPriceTv.setText(totalPrice+"");
            //LogUtils.d(this,"TotalPrice"+totalPrice);
            //小票时间
            String receiptDate = mReceiptInfo.getReceiptDate();
            //LogUtils.d(this,"ReceiptDate"+ receiptDate);
            mReceiptDateTv.setText(receiptDate);
        }else{
            LogUtils.d(this,"receiptAndProduct ===> is null ");
        }

        if (mProducts != null){
            //LogUtils.d(this,"productBeans.size ==> "+productBeans.size());
            mListAdapter.setData(mProducts);
        }else{
            LogUtils.d(this,"productBeans.size ===> is null");
        }
    }

    @SuppressLint("SetTextI18n")
    private void setReceiptAndProductToFragment(ReceiptAndProduct receiptInfo) {
        mReceiptInfoBean = receiptInfo.getReceiptInfoBean();
        mProducts = receiptInfo.getProductBean();
        if (mReceiptInfoBean != null){
            //图片路径
            mReceiptPhotoPath = mReceiptInfoBean.getReceiptPhotoPath();
            //LogUtils.d(this,"ReceiptPhotoPath"+ mReceiptPhotoPath);
            Glide.with(this).load(mReceiptPhotoPath).into(mReceiptPhotoIv);
            //总金额
            if (mListAdapter.getAllProductPrice() == 0){
                mReceiptTotalPriceTv.setText(new DecimalFormat("0.00").format(receiptInfo.getReceiptInfoBean().getTotalPrice()));
            }else{
                updateTotalPrice(mListAdapter.getAllProductPrice());
            }
            //LogUtils.d(this,"TotalPrice"+totalPrice);
            //小票时间
            String receiptDate = DateUtils.dateToString(mReceiptInfoBean.getReceiptDate(), false);
            //LogUtils.d(this,"ReceiptDate"+ receiptDate);
            mReceiptDateTv.setText(receiptDate);
        }else{
            LogUtils.d(this,"receiptAndProduct ===> is null ");
        }

        if (mProducts != null){
            //LogUtils.d(this,"productBeans.size ==> "+productBeans.size());
            mListAdapter.setData(mProducts);
        }else{
            LogUtils.d(this,"productBeans.size ===> is null");
        }
    }

    @Override
    protected void initListener() {
        mReceiptPhotoIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //LogUtils.d(ReceiptDetailsFragment.this,"mReceiptPhotoPath ==> " + mReceiptPhotoPath);
                //非添加添加用户状态
                if (!mIsAddAccountStatus){
                    if (new File(mReceiptPhotoPath).exists()){
                        //点击查看大图
                        GPreviewBuilder.from(getActivity())//activity实例必须
                                .setData(new ArrayList<UserViewInfo>(){{add(new UserViewInfo(mReceiptPhotoPath));}})//集合
                                .setCurrentIndex(0)
                                .setSingleFling(true)//是否在黑屏区域点击返回
                                .setDrag(false)//是否禁用图片拖拽返回
                                .setType(GPreviewBuilder.IndicatorType.Dot)//指示器类型
                                .start();//启动
                    }else{
                        ToastUtil.showToast("图片未找到");
                    }
                }
            }
        });

        mAppBar.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, AppBarStateChangeListener.AppBarState state) {
                Log.d("STATE", state.name());
                if (mToolBarMenu != null){
                    if( state == AppBarState.EXPANDED ) {
                        //展开状态
                        //ToastUtil.showToast("展开了");
                        mCurrentAppBarState = AppBarState.EXPANDED;
                        mToolBarMenu.setGroupVisible(0,false);
                    }else if(state == AppBarState.COLLAPSED){
                        //折叠状态
                        //ToastUtil.showToast("折叠了");
                        mCurrentAppBarState = AppBarState.COLLAPSED;
                        mToolBarMenu.setGroupVisible(0,true);
                    }else{
                        //ToastUtil.showToast("闲置");
                        mCurrentAppBarState = AppBarState.IDLE;
                        mToolBarMenu.setGroupVisible(0,false);
                    }
                }
            }
        });

        mModifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                extractDataAndRefreshView();
            }
        });

        mModifyAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentActivity activity = getActivity();
                if (activity instanceof IReceiptDetailsActivity){
                    //创建一个实例，传递到修改信息的fragment中去
                    ProductBean productBean = new ProductBean(0, "", 0, "");
                    if (mProducts.size() != 0){
                        //携带receiptId信息，方便更新数据库数据
                        productBean.setReceiptId(mProducts.get(0).getReceiptId());
                        ((IReceiptDetailsActivity)activity).setProductItem(productBean);
                        ((IReceiptDetailsActivity)activity).setItemPosition(mProducts.size());
                        ((IReceiptDetailsActivity)activity).switch2ModifyProductFragment(productBean);
                    }else{
                        //传一个空的数据过去
                        ((IReceiptDetailsActivity)activity).setProductItem(productBean);
                        ((IReceiptDetailsActivity)activity).setItemPosition(mProducts.size());
                        ((IReceiptDetailsActivity)activity).switch2ModifyProductFragment(productBean);
                    }
                }
            }
        });
    }

    private void refreshFragmentEditStatus() {
        //这里是要将view设置成什么状态
        if (mIsEditStatus){
            //修改成不可编辑状态
            //添加按钮隐藏
            mModifyAddBtn.setVisibility(View.GONE);
            //操作提示隐藏
            mOperationHint.setVisibility(View.GONE);
            //不可滑动
            mItemTouchHelper.attachToRecyclerView(null);
            //列表不可点击
            mListAdapter.setOnDetailsContentItemClickListener(null);
            //修改图标为——修改
            mModifyBtn.setImageResource(R.mipmap.modify_white_icon);
            mIsEditStatus = false;
        }else{
            //修改成可编辑
            //添加按钮显示
            mModifyAddBtn.setVisibility(View.VISIBLE);
            //操作提示显示
            mOperationHint.setVisibility(View.VISIBLE);
            //设置recyclerview可滑动删除
            mItemTouchHelper.attachToRecyclerView(mProductListRv);
            //列表可点击,通过adapter的点击事件去设置recyclerview的item的点击
            mListAdapter.setOnDetailsContentItemClickListener(new ReceiptInfoListAdapter.OnDetailsContentItemClickListener() {
                @Override
                public void onItemClick(IBaseProduct item,int position) {
                    FragmentActivity activity = getActivity();
                    if (activity instanceof IReceiptDetailsActivity) {
                        ((IReceiptDetailsActivity)activity).setProductItem(item);
                        ((IReceiptDetailsActivity)activity).setItemPosition(position);
                        ((IReceiptDetailsActivity)activity).switch2ModifyProductFragment(item);
                    }
                }
            });
            if (mIsAddAccountStatus){
                //预览图标
                mModifyBtn.setImageResource(R.mipmap.preview_icon);
            }else{
                //修改图标——完成
                mModifyBtn.setImageResource(R.mipmap.finish_icon);
            }
            mIsEditStatus = true;
        }
        mReceiptDateTv.setEnabled(mIsEditStatus);
        mReceiptTotalPriceTv.setEnabled(mIsEditStatus);
        //让enum的状态不隐藏
        mEnumHide = false;
        //刷新menu状态
        getActivity().invalidateOptionsMenu();
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

    }

    @Override
    public void onReceiptAndProductLoaded(ReceiptAndProduct receiptInfo) {
        setUpState(State.SUCCESS);
        //查找数据库的结果到了
        setReceiptAndProductToFragment(receiptInfo);
    }

    public void updateProductItem(int itemPosition, IBaseProduct productItem) {
        mListAdapter.notifyProductItemChange(itemPosition,productItem);
        mProducts = mListAdapter.getData();
        //如果是有id，那么就立即更新数据库
        if (mReceiptId != -1){
            mReceiptDetailsPresenter.updateProductToDB(this,(ProductBean)productItem);
        }
        //更新总额
        updateTotalPrice(mListAdapter.getAllProductPrice());
        ToastUtil.showToast("数据修改成功");
    }

    private void updateTotalPrice(double allProductPrice) {
        mReceiptTotalPriceTv.setText(new DecimalFormat("0.00").format(allProductPrice));
    }

    public void addProductItem(int position, IBaseProduct productItem) {
        //添加数据到recyclerview
        mListAdapter.notifyProductItemInsert(position, productItem);
        //如果是有id，那么就立即更新数据库
        if (mReceiptId != -1){
            mReceiptDetailsPresenter.insertProductToDB(this,(ProductBean)productItem);
        }
        mProducts = mListAdapter.getData();
        //更新总额
        updateTotalPrice(mListAdapter.getAllProductPrice());
        ToastUtil.showToast("数据添加成功");
    }
}
