package com.example.receiptsbooks.utils;

import android.widget.Toast;

import com.example.receiptsbooks.base.BaseApplication;

public class ToastUtil {

    private static Toast sToast;

    public static void showToast(String tips){
        if (sToast == null){
            sToast = Toast.makeText(BaseApplication.getAppContext(),tips,Toast.LENGTH_SHORT);
        }else{
            sToast.setText(tips);
        }
        sToast.show();

    }
}
