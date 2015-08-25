package com.shootr.android.util;

import android.graphics.Bitmap;
import android.widget.ImageView;
import java.io.File;
import java.io.IOException;

public interface ImageLoader {

    void loadProfilePhoto(String url, ImageView view);

    void loadStreamPicture(String url, ImageView view);

    void loadTimelineImage(String url, ImageView view);

    void load(String url, ImageView image, Callback callback);

    void load(String url, ImageView view);

    void load(File file, ImageView view);

    void load(File file, ImageView view, int maxSize);

    void loadWithPreview(String url, String previewUrl, ImageView view, Callback callback);

    Bitmap loadProfilePhoto(String url) throws IOException;

    Bitmap loadTimelineImage(String url) throws IOException;

    abstract class Callback {

        public abstract void onLoaded(Bitmap bitmap);

        public void onError() {
            /* default: no-op */
        }
    }
}
