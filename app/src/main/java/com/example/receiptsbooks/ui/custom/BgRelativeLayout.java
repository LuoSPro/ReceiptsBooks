package com.example.receiptsbooks.ui.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.example.receiptsbooks.R;

public class BgRelativeLayout extends RelativeLayout {
    private PaintFlagsDrawFilter mDrawFilter;
    private Paint mWavePaint;
    //三角函数的初象
    private float mOffset1 = 0.0f;
    private float mOffset2 = 0.0f;
    private float mSpeed1 = 0.05f;
    private float mSpeed2 = 0.07f;

    public BgRelativeLayout(Context context) {
        this(context,null);
    }

    public BgRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BgRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    @SuppressLint("ResourceAsColor")
    private void initView() {
        // 初始绘制波纹的画笔
        mWavePaint = new Paint();
        // 去除画笔锯齿
        mWavePaint.setAntiAlias(true);
        // 设置风格为实线
        mWavePaint.setStyle(Paint.Style.FILL);
        // 设置画笔颜色
        mWavePaint.setColor(getResources().getColor(R.color.colorPrimaryDark,null));
        mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        initDrawBg(canvas);//放在super前是后景，相反是前景，前景会覆盖子布局
        super.dispatchDraw(canvas);
    }

    private void initDrawBg(Canvas canvas) {
        // 从canvas层面去除绘制时锯齿
        canvas.setDrawFilter(mDrawFilter);
        for (int i = 0; i < getWidth(); i++) {
            // y = A * sin( wx + b) + h ; A： 浪高； w：周期；b：初相；  h: y轴的偏移位置
            float endY = (float) (20 * Math.sin(2 * Math.PI / getWidth() * i + mOffset1) + 330);
            //h:是y轴方向的偏移量
            //startY: 0就是从最顶部开始
            //画第一条波浪
            canvas.drawLine(i, 0, i, endY, mWavePaint);
            //跟第一条线一样

            float endY2 = (float) (20 * Math.sin(2 * Math.PI / getWidth() * i + mOffset2) + 330);
            //画第二条波浪
            canvas.drawLine(i, 0, i, endY2, mWavePaint);
        }

        if (mOffset1 > Float.MAX_VALUE - 1) {//防止数值超过浮点型的最大值
            mOffset1 = 0;
        }
        mOffset1 += mSpeed1;

        if (mOffset2 > Float.MAX_VALUE - 1) {//防止数值超过浮点型的最大值
            mOffset2 = 0;
        }
        mOffset2 += mSpeed2;
        //刷新
        postInvalidate();
    }

}
