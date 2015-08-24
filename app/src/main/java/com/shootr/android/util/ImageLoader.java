package com.shootr.android.util;

import android.graphics.Bitmap;
import android.widget.ImageView;
import java.io.File;
import java.io.IOException;

public interface ImageLoader {

    void loadProfilePhoto(String url, ImageView view);

    void loadStreamPicture(String url, ImageView view);

    void loadTimelineImage(String url, ImageView view);

    void load(String path, ImageView view);

    void loadWithPreview(String path, String previewPath, ImageView view, Callback callback);

    void load(File file, ImageView view);

    void loadPreviewImage(File file, ImageView view, Integer maxScreenWidth);

    Bitmap loadProfilePhoto(String url) throws IOException;

    Bitmap loadTimelineImage(String url) throws IOException;

    void cancelTag(Object previewTag);

    void load(String url, ImageView view, ImageLoaderCallback callback);

    void load(String imageUrl, ImageView image, Callback callback);

    interface Callback {

        void onLoaded(Bitmap bitmap);

    }
}
