package com.example.receiptsbooks.utils;

import android.content.Context;

import com.example.receiptsbooks.base.BaseApplication;
import com.example.receiptsbooks.model.domain.ProductType;

import java.util.ArrayList;
import java.util.List;

public class ProductTypeUtils {
    private static ProductTypeUtils ourInstance;
    private List<ProductType> mProductTypes = new ArrayList<>();

    public static ProductTypeUtils getInstance() {
        if (ourInstance == null){
            ourInstance = new ProductTypeUtils(BaseApplication.getAppContext());
        }
        return ourInstance;
    }

    private ProductTypeUtils(Context context) {
        List<String> productTypeString = Constants.PRODUCT_TYPE_LIST;
        List<Integer> productTypeIcon = new ArrayList<>();
        String packageName = context.getPackageName();
        String resType = "mipmap";
        productTypeIcon.add(context.getResources().getIdentifier("costume_icon",resType,packageName));
        productTypeIcon.add(context.getResources().getIdentifier("boot_icon",resType,packageName));
        productTypeIcon.add(context.getResources().getIdentifier("luggage_icon",resType,packageName));
        productTypeIcon.add(context.getResources().getIdentifier("infant_icon",resType,packageName));
        productTypeIcon.add(context.getResources().getIdentifier("electronic_icon",resType,packageName));
        productTypeIcon.add(context.getResources().getIdentifier("personal_care_icon",resType,packageName));
        productTypeIcon.add(context.getResources().getIdentifier("health_care_icon",resType,packageName));
        productTypeIcon.add(context.getResources().getIdentifier("domestic_icon",resType,packageName));
        productTypeIcon.add(context.getResources().getIdentifier("exercise_icon",resType,packageName));
        productTypeIcon.add(context.getResources().getIdentifier("musical_instruments_icon",resType,packageName));
        productTypeIcon.add(context.getResources().getIdentifier("entertainment_icon",resType,packageName));
        productTypeIcon.add(context.getResources().getIdentifier("cate_icon",resType,packageName));
        productTypeIcon.add(context.getResources().getIdentifier("fresh_icon",resType,packageName));
        productTypeIcon.add(context.getResources().getIdentifier("decoration_icon",resType,packageName));
        productTypeIcon.add(context.getResources().getIdentifier("pet_icon",resType,packageName));
        productTypeIcon.add(context.getResources().getIdentifier("agriculture_icon",resType,packageName));
        productTypeIcon.add(context.getResources().getIdentifier("study_tool_icon",resType,packageName));
        productTypeIcon.add(context.getResources().getIdentifier("car_icon",resType,packageName));
        productTypeIcon.add(context.getResources().getIdentifier("home_build_icon",resType,packageName));
        productTypeIcon.add(context.getResources().getIdentifier("office_icon",resType,packageName));
        productTypeIcon.add(context.getResources().getIdentifier("drinks_icon",resType,packageName));
        productTypeIcon.add(context.getResources().getIdentifier("cigarette_icon",resType,packageName));
        productTypeIcon.add(context.getResources().getIdentifier("other_icon",resType,packageName));
        for (int i = 0; i < productTypeString.size(); i++) {
            mProductTypes.add(new ProductType(productTypeString.get(i),productTypeIcon.get(i)));
        }
    }

    public List<ProductType> getProductTypes() {
        return mProductTypes;
    }
}
