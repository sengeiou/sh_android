package com.shootr.android.ui.activities;

import android.content.Intent;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.shootr.android.R;
import com.shootr.android.ui.base.BaseNoToolbarActivity;
import com.shootr.android.ui.model.EventModel;
import com.shootr.android.ui.model.UserWatchingModel;
import com.shootr.android.ui.presenter.SingleEventPresenter;
import com.shootr.android.ui.views.SingleEventView;
import com.shootr.android.ui.widgets.BadgeDrawable;
import com.shootr.android.ui.widgets.ObservableScrollView;
import com.shootr.android.ui.widgets.SwitchBar;
import com.shootr.android.ui.widgets.ToggleSwitch;
import com.shootr.android.ui.widgets.WatchersView;
import com.shootr.android.util.PicassoWrapper;
import com.squareup.picasso.Callback;
import java.util.List;
import javax.inject.Inject;

public class SingleEventActivity extends BaseNoToolbarActivity implements SingleEventView, ObservableScrollView.Callbacks {

    private static final int REQUEST_SELECT_EVENT = 2;
    private static final int REQUEST_CODE_EDIT = 1;
    private static final float PHOTO_ASPECT_RATIO = 2.3f;

    @InjectView(R.id.scroll) ObservableScrollView scrollView;
    @InjectView(R.id.scroll_child) View scrollChild;

    @InjectView(R.id.event_loading) View loadingView;

    @InjectView(R.id.event_photo) ImageView photo;
    @InjectView(R.id.event_photo_container) View photoContainer;

    @InjectView(R.id.event_title_container) View titleContainer;
    @InjectView(R.id.toolbar_actionbar) Toolbar toolbar;
    @InjectView(R.id.event_title) TextView titleText;
    @InjectView(R.id.event_date) TextView dateText;

    @InjectView(R.id.event_content_container) View contentContainer;
    @InjectView(R.id.event_content_empty) View contentEmpty;
    @InjectView(R.id.event_content_detail) View contentDetail;

    @InjectView(R.id.event_content_detail_switch) SwitchBar watchingSwitch;
    @InjectView(R.id.event_content_detail_watchers_number) TextView watchersNumber;
    @InjectView(R.id.event_content_detail_watchers_list) WatchersView watchersList;

    @Inject SingleEventPresenter presenter;
    @Inject PicassoWrapper picasso;

    private MenuItem notificationMenuItem;
    private int notificationIcon;
    private BadgeDrawable eventsBadgeDrawable;
    private int eventsCount;
    private Toast currentToast;
    private boolean hasPicture;
    private int lastPictureHeightPixels;
    private int lastHeaderHeightPixels;
    private MenuItem editMenuItem;
    private boolean showEditButton;
    private float headerMaxElevation;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContainerContent(R.layout.activity_event_watchers);
        initializeViews();
        setupActionbar();

