package com.example.receiptsbooks.model.domain;

public interface IBaseInfo {
    /**
     * 商品的封面图片
     * @return
     */
    String getCover();

    /**
     * 商品的标题
     * @return
     */
    String getTitle();

    /**
     * 商品的URL
     * @return
     */
    String getUrl();
}
