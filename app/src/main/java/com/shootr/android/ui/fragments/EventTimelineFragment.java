package com.shootr.android.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.shootr.android.R;
import com.shootr.android.domain.User;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.user.GetUserByUsernameInteractor;
import com.shootr.android.ui.ToolbarDecorator;
import com.shootr.android.ui.activities.BaseNavDrawerToolbarActivity;
import com.shootr.android.ui.activities.BaseToolbarDecoratedActivity;
import com.shootr.android.ui.activities.DraftsActivity;
import com.shootr.android.ui.activities.EventDetailActivity;
import com.shootr.android.ui.activities.PhotoViewActivity;
import com.shootr.android.ui.activities.PostNewShotActivity;
import com.shootr.android.ui.activities.ProfileContainerActivity;
import com.shootr.android.ui.activities.ShotDetailActivity;
import com.shootr.android.ui.adapters.TimelineAdapter;
import com.shootr.android.ui.base.BaseFragment;
import com.shootr.android.ui.component.PhotoPickerController;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.model.mappers.UserModelMapper;
import com.shootr.android.ui.presenter.NewShotBarPresenter;
import com.shootr.android.ui.presenter.TimelinePresenter;
import com.shootr.android.ui.presenter.WatchNumberPresenter;
import com.shootr.android.ui.views.NewShotBarView;
import com.shootr.android.ui.views.NullNewShotBarView;
import com.shootr.android.ui.views.NullWatchNumberView;
import com.shootr.android.ui.views.TimelineView;
import com.shootr.android.ui.views.WatchNumberView;
import com.shootr.android.ui.views.nullview.NullTimelineView;
import com.shootr.android.ui.widgets.BadgeDrawable;
import com.shootr.android.ui.widgets.ListViewScrollObserver;
import com.shootr.android.util.AndroidTimeUtils;
import com.shootr.android.util.PicassoWrapper;
import com.shootr.android.util.UsernameClickListener;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;
import timber.log.Timber;

