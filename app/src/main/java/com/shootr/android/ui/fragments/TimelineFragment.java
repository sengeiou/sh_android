package com.shootr.android.ui.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
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
import com.shootr.android.R;
import com.shootr.android.data.bus.Main;
import com.shootr.android.domain.Event;
import com.shootr.android.domain.EventInfo;
import com.shootr.android.domain.QueuedShot;
import com.shootr.android.domain.Shot;
import com.shootr.android.domain.Timeline;
import com.shootr.android.domain.Watch;
import com.shootr.android.domain.bus.ShotFailed;
import com.shootr.android.domain.bus.ShotSent;
import com.shootr.android.domain.interactor.event.SelectEventInteractor;
import com.shootr.android.domain.interactor.event.VisibleEventInfoInteractor;
import com.shootr.android.domain.interactor.shot.GetDraftsInteractor;
import com.shootr.android.domain.interactor.timeline.GetMainTimelineInteractor;
import com.shootr.android.domain.interactor.timeline.GetOlderMainTimelineInteractor;
import com.shootr.android.domain.interactor.timeline.RefreshMainTimelineInteractor;
import com.shootr.android.task.events.CommunicationErrorEvent;
import com.shootr.android.task.events.ConnectionNotAvailableEvent;
import com.shootr.android.ui.ToolbarDecorator;
import com.shootr.android.ui.activities.BaseNavDrawerToolbarActivity;
import com.shootr.android.ui.activities.DraftsActivity;
import com.shootr.android.ui.activities.EventDetailActivity;
import com.shootr.android.ui.activities.EventsListActivity;
import com.shootr.android.ui.activities.PhotoViewActivity;
import com.shootr.android.ui.activities.PostNewShotActivity;
import com.shootr.android.ui.activities.ProfileContainerActivity;
import com.shootr.android.ui.activities.ShotDetailActivity;
import com.shootr.android.ui.adapters.TimelineAdapter;
import com.shootr.android.ui.base.BaseFragment;
import com.shootr.android.ui.component.PhotoPickerController;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.model.mappers.ShotModelMapper;
import com.shootr.android.ui.presenter.WatchNumberPresenter;
import com.shootr.android.ui.views.WatchingRequestView;
import com.shootr.android.ui.widgets.BadgeDrawable;
import com.shootr.android.ui.widgets.ListViewScrollObserver;
import com.shootr.android.util.AndroidTimeUtils;
import com.shootr.android.util.PicassoWrapper;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import java.io.File;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class TimelineFragment extends BaseFragment
        implements SwipeRefreshLayout.OnRefreshListener, WatchingRequestView, ShotSent.Receiver, ShotFailed.Receiver {

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
    @Inject GetDraftsInteractor getDraftsInteractor;
    @Inject ShotModelMapper shotModelMapper;
    @Inject GetMainTimelineInteractor getMainTimelineInteractor;
    @Inject RefreshMainTimelineInteractor refreshMainTimelineInteractor;
    @Inject GetOlderMainTimelineInteractor getOlderMainTimelineInteractor;

    @InjectView(R.id.timeline_list) ListView listView;
    @InjectView(R.id.timeline_new) View newShotView;
    @InjectView(R.id.timeline_new_text) TextView newShotTextView;
    @InjectView(R.id.timeline_swipe_refresh) SwipeRefreshLayout swipeRefreshLayout;
    @InjectView(R.id.exit_event_fab) FloatingActionButton exitEventFab;

    @InjectView(R.id.timeline_empty) View emptyView;
    @InjectView(R.id.timeline_drafts) View draftsButton;

    private View footerView;
    private ProgressBar footerProgress;

    private TimelineAdapter adapter;
    private View.OnClickListener avatarClickListener;
    private View.OnClickListener imageClickListener;
    private boolean isLoadingMore;
    private boolean isRefreshing;
    private boolean moreShots = true;
    private boolean shouldPoll;
    private BadgeDrawable badgeDrawable;
    private String newShotPlaceholder;
    private PhotoPickerController photoPickerController;


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
                        startRetrieveNewShotsTimeLineJob();
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
        startPollingShots();
        watchNumberPresenter.resume();
        updateDraftsButtonVisibility();
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
                    showEventTagInToolbar(event.getTag());
                } else {
                    showEventTagInToolbar(getString(R.string.timeline_hall_title));
                }
            }
        });
    }

    private void showEventTagInToolbar(String eventTag) {
        ToolbarDecorator toolbarDecorator = ((BaseNavDrawerToolbarActivity) getActivity()).getToolbarDecorator();
        toolbarDecorator.setTitle(eventTag);
        toolbarDecorator.showDropdownIcon(true);
        toolbarDecorator.setTitleClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                selectEvent();
            }
        });
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

        // Header and footer
        footerView =
                LayoutInflater.from(getActivity()).inflate(R.layout.item_list_loading, listView, false);
        footerProgress = ButterKnife.findById(footerView, R.id.loading_progress);

        listView.addFooterView(footerView, null, false);

        adapter = new TimelineAdapter(getActivity(), picasso, avatarClickListener, imageClickListener, timeUtils);
        listView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.refresh_1, R.color.refresh_2, R.color.refresh_3,
          R.color.refresh_4);

        setupDraftButtonTransition();

        // List scroll stuff
        new ListViewScrollObserver(listView).setOnScrollUpAndDownListener(
                new ListViewScrollObserver.OnListViewScrollListener() {
                    @Override
                    public void onScrollUpDownChanged(int delta, int scrollPosition, boolean exact) {
                        if (delta < -10) {
                            exitEventFab.hide();
                        } else if(delta > 10) {
                            exitEventFab.show();
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
        loadEventPlaceholder();
        photoPickerController = new PhotoPickerController.Builder().onActivity(getActivity())
          .withHandler(new PhotoPickerController.Handler() {
              @Override public void onSelected(File imageFile) {
                  Intent intent = new Intent(getActivity(), PostNewShotActivity.class);
                  intent.putExtra(PostNewShotActivity.EXTRA_PHOTO, imageFile);
                  startNewShotActivityWithPlaceholder(intent);
              }

              @Override public void onError(Exception e) {
                  Timber.e(e, "Error selecting image");
              }

              @Override public void startPickerActivityForResult(Intent intent, int requestCode) {
                  startActivityForResult(intent, requestCode);
              }
          })
          .build();
        updateDraftsButtonVisibility();
    }

    private void updateDraftsButtonVisibility() {
        getDraftsInteractor.loadDrafts(new GetDraftsInteractor.Callback() {
            @Override public void onLoaded(List<QueuedShot> drafts) {
                if (!drafts.isEmpty()) {
                    showDraftsButton();
                } else {
                    hideDraftsButton();
                }
            }
        });
    }

    private void showDraftsButton() {
        if (draftsButton.getVisibility() == View.VISIBLE) {
            return;
        }
        draftsButton.setVisibility(View.VISIBLE);
        draftsButton.setScaleX(0);
        draftsButton.setScaleY(0);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(draftsButton, "scaleX", 0f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(draftsButton, "scaleY", 0f, 1f);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(scaleX, scaleY);
        set.setDuration(500);
        set.setStartDelay(200);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override public void onAnimationEnd(Animator animation) {
                draftsButton.setScaleX(1f);
                draftsButton.setScaleY(1f);
            }
        });
        set.start();
    }

    private void hideDraftsButton() {
        if (draftsButton.getVisibility() == View.GONE) {
            return;
        }
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(draftsButton, "scaleX", 1f, 0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(draftsButton, "scaleY", 1f, 0f);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(scaleX, scaleY);
        set.setDuration(500);
        set.setInterpolator(new AccelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override public void onAnimationEnd(Animator animation) {
                draftsButton.setVisibility(View.GONE);
            }
        });
        set.start();
    }

    private void setupDraftButtonTransition() {
        LayoutTransition transition = new LayoutTransition();
        // Disable button appearing and disappearing (button), we will do manually
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            transition.disableTransitionType(LayoutTransition.APPEARING);
            transition.disableTransitionType(LayoutTransition.DISAPPEARING);
        } else {
            transition.setAnimator(LayoutTransition.APPEARING, null);
            transition.setAnimator(LayoutTransition.DISAPPEARING, null);
        }

        // Setup text shrinking (when button appears)
        transition.setDuration(LayoutTransition.CHANGE_APPEARING, 200);
        transition.setInterpolator(LayoutTransition.CHANGE_APPEARING, new AccelerateDecelerateInterpolator());

        // Setup text expanding (when button disappears)
        transition.setInterpolator(LayoutTransition.CHANGE_DISAPPEARING, new AccelerateDecelerateInterpolator());
        transition.setStartDelay(LayoutTransition.CHANGE_DISAPPEARING, 0);

        ((ViewGroup) draftsButton.getParent()).setLayoutTransition(transition);
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
        } else {
            photoPickerController.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void onEventChanged() {
        this.loadEventPlaceholder();
        watchNumberPresenter.initialize(this);
        loadInitialTimeline();
    }

    public void selectEvent() {
        Intent intent = new Intent(getActivity(), EventsListActivity.class);
        startActivityForResult(intent, REQUEST_SELECT_EVENT);
    }

    @OnClick(R.id.exit_event_fab)
    public void exitEvent() {
        Toast.makeText(getActivity(), "exit", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.timeline_new_text)
    public void startNewShot() {
        Intent intent = new Intent(getActivity(), PostNewShotActivity.class);
        startNewShotActivityWithPlaceholder(intent);
    }

    @OnClick(R.id.timeline_drafts)
    public void openDrafts() {
        startActivity(new Intent(getActivity(), DraftsActivity.class));
    }

    @OnClick(R.id.timeline_new_image_camera)
    public void startNewShotWithPhoto() {
        photoPickerController.pickPhoto();
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
            startRetrieveNewShotsTimeLineJob();
        }
    }

    private void startRetrieveOldShotsTimeLineJob(){
        getOlderMainTimelineInteractor.loadOlderMainTimeline(oldestShotDate(),
          new GetOlderMainTimelineInteractor.Callback() {
              @Override public void onLoaded(Timeline timeline) {
                  isLoadingMore = false;
                  List<Shot> shots = timeline.getShots();
                  int olderShotsSize = shots.size();
                  if (olderShotsSize == 0) {
                      footerProgress.setVisibility(View.GONE);
                      moreShots = false;
                  } else {
                      Timber.d("Received %d old shots", olderShotsSize);
                      adapter.addShotsBelow(shotModelMapper.transform(shots));
                  }
              }
          });
    }

    private Long oldestShotDate() {
        Long oldestShotDate = adapter.getItem(adapter.getCount() - 1).getCsysBirth().getTime();
        return oldestShotDate;
    }

    private void startRetrieveNewShotsTimeLineJob() {
        refreshMainTimelineInteractor.refreshMainTimeline(new RefreshMainTimelineInteractor.Callback() {
            @Override public void onLoaded(Timeline timeline) {
                isRefreshing = false;
                swipeRefreshLayout.setRefreshing(false);
                List<Shot> shots = timeline.getShots();
                if (!shots.isEmpty()) {
                    List<ShotModel> shotModels = shotModelMapper.transform(shots);
                    int originalPosition = listView.getFirstVisiblePosition();
                    int newPosition = originalPosition + shotModels.size();
                    adapter.addShotsAbove(shotModels);
                    adapter.notifyDataSetChanged();
                    setListPosition(newPosition);
                    if (shouldGoToTop()) {
                        goToTop();
                    }
                }
            }
        });
    }

    private void loadMoreShotsIfNeeded() {
        int lastVisiblePosition = listView.getLastVisiblePosition();
        if (lastVisiblePosition >= listView.getAdapter().getCount()-1) {
            Context context = getActivity();
            if (context != null) {
                startLoadMoreShots();
            }
        }
    }

    public void startLoadMoreShots() {
        if (!isLoadingMore && moreShots) {
            isLoadingMore = true;
            Timber.d("Start loading more shots");
            startRetrieveOldShotsTimeLineJob();
        }
    }

    public void loadInitialTimeline() {
        setEmpty(false);
        swipeRefreshLayout.setRefreshing(true);
        getMainTimelineInteractor.loadMainTimeline(new GetMainTimelineInteractor.Callback() {
            @Override public void onLoaded(Timeline timeline) {
                List<Shot> shots = timeline.getShots();
                List<ShotModel> shotModels = shotModelMapper.transform(shots);
                showTimeline(shotModels);
            }
        });
    }

    public void showTimeline(List<ShotModel> shots) {
        swipeRefreshLayout.setRefreshing(false);
        if (shots != null && !shots.isEmpty()) {
            adapter.setShots(shots);
            setEmpty(false);
            loadMoreShotsIfNeeded();
        } else {
            setEmpty(true);
            Timber.i("No shots received");
        }
    }

    private void setEmpty(boolean empty) {
        emptyView.setVisibility(empty ? View.VISIBLE : View.GONE);
        swipeRefreshLayout.setVisibility(empty ? View.GONE : View.VISIBLE);
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
    @Override public void onShotSent(ShotSent.Event event) {
        startRefreshing(getActivity());
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

    @Subscribe
    @Override public void onShotFailed(ShotFailed.Event event) {
        updateDraftsButtonVisibility();
    }
}

