package com.shootr.mobile.util;

import android.graphics.Bitmap;
import android.widget.ImageView;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.widgets.AvatarView;
import java.io.File;
import java.io.IOException;

public class ImageLoaderEditMode implements ImageLoader {

    @Override public void loadProfilePhoto(String url, ImageView view) {
        view.setImageResource(R.drawable.sample_avatar);
    }

    @Override public void loadProfilePhoto(String url, AvatarView view, String username) {
        view.setImageResource(R.drawable.sample_avatar);
    }

    @Override public void loadStreamPicture(String url, ImageView view) {
        view.setImageResource(R.drawable.sample_avatar);
    }

    @Override
    public void loadStreamPicture(String url, ImageView image, CompletedCallback callback) {
        /* no-op */
    }

    @Override
    public void loadBlurStreamPicture(String url, ImageView blurView, RequestListener<String, GlideDrawable> listener) {
        /* no-op */
    }

    @Override public void loadTimelineImage(String url, ImageView view) {
        view.setImageResource(R.drawable.sample_avatar);
    }

    @Override public void load(String url, ImageView image, Callback callback) {
        image.setImageResource(R.drawable.sample_avatar);
    }

    @Override public void load(String url, ImageView view) {
        view.setImageResource(R.drawable.sample_avatar);
    }

    @Override public void loadDiscoverImage(String url, ImageView view, Callback callback) {
        view.setImageResource(R.drawable.sample_avatar);
    }

    @Override public void load(File file, ImageView view) {
        view.setImageResource(R.drawable.sample_avatar);
    }

    @Override public void load(File file, ImageView view, int maxSize) {
        view.setImageResource(R.drawable.sample_avatar);
    }

    @Override public void loadWithPreview(String url, String previewUrl, ImageView view, Callback callback) {
        view.setImageResource(R.drawable.sample_avatar);
    }

    @Override public void loadImageWithId(ImageView view, int resourceId) {
        view.setImageResource(R.drawable.sample_avatar);
    }

    @Override public Bitmap loadProfilePhoto(String url) throws IOException {
        return null;
    }

    @Override public Bitmap load(String url) throws IOException {
        return null;
    }
}
