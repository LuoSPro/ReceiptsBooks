package com.example.receiptsbooks.utils;

import com.google.android.material.appbar.AppBarLayout;

/**
 * AppBar的自定义监听类，用于查看AppBar的状态
 */
public abstract class AppBarStateChangeListener implements AppBarLayout.OnOffsetChangedListener {
 
    public enum  AppBarState {
        EXPANDED,
        COLLAPSED,
        IDLE
    }
 
    private AppBarState mCurrentState = AppBarState.IDLE;
 
    @Override
    public final void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        if (i == 0) {
            if (mCurrentState != AppBarState.EXPANDED) {
                onStateChanged(appBarLayout, AppBarState.EXPANDED);
            }
            mCurrentState = AppBarState.EXPANDED;
        } else if (Math.abs(i) >= appBarLayout.getTotalScrollRange() - 140) {
            if (mCurrentState != AppBarState.COLLAPSED) {
                onStateChanged(appBarLayout, AppBarState.COLLAPSED);
            }
            mCurrentState = AppBarState.COLLAPSED;
        } else {
            if (mCurrentState != AppBarState.IDLE) {
                onStateChanged(appBarLayout, AppBarState.IDLE);
            }
            mCurrentState = AppBarState.IDLE;
        }
    }
 
    public abstract void onStateChanged(AppBarLayout appBarLayout, AppBarState state);
}