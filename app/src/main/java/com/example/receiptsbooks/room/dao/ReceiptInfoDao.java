package com.example.receiptsbooks.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.receiptsbooks.room.bean.ReceiptInfoBean;

import java.util.Date;
import java.util.List;

@Dao
public interface ReceiptInfoDao {
    @Insert
    List<Long> insertReceiptInfo(ReceiptInfoBean... receiptInfoBeans);

    @Delete
    void deleteReceiptInfo(ReceiptInfoBean... receiptInfoBeans);

    @Update
    void updateReceiptInfo(ReceiptInfoBean... receiptInfoBeans);

    //ASC升序排列
    @Query("SELECT * FROM receipt_info_table ORDER BY id ASC")
    LiveData<List<ReceiptInfoBean>> getAllReceiptInfo();

    //DESC降序排列
    @Query("SELECT * FROM receipt_info_table ORDER BY receipt_date DESC")
    LiveData<List<ReceiptInfoBean>> getAllReceiptInfoByDate();

    @Query("SELECT * FROM receipt_info_table WHERE receipt_date=:date AND total_price=:totalPrice AND receipt_photo_path LIKE :receiptPhotoPath")
    LiveData<List<ReceiptInfoBean>> queryReceiptExists(Date date, double totalPrice,String receiptPhotoPath);
}
