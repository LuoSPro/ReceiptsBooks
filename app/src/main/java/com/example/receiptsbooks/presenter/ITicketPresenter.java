package com.example.receiptsbooks.presenter;

import com.example.receiptsbooks.base.IBasePresenter;
import com.example.receiptsbooks.view.ITicketPagerCallback;

public interface ITicketPresenter extends IBasePresenter<ITicketPagerCallback> {

    /**
     * 生成淘口令
     *
     * @param title
     * @param url
     * @param cover
     */
    void getTicket(String title, String url, String cover);
}
