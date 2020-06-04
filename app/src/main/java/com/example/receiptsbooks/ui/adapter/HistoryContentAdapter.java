package com.example.receiptsbooks.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.receiptsbooks.R;
import com.example.receiptsbooks.room.bean.ProductBean;
import com.example.receiptsbooks.room.bean.ReceiptAndProduct;
import com.example.receiptsbooks.room.bean.ReceiptInfoBean;
import com.example.receiptsbooks.ui.custom.TextFlowLayout;
import com.example.receiptsbooks.utils.DateUtils;
import com.example.receiptsbooks.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HistoryContentAdapter extends RecyclerView.Adapter<HistoryContentAdapter.InnerHolder> {
    private List<ReceiptAndProduct> mData = new ArrayList<>();
    private String mCategory = "";
    private OnListContentItemClickListener mListener = null;

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_content, parent, false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
        ReceiptAndProduct receiptAndProduct = mData.get(position);
        //设置数据
        holder.setData(receiptAndProduct,mCategory);
        //这里设置监听，保证点击item的位置都能接收到点击事件
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null){
                    mListener.onItemClick(receiptAndProduct);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(List<ReceiptAndProduct> contents, String category) {
        this.mCategory = category;
        mData.clear();
        mData.addAll(contents);
        notifyDataSetChanged();
    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_history_content_receipt_date)
        public TextView mReceiptDate;

        @BindView(R.id.item_history_content_total_price)
        public TextView mTotalPrice;

        @BindView(R.id.item_history_content_product_count)
        public TextView mProductCount;

        @BindView(R.id.item_history_content_save_date)
        public TextView mLastModifyDate;

        @BindView(R.id.item_history_content_type_list)
        public TextFlowLayout mProductTypeList;

        public InnerHolder(@NonNull View itemView) {
            super(itemView);
            //绑定视图
            ButterKnife.bind(this,itemView);
        }

        @SuppressLint("SetTextI18n")
        public void setData(ReceiptAndProduct receiptAndProduct, String category) {
            LogUtils.d(HistoryContentAdapter.this,"date ==> " + receiptAndProduct.getReceiptInfoBean().getReceiptDate().getTime());
            Context context = itemView.getContext();
            ReceiptInfoBean receiptInfoBean = receiptAndProduct.getReceiptInfoBean();
            List<ProductBean> productBean = receiptAndProduct.getProductBean();
            mReceiptDate.setText(DateUtils.dateToString(receiptInfoBean.getReceiptDate(),false));
            mTotalPrice.setText(String.format(context.getString(R.string.text_history_item_total_price),receiptInfoBean.getTotalPrice()));
            mProductCount.setText(String.format(context.getString(R.string.text_history_item_product_count),productBean.size()));
            mLastModifyDate.setText("上次修改: "+ DateUtils.dateFormat(receiptInfoBean.getSaveData()));
            //获取所有商品的类型
            List<String> productType = new ArrayList<>();
            for (ProductBean bean : productBean) {
                if (bean.getType().equals(category)||productType.contains(bean.getType())){
                    continue;
                }
                productType.add(bean.getType());
            }
            if (!"".equals(category)){
                productType.add(category);
            }
            mProductTypeList.setMaxLine(1);
            mProductTypeList.setTextList(productType,true);
        }
    }

    public void setOnListContentItemClickListener(OnListContentItemClickListener listener){
        this.mListener = listener;
    }

    public interface OnListContentItemClickListener{
        void onItemClick(ReceiptAndProduct item);
    }
}
