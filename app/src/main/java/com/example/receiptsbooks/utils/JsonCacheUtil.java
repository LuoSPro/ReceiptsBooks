package com.example.receiptsbooks.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.receiptsbooks.base.BaseApplication;
import com.example.receiptsbooks.model.domain.CacheWithDuration;
import com.google.gson.Gson;

public class JsonCacheUtil {

    private static final String JSON_CACHE_SP_NAME = "json_cache_sp_name";
    private final SharedPreferences mSharedPreferences;
    private final Gson mGson;

    private JsonCacheUtil() {
        mSharedPreferences = BaseApplication.getAppContext().getSharedPreferences(JSON_CACHE_SP_NAME, Context.MODE_PRIVATE);
        mGson = new Gson();
    }

    public void saveCache(String key, Object value) {
        this.saveCache(key, value, -1);
    }

    public void saveCache(String key, Object value, long duration) {
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        //toJson是把字符串转换成json格式
        String valueStr = mGson.toJson(value);
        if (duration != -1L) {
            //当前时间
            duration += System.currentTimeMillis();
        }
        //保存一个有数据有时间的内容
        CacheWithDuration cacheWithDuration = new CacheWithDuration(duration, valueStr);
        String cacheWithTime = mGson.toJson(cacheWithDuration);
        edit.putString(key, cacheWithTime);
        edit.apply();
    }

    public void delCache(String key) {
        mSharedPreferences.edit().remove(key).apply();

    }

    //注：如果这里定义泛型的时候，用的T extends Class,那么T只能接收继承了Class的类
    public <T> T getValue(String key,Class<T> clazz) {
        String valueWidthDuration = mSharedPreferences.getString(key, null);
        if (valueWidthDuration == null) {
            return null;
        }
        //将json转换成字符串
        CacheWithDuration cacheWithDuration = mGson.fromJson(valueWidthDuration, CacheWithDuration.class);
        //对时间进行判断
        long duration = cacheWithDuration.getDuration();
        //注：当前的时间肯定会比保存数据的时间后面，所以如果保存的时候用的临时时间，那么这里恒小于0
        //判断是否过期了
        if (duration != -1 && duration - System.currentTimeMillis() <= 0) {
            //过期了
            return null;
        }else {
            //没过期
            String cache = cacheWithDuration.getCache();
            T result = mGson.fromJson(cache, clazz);
            return result;
        }
    }

    private static JsonCacheUtil sJsonCacheUtil = null;

    public static JsonCacheUtil getInstance() {
        if (sJsonCacheUtil == null) {//懒汉模式
            sJsonCacheUtil = new JsonCacheUtil();
        }
        return sJsonCacheUtil;
    }
}
