package com.example.receiptsbooks.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.receiptsbooks.R;
import com.example.receiptsbooks.model.domain.IBaseInfo;
import com.example.receiptsbooks.model.domain.ILinearItemInfo;
import com.example.receiptsbooks.utils.LogUtils;
import com.example.receiptsbooks.utils.UrlUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LinearItemContentAdapter extends RecyclerView.Adapter<LinearItemContentAdapter.InnerHolder> {
    private static final String TAG = "HomePageContentAdapter";

    private List<ILinearItemInfo> mData = new ArrayList<>();
    private OnListenItemClickListener mItemClickListener;

    @NonNull
    @Override
    public LinearItemContentAdapter.InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_linear_goods_content, parent, false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull LinearItemContentAdapter.InnerHolder holder, int position) {
        LogUtils.d(this,"onBindViewHolder..." + position);
        ILinearItemInfo dataBean = mData.get(position);
        //设置数据
        holder.setData(dataBean);
        //给recyclerView的整个item设置监听
        holder.itemView.setOnClickListener(v -> {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(dataBean);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    /**
     * 设置数据
     *
     * 这里用通配符的原因：因为外面传进出的对象是ILinearItemInfo的子类的对象，如果这里使用的ILinearItemInfo去接收，那么发生的
     * 是向上转型，但是这里是List<ILinearItemInfo>，而传进来的是List<ILinearItemInfo的子类>，所以这里才使用通配符，解除只接收
     * ILinearItemInfo类型的限制
     *
     * @param contents
     */
    public void setData(List<? extends ILinearItemInfo> contents) {
        mData.clear();
        mData.addAll(contents);
        notifyDataSetChanged();
    }

    public void addDate(List<? extends ILinearItemInfo> contents) {
        //调价之前拿到的size
        int oldSize = mData.size();
        mData.addAll(contents);
        //更新UI
        notifyItemRangeChanged(oldSize, contents.size());
    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.goods_cover)
        public ImageView cover;

        @BindView(R.id.goods_title)
        public TextView title;

        @BindView(R.id.goods_off_prise)
        public TextView offPriseTv;

        @BindView(R.id.goods_after_off_price)
        public TextView finalPriseTv;

        @BindView(R.id.goods_original_price)
        public TextView originPriceTv;

        @BindView(R.id.goods_sell_count)
        public TextView sellCountTv;

        public InnerHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @SuppressLint("DefaultLocale")
        public void setData(ILinearItemInfo dataBean) {
            Context context = itemView.getContext();
            title.setText(dataBean.getTitle());
            //根据图片大小压缩图片
            //ViewGroup.LayoutParams layoutParams = cover.getLayoutParams();
            //int width = layoutParams.width;
            //int height = layoutParams.height;
            //int coverSize = (Math.max(width, height)) / 2;

//            LogUtils.d(TAG, "width ==> " + width + " height ==> " + height);
            //LogUtils.d(this,"url ==> " + dataBean.getPict_url());
            //根据我们屏幕的大小去选取图片的大小类型，但是由于不是自己的服务器，所以不太好控制
//            String coverPath = UrlUtils.getCoverPath(dataBean.getCover(), coverSize);
            String coverPath = UrlUtils.getCoverPath(dataBean.getCover());
            //LogUtils.d(TAG,"coverPath" + coverPath);
            Glide.with(context).load(coverPath).into(cover);
            long couponAmount = dataBean.getCouponAmount();
            String finalPrice = dataBean.getFinalPrice();
            //LogUtils.d(this, "final price ==> " + finalPrice);
            float resultPrice = Float.parseFloat(finalPrice) - couponAmount;
            //LogUtils.d(this, "resultPrice ==> " + resultPrice);
            finalPriseTv.setText(String.format("%.2f", resultPrice));
            offPriseTv.setText(String.format(context.getString(R.string.text_goods_off_prise), couponAmount));
            originPriceTv.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            originPriceTv.setText(String.format(context.getString(R.string.text_goods_original_prise), finalPrice));
            sellCountTv.setText(String.format(context.getString(R.string.text_goods_sell_count), dataBean.getVolume()));
        }
    }

    public void setOnListenItemClickListener(OnListenItemClickListener listener){
        this.mItemClickListener = listener;
    }

    public interface OnListenItemClickListener{
        void onItemClick(IBaseInfo item);
    }
}
