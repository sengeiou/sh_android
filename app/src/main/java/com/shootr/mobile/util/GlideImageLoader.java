package com.shootr.mobile.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.signature.StringSignature;
import com.shootr.mobile.R;
import com.shootr.mobile.data.dagger.ApplicationContext;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import javax.inject.Inject;
import timber.log.Timber;

public class GlideImageLoader implements ImageLoader {

  private static final int DEFAULT_STREAM_PICTURE_RES = R.drawable.ic_stream_picture_default;
  private static final int DEFAULT_PROFILE_PHOTO_RES = R.drawable.ic_contact_picture_default;

  private final Resources resources;
  private final RequestManager glide;

  @Inject public GlideImageLoader(@ApplicationContext Context context, Resources resources) {
    this.resources = resources;
    glide = Glide.with(context);
  }

  @Override public void loadProfilePhoto(String url, ImageView view) {
    boolean isValidPhoto = url != null && !url.isEmpty();
    if (isValidPhoto) {
            /* dont animate: https://github.com/bumptech/glide/issues/504#issuecomment-113459960 */
      glide.load(url)
          .dontAnimate()
          .placeholder(DEFAULT_PROFILE_PHOTO_RES)
          .diskCacheStrategy(DiskCacheStrategy.ALL)
          .into(view);
    } else {
      view.setImageResource(DEFAULT_PROFILE_PHOTO_RES);
    }
  }

  @Override public void loadStreamPicture(String url, ImageView view) {
    boolean isValidPicture = url != null && !url.isEmpty();
    if (isValidPicture) {
            /* dont animate: https://github.com/bumptech/glide/issues/504#issuecomment-113459960 */
      glide.load(url)
          .dontAnimate()
          .placeholder(DEFAULT_STREAM_PICTURE_RES)
          .diskCacheStrategy(DiskCacheStrategy.ALL)
          .into(view);
    } else {
      view.setImageResource(DEFAULT_STREAM_PICTURE_RES);
    }
  }

  @Override public void loadBlurStreamPicture(String url, ImageView blurView,
      RequestListener<String, GlideDrawable> listener) {
    boolean isValidPicture = url != null && !url.isEmpty();
    if (isValidPicture) {
            /* dont animate: https://github.com/bumptech/glide/issues/504#issuecomment-113459960 */
      glide.load(url)
          .listener(listener)
          .dontAnimate()
          .bitmapTransform(new BlurTransformation(blurView.getContext()))
          .diskCacheStrategy(DiskCacheStrategy.ALL)
          .into(blurView);
    }
  }

  @Override public void loadTimelineImage(final String url, ImageView view) {
    boolean isValidPicture = url != null && !url.isEmpty();
    if (isValidPicture) {
      glide.load(url).dontAnimate().diskCacheStrategy(DiskCacheStrategy.ALL).into(view);
    }
  }

  @Override public void load(String url, ImageView image, final Callback callback) {
    glide.load(url).into(new ImageViewTarget<GlideDrawable>(image) {
      @Override protected void setResource(GlideDrawable resource) {
        view.setImageDrawable(resource);
        callback.onLoaded();
      }
    });
  }

  @Override public void load(String url, ImageView view) {
    glide.load(url).diskCacheStrategy(DiskCacheStrategy.ALL).into(view);
  }

  @Override public void load(File file, ImageView view) {
    glide.load(file)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .signature(getSignature(file))
        .into(view);
  }

  @Override public void load(File file, ImageView view, int maxSize) {
    glide.load(file)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .signature(getSignature(file))
        .fitCenter()
        .override(maxSize, maxSize)
        .into(view);
  }

  @Override
  public void loadWithPreview(String url, String previewUrl, ImageView view, Callback callback) {
    //TODO no preview?
    load(url, view, callback);
  }

  @Override public Bitmap loadProfilePhoto(String url) throws IOException {
    try {
      boolean isValidPhoto = url != null && !url.isEmpty();
      if (isValidPhoto) {
        return glide.load(url).asBitmap().into(-1, -1).get();
      } else {
        // Workaround: Android doesn't decode layer drawable from resources, it fails silently
        Drawable defaultPhotoDrawable = resources.getDrawable(DEFAULT_PROFILE_PHOTO_RES);
        return drawableToBitmap(defaultPhotoDrawable);
      }
    } catch (InterruptedException | ExecutionException e) {
      Timber.w(e, "Bitmap loading for profile (%s) failed, probably interrupted by another call?",
          url);
      return null;
    }
  }

  @Override public Bitmap load(String url) throws IOException {
    try {
      boolean isValidPhoto = url != null && !url.isEmpty();
      if (isValidPhoto) {
        return glide.load(url).asBitmap().into(-1, -1).get();
      } else {
        return null;
      }
    } catch (InterruptedException | ExecutionException e) {
      Timber.w(e, "Bitmap loading (%s) failed, probably interrupted by another call?", url);
      return null;
    }
  }

  @NonNull protected StringSignature getSignature(File file) {
    return new StringSignature(String.valueOf(file.lastModified()));
  }

  private static Bitmap drawableToBitmap(Drawable drawable) {
    Bitmap bitmap;

    if (drawable instanceof BitmapDrawable) {
      BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
      if (bitmapDrawable.getBitmap() != null) {
        return bitmapDrawable.getBitmap();
      }
    }

    if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
      bitmap = Bitmap.createBitmap(1, 1,
          Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
    } else {
      bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
          Bitmap.Config.ARGB_8888);
    }

    Canvas canvas = new Canvas(bitmap);
    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
    drawable.draw(canvas);
    return bitmap;
  }
}
