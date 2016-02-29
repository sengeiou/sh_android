package com.shootr.mobile.ui.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.OnItemLongClick;
import com.shootr.mobile.R;
import com.shootr.mobile.domain.dagger.TemporaryFilesDir;
import com.shootr.mobile.ui.ToolbarDecorator;
import com.shootr.mobile.ui.activities.DraftsActivity;
import com.shootr.mobile.ui.activities.NewStreamActivity;
import com.shootr.mobile.ui.activities.PostNewShotActivity;
import com.shootr.mobile.ui.activities.ProfileContainerActivity;
import com.shootr.mobile.ui.activities.ShotDetailActivity;
import com.shootr.mobile.ui.activities.StreamDetailActivity;
import com.shootr.mobile.ui.adapters.TimelineAdapter;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnNiceShotListener;
import com.shootr.mobile.ui.adapters.listeners.OnUsernameClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnVideoClickListener;
import com.shootr.mobile.ui.base.BaseFragment;
import com.shootr.mobile.ui.component.PhotoPickerController;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.presenter.NewShotBarPresenter;
import com.shootr.mobile.ui.presenter.PinShotPresenter;
import com.shootr.mobile.ui.presenter.ReportShotPresenter;
import com.shootr.mobile.ui.presenter.StreamTimelineOptionsPresenter;
import com.shootr.mobile.ui.presenter.StreamTimelinePresenter;
import com.shootr.mobile.ui.presenter.WatchNumberPresenter;
import com.shootr.mobile.ui.views.NewShotBarView;
import com.shootr.mobile.ui.views.NullNewShotBarView;
import com.shootr.mobile.ui.views.NullWatchNumberView;
import com.shootr.mobile.ui.views.PinShotView;
import com.shootr.mobile.ui.views.ReportShotView;
import com.shootr.mobile.ui.views.StreamTimelineOptionsView;
import com.shootr.mobile.ui.views.StreamTimelineView;
import com.shootr.mobile.ui.views.WatchNumberView;
import com.shootr.mobile.ui.views.nullview.NullStreamTimelineOptionsView;
import com.shootr.mobile.ui.views.nullview.NullStreamTimelineView;
import com.shootr.mobile.ui.widgets.ListViewScrollObserver;
import com.shootr.mobile.util.AnalyticsTool;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.Clipboard;
import com.shootr.mobile.util.CrashReportTool;
import com.shootr.mobile.util.CustomContextMenu;
import com.shootr.mobile.util.FeedbackMessage;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.IntentFactory;
import com.shootr.mobile.util.Intents;
import com.shootr.mobile.util.MenuItemValueHolder;
import com.shootr.mobile.util.WritePermissionManager;
import java.io.File;
import java.util.List;
import java.util.Locale;
import javax.inject.Inject;
import timber.log.Timber;

