package com.shootr.android.ui.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.cocosw.bottomsheet.BottomSheet;
import com.shootr.android.R;
import com.shootr.android.ui.base.BaseNoToolbarActivity;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.presenter.CheckinPresenter;
import com.shootr.android.ui.presenter.EventDetailPresenter;
import com.shootr.android.ui.views.CheckinView;
import com.shootr.android.ui.views.EventDetailView;
import com.shootr.android.ui.widgets.ObservableScrollView;
import com.shootr.android.ui.widgets.WatchersView;
import com.shootr.android.util.FileChooserUtils;
import com.shootr.android.util.PicassoWrapper;
import com.squareup.picasso.Callback;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class EventDetailActivity extends BaseNoToolbarActivity
  implements EventDetailView, CheckinView, ObservableScrollView.Callbacks {

    private static final int REQUEST_EDIT_EVENT = 3;
    private static final int REQUEST_CHOOSE_PHOTO = 4;
    private static final int REQUEST_TAKE_PHOTO = 5;
    private static final float PHOTO_ASPECT_RATIO = 2.3f;

    private static final String EXTRA_EVENT_ID = "eventId";
    private static final String EXTRA_EVENT_MEDIA_COUNT = "eventMediaCount";

    @Bind(R.id.scroll) ObservableScrollView scrollView;
    @Bind(R.id.scroll_child) View scrollChild;
    @Bind(R.id.event_swipe_refresh) SwipeRefreshLayout swipeRefreshLayout;

    @Bind(R.id.event_loading) View loadingView;

    @Bind(R.id.event_photo) ImageView photo;
    @Bind(R.id.event_photo_edit_indicator) ImageView photoEditIndicator;
    @Bind(R.id.event_photo_edit_loading) ProgressBar photoLoadingIndicator;
    @Bind(R.id.event_photo_container) View photoContainer;

    @Bind(R.id.event_title_container) View titleContainer;
    @Bind(R.id.toolbar_actionbar) Toolbar toolbar;
    @Bind(R.id.event_title) TextView titleText;
    @Bind(R.id.event_author) TextView authorText;

    @Bind(R.id.event_content_container) View contentContainer;
    @Bind(R.id.event_content_empty) View contentEmpty;
    @Bind(R.id.event_content_detail) View contentDetail;

    @Bind(R.id.event_content_detail_watchers_number) TextView watchersNumber;
    @Bind(R.id.event_content_detail_watchers_list) WatchersView watchersList;

    @Bind(R.id.event_detail_media) TextView eventMedia;
    @Bind(R.id.event_detail_media_number) TextView eventMediaNumber;

    @Bind(R.id.event_checkin) View checkinButton;

    @Inject EventDetailPresenter eventDetailPresenter;
    @Inject CheckinPresenter checkinPresenter;
    @Inject PicassoWrapper picasso;

    private boolean hasPicture;
    private int lastPictureHeightPixels;
    private int lastHeaderHeightPixels;
    private MenuItem editMenuItem;
    private boolean showEditButton;
    private float headerMaxElevation;

    public static Intent getIntent(Context context, String eventId) {
        Intent intent = new Intent(context, EventDetailActivity.class);
        intent.putExtra(EXTRA_EVENT_ID, eventId);
        return intent;
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerContent(R.layout.activity_event_detail);
        initializeViews();
        setupActionbar();

        String idEvent = getIntent().getStringExtra(EXTRA_EVENT_ID);
        initializePresenter(idEvent);
    }

    private void initializePresenter(String idEvent) {
        eventDetailPresenter.initialize(this, idEvent);
        checkinPresenter.initialize(this, idEvent);
    }

    private void initializeViews() {
        ButterKnife.bind(this);
        headerMaxElevation = getResources().getDimension(R.dimen.event_header_elevation);
        watchersList.setOnProfileClickListener(new WatchersView.OnProfileClickListener() {
            @Override public void onProfile(String idUser) {
                navigateToUserProfile(idUser);
            }
        });
        scrollView.addCallbacks(this);

        swipeRefreshLayout.setColorSchemeResources(R.color.refresh_1, R.color.refresh_2, R.color.refresh_3,
          R.color.refresh_4);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override public void onRefresh() {
                eventDetailPresenter.refreshInfo();
            }
        });
        final ViewTreeObserver scrollViewViewTreeObserver = scrollView.getViewTreeObserver();
        if (scrollViewViewTreeObserver.isAlive()) {
            scrollViewViewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override public void onGlobalLayout() {
                    recomputePhotoAndScrollingMetrics();
                }
            });
        }

        scrollChild.setVisibility(View.INVISIBLE);
    }

    private void setupActionbar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
    }

    private void updateEditIcon() {
        editMenuItem.setVisible(showEditButton);
    }

    @OnClick(R.id.event_author)
    public void onAuthorClick() {
        eventDetailPresenter.clickAuthor();
    }

    @OnClick(R.id.event_detail_media)
    public void onMediaClick() {
        eventDetailPresenter.clickMedia();
    }

    @OnClick(R.id.event_checkin)
    public void onCheckinClick() {
        checkinPresenter.checkIn();
    }

    //region Edit photo
    @OnClick(R.id.event_photo_container)
    public void onPhotoClick() {
        eventDetailPresenter.photoClick();
    }

    private void choosePhotoFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.photo_edit_gallery)),
          REQUEST_CHOOSE_PHOTO);
    }

    private void takePhotoFromCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File pictureTemporaryFile = getCameraPhotoFile();
        if (!pictureTemporaryFile.exists()) {
            try {
                pictureTemporaryFile.getParentFile().mkdirs();
                pictureTemporaryFile.createNewFile();
            } catch (IOException e) {
                Timber.e(e, "No se pudo crear el archivo temporal para la foto de perfil");
                //TODO cancelar operación y avisar al usuario
            }
        }
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(pictureTemporaryFile));
        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
    }

    private File getCameraPhotoFile() {
        return new File(getExternalFilesDir("tmp"), "eventUpload.jpg");
    }
    //endregion

    private void navigateToUserProfile(String idUser) {
        Intent intent = ProfileContainerActivity.getIntent(this, idUser.toString());
        startActivity(intent);
    }

    //region Scroll updates
    @Override public void onScrollChanged(int deltaX, int deltaY) {
        int verticalScrollPosition = scrollView.getScrollY();

        setTitleContainerPosition(verticalScrollPosition);

        setPictureParallaxPosition(verticalScrollPosition);

        setTitleContainerElevation(verticalScrollPosition);
    }

    private void setPictureParallaxPosition(int verticalScrollPosition) {
        photo.setTranslationY(verticalScrollPosition * 0.5f);
    }

    private void setTitleContainerPosition(int verticalScrollPosition) {
        float newTitleTop = Math.max(lastPictureHeightPixels, verticalScrollPosition);
        titleContainer.setTranslationY(newTitleTop);
    }

    private void setTitleContainerElevation(int verticalScrollPosition) {
        float gapFillProgress = 1;
        if (lastPictureHeightPixels != 0) {
            gapFillProgress = scrollProgress(verticalScrollPosition);
        }
        ViewCompat.setElevation(titleContainer, gapFillProgress * headerMaxElevation);
    }

    private float scrollProgress(float current) {
        float max = lastPictureHeightPixels;
        float progress = current / max;

        if (progress > 1) {
            progress = 1;
        } else if (progress < 0) {
            progress = 0;
        }
        return progress;
    }
    //endregion

    //region Views positions
    private void recomputePhotoAndScrollingMetrics() {
        lastPictureHeightPixels = calculatePictureHeight();
        lastHeaderHeightPixels = titleContainer.getHeight();

        setPictureHeight(lastPictureHeightPixels);
        setContentTopMargin();
        onScrollChanged(0, 0);
    }

    private void setContentTopMargin() {
        int detailContainerTopMargin = lastHeaderHeightPixels + lastPictureHeightPixels;
        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) contentContainer.getLayoutParams();
        if (mlp.topMargin != detailContainerTopMargin) {
            mlp.topMargin = detailContainerTopMargin;
            contentContainer.setLayoutParams(mlp);
        }
    }

    private int calculatePictureHeight() {
        int photoHeightPixels = 0;
        if (hasPicture) {
            photoHeightPixels = (int) (photo.getWidth() / PHOTO_ASPECT_RATIO);
            photoHeightPixels = Math.min(photoHeightPixels, scrollView.getHeight() * 2 / 3);
        }
        return photoHeightPixels;
    }

    private void setPictureHeight(int heightPixels) {
        ViewGroup.LayoutParams lp = photoContainer.getLayoutParams();
        if (lp.height != heightPixels) {
            lp.height = lastPictureHeightPixels;
            photoContainer.setLayoutParams(lp);
        }
    }
    //endregion

    //region Activity Methods
    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.event, menu);

        editMenuItem = menu.findItem(R.id.menu_edit);
        updateEditIcon();
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_edit) {
            eventDetailPresenter.editEventClick();
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_EDIT_EVENT && resultCode == RESULT_OK) {
            String idEventEdited = data.getStringExtra(NewEventActivity.KEY_EVENT_ID);
            eventDetailPresenter.resultFromEditEventInfo(idEventEdited);
        }else if (requestCode == REQUEST_EDIT_EVENT && resultCode == NewEventActivity.RESULT_EXIT_EVENT) {
            setResult(NewEventActivity.RESULT_EXIT_EVENT);
            finish();
        } else if (requestCode == REQUEST_CHOOSE_PHOTO && resultCode == Activity.RESULT_OK) {
            Uri selectedImageUri = data.getData();
            File photoFile = new File(FileChooserUtils.getPath(this, selectedImageUri));
            eventDetailPresenter.photoSelected(photoFile);
        } else if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            File photoFile = getCameraPhotoFile();
            eventDetailPresenter.photoSelected(photoFile);
        }
    }

    @Override protected void onResume() {
        super.onResume();
        eventDetailPresenter.resume();
    }

    @Override protected void onPause() {
        super.onPause();
        eventDetailPresenter.pause();
    }
    //endregion

    //region View methods
    @Override public void setEventTitle(String title) {
        titleText.setText(title);
    }

    @Override public void setEventAuthor(String author) {
        authorText.setText(author);
    }

    @Override public void setEventPicture(String picture) {
        if (picture != null) {
            hasPicture = true;
            picasso.load(picture).into(photo, new Callback() {
                @Override public void onSuccess() {
                    //Trigger image transition
                    recomputePhotoAndScrollingMetrics();
                }

                @Override public void onError() {
                    hasPicture = false;
                    recomputePhotoAndScrollingMetrics();
                }
            });
        } else {
            hasPicture = false;
        }
    }

    @Override public void showEditEventPhotoOrInfo() {
        new BottomSheet.Builder(this).title(getString(R.string.event_detail_edit_menu_title))
          .sheet(R.menu.event_edit_photo_or_info)
          .listener(new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                  switch (which) {
                      case R.id.menu_event_edit_photo:
                          eventDetailPresenter.editEventPhoto();
                          break;
                      case R.id.menu_event_edit_info:
                          eventDetailPresenter.editEventInfo();
                          break;
                  }
              }
          })
          .show();
    }

    @Override public void showPhotoPicker() {
        new BottomSheet.Builder(this).title(R.string.change_photo)
          .sheet(R.menu.profile_photo_bottom_sheet)
          .listener(new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                  switch (which) {
                      case R.id.menu_photo_gallery:
                          choosePhotoFromGallery();
                          break;
                      case R.id.menu_photo_take:
                          takePhotoFromCamera();
                          break;
                  }
              }
          })
          .show();
    }

    @Override public void showEditPicture(String picture) {
        hasPicture = true;
        if (picture == null) {
            photo.setImageDrawable(null);
            photoEditIndicator.setVisibility(View.VISIBLE);
        } else {
            photoEditIndicator.setVisibility(View.GONE);
        }
        recomputePhotoAndScrollingMetrics();
    }

    @Override public void hideEditPicture() {
        photoEditIndicator.setVisibility(View.GONE);
        recomputePhotoAndScrollingMetrics();
    }

    @Override public void showLoadingPictureUpload() {
        photoLoadingIndicator.setVisibility(View.VISIBLE);
        photoEditIndicator.setVisibility(View.GONE);
    }

    @Override public void hideLoadingPictureUpload() {
        photoLoadingIndicator.setVisibility(View.GONE);
    }

    @Override public void zoomPhoto(String picture) {
        Bundle animationBundle = ActivityOptionsCompat.makeScaleUpAnimation(photoContainer, photoContainer.getLeft(), 0,
          photoContainer.getWidth(), photoContainer.getBottom()).toBundle();
        Intent photoIntent = PhotoViewActivity.getIntentForActivity(this, picture);
        ActivityCompat.startActivity(this, photoIntent, animationBundle);
    }

    @Override public void setWatchers(List<UserModel> watchers) {
        watchersList.clearWatchers();
        watchersList.setWatchers(watchers);
    }

    @Override public void setWatchersCount(int watchersCount) {
        watchersNumber.setText(getResources().getQuantityString(R.plurals.event_watching_watchers_number,
          watchersCount,
          watchersCount));
    }

    @Override public void setCurrentUserWatching(UserModel userWatchingModel) {
        watchersList.setCurrentUserWatching(userWatchingModel);
    }

    @Override public void navigateToEditEvent(String idEvent) {
        Intent editIntent = new Intent(this, NewEventActivity.class).putExtra(NewEventActivity.KEY_EVENT_ID, idEvent);
        startActivityForResult(editIntent, REQUEST_EDIT_EVENT);
    }

    @Override public void navigateToUser(String userId) {
        Intent userProfileIntent = ProfileContainerActivity.getIntent(this, userId);
        startActivity(userProfileIntent);
    }

    @Override public void showContent() {
        if (scrollChild.getVisibility() != View.VISIBLE) {
            scrollChild.setVisibility(View.VISIBLE);
        }
        recomputePhotoAndScrollingMetrics();
    }

    @Override public void hideContent() {
        scrollChild.setVisibility(View.INVISIBLE);
    }

    @Override public void showDetail() {
        contentDetail.setVisibility(View.VISIBLE);
    }

    @Override public void showEditEventButton() {
        showEditButton = true;
        if (editMenuItem != null) {
            updateEditIcon();
        }
    }

    @Override public void hideEditEventButton() {
        showEditButton = false;
        if (editMenuItem != null) {
            updateEditIcon();
        }
    }

    @Override public void setMediaCount(Integer mediaCount) {
        eventMediaNumber.setText(mediaCount.toString());
    }

    @Override public void navigateToMedia(String idEvent, Integer eventMediaCount) {
        Intent intent = new Intent(this, EventMediaActivity.class);
        intent.putExtra(EXTRA_EVENT_ID, idEvent);
        intent.putExtra(EXTRA_EVENT_MEDIA_COUNT, eventMediaCount);
        this.startActivity(intent);
    }

    @Override public void showMediaCount() {
        eventMediaNumber.setVisibility(View.VISIBLE);
    }

    @Override public void showEmpty() {
        contentEmpty.setVisibility(View.VISIBLE);
        contentDetail.setVisibility(View.INVISIBLE);
    }

    @Override public void hideEmpty() {
        contentEmpty.setVisibility(View.GONE);
    }

    @Override public void showLoading() {
        loadingView.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override public void hideLoading() {
        swipeRefreshLayout.setRefreshing(false);
        loadingView.setVisibility(View.GONE);
    }

    @Override public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void disableCheckinButton() {
        checkinButton.setEnabled(false);
    }

    @Override
    public void enableCheckinButton() {
        checkinButton.setEnabled(true);
    }

    @Override
    public void showCheckinError() {
        Toast.makeText(this, getString(R.string.problem_while_checkin), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showCheckinConfirmation() {
        new AlertDialog.Builder(this).setMessage(R.string.checkin_notification_message)
          .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                  checkinPresenter.confirmCheckin();
              }
          })
          .setNegativeButton(R.string.cancel, null)
          .show();
    }

    @Override
    public void showCheckinDone() {
        Toast.makeText(this, getString(R.string.successfully_checked_in), Toast.LENGTH_SHORT).show();
    }
    //endregion
}
