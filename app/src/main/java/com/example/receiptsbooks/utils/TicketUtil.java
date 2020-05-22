package com.example.receiptsbooks.utils;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.example.receiptsbooks.model.domain.IBaseInfo;
import com.example.receiptsbooks.presenter.ITicketPresenter;
import com.example.receiptsbooks.ui.activity.TicketActivity;

public class TicketUtil {

    public static void toTicketPage(Context context, IBaseInfo baseInfo){
        //处理数据
        String title = baseInfo.getTitle();
        //商品的详情地址
        String url = baseInfo.getUrl();
        if (TextUtils.isEmpty(url)) {
            url = baseInfo.getUrl();
        }
        String cover = baseInfo.getCover();
        //拿到ticketPresenter去加载数据
        ITicketPresenter ticketPresenter = PresenterManager.getInstance().getTicketPresenter();
        ticketPresenter.getTicket(title, url, cover);

        //因为Application的生命周期较长，所以不会造成内存泄漏的问题
        //但是这里因为要启动startActivity，所以需要外部传进来context
        context.startActivity(new Intent(context, TicketActivity.class));
    }
}
