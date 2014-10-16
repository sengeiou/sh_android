package gm.mobi.android.ui.fragments;

import android.animation.TimeInterpolator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;
import com.path.android.jobqueue.JobManager;
import com.path.android.jobqueue.persistentQueue.sqlite.DbOpenHelper;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import es.oneoctopus.swiperefreshlayoutoverlay.SwipeRefreshLayoutOverlay;
import gm.mobi.android.GolesApplication;
import gm.mobi.android.R;
import gm.mobi.android.db.manager.FollowManager;
import gm.mobi.android.db.objects.Follow;
import gm.mobi.android.db.objects.Shot;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.gcm.notifications.BagdadNotificationManager;
import gm.mobi.android.task.events.ConnectionNotAvailableEvent;
import gm.mobi.android.task.events.timeline.NewShotsReceivedEvent;
import gm.mobi.android.task.events.timeline.OldShotsReceivedEvent;
import gm.mobi.android.task.events.timeline.ShotsResultEvent;
import gm.mobi.android.task.jobs.timeline.RetrieveFromDataBaseTimeLineJob;
import gm.mobi.android.task.jobs.timeline.RetrieveInitialTimeLineJob;
import gm.mobi.android.task.jobs.timeline.RetrieveNewShotsTimeLineJob;
import gm.mobi.android.task.jobs.timeline.RetrieveOldShotsTimeLineJob;
import gm.mobi.android.task.jobs.timeline.TimelineJob;
import gm.mobi.android.ui.activities.PostNewShotActivity;
import gm.mobi.android.ui.activities.ProfileContainerActivity;
import gm.mobi.android.ui.adapters.TimelineAdapter;
import gm.mobi.android.ui.base.BaseActivity;
import gm.mobi.android.ui.base.BaseFragment;
import gm.mobi.android.ui.model.ShotVO;
import gm.mobi.android.ui.model.UserVO;
import gm.mobi.android.ui.model.mappers.ShotVOMapper;
import gm.mobi.android.ui.model.mappers.UserVOMapper;
import gm.mobi.android.ui.widgets.ListViewScrollObserver;
import java.sql.SQLException;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class TimelineFragment extends BaseFragment
        implements SwipeRefreshLayoutOverlay.OnRefreshListener {

    public static final int REQUEST_NEW_SHOT = 1;
    private static final long REFRESH_INTERVAL_MILLISECONDS = 10 * 1000;

    @Inject Picasso picasso;
    @Inject Bus bus;
    @Inject JobManager jobManager;
    @Inject BagdadNotificationManager notificationManager;
    @Inject ShotVOMapper shotMapper;

    @InjectView(R.id.timeline_list) ListView listView;
    @InjectView(R.id.timeline_new) View newShotView;
    @InjectView(R.id.timeline_watching_container) View watchingContainer;
    @InjectView(R.id.timeline_swipe_refresh) SwipeRefreshLayoutOverlay swipeRefreshLayout;
    @InjectView(R.id.timeline_empty) View emptyView;

    private View headerView;
    private View footerView;
    private ProgressBar footerProgress;
    private TextView footerText;

    private TimelineAdapter adapter;
    private View.OnClickListener avatarClickListener;
    private boolean isLoadingMore;
    private boolean isRefreshing;
    private int watchingHeight;
    private boolean moreShots = true;
    private boolean shouldPoll;
    private User currentUser;


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
                        try {
                            startRetrieveNewShotsTimeLineJob(context);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
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
        currentUser = GolesApplication.get(activity).getCurrentUser();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        bus.unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        startRetrieveFromDataBaseJob(getActivity());
        startPollingShots();
        clearCurrentNotifications();
    }

    private void clearCurrentNotifications() {
        //TODO should we? notificationManager.clearShotNotifications();
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
        headerView =
                LayoutInflater.from(getActivity()).inflate(R.layout.timeline_margin, listView, false);
        footerView =
                LayoutInflater.from(getActivity()).inflate(R.layout.item_list_loading, listView, false);
        footerProgress = ButterKnife.findById(footerView, R.id.loading_progress);
        footerText = ButterKnife.findById(footerView, R.id.loading_text);

        listView.addHeaderView(headerView, null, false);
        listView.addFooterView(footerView, null, false);

        watchingHeight = getResources().getDimensionPixelOffset(R.dimen.watching_bar_height);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.refresh_1, R.color.refresh_2,
                R.color.refresh_3, R.color.refresh_4);
        swipeRefreshLayout.setTopMargin(watchingHeight);

        // List scroll stuff
        new ListViewScrollObserver(listView).setOnScrollUpAndDownListener(
                new ListViewScrollObserver.OnListViewScrollListener() {
                    public TimeInterpolator mInterpolator = new AccelerateDecelerateInterpolator();

                    @Override
                    public void onScrollUpDownChanged(int delta, int scrollPosition, boolean exact) {
                        // delta negativo: scoll abajo
                        if (delta < -10 && scrollPosition < -watchingHeight && !isRefreshing) { //Hide
                            watchingContainer.animate()
                                    .setInterpolator(mInterpolator)
                                    .setDuration(200)
                                    .translationY(-watchingHeight);
                        } else if (delta > 10) { // Show
                            watchingContainer.animate()
                                    .setInterpolator(mInterpolator)
                                    .setDuration(200)
                                    .translationY(0)
                                    .start();
                        }
                    }

                    @Override
                    public void onScrollIdle() throws SQLException {
                        int lastVisiblePosition = listView.getLastVisiblePosition();
                        if (lastVisiblePosition >= listView.getAdapter().getCount()-1) {
                            Context context = getActivity();
                            if (context != null) {
                                startLoadMoreShots(context);
                            }
                        }
                    }
                });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        currentUser = GolesApplication.get(getActivity()).getCurrentUser();
        loadInitialTimeline();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.timeline, menu);
        // Little hack for ActionBarCompat
        menu.findItem(R.id.menu_search).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() throws SQLException {
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
                try {
                    startRefreshing(getActivity());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
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
        startActivityForResult(intent, REQUEST_NEW_SHOT);
        // ActivityCompat.startActivityForResult(getActivity(), intent, REQUEST_NEW_SHOT, anim);
    }

    @OnItemClick(R.id.timeline_list)
    public void openShot(int position) {
        //TODO Shot detail
        //Shot shot = adapter.getItem(position - 1);
        Timber.d("Clicked shot %d", position);
    }

    public void openProfile(int position) {
        Long currentUserId = currentUser.getIdUser();
        ShotVO shotVO = adapter.getItem(position);
         ShotVOMapper shotVOMapper = new ShotVOMapper();
        UserVO userVO = shotVOMapper.userVOFromShotVO(shotVO);

        Intent profileIntent = ProfileContainerActivity.getIntent(getActivity(), userVO);
        startActivity(profileIntent);
    }

    public void startRefreshing(Context context) throws SQLException {
        if (!isRefreshing) {
            isRefreshing = true;
            swipeRefreshLayout.setRefreshing(true);
            Timber.d("Start new timeline refresh");
            //User currentUser = GolesApplication.get(context).getCurrentUser();
            startRetrieveNewShotsTimeLineJob(context);
        }
    }

    private void startRetrieveFromDataBaseJob(Context context){
        RetrieveFromDataBaseTimeLineJob job = GolesApplication.get(context).getObjectGraph().get(RetrieveFromDataBaseTimeLineJob.class);
        startJob(job);
    }

    private void startRetrieveOldShotsTimeLineJob(Context context){
        RetrieveOldShotsTimeLineJob job = GolesApplication.get(context).getObjectGraph().get(RetrieveOldShotsTimeLineJob.class);
        startJob(job);
    }

    private void startRetrieveInitialTimeLineJob(Context context){
        RetrieveInitialTimeLineJob job = GolesApplication.get(context).getObjectGraph().get(
          RetrieveInitialTimeLineJob.class);
        startJob(job);
    }

    private void startRetrieveNewShotsTimeLineJob(Context context) throws SQLException {
        RetrieveNewShotsTimeLineJob job = GolesApplication.get(context).getObjectGraph().get(RetrieveNewShotsTimeLineJob.class);
        startJob(job);
    }

    private void startJob(TimelineJob job){
        job.init(currentUser);
        jobManager.addJobInBackground(job);
    }

    //TODO parameter: last shot as offset
    public void startLoadMoreShots(Context context) throws SQLException {
        if (!isLoadingMore && moreShots) {
            isLoadingMore = true;
            Timber.d("Start loading more shots");
            //User currentUser = GolesApplication.get(context).getCurrentUser();
            //Shot oldestShot = adapter.getItem(adapter.getCount() - 1);
            startRetrieveOldShotsTimeLineJob(context);
        }
    }

    public void loadInitialTimeline() {
        //User currentUser = GolesApplication.get(getActivity()).getCurrentUser();
        startRetrieveInitialTimeLineJob(getActivity());
        swipeRefreshLayout.setRefreshing(true);
    }

    @Subscribe
    public void showTimeline(ShotsResultEvent event) {
        List<ShotVO> shots = event.getResult();
        swipeRefreshLayout.setRefreshing(false);
        if (shots != null && shots.size() > 0) {
            adapter = new TimelineAdapter(getActivity(), shots, picasso, avatarClickListener);
            listView.setAdapter(adapter);
            setEmpty(false);
        } else {
            setEmpty(true);
            Timber.i("No shots received");
        }
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
        List<ShotVO> updatedTimeline = event.getResult();

        if (newShotsCount == 0) {
            Timber.i("No new shots");
            if(adapter != null){ //Means that We have at least one shot and for that reason the adapter was initialized
                adapter.notifyDataSetChanged(); // Refresh time indicator
            }else{

            }

        } else {
            Timber.i("Received %d new shots", newShotsCount);
            int originalPosition = listView.getFirstVisiblePosition();
            int newPosition = originalPosition + newShotsCount - 1;
            adapter.setShots(updatedTimeline);
            listView.setSelection(newPosition);
            listView.smoothScrollToPosition(0);
        }
    }

    @Subscribe
    public void displayOldShots(OldShotsReceivedEvent event) {
        isLoadingMore = false;
        List<ShotVO> shots = event.getResult();
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

    @OnClick(R.id.timeline_new_pronostic)
    public void debugNotification() {
        new AsyncTask<Void, Void, Void>() {
            @Override protected Void doInBackground(Void... params) {
                notificationManager.sendNewShotNotification((Shot) listView.getItemAtPosition(1));
                return null;
            }
        }.execute();
    }

}

