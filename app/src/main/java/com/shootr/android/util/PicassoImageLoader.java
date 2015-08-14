package com.shootr.android.util;

import android.graphics.Bitmap;
import android.widget.ImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Target;
import java.io.File;
import java.io.IOException;
import javax.inject.Inject;

public class PicassoImageLoader implements ImageLoader {

    private final PicassoWrapper picasso;

    @Inject public PicassoImageLoader(PicassoWrapper picasso) {
        this.picasso = picasso;
    }

    @Override public void loadProfilePhoto(String url, ImageView view) {
        picasso.loadProfilePhoto(url).into(view);
    }

    @Override public void loadStreamPicture(String url, ImageView view) {
        picasso.loadStreamPicture(url).into(view);
    }

    @Override public void loadTimelineImage(String url, ImageView view) {
        picasso.loadTimelineImage(url).into(view);
    }

    @Override public void load(String path, ImageView view) {
        picasso.load(path).into(view);
    }

    @Override public void load(File file, ImageView view) {
        picasso.load(file).into(view);
    }

    @Override public void loadPreviewImage(File file, ImageView view, Integer maxScreenWidth) {
        picasso.load(file).resize(maxScreenWidth, maxScreenWidth).centerInside().skipMemoryCache().into(view);
    }

    @Override public Bitmap loadProfilePhoto(String url) throws IOException {
        return picasso.loadProfilePhoto(url).get();
    }

    @Override public Bitmap loadTimelineImage(String url) throws IOException {
        return picasso.loadTimelineImage(url).get();
    }

    @Override public void loadWithTag(String preview, Target previewTargetStrongReference, Object previewTag) {
        picasso.load(preview).tag(previewTag).into(previewTargetStrongReference);
    }

    @Override public void loadIntoTarget(String imageUrl, Target finalTargetStrongReference) {
        picasso.load(imageUrl).into(finalTargetStrongReference);
    }

    @Override public void cancelTag(Object previewTag) {
        picasso.getPicasso().cancelTag(previewTag);
    }

    @Override public void load(String url, ImageView view, Callback callback) {
        picasso.load(url).into(view, callback);
    }
}
