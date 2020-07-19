package com.example.receiptsbooks.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.receiptsbooks.R;
import com.example.receiptsbooks.utils.CameraFilePathUtil;
import com.example.receiptsbooks.utils.LogUtils;
import com.example.receiptsbooks.utils.permission.PermissionHelper;
import com.example.receiptsbooks.utils.permission.PermissionInterface;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;
import com.zhihu.matisse.internal.utils.MediaStoreCompat;

import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TestActivity extends AppCompatActivity implements PermissionInterface {
    private static final int REQUEST_CODE_CHOOSE_PHOTO_SHOOT = 10001;
    private List<Uri> mSelected;
    //权限工具类
    private PermissionHelper mPermissionHelper;

    private static final int REQUEST_CODE_CHOOSE = 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);
        //初始化并发起权限申请
        mPermissionHelper = new PermissionHelper(this, this);
        mPermissionHelper.requestPermissions();
        initListener();

        DisplayMetrics systemDM = Resources.getSystem().getDisplayMetrics();

    }

    @BindView(R.id.test_btn_show_path)
    public Button showPathBtn;

    @BindView(R.id.test_iv_camera_photo)
    public ImageView cameraPhoto;

    public void showPath(View view){
        //这里是去调用相机
        MediaStoreCompat mediaStoreCompat = new MediaStoreCompat(this);
        mediaStoreCompat.setCaptureStrategy(new CaptureStrategy(true,"com.example.receiptdemoone.fileprovider"));
        mediaStoreCompat.dispatchCaptureIntent(this,REQUEST_CODE_CHOOSE_PHOTO_SHOOT);
        //因为是指定Uri所以onActivityResult中的data为空 只能再这里获取拍照的路径
        String currentPhotoPath = mediaStoreCompat.getCurrentPhotoPath();
        //返回图片路径名
        String realFilePath = CameraFilePathUtil.getRealFilePath(this, Uri.parse(currentPhotoPath));
        cameraPhoto.setImageURI(Uri.parse(realFilePath));
        LogUtils.d(this,"real path ==> " + realFilePath);
    }

    private void initListener() {
        upLoadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAction();
//                LogUtils.d(TestActivity.this,"photo real path ==> " + getRealFilePath(TestActivity.this, PhotoMetadataUtils.getPath(getContentResolver(),)));
            }
        });

        showPathBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPath(v);
            }
        });
    }

    private void startAction() {
        Matisse.from(TestActivity.this)
                .choose(MimeType.ofAll(), false)//ofAll:图片和视频   ofImage:只有图片   ofVideo：视频
                .countable(false)//有序图片,当选择时，会根据选择的顺序标明1234...
                .capture(true)//使用拍照功能，下面两行必须连用
                .captureStrategy(
                        new CaptureStrategy(true, "com.example.receiptdemoone.fileprovider", "test"))//存储路径
                .maxSelectable(1)//最多选择个数
//                        .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                .gridExpectedSize(
                        getResources().getDimensionPixelSize(R.dimen.grid_expected_size))//便于展示界面适配不同屏幕的手机
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                .thumbnailScale(0.85f)//缩略图与原图的比例
                .imageEngine(new GlideEngine())//映像引擎
                .setOnSelectedListener((uriList, pathList) -> {
                    Log.e("onSelected", "onSelected: pathList=" + pathList);//用户对展示界面进行操作时
                })
                .showSingleMediaType(true)//是否只能选择图片和视频的一种
                .originalEnable(true)//用户能否选择原图
                .maxOriginalSize(10)//原图最大为多少MB
                .autoHideToolbarOnSingleTap(true)//是否隐藏toolbar
                .setOnCheckedListener(isChecked -> {
                    Log.e("isChecked", "onCheck: isChecked=" + isChecked);//用户选着原始图片时回调
                })
                .forResult(REQUEST_CODE_CHOOSE);//与onActivityResult中的判断相对应
    }

    @BindView(R.id.test_btn_upload)
    public Button upLoadBtn;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            mSelected = Matisse.obtainResult(data);//获取选择的图片的路径
            Log.d("Matisse", "mSelected: " + mSelected);
            cameraPhoto.setImageURI(mSelected.get(0));
        }
    }

    @Override
    public int getPermissionsRequestCode() {
        //设置权限请求requestCode，只有不跟onRequestPermissionsResult方法中的其他请求码冲突即可
        return 10000;
    }

    @Override
    public String[] getPermissions() {
        //设置所需的权限
        return new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET,
                Manifest.permission.CAMERA};
    }

    @Override
    public void requestPermissionsSuccess() {
        //权限通过
        initListener();
    }

    @Override
    public void requestPermissionsFail() {
        //请求失败
    }

    @Override
    protected void onResume() {
        super.onResume();
        ImageView imageView = this.findViewById(R.id.test_verify_image);
        Random random = new Random();
        Glide.with(this).load("http://10.0.2.2:8888/test/captcha?test=" + random.nextInt()).into(imageView);
    }
}
