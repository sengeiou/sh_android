package com.shootr.android.ui.activities;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
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
import com.shootr.android.ui.base.BaseToolbarActivity;
import com.shootr.android.util.ImageLoader;
import javax.inject.Inject;
import uk.co.senab.photoview.PhotoViewAttacher;

import static com.shootr.android.domain.utils.Preconditions.checkNotNull;

public class PhotoViewActivity extends BaseToolbarActivity {

    private static final String EXTRA_IMAGE_PREVIEW_URL = "preview";
    private static final String EXTRA_IMAGE_URL = "image";
    public static final int UI_ANIMATION_DURATION = 300;
    public static final TimeInterpolator UI_ANIMATION_INTERPOLATOR = new DecelerateInterpolator();

    @Bind(R.id.photo) ImageView image;
    @Bind(R.id.toolbar_actionbar) Toolbar toolbar;

    @Inject ImageLoader imageLoader;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Avoid using DrawerLayout for now, it doesn't work with PhotoView version 1.2.3: https://github.com/chrisbanes/PhotoView/issues/72
        setContentView(R.layout.activity_photo_view);

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
                @Override public void onLoaded(Bitmap bitmap) {
                    attacher.update();
                }
            });
        } else {
            imageLoader.load(imageUrl, image, new ImageLoader.Callback() {
                @Override public void onLoaded(Bitmap bitmap) {
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
        }
        return super.onOptionsItemSelected(item);
    }
}
