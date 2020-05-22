package com.example.receiptsbooks.ui.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.receiptsbooks.model.domain.Categories;
import com.example.receiptsbooks.ui.fragment.StorePagerFragment;
import com.example.receiptsbooks.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

public class StorePagerAdapter extends FragmentPagerAdapter {
    private List<Categories.DataBean> categoryList = new ArrayList<>();

    public StorePagerAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return categoryList.get(position).getTitle();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        LogUtils.d(this,"getItem ==> " + position);
        Categories.DataBean bean = categoryList.get(position);
//        StorePagerFragment storePagerFragment = StorePagerFragment.newInstance(bean);
        StorePagerFragment storePagerFragment = StorePagerFragment.newInstance(bean);
        return storePagerFragment;
    }

    @Override
    public int getCount() {
        return categoryList.size();
    }

    public void setCategories(Categories categories) {
        this.categoryList.clear();
        List<Categories.DataBean> data = categories.getData();
        this.categoryList.addAll(data);
        LogUtils.d(this,"categoryList size ==> " +categoryList.size());
        notifyDataSetChanged();
    }
}
