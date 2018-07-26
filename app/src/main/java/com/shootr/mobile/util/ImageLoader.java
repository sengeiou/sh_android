package com.shootr.mobile.util;

import android.graphics.Bitmap;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.widget.ImageView;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.shootr.mobile.ui.widgets.AvatarView;
import java.io.File;
import java.io.IOException;

public interface ImageLoader {

    @UiThread
    void loadProfilePhoto(String url, ImageView view);

    @UiThread
    void loadProfilePhoto(String url, AvatarView view, String username);

    @UiThread
    void loadStreamPicture(String url, ImageView view);

    @UiThread
    void loadStreamPicture(String url, ImageView image, CompletedCallback callback);

    @UiThread
    void loadBlurStreamPicture(String url, ImageView blurView, RequestListener<String, GlideDrawable> listener);

    @UiThread
    void loadTimelineImage(String url, ImageView view);

    @UiThread
    void load(String url, ImageView image, Callback callback);

    @UiThread
    void load(String url, ImageView view);

    @UiThread
    void loadDiscoverImage(String url, ImageView view, Callback callback);

    @UiThread
    void load(File file, ImageView view);

    @UiThread
    void load(File file, ImageView view, int maxSize);

    @UiThread
    void loadWithPreview(String url, String previewUrl, ImageView view, Callback callback);

    @UiThread
    void loadImageWithId(ImageView view, int resourceId);

    @WorkerThread
    Bitmap loadProfilePhoto(String url) throws IOException;

    @WorkerThread
    Bitmap load(String url) throws IOException;

    interface Callback {

        void onLoaded();
    }

    interface CompletedCallback {

        void onCompleted(Bitmap bitmap);
    }
}