        initializePresenter();
    }

    private void initializePresenter() {
        presenter.initialize(this);
    }

    private void initializeViews() {
        ButterKnife.inject(this);
        headerMaxElevation = getResources().getDimension(R.dimen.event_header_elevation);

        watchingSwitch.getSwitch().setOnBeforeCheckedChangeListener(new ToggleSwitch.OnBeforeCheckedChangeListener() {
            @Override public boolean onBeforeCheckedChanged(ToggleSwitch toggleSwitch, boolean checked) {
                presenter.sendWatching(checked);
                return false;
            }
        });
        watchersList.setOnProfileClickListener(new WatchersView.OnProfileClickListener() {
            @Override public void onProfile(Long idUser) {
                navigateToUserProfile(idUser);
            }
        });
        watchersList.setOnEditListener(new WatchersView.OnEditListener() {
            @Override public void onEdit() {
                presenter.edit();
            }
        });
        currentToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        scrollView.addCallbacks(this);

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

    private void updateNotificationIcon() {
        notificationMenuItem.setIcon(notificationIcon);
        notificationMenuItem.setVisible(true);
    }

    private void updateEventsIconBadge() {
        if (eventsBadgeDrawable != null) {
            eventsBadgeDrawable.setCount(eventsCount);
        } else {
            invalidateOptionsMenu();
        }
    }

    private void updateEditIcon() {
        editMenuItem.setVisible(showEditButton);
    }

    private void setupEventsIcon(MenuItem eventsMenuItem) {
        LayerDrawable icon = (LayerDrawable) eventsMenuItem.getIcon();
        eventsBadgeDrawable = new BadgeDrawable(this);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_badge, eventsBadgeDrawable);

        updateEventsIconBadge();
    }

    @OnClick(R.id.event_title_container)
    public void onTitleClick() {
        //TODO extract logic
        navigateToSelectEvent();
    }

    private void navigateToSelectEvent() {
        Bundle animationBundle = ActivityOptionsCompat.makeScaleUpAnimation(titleContainer, titleContainer.getLeft(), 0,
          titleContainer.getWidth(), titleContainer.getBottom()).toBundle();
        Intent intent = new Intent(this, EventsListActivity.class);
        ActivityCompat.startActivityForResult(this, intent, REQUEST_SELECT_EVENT, animationBundle);
    }

    private void navigateToUserProfile(Long idUser) {
        Intent intent = ProfileContainerActivity.getIntent(this, idUser);
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
        }else if (progress < 0) {
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
        notificationMenuItem = menu.findItem(R.id.menu_notifications);
        if (notificationIcon != 0) {
            updateNotificationIcon();
        }

        MenuItem eventsMenuItem = menu.findItem(R.id.menu_events);
        setupEventsIcon(eventsMenuItem);

        editMenuItem = menu.findItem(R.id.menu_edit);
        updateEditIcon();
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_notifications) {
            presenter.toggleNotifications();
            return true;
        } else if (item.getItemId() == R.id.menu_events) {
            navigateToSelectEvent();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_EDIT && resultCode == RESULT_OK) {
            String statusText = data.getStringExtra(EditInfoActivity.KEY_STATUS);
            presenter.resultFromEdit(statusText);
        } else if (requestCode == REQUEST_SELECT_EVENT && resultCode == RESULT_OK) {
            Long idEventSelected = data.getLongExtra(EventsListActivity.KEY_EVENT_ID, 0L);
            presenter.resultFromSelectEvent(idEventSelected);
        }
    }

    @Override protected void onResume() {
        super.onResume();
        presenter.resume();
    }

    @Override protected void onPause() {
        super.onPause();
        presenter.pause();
    }
    //endregion

    //region View methods
    @Override public void setEventTitle(String title) {
        titleText.setText(title);
    }

    @Override public void setEventDate(String date) {
        if (date != null && !date.isEmpty()) {
            dateText.setText(date);
            dateText.setVisibility(View.VISIBLE);
        } else {
            dateText.setVisibility(View.INVISIBLE);
        }
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

    @Override public void setWatchers(List<UserWatchingModel> watchers) {
        watchersList.clearWatchers();
        watchersList.setWatchers(watchers);
    }

    @Override public void setWatchersCount(int watchersCount) {
        watchersNumber.setText(
          getResources().getQuantityString(R.plurals.event_watching_watchers_number, watchersCount, watchersCount));
    }

    @Override public void setEventsCount(int eventsCount) {
        this.eventsCount = eventsCount;
        updateEventsIconBadge();
    }

    @Override public void setCurrentUserWatching(UserWatchingModel userWatchingModel) {
        watchersList.setCurrentUserWatching(userWatchingModel);
    }

    @Override public void setIsWatching(boolean watching) {
        watchingSwitch.setCheckedInternal(watching);
    }

    @Override public void setNotificationsEnabled(boolean enabled) {
        notificationIcon = enabled ? R.drawable.ic_action_notifications_on : R.drawable.ic_action_notifications_none;
        if (notificationMenuItem != null) {
            updateNotificationIcon();
        }
    }

    @Override public void alertNotificationsEnabled() {
        currentToast.setText(R.string.notifications_enabled);
        currentToast.show();
    }

    @Override public void alertNotificationsDisabled() {
        currentToast.setText(R.string.notifications_disabled);
        currentToast.show();
    }

    @Override public void navigateToEdit(EventModel eventModel, UserWatchingModel currentUserWatchingModel) {
        Intent intent = EditInfoActivity.getIntent(this, eventModel, currentUserWatchingModel);
        startActivityForResult(intent, REQUEST_CODE_EDIT);
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

    @Override public void showEmpty() {
        contentEmpty.setVisibility(View.VISIBLE);
        contentDetail.setVisibility(View.INVISIBLE);
    }

    @Override public void hideEmpty() {
        contentEmpty.setVisibility(View.GONE);
    }

    @Override public void showLoading() {
        loadingView.setVisibility(View.VISIBLE);
    }

    @Override public void hideLoading() {
        loadingView.setVisibility(View.GONE);
    }

    @Override public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    //endregion
}
