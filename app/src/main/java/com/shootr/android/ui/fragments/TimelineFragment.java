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
import com.melnykov.fab.FloatingActionButton;
import com.path.android.jobqueue.JobManager;
import com.shootr.android.data.bus.Main;
import com.shootr.android.domain.Event;
import com.shootr.android.domain.EventInfo;
import com.shootr.android.domain.Watch;
import com.shootr.android.domain.interactor.event.SelectEventInteractor;
import com.shootr.android.domain.interactor.event.VisibleEventInfoInteractor;
import com.shootr.android.domain.validation.EventValidator;
import com.shootr.android.task.events.CommunicationErrorEvent;
import com.shootr.android.ui.activities.EventDetailActivity;
import com.shootr.android.ui.activities.EventsListActivity;
import com.shootr.android.ui.activities.ShotDetailActivity;
import com.shootr.android.ui.activities.PhotoViewActivity;
import com.shootr.android.ui.presenter.WatchNumberPresenter;
import com.shootr.android.ui.views.WatchingRequestView;
import com.shootr.android.ui.widgets.BadgeDrawable;
import com.shootr.android.ui.widgets.FloatLabelLayout;
import com.shootr.android.util.AndroidTimeUtils;
import com.shootr.android.util.PicassoWrapper;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.shootr.android.ShootrApplication;
import com.shootr.android.R;
import com.shootr.android.task.events.ConnectionNotAvailableEvent;
import com.shootr.android.task.events.timeline.NewShotsReceivedEvent;
import com.shootr.android.task.events.timeline.OldShotsReceivedEvent;
import com.shootr.android.task.events.timeline.ShotsResultEvent;
import com.shootr.android.task.jobs.timeline.RetrieveFromDataBaseTimeLineJob;
import com.shootr.android.task.jobs.timeline.RetrieveInitialTimeLineJob;
import com.shootr.android.task.jobs.timeline.RetrieveNewShotsTimeLineJob;
import com.shootr.android.task.jobs.timeline.RetrieveOldShotsTimeLineJob;
import com.shootr.android.task.jobs.timeline.TimelineJob;
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
        implements SwipeRefreshLayout.OnRefreshListener, WatchingRequestView {

    private static final int REQUEST_SELECT_EVENT = 2;
    public static final int REQUEST_NEW_SHOT = 1;
    private static final long REFRESH_INTERVAL_MILLISECONDS = 10 * 1000;
    public static final int PLACEHOLDER_MAX_LENGHT = 25;

    @Inject PicassoWrapper picasso;
    @Inject @Main Bus bus;
    @Inject AndroidTimeUtils timeUtils;
    @Inject JobManager jobManager;
    @Inject WatchNumberPresenter watchNumberPresenter;
    @Inject VisibleEventInfoInteractor visibleEventInfoInteractor;
    @Inject SelectEventInteractor selectEventInteractor;

    @InjectView(R.id.timeline_list) ListView listView;
    @InjectView(R.id.timeline_new) View newShotView;
    @InjectView(R.id.timeline_new_text) TextView newShotTextView;
    @InjectView(R.id.timeline_swipe_refresh) SwipeRefreshLayout swipeRefreshLayout;
    @InjectView(R.id.select_event_fab) FloatingActionButton selectEventFab;

    @InjectView(R.id.timeline_empty) View emptyView;

    private View footerView;
    private ProgressBar footerProgress;

    private TextView footerText;
    private TimelineAdapter adapter;
    private View.OnClickListener avatarClickListener;
    private View.OnClickListener imageClickListener;
    private boolean isLoadingMore;
    private boolean isRefreshing;
    private boolean moreShots = true;
    private boolean shouldPoll;
    private BadgeDrawable badgeDrawable;
    private String newShotPlaceholder;


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

        imageClickListener = new View.OnClickListener() {
            @Override public void onClick(View v) {
                int position = ((TimelineAdapter.ViewHolder) v.getTag()).position;
                openImage(position);
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
                    if (!shouldPoll){
                        return;
                    }
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
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.register(this);
        startRetrieveFromDataBaseJob(getActivity());
        startPollingShots();
        watchNumberPresenter.resume();
        loadEventPlaceholder();
    }

    @Override
    public void onPause() {
        super.onPause();
        bus.unregister(this);
        stopPollingShots();
        watchNumberPresenter.pause();
    }

    private void loadEventPlaceholder() {
        visibleEventInfoInteractor.obtainVisibleEventInfo(new VisibleEventInfoInteractor.Callback() {
            @Override public void onLoaded(EventInfo eventInfo) {
                Event event = eventInfo.getEvent();
                if (event != null) {
                    showEventTitleInPlaceholder(event.getTitle());
                }
            }
        });
    }

    private void showEventTitleInPlaceholder(String eventTitle) {
        newShotPlaceholder = filterAndTrimEventTitle(eventTitle);
        newShotTextView.setText(newShotPlaceholder);
    }

    private String filterAndTrimEventTitle(String eventTitle) {
        eventTitle = filterTitleEmojis(eventTitle);
        if (eventTitle.length() > PLACEHOLDER_MAX_LENGHT) {
            eventTitle = eventTitle.substring(0, PLACEHOLDER_MAX_LENGHT);
            eventTitle += "...";
        }
        return eventTitle;
    }

    private String filterTitleEmojis(String eventTitle) {
        return eventTitle.replaceAll(EventValidator.EMOJI_RANGE_REGEX, "");
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
            Timber.w("Activity null in TimelineFragment#onViewCreated()",e);
        }

        // Header and footer
        footerView =
                LayoutInflater.from(getActivity()).inflate(R.layout.item_list_loading, listView, false);
        footerProgress = ButterKnife.findById(footerView, R.id.loading_progress);
        footerText = ButterKnife.findById(footerView, R.id.loading_text);

        listView.addFooterView(footerView, null, false);

        adapter = new TimelineAdapter(getActivity(), picasso, avatarClickListener, imageClickListener, timeUtils);
        listView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.refresh_1, R.color.refresh_2, R.color.refresh_3,
          R.color.refresh_4);

        // List scroll stuff
        new ListViewScrollObserver(listView).setOnScrollUpAndDownListener(
                new ListViewScrollObserver.OnListViewScrollListener() {
                    @Override
                    public void onScrollUpDownChanged(int delta, int scrollPosition, boolean exact) {
                        if (delta < -10) {
                            selectEventFab.hide();
                        } else if(delta > 10) {
                            selectEventFab.show();
                        }
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
        loadInitialTimeline();
        watchNumberPresenter.initialize(this);
    }

    @Override
    public void setWatchingPeopleCount(Integer count){
        setBadgeCount(count);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.timeline, menu);
        menu.findItem(R.id.menu_search).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        MenuItem menuItem = menu.findItem(R.id.menu_info);
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        LayerDrawable icon = (LayerDrawable) getResources().getDrawable(R.drawable.badge_circle);
        icon.setDrawableByLayerId(R.id.ic_people, getResources().getDrawable(R.drawable.ic_action_ic_one_people));
        setBadgeIcon(getActivity(), icon, 0);
        menuItem.setIcon(icon);
        menuItem.getIcon();
        watchNumberPresenter.menuCreated();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_info:
                startActivity(new Intent(getActivity(), EventDetailActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
        if (requestCode == REQUEST_NEW_SHOT && resultCode == Activity.RESULT_OK) {
            /* no-op */
        }else if (requestCode == REQUEST_SELECT_EVENT && resultCode == Activity.RESULT_OK) {
            Long idEventSelected = data.getLongExtra(EventsListActivity.KEY_EVENT_ID, 0L);
            selectEventInteractor.selectEvent(idEventSelected, new SelectEventInteractor.Callback() {
                @Override public void onLoaded(Watch watch) {
                    onEventChanged();
                }
            });
        }
    }

    private void onEventChanged() {
        this.loadEventPlaceholder();
        watchNumberPresenter.initialize(this);
    }

    /* --- UI Events --- */

    @OnClick(R.id.select_event_fab)
    public void selectEvent() {
        Intent intent = new Intent(getActivity(), EventsListActivity.class);
        startActivityForResult(intent, REQUEST_SELECT_EVENT);
    }


    @OnClick(R.id.timeline_new_text)
    public void startNewShot() {
        Intent intent = new Intent(getActivity(), PostNewShotActivity.class);
        startNewShotActivityWithPlaceholder(intent);
    }

    @OnClick(R.id.timeline_new_image_camera)
    public void startNewShotFromCamera() {
        Intent intent = new Intent(getActivity(), PostNewShotActivity.class);
        intent.putExtra(PostNewShotActivity.EXTRA_DEFAULT_INPUT_MODE, PostNewShotActivity.INPUT_CAMERA);
        startNewShotActivityWithPlaceholder(intent);
    }

    @OnClick(R.id.timeline_new_image_gallery)
    public void startNewShotFromGallery() {
        Intent intent = new Intent(getActivity(), PostNewShotActivity.class);
        intent.putExtra(PostNewShotActivity.EXTRA_DEFAULT_INPUT_MODE, PostNewShotActivity.INPUT_GALLERY);
        startNewShotActivityWithPlaceholder(intent);
    }

    private void startNewShotActivityWithPlaceholder(Intent intent) {
        intent.putExtra(PostNewShotActivity.KEY_PLACEHOLDER, newShotPlaceholder);
        startActivityForResult(intent, REQUEST_NEW_SHOT);
    }

    @OnItemClick(R.id.timeline_list)
    public void openShot(int position) {
        ShotModel shot = adapter.getItem(position);
        Intent intent = ShotDetailActivity.getIntentForActivity(getActivity(), shot);
        startActivity(intent);
    }

    public void openProfile(int position) {
        ShotModel shotVO = adapter.getItem(position);
        Intent profileIntent = ProfileContainerActivity.getIntent(getActivity(), shotVO.getIdUser());
        startActivity(profileIntent);
    }

    public void openImage(int position) {
        ShotModel shotVO = adapter.getItem(position);
        String imageUrl = shotVO.getImage();
        if (imageUrl != null) {
            Intent intentForImage = PhotoViewActivity.getIntentForActivity(getActivity(), imageUrl);
            startActivity(intentForImage);
        }
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
        if (shots != null && !shots.isEmpty()) {
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

    public void setBadgeIcon(Context context, LayerDrawable icon, int count) {
        // Reuse drawable if possible
        if (badgeDrawable == null) {
            Drawable reuse = icon.findDrawableByLayerId(R.id.ic_badge);
            if (reuse != null && reuse instanceof BadgeDrawable) {
                badgeDrawable = (BadgeDrawable) reuse;
            } else {
                badgeDrawable = new BadgeDrawable(context);
            }
        }
        setBadgeCount(count);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_badge, badgeDrawable);
    }

    private void setBadgeCount(int count) {
        if (badgeDrawable != null) {
            badgeDrawable.setCount(count);
        } else {
            getActivity().invalidateOptionsMenu();
        }
    }
}

