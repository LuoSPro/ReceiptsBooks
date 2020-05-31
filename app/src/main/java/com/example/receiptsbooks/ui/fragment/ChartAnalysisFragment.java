package com.example.receiptsbooks.ui.fragment;

import android.graphics.Color;
import android.util.Log;
import android.view.View;

import com.example.receiptsbooks.R;
import com.example.receiptsbooks.base.BaseFragment;
import com.example.receiptsbooks.base.State;
import com.example.receiptsbooks.utils.ToastUtil;
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
import java.util.ArrayList;

import butterknife.BindView;

public class ChartAnalysisFragment extends BaseFragment {
    @Override
    protected int getRootViewResId() {
        return R.layout.fragment_chart_analysis;
    }

    @BindView(R.id.main_pie_chart)
    public PieChart mPieChart;

    @Override
    protected void initView(View rootView) {
        setUpState(State.SUCCESS);
        initData();
    }

    /**
     * 配置数据
     */
    private void initData() {

        //标题位置
        Description description = mPieChart.getDescription();
        description.setText("账单统计表"); //设置描述的文字,默认在右下角
//        description.setTextColor(R.color.colorWhite);
//        Display display = getActivity().getWindowManager().getDefaultDisplay();
//        Point point = new Point();
//        display.getSize(point);
//        description.setPosition(point.x/2.0f,point.y/20.0f);

        mPieChart.setDrawEntryLabels(false);// 设置entry中的描述label是否画进饼状图中
        mPieChart.setCenterText("200.00\n总支出");//设置饼状图中心的文字
        mPieChart.setTransparentCircleRadius(31f);//设置内圆和外圆的一个交叉园的半径，这样会凸显内外部的空间

        Legend legend = mPieChart.getLegend();//获取图例
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


        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        String a = null;
        String b = null;
        String c = null;
        String d = null;
        String e = null;
        String f = null;
        String g = null;
        String h = null;
        String i = null;
        a = "10%";
        b = "30%";
        c = "15.8%";
        d = "10%";
        e = "5%";
        f = "2%";
        g = "3%";
        h = "20%";
        i = "4.2%";
        //去除最后一位百分号
        a = a.substring(0, a.length() - 1);
        b = b.substring(0, b.length() - 1);
        c = c.substring(0, c.length() - 1);
        d = d.substring(0, d.length() - 1);
        e = e.substring(0, e.length() - 1);
        f = f.substring(0, f.length() - 1);
        g = g.substring(0, g.length() - 1);
        h = h.substring(0, h.length() - 1);
        i = i.substring(0, i.length() - 1);
        pieEntries.add(new PieEntry(Float.parseFloat(a), "暗夜猎手:"+ a + "%"));
        pieEntries.add(new PieEntry(Float.parseFloat(b), "暴走萝莉:"+ b + "%"));
        pieEntries.add(new PieEntry(Float.parseFloat(c), "寒冰射手:"+ c + "%"));
        pieEntries.add(new PieEntry(Float.parseFloat(d), "Android:"+ d + "%"));
        pieEntries.add(new PieEntry(Float.parseFloat(e), "哈哈哈:"+ e + "%"));
        pieEntries.add(new PieEntry(Float.parseFloat(f), "美食:"+ f + "%"));
        pieEntries.add(new PieEntry(Float.parseFloat(g), "学习用具:"+ g + "%"));
        pieEntries.add(new PieEntry(Float.parseFloat(h), "生鲜:"+ h + "%"));
        pieEntries.add(new PieEntry(Float.parseFloat(i), "hhhh:"+ i + "%"));
        PieDataSet pieDataSet = new PieDataSet(pieEntries, null);
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
        PieData pieData = new PieData(pieDataSet);
        pieData.setDrawValues(true);            //设置是否显示数据实体(百分比，true:以下属性才有意义)
        pieData.setValueTextColor(Color.WHITE);  //设置所有DataSet内数据实体（百分比）的文本颜色
        pieData.setValueTextSize(12f);          //设置所有DataSet内数据实体（百分比）的文本字体大小
        pieData.setValueFormatter(new ValueFormatter() { //格式化数据，并加上百分号
            @Override
            public String getFormattedValue(float value) {
                Log.d("fantasychong_value", value + "");
                if (value == 0) {
                    return "";
                } else {
                    BigDecimal b = new BigDecimal(String.valueOf(value));
                    float num = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
                    return num + "%";
                }
            }
        });
        mPieChart.setData(pieData);
        mPieChart.highlightValues(null); //设置高亮
        mPieChart.invalidate(); //将图表重绘以显示设置的属性和数据

        mPieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                e.getData();
                ToastUtil.showToast(e.toString()+"---"+h.toString());
            }

            @Override
            public void onNothingSelected() {

            }
        });

    }
}
