package com.shootr.android.util;

import android.graphics.Bitmap;
import android.support.annotation.DrawableRes;
import android.widget.ImageView;
import com.shootr.android.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;
import java.io.File;
import java.io.IOException;
import javax.inject.Inject;

public class PicassoImageLoader implements ImageLoader {

    private final Picasso picasso;
    private final int defaultImageRes;
    private int defaultStreamPictureRes;

    @Inject public PicassoImageLoader(Picasso picasso) {
        this.picasso = picasso;
        defaultImageRes = R.drawable.ic_contact_picture_default;
        defaultStreamPictureRes = R.drawable.ic_stream_picture_default;
    }

    @Override public void loadProfilePhoto(String url, ImageView view) {
        boolean isValidPhoto = url != null && !url.isEmpty();
        RequestCreator loadResult;
        if (isValidPhoto) {
            loadResult = picasso.load(url);
        } else {
            loadResult = loadDefaultImage();
        }
        loadResult.placeholder(defaultImageRes).error(defaultImageRes);
        loadResult.into(view);
    }

    @Override public void loadStreamPicture(String url, ImageView view) {
        boolean isValidPicture = url != null && !url.isEmpty();
        RequestCreator loadResult;
        if (isValidPicture) {
            loadResult = picasso.load(url);
        } else {
            loadResult = load(defaultStreamPictureRes);
        }
        loadResult.placeholder(defaultStreamPictureRes);
        loadResult.into(view);
    }

    @Override public void loadTimelineImage(String url, ImageView view) {
        picasso.load(url).placeholder(R.color.transparent).into(view);
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
        boolean isValidPhoto = url != null && !url.isEmpty();
        RequestCreator loadResult;
        if (isValidPhoto) {
            loadResult = picasso.load(url);
        } else {
            loadResult = loadDefaultImage();
        }
        loadResult.placeholder(defaultImageRes).error(defaultImageRes);
        return loadResult.get();
    }

    @Override public Bitmap loadTimelineImage(String url) throws IOException {
        return picasso.load(url).placeholder(R.color.transparent).get();
    }

    @Override public void loadWithTag(String preview, Target previewTargetStrongReference, Object previewTag) {
        picasso.load(preview).tag(previewTag).into(previewTargetStrongReference);
    }

    @Override public void loadIntoTarget(String imageUrl, Target finalTargetStrongReference) {
        picasso.load(imageUrl).into(finalTargetStrongReference);
    }

    @Override public void cancelTag(Object previewTag) {
        picasso.cancelTag(previewTag);
    }

    @Override public void load(String url, ImageView view, Callback callback) {
        picasso.load(url).into(view, callback);
    }

    private RequestCreator loadDefaultImage() {
        return load(defaultImageRes);
    }

    private RequestCreator load(@DrawableRes int resource) {
        return picasso.load(resource);
    }

}
