package com.example.receiptsbooks.utils;

import com.example.receiptsbooks.presenter.IBudgetCenterPresenter;
import com.example.receiptsbooks.presenter.IChartAnalysisPresenter;
import com.example.receiptsbooks.presenter.IListCategoryPagerPresenter;
import com.example.receiptsbooks.presenter.IListPresenter;
import com.example.receiptsbooks.presenter.IReceiptDetailsPresenter;
import com.example.receiptsbooks.presenter.IReceiptInfoPresenter;
import com.example.receiptsbooks.presenter.ISearchPresenter;
import com.example.receiptsbooks.presenter.IStoreCategoryPagerPresenter;
import com.example.receiptsbooks.presenter.IStorePresenter;
import com.example.receiptsbooks.presenter.ITicketPresenter;
import com.example.receiptsbooks.presenter.impl.BudgetCenterPresenterImpl;
import com.example.receiptsbooks.presenter.impl.ChartAnalysisPresenterImpl;
import com.example.receiptsbooks.presenter.impl.HistoriesPresenterImpl;
import com.example.receiptsbooks.presenter.impl.HomePresenterImpl;
import com.example.receiptsbooks.presenter.impl.ListCategoryPagerPresenter;
import com.example.receiptsbooks.presenter.impl.ListPresenterImpl;
import com.example.receiptsbooks.presenter.impl.ReceiptDetailsPresenterImpl;
import com.example.receiptsbooks.presenter.impl.ReceiptInfoPresenterImpl;
import com.example.receiptsbooks.presenter.impl.SearchPresenterImpl;
import com.example.receiptsbooks.presenter.impl.StoreCategoryPagerPresenterImpl;
import com.example.receiptsbooks.presenter.impl.StorePresenterImpl;
import com.example.receiptsbooks.presenter.impl.TicketPresenterImpl;

public class PresenterManager {
    private static final PresenterManager ourInstance = new PresenterManager();
    private final IStoreCategoryPagerPresenter mStoreCategoryPagerPresenter;
    private final IStorePresenter mStorePresenter;
    private final ITicketPresenter mTicketPresenter;
    private final ISearchPresenter mSearchPresenter;
    private final IListPresenter mListPresenter;
    private final IListCategoryPagerPresenter mListCategoryPagerPresenter;
    private final IReceiptDetailsPresenter mReceiptInfoDetailsPresenter;
    private final HomePresenterImpl mHomePresenter;
    private final IReceiptInfoPresenter mReceiptInfoPresenter;
    private final HistoriesPresenterImpl mHistoriesPresenter;
    private final IBudgetCenterPresenter mBudgetCenterPresenter;
    private final IChartAnalysisPresenter mChartAnalysisPresenter;

    public static PresenterManager getInstance() {
        return ourInstance;
    }

    private PresenterManager() {
        mHomePresenter = new HomePresenterImpl();
        mStoreCategoryPagerPresenter = new StoreCategoryPagerPresenterImpl();
        mStorePresenter = new StorePresenterImpl();
        mTicketPresenter = new TicketPresenterImpl();
        mSearchPresenter = new SearchPresenterImpl();
        mListPresenter = new ListPresenterImpl();
        mListCategoryPagerPresenter = new ListCategoryPagerPresenter();
        mReceiptInfoDetailsPresenter = new ReceiptDetailsPresenterImpl();
        mReceiptInfoPresenter = new ReceiptInfoPresenterImpl();
        mHistoriesPresenter = new HistoriesPresenterImpl();
        mBudgetCenterPresenter = new BudgetCenterPresenterImpl();
        mChartAnalysisPresenter = new ChartAnalysisPresenterImpl();
    }

    public IChartAnalysisPresenter getChartAnalysisPresenter() {
        return mChartAnalysisPresenter;
    }

    public IBudgetCenterPresenter getBudgetCenterPresenter() {
        return mBudgetCenterPresenter;
    }

    public HistoriesPresenterImpl getHistoriesPresenter() {
        return mHistoriesPresenter;
    }

    public IReceiptInfoPresenter getReceiptInfoPresenter() {
        return mReceiptInfoPresenter;
    }

    public HomePresenterImpl getHomePresenter() {
        return mHomePresenter;
    }

    public IReceiptDetailsPresenter getReceiptDetailsPresenter() {
        return mReceiptInfoDetailsPresenter;
    }

    public IListCategoryPagerPresenter getListCategoryPagerPresenter() {
        return mListCategoryPagerPresenter;
    }

    public IListPresenter getListPresenter() {
        return mListPresenter;
    }

    public ISearchPresenter getSearchPresenter() {
        return mSearchPresenter;
    }

    public ITicketPresenter getTicketPresenter() {
        return mTicketPresenter;
    }

    public IStoreCategoryPagerPresenter getStoreCategoryPagerPresenter() {
        return mStoreCategoryPagerPresenter;
    }

    public IStorePresenter getStorePresenter() {
        return mStorePresenter;
    }
}
