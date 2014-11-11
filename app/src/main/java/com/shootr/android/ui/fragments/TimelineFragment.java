package com.shootr.android.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;
import com.path.android.jobqueue.JobManager;
import com.shootr.android.db.objects.ShotEntity;
import com.shootr.android.gcm.event.RequestWatchByPushEvent;
import com.shootr.android.task.events.CommunicationErrorEvent;
import com.shootr.android.task.events.info.WatchingInfoResult;
import com.shootr.android.task.events.timeline.WatchingPeopleNumberEvent;
import com.shootr.android.task.events.timeline.WatchingRequestPendingEvent;
import com.shootr.android.task.jobs.info.GetWatchingInfoJob;
import com.shootr.android.task.jobs.info.SetWatchingInfoOfflineJob;
import com.shootr.android.task.jobs.info.SetWatchingInfoOnlineJob;
import com.shootr.android.task.jobs.timeline.GetWatchingPeopleNumberJob;
import com.shootr.android.task.jobs.timeline.GetWatchingRequestsPendingJob;
import com.shootr.android.ui.model.WatchingRequestModel;
import com.shootr.android.ui.widgets.BadgeDrawable;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.shootr.android.ShootrApplication;
import com.shootr.android.R;
import com.shootr.android.db.objects.UserEntity;
import com.shootr.android.task.events.ConnectionNotAvailableEvent;
import com.shootr.android.task.events.timeline.NewShotsReceivedEvent;
import com.shootr.android.task.events.timeline.OldShotsReceivedEvent;
import com.shootr.android.task.events.timeline.ShotsResultEvent;
import com.shootr.android.task.jobs.timeline.RetrieveFromDataBaseTimeLineJob;
import com.shootr.android.task.jobs.timeline.RetrieveInitialTimeLineJob;
import com.shootr.android.task.jobs.timeline.RetrieveNewShotsTimeLineJob;
import com.shootr.android.task.jobs.timeline.RetrieveOldShotsTimeLineJob;
import com.shootr.android.task.jobs.timeline.TimelineJob;
import com.shootr.android.ui.activities.InfoActivity;
import com.shootr.android.ui.activities.PostNewShotActivity;
import com.shootr.android.ui.activities.ProfileContainerActivity;
import com.shootr.android.ui.adapters.TimelineAdapter;
import com.shootr.android.ui.base.BaseActivity;
import com.shootr.android.ui.base.BaseFragment;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.widgets.ListViewScrollObserver;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class TimelineFragment extends BaseFragment
        implements SwipeRefreshLayout.OnRefreshListener {

    public static final int REQUEST_NEW_SHOT = 1;
    private static final long REFRESH_INTERVAL_MILLISECONDS = 10 * 1000;
    public static final Long WATCH_STATUS_IGNORE = 2L;
    public static final Long WATCH_STATUS_WATCHING = 1L;

    @Inject Picasso picasso;
    @Inject Bus bus;
    @Inject JobManager jobManager;

    @InjectView(R.id.timeline_list) ListView listView;
    @InjectView(R.id.timeline_new) View newShotView;
    @InjectView(R.id.timeline_swipe_refresh) SwipeRefreshLayout swipeRefreshLayout;
    @InjectView(R.id.timeline_empty) View emptyView;

    @InjectView(R.id.timeline_watching_container) View watchingRequestContainerView;
    @InjectView(R.id.timeline_watching_title) TextView watchingRequestTitleView;
    @InjectView(R.id.timeline_watching_subtitle) TextView watchingRequestSubtitleView;
    @InjectView(R.id.timeline_watching_action_ignore) View watchingRequestActionIgnoreView;
    @InjectView(R.id.timeline_watching_action_yes) View watchingRequestActionYesView;

    private View footerView;
    private ProgressBar footerProgress;
    private TextView footerText;

    private TimelineAdapter adapter;
    private View.OnClickListener avatarClickListener;
    private boolean isLoadingMore;
    private boolean isRefreshing;
    private boolean moreShots = true;
    private boolean shouldPoll;
    private UserEntity currentUser;
    private List<WatchingRequestModel> watchingRequestsPendingStack;
    private int numNotificationBadge;


     /* ---- Lifecycle methods ---- */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        avatarClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = ((TimelineAdapter.ViewHolder) v.getTag()).position;
                openProfile(position);
            }
        };
    }

    private void startPollingShots() {
        shouldPoll = true;
        pollShots();
    }

    private void stopPollingShots() {
        shouldPoll = false;
    }

    private void pollShots() {
        if (shouldPoll) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!shouldPoll) return;
                    Context context = getActivity();
                    if (context != null) {
                        startRetrieveNewShotsTimeLineJob(context);
                    }
                    pollShots();
                }
            }, REFRESH_INTERVAL_MILLISECONDS);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        bus.register(this);
        currentUser = ShootrApplication.get(activity).getCurrentUser();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        bus.unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        startUpdateNotificationBadge();
        startRetrieveFromDataBaseJob(getActivity());
        startPollingShots();
    }

    public void startUpdateNotificationBadge(){
        GetWatchingPeopleNumberJob job = ShootrApplication.get(getActivity()).getObjectGraph().get(GetWatchingPeopleNumberJob.class);
        jobManager.addJobInBackground(job);
    }

    @Subscribe
    public void onNumberReceived(WatchingPeopleNumberEvent event){
        numNotificationBadge = event.getResult();
        updateNotificationBadge(numNotificationBadge);
    }

    @Override
    public void onPause() {
        super.onPause();
        stopPollingShots();
    }

    @Subscribe
    public void onConnectionNotAvailable(ConnectionNotAvailableEvent event) {
        Toast.makeText(getActivity(), R.string.connection_lost, Toast.LENGTH_SHORT).show();
        swipeRefreshLayout.setRefreshing(false);
        isLoadingMore = false;
        isRefreshing = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_timeline, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

        //TODO change by drawerLayout, not the fragment itself
        try {
            ((BaseActivity) getActivity()).getSupportActionBar().setTitle("Timeline");
        } catch (NullPointerException e) {
            Timber.w("Activity null in TimelineFragment#onViewCreated()");
        }

        // Header and footer
        footerView =
                LayoutInflater.from(getActivity()).inflate(R.layout.item_list_loading, listView, false);
        footerProgress = ButterKnife.findById(footerView, R.id.loading_progress);
        footerText = ButterKnife.findById(footerView, R.id.loading_text);

        listView.addFooterView(footerView, null, false);

        adapter = new TimelineAdapter(getActivity(), picasso, avatarClickListener);
        listView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.refresh_1, R.color.refresh_2, R.color.refresh_3,
          R.color.refresh_4);

        // List scroll stuff
        new ListViewScrollObserver(listView).setOnScrollUpAndDownListener(
                new ListViewScrollObserver.OnListViewScrollListener() {
                    @Override
                    public void onScrollUpDownChanged(int delta, int scrollPosition, boolean exact) {
                    }

                    @Override
                    public void onScrollIdle() {
                        loadMoreShotsIfNeeded();
                    }
                });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        currentUser = ShootrApplication.get(getActivity()).getCurrentUser();
        loadInitialTimeline();
        refreshInfoData();
    }

    private void refreshInfoData() {
        GetWatchingInfoJob getWatchingInfoJob =
          ShootrApplication.get(getActivity()).getObjectGraph().get(GetWatchingInfoJob.class);

        getWatchingInfoJob.init(true);

        jobManager.addJobInBackground(getWatchingInfoJob);
    }

    @Subscribe
    public void onInfoDataRefreshed(WatchingInfoResult event) {
        startUpdateNotificationBadge();
        startRetrievingWatchingRequests();
    }

    private void startRetrievingWatchingRequests() {
        GetWatchingRequestsPendingJob getWatchingRequestsPendingJob  = ShootrApplication.get(getActivity()).getObjectGraph().get(GetWatchingRequestsPendingJob.class);
        jobManager.addJobInBackground(getWatchingRequestsPendingJob);
    }

    @Subscribe
    public void onRequestWatchByPush(RequestWatchByPushEvent event){

        startUpdateNotificationBadge();
        startRetrievingWatchingRequests();
    }

    @Subscribe
    public void onWatchingRequestsPendingReceived(WatchingRequestPendingEvent event) {
        List<WatchingRequestModel> watchingRequestModels = event.getResult();
        if (watchingRequestModels != null && watchingRequestModels.size() > 0) {
            watchingRequestsPendingStack = watchingRequestModels;
            showNextWatchingRequest();
        }

    }

    private void showNextWatchingRequest() {
        if (watchingRequestsPendingStack.size() > 0) {
            WatchingRequestModel watchingRequestModel = watchingRequestsPendingStack.get(0);
            showNextWatchingRequestDelayed(watchingRequestModel);
        } else {
            hideWatchingRequests();
        }
    }

    private void showNextWatchingRequestDelayed(final WatchingRequestModel watchingRequestModel) {
        watchingRequestContainerView.setVisibility(View.GONE);
        new Handler().postDelayed(new Runnable() {
            @Override public void run() {
                showWatchingRequest(watchingRequestModel);
            }
        }, 1000);
    }

    private void showWatchingRequest(WatchingRequestModel watchingRequestModel) {
        if (watchingRequestModel != null) {
            watchingRequestContainerView.setVisibility(View.VISIBLE);
            watchingRequestTitleView.setText(watchingRequestModel.getTitle());
            watchingRequestSubtitleView.setText(watchingRequestModel.getSubtitle());
        }
    }

    private void hideWatchingRequests() {
        watchingRequestContainerView.setVisibility(View.GONE);
    }

    @OnClick(R.id.timeline_watching_action_yes)
    public void onWatchRequestAnswerPositive() {
        answerWatchRequest(WATCH_STATUS_WATCHING);
    }

    @OnClick(R.id.timeline_watching_action_ignore)
    public void onWatchRequestAnswerNegative() {
        answerWatchRequest(WATCH_STATUS_IGNORE);
    }

    private void answerWatchRequest(Long status) {
        WatchingRequestModel watchingRequestModel = watchingRequestsPendingStack.get(0);
        watchingRequestsPendingStack.remove(0);

        SetWatchingInfoOfflineJob jobOffline = ShootrApplication.get(getActivity()).getObjectGraph().get(SetWatchingInfoOfflineJob.class);
        jobOffline.init(watchingRequestModel.getMatchId(),status);
        jobManager.addJobInBackground(jobOffline);
        SetWatchingInfoOnlineJob jobOnline = ShootrApplication.get(getActivity()).getObjectGraph().get(SetWatchingInfoOnlineJob.class);
        jobManager.addJobInBackground(jobOnline);
        showNextWatchingRequest();
    }

    private void updateNotificationBadge(int count){
        numNotificationBadge = count;
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.timeline, menu);

        menu.findItem(R.id.menu_search).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        MenuItem menuItem = menu .findItem(R.id.menu_info);
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        LayerDrawable icon = (LayerDrawable) menuItem.getIcon();
        if(numNotificationBadge == 0){
            icon.setDrawableByLayerId(R.id.ic_people,getResources().getDrawable(R.drawable.ic_action_social_people_outline));
        }else{
            icon.setDrawableByLayerId(R.id.ic_people,getResources().getDrawable(R.drawable.ic_action_ic_one_people));
        }
        setBadgeCount(getActivity(),icon,numNotificationBadge);

        super.onCreateOptionsMenu(menu,inflater);
     }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_info:
                startActivity(new Intent(getActivity(), InfoActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        Context context = getActivity();
        if (context != null) {
            startRefreshing(context);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_NEW_SHOT) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(getActivity(), "Shot sent", Toast.LENGTH_SHORT);
                startRefreshing(getActivity());
            }
        }
    }

    /* --- UI Events --- */

    @OnClick(R.id.timeline_new_text)
    public void startNewShot() {
        Bundle anim =
                ActivityOptionsCompat.makeScaleUpAnimation(newShotView, 0, 0, newShotView.getWidth(),
                        newShotView.getHeight()).toBundle();
        Intent intent = new Intent(getActivity(), PostNewShotActivity.class);
        intent.putExtras(anim);
        ActivityCompat.startActivityForResult(getActivity(), intent, REQUEST_NEW_SHOT, anim);
    }

    @OnItemClick(R.id.timeline_list)
    public void openShot(int position) {
        ShotModel shot = adapter.getItem(position - 1);
        Timber.d("Clicked shot %d", position);
    }

    public void openProfile(int position) {
        Long currentUserId = currentUser.getIdUser();
        ShotModel shotVO = adapter.getItem(position);

        Intent profileIntent = ProfileContainerActivity.getIntent(getActivity(), shotVO.getIdUser());
        startActivity(profileIntent);
    }

    public void startRefreshing(Context context) {
        if (!isRefreshing) {
            isRefreshing = true;
            swipeRefreshLayout.setRefreshing(true);
            Timber.d("Start new timeline refresh");
            startRetrieveNewShotsTimeLineJob(context);
        }
    }

    private void startRetrieveFromDataBaseJob(Context context){
        RetrieveFromDataBaseTimeLineJob job = ShootrApplication.get(context).getObjectGraph().get(RetrieveFromDataBaseTimeLineJob.class);
        startJob(job);
    }

    private void startRetrieveOldShotsTimeLineJob(Context context){
        RetrieveOldShotsTimeLineJob job = ShootrApplication.get(context).getObjectGraph().get(RetrieveOldShotsTimeLineJob.class);
        startJob(job);
    }

    private void startRetrieveInitialTimeLineJob(Context context){
        RetrieveInitialTimeLineJob job = ShootrApplication.get(context).getObjectGraph().get(
          RetrieveInitialTimeLineJob.class);
        startJob(job);
    }

    private void startRetrieveNewShotsTimeLineJob(Context context) {
        RetrieveNewShotsTimeLineJob job = ShootrApplication.get(context).getObjectGraph().get(RetrieveNewShotsTimeLineJob.class);
        startJob(job);
    }

    private void startJob(TimelineJob job){
        job.init(currentUser);
        jobManager.addJobInBackground(job);
    }

    private void loadMoreShotsIfNeeded() {
        int lastVisiblePosition = listView.getLastVisiblePosition();
        if (lastVisiblePosition >= listView.getAdapter().getCount()-1) {
            Context context = getActivity();
            if (context != null) {
                startLoadMoreShots(context);
            }
        }
    }

    //TODO parameter: last shot as offset
    public void startLoadMoreShots(Context context) {
        if (!isLoadingMore && moreShots) {
            isLoadingMore = true;
            Timber.d("Start loading more shots");
            startRetrieveOldShotsTimeLineJob(context);
        }
    }

    public void loadInitialTimeline() {
        startRetrieveInitialTimeLineJob(getActivity());
        swipeRefreshLayout.setRefreshing(true);
    }

    @Subscribe
    public void showTimeline(ShotsResultEvent event) {
        List<ShotModel> shots = event.getResult();
        swipeRefreshLayout.setRefreshing(false);
        if (shots != null && shots.size() > 0) {
            adapter.setShots(shots);
            setEmpty(false);
        } else {
            setEmpty(true);
            Timber.i("No shots received");
        }
        loadMoreShotsIfNeeded();
    }

    private void setEmpty(boolean empty) {
        emptyView.setVisibility(empty ? View.VISIBLE : View.GONE);
        swipeRefreshLayout.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    @Subscribe
    public void displayNewShots(NewShotsReceivedEvent event) {
        isRefreshing = false;
        swipeRefreshLayout.setRefreshing(false);
        int newShotsCount = event.getNewShotsCount();
        List<ShotModel> updatedTimeline = event.getResult();

        if (newShotsCount == 0) {
            Timber.i("No new shots");
            if(adapter != null){ //Means that We have at least one shot and for that reason the adapter was initialized
                adapter.notifyDataSetChanged(); // Refresh time indicator
            }else{
                //TODO why is this empty?
            }
        } else {
            Timber.i("Received %d new shots", newShotsCount);
            int originalPosition = listView.getFirstVisiblePosition();
            int newPosition = originalPosition + newShotsCount;
            adapter.setShots(updatedTimeline);
            setListPosition(newPosition);
            if (shouldGoToTop()) {
                goToTop();
            }
        }
    }

    private boolean shouldGoToTop() {
        return listView.getFirstVisiblePosition() == 0;
    }

    public void setListPosition(int position) {
        listView.setSelection(position);
    }

    public void goToTop() {
        listView.smoothScrollToPosition(0);
    }

    @Subscribe
    public void displayOldShots(OldShotsReceivedEvent event) {
        isLoadingMore = false;
        List<ShotModel> shots = event.getResult();
        int olderShotsSize = shots.size();
        if (olderShotsSize == 0) {
            footerProgress.setVisibility(View.INVISIBLE); // Maintain size
            footerText.setVisibility(View.VISIBLE);
            footerText.setText(R.string.no_more_shots);
            moreShots = false;
        } else {
            Timber.d("Received %d old shots", olderShotsSize);
            adapter.addShotsBelow(shots);
        }
    }

    @Subscribe
    public void onCommunicationError(CommunicationErrorEvent event) {
        Toast.makeText(getActivity(), R.string.communication_error, Toast.LENGTH_SHORT).show();
        isRefreshing = false;
        swipeRefreshLayout.setRefreshing(false);
    }


    public void setBadgeCount(Context context, LayerDrawable icon, int count) {
        BadgeDrawable badge;
        // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_badge);
        if (reuse != null && reuse instanceof BadgeDrawable) {
            badge = (BadgeDrawable) reuse;
        } else {
            badge = new BadgeDrawable(context);
        }
        badge.setCount(count);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_badge, badge);
    }
}

