package com.shootr.mobile.util;

import android.graphics.Bitmap;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.widget.ImageView;
import java.io.File;
import java.io.IOException;

public interface ImageLoader {

    @UiThread
    void loadProfilePhoto(String url, ImageView view);

    @UiThread
    void loadStreamPicture(String url, ImageView view);

    @UiThread
    void loadTimelineImage(String url, ImageView view);

    @UiThread
    void load(String url, ImageView image, Callback callback);

    @UiThread
    void load(String url, ImageView view);

    @UiThread
    void load(File file, ImageView view);

    @UiThread
    void load(File file, ImageView view, int maxSize);

    @UiThread
    void loadWithPreview(String url, String previewUrl, ImageView view, Callback callback);

    @WorkerThread
    Bitmap loadProfilePhoto(String url) throws IOException;

    @WorkerThread
    Bitmap load(String url) throws IOException;

    interface Callback {

        void onLoaded();
    }
}
