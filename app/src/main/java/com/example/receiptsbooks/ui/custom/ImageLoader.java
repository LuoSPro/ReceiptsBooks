package com.example.receiptsbooks.ui.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.previewlibrary.loader.IZoomMediaLoader;
import com.previewlibrary.loader.MySimpleTarget;

public class ImageLoader implements IZoomMediaLoader {
    RequestOptions options;

    {
        options = new RequestOptions()
                .centerCrop()
                .priority(Priority.HIGH);
    }

    @Override
    public void displayImage(@NonNull Fragment context, @NonNull String path, ImageView imageView, @NonNull MySimpleTarget mySimpleTarget) {
        Glide.with(context)
                .asBitmap()
                .load(path)
                .apply(options)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        mySimpleTarget.onResourceReady();
                        imageView.setImageBitmap(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    @Override
    public void displayGifImage(@NonNull Fragment fragment, @NonNull String s, ImageView imageView, @NonNull MySimpleTarget mySimpleTarget) {

    }

    @Override
    public void onStop(@NonNull Fragment context) {
        Glide.with(context).onStop();
    }
    @Override
    public void clearMemory(@NonNull Context c) {
        Glide.get(c).clearMemory();
    }
}