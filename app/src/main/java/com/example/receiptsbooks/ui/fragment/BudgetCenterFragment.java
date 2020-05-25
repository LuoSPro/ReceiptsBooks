package com.example.receiptsbooks.ui.fragment;

import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.receiptsbooks.R;
import com.example.receiptsbooks.base.BaseFragment;
import com.example.receiptsbooks.base.State;
import com.example.receiptsbooks.model.domain.BudgetItem;
import com.example.receiptsbooks.ui.adapter.BudgetContentAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class BudgetCenterFragment extends BaseFragment {

    @BindView(R.id.budget_center_rv_budget_list)
    public RecyclerView mBudgetList;
    private BudgetContentAdapter mBudgetAdapter;

    @Override
    protected void initView(View rootView) {
        setUpState(State.SUCCESS);
        mBudgetList.setLayoutManager(new LinearLayoutManager(getContext()));
        //创建适配器
        mBudgetAdapter = new BudgetContentAdapter();
        //设置适配器
        mBudgetList.setAdapter(mBudgetAdapter);
        //设置数据

    }

    @Override
    protected void loadData() {
        BudgetItem item = new BudgetItem();
        item.setBudgetBalance(123.21);
        item.setBudgetMoney(200.0);
        item.setBudgetStatus(BudgetItem.BudgetStatus.SPEND);
        item.setBudgetTitle("美食");
        item.setBudgetIcon(R.mipmap.cate_icon);
        List<BudgetItem> budgetItems = new ArrayList<>();
        budgetItems.add(item);
        mBudgetAdapter.setData(budgetItems);
    }

    @Override
    protected int getRootViewResId() {
        return R.layout.fragment_budget_center;
    }
}
