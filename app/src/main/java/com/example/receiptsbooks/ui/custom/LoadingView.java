package com.example.receiptsbooks.ui.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.example.receiptsbooks.R;

public class LoadingView extends androidx.appcompat.widget.AppCompatImageView {
    private float mDegrees = 0;
    private boolean mNeedRotate = true;
    //最大转动值
    private static final int MAX_DEGREE = 360;
    //转动角度
    private static final int INTERVAL = 30;
    //重绘时间间隔
    private static final int DELAY_MILLIS = 50;

    public LoadingView(Context context) {
        this(context, null);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setImageResource(R.mipmap.loading);
        startRotate();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mNeedRotate = true;
        startRotate();
    }

    private void startRotate() {
        post(new Runnable() {
            @Override
            public void run() {
                mDegrees += INTERVAL;
                if (mDegrees >= MAX_DEGREE) {
                    mDegrees = 0;
                }
                invalidate();
                //LogUtils.d(LoadingView.this, "loading...");
                //判断是否继续旋转
                //如果不可见，或者已经DetachedFromWindow就不再转动
                if (getVisibility() == VISIBLE && mNeedRotate) {
                    postDelayed(this, DELAY_MILLIS);
                } else {
                    removeCallbacks(this);
                }
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopRotate();
    }

    private void stopRotate() {
        mNeedRotate = false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.rotate(mDegrees, getWidth() / 2, getHeight() / 2);
        super.onDraw(canvas);
    }
}
