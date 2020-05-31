package com.example.receiptsbooks.presenter.impl;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.receiptsbooks.model.Api;
import com.example.receiptsbooks.model.domain.ReceiptInfo;
import com.example.receiptsbooks.presenter.IReceiptInfoPresenter;
import com.example.receiptsbooks.room.bean.IBaseProduct;
import com.example.receiptsbooks.room.bean.ProductBean;
import com.example.receiptsbooks.room.bean.ReceiptInfoBean;
import com.example.receiptsbooks.room.repository.ReceiptInfoRepository;
import com.example.receiptsbooks.room.viewmodel.ProductViewModel;
import com.example.receiptsbooks.room.viewmodel.ReceiptInfoViewModel;
import com.example.receiptsbooks.utils.DateUtils;
import com.example.receiptsbooks.utils.LogUtils;
import com.example.receiptsbooks.utils.NetworkUtils;
import com.example.receiptsbooks.utils.RetrofitManager;
import com.example.receiptsbooks.utils.ToastUtil;
import com.example.receiptsbooks.view.IReceiptInfoCallback;

import java.io.File;
import java.net.HttpURLConnection;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ReceiptInfoPresenterImpl implements IReceiptInfoPresenter {
    private IReceiptInfoCallback mCallback = null;
    private ReceiptInfoViewModel mReceiptInfoViewModel;
    private ProductViewModel mProductViewModel;
    private Handler mMainThread = new Handler(Looper.getMainLooper());
    private boolean isRepeatSave = false;

    /**
     * 向服务器请求数据
     * @param filePath 上传的图片路径
     * @param context 用于获取网络是否可用
     */
    @Override
    public void getReceiptInfo(String filePath,Context context) {
        if (mCallback != null) {
            mCallback.onLoading();
        }
        File file = new File(filePath);
        RequestBody body = RequestBody.create(MediaType.parse("image/jpg"),file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("file","file",body);
        Retrofit retrofit = RetrofitManager.getInstance().getReceiptInfoRetrofit();
        Api api = retrofit.create(Api.class);
        Call<ReceiptInfo> task = api.getReceiptInfo(part);
        task.enqueue(new Callback<ReceiptInfo>() {
            @Override
            public void onResponse(Call<ReceiptInfo> call, Response<ReceiptInfo> response) {
                //数据结果
                int code = response.code();
                LogUtils.d(ReceiptInfoPresenterImpl.this,"getReceiptContent code ==> " + code);
                if (code == HttpURLConnection.HTTP_OK){
                    //请求成功
                    ReceiptInfo receiptInfo = response.body();
                    LogUtils.d(ReceiptInfoPresenterImpl.this,"getReceiptContent result ==> " + receiptInfo);
                    if (mCallback != null) {
                        if (receiptInfo == null || receiptInfo.getTotalProduct() == null){
                            mCallback.onAnalysisError();
                        }else{
                            //成功
                            mCallback.onResultLoaded(receiptInfo);
                        }
                    }
                }else{
                    //请求失败
                    if (mCallback != null) {
                        mCallback.onAnalysisError();
                    }
                }
            }

            @Override
            public void onFailure(Call<ReceiptInfo> call, Throwable t) {
                //加载失败
                LogUtils.d(ReceiptInfoPresenterImpl.this,"getReceiptContent onFailure + " + t);
                if (mCallback != null) {
                    if (NetworkUtils.iConnected(context)){
                        mCallback.onAnalysisError();
                    }else{
                        mCallback.onNetworkError();
                    }
                }
            }
        });
    }

    @Override
    public void saveReceiptToDB(FragmentActivity activity, ReceiptInfo receiptInfo,String receiptPhotoPath) {
        //保存数据
        mReceiptInfoViewModel = ViewModelProviders.of(activity).get(ReceiptInfoViewModel.class);
        mProductViewModel = ViewModelProviders.of(activity).get(ProductViewModel.class);
        //插入前，检查小票数据是否已经存在
        LiveData<List<ReceiptInfoBean>> receiptInfoBeanLive = mReceiptInfoViewModel.queryReceiptExists(receiptInfo);
        receiptInfoBeanLive.observe(activity, new Observer<List<ReceiptInfoBean>>() {
            @Override
            public void onChanged(List<ReceiptInfoBean> receiptInfoBeans) {
                if (receiptInfoBeans.size() != 0){
//                    if (isRepeatSave){
//                        return;
//                    }
                    //如果查询到数据就说明这个数据已经存在了
                    mMainThread.post(new Runnable() {
                        @Override
                        public void run() {
                            //ToastUtil.showToast("保存失败, 这张小票在"+ DateUtils.dateToString(receiptInfoBeans.get(0).getSaveData(),false)+"已保存");
                        }
                    });
                }else{
                    //定义接口对象，等到小票插入成功后，再带着receiptId来回调这里的onRespond方法
                    ReceiptInfoRepository.ResponseCallback responseCallback = new ReceiptInfoRepository.ResponseCallback() {
                        @Override
                        public void onRespond(List<Long> receiptId) {
                            //插入商品
                            insertProductToDatabase(receiptId.get(0),receiptInfo);
                        }
                    };
                    //插入小票
                    insertReceiptToDatabase(responseCallback,receiptInfo,receiptPhotoPath);
                    isRepeatSave = true;
                }
            }
        });

    }

    private void insertReceiptToDatabase(ReceiptInfoRepository.ResponseCallback responseCallback, ReceiptInfo receiptInfo, String receiptPhotoPath) {
        ReceiptInfoBean receiptInfoBean = new ReceiptInfoBean();
        receiptInfoBean.setSaveData(DateUtils.revertDate(System.currentTimeMillis()));
        receiptInfoBean.setReceiptDate(DateUtils.stringToData(receiptInfo.getReceiptDate()));
        receiptInfoBean.setTotalPrice(receiptInfo.getTotalPrice());
        receiptInfoBean.setReceiptPhotoPath(receiptPhotoPath);
        mReceiptInfoViewModel.insertReceiptInfo(responseCallback,receiptInfoBean);
    }

    private void insertProductToDatabase(long receiptId, ReceiptInfo receiptInfo) {
        List<ReceiptInfo.Product> totalMessage = receiptInfo.getTotalProduct();
        for (int i = 0; i < totalMessage.size(); i++) {
            IBaseProduct product = totalMessage.get(i);
            ProductBean productBean = new ProductBean((int) receiptId,product.getName(),product.getPrice(),product.getType());
            mProductViewModel.insertProduct(productBean);
        }
        mMainThread.post(new Runnable() {
            @Override
            public void run() {
                ToastUtil.showToast("小票数据保存成功");
            }
        });
    }

    @Override
    public void registerViewCallback(IReceiptInfoCallback callback) {
        //注：这里虽然会有两个组件来注册这个Presenter，但是这里不用List去把每个都保存下来，因为我们用的是单例子
        //的Presenter，这里一响应，所有引用这个Presenter的组件都会收到通知
        mCallback = callback;
    }

    @Override
    public void unregisterViewCallback(IReceiptInfoCallback callback) {
        //取消UI的引用，避免引起内存泄漏
        mCallback = null;
    }
}
