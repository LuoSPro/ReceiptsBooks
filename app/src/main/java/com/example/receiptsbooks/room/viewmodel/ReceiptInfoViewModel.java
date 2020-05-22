package com.example.receiptsbooks.room.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.receiptsbooks.model.domain.ReceiptInfo;
import com.example.receiptsbooks.room.bean.ReceiptInfoBean;
import com.example.receiptsbooks.room.repository.ReceiptInfoRepository;

import java.util.List;

public class ReceiptInfoViewModel extends AndroidViewModel {
    private ReceiptInfoRepository mInfoRepository;
    public ReceiptInfoViewModel(@NonNull Application application) {
        super(application);
        mInfoRepository = new ReceiptInfoRepository(application);
    }

    public LiveData<List<ReceiptInfoBean>> getAllReceiptInfoLive() {
        return mInfoRepository.getAllReceiptInfoLive();
    }

    public LiveData<List<ReceiptInfoBean>> getAllReceiptInfoByDate() {
        return mInfoRepository.getAllReceiptInfoByDateLive();
    }

    public LiveData<List<ReceiptInfoBean>> queryReceiptExists(ReceiptInfo receiptInfo) {
        return mInfoRepository.queryReceiptExists(receiptInfo);
    }

    public void insertReceiptInfo(ReceiptInfoRepository.ResponseCallback responseCallback, ReceiptInfoBean... receiptInfoBeans){
        mInfoRepository.insertReceiptInfo(responseCallback,receiptInfoBeans);
    }

    public void deleteReceiptInfo(ReceiptInfoBean...receiptInfoBeans){
        mInfoRepository.deleteReceiptInfo(receiptInfoBeans);
    }

    public void updateReceiptInfo(ReceiptInfoBean...receiptInfoBeans){
        mInfoRepository.updateReceiptInfo(receiptInfoBeans);
    }
}
