package com.example.receiptsbooks.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.receiptsbooks.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingContentAdapter extends RecyclerView.Adapter<SettingContentAdapter.InnerHolder> {
    private List<String> mDataContent = new ArrayList<>();
    private List<Integer> mDataIcon = new ArrayList<>();
    private OnSettingContentItemClickListener mItemListener;

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_setting_content_list, parent, false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
        holder.mIcon.setImageResource(mDataIcon.get(position));
        holder.mTitle.setText(mDataContent.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemListener.onSettingItemClick(position);
            }
        });
    }

    public void setData(List<Integer> dataIcon,List<String> dataContent){
        this.mDataContent.clear();
        this.mDataIcon.clear();
        this.mDataContent.addAll(dataContent);
        this.mDataIcon.addAll(dataIcon);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mDataContent.size();
    }

    public class InnerHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_setting_list_icon)
        public ImageView mIcon;

        @BindView(R.id.item_setting_list_title)
        public TextView mTitle;

        public InnerHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    public void setOnSettingContentItemClickListener(OnSettingContentItemClickListener listener){
        this.mItemListener = listener;
    }

    public interface OnSettingContentItemClickListener{
        void onSettingItemClick(int position);
    }
}
