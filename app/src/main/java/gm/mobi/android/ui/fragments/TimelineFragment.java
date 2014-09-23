package gm.mobi.android.ui.fragments;

import android.animation.TimeInterpolator;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ListView;
import android.widget.Toast;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import es.oneoctopus.swiperefreshlayoutoverlay.SwipeRefreshLayoutOverlay;
import gm.mobi.android.GolesApplication;
import gm.mobi.android.R;
import gm.mobi.android.task.events.timeline.NewShotsReceivedEvent;
import gm.mobi.android.task.events.timeline.OldShotsReceivedEvent;
import gm.mobi.android.ui.adapters.TimelineAdapter;
import gm.mobi.android.ui.base.BaseFragment;
import gm.mobi.android.ui.widgets.ListViewScrollObserver;
import hugo.weaving.DebugLog;
import timber.log.Timber;

public class TimelineFragment extends BaseFragment implements SwipeRefreshLayoutOverlay.OnRefreshListener {

    @Inject Picasso picasso;
    @Inject Bus bus;

    @InjectView(R.id.timeline_list) ListView listView;
    //    @InjectView(R.id.timeline_fab_watching) FloatingActionButton imwatchingView;
    @InjectView(R.id.timeline_new) View newShotView;
    @InjectView(R.id.timeline_watching_container) View watchingContainer;
    @InjectView(R.id.timeline_swipe_refresh) SwipeRefreshLayoutOverlay swipeRefreshLayout;

    private TimelineAdapter adapter;
    private View.OnClickListener avatarClickListener;
    private boolean mFistAttach = true;
    private boolean isLoadingMore;
    private boolean isRefreshing;
    private int watchingHeight;

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
        if (mFistAttach) {
            GolesApplication.get(getActivity()).inject(this);
            mFistAttach = false;
        }
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
        try {
            getActivity().getActionBar().setTitle("Timeline");
        } catch (NullPointerException e) {
            Timber.w("Activity null in TimelineFragment#onViewCreated()");
        }

        watchingHeight = getResources().getDimensionPixelOffset(R.dimen.watching_bar_height);


        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.refresh_1, R.color.refresh_2, R.color.refresh_3, R.color.refresh_4);
        swipeRefreshLayout.setTopMargin(watchingHeight);

        // FAB
//        imwatchingView.attachToListView(listView); //TODO should automatically hide the fab, but doesn't work
        newShotView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        final int newShotHeight = newShotView.getMeasuredHeight();

        // List scroll stuff

        new ListViewScrollObserver(listView).setOnScrollUpAndDownListener(new ListViewScrollObserver.OnListViewScrollListener() {
            public TimeInterpolator mInterpolator = new AccelerateDecelerateInterpolator();

            @Override
            public void onScrollUpDownChanged(int delta, int scrollPosition, boolean exact) {
                // delta negativo: scoll abajo
                if (delta < -10 && scrollPosition < -watchingHeight) {
//                    newShotView.animate().setInterpolator(mInterpolator).setDuration(200).translationY(newShotHeight);
                    watchingContainer.animate().setInterpolator(mInterpolator).setDuration(200).translationY(-watchingHeight);
//                    newShotView.setTranslationY(newShotHeight);
//                    imwatchingView.hide();
                } else if (delta > 10) {
                    watchingContainer.animate().setInterpolator(mInterpolator).setDuration(200).translationY(0).start();
//                    newShotView.animate().translationY(0).setDuration(200).start();
//                    newShotView.setTranslationY(0);
//                    imwatchingView.show();
                }
            }

            @Override
            public void onScrollIdle() {
                int lastVisiblePosition = listView.getLastVisiblePosition();
                if (adapter.isLast(lastVisiblePosition)) {
                    startLoadMoreShots();
                }
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new TimelineAdapter(getActivity(), picasso, avatarClickListener);
        listView.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.timeline, menu);
        // Little hack for ActionBarCompat
//        menu.findItem(R.id.menu_info).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.findItem(R.id.menu_search).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        startRefreshing();
    }

    /* --- UI Events --- */

    @OnItemClick(R.id.timeline_list)
    public void openShot(int position) {
        //TODO Shot detail
        TimelineAdapter.MockShot shot = adapter.getItem(position);
        Toast.makeText(getActivity(), "Shot " + position + " from " + shot.name, Toast.LENGTH_SHORT).show();
        Timber.d("Clicked shot %d from %s", position, shot.name);
    }

    public void openProfile(int position) {
        TimelineAdapter.MockShot shot = adapter.getItem(position);
        //TODO profile
        Toast.makeText(getActivity(), "Open " + shot.name + "'s profile", Toast.LENGTH_SHORT).show();
        Timber.d("Open profile in position %d: %s", position, shot.name);
    }

    public void startRefreshing() {
        if (!isRefreshing) {
            isRefreshing = true;
            Timber.d("Start new timeline refresh");
            //TODO start job
            //TODO stop job from being launched again and again. Cancell current or restrict
            //Debug:
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    List<TimelineAdapter.MockShot> newShots = TimelineAdapter.MockShot.getBlueList();
                    bus.post(new NewShotsReceivedEvent(newShots));
                }
            }, 2000);
        }
    }

    //TODO parameter: last shot as offset
    public void startLoadMoreShots() {
        if (!isLoadingMore) {
            isLoadingMore = true;
            Timber.d("Start loading more shots");
            //Debug:
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    List<TimelineAdapter.MockShot> newShots = TimelineAdapter.MockShot.getBlueList();
                    bus.post(new OldShotsReceivedEvent(newShots));
                }
            }, 2000);
        }
    }

    @Subscribe
    public void displayNewShots(NewShotsReceivedEvent event) {
        isRefreshing = false;
        swipeRefreshLayout.setRefreshing(false);
        List<TimelineAdapter.MockShot> newShots = event.getShots();
        if (newShots == null) {
            //TODO mostrar error? Es posible esta situación? No debería
            Timber.e("Null shot list received");
            return;
        }
        Timber.d("Received %d new shots", newShots.size());
        int originalPosition = listView.getFirstVisiblePosition();
        int newPosition = originalPosition + newShots.size() - 1;
        adapter.addShotsAbove(newShots);
        listView.setSelection(newPosition);
        listView.smoothScrollToPosition(0);
    }

    @Subscribe
    public void displayOldShots(OldShotsReceivedEvent event) {
        isLoadingMore = false;
        List<TimelineAdapter.MockShot> oldShots = event.getShots();
        if (oldShots == null) {
            //TODO mostrar error? Es posible esta situación? No debería
            Timber.e("Null shot list received");
            return;
        }
        Timber.d("Received %d old shots", oldShots.size());
        adapter.addShotsBelow(oldShots);
    }
}

