package com.example.receiptsbooks.ui.fragment;

import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.receiptsbooks.presenter.IChartAnalysisPresenter;
import com.example.receiptsbooks.room.bean.ReceiptAndProduct;
import com.example.receiptsbooks.ui.adapter.BudgetCenterDateListAdapter;
import com.example.receiptsbooks.ui.adapter.ChartAnalysisProductAdapter;
import com.example.receiptsbooks.utils.AnimationUtil;
import com.example.receiptsbooks.utils.PresenterManager;
import com.example.receiptsbooks.utils.ToastUtil;
import com.example.receiptsbooks.utils.ViewUtils;
import com.example.receiptsbooks.view.IChartAnalysisCallback;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class ChartAnalysisFragment extends BaseFragment implements IChartAnalysisCallback, BudgetCenterDateListAdapter.OnBudgetCenterDateItemListener {

    @BindView(R.id.chart_analysis_pie_chart)
    public PieChart mPieChart;

    @BindView(R.id.chart_analysis_gray_layout)
    public View mGrayLayout;

    @BindView(R.id.setting_tool_bar_ll_date_select)
    public LinearLayout mDateSelectBtn;

    @BindView(R.id.chart_analysis_tool_bar)
    public RelativeLayout mToolBar;

    @BindView(R.id.chart_analysis_pie_chart_rv_product_list)
    public RecyclerView mProductListRv;

    @BindView(R.id.setting_tool_bar_tv_date)
    public TextView mDateTv;

    @BindView(R.id.setting_tool_bar_back)
    public ImageView mToolbarBack;

    private IChartAnalysisPresenter mChartAnalysisPresenter;
    private int mCurrentSelectedDate;
    //popWindow是否已经显示了
    private boolean isPopWindowShowing = false;
    private int fromYDelta;
    private PopupWindow mPopupWindow;
    private ArrayList<String> mDateList;
    private ChartAnalysisProductAdapter mChartAnalysisProductAdapter;
    private ArrayList<PieEntry> mPieEntries;
    private PieData mPieData;

    @Override
    protected int getRootViewResId() {
        return R.layout.fragment_chart_analysis;
    }

    @Override
    protected View loadRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.base_chart_analysis_fragment,container,false);
    }

    @Override
    protected void initView(View rootView) {
        setUpState(State.SUCCESS);
        initPieChart();
        //设置布局管理器
        mProductListRv.setLayoutManager(new LinearLayoutManager(getContext()));
        //创建适配器
        mChartAnalysisProductAdapter = new ChartAnalysisProductAdapter();
        //设置适配器
        mProductListRv.setAdapter(mChartAnalysisProductAdapter);
        //对黑色半透明背景做监听，点击时开始退出动画并将popupwindow dismiss掉
        mGrayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.getContentView().startAnimation(AnimationUtil.createOutAnimation(getContext(), fromYDelta));
                mPopupWindow.getContentView().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //popwindow隐藏
                        mPopupWindow.dismiss();
                    }
                }, AnimationUtil.ANIMATION_OUT_TIME);
            }
        });
    }

    @Override
    protected void initListener() {
        mToolbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        mDateSelectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPopWindowShowing) {
                    //如果window已经展示出来，那么就隐藏
                    //否则展示window
                    showPopupWindow();
                }
            }
        });
    }

    @Override
    protected void initPresenter() {
        mChartAnalysisPresenter = PresenterManager.getInstance().getChartAnalysisPresenter();
        mChartAnalysisPresenter.registerViewCallback(this);
    }

    @Override
    protected void release() {
        mChartAnalysisPresenter.unregisterViewCallback(this);
    }

    @Override
    protected void loadData() {
        mCurrentSelectedDate = 3;
        mChartAnalysisPresenter.getProductInfoFromDb(this,this,mCurrentSelectedDate);
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
        //如果设置了这两个属性，那么就不能有收回view的动画了，以为点击事件被popWindow消费了，我们的灰色层再也接收不到点击事件
        //将这两个属性设置为false，使点击popupwindow外面其他地方不会消失
        mPopupWindow.setOutsideTouchable(true);
        //设置popupWindow.setFocusable(true); 这样才能让popupWindow里面的布局控件获得点击的事件，否则就被它的父亲view给拦截了。
        mPopupWindow.setFocusable(true);
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

    /**
     * 配置数据
     */
    private void initPieChart() {
        //标题位置
        Description description = mPieChart.getDescription();
        description.setText("账单统计表");
        //设置描述的文字,默认在右下角
//        description.setTextColor(R.color.colorWhite);
//        Display display = getActivity().getWindowManager().getDefaultDisplay();
//        Point point = new Point();
//        display.getSize(point);
//        description.setPosition(point.x/2.0f,point.y/20.0f);

        mPieChart.setDrawEntryLabels(false);// 设置entry中的描述label是否画进饼状图中
        mPieChart.setCenterText("总支出\n0.00");//设置饼状图中心的文字
        mPieChart.setTransparentCircleRadius(31f);//设置内圆和外圆的一个交叉园的半径，这样会凸显内外部的空间（没看出效果）

        Legend legend = mPieChart.getLegend();//获取图例（旁边的指示图）
        legend.setEnabled(true);//是否显示图例，false则以下配置不生效
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);//设置图例和饼状图竖向对齐
        //legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);//设置图例和饼状图横线对齐
        //以下偏移量设置可以多设几次不同值，慢慢调试最终效果
        legend.setYOffset(-30f); //图例的Y轴偏移量
        legend.setXOffset(15f); //图例的X轴偏移量
        mPieChart.setExtraOffsets(10, 0, 20, 0); //饼状图的偏移量
        legend.setYEntrySpace(4);//不同图例的Y轴间距
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);//设置图例的排列走向:vertacal相当于分行
        legend.setForm(Legend.LegendForm.SQUARE);//设置图例的图形样式,默认为圆形
        legend.setFormSize(8f);//设置图例的大小
        legend.setTextSize(8f);//设置图注的字体大小
        //legend.setTextColor(getResources().getColor(R.color.colorWhite)); //图例的文字颜色
        legend.setFormToTextSpace(5f);//设置图例到饼状图的距离
        legend.setDrawInside(false); //设置图例是否绘制在内部
        legend.setWordWrapEnabled(false);//设置图列换行(注意使用影响性能,仅适用legend位于图表下面)，我也不知道怎么用的


        mPieEntries = new ArrayList<>();
        //pie chart里面的数据
        mPieData = new PieData(getPieDataSet());
        mPieData.setDrawValues(false);            //设置是否显示数据实体(百分比，true:以下属性才有意义)
        mPieChart.setData(mPieData);
        mPieChart.invalidate(); //将图表重绘以显示设置的属性和数据

        mPieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                //这里要用向下转型，不然Entry里面的date一直未null，无法获取数据
                PieEntry pieEntry = (PieEntry) e;
                ToastUtil.showToast(pieEntry.getLabel());
            }

            @Override
            public void onNothingSelected() {

            }
        });

    }

    private PieDataSet getPieDataSet() {
        //pie chart里面的一些配置
        PieDataSet pieDataSet = new PieDataSet(mPieEntries, null);
        pieDataSet.setSliceSpace(1f);//设置每块饼之间的空隙
        pieDataSet.setSelectionShift(12f);//点击某个饼时拉长的宽度
        int[] colorArray = new int[]{
            getContext().getColor(R.color.color1),
            getContext().getColor(R.color.color2),
            getContext().getColor(R.color.color3),
            getContext().getColor(R.color.color4),
            getContext().getColor(R.color.color5),
            getContext().getColor(R.color.color6),
            getContext().getColor(R.color.color7),
            getContext().getColor(R.color.color8),
            getContext().getColor(R.color.color9),
            getContext().getColor(R.color.color10),
            getContext().getColor(R.color.color11),
            getContext().getColor(R.color.color12),
            getContext().getColor(R.color.color13),
            getContext().getColor(R.color.color14),
            getContext().getColor(R.color.color15),
            getContext().getColor(R.color.color16),
            getContext().getColor(R.color.color17),
            getContext().getColor(R.color.color18),
            getContext().getColor(R.color.color19),
            getContext().getColor(R.color.color20),
            getContext().getColor(R.color.color21),
            getContext().getColor(R.color.color22),
            getContext().getColor(R.color.color23)
        };
        pieDataSet.setColors(colorArray); //设置饼状图不同数据的颜色
        return pieDataSet;
    }

    @Override
    public void onProductInfoLoaded(List<BudgetInfo> budgetInfos, double totalPrice, List<ReceiptAndProduct> receiptAndProducts) {
        setUpState(State.SUCCESS);
        //给recyclerview设置数据
        mChartAnalysisProductAdapter.setData(budgetInfos,totalPrice);
        updatePieChart(budgetInfos,totalPrice);
    }

    private void updatePieChart(List<BudgetInfo> budgetInfos, double totalPrice) {
        //更新总额
        mPieChart.setCenterText("总支出\n"+new DecimalFormat("0.00").format(totalPrice));
        mPieEntries.clear();
        for (int i = 0; i < budgetInfos.size(); i++) {
            BudgetInfo budgetInfo = budgetInfos.get(i);
            BigDecimal b = new BigDecimal(budgetInfo.getBudgetBalance() * 100 / totalPrice);
            float value = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
            mPieEntries.add(new PieEntry(value,budgetInfo.getBudgetTitle()+" "+value+"%"));
        }
        mPieData.setDataSet(getPieDataSet());
        mPieData.setValueFormatter(new ValueFormatter() { //格式化数据，并加上百分号
            @Override
            public String getFormattedValue(float value) {
                if (value == 0) {
                    return "";
                } else {
                    BigDecimal b = new BigDecimal(String.valueOf(value));
                    float num = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
                    return num + "%";
                }
            }
        });
        mPieData.setValueTextSize(12f);          //设置所有DataSet内数据实体（百分比）的文本字体大小
        mPieData.setValueTextColor(Color.WHITE);  //设置所有DataSet内数据实体（百分比）的文本颜色
        mPieChart.setData(mPieData);
        mPieChart.invalidate(); //将图表重绘以显示设置的属性和数据
        mPieChart.animateXY(1400, 1400);
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
    public void onDateItemClick(int position) {
        //隐藏选择（只有这里才能用动画的形式消失，不然其他地方都不能监听到点击事件，进而无法用动画）
        mPopupWindow.getContentView().startAnimation(AnimationUtil.createOutAnimation(getContext(), fromYDelta));
        mPopupWindow.getContentView().postDelayed(new Runnable() {
            @Override
            public void run() {
                //popwindow隐藏
                mPopupWindow.dismiss();
            }
        }, AnimationUtil.ANIMATION_OUT_TIME);
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
        mChartAnalysisPresenter.getProductInfoFromDb(this,this,mCurrentSelectedDate);
    }
}
