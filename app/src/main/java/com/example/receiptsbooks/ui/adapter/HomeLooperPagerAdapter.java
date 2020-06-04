package com.example.receiptsbooks.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.receiptsbooks.R;

import java.util.ArrayList;

public class HomeLooperPagerAdapter extends PagerAdapter {
    private ArrayList<Integer> mIconList = new ArrayList<>();
    private OnHomeLooperClickListener mLooperListener;

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.item_home_pager_content, container, false);
        ImageView imageView = view.findViewById(R.id.item_home_pager_content_iv);
        TextView textView = view.findViewById(R.id.item_home_pager_content_tv);
        imageView.setImageResource(mIconList.get(position));
        textView.setText(position == 0?"提取":"记账");
        container.addView(view);
        //设置监听
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLooperListener.onLooperClick(position);
            }
        });
        return view;
    }

    @Override
    public int getCount() {
        return mIconList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    public void setData(ArrayList<Integer> icon) {
        this.mIconList.clear();
        this.mIconList.addAll(icon);
        notifyDataSetChanged();
    }

    public int getDataSize(){
        return mIconList.size();
    }

    public void setOnHomeLooperClickListener(OnHomeLooperClickListener listener){
        this.mLooperListener = listener;
    }

    public interface OnHomeLooperClickListener{
        void onLooperClick(int position);
    }
}
