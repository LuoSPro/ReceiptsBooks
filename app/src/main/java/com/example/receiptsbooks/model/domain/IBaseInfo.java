package com.example.receiptsbooks.model.domain;

public interface IBaseInfo {
    /**
     * 商品的封面图片
     */
    String getCover();

    /**
     * 商品的标题
     */
    String getTitle();

    /**
     * 商品的URL
     */
    String getUrl();
}
