package com.example.receiptsbooks.room.bean;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "product_table" ,
        foreignKeys = @ForeignKey(entity = ReceiptInfoBean.class,parentColumns = "id",childColumns = "receipt_id",onDelete = CASCADE),
        indices = {@Index(value = "receipt_id",unique = false)})
public class ProductBean implements IBaseProduct {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "product_id")
    private int productId;
    @ColumnInfo(name = "receipt_id")
    private int receiptId;
    @ColumnInfo(name = "product_name")
    private String name;
    @ColumnInfo(name = "product_price")
    private double price;
    @ColumnInfo(name = "product_type")
    private String type;

    public ProductBean() {
    }

    @Ignore
    public ProductBean(String name, double price, String type) {
        this.name = name;
        this.price = price;
        this.type = type;
    }

    @Ignore
    public ProductBean(int productId, int receiptId, String name, double price, String type) {
        this.productId = productId;
        this.receiptId = receiptId;
        this.name = name;
        this.price = price;
        this.type = type;
    }

    @Ignore
    public ProductBean(int receiptId, String name, double price, String type) {
        this.receiptId = receiptId;
        this.name = name;
        this.price = price;
        this.type = type;
    }

    @Override
    public String toString() {
        return "ProductBean{" +
                "productId=" + productId +
                ", receiptId=" + receiptId +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", type='" + type + '\'' +
                '}';
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(int receiptId) {
        this.receiptId = receiptId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
