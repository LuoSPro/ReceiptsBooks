package com.example.receiptsbooks.ui.activity;

import com.example.receiptsbooks.room.bean.IBaseProduct;

public interface IReceiptDetailsActivity {

    void switch2ModifyProductFragment(IBaseProduct item);

    void switch2ReceiptDetailsFragment(boolean isModify,boolean modifyOrAdd);

    void setProductItem(IBaseProduct productItem);

    IBaseProduct getProductItem();

    void setItemPosition(int position);

    int getItemPosition();

}
