package com.example.receiptsbooks.room.bean;

import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Relation;

import java.util.List;

//Parcelable方便在内存间传输
public class ReceiptAndProduct implements IBaseReceipt {

    public ReceiptAndProduct() {
    }

    @Ignore
    public ReceiptAndProduct(ReceiptInfoBean receiptInfoBean, List<ProductBean> productBean) {
        mReceiptInfoBean = receiptInfoBean;
        mProductBean = productBean;
    }

    @Override
    public String toString() {
        return "ReceiptAndProduct{" +
                "mReceiptInfoBean=" + mReceiptInfoBean +
                ", mProductBean=" + mProductBean +
                '}';
    }

    @Embedded
    private ReceiptInfoBean mReceiptInfoBean;
    @Relation(
            parentColumn = "id",
            entityColumn = "receipt_id"
    )
    //注：这里是一对多的关系，所以ProductBean用List保存，之前由于没用List，导致每次返回的数据都是最后一条
    private List<ProductBean> mProductBean;

    public ReceiptInfoBean getReceiptInfoBean() {
        return mReceiptInfoBean;
    }

    public void setReceiptInfoBean(ReceiptInfoBean receiptInfoBean) {
        mReceiptInfoBean = receiptInfoBean;
    }

    public List<ProductBean> getProductBean() {
        return mProductBean;
    }

    public void setProductBean(List<ProductBean> productBean) {
        mProductBean = productBean;
    }

}
