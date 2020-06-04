package com.example.receiptsbooks.model.domain;

public interface ILinearItemInfo extends IBaseInfo {
    /**
     * 获取原价
     */
    String getFinalPrice();

    /**
     * 获取优惠价格
     */
    long getCouponAmount();

    /**
     * 获取销量
     */
    long getVolume();
}
