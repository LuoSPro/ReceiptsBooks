package com.example.receiptsbooks.ui.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.example.receiptsbooks.R;
import com.example.receiptsbooks.base.BaseFragment;
import com.example.receiptsbooks.base.State;
import com.example.receiptsbooks.room.bean.IBaseProduct;
import com.example.receiptsbooks.room.bean.ProductBean;
import com.example.receiptsbooks.ui.activity.IReceiptDetailsActivity;
import com.example.receiptsbooks.ui.custom.WheelView;
import com.example.receiptsbooks.utils.Constants;
import com.example.receiptsbooks.utils.ToastUtil;

import java.util.List;

import butterknife.BindView;

public class ModifyProductFragment extends BaseFragment {

    private IBaseProduct mProductItem;
    private WheelView mTypeWheelPicker;
    //dialog上面的确认按钮
    private TextView mConfirmBtn;
    private Dialog dialog;
    //类型列表
    private List<String> mTypeList;
    //传过来的产品的名字
    private String mOriginalName;
    //传过来的产品的价格
    private double mOriginalPrice;
    //传过来的产品的类型
    private String mOriginalType;
    //修改产品的名称
    private String mModifyName;
    //修改产品的类型
    private String mModifyType;
    //修改产品的价格
    private double mModifyPrice;
    //输入的数据是否合法
    private boolean mDataIsLegal = true;
    //要展示的warning信息
    private String mWarningInfo;
    //当前页面是修改数据还是增加数据
    private boolean mModifyOrAdd = true;

    /**
     * 为什么使用bundle传输数据，而不用构造方法？
     * 原因：因为当我们旋转屏幕，Activity会重建，Fragment也会重建，所以如果我们使用构造方法传过来的数据就不再了
     * 如果我们使用bundle传递数据，那么当Fragment重建的时候，就会通过反射去拿出bundle的数据
     * @return
     */
    public static ModifyProductFragment newInstance(){
        return new ModifyProductFragment();
    }

    @Override
    protected void initView(View rootView) {
        //WheelPicker的初始化
        dialog = new Dialog(getContext(), R.style.ReleaseDialog);
        View view = View.inflate(getContext(), R.layout.modify_type_picker_layout, null);
        dialog.setContentView(view);
        mConfirmBtn = dialog.findViewById(R.id.modify_confirm);
        mTypeWheelPicker = dialog.findViewById(R.id.type_wheel_picker);
        mTypeWheelPicker.setCurved(true);//是否呈现3D效果
        mTypeWheelPicker.setTextSize(60);
    }

    public boolean isModifyData(){
        //因为外部也要调用这个方法，所以这里需要提取一下
        extractViewData();
        return !mOriginalName.equals(mModifyName) || mOriginalPrice != mModifyPrice || !mOriginalType.equals(mModifyType);
    }

