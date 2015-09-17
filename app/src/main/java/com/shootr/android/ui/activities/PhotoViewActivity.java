package com.shootr.android.ui.activities;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.shootr.android.R;
import com.shootr.android.domain.utils.TimeUtils;
import com.shootr.android.ui.base.BaseActivity;
import com.shootr.android.util.FeedbackMessage;
import com.shootr.android.util.ImageLoader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.inject.Inject;
import timber.log.Timber;
import uk.co.senab.photoview.PhotoViewAttacher;

import static com.shootr.android.domain.utils.Preconditions.checkNotNull;

public class PhotoViewActivity extends BaseActivity {

    private static final String EXTRA_IMAGE_PREVIEW_URL = "preview";
    private static final String EXTRA_IMAGE_URL = "image";
    public static final int UI_ANIMATION_DURATION = 300;
    public static final TimeInterpolator UI_ANIMATION_INTERPOLATOR = new DecelerateInterpolator();

    @Bind(R.id.photo) ImageView image;
    @Bind(R.id.toolbar_actionbar) Toolbar toolbar;

    @Inject ImageLoader imageLoader;
    @Inject FeedbackMessage feedbackMessage;
    @Inject TimeUtils timeUtils;

    private PhotoViewAttacher attacher;
    private boolean isUiShown = true;

    public static Intent getIntentForActivity(Context context, String imageUrl) {
        return getIntentForActivity(context, imageUrl, null);
    }

    public static Intent getIntentForActivity(Context context, String imageUrl, String previewUrl) {
        Intent intent = new Intent(context, PhotoViewActivity.class);
        intent.putExtra(EXTRA_IMAGE_URL, checkNotNull(imageUrl));
        intent.putExtra(EXTRA_IMAGE_PREVIEW_URL, previewUrl);
        return intent;
    }

    @Override protected int getLayoutResource() {
        return R.layout.activity_photo_view;
    }

    @Override protected void initializeViews(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        setupActionBar();

        attacher = new PhotoViewAttacher(image);
        attacher.setZoomable(false);
        attacher.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override public void onViewTap(View view, float v, float v2) {
                onPhotoClick();
            }
        });
        loadImages();
    }

    @Override protected void initializePresenter() {
        /* no presenter, so no-op */
    }


    private void setupActionBar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
    }

    private void loadImages() {
        String previewUrl = getIntent().getStringExtra(EXTRA_IMAGE_PREVIEW_URL);
        String imageUrl = getIntent().getStringExtra(EXTRA_IMAGE_URL);

        if (previewUrl != null) {
            imageLoader.loadWithPreview(imageUrl, previewUrl, image, new ImageLoader.Callback() {
                @Override public void onLoaded() {
                    attacher.update();
                }
            });
        } else {
            imageLoader.load(imageUrl, image, new ImageLoader.Callback() {
                @Override public void onLoaded() {
                    attacher.update();
                }
            });
        }
    }

    public void onPhotoClick() {
        if (isUiShown) {
            hideUi();
            isUiShown = false;
        } else {
            showUi();
            isUiShown = true;
        }
    }

    private void showUi() {
        toolbar.animate().setDuration(UI_ANIMATION_DURATION).setInterpolator(UI_ANIMATION_INTERPOLATOR).translationY(0);
    }

    private void hideUi() {
        int toolbarHeight = toolbar.getHeight();
        toolbar.animate()
          .setDuration(UI_ANIMATION_DURATION)
          .setInterpolator(UI_ANIMATION_INTERPOLATOR)
          .translationY(-toolbarHeight);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.photo_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.menu_download_photo) {
            saveImage(image.getDrawingCache());
        }
        return super.onOptionsItemSelected(item);
    }

    public void saveImage(Bitmap image){
        String folder = getString(R.string.downloading_image_folder);
        String storageDirectory = Environment.getExternalStorageDirectory().getAbsolutePath() + folder;
        File downloadDirectory = new File(storageDirectory);

        if (!downloadDirectory.exists()) {
            downloadDirectory.mkdirs();
        }

        String filename = String.format(getString(R.string.downloading_image_basic_filename), getDateInString(), ".jpg");
        File file = new File(downloadDirectory, filename);

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.JPEG, 85, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            scanForFile(file);
            showImageSavedSuccessfully();
        } catch (IOException e) {
            showErrorSavingImage();
        }
    }

    private void scanForFile(File file) {
        MediaScannerConnection.scanFile(PhotoViewActivity.this,
          new String[] { file.toString() },
          null,
          new MediaScannerConnection.OnScanCompletedListener() {
              public void onScanCompleted(String path, Uri uri) {
                  Timber.d("ExternalStorage", "Scanned " + path + ":");
                  Timber.d("ExternalStorage", "-> uri=" + uri);
              }
          });
    }

    private String getDateInString() {
        return String.valueOf(timeUtils.getCurrentDate());
    }

    private void showErrorSavingImage() {
        feedbackMessage.show(getView(), getString(R.string.downloading_image_error));
    }

    private void showImageSavedSuccessfully() {
        feedbackMessage.show(getView(), getString(R.string.downloading_image_success));
    }

}