public class EventTimelineFragment extends BaseFragment
  implements TimelineView, NewShotBarView, WatchNumberView{

    public static final String EXTRA_EVENT_ID = "eventId";
    public static final String EXTRA_EVENT_TITLE = "eventTitle";

    //region Fields
    @Inject TimelinePresenter timelinePresenter;
    @Inject NewShotBarPresenter newShotBarPresenter;
    @Inject WatchNumberPresenter watchNumberPresenter;

    @Inject
    GetUserByUsernameInteractor getUserByUsernameInteractor;
    @Inject
    UserModelMapper userModelMapper;

    @Inject PicassoWrapper picasso;
    @Inject AndroidTimeUtils timeUtils;

    @InjectView(R.id.timeline_shot_list) ListView listView;
    @InjectView(R.id.timeline_swipe_refresh) SwipeRefreshLayout swipeRefreshLayout;

    @InjectView(R.id.timeline_empty) View emptyView;
    @InjectView(R.id.shot_bar_drafts) View draftsButton;

    @Deprecated
    private TimelineAdapter adapter;
    private View.OnClickListener avatarClickListener;
    private View.OnClickListener imageClickListener;
    private UsernameClickListener usernameClickListener;
    private PhotoPickerController photoPickerController;

    private NewShotBarView newShotBarViewDelegate;
    private ToolbarDecorator toolbarDecorator;
    private MenuItem watchersMenuItem;
    private BadgeDrawable watchersBadgeDrawable;
    private Integer watchNumberCount;
    private View footerProgress;
    //endregion

    public static EventTimelineFragment newInstance(String eventId, String eventTitle) {
        Bundle fragmentArguments = new Bundle();
        fragmentArguments.putString(EXTRA_EVENT_ID, eventId);
        fragmentArguments.putString(EXTRA_EVENT_TITLE, eventTitle);
        return newInstance(fragmentArguments);
    }

    public static EventTimelineFragment newInstance(Bundle fragmentArguments) {
        EventTimelineFragment fragment = new EventTimelineFragment();
        fragment.setArguments(fragmentArguments);
        return fragment;
    }

    //region Lifecycle methods
    @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.timeline_event, container, false);
        ButterKnife.inject(this, fragmentView);
        return fragmentView;
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
        timelinePresenter.setView(new NullTimelineView());
        newShotBarPresenter.setView(new NullNewShotBarView());
        watchNumberPresenter.setView(new NullWatchNumberView());
    }

    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        initializeToolbar();
        initializePresenters();
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        photoPickerController.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.timeline, menu);
        watchersMenuItem = menu.findItem(R.id.menu_info);
        watchersMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        LayerDrawable icon = (LayerDrawable) getResources().getDrawable(R.drawable.badge_circle);
        icon.setDrawableByLayerId(R.id.ic_people, getResources().getDrawable(R.drawable.ic_action_ic_one_people));
        setupWatchNumberBadgeIcon(getActivity(), icon);
        watchersMenuItem.setIcon(icon);
        watchersMenuItem.getIcon();
        updateWatchNumberIcon();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_info:
                startActivity(new Intent(getActivity(), EventDetailActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override public void onResume() {
        super.onResume();
        timelinePresenter.resume();
        newShotBarPresenter.resume();
        watchNumberPresenter.resume();
    }

    @Override public void onPause() {
        super.onPause();
        timelinePresenter.pause();
        newShotBarPresenter.pause();
        watchNumberPresenter.pause();
    }

    private void initializeToolbar() {
        //FIXME So coupling. Much bad. Such ugly.
        if (getActivity() instanceof BaseNavDrawerToolbarActivity) {
            toolbarDecorator = ((BaseNavDrawerToolbarActivity) getActivity()).getToolbarDecorator();
        } else {
            toolbarDecorator = ((BaseToolbarDecoratedActivity) getActivity()).getToolbarDecorator();
        }
        toolbarDecorator.setTitleClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                //TODO go to top?
            }
        });
    }

    private void initializePresenters() {
        timelinePresenter.initialize(this);
        newShotBarPresenter.initialize(this);
        watchNumberPresenter.initialize(this);
    }

    //endregion

    private void setupNewShotBarDelegate() {
        newShotBarViewDelegate = new NewShotBarViewDelegate(photoPickerController, draftsButton) {
            @Override public void openNewShotView() {
                Intent newShotIntent = PostNewShotActivity.IntentBuilder //
                  .from(getActivity()) //
                  .build();
                startActivity(newShotIntent);
            }

            @Override public void openNewShotViewWithImage(File image) {
                Intent newShotIntent = PostNewShotActivity.IntentBuilder //
                  .from(getActivity()) //
                  .withImage(image) //
                  .build();
                startActivity(newShotIntent);
            }
        };
    }

    public void setupWatchNumberBadgeIcon(Context context, LayerDrawable icon) {
        // Reuse drawable if possible
        if (watchersBadgeDrawable == null) {
            Drawable reuse = icon.findDrawableByLayerId(R.id.ic_badge);
            if (reuse != null && reuse instanceof BadgeDrawable) {
                watchersBadgeDrawable = (BadgeDrawable) reuse;
            } else {
                watchersBadgeDrawable = new BadgeDrawable(context);
            }
        }
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_badge, watchersBadgeDrawable);
    }

    private void updateWatchNumberIcon() {
        if (watchersBadgeDrawable != null && watchersMenuItem != null) {
            if (watchNumberCount != null) {
                watchersBadgeDrawable.setCount(watchNumberCount);
                watchersMenuItem.setVisible(true);
            }
        }
    }

    //region Views manipulation
    private void initializeViews() {
        setupListAdapter();
        setupSwipeRefreshLayout();
        setupListScrollListeners();
        setupPhotoPicker();
        setupNewShotBarDelegate();
    }

    private void setupPhotoPicker() {
        photoPickerController = new PhotoPickerController.Builder().onActivity(getActivity())
          .withHandler(new PhotoPickerController.Handler() {
              @Override public void onSelected(File imageFile) {
                  newShotBarPresenter.newShotImagePicked(imageFile);
              }

              @Override public void onError(Exception e) {
                  //TODO mostrar algo
                  Timber.e(e, "Error selecting image");
              }

              @Override public void startPickerActivityForResult(Intent intent, int requestCode) {
                  startActivityForResult(intent, requestCode);
              }
          })
          .build();
    }

    private void setupListAdapter() {
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

        usernameClickListener = new UsernameClickListener() {
            @Override
            public void onClick(String username) {
                goToUserProfile(username);
            }
        };

        View footerView = LayoutInflater.from(getActivity()).inflate(R.layout.item_list_loading, listView, false);
        footerProgress = ButterKnife.findById(footerView, R.id.loading_progress);

        footerProgress.setVisibility(View.GONE);

        listView.addFooterView(footerView, null, false);

        adapter = new TimelineAdapter(getActivity(), picasso, avatarClickListener,
                imageClickListener, usernameClickListener, timeUtils);
        listView.setAdapter(adapter);
    }

    private void startProfileContainerActivity(String idUser) {
        Intent intentForUser = ProfileContainerActivity.getIntent(getActivity(), idUser);
        startActivity(intentForUser);
    }

    private void goToUserProfile(String username) {
        getUserByUsernameInteractor.searchUserByUsername(username, new Interactor.Callback<User>() {
            @Override
            public void onLoaded(User user) {
                if (user != null) {
                    startProfileContainerActivity(user.getIdUser());
                } else {
                    userNotFoundNotification();
                }
            }
        }, new Interactor.ErrorCallback() {
            @Override
            public void onError(ShootrException error) {
                Timber.e(error, "Error while searching user by username");
            }
        });
    }

    private void userNotFoundNotification(){
        Toast.makeText(getActivity(), "User not found", Toast.LENGTH_LONG).show();
    }

    private void setupSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override public void onRefresh() {
                timelinePresenter.refresh();
            }
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.refresh_1,
          R.color.refresh_2,
          R.color.refresh_3,
          R.color.refresh_4);
    }

    private void setupListScrollListeners() {
        new ListViewScrollObserver(listView).setOnScrollUpAndDownListener(new ListViewScrollObserver.OnListViewScrollListener() {
            @Override public void onScrollUpDownChanged(int delta, int scrollPosition, boolean exact) {
                if (delta < -10) {
                    // going down
                } else if (delta > 10) {
                    // going up
                }
            }

            @Override public void onScrollIdle() {
                checkIfEndOfListVisible();
            }
        });
    }

    private void checkIfEndOfListVisible() {
        int lastItemPosition = listView.getAdapter().getCount() - 1;
        int lastVisiblePosition = listView.getLastVisiblePosition();
        if (lastItemPosition == lastVisiblePosition) {
            timelinePresenter.showingLastShot(adapter.getLastShot());
        }
    }
    //endregion

    @OnItemClick(R.id.timeline_shot_list)
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

    @OnClick(R.id.shot_bar_text)
    public void startNewShot() {
        newShotBarPresenter.newShotFromTextBox();
    }

    @OnClick(R.id.shot_bar_photo)
    public void startNewShotWithPhoto() {
        newShotBarPresenter.newShotFromImage();
    }

    @OnClick(R.id.shot_bar_drafts)
    public void openDrafts() {
        startActivity(new Intent(getActivity(), DraftsActivity.class));
    }

    //region View methods
    @Override public void setShots(List<ShotModel> shots) {
        adapter.setShots(shots);
    }

    @Override public void hideShots() {
        listView.setVisibility(View.GONE);
    }

    @Override public void showShots() {
        listView.setVisibility(View.VISIBLE);
    }

    @Override public void addNewShots(List<ShotModel> newShots) {
        adapter.addShotsAbove(newShots);
    }

    @Override public void addOldShots(List<ShotModel> oldShots) {
        adapter.addShotsBelow(oldShots);
    }

    @Override public void showLoadingOldShots() {
        footerProgress.setVisibility(View.VISIBLE);
    }

    @Override public void hideLoadingOldShots() {
        footerProgress.setVisibility(View.GONE);
    }

    @Override public void showEmpty() {
        emptyView.setVisibility(View.VISIBLE);
    }

    @Override public void hideEmpty() {
        emptyView.setVisibility(View.GONE);
    }

    @Override public void showLoading() {
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override public void hideLoading() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override public void showError(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override public void openNewShotView() {
        newShotBarViewDelegate.openNewShotView();
    }

    @Override public void pickImage() {
        newShotBarViewDelegate.pickImage();
    }

    @Override public void openNewShotViewWithImage(File image) {
        newShotBarViewDelegate.openNewShotViewWithImage(image);
    }

    @Override public void showDraftsButton() {
        newShotBarViewDelegate.showDraftsButton();
    }

    @Override public void hideDraftsButton() {
        newShotBarViewDelegate.hideDraftsButton();
    }

    @Override public void showWatchingPeopleCount(Integer count) {
        watchNumberCount = count;
        updateWatchNumberIcon();
    }

    @Override public void hideWatchingPeopleCount() {
        watchNumberCount = null;
        updateWatchNumberIcon();
    }
    //endregion
}