    @Override
    protected void initListener() {
        mFragmentBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //查看是否修改了数据
                if (isModifyData()){
                    alterDialogShow();
                }else{
                    //没对界面进行修改就直接返回上一个fragment
                    backToDetails();
                }
            }
        });

        mProductType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Window window = dialog.getWindow();
                //设置弹出位置
                window.setGravity(Gravity.BOTTOM);
                //设置弹出动画
                window.setWindowAnimations(R.style.main_menu_animStyle);
                //设置对话框大小
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();
            }
        });

        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                mProductType.setText(mTypeList.get(mTypeWheelPicker.getCurrentItemPosition()));
            }
        });

        mConfirmModifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //提取数据
                extractViewData();
                if (!mDataIsLegal){
                    ToastUtil.showToast(mWarningInfo);
                }else{
                    if (isModifyData()){
                        FragmentActivity activity = getActivity();
                        if (activity instanceof IReceiptDetailsActivity){
                            IReceiptDetailsActivity receiptActivity = (IReceiptDetailsActivity) activity;
                            receiptActivity.setProductItem(new ProductBean(receiptActivity.getProductItem().getProductId(),receiptActivity.getProductItem().getReceiptId(),mModifyName,mModifyPrice,mModifyType));
                            receiptActivity.switch2ReceiptDetailsFragment(true,mModifyOrAdd);
                        }
                    }else{
                        //没有修改数据
                        backToDetails();
                    }
                }
            }
        });
    }

    private void  extractViewData() {
        mDataIsLegal = true;
        //获取界面信息
        mModifyPrice = -1;
        try {
            mModifyPrice = Double.parseDouble(mProductPriceEv.getText().toString().trim());
        } catch (NumberFormatException e) {
            mWarningInfo = "请输入正确的价格";
            mDataIsLegal = false;
            return;
        }
        mModifyType = mProductType.getText().toString();
        if ("".equals(mModifyType)){
            mWarningInfo = "商品类型不能为空";
            mDataIsLegal = false;
        }
        mModifyName = mProductNameEv.getText().toString().trim();
        if ("".equals(mModifyName)){
            mWarningInfo = "商品名称不能为空";
            mDataIsLegal = false;
        }
    }

    private void backToDetails(){
        FragmentActivity activity = getActivity();
        if (activity instanceof IReceiptDetailsActivity){
            ((IReceiptDetailsActivity) activity).switch2ReceiptDetailsFragment(false,mModifyOrAdd);
        }
    }

    //显示界面中心对话框
    public void alterDialogShow(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("你要放弃本次编辑吗？");
        builder.setCancelable(true);
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                backToDetails();
            }
        });
        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void loadData() {
        setUpState(State.SUCCESS);
        //将类型的信息传过来
        this.mTypeList = Constants.PRODUCT_TYPE_LIST;
        mTypeWheelPicker.setData(mTypeList);
        //接收从Activity来的数据
        FragmentActivity activity = getActivity();
        if (activity instanceof IReceiptDetailsActivity){
            mProductItem = ((IReceiptDetailsActivity) activity).getProductItem();
            //设置按钮的text
            setModifyOrAddModel(mProductItem);
            setDataToView();
        }
    }

    @SuppressLint("SetTextI18n")
    private void setDataToView() {
        //获取数据
        mOriginalName = mProductItem.getName();
        mOriginalPrice = mProductItem.getPrice();
        mOriginalType = mProductItem.getType();
        //设置内容
        mProductNameEv.setText(mOriginalName);
        mProductNameEv.setSelection(mOriginalName.length());
        mProductPriceEv.setText(mOriginalPrice+"");
        mProductPriceEv.setSelection((mOriginalPrice+"").length());
        mProductType.setText(mProductItem.getType());
        mTypeWheelPicker.setDefaultItem(mProductItem.getType());//设置默认的item
    }

    @BindView(R.id.modify_product_name)
    public EditText mProductNameEv;

    @BindView(R.id.modify_product_price)
    public EditText mProductPriceEv;

    @BindView(R.id.modify_product_type)
    public TextView mProductType;

    @BindView(R.id.modify_product_back)
    public ImageView mFragmentBack;

    @BindView(R.id.confirm_modify_btn)
    public Button mConfirmModifyBtn;

    @BindView(R.id.modify_product_title)
    public TextView mFragmentTitle;

    @SuppressLint("SetTextI18n")
    public void setFragmentData(IBaseProduct productItem){
        //更新productItem
        mProductItem = productItem;
        //因为第一次传过来的时候，Fragment还没有创建，所以第一次传递数据我们就使用loadData中去activity那里获取，后面的
        //获取数据的方式直接通过activity那边主动调用setData就好了，因为我们的fragment始终存在，只是被隐藏了
        if (mProductNameEv == null){
            return;
        }
        //设置按钮的text
        setModifyOrAddModel(productItem);
        //设置数据
        setDataToView();
    }

    private void setModifyOrAddModel(IBaseProduct productItem) {
        if ("".equals(productItem.getType())){
            mConfirmModifyBtn.setText("确认添加");
            mFragmentTitle.setText("添加信息");
            this.mModifyOrAdd = false;
        }else{
            this.mModifyOrAdd = true;
            mConfirmModifyBtn.setText("确认修改");
            mFragmentTitle.setText("修改信息");
        }
    }

    @Override
    protected int getRootViewResId() {
        return R.layout.fragment_modify_product;
    }

}
