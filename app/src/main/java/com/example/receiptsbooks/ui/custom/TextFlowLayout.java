package com.example.receiptsbooks.ui.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.receiptsbooks.R;
import com.example.receiptsbooks.utils.LogUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TextFlowLayout extends ViewGroup {
    private static final int DEFAULT_SPACE = 10;
    private float mItemHorizontalSpace = DEFAULT_SPACE;
    private float mItemVerticalSpace = DEFAULT_SPACE;
    private int mSelfWidth;
    private int mItemHeight;
    private OnFlowTextItemClickListener mItemClickListener = null;
    private static final int DEFAULT_MAX_LINE = 10;
    //默认最大行
    private int mMaxLine = DEFAULT_MAX_LINE;

    public int getContentSize(){
        return mTextList.size();
    }

    public float getItemHorizontalSpace() {
        return mItemHorizontalSpace;
    }

    public void setItemHorizontalSpace(float itemHorizontalSpace) {
        mItemHorizontalSpace = itemHorizontalSpace;
    }

    public float getItemVerticalSpace() {
        return mItemVerticalSpace;
    }

    public void setItemVerticalSpace(float itemVerticalSpace) {
        mItemVerticalSpace = itemVerticalSpace;
    }

    private List<String> mTextList = new ArrayList<>();

    public TextFlowLayout(Context context) {
        this(context, null);
    }

    public TextFlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextFlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //去拿相关属性
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.FlowTextStyle);
        mItemHorizontalSpace = ta.getDimension(R.styleable.FlowTextStyle_horizontalSpace, DEFAULT_SPACE);
        mItemVerticalSpace = ta.getDimension(R.styleable.FlowTextStyle_verticalSpace, DEFAULT_SPACE);
        ta.recycle();//释放
        //LogUtils.d(this, "mItemHorizontalSpace ==> " + mItemHorizontalSpace);
        //LogUtils.d(this, "mItemVerticalSpace ==> " + mItemVerticalSpace);
    }

    /**
     * 设置数据
     * @param textList 数据集合
     * @param isReverse 判断是否需要将数据翻转
     */
    public void setTextList(List<String> textList,boolean isReverse) {
        //因为是遍历添加item，所以会和之前的数据重复，所以先将所有的移除，再遍历添加
        removeAllViews();
        //同时List也清空
        this.mTextList.clear();
        this.mTextList.addAll(textList);
        if (isReverse){
            //这里将数据翻转一下，不然每次新的记录都在历史记录之后
            Collections.reverse(mTextList);
        }
        //遍历内容
        for (String text : mTextList) {
            //添加子View
            //LayoutInflater.from(getContext()).inflate(R.layout.flow_text_view,this,true);
            //上面这句话等价于:
            //============
            //false就是不绑定到此this（这里就是我们的ViewGroup）
            TextView item = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.flow_text_view, this, false);
            item.setText(text);
            //============
            item.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onFlowItemClick(text);
                    }
                }
            });
            addView(item);
        }
    }

    //单行的内容
    private List<View> line = null;
    //描述所有的行（Lines保存每个line，相当于保存每一行）
    private List<List<View>> lines = new ArrayList<>();

    public int getMaxLine() {
        return mMaxLine;
    }

    public void setMaxLine(int maxLine) {
        mMaxLine = maxLine;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //如果测量的时候，还没有给view设置数据，此时getChildAt(0).getMeasuredHeight()会报错，所以在这里加一个判断
        if (getChildCount() == 0){
            return;
        }
        //因为measure方法要调用多次，如果每次不把line清空，那么下次进入方法时，line任然还有值。此时就不会给line创建新的行
        //而是追加到上次measure的数据后面，所以会造成追加的那几个数据的丢失
        // 所以这个每次都必须置空，不然会影响
        line = null;
        //每次measure前，如果不把lines清空，那么就会影响下面selfHeight的计算，此时父布局的高度会增加很多
        lines.clear();
        mSelfWidth = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        //LogUtils.d(this, "mSelfWidth ==> " + mSelfWidth);
        //测量
        //LogUtils.d(this, "onMeasure ==> " + getChildCount());
        //测量孩子
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View itemView = getChildAt(i);
            //测量前
            //LogUtils.d(this, "before height ==> " + itemView.getMeasuredHeight());
            measureChild(itemView, widthMeasureSpec, heightMeasureSpec);
            //测量后
            //LogUtils.d(this, "after height ==> " + itemView.getMeasuredHeight());
            if (line == null) {
                //说明当前行为空，可以添加
                createNewLine(itemView);
            } else {
                //判断是否可以继续添加+
                if (canBeAdd(itemView, line)) {
                    //可以添加进去
                    line.add(itemView);
                } else {
                    //新创建一行
                    createNewLine(itemView);
                }
            }
        }
        mItemHeight = getChildAt(0).getMeasuredHeight();
        //所有行*每一行的高度+(n+1)个的间隙
        int selfHeight = (int) (lines.size() * mItemHeight + mItemVerticalSpace * (lines.size() + 1) + 0.5f);
        //测量自己，根据每个Item的宽度和高度的总和计算
        setMeasuredDimension(mSelfWidth, selfHeight);
    }

    private void createNewLine(View itemView) {
        if (lines.size() >= mMaxLine){
            return;
        }
        line = new ArrayList<>();
        //新建一行
        line.add(itemView);
        //把新建的那一行添加到lines中
        lines.add(line);
    }

    /**
     * 判断当前行是否可以再继续添加数据
     *
     * @param itemView
     * @param line
     * @return
     */
    private boolean canBeAdd(View itemView, List<View> line) {
        //所有已经添加的子View宽度相加:(line.size()+1)*mItemHorizontalSpace + itemView.getMeasureWidth()   已添加的宽度+准备添加的宽度
        //条件：如果小于当前控件的宽度，则可以添加，否则不能添加
        int totalWidth = itemView.getMeasuredWidth();
        //遍历
        for (View view : line) {
            //叠加所有已经添加控件的宽度
            totalWidth += view.getMeasuredWidth();
            if (itemView.getVisibility() != VISIBLE) {
                //不需要进行测量
                continue;
            }
        }
        //水平间距的宽度
        totalWidth += mItemHorizontalSpace * (line.size() + 1);
        //LogUtils.d(this, "totalWidth ==> " + totalWidth);
        //LogUtils.d(this, "mSelfWidth ==> " + mSelfWidth);
        //如果小于当前控件的宽度，则可以添加，否则不能添加
        return totalWidth <= mSelfWidth;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //布局
        LogUtils.d(this, "onMeasure ==> " + getChildCount());
//        View itemOne = getChildAt(0);
//        itemOne.layout(0,0,itemOne.getMeasuredWidth(),itemOne.getMeasuredHeight());
//
//        View itemTwo = getChildAt(1);
//        itemTwo.layout((int) (itemOne.getRight() + mItemHorizontalSpace),
//                0,
//                (int) (itemOne.getRight() + mItemHorizontalSpace + itemTwo.getMeasuredWidth()),
//                itemTwo.getMeasuredHeight());
        //上面的间隙
        int topOffset = (int) mItemVerticalSpace;
        for (List<View> views : lines) {
            //Views是每一行
            //记录每行的偏移量
            int leftOffset = (int) mItemHorizontalSpace;
            for (View view : views) {
                //每一行里的每个item
                view.layout(leftOffset, topOffset, leftOffset + view.getMeasuredWidth(), topOffset + view.getMeasuredHeight());
                //水平偏移量增加
                leftOffset += view.getMeasuredWidth() + mItemHorizontalSpace;
            }
            //垂直偏移量增加
            topOffset += mItemHeight + mItemVerticalSpace;
        }
    }
    public void setOnFlowTextItemClickListener(OnFlowTextItemClickListener listener){
        this.mItemClickListener = listener;
    }

    public interface OnFlowTextItemClickListener{
        void onFlowItemClick(String text);
    }
}
