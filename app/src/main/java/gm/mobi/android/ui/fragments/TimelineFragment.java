package gm.mobi.android.ui.fragments;

import android.animation.TimeInterpolator;
import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
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

import com.path.android.jobqueue.JobManager;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import es.oneoctopus.swiperefreshlayoutoverlay.SwipeRefreshLayoutOverlay;
import gm.mobi.android.GolesApplication;
import gm.mobi.android.R;
import gm.mobi.android.db.manager.ShotManager;
import gm.mobi.android.db.objects.Shot;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.task.events.timeline.NewShotsReceivedEvent;
import gm.mobi.android.task.events.timeline.OldShotsReceivedEvent;
import gm.mobi.android.task.events.timeline.ShotsResultEvent;
import gm.mobi.android.task.jobs.timeline.TimelineJob;
import gm.mobi.android.ui.adapters.TimelineAdapter;
import gm.mobi.android.ui.base.BaseFragment;
import gm.mobi.android.ui.widgets.ListViewScrollObserver;
import timber.log.Timber;

public class TimelineFragment extends BaseFragment implements SwipeRefreshLayoutOverlay.OnRefreshListener {

    @Inject Picasso picasso;
    @Inject Bus bus;
    @Inject SQLiteOpenHelper dbHelper;
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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        bus.register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        bus.unregister(this);
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
            getActivity().getActionBar().setTitle("Timeline");
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

    /* --- UI Events --- */

    @OnItemClick(R.id.timeline_list)
    public void openShot(int position) {
        //TODO Shot detail
        Shot shot = adapter.getItem(position);
        Toast.makeText(getActivity(), "Shot " + position, Toast.LENGTH_SHORT).show();
        Timber.d("Clicked shot %d", position);
    }

    public void openProfile(int position) {
        Shot shot = adapter.getItem(position);
        //TODO profile
        Toast.makeText(getActivity(), "Open profile "+position, Toast.LENGTH_SHORT).show();
        Timber.d("Open profile in position %d", position);
    }

    public void startRefreshing(Context context) {
        if (!isRefreshing) {
            isRefreshing = true;
            Timber.d("Start new timeline refresh");
            User currentUser = GolesApplication.get(context).getCurrentUser();
            Shot latestShot = adapter.getItem(0);
            //TODO stop job from being launched again and again. Cancell current or restrict
            jobManager.addJobInBackground(new TimelineJob(context, currentUser, TimelineJob.RETRIEVE_NEWER, latestShot));
        }
    }

    //TODO parameter: last shot as offset
    public void startLoadMoreShots(Context context) {
        if (!isLoadingMore && moreShots) {
            isLoadingMore = true;
            Timber.d("Start loading more shots");
            User currentUser = GolesApplication.get(context).getCurrentUser();
            Shot oldestShot = adapter.getItem(adapter.getCount()-1);
            jobManager.addJobInBackground(new TimelineJob(context, currentUser, TimelineJob.RETRIEVE_OLDER, oldestShot));
        }
    }

    public void loadInitialTimeline() {
        User currentUser = GolesApplication.get(getActivity()).getCurrentUser();
        jobManager.addJobInBackground(new TimelineJob(getActivity(), currentUser, TimelineJob.RETRIEVE_INITIAL, null));
    }

    @Subscribe
    public void showTimeline(ShotsResultEvent event) {
        List<Shot> shots = event.getShots();
        Timber.d("Nananananana %d", shots.size());
        adapter = new TimelineAdapter(getActivity(), shots, picasso, avatarClickListener);
        listView.setAdapter(adapter);
    }

    @Subscribe
    public void displayNewShots(NewShotsReceivedEvent event) {
        isRefreshing = false;
        swipeRefreshLayout.setRefreshing(false);
        List<Shot> newShots = event.getShots();
        if (newShots == null) {
            //TODO mostrar error? Es posible esta situación? No debería
            Timber.e("Null shot list received");
            return;
        }
        if (newShots.size() == 0) {
            Toast.makeText(getActivity(), "No new shots", Toast.LENGTH_SHORT).show();
        } else {
            Timber.d("Received %d new shots", newShots.size());
            int originalPosition = listView.getFirstVisiblePosition();
            int newPosition = originalPosition + newShots.size() - 1;
            adapter.addShotsAbove(newShots);
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

