package com.example.receiptsbooks.presenter.impl;

import com.example.receiptsbooks.model.Api;
import com.example.receiptsbooks.model.domain.Histories;
import com.example.receiptsbooks.model.domain.SearchRecommend;
import com.example.receiptsbooks.model.domain.SearchResult;
import com.example.receiptsbooks.presenter.ISearchPresenter;
import com.example.receiptsbooks.utils.JsonCacheUtil;
import com.example.receiptsbooks.utils.LogUtils;
import com.example.receiptsbooks.utils.RetrofitManager;
import com.example.receiptsbooks.view.ISearchPageCallback;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SearchPresenterImpl implements ISearchPresenter {

    private final Api mApi;
    private ISearchPageCallback mSearchPageCallback = null;
    private static final int DEFAULT_PAGE = 0;
    /**
     * 搜索的当前页
     */
    private int mCurrentPage = DEFAULT_PAGE;
    private String mCurrentKeyword = null;
    private final JsonCacheUtil mJsonCacheUtil;

    public SearchPresenterImpl() {
        RetrofitManager instance = RetrofitManager.getInstance();
        Retrofit retrofit = instance.getSOBRetrofit();
        mApi = retrofit.create(Api.class);
        mJsonCacheUtil = JsonCacheUtil.getInstance();
    }

    @Override
    public void getHistory() {
        Histories histories = mJsonCacheUtil.getValue(KEY_HISTORIES, Histories.class);
        if (mSearchPageCallback != null) {
            mSearchPageCallback.onHistoriesLoaded(histories);
        }
    }

    @Override
    public void delHistory() {
        mJsonCacheUtil.delCache(KEY_HISTORIES);
        //删完之后回调
        mSearchPageCallback.onHistoriesDeleted();
    }

    private static final String KEY_HISTORIES = "ket_histories";

    private static final int DEFAULT_HISTORY_SIZE = 10;
    private int mHistoriesMaxSize = DEFAULT_HISTORY_SIZE;

    /**
     * 添加历史记录
     *
     * @param history
     */
    private void saveHistory(String history) {
        Histories histories = mJsonCacheUtil.getValue(KEY_HISTORIES, Histories.class);
        //如果说已经在了，就干掉，然后再添加
        List<String> historiesList = null;
        if (histories != null && histories.getHistories() != null) {
            historiesList = histories.getHistories();
            if (historiesList.contains(history)) {
                historiesList.remove(history);
            }
        }
        //去重完成
        //处理没有数据的情况
        if (historiesList == null) {
            historiesList = new ArrayList<>();
        }
        //重新设置新的List
        if (histories == null) {
            histories = new Histories();
            histories.setHistories(historiesList);
        }
        //对个数进行限制
        if (historiesList.size() > mHistoriesMaxSize) {
            historiesList = historiesList.subList(0, mHistoriesMaxSize);
        }
        //添加记录
        historiesList.add(history);
        //保存记录（这里保存的是histories，而不是histories里面的List，否则取数据的时候会报错）
        mJsonCacheUtil.saveCache(KEY_HISTORIES, histories);
    }

    @Override
    public void doSearch(String keyword) {
        if (mCurrentKeyword == null || !mCurrentKeyword.equals(keyword)) {
            //如果搜索词没有重复，那么就保存
            this.saveHistory(keyword);
            this.mCurrentKeyword = keyword;
        }
        //更新UI状态
        if (mSearchPageCallback != null) {
            mSearchPageCallback.onLoading();
        }
        Call<SearchResult> task = mApi.doSearch(mCurrentPage, keyword);
        task.enqueue(new Callback<SearchResult>() {
            @Override
            public void onResponse(Call<SearchResult> call, Response<SearchResult> response) {
                int code = response.code();
                LogUtils.d(SearchPresenterImpl.this, "doSearch code is ==> " + code);
                if (code == HttpURLConnection.HTTP_OK) {
                    handleSearchResult(response.body());
                } else {
                    onError();
                }
            }

            @Override
            public void onFailure(Call<SearchResult> call, Throwable t) {
                t.printStackTrace();
                onError();
            }
        });
    }

    private void onError() {
        if (mSearchPageCallback != null) {
            mSearchPageCallback.onNetworkError();
        }
    }

    private void handleSearchResult(SearchResult result) {
        if (mSearchPageCallback != null) {
            if (!isResultEmpty(result)) {
                mSearchPageCallback.onSearchSuccess(result);
            } else {
                //数据为空
                mSearchPageCallback.onEmpty();
            }
        }
    }

    private boolean isResultEmpty(SearchResult result) {
        try {
            return result == null || result.getData().getTbk_dg_material_optional_response().getResult_list().getMap_data().size() == 0;
        } catch (Exception e) {
            return true;
        }
    }

    @Override
    public void research() {
        if (mCurrentKeyword == null) {
            if (mSearchPageCallback != null) {
                mSearchPageCallback.onEmpty();
            }
        } else {
            //可以搜索
            this.doSearch(mCurrentKeyword);
        }
    }

    @Override
    public void loaderMore() {
        mCurrentPage++;
        if (mCurrentKeyword == null) {
            if (mSearchPageCallback != null) {
                mSearchPageCallback.onMoreLoadedEmpty();
            }
        }else {
            doSearchMore();
        }
    }

    private void doSearchMore() {
        Call<SearchResult> task = mApi.doSearch(mCurrentPage, mCurrentKeyword);
        task.enqueue(new Callback<SearchResult>() {
            @Override
            public void onResponse(Call<SearchResult> call, Response<SearchResult> response) {
                int code = response.code();
                LogUtils.d(SearchPresenterImpl.this, "doSearch code is ==> " + code);
                if (code == HttpURLConnection.HTTP_OK) {
                    handleMoreSearchResult(response.body());
                } else {
                    onLoadedMoreError();
                }
            }

            @Override
            public void onFailure(Call<SearchResult> call, Throwable t) {
                t.printStackTrace();
                onLoadedMoreError();
            }
        });

    }

    /**
     * 处理加载更多的结果
     *
     * @param result
     */
    private void handleMoreSearchResult(SearchResult result) {
        if (mSearchPageCallback != null) {
            if (isResultEmpty(result)) {
                //数据为空
                mSearchPageCallback.onMoreLoadedError();
            } else {
                mSearchPageCallback.onMoreLoaded(result);
            }
        }
    }

    /**
     * 记载更多内容失败
     */
    private void onLoadedMoreError() {
        mCurrentPage--;
        if (mSearchPageCallback != null) {
            mSearchPageCallback.onMoreLoadedError();
        }
    }

    @Override
    public void getRecommendWords() {
        Call<SearchRecommend> task = mApi.getRecommendWords();
        task.enqueue(new Callback<SearchRecommend>() {
            @Override
            public void onResponse(Call<SearchRecommend> call, Response<SearchRecommend> response) {
                int code = response.code();
                LogUtils.d(SearchPresenterImpl.this, "getRecommendWords code is ==> " + code);
                if (code == HttpURLConnection.HTTP_OK) {
                    //处理结果
                    if (mSearchPageCallback != null) {
                        mSearchPageCallback.onRecommendWordsLoaded(response.body().getData());
                    }
                }
            }

            @Override
            public void onFailure(Call<SearchRecommend> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    public void registerViewCallback(ISearchPageCallback callback) {
        this.mSearchPageCallback = callback;
    }

    @Override
    public void unregisterViewCallback(ISearchPageCallback callback) {
        if (mSearchPageCallback != null) {
            this.mSearchPageCallback = null;
        }
    }
}
