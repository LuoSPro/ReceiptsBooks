package com.example.receiptsbooks.view;

import com.example.receiptsbooks.base.IBaseCallback;
import com.example.receiptsbooks.model.domain.TicketResult;

public interface ITicketPagerCallback extends IBaseCallback {

    /**
     * 淘口令加载结果
     * @param cover
     */
    void onTicketLoaded(String cover, TicketResult ticketResult);

}