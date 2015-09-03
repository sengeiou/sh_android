package com.shootr.android.util;

import android.graphics.Bitmap;
import android.widget.ImageView;
import com.shootr.android.R;
import java.io.File;
import java.io.IOException;

public class ImageLoaderEditMode implements ImageLoader {

    @Override
    public void loadProfilePhoto(String url, ImageView view) {
        view.setImageResource(R.drawable.sample_avatar);
    }

    @Override
    public void loadStreamPicture(String url, ImageView view) {
        view.setImageResource(R.drawable.sample_avatar);
    }

    @Override
    public void loadTimelineImage(String url, ImageView view) {
        view.setImageResource(R.drawable.sample_avatar);
    }

    @Override
    public void load(String url, ImageView image, Callback callback) {
        image.setImageResource(R.drawable.sample_avatar);
    }

    @Override
    public void load(String url, ImageView view) {
        view.setImageResource(R.drawable.sample_avatar);
    }

    @Override
    public void load(File file, ImageView view) {
        view.setImageResource(R.drawable.sample_avatar);
    }

    @Override
    public void load(File file, ImageView view, int maxSize) {
        view.setImageResource(R.drawable.sample_avatar);
    }

    @Override
    public void loadWithPreview(String url, String previewUrl, ImageView view, Callback callback) {
        view.setImageResource(R.drawable.sample_avatar);
    }

    @Override
    public Bitmap loadProfilePhoto(String url) throws IOException {
        return null;
    }

    @Override
    public Bitmap load(String url) throws IOException {
        return null;
    }
}
