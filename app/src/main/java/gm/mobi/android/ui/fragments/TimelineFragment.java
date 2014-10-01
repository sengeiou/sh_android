package gm.mobi.android.ui.fragments;

import android.animation.TimeInterpolator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
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

import com.google.android.gms.internal.dr;
import com.path.android.jobqueue.JobManager;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import gm.mobi.android.db.manager.UserManager;
import gm.mobi.android.ui.activities.ProfileContainerActivity;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;
import es.oneoctopus.swiperefreshlayoutoverlay.SwipeRefreshLayoutOverlay;
import gm.mobi.android.GolesApplication;
import gm.mobi.android.R;
import gm.mobi.android.db.objects.Shot;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.task.events.ConnectionNotAvailableEvent;
import gm.mobi.android.task.events.timeline.NewShotsReceivedEvent;
import gm.mobi.android.task.events.timeline.OldShotsReceivedEvent;
import gm.mobi.android.task.events.timeline.ShotsResultEvent;
import gm.mobi.android.task.jobs.timeline.TimelineJob;
import gm.mobi.android.ui.activities.NewShotActivity;
import gm.mobi.android.ui.adapters.TimelineAdapter;
import gm.mobi.android.ui.base.BaseActivity;
import gm.mobi.android.ui.base.BaseFragment;
import gm.mobi.android.ui.widgets.ListViewScrollObserver;
import timber.log.Timber;

public class TimelineFragment extends BaseFragment implements SwipeRefreshLayoutOverlay.OnRefreshListener {

    public static final int REQUEST_NEW_SHOT = 1;
    @Inject Picasso picasso;
    @Inject Bus bus;
    @Inject JobManager jobManager;

    @InjectView(R.id.timeline_list) ListView listView;
    @InjectView(R.id.timeline_new) View newShotView;
    @InjectView(R.id.timeline_watching_container) View watchingContainer;
    @InjectView(R.id.timeline_swipe_refresh) SwipeRefreshLayoutOverlay swipeRefreshLayout;

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
    private List<Shot> shots;


