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

    void load(File file, ImageView view);

    void loadPreviewImage(File file, ImageView view, Integer maxScreenWidth);

    Bitmap loadProfilePhoto(String url) throws IOException;

    Bitmap loadTimelineImage(String url) throws IOException;
}
