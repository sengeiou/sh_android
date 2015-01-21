package com.shootr.android.ui.activities;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.shootr.android.R;
import com.shootr.android.ui.base.BaseSignedInActivity;
import com.shootr.android.ui.model.EventModel;
import com.shootr.android.ui.model.UserWatchingModel;
import com.shootr.android.ui.presenter.SingleEventPresenter;
import com.shootr.android.ui.views.SingleEventView;
import com.shootr.android.ui.widgets.BadgeDrawable;
import com.shootr.android.ui.widgets.SwitchBar;
import com.shootr.android.ui.widgets.ToggleSwitch;
import com.shootr.android.ui.widgets.WatchersView;
import java.util.List;
import javax.inject.Inject;

public class EventActivity extends BaseSignedInActivity implements SingleEventView {

    private static final int REQUEST_SELECT_EVENT = 2;
    private static final int REQUEST_CODE_EDIT = 1;

    @InjectView(R.id.event_watchig_switch) SwitchBar watchingSwitch;
    @InjectView(R.id.event_watchers_number) TextView watchersNumber;
    @InjectView(R.id.event_watchers_list) WatchersView watchersList;
    @InjectView(R.id.event_title) TextView titleText;
    @InjectView(R.id.event_date) TextView dateText;
    @InjectView(R.id.event_empty) View emptyView;
    @InjectView(R.id.event_content) View contentView;
    @InjectView(R.id.event_title_container) View titleContainer;
    @InjectView(R.id.event_loading) View loadingView;

    @Inject SingleEventPresenter presenter;

    private MenuItem notificationMenuItem;
    private int notificationIcon;
    private BadgeDrawable eventsBadgeDrawable;
    private int eventsCount;
    private Toast currentToast;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!restoreSessionOrLogin()) {
            return;
        }
        setContainerContent(R.layout.activity_event_watchers);
        setupActionbar();
        initializeViews();

        initializePresenter();
    }

    private void initializePresenter() {
        presenter.initialize(this);
    }

    private void initializeViews() {
        ButterKnife.inject(this);
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
    }

    private void setupActionbar() {
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
        Bundle animationBundle =
          ActivityOptionsCompat.makeScaleUpAnimation(titleContainer, titleContainer.getLeft(), 0,
            titleContainer.getWidth(), titleContainer.getBottom()).toBundle();
        Intent intent = new Intent(this, EventsListActivity.class);
        ActivityCompat.startActivityForResult(this, intent, REQUEST_SELECT_EVENT, animationBundle);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.event, menu);
        notificationMenuItem = menu.findItem(R.id.menu_notifications);
        if (notificationIcon != 0) {
            updateNotificationIcon();
        }

        MenuItem eventsMenuItem = menu.findItem(R.id.menu_events);
        setupEventsIcon(eventsMenuItem);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_notifications) {
            presenter.toggleNotifications();
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
        }else if (requestCode == REQUEST_SELECT_EVENT && resultCode == RESULT_OK) {
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

    @Override public void setEventTitle(String title) {
        titleText.setText(title);
    }

    @Override public void setEventDate(String date) {
        if (date != null && !date.isEmpty()) {
            dateText.setText(date);
            dateText.setVisibility(View.VISIBLE);
        } else {
            dateText.setVisibility(View.GONE);
        }
    }

    @Override public void setWatchers(List<UserWatchingModel> watchers) {
        watchersList.setWatchers(watchers);
    }

    @Override public void setWatchersCount(int watchersCount) {
        watchersNumber.setText(getResources().getQuantityString(R.plurals.event_watching_watchers_number, watchersCount, watchersCount));
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

    private void navigateToUserProfile(Long idUser) {
        Intent intent = ProfileContainerActivity.getIntent(this, idUser);
        startActivity(intent);
    }

    @Override public void showContent() {
        if (contentView.getVisibility() != View.VISIBLE) {
            contentView.setVisibility(View.VISIBLE);
            ObjectAnimator.ofFloat(contentView, "alpha", 0f, 1f).start();
        }
    }

    @Override public void showEmpty() {
        emptyView.setVisibility(View.VISIBLE);
        contentView.setVisibility(View.GONE);
    }

    @Override public void hideEmpty() {
        emptyView.setVisibility(View.GONE);
    }

    @Override public void showLoading() {
        loadingView.setVisibility(View.VISIBLE);
    }

    @Override public void hideLoading() {
        loadingView.setVisibility(View.GONE);
        if (titleContainer.getVisibility() != View.VISIBLE) {
            showTitleAnimated();
        }
    }

    private void showTitleAnimated() {
        titleContainer.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override public boolean onPreDraw() {
                titleContainer.getViewTreeObserver().removeOnPreDrawListener(this);
                int height = titleContainer.getHeight();
                titleContainer.setTranslationY(-height);

                ObjectAnimator.ofFloat(titleContainer, "translationY", -height, 0f).setDuration(200).start();
                //titleContainer.animate().translationY(0f).setDuration(200);
                titleText.setAlpha(0f);
                titleText.animate().alpha(1f).setStartDelay(100);
                dateText.setAlpha(0f);
                dateText.animate().alpha(1f).setStartDelay(100);
                return true;
            }
        });
        titleContainer.setVisibility(View.VISIBLE);
    }

    @Override public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
