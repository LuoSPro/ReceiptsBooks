package com.example.receiptsbooks.ui.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.receiptsbooks.R;
import com.example.receiptsbooks.model.domain.BudgetInfo;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChartAnalysisProductAdapter extends RecyclerView.Adapter<ChartAnalysisProductAdapter.InnerHolder> {
    private List<BudgetInfo> mBudgetInfos = new ArrayList<>();
    private double mTotalPrice;

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chart_analysis_list, parent, false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
        holder.setData(mBudgetInfos.get(position));
    }

    @Override
    public int getItemCount() {
        return mBudgetInfos.size();
    }

    public void setData(List<BudgetInfo> budgetInfos, double totalPrice) {
        this.mBudgetInfos.clear();
        this.mBudgetInfos.addAll(budgetInfos);
        this.mTotalPrice = totalPrice;
        //界面更新
        notifyDataSetChanged();
    }

    public class InnerHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_chart_analysis_icon)
        public ImageView mIcon;

        @BindView(R.id.item_chart_analysis_title)
        public TextView mTitle;

        @BindView(R.id.item_chart_analysis_ratio)
        public TextView mRadio;

        @BindView(R.id.item_chart_analysis_balance)
        public TextView mBalance;

        public InnerHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        @SuppressLint("SetTextI18n")
        public void setData(BudgetInfo budgetInfo) {
            Glide.with(itemView.getContext()).load(budgetInfo.getBudgetIcon()).into(mIcon);
            mTitle.setText(budgetInfo.getBudgetTitle());
            mRadio.setText(new DecimalFormat("0.0").format(mTotalPrice==0?0:budgetInfo.getBudgetBalance()/mTotalPrice*100)+"%");
            mBalance.setText(new DecimalFormat("0.00").format(budgetInfo.getBudgetBalance()));
        }
    }
}