public class StreamTimelineFragment extends BaseFragment
  implements StreamTimelineView, NewShotBarView, WatchNumberView, StreamTimelineOptionsView, ReportShotView,
  PinShotView {

    public static final String EXTRA_STREAM_ID = "streamId";
    public static final String EXTRA_STREAM_SHORT_TITLE = "streamShortTitle";
    public static final String EXTRA_ID_USER = "userId";
    private static final int REQUEST_STREAM_DETAIL = 1;

    //region Fields
    @Inject StreamTimelinePresenter streamTimelinePresenter;
    @Inject NewShotBarPresenter newShotBarPresenter;
    @Inject WatchNumberPresenter watchNumberPresenter;
    @Inject StreamTimelineOptionsPresenter streamTimelineOptionsPresenter;
    @Inject ReportShotPresenter reportShotPresenter;
    @Inject PinShotPresenter pinShotPresenter;

    @Inject ImageLoader imageLoader;
    @Inject AndroidTimeUtils timeUtils;
    @Inject ToolbarDecorator toolbarDecorator;
    @Inject IntentFactory intentFactory;
    @Inject FeedbackMessage feedbackMessage;
    @Inject @TemporaryFilesDir File tmpFiles;
    @Inject AnalyticsTool analyticsTool;
    @Inject WritePermissionManager writePermissionManager;
    @Inject CrashReportTool crashReportTool;

    @Bind(com.shootr.mobile.R.id.timeline_shot_list) ListView listView;
    @Bind(com.shootr.mobile.R.id.timeline_swipe_refresh) SwipeRefreshLayout swipeRefreshLayout;

    @Bind(com.shootr.mobile.R.id.timeline_empty) View emptyView;
    @Bind(com.shootr.mobile.R.id.timeline_checking_for_shots) TextView checkingForShotsView;
    @Bind(com.shootr.mobile.R.id.shot_bar_drafts) View draftsButton;

    @Bind(R.id.timeline_new_shots_indicator_container) LinearLayout timelineIndicator;
    @Bind(R.id.timeline_new_shots_indicator) LinearLayout timelineIndicatorContainer;
    @Bind(R.id.timeline_new_shots_indicator_text) TextView timelineIndicatorText;

    @BindString(com.shootr.mobile.R.string.report_base_url) String reportBaseUrl;
    @BindString(com.shootr.mobile.R.string.added_to_favorites) String addToFavorites;
    @BindString(com.shootr.mobile.R.string.shot_shared_message) String shotShared;

    @BindString(R.string.analytics_screen_stream_timeline) String analyticsScreenStreamTimeline;

    private TimelineAdapter adapter;

    private PhotoPickerController photoPickerController;
    private NewShotBarView newShotBarViewDelegate;
    private Integer watchNumberCount;
    private View footerProgress;
    private MenuItemValueHolder showHoldingShotsMenuItem = new MenuItemValueHolder();
    private MenuItemValueHolder showAllShotsMenuItem = new MenuItemValueHolder();
    private MenuItemValueHolder addToFavoritesMenuItem = new MenuItemValueHolder();
    private MenuItemValueHolder removeFromFavoritesMenuItem = new MenuItemValueHolder();
    private MenuItemValueHolder muteMenuItem = new MenuItemValueHolder();
    private MenuItemValueHolder unmuteMenuItem = new MenuItemValueHolder();
    //endregion

    public static StreamTimelineFragment newInstance(Bundle fragmentArguments) {
        StreamTimelineFragment fragment = new StreamTimelineFragment();
        fragment.setArguments(fragmentArguments);
        return fragment;
    }

    //region Lifecycle methods
    @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(com.shootr.mobile.R.layout.timeline_stream, container, false);
        ButterKnife.bind(this, fragmentView);
        return fragmentView;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        analyticsTool.analyticsStop(getContext(), getActivity());
        ButterKnife.unbind(this);
        streamTimelinePresenter.setView(new NullStreamTimelineView());
        newShotBarPresenter.setView(new NullNewShotBarView());
        watchNumberPresenter.setView(new NullWatchNumberView());
        streamTimelineOptionsPresenter.setView(new NullStreamTimelineOptionsView());
    }

    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializeViews();
        setHasOptionsMenu(true);
        String idStream = getArguments().getString(EXTRA_STREAM_ID);
        String streamAuthorIdUser = getArguments().getString(EXTRA_ID_USER);
        setStreamTitle(getArguments().getString(EXTRA_STREAM_SHORT_TITLE));
        setStreamTitleClickListener(idStream);
        if (streamAuthorIdUser != null) {
            initializePresenters(idStream, streamAuthorIdUser);
        } else {
            initializePresenters(idStream);
        }

        streamTimelinePresenter.setIsFirstLoad(true);
        streamTimelinePresenter.setIsFirstShotPosition(true);

        analyticsTool.analyticsStart(getContext(), analyticsScreenStreamTimeline);
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_STREAM_DETAIL && resultCode == NewStreamActivity.RESULT_EXIT_STREAM) {
            if (getActivity() != null) {
                getActivity().finish();
            }
        } else if (requestCode == REQUEST_STREAM_DETAIL && resultCode == Activity.RESULT_OK) {
            String updatedShortTitle = data.getStringExtra(StreamDetailActivity.EXTRA_STREAM_SHORT_TITLE);
            setStreamTitle(updatedShortTitle);
        } else {
            photoPickerController.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(com.shootr.mobile.R.menu.timeline, menu);

        showHoldingShotsMenuItem.bindRealMenuItem(menu.findItem(com.shootr.mobile.R.id.menu_showing_holding_shots));
        showHoldingShotsMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        showAllShotsMenuItem.bindRealMenuItem(menu.findItem(com.shootr.mobile.R.id.menu_showing_all_shots));
        showAllShotsMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        addToFavoritesMenuItem.bindRealMenuItem(menu.findItem(com.shootr.mobile.R.id.menu_stream_add_favorite));
        removeFromFavoritesMenuItem.bindRealMenuItem(menu.findItem(com.shootr.mobile.R.id.menu_stream_remove_favorite));

        muteMenuItem.bindRealMenuItem(menu.findItem(R.id.menu_mute_stream));
        unmuteMenuItem.bindRealMenuItem(menu.findItem(R.id.menu_unmute_stream));

        if (isAdded()) {
            updateWatchNumberIcon();
        }
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case com.shootr.mobile.R.id.menu_showing_holding_shots:
                streamTimelinePresenter.onHoldingShotsClick();
                return true;
            case com.shootr.mobile.R.id.menu_showing_all_shots:
                streamTimelinePresenter.onAllStreamShotsClick();
                return true;
            case com.shootr.mobile.R.id.menu_stream_add_favorite:
                streamTimelineOptionsPresenter.addToFavorites();
                return true;
            case com.shootr.mobile.R.id.menu_stream_remove_favorite:
                streamTimelineOptionsPresenter.removeFromFavorites();
                return true;
            case R.id.menu_mute_stream:
                streamTimelineOptionsPresenter.mute();
                return true;
            case R.id.menu_unmute_stream:
                streamTimelineOptionsPresenter.unmute();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override public void onResume() {
        super.onResume();
        streamTimelinePresenter.resume();
        newShotBarPresenter.resume();
        watchNumberPresenter.resume();
        streamTimelineOptionsPresenter.resume();
    }

    @Override public void onPause() {
        super.onPause();
        streamTimelinePresenter.pause();
        newShotBarPresenter.pause();
        watchNumberPresenter.pause();
        streamTimelineOptionsPresenter.pause();
    }

    private void initializePresenters(String idStream, String streamAuthorIdUser) {
        streamTimelinePresenter.initialize(this, idStream, streamAuthorIdUser);
        pinShotPresenter.initialize(this);
        newShotBarPresenter.initialize(this, idStream);
        watchNumberPresenter.initialize(this, idStream);
        streamTimelineOptionsPresenter.initialize(this, idStream);
        reportShotPresenter.initialize(this);
    }

    private void initializePresenters(String idStream) {
        streamTimelinePresenter.initialize(this, idStream);
        newShotBarPresenter.initialize(this, idStream);
        watchNumberPresenter.initialize(this, idStream);
        streamTimelineOptionsPresenter.initialize(this, idStream);
        reportShotPresenter.initialize(this);
    }

    //endregion

    private void setStreamTitle(String streamShortTitle) {
        toolbarDecorator.setTitle(streamShortTitle);
    }

    private void setStreamTitleClickListener(final String idStream) {
        toolbarDecorator.setTitleClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                navigateToStreamDetail(idStream);
            }
        });
    }

    private void setupNewShotBarDelegate() {
        newShotBarViewDelegate = new NewShotBarViewDelegate(photoPickerController, draftsButton, feedbackMessage) {
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

    private void updateWatchNumberIcon() {
        if (watchNumberCount != null) {
            toolbarDecorator.setSubtitle(watchNumberCount);
        }
    }

    //region Views manipulation
    private void initializeViews() {
        writePermissionManager.init(getActivity());
        setupListAdapter();
        setupSwipeRefreshLayout();
        setupListScrollListeners();
        setupPhotoPicker();
        setupNewShotBarDelegate();
    }

    private void setupPhotoPicker() {
        if (tmpFiles != null) {
            setupPhotoControllerWithTmpFilesDir();
        } else {
            crashReportTool.logException("Picker must have a temporary files directory.");
        }
    }

    private void setupPhotoControllerWithTmpFilesDir() {
        photoPickerController = new PhotoPickerController.Builder().onActivity(getActivity())
          .withTemporaryDir(tmpFiles)
          .withHandler(new PhotoPickerController.Handler() {
              @Override public void onSelected(File imageFile) {
                  newShotBarPresenter.newShotImagePicked(imageFile);
              }

              @Override public void onError(Exception e) {
                  Timber.e(e, "Error selecting image");
              }

              @Override public void startPickerActivityForResult(Intent intent, int requestCode) {
                  startActivityForResult(intent, requestCode);
              }
          })
          .build();
    }

    private void setupListAdapter() {
        View footerView = LayoutInflater.from(getActivity()).inflate(com.shootr.mobile.R.layout.item_list_loading, listView, false);
        footerProgress = ButterKnife.findById(footerView, com.shootr.mobile.R.id.loading_progress);

        footerProgress.setVisibility(View.GONE);

        listView.addFooterView(footerView, null, false);

        adapter = new TimelineAdapter(getActivity(), //
          imageLoader, //
          timeUtils, //
          new OnAvatarClickListener() {
              @Override public void onAvatarClick(String userId, View avatarView) {
                  openProfile(userId);
              }
          }, //
          new OnVideoClickListener() {
              @Override public void onVideoClick(String url) {
                  openVideo(url);
              }
          }, //
          new OnNiceShotListener() {
              @Override public void markNice(String idShot) {
                  streamTimelinePresenter.markNiceShot(idShot);
              }

              @Override public void unmarkNice(String idShot) {
                  streamTimelinePresenter.unmarkNiceShot(idShot);
              }
          }, //
          new OnUsernameClickListener() {
              @Override public void onUsernameClick(String username) {
                  openProfileFromUsername(username);
              }
          }, null, false);

        listView.setAdapter(adapter);
    }

    private void setupSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override public void onRefresh() {
                streamTimelinePresenter.refresh();
            }
        });
        swipeRefreshLayout.setColorSchemeResources(com.shootr.mobile.R.color.refresh_1,
          com.shootr.mobile.R.color.refresh_2,
          com.shootr.mobile.R.color.refresh_3,
          com.shootr.mobile.R.color.refresh_4);
    }

    private void setupListScrollListeners() {
        new ListViewScrollObserver(listView).setOnScrollUpAndDownListener(new ListViewScrollObserver.OnListViewScrollListener() {
            @Override public void onScrollUpDownChanged(int delta, int scrollPosition, boolean exact) {
                if (delta > 10) {
                    hideTimelineIndicator();
                }
            }

            @Override public void onScrollIdle() {
                if (listView.getFirstVisiblePosition() == 0) {
                    streamTimelinePresenter.setIsFirstShotPosition(true);
                    hideTimelineIndicator();
                } else {
                    streamTimelinePresenter.setIsFirstShotPosition(false);
                }
                checkIfEndOfListVisible();
            }
        });
    }

    private void checkIfEndOfListVisible() {
        int lastItemPosition = listView.getAdapter().getCount() - 1;
        int lastVisiblePosition = listView.getLastVisiblePosition();
        if (lastItemPosition == lastVisiblePosition && lastItemPosition >= 0) {
            streamTimelinePresenter.showingLastShot(adapter.getLastShot());
        }
    }
    //endregion

    private void openProfile(String idUser) {
        Intent profileIntent = ProfileContainerActivity.getIntent(getActivity(), idUser);
        startActivity(profileIntent);
    }

    private void openProfileFromUsername(String username) {
        Intent intentForUser = ProfileContainerActivity.getIntentWithUsername(getActivity(), username);
        startActivity(intentForUser);
    }

    private void openVideo(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    private void shareShotIntent(ShotModel shotModel) {
        Intent shareIntent = intentFactory.shareShotIntent(getActivity(), shotModel);
        Intents.maybeStartActivity(getActivity(), shareIntent);
    }

    private void copyShotCommentToClipboard(ShotModel shotModel) {
        Clipboard.copyShotComment(getActivity(), shotModel);
    }

    @OnClick(com.shootr.mobile.R.id.shot_bar_text) public void startNewShot() {
        newShotBarPresenter.newShotFromTextBox();
    }

    @OnClick(com.shootr.mobile.R.id.shot_bar_photo) public void startNewShotWithPhoto() {
        newShotBarPresenter.newShotFromImage();
    }

    @OnClick(com.shootr.mobile.R.id.shot_bar_drafts) public void openDraftsClicked() {
        startActivity(new Intent(getActivity(), DraftsActivity.class));
    }

    @OnClick(R.id.timeline_new_shots_indicator_container) public void goToTopOfTimeline(){
        listView.smoothScrollToPosition(0);
    }

    //region View methods
    @Override public void setShots(List<ShotModel> shots) {
        adapter.setShots(shots);
        adapter.notifyDataSetChanged();
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

    @Override public void navigateToStreamDetail(String idStream) {
        startActivityForResult(StreamDetailActivity.getIntent(getActivity(), idStream), REQUEST_STREAM_DETAIL);
    }

    @Override public void showCheckingForShots() {
        checkingForShotsView.setVisibility(View.VISIBLE);
    }

    @Override public void hideCheckingForShots() {
        checkingForShotsView.setVisibility(View.GONE);
    }

    @Override public void showShotShared() {
        feedbackMessage.show(getView(), shotShared);
    }

    @Override public void hideHoldingShots() {
        showHoldingShotsMenuItem.setVisible(false);
    }

    @Override public void showAllStreamShots() {
        showAllShotsMenuItem.setVisible(true);
        feedbackMessage.show(getView(), com.shootr.mobile.R.string.showing_shots_by_holder);
    }

    @Override public void showHoldingShots() {
        showHoldingShotsMenuItem.setVisible(true);
    }

    @Override public void hideAllStreamShots() {
        showAllShotsMenuItem.setVisible(false);
    }

    @Override public void setTitle(String shortTitle) {
        setStreamTitle(shortTitle);
    }

    @Override public Integer getFirstVisiblePosition() {
        return listView.getFirstVisiblePosition();
    }

    @Override public void setPosition(int newPosition) {
        listView.setSelection(newPosition);
    }

    @Override public void showTimelineIndicator(Integer numberNewShots) {
        timelineIndicatorContainer.setVisibility(View.VISIBLE);
        timelineIndicator.setVisibility(View.VISIBLE);
        String indicatorText = getResources().getQuantityString(R.plurals.new_shots_indicator, numberNewShots, numberNewShots);
        timelineIndicatorText.setText(indicatorText);
    }

    @Override public void hideTimelineIndicator() {
        timelineIndicatorContainer.setVisibility(View.GONE);
        timelineIndicator.setVisibility(View.GONE);
        streamTimelinePresenter.setNewShotsNumber(0);
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
        feedbackMessage.showLong(getView(), message);
    }

    @Override public void showUnmuteButton() {
        unmuteMenuItem.setVisible(true);
    }

    @Override public void showMuteButton() {
        muteMenuItem.setVisible(true);
    }

    @Override public void hideMuteButton() {
        muteMenuItem.setVisible(false);
    }

    @Override public void hideUnmuteButton() {
        unmuteMenuItem.setVisible(false);
    }

    @Override public void showContextMenuWithUnblock(final ShotModel shotModel) {
        getBaseContextMenuOptions(shotModel).addAction(com.shootr.mobile.R.string.report_context_menu_unblock,
          new Runnable() {
              @Override public void run() {
                  reportShotPresenter.unblockUser(shotModel);
              }
          }).show();
    }

    @Override public void showBlockFollowingUserAlert() {
        feedbackMessage.showLong(getView(), com.shootr.mobile.R.string.block_user_error);
    }

    @Override public void showUserBlocked() {
        feedbackMessage.show(getView(), com.shootr.mobile.R.string.user_blocked);
    }

    @Override public void showUserUnblocked() {
        feedbackMessage.show(getView(), com.shootr.mobile.R.string.user_unblocked);
    }

    @Override public void showBlockUserConfirmation() {
        new AlertDialog.Builder(getActivity()).setMessage(com.shootr.mobile.R.string.block_user_dialog_message)
          .setPositiveButton(getString(com.shootr.mobile.R.string.block_user_dialog_block),
            new DialogInterface.OnClickListener() {
                @Override public void onClick(DialogInterface dialog, int which) {
                    reportShotPresenter.confirmBlock();
                }
            })
          .setNegativeButton(getString(com.shootr.mobile.R.string.block_user_dialog_cancel), null)
          .create()
          .show();
    }

    @Override public void showErrorLong(String messageForError) {
        feedbackMessage.showLong(getView(), messageForError);
    }

    @Override public void showUserBanned() {
        /* no-op */
    }

    @Override public void showUserUnbanned() {
        /* no-op */
    }

    @Override public void showAuthorContextMenuWithoutPin(final ShotModel shotModel) {
        CustomContextMenu.Builder builder = getBaseContextMenuOptions(shotModel);
        builder.addAction(R.string.report_context_menu_delete, new Runnable() {
            @Override public void run() {
                openDeleteConfirmation(shotModel);
            }
        }).show();
    }

    @Override public void notifyPinnedShot(ShotModel shotModel) {
        adapter.onPinnedShot(shotModel);
    }

    @Override public void showPinned() {
        feedbackMessage.show(getView(), R.string.shot_pinned);
    }

    @Override public void openNewShotView() {
        newShotBarViewDelegate.openNewShotView();
    }

    @Override public void pickImage() {
        if (writePermissionManager.hasWritePermission()) {
            newShotBarViewDelegate.pickImage();
        } else {
            writePermissionManager.requestWritePermissionToUser();
        }
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

    @Override public void showAddToFavoritesButton() {
        addToFavoritesMenuItem.setVisible(true);
    }

    @Override public void hideAddToFavoritesButton() {
        addToFavoritesMenuItem.setVisible(false);
    }

    @Override public void showRemoveFromFavoritesButton() {
        removeFromFavoritesMenuItem.setVisible(true);
    }

    @Override public void hideRemoveFromFavoritesButton() {
        removeFromFavoritesMenuItem.setVisible(false);
    }

    @Override public void showAddedToFavorites() {
        feedbackMessage.show(getView(), addToFavorites);
    }

    @Override public void handleReport(String sessionToken, ShotModel shotModel) {
        reportShotPresenter.reportClicked(Locale.getDefault().getLanguage(), sessionToken, shotModel);
    }

    @Override public void showAlertLanguageSupportDialog(final String sessionToken, final ShotModel shotModel) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder //
          .setMessage(getString(R.string.language_support_alert)) //
          .setPositiveButton(getString(com.shootr.mobile.R.string.email_confirmation_ok),
            new DialogInterface.OnClickListener() {
                @Override public void onClick(DialogInterface dialog, int which) {
                    goToReport(sessionToken, shotModel);
                }
            }).show();
    }

    @Override public void showHolderContextMenu(final ShotModel shot) {
        new CustomContextMenu.Builder(getActivity()).addAction(R.string.menu_share_shot_via_shootr, new Runnable() {
            @Override public void run() {
                streamTimelinePresenter.shareShot(shot);
            }
        }).addAction(R.string.menu_share_shot_via, new Runnable() {
            @Override public void run() {
                shareShotIntent(shot);
            }
        }).addAction(R.string.menu_copy_text, new Runnable() {
            @Override public void run() {
                copyShotCommentToClipboard(shot);
            }
        }).addAction(R.string.report_context_menu_delete, new Runnable() {
            @Override public void run() {
                openDeleteConfirmation(shot);
            }
        }).show();
    }

    @Override
    public void goToReport(String sessionToken, ShotModel shotModel){
        Intent browserIntent =
          new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(reportBaseUrl, sessionToken, shotModel.getIdShot())));
        startActivity(browserIntent);
    }

    @Override public void showEmailNotConfirmedError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(getActivity().getString(com.shootr.mobile.R.string.alert_report_confirmed_email_message))
          .setTitle(getActivity().getString(com.shootr.mobile.R.string.alert_report_confirmed_email_title))
          .setPositiveButton(getActivity().getString(com.shootr.mobile.R.string.alert_report_confirmed_email_ok), null);

        builder.create().show();
    }

    @Override public void showContextMenu(final ShotModel shotModel) {
        CustomContextMenu.Builder builder = getBaseContextMenuOptions(shotModel);
        builder.addAction(com.shootr.mobile.R.string.report_context_menu_report, new Runnable() {
            @Override public void run() {
                reportShotPresenter.report(shotModel);
            }
        }).addAction(com.shootr.mobile.R.string.report_context_menu_block, new Runnable() {
            @Override public void run() {
                reportShotPresenter.blockUserClicked(shotModel);
            }
        }).show();
    }

    @Override public void showAuthorContextMenuWithPin(final ShotModel shotModel) {
        new CustomContextMenu.Builder(getActivity())
          .addAction(R.string.menu_pin_shot, new Runnable() {
              @Override public void run() {
                  pinShotPresenter.pinToProfile(shotModel);
              }
          }).addAction(R.string.menu_share_shot_via_shootr, new Runnable() {
            @Override public void run() {
                streamTimelinePresenter.shareShot(shotModel);
            }
        }).addAction(R.string.menu_share_shot_via, new Runnable() {
            @Override public void run() {
                shareShotIntent(shotModel);
            }
        }).addAction(R.string.menu_copy_text, new Runnable() {
            @Override public void run() {
                copyShotCommentToClipboard(shotModel);
            }
        }).addAction(R.string.report_context_menu_delete, new Runnable() {
            @Override public void run() {
                openDeleteConfirmation(shotModel);
            }
        }).show();
    }

    private void openDeleteConfirmation(final ShotModel shotModel) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        alertDialogBuilder.setMessage(com.shootr.mobile.R.string.delete_shot_confirmation_message);
        alertDialogBuilder.setPositiveButton(com.shootr.mobile.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                reportShotPresenter.deleteShot(shotModel);
            }
        });
        alertDialogBuilder.setNegativeButton(com.shootr.mobile.R.string.cancel, null);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private CustomContextMenu.Builder getBaseContextMenuOptions(final ShotModel shotModel) {
        return new CustomContextMenu.Builder(getActivity()).addAction(R.string.menu_share_shot_via_shootr,
          new Runnable() {
              @Override public void run() {
                  streamTimelinePresenter.shareShot(shotModel);
              }
          }).addAction(R.string.menu_share_shot_via, new Runnable() {
            @Override public void run() {
                shareShotIntent(shotModel);
            }
        }).addAction(R.string.menu_copy_text, new Runnable() {
            @Override public void run() {
                copyShotCommentToClipboard(shotModel);
            }
        });
    }

    @Override public void notifyDeletedShot(ShotModel shotModel) {
        adapter.removeShot(shotModel);
        adapter.notifyDataSetChanged();
        streamTimelinePresenter.onShotDeleted(adapter.getCount());
    }

    @OnItemClick(com.shootr.mobile.R.id.timeline_shot_list) public void openShot(int position) {
        ShotModel shot = adapter.getItem(position);
        Intent intent = ShotDetailActivity.getIntentForActivity(getActivity(), shot);
        startActivity(intent);
    }

    @OnItemLongClick(com.shootr.mobile.R.id.timeline_shot_list) public boolean openContextMenu(int position) {
        ShotModel shot = adapter.getItem(position);
        String streamAuthorIdUser = getArguments().getString(EXTRA_ID_USER);
        reportShotPresenter.onShotLongPressed(shot, streamAuthorIdUser);
        return true;
    }
    //endregion
}
