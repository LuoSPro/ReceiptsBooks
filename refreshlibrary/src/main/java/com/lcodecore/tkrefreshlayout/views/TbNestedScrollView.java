package com.lcodecore.tkrefreshlayout.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;
public class TbNestedScrollView extends NestedScrollView {
    private static final String TAG = "TbNestedScrollView";
    private int mHeaderHeight = 0;//onScrollChanged方法中，滑动距离得最大值为466(我的手机上是这么多)
    private int originScroll = 0;
    private RecyclerView mRecyclerView;

    public TbNestedScrollView(@NonNull Context context) {
        this(context,null);
    }

    public TbNestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TbNestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setHeaderHeight(int headerHeight){
        this.mHeaderHeight = headerHeight;
    }

    /**\
     * 滑动之前的准备工作
     * @param target
     * @param dx
     * @param dy
     * @param consumed
     * @param type
     */
    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        //LogUtils.d(this,"dy ==> " + dy);
        if (target instanceof RecyclerView){
            this.mRecyclerView = (RecyclerView)target;
        }
        if (originScroll < mHeaderHeight){
            scrollBy(dx,dy);
            consumed[0] = dx;
            consumed[1] = dy;
        }
        super.onNestedPreScroll(target, dx, dy, consumed, type);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        //t是垂直方向的原始高度(就是滑动的顶部的高度)
        this.originScroll = t;
        //LogUtils.d(this,"vertical ==> " + t);
        super.onScrollChanged(l, t, oldl, oldt);
    }

    /**
     * 判断子类是否滑动到了底部
     * @return
     */
    public boolean isInBottom() {
        if (mRecyclerView != null) {
            //这里通过使用RecyclerView的判断是否到达底部的方法，来通知refreshView当前是否已经到达底部
            //进而动态的控制nestedView的滑动
            //在滑动过程中mRecyclerView.canScrollVertically(1)返回true，滑倒底部返回false
            //我们希望滑动过程中不加载更多，所以!true=false，滑到底部后，!false=true，可以加载更多
            boolean isBottom = !mRecyclerView.canScrollVertically(1);
            //Log.d(TAG, "canScroll ==> " + isBottom);
            return isBottom;
        }
        return false;
    }
}
