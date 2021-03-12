package com.example.receiptsbooks.ui.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.BitmapDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.receiptsbooks.R;
import com.example.receiptsbooks.base.BaseFragment;
import com.example.receiptsbooks.base.State;
import com.example.receiptsbooks.model.domain.BudgetInfo;
import com.example.receiptsbooks.presenter.IBudgetCenterPresenter;
import com.example.receiptsbooks.room.bean.BudgetDateBean;
import com.example.receiptsbooks.ui.adapter.BudgetCenterDateListAdapter;
import com.example.receiptsbooks.ui.adapter.BudgetContentAdapter;
import com.example.receiptsbooks.utils.AnimationUtil;
import com.example.receiptsbooks.utils.PresenterManager;
import com.example.receiptsbooks.utils.ViewUtils;
import com.example.receiptsbooks.view.IBudgetCenterCallback;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class BudgetCenterFragment extends BaseFragment implements BudgetContentAdapter.OnBudgetItemClickListener, BudgetCenterDateListAdapter.OnBudgetCenterDateItemListener, IBudgetCenterCallback {

    @BindView(R.id.budget_center_rv_budget_list)
    public RecyclerView mBudgetList;

    @BindView(R.id.budget_center_tv_total_budget)
    public TextView mTotalBudget;

    @BindView(R.id.budget_center_btn_total_budget)
    public ImageView mModifyTotalBudget;

    @BindView(R.id.budget_center_tv_current_money)
    public TextView mCurExpend;

    @BindView(R.id.budget_center_tv_exceed_money)
    public TextView mExcExpend;

    @BindView(R.id.setting_tool_bar_btn_refresh)
    public ImageView mRefreshBudgetBtn;

    @BindView(R.id.setting_tool_bar_ll_date_select)
    public LinearLayout mDateSelectBtn;

    @BindView(R.id.setting_tool_bar_tv_date)
    public TextView mDateTv;

    @BindView(R.id.budget_center_tool_bar)
    public RelativeLayout mToolBar;

    @BindView(R.id.setting_tool_bar_tv_title)
    public TextView mToolBarTitle;

    @BindView(R.id.budget_center_gray_layout)
    public View mGrayLayout;

    @BindView(R.id.setting_tool_bar_back)
    public ImageView mBackIv;

    private BudgetContentAdapter mBudgetAdapter;
    private EditText mBudgetDialogEt;
    private TextView mBudgetDialogWordCount;
    private TextView mBudgetDialogConfirm;
    //popWindow是否已经显示了
    private boolean isPopWindowShowing = false;
    private int fromYDelta;
    private PopupWindow mPopupWindow;
    private IBudgetCenterPresenter mBudgetCenterPresenter;
    private int mCurrentSelectedDate;
    private ArrayList<String> mDateList;

    @Override
    protected void initPresenter() {
        mBudgetCenterPresenter = PresenterManager.getInstance().getBudgetCenterPresenter();
        mBudgetCenterPresenter.registerViewCallback(this);
    }

    @Override
    protected void release() {
        if (mBudgetCenterPresenter != null) {
            mBudgetCenterPresenter.unregisterViewCallback(this);
        }
    }

    @Override
    protected void initView(View rootView) {
        //设置标题
        mToolBarTitle.setText("预算中心");
        //刷新按钮可见
        mRefreshBudgetBtn.setVisibility(View.VISIBLE);
        mBudgetList.setLayoutManager(new LinearLayoutManager(getContext()));
        //创建适配器
        mBudgetAdapter = new BudgetContentAdapter();
        //设置适配器
        mBudgetList.setAdapter(mBudgetAdapter);
        //对黑色半透明背景做监听，点击时开始退出动画并将popupwindow dismiss掉
        mGrayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissPopupWindow(getContext());
            }
        });
    }

    @Override
    protected void initListener() {
        mGrayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPopWindowShowing) {
                    dismissPopupWindow(getContext());
                }
            }
        });
        mBudgetAdapter.setOnBudgetItemClickListener(this);
        mBackIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        mModifyTotalBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //弹出Dialog
                AlertDialog alertDialog = initDialogData("支出总预算", mTotalBudget.getText().toString());
                initDialogListener(alertDialog,-1,false);
            }
        });
        mRefreshBudgetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //展示询问dialog
                showRefreshDialog();
            }
        });
        mDateSelectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPopWindowShowing) {
                    //否则展示window
                    showPopupWindow();
                }else {
                    //如果window已经展示出来，那么就隐藏
                    dismissPopupWindow(getContext());
                }
            }
        });

    }

    private void dismissPopupWindow(Context mainActivity) {
        mPopupWindow.getContentView().startAnimation(AnimationUtil.createOutAnimation(mainActivity, fromYDelta));
        mPopupWindow.getContentView().postDelayed(new Runnable() {
            @Override
            public void run() {
                //popwindow隐藏
                mPopupWindow.dismiss();
            }
        }, AnimationUtil.ANIMATION_OUT_TIME);
    }

    private void showRefreshDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("你要将所有预算汇总为总预算吗？");
        builder.setCancelable(true);
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                double totalBudgetMoney = mBudgetAdapter.getAllBudgetMoney();
                double totalBudgetBalance = mBudgetAdapter.getAllBudgetBalance();
                mTotalBudget.setText(new DecimalFormat("0.00").format(totalBudgetMoney));
                mCurExpend.setText(new DecimalFormat("0.00").format(totalBudgetBalance));
                mExcExpend.setText(new DecimalFormat("0.00").format(totalBudgetBalance-totalBudgetMoney>0?(totalBudgetBalance-totalBudgetMoney):0));
                //更新数据库
                BudgetDateBean budgetDateBean = new BudgetDateBean();
                budgetDateBean.setBudgetDateId(mCurrentSelectedDate);
                budgetDateBean.setTotalBudget(totalBudgetMoney);
                mBudgetCenterPresenter.updateTotalBudget(budgetDateBean);
            }
        });
        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }

    private void showPopupWindow() {
        initPopupWindowData();
        //监听事件
        initPopupWindowListener();
        isPopWindowShowing = true;
    }

    private void initPopupWindowListener() {
        //popWindow消失的监听
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //设置window状态为隐藏
                isPopWindowShowing = false;
                mGrayLayout.setVisibility(View.GONE);
            }
        });

    }

    private void initPopupWindowData() {
        //需要映射的view
        final View contentView = LayoutInflater.from(getContext()).inflate(R.layout.budget_center_date_select_list, null);
        RecyclerView dateListRv = contentView.findViewById(R.id.budget_center_date_list);
        dateListRv.setLayoutManager(new LinearLayoutManager(getContext()));
        BudgetCenterDateListAdapter dateAdapter = new BudgetCenterDateListAdapter();
        mDateList = new ArrayList<String>() {{
            add("今天");
            add("本周");
            add("本月");
            add("本季");
            add("本年");
        }};
        dateAdapter.setData(mDateList);
        dateAdapter.setSelectedDate(mDateList.get(mCurrentSelectedDate-1));
        //设置监听
        dateAdapter.setOnBudgetCenterDateItemListener(this);
        dateListRv.setAdapter(dateAdapter);

        mPopupWindow = new PopupWindow(contentView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        //如果设置了这两个属性，那么就不能有收回view的动画了，因为点击事件被popWindow消费了，我们的灰色层再也接收不到点击事件
        //将这两个属性设置为false，使点击popupwindow外面其他地方不会消失
        //mPopupWindow.setOutsideTouchable(true);
        //设置popupWindow.setFocusable(true); 这样才能让popupWindow里面的布局控件获得点击的事件，否则就被它的父亲view给拦截了。
        //mPopupWindow.setFocusable(true);
        //灰色背景可见
        mGrayLayout.setVisibility(View.VISIBLE);
        //获取popupwindow高度确定动画开始位置
        int contentHeight = ViewUtils.getViewMeasuredHeight(contentView);
        //在点击内容的下面展示
        mPopupWindow.showAsDropDown(mToolBar, 0, 0);
        fromYDelta = -contentHeight - 50;
        //开始动画
        mPopupWindow.getContentView().startAnimation(AnimationUtil.createInAnimation(getContext(), fromYDelta));
    }


    @Override
    protected void loadData() {
        //首先去获取本月的预算数据
        mCurrentSelectedDate = 3;
        mBudgetCenterPresenter.getAllBudgetInfoFromDB(this,this,mCurrentSelectedDate);
    }

    @Override
    protected View loadRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.base_budget_center_fragment,container,false);
    }

    @Override
    protected int getRootViewResId() {
        return R.layout.fragment_budget_center;
    }

    @Override
    public void onBudgetClick(String title,int position,String price) {
        //初始化数据
        AlertDialog alertDialog = initDialogData(title,price);
        //设置监听
        initDialogListener(alertDialog,position,true);
    }

    private AlertDialog initDialogData(String title, String price) {
        View view = getLayoutInflater().inflate(R.layout.dialog_budget_setting, null);
        //创建dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view).setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.show();
        //初始化数据
        TextView budgetDialogTitle = view.findViewById(R.id.budget_dialog_title);
        mBudgetDialogEt = view.findViewById(R.id.budget_dialog_et);
        mBudgetDialogWordCount = view.findViewById(R.id.budget_dialog_tv_word_count);
        mBudgetDialogConfirm = view.findViewById(R.id.budget_dialog_button_confirm);
        budgetDialogTitle.setText(title);
        //设置EditText起始的数据
        if (Double.parseDouble(price) != 0.0){
            mBudgetDialogEt.setText(price);
            //光标移到最后
            mBudgetDialogEt.setSelection(price.length());
            //如果有数据，确定按钮设置可点击
            mBudgetDialogConfirm.setEnabled(true);
        }
        return dialog;
    }

    private void initDialogListener(AlertDialog dialog, int position, boolean isItemClick) {
        mBudgetDialogEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mBudgetDialogWordCount.setText(s.length()+"/6");
                if (s.length() > 6||s.length() == 0){
                    mBudgetDialogConfirm.setTextColor(getContext().getColor(R.color.gray));
                    mBudgetDialogConfirm.setEnabled(false);
                }else{
                    mBudgetDialogConfirm.setTextColor(getContext().getColor(R.color.colorPrimaryDark));
                    mBudgetDialogConfirm.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mBudgetDialogConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double budgetPrice = Double.parseDouble(mBudgetDialogEt.getText().toString());
                if (isItemClick){
                    //点击之后，数据更新
                    //mBudgetAdapter.updateBudgetItem(budgetPrice,position);
                    BudgetInfo budgetInfo = mBudgetAdapter.getData().get(position);
                    budgetInfo.setBudgetMoney(budgetPrice);
                    mBudgetCenterPresenter.updateBudgetItem(budgetInfo);
                }else{
                    //设置的总预算
                    mTotalBudget.setText(new DecimalFormat("0.00").format(budgetPrice));
                    BudgetDateBean budgetDateBean = new BudgetDateBean();
                    budgetDateBean.setTotalBudget(budgetPrice);
                    budgetDateBean.setBudgetDateId(mCurrentSelectedDate);
                    mBudgetCenterPresenter.updateTotalBudget(budgetDateBean);
                }
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onDateItemClick(int position) {
        //隐藏选择（只有这里才能用动画的形式消失，不然其他地方都不能监听到点击事件，进而无法用动画）
        dismissPopupWindow(getContext());
        //如果选择的是上次已经选择了的时间段，就没必要更新
        if (mCurrentSelectedDate == position+1){
            return;
        }
        //选择了时间段，更新数据
        //ToastUtil.showToast("position" + position);
        //更新时间段
        mCurrentSelectedDate = position+1;
        //更新tv
        mDateTv.setText(mDateList.get(position));
        mBudgetCenterPresenter.getAllBudgetInfoFromDB(this,this,mCurrentSelectedDate);
    }

    @Override
    public void onBudgetDataLoaded(BudgetDateBean budgetDateBean) {
        //总预算数据到了
        mTotalBudget.setText(new DecimalFormat("0.00").format(budgetDateBean.getTotalBudget()));
    }

    @Override
    public void onCurBudgetInfoLoaded(List<BudgetInfo> budgetInfos) {
        //数据加载到了
        mBudgetAdapter.setData(budgetInfos,getContext());
        //先设置数据再标为成功
        setUpState(State.SUCCESS);
    }

    @Override
    public void onTotalExpendLoaded(double totalExpend) {
        //总开支到了
        mCurExpend.setText(new DecimalFormat("0.00").format(totalExpend));
        //设置超过的金额
        mExcExpend.setText(new DecimalFormat("0.00").format(totalExpend-Double.parseDouble(mTotalBudget.getText().toString())>0?(totalExpend-Double.parseDouble(mTotalBudget.getText().toString())):0));
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
}