     /* ---- Lifecycle methods ---- */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        avatarClickListener = new View.OnClickListener() {
            @Override public void onClick(View v) {
                int position = ((TimelineAdapter.ViewHolder) v.getTag()).position;
                openProfile(position);
            }
        };
    }

    public void pollingShots(final Activity a){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try{
                        Thread.sleep(10000);
                        User currentUser = GolesApplication.get(a).getCurrentUser();
                        jobManager.addJobInBackground(new TimelineJob(a, currentUser, TimelineJob.RETRIEVE_NEWER));
                    }catch(InterruptedException e){
                        Timber.e("InterruptedException with message", e.getMessage());
                    }
                }

            }
        }).start();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        bus.register(this);
        pollingShots(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        bus.unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Subscribe
    public void onConnectionNotAvailable(ConnectionNotAvailableEvent event) {
        Toast.makeText(getActivity(), R.string.connection_lost, Toast.LENGTH_SHORT).show();
        swipeRefreshLayout.setRefreshing(false);
        isLoadingMore = false;
        isRefreshing = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_timeline, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

        //TODO change by drawerLayout, not the fragment itself
        try {
            ((BaseActivity)getActivity()).getSupportActionBar().setTitle("Timeline");
        } catch (NullPointerException e) {
            Timber.w("Activity null in TimelineFragment#onViewCreated()");
        }

        // Header and footer
        headerView = LayoutInflater.from(getActivity()).inflate(R.layout.timeline_margin, listView, false);
        footerView = LayoutInflater.from(getActivity()).inflate(R.layout.item_list_loading, listView, false);
        footerProgress = ButterKnife.findById(footerView, R.id.loading_progress);
        footerText = ButterKnife.findById(footerView, R.id.loading_text);

        listView.addHeaderView(headerView, null, false);
        listView.addFooterView(footerView, null, false);

        watchingHeight = getResources().getDimensionPixelOffset(R.dimen.watching_bar_height);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.refresh_1, R.color.refresh_2, R.color.refresh_3, R.color.refresh_4);
        swipeRefreshLayout.setTopMargin(watchingHeight);

        // List scroll stuff
        new ListViewScrollObserver(listView).setOnScrollUpAndDownListener(new ListViewScrollObserver.OnListViewScrollListener() {
            public TimeInterpolator mInterpolator = new AccelerateDecelerateInterpolator();

            @Override
            public void onScrollUpDownChanged(int delta, int scrollPosition, boolean exact) {
                // delta negativo: scoll abajo
                if (delta < -10 && scrollPosition < -watchingHeight && !isRefreshing) { //Hide
                    watchingContainer.animate().setInterpolator(mInterpolator).setDuration(200).translationY(-watchingHeight);
                } else if (delta > 10) { // Show
                    watchingContainer.animate().setInterpolator(mInterpolator).setDuration(200).translationY(0).start();
                }
            }

            @Override
            public void onScrollIdle() {
                int lastVisiblePosition = listView.getLastVisiblePosition();
                if (lastVisiblePosition >= adapter.getCount() + 1 /*footer*/) {
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
        Bundle anim = ActivityOptionsCompat.makeScaleUpAnimation(newShotView, 0, 0, newShotView.getWidth(), newShotView.getHeight()).toBundle();
        Intent intent = new Intent(getActivity(), NewShotActivity.class);
        intent.putExtras(anim);
        startActivityForResult(intent, REQUEST_NEW_SHOT);
//        ActivityCompat.startActivityForResult(getActivity(), intent, REQUEST_NEW_SHOT, anim);
    }

    @OnItemClick(R.id.timeline_list)
    public void openShot(int position) {
        //TODO Shot detail
        Shot shot = adapter.getItem(position - 1);
        User user = shot.getUser();

        Toast.makeText(getActivity(), "Shot " + user.toString()+"---"+shot.getUser().getName(), Toast.LENGTH_SHORT).show();

        Timber.d("Clicked shot %d", position);
    }

    public void openProfile(int position) {
        User user = adapter.getItem(position).getUser();
        Intent profileIntent = ProfileContainerActivity.getIntent(getActivity(), user.getIdUser());
        startActivity(profileIntent);

    }

    public void startRefreshing(Context context) {
        if (!isRefreshing) {
            isRefreshing = true;
            swipeRefreshLayout.setRefreshing(true);
            Timber.d("Start new timeline refresh");
            User currentUser = GolesApplication.get(context).getCurrentUser();
            jobManager.addJobInBackground(new TimelineJob(context, currentUser, TimelineJob.RETRIEVE_NEWER));
        }
    }

    //TODO parameter: last shot as offset
    public void startLoadMoreShots(Context context) {
        if (!isLoadingMore && moreShots) {
            isLoadingMore = true;
            Timber.d("Start loading more shots");
            User currentUser = GolesApplication.get(context).getCurrentUser();
            Shot oldestShot = adapter.getItem(adapter.getCount() - 1);
            jobManager.addJobInBackground(new TimelineJob(context, currentUser, TimelineJob.RETRIEVE_OLDER));
        }
    }

    public void loadInitialTimeline() {
        User currentUser = GolesApplication.get(getActivity()).getCurrentUser();
        jobManager.addJobInBackground(new TimelineJob(getActivity(), currentUser, TimelineJob.RETRIEVE_INITIAL));
        swipeRefreshLayout.setRefreshing(true);
    }

    @Subscribe
    public void showTimeline(ShotsResultEvent event) {
        swipeRefreshLayout.setRefreshing(false);
        List<Shot> shots = event.getShots();
        if (shots != null && shots.size() > 0) {
            adapter = new TimelineAdapter(getActivity(), shots, picasso, avatarClickListener);
            listView.setAdapter(adapter);
        } else {
            Timber.w("No shots received");
        }
    }

    @Subscribe
    public void displayNewShots(NewShotsReceivedEvent event) {
        isRefreshing = false;
        swipeRefreshLayout.setRefreshing(false);
        List<Shot> updatedTimeline = event.getAllShots();
        int newShotsCount = event.getNewShotsCount();

        if (updatedTimeline == null) {
            //TODO mostrar error? Es posible esta situación? No debería
            Timber.e("Null shot list received");
            return;
        }
        if (newShotsCount == 0) {
            Timber.i("No new shots");
            adapter.notifyDataSetChanged(); // Refresh time indicator
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
        List<Shot> oldShots = event.getShots();
        if (oldShots == null) {
            //TODO mostrar error? Es posible esta situación? No debería
            Timber.e("Null shot list received");
            return;
        }
        if (oldShots.size() == 0) {
            footerProgress.setVisibility(View.INVISIBLE); // Maintain size
            footerText.setVisibility(View.VISIBLE);
            footerText.setText(R.string.no_more_shots);
            moreShots = false;
        } else {
            Timber.d("Received %d old shots", oldShots.size());
            adapter.addShotsBelow(oldShots);
        }
    }
}

