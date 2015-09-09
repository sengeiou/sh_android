package com.shootr.android.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.signature.StringSignature;
import com.shootr.android.R;
import com.shootr.android.data.dagger.ApplicationContext;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import javax.inject.Inject;

public class GlideImageLoader implements ImageLoader {

    private static final int DEFAULT_STREAM_PICTURE_RES = R.drawable.ic_stream_picture_default;
    private static final int DEFAULT_PROFILE_PHOTO_RES = R.drawable.ic_contact_picture_default;

    private final RequestManager glide;

    @Inject
    public GlideImageLoader(@ApplicationContext Context context) {
        glide = Glide.with(context);
    }

    @Override
    public void loadProfilePhoto(String url, ImageView view) {
        boolean isValidPhoto = url != null && !url.isEmpty();
        if (isValidPhoto) {
            glide.load(url).dontAnimate().diskCacheStrategy(DiskCacheStrategy.ALL).into(view);
        } else {
            view.setImageResource(DEFAULT_PROFILE_PHOTO_RES);
        }
    }

    @Override
    public void loadStreamPicture(String url, ImageView view) {
        boolean isValidPicture = url != null && !url.isEmpty();
        if (isValidPicture) {
            glide.load(url).into(view);
        } else {
            view.setImageResource(DEFAULT_STREAM_PICTURE_RES);
        }
    }

    @Override
    public void loadTimelineImage(String url, ImageView view) {
        boolean isValidPicture = url != null && !url.isEmpty();
        if (isValidPicture) {
            glide.load(url).placeholder(R.color.transparent).into(view);
        }
    }

    @Override
    public void load(String url, ImageView image, final Callback callback) {
        glide.load(url).into(new ImageViewTarget<GlideDrawable>(image) {
            @Override
            protected void setResource(GlideDrawable resource) {
                view.setImageDrawable(resource);
                callback.onLoaded();
            }
        });
    }

    @Override
    public void load(String url, ImageView view) {
        glide.load(url).into(view);
    }

    @Override
    public void load(File file, ImageView view) {
        glide.load(file).signature(getSignature(file)).into(view);
    }

    @Override
    public void load(File file, ImageView view, int maxSize) {
        glide.load(file).signature(getSignature(file)).fitCenter().override(maxSize, maxSize).into(view);
    }

    @Override
    public void loadWithPreview(String url, String previewUrl, ImageView view, Callback callback) {
        //TODO no preview?
        load(url, view, callback);
    }

    @Override
    public Bitmap loadProfilePhoto(String url) throws IOException {
        try {
            boolean isValidPhoto = url != null && !url.isEmpty();
            if (isValidPhoto) {
                return glide.load(url).asBitmap().into(-1, -1).get();
            } else {
                return glide.load(DEFAULT_PROFILE_PHOTO_RES).asBitmap().into(-1, -1).get();
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public Bitmap load(String url) throws IOException {
        try {
            boolean isValidPhoto = url != null && !url.isEmpty();
            if (isValidPhoto) {
                return glide.load(url).asBitmap().into(-1, -1).get();
            } else {
                return null;
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new IllegalStateException(e);
        }
    }

    @NonNull
    protected StringSignature getSignature(File file) {
        return new StringSignature(String.valueOf(file.lastModified()));
    }

    //TODO use or delete
    public static class GlidePreviewHelper {

        private final String previewUrl;
        private final String finalUrl;
        private final ImageView imageView;
        private final RequestManager glide;
        private boolean loadedFinalImage = false;
        private ImageViewTarget<GlideDrawable> previewTarget;
        private ImageViewTarget<GlideDrawable> finalTarget;

        public GlidePreviewHelper(String previewUrl, String finalUrl, ImageView imageView, RequestManager glide) {
            this.previewUrl = previewUrl;
            this.finalUrl = finalUrl;
            this.imageView = imageView;
            this.glide = glide;
        }

        public void loadImageWithPreview(final Callback callback) {
            previewTarget = new ImageViewTarget<GlideDrawable>(imageView) {
                @Override
                protected void setResource(GlideDrawable resource) {
                    if (!loadedFinalImage) {
                        imageView.setImageDrawable(resource);
                    }
                }
            };

            finalTarget = new ImageViewTarget<GlideDrawable>(imageView) {

                @Override
                protected void setResource(GlideDrawable resource) {
                    loadedFinalImage = true;
                    imageView.setImageDrawable(resource);
                    cancelPreview();
                    callback.onLoaded();
                }
            };

            glide.load(previewUrl).into(previewTarget);
            glide.load(finalUrl).into(finalTarget);
        }

        private void cancelPreview() {
            previewTarget.getRequest().clear();
        }
    }
}
