package com.example.receiptsbooks.room.bean;

import java.io.Serializable;

public interface IBaseProduct extends Serializable {

    String getName();

    double getPrice();

    String getType();

    int getReceiptId();

    int getProductId();
}
