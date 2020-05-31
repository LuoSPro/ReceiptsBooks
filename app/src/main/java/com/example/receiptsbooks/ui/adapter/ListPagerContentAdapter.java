package com.example.receiptsbooks.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.receiptsbooks.R;
import com.example.receiptsbooks.room.bean.ProductBean;
import com.example.receiptsbooks.room.bean.ReceiptAndProduct;
import com.example.receiptsbooks.utils.DateUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ListPagerContentAdapter extends BaseExpandableListAdapter {
    private List<ReceiptAndProduct> mGroupData = new ArrayList<>();
    private List<ProductBean> mChildData = new ArrayList<>();
    private String mCategory;
    private OnClickViewDetailsListener mViewDetailsListener;
    private OnClickExpandListListener mExpandListListener;

    @Override
    public int getGroupCount() {
        return mGroupData.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mGroupData.get(groupPosition).getProductBean().size();
    }

    @Override
    public ReceiptAndProduct getGroup(int groupPosition) {
        return mGroupData.get(groupPosition);
    }

    @Override
    public ProductBean getChild(int groupPosition, int childPosition) {
        return mGroupData.get(groupPosition).getProductBean().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    /**
     * 分组和子选项是否持有稳定的ID, 就是说底层数据的改变会不会影响到它们
     * @return
     */
    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder groupViewHolder;
        //加载View
        if (convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_pager_content, parent, false);
            groupViewHolder = new GroupViewHolder();
            groupViewHolder.mReceiptDate = convertView.findViewById(R.id.item_list_pager_content_receipt_date);
            groupViewHolder.mProductType1 = convertView.findViewById(R.id.item_list_pager_content_type1);
            groupViewHolder.mProductType2 = convertView.findViewById(R.id.item_list_pager_content_type2);
            groupViewHolder.mProductCount = convertView.findViewById(R.id.item_list_pager_content_product_count);
            groupViewHolder.mProductTotalPrice = convertView.findViewById(R.id.item_list_pager_content_total_price);
            groupViewHolder.mViewDetails = convertView.findViewById(R.id.item_list_pager_content_view_details);
            convertView.setTag(groupViewHolder);
        }else{
            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }
        //设置数据
        ReceiptAndProduct receiptAndProduct = mGroupData.get(groupPosition);
        //改变数据
        List<ProductBean> productBeans = receiptAndProduct.getProductBean();
        Iterator<ProductBean> iterator = productBeans.iterator();
        while (iterator.hasNext()){
            if (!mCategory.equals(iterator.next().getType())){
                iterator.remove();
            }
        }
        groupViewHolder.mReceiptDate.setText(DateUtils.dateToString(receiptAndProduct.getReceiptInfoBean().getReceiptDate(),false));
        groupViewHolder.mProductType1.setText(mCategory);
        groupViewHolder.mProductType2.setText(mCategory);
        groupViewHolder.mProductCount.setText(String.format(parent.getContext().getString(R.string.text_history_item_product_count),productBeans.size()));
        double totalPrice = 0;
        for (ProductBean productBean : productBeans) {
            totalPrice += productBean.getPrice();
        }
        groupViewHolder.mProductTotalPrice.setText(String.format(parent.getContext().getString(R.string.text_history_item_total_price),totalPrice));
        //设置监听
        groupViewHolder.mViewDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewDetailsListener.onViewDetails(receiptAndProduct);
            }
        });
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder childViewHolder;
        if (convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_receipt_info_list, parent, false);
            childViewHolder = new ChildViewHolder();
            childViewHolder.mProductName = convertView.findViewById(R.id.produce_name);
            childViewHolder.mProductPrice = convertView.findViewById(R.id.produce_price);
            childViewHolder.mProductType = convertView.findViewById(R.id.produce_type);
            convertView.setTag(childViewHolder);
        }else{
            childViewHolder = (ChildViewHolder) convertView.getTag();
        }
        ProductBean productBean = mGroupData.get(groupPosition).getProductBean().get(childPosition);
        childViewHolder.mProductName.setText(productBean.getName());
        childViewHolder.mProductPrice.setText(productBean.getPrice()+"");
        childViewHolder.mProductType.setText(productBean.getType());
        return convertView;
    }

    /**
     * 指定位置上的子元素是否可选中
     * @param groupPosition
     * @param childPosition
     * @return
     */
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void setGroupData(List<ReceiptAndProduct> groupData,String category){
        this.mGroupData.clear();
        this.mChildData.clear();
        mGroupData.addAll(groupData);
        this.mCategory = category;
        notifyDataSetChanged();
    }

    static class GroupViewHolder{
        TextView mReceiptDate;
        TextView mProductType1;
        TextView mProductType2;
        TextView mProductTotalPrice;
        TextView mProductCount;
        TextView mViewDetails;
    }

    static class ChildViewHolder{
        TextView mProductName;
        TextView mProductPrice;
        TextView mProductType;
    }

    public void setOnClickViewDetailsListener(OnClickViewDetailsListener listener){
        this.mViewDetailsListener = listener;
    }

    public void setOnClickExpandListListener(OnClickExpandListListener listener){
        this.mExpandListListener = listener;
    }

    public interface OnClickViewDetailsListener{
        void onViewDetails(ReceiptAndProduct receiptAndProduct);
    }

    public interface OnClickExpandListListener{
        void onExpandList();
    }
}
