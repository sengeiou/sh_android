package com.shootr.android.ui.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.shootr.android.R;
import com.shootr.android.ui.base.BaseSignedInActivity;
import com.shootr.android.ui.model.UserWatchingModel;
import com.shootr.android.ui.presenter.SingleEventPresenter;
import com.shootr.android.ui.views.SingleEventView;
import com.shootr.android.ui.widgets.SwitchBar;
import com.shootr.android.ui.widgets.WatchersView;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class EventActivity extends BaseSignedInActivity implements SingleEventView {

    @InjectView(R.id.event_watchig_switch) SwitchBar watchingSwitch;
    @InjectView(R.id.event_watchers_number) TextView watchersNumber;
    @InjectView(R.id.event_watchers_list) WatchersView watchersList;
    @InjectView(R.id.event_title) TextView titleText;

    @Inject SingleEventPresenter presenter;

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

    private void mockData() {
        setEventTitle("Atlético-Barcelona 18:30");
        setWatchers(mockWatchers());
        setCurrentUserWatching(mockCurrentUser());
        setWatchersCount(3);
    }

    private UserWatchingModel mockCurrentUser() {
        UserWatchingModel watch1 = new UserWatchingModel();
        watch1.setUserName("Rafa");
        watch1.setPhoto("https://pbs.twimg.com/profile_images/2576254530/xrq3ziszvvt90xf54579_bigger.png");
        watch1.setPlace("Texto libre weee");
        return watch1;
    }

    private List<UserWatchingModel> mockWatchers() {
        List<UserWatchingModel> userWatchingMocks = new ArrayList<>();
        UserWatchingModel watch1 = new UserWatchingModel();
        watch1.setUserName("Ignasi");
        watch1.setPhoto("https://pbs.twimg.com/profile_images/462743664511307776/1GYsZlsU_bigger.jpeg");
        watch1.setPlace("Watching");

        UserWatchingModel watch2 = new UserWatchingModel();
        watch2.setUserName("Inma");
        watch2.setPhoto("https://pbs.twimg.com/profile_images/469241262563139584/0IWhWGMq_bigger.jpeg");
        watch2.setPlace("Watching");

        UserWatchingModel watch3 = new UserWatchingModel();
        watch3.setUserName("Teo");
        watch3.setPhoto("https://pbs.twimg.com/profile_images/474528174508740608/KANamxCB_bigger.jpeg");
        watch3.setPlace("Watching");

        userWatchingMocks.add(watch1);
        userWatchingMocks.add(watch2);
        userWatchingMocks.add(watch3);

        return userWatchingMocks;
    }

    private void initializeViews() {
        ButterKnife.inject(this);
    }

    private void setupActionbar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
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

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.event, menu);
        return true;
    }

    @Override public void showEmpty() {

    }

    @Override public void hideEmpty() {

    }

    @Override public void showLoading() {

    }

    @Override public void hideLoading() {

    }

    @Override public void showError(String message) {

    }
}
