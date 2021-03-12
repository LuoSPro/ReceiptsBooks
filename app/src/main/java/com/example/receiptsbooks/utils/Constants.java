package com.example.receiptsbooks.utils;

import java.util.Arrays;
import java.util.List;

/**
 * 同于保存常量
 */
public class Constants {
    //base url
    public static final String BASE_RECEIPT_INFO_URL = "http://192.168.137.254:8082/";
    public static final String BASE_SOB_URL = "https://api.sunofbeach.net/shop/";
    public static final String KEY_RECEIPT_PHOTO = "receiptPhoto";
    //home pager fragment bundle key
    public static final String KEY_STORE_PAGER_TITLE = "key_store_pager_title";
    public static final String KEY_STORE_PAGER_ID = "key_store_pager_id";
    //list pager fragment bundle key
    public static final String KEY_LIST_PAGER_CATEGORY = "product_type_category";

    //分类目录
    public static final List<String> PRODUCT_TYPE_LIST = Arrays.asList("服饰","鞋靴","箱包","母婴用品","电子产品","个人护理",
            "保健品","家用杂物","运动用品","乐器","娱乐","美食","生鲜","装饰品","宠物水族","农资","学习用品","汽车",
            "家居建材","家电办公","酒水饮料","烟草类","other");

    //传递receiptInfo对象
    public static final String KEY_RECEIPT_DETAILS_RECEIPT_INFO = "key_receipt_details_receipt_info";
    public static final String KEY_RECEIPT_DETAILS_PRODUCTS = "key_receipt_details_products";
    public static final String KEY_RECEIPT_DETAILS_RECEIPT_ID = "key_receipt_details_receipt_id";
    //标明点击这个界面的目的是增加账单
    public static final String KEY_RECEIPT_DETAILS_IS_ADD_ACCOUNT = "key_receipt_details_is_add_account";
    //标明是否是预览
    public static final String KEY_RECEIPT_INFO_IS_PREVIEW = "key_receipt_info_is_preview";
    //传递点击的item的ID
    public static final String KEY_MODIFY_ITEM_INDEX = "key_modify_item_index";
    public static final String KEY_MODIFY_SUCCESS_LISTENER = "key_modify_success_listener";
    //ReceiptInfoActivity到ReceiptDetailsActivity的requestCode
    public static final int INFO_TO_DETAILS_REQUEST_CODE = 10;
}
