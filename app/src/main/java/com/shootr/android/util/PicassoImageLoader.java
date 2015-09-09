package com.shootr.android.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import com.shootr.android.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import java.io.File;
import java.io.IOException;
import javax.inject.Inject;

public class PicassoImageLoader implements ImageLoader {

    private static final int DEFAULT_STREAM_PICTURE_RES = R.drawable.ic_stream_picture_default;
    private static final int DEFAULT_PROFILE_PHOTO_RES = R.drawable.ic_contact_picture_default;

    private final Picasso picasso;
    private final Resources resources;

    @Inject public PicassoImageLoader(Picasso picasso, Resources resources) {
        this.picasso = picasso;
        this.resources = resources;
    }

    @Override public void loadProfilePhoto(String url, ImageView view) {
        boolean isValidPhoto = url != null && !url.isEmpty();
        if (isValidPhoto) {
            picasso.load(url).into(view);
        } else {
            view.setImageResource(DEFAULT_PROFILE_PHOTO_RES);
        }
    }

    @Override public void loadStreamPicture(String url, ImageView view) {
        boolean isValidPicture = url != null && !url.isEmpty();
        if (isValidPicture) {
            picasso.load(url).into(view);
        } else {
            view.setImageResource(DEFAULT_STREAM_PICTURE_RES);
        }
    }

    @Override public void loadTimelineImage(String url, ImageView view) {
        boolean isValidPicture = url != null && !url.isEmpty();
        if (isValidPicture) {
            picasso.load(url).placeholder(R.color.transparent).into(view);
        }
    }

    @Override public void load(String url, ImageView view) {
        picasso.load(url).into(view);
    }

    @Override public void loadWithPreview(String url, String previewUrl, ImageView view, Callback callback) {
        PicassoPreviewHelper picassoPreviewHelper = new PicassoPreviewHelper(previewUrl, url, view, picasso);
        picassoPreviewHelper.loadImageWithPreview(callback);
        view.setTag(picassoPreviewHelper);
    }

    @Override public void load(File file, ImageView view) {
        picasso.load(file).into(view);
    }

    @Override public void load(File file, ImageView view, int maxSize) {
        picasso.load(file).resize(maxSize, maxSize).centerInside().skipMemoryCache().into(view);
    }

    @Override public Bitmap loadProfilePhoto(String url) throws IOException {
        boolean isValidPhoto = url != null && !url.isEmpty();
        if (isValidPhoto) {
            return picasso.load(url).get();
        } else {
            Drawable defaultPhotoDrawable = resources.getDrawable(DEFAULT_PROFILE_PHOTO_RES);
            return drawableToBitmap(defaultPhotoDrawable);
        }
    }

    @Override
    public Bitmap load(String url) throws IOException {
        boolean isValidPhoto = url != null && !url.isEmpty();
        if (isValidPhoto) {
            return picasso.load(url).get();
        } else {
            return null;
        }
    }

    @Override public void load(String url, ImageView image, Callback callback) {
        PicassoPreviewHelper picassoPreviewHelper = new PicassoPreviewHelper(null, url, image, picasso);
        picassoPreviewHelper.loadImage(callback);
        image.setTag(picassoPreviewHelper);
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
                    callback.onLoaded();
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
                    callback.onLoaded();
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

    private static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
