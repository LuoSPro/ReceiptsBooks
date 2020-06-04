package com.example.receiptsbooks.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.receiptsbooks.R;
import com.example.receiptsbooks.room.bean.IBaseProduct;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReceiptInfoListAdapter extends RecyclerView.Adapter<ReceiptInfoListAdapter.InnerHolder> {
    private List<IBaseProduct> mData = new ArrayList<>();
    private OnDetailsContentItemClickListener mItemListener = null;
    private double mTotalPrice;

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_receipt_info_list, parent, false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
        IBaseProduct product = mData.get(position);
        holder.setData(product);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemListener != null) {
                    //这里如果直接用上面的Position，会造成，删除了某些项，但是被删的那一项任然在"占位"，原因是因为
                    //我们当初设置进来的list数据没有变，唯一变得是我们一直在更新的mData，所以每次在mData里面去查找那一项就行
                    mItemListener.onItemClick(product,mData.indexOf(product));
                }
            }
        });
    }

    public List<IBaseProduct> getData() {
        return mData;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(List<? extends IBaseProduct> produces) {
        mData.clear();
        mData.addAll(produces);
        notifyDataSetChanged();
    }

    public double getAllProductPrice(){
        mTotalPrice = 0;
        for (int i = 0; i < mData.size(); i++) {
            mTotalPrice += mData.get(i).getPrice();
        }
        return mTotalPrice;
    }

    public void notifyProductItemChange(int position,IBaseProduct product){
        mData.set(position,product);
        notifyItemChanged(position,product);
    }

    public void notifyProductItemDelete(int position){
        mData.remove(position);
        notifyItemRemoved(position);
    }

    public void notifyProductItemInsert(int position,IBaseProduct product){
        //要按照之前的位置进行插入，否者出现的顺序会改变，照成信息不准
        mData.add(position,product);
        notifyItemInserted(position);
    }

    public class InnerHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.produce_name)
        public TextView produceName;

        @BindView(R.id.produce_price)
        public TextView producePrice;

        @BindView(R.id.produce_type)
        public TextView produceType;

        public InnerHolder(@NonNull View itemView) {
            super(itemView);
            //这里需要添加一个this
             ButterKnife.bind(this,itemView);
        }

        public void setData(IBaseProduct product) {
            produceName.setText("名称: " + product.getName());
            producePrice.setText("价格 "+product.getPrice() + "");
            produceType.setText(product.getType());
        }
    }

    public void setOnDetailsContentItemClickListener(OnDetailsContentItemClickListener listener){
        this.mItemListener = listener;
    }

    public interface OnDetailsContentItemClickListener{
        void onItemClick(IBaseProduct item,int position);
    }
}
