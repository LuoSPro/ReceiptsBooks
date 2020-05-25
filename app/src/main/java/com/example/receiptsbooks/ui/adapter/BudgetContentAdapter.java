package com.example.receiptsbooks.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.receiptsbooks.R;
import com.example.receiptsbooks.model.domain.BudgetItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BudgetContentAdapter extends RecyclerView.Adapter<BudgetContentAdapter.InnerHolder> {

    private List<BudgetItem> mBudgetItems = new ArrayList<>();
    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_budget_center_list, parent, false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
        BudgetItem budgetItem = mBudgetItems.get(position);
        holder.mBudgetTitleTv.setText(budgetItem.getBudgetTitle());
        holder.mBudgetBalanceTv.setText(""+budgetItem.getBudgetBalance());
        holder.mBudgetStatusTv.setText(budgetItem.getBudgetStatus());
        holder.mBudgetMoneyTv.setText(""+budgetItem.getBudgetMoney());
        holder.mBudgetIconIv.setImageResource(budgetItem.getBudgetIcon());
        holder.mBudgetProgressPb.setProgress(budgetItem.getBudgetProgress());
    }

    @Override
    public int getItemCount() {
        return mBudgetItems.size();
    }

    public void setData(List<BudgetItem> budgetItems){
        this.mBudgetItems.clear();
        this.mBudgetItems.addAll(budgetItems);
        notifyDataSetChanged();
    }

    public class InnerHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_budget_center_tv_title)
        public TextView mBudgetTitleTv;

        @BindView(R.id.item_budget_center_tv_money)
        public TextView mBudgetMoneyTv;

        @BindView(R.id.item_budget_center_tv_balance)
        public TextView mBudgetBalanceTv;

        @BindView(R.id.item_budget_center_tv_status)
        public TextView mBudgetStatusTv;

        @BindView(R.id.item_budget_center_pb_progress)
        public ProgressBar mBudgetProgressPb;

        @BindView(R.id.item_budget_center_iv_icon)
        public ImageView mBudgetIconIv;

        public InnerHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
