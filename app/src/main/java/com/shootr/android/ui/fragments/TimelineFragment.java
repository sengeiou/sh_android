package com.shootr.android.ui.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.melnykov.fab.FloatingActionButton;
import com.shootr.android.R;
import com.shootr.android.ui.ToolbarDecorator;
import com.shootr.android.ui.activities.BaseNavDrawerToolbarActivity;
import com.shootr.android.ui.activities.DraftsActivity;
import com.shootr.android.ui.activities.PostNewShotActivity;
import com.shootr.android.ui.adapters.TimelineAdapter;
import com.shootr.android.ui.base.BaseFragment;
import com.shootr.android.ui.component.PhotoPickerController;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.presenter.EventSelectionPresenter;
import com.shootr.android.ui.presenter.NewShotBarPresenter;
import com.shootr.android.ui.presenter.TimelinePresenter;
import com.shootr.android.ui.views.EventSelectionView;
import com.shootr.android.ui.views.NewShotBarView;
import com.shootr.android.ui.views.TimelineView;
import com.shootr.android.ui.widgets.ListViewScrollObserver;
import com.shootr.android.util.AndroidTimeUtils;
import com.shootr.android.util.PicassoWrapper;
import java.io.File;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class TimelineFragment extends BaseFragment implements TimelineView, NewShotBarView, EventSelectionView {

    private static final int REQUEST_NEW_SHOT = 1;

    //region Fields
    @Inject EventSelectionPresenter eventSelectionPresenter;
    @Inject TimelinePresenter timelinePresenter;
    @Inject NewShotBarPresenter newShotBarPresenter;
    @Inject PicassoWrapper picasso;
    @Inject AndroidTimeUtils timeUtils;

    @InjectView(R.id.timeline_list) ListView listView;
    @InjectView(R.id.timeline_new) View newShotView;
    @InjectView(R.id.timeline_new_text) TextView newShotTextView;
    @InjectView(R.id.timeline_swipe_refresh) SwipeRefreshLayout swipeRefreshLayout;
    @InjectView(R.id.exit_event_fab) FloatingActionButton exitEventFab;

    @InjectView(R.id.timeline_empty) View emptyView;
    @InjectView(R.id.timeline_drafts) View draftsButton;

    @Deprecated
    private TimelineAdapter adapter;
    private View.OnClickListener avatarClickListener;
    private View.OnClickListener imageClickListener;
    private PhotoPickerController photoPickerController;

    private ToolbarDecorator toolbarDecorator;
    //endregion

    //region Lifecycle methods
    @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_timeline, container, true);
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
    }

    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializeToolbar();
        initializePresenters();
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        photoPickerController.onActivityResult(requestCode, resultCode, data);
    }

    @Override public void onResume() {
        super.onResume();
        timelinePresenter.resume();
        newShotBarPresenter.resume();
    }

    @Override public void onPause() {
        super.onPause();
        timelinePresenter.pause();
        newShotBarPresenter.pause();
    }
    //endregion

    private void initializeToolbar() {
        //TODO So coupling. Much bad. Such ugly.
        toolbarDecorator = ((BaseNavDrawerToolbarActivity) getActivity()).getToolbarDecorator();
    }

    private void initializePresenters() {
        timelinePresenter.initialize(this);
        newShotBarPresenter.initialize(this);
        eventSelectionPresenter.initialize(this);
    }

    //region Views manipulation
    private void initializeViews() {
        setupListAdapter();
        setupSwipeRefreshLayout();
        setupListScrollListeners();
        setupDraftButtonTransition();
        setupPhotoPicker();
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
        adapter = new TimelineAdapter(getActivity(), picasso, avatarClickListener, imageClickListener, timeUtils);
        listView.setAdapter(adapter);
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
            @Override
            public void onScrollUpDownChanged(int delta, int scrollPosition, boolean exact) {
                if (delta < -10) {
                    exitEventFab.hide();
                } else if (delta > 10) {
                    exitEventFab.show();
                }
            }

            @Override
            public void onScrollIdle() {
                int lastItemPosition = listView.getAdapter().getCount() - 1;
                int lastVisiblePosition = listView.getLastVisiblePosition();
                if (lastItemPosition == lastVisiblePosition) {
                    timelinePresenter.showingLastShot(adapter.getLastShot());
                }
            }
        });
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
    //endregion

    @OnClick(R.id.timeline_new_text)
    public void startNewShot() {
        newShotBarPresenter.newShotFromTextBox();
    }

    @OnClick(R.id.timeline_new_image_camera)
    public void startNewShotWithPhoto() {
       newShotBarPresenter.newShotFromImage();
    }

    @OnClick(R.id.timeline_drafts)
    public void openDrafts() {
        startActivity(new Intent(getActivity(), DraftsActivity.class));
    }

    //region View methods
    @Override public void setShots(List<ShotModel> shots) {
        adapter.setShots(shots);
    }

    @Override public void addNewShots(List<ShotModel> newShots) {
        adapter.addShotsAbove(newShots);
    }

    @Override public void addOldShots(List<ShotModel> oldShots) {
        adapter.addShotsBelow(oldShots);
    }

    @Override public void showLoadingOldShots() {
        //TODO
    }

    @Override public void hideLoadingOldShots() {
        //TODO
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
        Intent newShotIntent = new Intent(getActivity(), PostNewShotActivity.class);
        startActivityForResult(newShotIntent, REQUEST_NEW_SHOT);
    }

    @Override public void pickImage() {
        photoPickerController.pickPhoto();
    }

    @Override public void openNewShotViewWithImage(File image) {
        Intent newShotIntent = new Intent(getActivity(), PostNewShotActivity.class);
        newShotIntent.putExtra(PostNewShotActivity.EXTRA_PHOTO, image);
        startActivityForResult(newShotIntent, REQUEST_NEW_SHOT);
    }

    @Override public void showDraftsButton() {
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

    @Override public void hideDraftsButton() {
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

    @Override public void showCurrentEventTitle(String eventTitle) {
        toolbarDecorator.setTitle(eventTitle);
    }

    @Override public void showHallTitle() {
        toolbarDecorator.setTitle(getString(R.string.timeline_hall_title));
    }
    //endregion
}
