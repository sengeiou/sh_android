package com.shootr.android.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.shootr.android.R;
import com.shootr.android.ui.base.BaseSignedInActivity;
import com.shootr.android.ui.model.MatchModel;
import com.shootr.android.ui.model.UserWatchingModel;
import com.shootr.android.ui.presenter.SingleEventPresenter;
import com.shootr.android.ui.views.SingleEventView;
import com.shootr.android.ui.widgets.SwitchBar;
import com.shootr.android.ui.widgets.WatchersView;
import java.util.List;
import javax.inject.Inject;

public class EventActivity extends BaseSignedInActivity implements SingleEventView {

    private static final int REQUEST_CODE_EDIT = 1;

    @InjectView(R.id.event_watchig_switch) SwitchBar watchingSwitch;
    @InjectView(R.id.event_watchers_number) TextView watchersNumber;
    @InjectView(R.id.event_watchers_list) WatchersView watchersList;
    @InjectView(R.id.event_title) TextView titleText;
    @InjectView(R.id.event_date) TextView dateText;

    @Inject SingleEventPresenter presenter;

    private MenuItem notificationMenuItem;
    private int notificationIcon;

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
        watchersList.setOnEditListener(new WatchersView.OnEditListener() {
            @Override public void onEdit() {
                presenter.edit();
            }
        });
    }

    private void setupActionbar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.event, menu);
        notificationMenuItem = menu.findItem(R.id.menu_notifications);
        if (notificationIcon != 0) {
            notificationMenuItem.setIcon(notificationIcon);
        }
        return true;
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_EDIT && resultCode == RESULT_OK) {
            presenter.loadEventInfo();
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
        dateText.setText(date);
    }

    @Override public void setWatchers(List<UserWatchingModel> watchers) {
        watchersList.setWatchers(watchers);
    }

    @Override public void setWatchersCount(int watchersCount) {
        watchersNumber.setText(getString(R.string.event_watching_watchers_number, watchersCount));
    }

    @Override public void setCurrentUserWatching(UserWatchingModel userWatchingModel) {
        watchersList.setCurrentUserWatching(userWatchingModel);
    }

    @Override public void setIsWatching(boolean watching) {
        watchingSwitch.setChecked(watching);
    }

    @Override public void setNotificationsEnabled(boolean enabled) {
        notificationIcon = enabled ? R.drawable.ic_action_notifications_on : R.drawable.ic_action_notifications_none;
        if (notificationMenuItem != null) {
            notificationMenuItem.setIcon(notificationIcon);
        }
    }

    @Override public void navigateToEdit(MatchModel eventModel, UserWatchingModel currentUserWatchingModel) {
        Intent intent = EditInfoActivity.getIntent(this, eventModel, currentUserWatchingModel);
        startActivityForResult(intent, REQUEST_CODE_EDIT);
    }

    @Override public void showEmpty() {
        //TODO
    }

    @Override public void hideEmpty() {
        //TODO
    }

    @Override public void showLoading() {
        //TODO
    }

    @Override public void hideLoading() {
        //TODO
    }

    @Override public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
