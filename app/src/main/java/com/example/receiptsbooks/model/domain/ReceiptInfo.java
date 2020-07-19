package com.example.receiptsbooks.model.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.receiptsbooks.room.bean.IBaseReceipt;
import com.example.receiptsbooks.room.bean.IBaseProduct;

import java.util.List;

public class ReceiptInfo implements IBaseReceipt, Parcelable {

    /**
     * date : 2020.02.28
     * name : ["佳洁士茶爽牙膏柠","娇子金格调"]
     * price : [9.8,30]
     * totalProduct : [{"name":"佳洁士茶爽牙膏柠","price":9.8,"type":"个人护理"},{"name":"娇子金格调","price":30,"type":"酒水饮料"}]
     * totalPrice : 39.80
     */

    private String saveDate;
    private int status;
    private String receiptDate;
    private double totalPrice;
    private List<String> name;
    private List<Double> price;
    private List<Product> totalProduct;
    private String receiptPhoto;

    public ReceiptInfo() {
    }

    protected ReceiptInfo(Parcel in) {
        saveDate = in.readString();
        receiptDate = in.readString();
        totalPrice = in.readDouble();
        name = in.createStringArrayList();
        receiptPhoto = in.readString();
    }

    public static final Creator<ReceiptInfo> CREATOR = new Creator<ReceiptInfo>() {
        @Override
        public ReceiptInfo createFromParcel(Parcel in) {
            return new ReceiptInfo(in);
        }

        @Override
        public ReceiptInfo[] newArray(int size) {
            return new ReceiptInfo[size];
        }
    };

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getReceiptPhoto() {
        return receiptPhoto;
    }

    public void setReceiptPhoto(String receiptPhoto) {
        this.receiptPhoto = receiptPhoto;
    }

    public String getSaveDate() {
        return saveDate;
    }

    public void setSaveDate(String saveDate) {
        this.saveDate = saveDate;
    }

    public String getReceiptDate() {
        return receiptDate;
    }

    public void setReceiptDate(String receiptDate) {
        this.receiptDate = receiptDate;
    }

    public String getDate() {
        return receiptDate;
    }

    public void setDate(String receiptDate) {
        this.receiptDate = receiptDate;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public List<String> getName() {
        return name;
    }

    public void setName(List<String> name) {
        this.name = name;
    }

    public List<Double> getPrice() {
        return price;
    }

    public void setPrice(List<Double> price) {
        this.price = price;
    }

    public List<Product> getTotalProduct() {
        return totalProduct;
    }

    public void setTotalProduct(List<Product> totalProduct) {
        this.totalProduct = totalProduct;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(saveDate);
        dest.writeString(receiptDate);
        dest.writeDouble(totalPrice);
        dest.writeStringList(name);
        dest.writeString(receiptPhoto);
    }

    public static class Product implements IBaseProduct,Parcelable {
        protected Product(Parcel in) {
            name = in.readString();
            price = in.readDouble();
            type = in.readString();
        }

        public static final Creator<Product> CREATOR = new Creator<Product>() {
            @Override
            public Product createFromParcel(Parcel in) {
                return new Product(in);
            }

            @Override
            public Product[] newArray(int size) {
                return new Product[size];
            }
        };

        @Override
        public String toString() {
            return "ProductBean{" +
                    "name='" + name + '\'' +
                    ", price=" + price +
                    ", type='" + type + '\'' +
                    '}';
        }

        /**
         * name : 佳洁士茶爽牙膏柠
         * price : 9.8
         * type : 个人护理
         */

        private String name;
        private double price;
        private String type;

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

        @Override
        public int getReceiptId() {
            return 0;
        }

        @Override
        public int getProductId() {
            return 0;
        }

        public void setType(String type) {
            this.type = type;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(name);
            dest.writeDouble(price);
            dest.writeString(type);
        }
    }
}
