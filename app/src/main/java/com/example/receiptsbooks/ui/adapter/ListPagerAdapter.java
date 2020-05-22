package com.example.receiptsbooks.ui.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.receiptsbooks.ui.fragment.ListPagerFragment;

import java.util.ArrayList;
import java.util.List;

public class ListPagerAdapter extends FragmentPagerAdapter {
    private List<String> mCategoriesList = new ArrayList<>();

    public ListPagerAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        //LogUtils.d(this,"categories position ==》 " + position);
        return mCategoriesList.get(position);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        String category = mCategoriesList.get(position);
        ListPagerFragment listPagerFragment = ListPagerFragment.newInstance(category);
        return listPagerFragment;
    }

    @Override
    public int getCount() {
        return mCategoriesList.size();
    }

    public void setCategories(List<String> categories) {
        //LogUtils.d(this,"categories categories.size() ==》 " + categories.size());
        mCategoriesList.clear();
        mCategoriesList.addAll(categories);
        notifyDataSetChanged();
    }
}
