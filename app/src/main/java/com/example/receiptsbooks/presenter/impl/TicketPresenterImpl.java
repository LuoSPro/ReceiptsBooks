package com.example.receiptsbooks.presenter.impl;

import com.example.receiptsbooks.model.Api;
import com.example.receiptsbooks.model.domain.TicketParams;
import com.example.receiptsbooks.model.domain.TicketResult;
import com.example.receiptsbooks.presenter.ITicketPresenter;
import com.example.receiptsbooks.utils.LogUtils;
import com.example.receiptsbooks.utils.RetrofitManager;
import com.example.receiptsbooks.utils.UrlUtils;
import com.example.receiptsbooks.view.ITicketPagerCallback;

import java.net.HttpURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TicketPresenterImpl implements ITicketPresenter {
    private ITicketPagerCallback mViewCallback = null;
    private String mCover;
    private TicketResult mTicketResult;

    enum LoadState{
        LOADING,SUCCESS,ERROR,NONE
    }

    private LoadState mCurrentState = LoadState.NONE;

    @Override
    public void getTicket(String title, String url, String cover) {
        mCurrentState = LoadState.LOADING;
        this.onTicketLoading();
        this.mCover = UrlUtils.getCoverPath(cover);
        //LogUtils.d(this,"mCover ==> " + mCover);
        LogUtils.d(this,"title ==> " + title);
        LogUtils.d(this,"url ==> " + url);
        LogUtils.d(this,"cover ==> " + cover);
        //如果没加https，那么就得不到淘口令
        String targetUrl = UrlUtils.getTicketUrl(url);
        //T去获取淘口令
        Retrofit retrofit = RetrofitManager.getInstance().getSOBRetrofit();
        Api api = retrofit.create(Api.class);
        TicketParams ticketParams = new TicketParams(targetUrl,title);
        Call<TicketResult> task = api.getTicket(ticketParams);
        task.enqueue(new Callback<TicketResult>() {
            @Override
            public void onResponse(Call<TicketResult> call, Response<TicketResult> response) {
                int code = response.code();
                //LogUtils.d(TicketPresenterImpl.this,"code is ==> " + code);
                if (code == HttpURLConnection.HTTP_OK){
                    //请求成功
                    mTicketResult = response.body();
                    //LogUtils.d(TicketPresenterImpl.this,"ticketResult ==> "+ticketResult);
                    onTicketLoadSuccess();
                }else{
                    //请求失败
                    onLoadTicketError();
                    mCurrentState = LoadState.ERROR;
                }
            }

            @Override
            public void onFailure(Call<TicketResult> call, Throwable t) {
                //失败
                onLoadTicketError();
            }
        });

    }

    private void onTicketLoadSuccess() {
        if (mViewCallback != null) {
            //如果mViewCallback注册成功，那么直接加载数据
            mViewCallback.onTicketLoaded(mCover, mTicketResult);
        }else {
            //如果数据到达后，mViewCallback还没有注册成功，那么就表明当前的状态，等mViewCallback注册时，进行处理
            //通知UI更新
            mCurrentState = LoadState.SUCCESS;
        }
    }

    private void onLoadTicketError() {
        if (mViewCallback != null) {
            mViewCallback.onNetworkError();
        }else{
            mCurrentState = LoadState.ERROR;
        }
    }

    @Override
    public void registerViewCallback(ITicketPagerCallback callback) {
        //这句话应该放在最前面，因为后面的操作中会用到mViewCallback ，所以放在后面可能造成mViewCallback 一直为null，而完不成回调
        this.mViewCallback = callback;
        if (mCurrentState != LoadState.NONE){
            //说明状态已经改变了
            //跟新UI
            if (mCurrentState == LoadState.SUCCESS){
                onTicketLoadSuccess();
            }else if (mCurrentState == LoadState.ERROR){
                onLoadTicketError();
            }else if (mCurrentState == LoadState.LOADING){
                onTicketLoading();
            }
        }
    }

    private void onTicketLoading() {
        if (mViewCallback != null) {
            mViewCallback.onLoading();
        }else{
            mCurrentState = LoadState.LOADING;
        }
    }

    @Override
    public void unregisterViewCallback(ITicketPagerCallback callback) {
        mViewCallback = null;
    }
}
