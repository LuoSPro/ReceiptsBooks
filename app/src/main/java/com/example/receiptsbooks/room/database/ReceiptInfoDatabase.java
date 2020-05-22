package com.example.receiptsbooks.room.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.receiptsbooks.room.bean.ProductBean;
import com.example.receiptsbooks.room.bean.ReceiptInfoBean;
import com.example.receiptsbooks.room.dao.ProductDao;
import com.example.receiptsbooks.room.dao.ReceiptInfoDao;
import com.example.receiptsbooks.utils.DateConverterUtil;

@Database(entities = {ReceiptInfoBean.class, ProductBean.class},version = 1,exportSchema = false)
@TypeConverters(DateConverterUtil.class)
public abstract class ReceiptInfoDatabase extends RoomDatabase {
    private static ReceiptInfoDatabase INSTANCE;
    //不要在数据库名字后面加db，不然创建不成功
    private static final String DB_NAME = "receiptInfoDb";

    public static synchronized ReceiptInfoDatabase getDatabase(Context context){
        if (INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    ReceiptInfoDatabase.class,DB_NAME)
                    .build();
        }
        return INSTANCE;
    }

    public abstract ReceiptInfoDao getReceiptInfoDao();

    public abstract ProductDao getProductDao();
}
