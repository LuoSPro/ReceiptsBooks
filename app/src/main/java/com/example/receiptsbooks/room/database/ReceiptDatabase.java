package com.example.receiptsbooks.room.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.receiptsbooks.room.bean.BudgetBean;
import com.example.receiptsbooks.room.bean.BudgetDateBean;
import com.example.receiptsbooks.room.bean.ProductBean;
import com.example.receiptsbooks.room.bean.ReceiptInfoBean;
import com.example.receiptsbooks.room.dao.BudgetDao;
import com.example.receiptsbooks.room.dao.BudgetDateDao;
import com.example.receiptsbooks.room.dao.ProductDao;
import com.example.receiptsbooks.room.dao.ReceiptInfoDao;
import com.example.receiptsbooks.utils.DateUtils;

@Database(entities = {ReceiptInfoBean.class, ProductBean.class, BudgetBean.class, BudgetDateBean.class},version = 1,exportSchema = false)
@TypeConverters(DateUtils.class)
public abstract class ReceiptDatabase extends RoomDatabase {
    private static ReceiptDatabase INSTANCE;
    //不要在数据库名字后面加db，不然创建不成功
    private static final String DB_NAME = "receiptInfoDb";

    public static synchronized ReceiptDatabase getDatabase(Context context){
        if (INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    ReceiptDatabase.class,DB_NAME)
                    .build();
        }
        return INSTANCE;
    }

    public abstract ReceiptInfoDao getReceiptInfoDao();

    public abstract ProductDao getProductDao();

    public abstract BudgetDao getBudgetDao();

    public abstract BudgetDateDao getBudgetDateDao();
}
