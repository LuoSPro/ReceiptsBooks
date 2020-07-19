package com.example.receiptsbooks.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.RoomWarnings;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.receiptsbooks.room.bean.ProductBean;
import com.example.receiptsbooks.room.bean.ReceiptAndProduct;

import java.util.List;

@Dao
public interface ProductDao {
    @Insert
    void insertProduct(ProductBean... productBeans);

    @Update
    void updateProduct(ProductBean... productBeans);

    @Delete
    void deleteProduct(ProductBean... productBeans);

    @Query("SELECT * FROM product_table WHERE product_type like :productType")
    LiveData<List<ProductBean>> getProductFromType(final String productType);

    @Query("SELECT * FROM product_table WHERE receipt_id=:receiptId")
    LiveData<List<ProductBean>> getProductFromReceiptId(final int receiptId);

    @Transaction
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT * FROM product_table p INNER JOIN receipt_info_table r ON p.receipt_id=r.id GROUP BY p.receipt_id ORDER BY save_date DESC")
    LiveData<List<ReceiptAndProduct>> getAllProduct();

    @Transaction
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT * FROM product_table p INNER JOIN receipt_info_table r ON p.receipt_id=r.id AND p.product_type like :type GROUP BY p.receipt_id ORDER BY receipt_date")
    LiveData<List<ReceiptAndProduct>> getReceiptAndProductByType(String type);

    @Transaction
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT * FROM product_table p INNER JOIN receipt_info_table r ON p.receipt_id=r.id AND p.receipt_id=:receiptId GROUP BY p.receipt_id")
    LiveData<ReceiptAndProduct> getReceiptInfoById(int receiptId);

    @Transaction
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT * FROM product_table p INNER JOIN receipt_info_table r ON p.receipt_id=r.id AND p.product_type like :type AND receipt_date>=:beginDate AND receipt_date<:endDate GROUP BY p.receipt_id")
    LiveData<List<ReceiptAndProduct>> getReceiptAndProductByDate(String type,long beginDate, long endDate);

    @Transaction
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT * FROM product_table p INNER JOIN receipt_info_table r ON p.receipt_id=r.id AND receipt_date>=:beginDate AND receipt_date<:endDate GROUP BY p.receipt_id")
    LiveData<List<ReceiptAndProduct>> getReceiptAndProductByDate(long beginDate, long endDate);
}
