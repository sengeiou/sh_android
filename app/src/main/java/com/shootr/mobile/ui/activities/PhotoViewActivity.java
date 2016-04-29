package com.shootr.mobile.ui.activities;

import android.animation.TimeInterpolator;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.base.BaseActivity;
import com.shootr.mobile.util.FeedbackMessage;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.WritePermissionManager;
import java.io.File;
import javax.inject.Inject;
import uk.co.senab.photoview.PhotoViewAttacher;

import static com.shootr.mobile.domain.utils.Preconditions.checkNotNull;

public class PhotoViewActivity extends BaseActivity {

    private static final String EXTRA_IMAGE_PREVIEW_URL = "preview";
    private static final String EXTRA_IMAGE_URL = "image";
    public static final int UI_ANIMATION_DURATION = 300;
    public static final TimeInterpolator UI_ANIMATION_INTERPOLATOR = new DecelerateInterpolator();

    @Bind(R.id.photo) ImageView imageView;
    @Bind(R.id.toolbar_actionbar) Toolbar toolbar;

    @Inject ImageLoader imageLoader;
    @Inject FeedbackMessage feedbackMessage;
    @Inject WritePermissionManager writePermissionManager;

    private PhotoViewAttacher attacher;
    private boolean isUiShown = true;
    private String previewUrl;
    private String imageUrl;

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

        writePermissionManager.init(this);

        attacher = new PhotoViewAttacher(imageView);
        attacher.setZoomable(false);
        attacher.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override public void onViewTap(View view, float v, float v2) {
                onPhotoClick();
            }
        });
    }

    @Override protected void initializePresenter() {
        previewUrl = getIntent().getStringExtra(EXTRA_IMAGE_PREVIEW_URL);
        imageUrl = getIntent().getStringExtra(EXTRA_IMAGE_URL);
        loadImages();
    }

    private void setupActionBar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
    }

    private void loadImages() {
        if (previewUrl != null) {
            imageLoader.loadWithPreview(imageUrl, previewUrl, imageView, new ImageLoader.Callback() {
                @Override public void onLoaded() {
                    attacher.update();
                }
            });
        } else {
            imageLoader.load(imageUrl, imageView, new ImageLoader.Callback() {
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

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.photo_view, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.menu_download_photo) {
            saveImage();
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveImage() {
        if (writePermissionManager.hasWritePermission()) {
            performImageDownload();
        } else {
            writePermissionManager.requestWritePermissionToUser();
        }
    }

    private void performImageDownload() {
        Uri imageUri = Uri.parse(imageUrl);
        String fileName = imageUri.getLastPathSegment();
        String downloadSubpath = getString(R.string.downloaded_pictures_subfolder) + fileName;

        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(imageUri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDescription(imageUrl);
        request.allowScanningByMediaScanner();
        // Equivalent to request.setDestinationInExternalPublicDir(), but makes sure the Shootr subfolder exists
        request.setDestinationUri(getDownloadDestination(downloadSubpath));

        request.setMimeType("image/jpeg"); //TODO servidor debe mandarlo correctamente

        downloadManager.enqueue(request);
    }

    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
        if (requestCode == writePermissionManager.getWritePermissionRequest()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                performImageDownload();
            } else {
                feedbackMessage.showLong(getView(), R.string.download_photo_permission_denied);
            }
        }
    }

    @NonNull private Uri getDownloadDestination(String downloadSubpath) {
        File picturesFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File destinationFile = new File(picturesFolder, downloadSubpath);
        destinationFile.mkdirs();
        return Uri.fromFile(destinationFile);
    }
}
