package com.example.receiptsbooks.presenter.impl;

import com.example.receiptsbooks.model.Api;
import com.example.receiptsbooks.model.domain.StorePagerContent;
import com.example.receiptsbooks.presenter.IStoreCategoryPagerPresenter;
import com.example.receiptsbooks.utils.LogUtils;
import com.example.receiptsbooks.utils.RetrofitManager;
import com.example.receiptsbooks.utils.UrlUtils;
import com.example.receiptsbooks.view.IStoreCategoryPagerCallback;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class StoreCategoryPagerPresenterImpl implements IStoreCategoryPagerPresenter {

    //用来保存page页数得集合
    private Map<Integer, Integer> pagesInfo = new HashMap<>();

    private static final int DEFAULT_PAGE = 1;
    private Integer mCurrentPage;

    @Override
    public void getContentByCategoryId(int categoryId) {
        //一进来就显示在加载中
        for (IStoreCategoryPagerCallback callback : callbacks) {
            if (callback.getCategoryId() == categoryId) {
                callback.onLoading();
            }
        }
        //根据分类id去加载内容
        Integer targetPage = pagesInfo.get(categoryId);
        if (targetPage == null) {
            targetPage = DEFAULT_PAGE;
            pagesInfo.put(categoryId, targetPage);
        }
        //这个targetPage是当前pager的加载页，加载更多时，是去获得第二页...
        LogUtils.d(this,"targetPage ==> " + targetPage);
        Call<StorePagerContent> task = createTask(categoryId, targetPage);
        task.enqueue(new Callback<StorePagerContent>() {
            @Override
            public void onResponse(Call<StorePagerContent> call, Response<StorePagerContent> response) {
                int code = response.code();
                LogUtils.d(StoreCategoryPagerPresenterImpl.this, "code ==> " + code);
                if (code == HttpURLConnection.HTTP_OK) {
                    StorePagerContent pagerContent = response.body();
                    //LogUtils.d(CategoryPagerPresenterImpl.this, "pageContent ==> " + pagerContent);
                    //更新UI
                    handleStorePageContentResult(pagerContent, categoryId);
                } else {
                    //网络错误
                    handleNetworkError(categoryId);
                }
            }

            @Override
            public void onFailure(Call<StorePagerContent> call, Throwable t) {
                LogUtils.d(StoreCategoryPagerPresenterImpl.this, "onFailure ==> " + t.toString());
            }
        });
    }

    private Call<StorePagerContent> createTask(int categoryId, Integer targetPage) {
        String homePagerUrl = UrlUtils.createHomePagerUrl(categoryId, targetPage);
        LogUtils.d(this, "home pager url ==> " + homePagerUrl);
        Retrofit retrofit = RetrofitManager.getInstance().getSOBRetrofit();
        Api api = retrofit.create(Api.class);
        return api.getHomePagerContent(homePagerUrl);
    }

    private void handleNetworkError(int categoryId) {
        for (IStoreCategoryPagerCallback callback : callbacks) {
            if (callback.getCategoryId() == categoryId) {
                callback.onNetworkError();
            }
        }

    }

    private void handleStorePageContentResult(StorePagerContent pagerContent, int categoryId) {
        List<StorePagerContent.DataBean> data = pagerContent.getData();
        //通知UI层更新数据
        for (IStoreCategoryPagerCallback callback : callbacks) {
            if (callback.getCategoryId() == categoryId) {
                if (pagerContent == null || pagerContent.getData().size() == 0) {
                    callback.onEmpty();
                } else {
                    //最后的那5个数据
                    List<StorePagerContent.DataBean> looperData = data.subList(data.size() - 5, data.size());
                    callback.onLooperListLoaded(looperData);
                    callback.onContentLoaded(data);
                }
            }
        }
    }

    @Override
    public void loaderMore(int categoryId) {
        //加载更多数据
        //1、拿到当前页码
        mCurrentPage = pagesInfo.get(categoryId);
        if (pagesInfo == null) {
            mCurrentPage = 1;
        }
        //2、页码++
        mCurrentPage++;
        //3、加载数据
        Call<StorePagerContent> task = createTask(categoryId, mCurrentPage);
        //4、处理数据结果
        task.enqueue(new Callback<StorePagerContent>() {
            @Override
            public void onResponse(Call<StorePagerContent> call, Response<StorePagerContent> response) {
                //结果
                int code = response.code();
                LogUtils.d(StoreCategoryPagerPresenterImpl.this, "result code ==> " + code);
                if (code == HttpURLConnection.HTTP_OK) {
                    StorePagerContent result = response.body();
                    //LogUtils.d(CategoryPagerPresenterImpl.this,result.toString());
                    handleLoaderResult(result, categoryId);
                } else {
                    handleLoaderMoreError(categoryId);
                }
            }

            @Override
            public void onFailure(Call<StorePagerContent> call, Throwable t) {
                //请求失败
                LogUtils.d(StoreCategoryPagerPresenterImpl.this, t.toString());
                handleLoaderMoreError(categoryId);
            }
        });
    }

    private void handleLoaderResult(StorePagerContent result, int categoryId) {
        for (IStoreCategoryPagerCallback callback : callbacks) {
            if (callback.getCategoryId() == categoryId) {
                if (result == null || result.getData().size() == 0) {
                    callback.onLoaderMoreEmpty();
                }else{
                    callback.onLoaderMoreLoaded(result.getData());
                }
            }
        }
    }

    private void handleLoaderMoreError(int categoryId) {
        mCurrentPage--;
        pagesInfo.put(mCurrentPage, mCurrentPage);
        for (IStoreCategoryPagerCallback callback : callbacks) {
            if (callback.getCategoryId() == categoryId) {
                callback.onLoaderMoreError();
            }
        }
    }

    @Override
    public void reload(int categoryId) {

    }

    private ArrayList<IStoreCategoryPagerCallback> callbacks = new ArrayList<>();

    @Override
    public void registerViewCallback(IStoreCategoryPagerCallback callback) {
        if (!callbacks.contains(callback)) {
            callbacks.add(callback);
        }
    }

    @Override
    public void unregisterViewCallback(IStoreCategoryPagerCallback callback) {
        callbacks.remove(callback);
    }
}
