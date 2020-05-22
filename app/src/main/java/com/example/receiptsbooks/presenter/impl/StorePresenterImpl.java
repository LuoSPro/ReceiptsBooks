package com.example.receiptsbooks.presenter.impl;

import com.example.receiptsbooks.model.Api;
import com.example.receiptsbooks.model.domain.Categories;
import com.example.receiptsbooks.presenter.IStorePresenter;
import com.example.receiptsbooks.utils.LogUtils;
import com.example.receiptsbooks.utils.RetrofitManager;
import com.example.receiptsbooks.view.IStoreCallback;

import java.net.HttpURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class StorePresenterImpl implements IStorePresenter {
    private IStoreCallback mCallback = null;

    @Override
    public void getCategories() {
        if (mCallback != null) {
            mCallback.onLoading();
        }
        //加载分类数据
        Retrofit retrofit = RetrofitManager.getInstance().getSOBRetrofit();
        Api api = retrofit.create(Api.class);
        Call<Categories> task = api.getCategories();
        task.enqueue(new Callback<Categories>() {
            @Override
            public void onResponse(Call<Categories> call, Response<Categories> response) {
                //数据结果
                int code = response.code();
                LogUtils.d(HomePresenterImpl.class, "result code is " + code);
                if (code == HttpURLConnection.HTTP_OK) {
                    //请求成功
                    Categories categories = response.body();
                    if (mCallback != null) {
                        if (categories == null || categories.getData().size() == 0) {
                            mCallback.onEmpty();
                        } else {//成功
                            //这里是匿名内部类，所以这里的this，判断不出来当前为哪个class，所以应该用外部的类：HomePresenterImpl.this
                            //LogUtils.d(HomePresenterImpl.this,categories.toString());
                            mCallback.onCategoriesLoaded(categories);
                        }
                    }
                } else {
                    //请求失败
                    LogUtils.i(StorePresenterImpl.this, "请求失败。。。");
                    if (mCallback != null) {
                        mCallback.onNetworkError();
                    }
                }
            }

            @Override
            public void onFailure(Call<Categories> call, Throwable t) {
                //加载失败结果
                LogUtils.e(StorePresenterImpl.this, "请求错误。。。" + t);
                if (mCallback != null) {
                    mCallback.onNetworkError();
                }
            }
        });
    }

    @Override
    public void registerViewCallback(IStoreCallback callback) {
        this.mCallback = callback;
    }

    @Override
    public void unregisterViewCallback(IStoreCallback callback) {
        mCallback = null;
    }
}
