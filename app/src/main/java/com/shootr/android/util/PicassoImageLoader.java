package com.shootr.android.util;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.widget.ImageView;
import com.shootr.android.R;
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
        RequestCreator loadResult = loadProfileRequestCreator(url);
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

    @Override public void loadWithPreview(String path, String previewPath, ImageView view, Callback callback) {
        PicassoPreviewHelper picassoPreviewHelper = new PicassoPreviewHelper(previewPath, path, view, picasso);
        picassoPreviewHelper.loadImageWithPreview(callback);
        view.setTag(picassoPreviewHelper);
    }

    @Override public void load(File file, ImageView view) {
        picasso.load(file).into(view);
    }

    @Override public void loadPreviewImage(File file, ImageView view, Integer maxScreenWidth) {
        picasso.load(file).resize(maxScreenWidth, maxScreenWidth).centerInside().skipMemoryCache().into(view);
    }

    @Override public Bitmap loadProfilePhoto(String url) throws IOException {
        RequestCreator loadResult = loadProfileRequestCreator(url);
        return loadResult.get();
    }

    private RequestCreator loadProfileRequestCreator(String url) {
        boolean isValidPhoto = url != null && !url.isEmpty();
        RequestCreator loadResult;
        if (isValidPhoto) {
            loadResult = picasso.load(url);
        } else {
            loadResult = loadDefaultImage();
        }
        loadResult.placeholder(defaultImageRes).error(defaultImageRes);
        return loadResult;
    }

    @Override public Bitmap loadTimelineImage(String url) throws IOException {
        return picasso.load(url).placeholder(R.color.transparent).get();
    }

    @Override public void cancelTag(Object previewTag) {
        picasso.cancelTag(previewTag);
    }

    @Override public void load(String url, ImageView view, ImageLoaderCallback callback) {
        picasso.load(url).into(view, callback);
    }

    @Override public void load(String imageUrl, ImageView image, Callback callback) {
        PicassoPreviewHelper picassoPreviewHelper = new PicassoPreviewHelper(null, imageUrl, image, picasso);
        picassoPreviewHelper.loadImage(callback);
        image.setTag(picassoPreviewHelper);
    }

    private RequestCreator loadDefaultImage() {
        return load(defaultImageRes);
    }

    private RequestCreator load(@DrawableRes int resource) {
        return picasso.load(resource);
    }

    public static class PicassoPreviewHelper {

        private final String previewUrl;
        private final String finalUrl;
        private final ImageView imageView;
        private final Picasso picasso;
        private boolean loadedFinalImage = false;
        private Target previewTarget;
        private Target finalTarget;

        public PicassoPreviewHelper(String previewUrl, String finalUrl, ImageView imageView, Picasso picasso) {
            this.previewUrl = previewUrl;
            this.finalUrl = finalUrl;
            this.imageView = imageView;
            this.picasso = picasso;
        }

        public void loadImageWithPreview(final Callback callback) {
            previewTarget = new Target() {
                @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    if (!loadedFinalImage) {
                        imageView.setImageBitmap(bitmap);
                    }
                }

                @Override public void onBitmapFailed(Drawable errorDrawable) {
                    /* no-op */
                }

                @Override public void onPrepareLoad(Drawable placeHolderDrawable) {
                    /* no-op */
                }
            };

            finalTarget = new Target() {
                @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    loadedFinalImage = true;
                    imageView.setImageBitmap(bitmap);
                    cancelPreview();
                    callback.onLoaded(bitmap);
                }

                @Override public void onBitmapFailed(Drawable errorDrawable) {
                    /* no-op */
                }

                @Override public void onPrepareLoad(Drawable placeHolderDrawable) {
                    /* no-op */
                }
            };

            picasso.load(previewUrl).into(previewTarget);
            picasso.load(finalUrl).into(finalTarget);
        }

        public void loadImage(final Callback callback) {
            finalTarget = new Target() {
                @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    loadedFinalImage = true;
                    imageView.setImageBitmap(bitmap);
                    cancelPreview();
                    callback.onLoaded(bitmap);
                }

                @Override public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };
            picasso.load(finalUrl).into(finalTarget);
        }

        private void cancelPreview() {
            picasso.cancelRequest(previewTarget);
        }
    }
}
