package com.shootr.android.util;

import android.widget.ImageView;
import java.io.File;

public interface ImageLoader {

    void loadProfilePhoto(String url, ImageView view);

    void loadStreamPicture(String url, ImageView view);

    void loadTimelineImage(String url, ImageView view);

    void load(String path, ImageView view);

    void load(File file, ImageView view);

    void loadPreviewImage(File file, ImageView view, Integer maxScreenWidth);
}
