package com.example.receiptsbooks.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.receiptsbooks.R;
import com.example.receiptsbooks.model.domain.BudgetInfo;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BudgetContentAdapter extends RecyclerView.Adapter<BudgetContentAdapter.InnerHolder> {

    private List<BudgetInfo> mBudgetItems = new ArrayList<>();
    private OnBudgetItemClickListener mBudgetListener;
    private Context mContext;

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_budget_center_list, parent, false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
        BudgetInfo budgetItem = mBudgetItems.get(position);
        holder.setData(budgetItem);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBudgetListener.onBudgetClick(budgetItem.getBudgetTitle()+"-预算",position,budgetItem.getBudgetMoney()+"");
            }
        });
    }

    @Override
    public int getItemCount() {
        return mBudgetItems.size();
    }

    public void setData(List<BudgetInfo> budgetItems,Context context){
        this.mContext = context;
        this.mBudgetItems.clear();
        this.mBudgetItems.addAll(budgetItems);
        notifyDataSetChanged();
    }

    public List<BudgetInfo> getData(){
        return mBudgetItems;
    }

    public double getAllBudgetMoney(){
        double totalBudget = 0;
        for (int i = 0; i < mBudgetItems.size(); i++) {
            totalBudget += mBudgetItems.get(i).getBudgetMoney();
        }
        return totalBudget;
    }

    public double getAllBudgetBalance(){
        double totalBalance = 0;
        for (int i = 0; i < mBudgetItems.size(); i++) {
            totalBalance += mBudgetItems.get(i).getBudgetBalance();
        }
        return totalBalance;
    }

    public void updateBudgetItem(double budgetPrice, int position) {
        BudgetInfo budgetInfo = mBudgetItems.get(position);
        budgetInfo.setBudgetMoney(budgetPrice);
        if (budgetInfo.getBudgetProgress()<0){
            //说明预算不够
            budgetInfo.setBudgetStatus(BudgetInfo.BudgetStatus.OVERSPEND);
        }else{
            budgetInfo.setBudgetStatus(BudgetInfo.BudgetStatus.BALANCE);
        }
        budgetInfo.setSetting(true);
        notifyItemChanged(mBudgetItems.indexOf(budgetInfo));
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

        @SuppressLint("SetTextI18n")
        public void setData(BudgetInfo budgetItem) {
            mBudgetTitleTv.setText(budgetItem.getBudgetTitle());
            if (budgetItem.isSetting()){
                mBudgetMoneyTv.setText(new DecimalFormat("0.00").format(budgetItem.getBudgetMoney()));
            }else{
                //如果不在这里加这个，如果滑动到未设置到的item，那么他会去复用之前的item的信息，造成重复
                mBudgetMoneyTv.setText("未设置");
            }
            mBudgetIconIv.setImageResource(budgetItem.getBudgetIcon());
            if (budgetItem.getBudgetProgress()<0){
                mBudgetProgressPb.setProgress(5);
                budgetItem.setBudgetStatus(BudgetInfo.BudgetStatus.OVERSPEND);
                mBudgetProgressPb.setProgressDrawable(mContext.getDrawable(R.drawable.custom_progress_bg_excess));
                mBudgetBalanceTv.setText(new DecimalFormat("0.00").format(budgetItem.getBudgetBalance()-budgetItem.getBudgetMoney()));
            }else if (budgetItem.getBudgetProgress()>0){
                budgetItem.setBudgetStatus(BudgetInfo.BudgetStatus.BALANCE);
                mBudgetProgressPb.setProgress(budgetItem.getBudgetProgress());
                mBudgetBalanceTv.setText(new DecimalFormat("0.00").format(budgetItem.getBudgetMoney()-budgetItem.getBudgetBalance()));
            }else{
                budgetItem.setBudgetStatus(BudgetInfo.BudgetStatus.EXPEND);
                mBudgetProgressPb.setProgress(budgetItem.getBudgetProgress());
                mBudgetBalanceTv.setText(new DecimalFormat("0.00").format(budgetItem.getBudgetBalance()));
            }
            //前面先设置，后面再展示
            mBudgetStatusTv.setText(budgetItem.getBudgetStatus());
        }
    }

    public void setOnBudgetItemClickListener(OnBudgetItemClickListener listener){
        this.mBudgetListener = listener;
    }

    public interface OnBudgetItemClickListener{
        void onBudgetClick(String title, int position, String s);
    }
}
