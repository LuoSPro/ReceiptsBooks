package com.example.receiptsbooks.utils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitManager {

    private static final RetrofitManager ourInstance = new RetrofitManager();
    private Retrofit mReceiptInfoRetrofit;
    private Retrofit mSOBRetrofit;

    private static OkHttpClient client = null;

    public static RetrofitManager getInstance() {
        return ourInstance;
    }

    private RetrofitManager() {
        //这是我自己加的，为了使网络加载等待时间变长，以免响应太久，报错
        client = new OkHttpClient.Builder().
                connectTimeout(30, TimeUnit.SECONDS).
                readTimeout(30, TimeUnit.SECONDS).
                writeTimeout(30, TimeUnit.SECONDS).build();

        //创建ReceiptInfoRetrofit
        mReceiptInfoRetrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_RECEIPT_INFO_URL)
                .addConverterFactory(GsonConverterFactory.create())//实现自动把Json转换成对象
                .client(client)
                .build();

        //创建ReceiptInfoRetrofit
        mSOBRetrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_SOB_URL)
                .addConverterFactory(GsonConverterFactory.create())//实现自动把Json转换成对象
                .client(client)
                .build();
    }

    public Retrofit getReceiptInfoRetrofit() {
        return mReceiptInfoRetrofit;
    }

    public Retrofit getSOBRetrofit() {
        return mSOBRetrofit;
    }
}
