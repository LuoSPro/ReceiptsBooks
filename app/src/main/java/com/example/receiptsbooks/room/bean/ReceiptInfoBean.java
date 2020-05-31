package com.example.receiptsbooks.room.bean;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.receiptsbooks.utils.DateUtils;

import java.util.Date;

@Entity(tableName = "receipt_info_table")
public class ReceiptInfoBean implements IBaseReceipt {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;
    @ColumnInfo(name = "receipt_date")
    private Date receiptDate;
    @ColumnInfo(name = "total_price")
    private double totalPrice;
    @ColumnInfo(name = "save_date")
    private Date saveData;
    @ColumnInfo(name = "receipt_photo_path")
    private String receiptPhotoPath;

    public ReceiptInfoBean() {
    }

    @Override
    public String toString() {
        return "ReceiptInfoBean{" +
                "id=" + id +
                ", receiptDate=" + DateUtils.dateToString(receiptDate,false) +
                ", totalPrice='" + totalPrice + '\'' +
                ", saveData=" + DateUtils.dateToString(saveData,true) +
                '}';
    }

    public String getReceiptPhotoPath() {
        return receiptPhotoPath;
    }

    public void setReceiptPhotoPath(String receiptPhotoPath) {
        this.receiptPhotoPath = receiptPhotoPath;
    }

    public Date getSaveData() {
        return saveData;
    }

    public void setSaveData(Date saveData) {
        this.saveData = saveData;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getReceiptDate() {
        return receiptDate;
    }

    public void setReceiptDate(Date receiptDate) {
        this.receiptDate = receiptDate;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
