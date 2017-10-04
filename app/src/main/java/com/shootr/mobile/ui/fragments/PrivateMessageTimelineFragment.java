package com.shootr.mobile.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.shootr.mobile.R;
import com.shootr.mobile.domain.dagger.TemporaryFilesDir;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.ui.ToolbarDecorator;
import com.shootr.mobile.ui.activities.NewStreamActivity;
import com.shootr.mobile.ui.activities.PhotoViewActivity;
import com.shootr.mobile.ui.activities.PostNewShotActivity;
import com.shootr.mobile.ui.activities.ProfileActivity;
import com.shootr.mobile.ui.activities.StreamDetailActivity;
import com.shootr.mobile.ui.adapters.MessagesTimelineAdapter;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnImageClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnUrlClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnUsernameClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnVideoClickListener;
import com.shootr.mobile.ui.base.BaseFragment;
import com.shootr.mobile.ui.component.PhotoPickerController;
import com.shootr.mobile.ui.model.BaseMessageModel;
import com.shootr.mobile.ui.model.PrivateMessageModel;
import com.shootr.mobile.ui.presenter.NewMessageBarPresenter;
import com.shootr.mobile.ui.presenter.PrivateMessageTimelinePresenter;
import com.shootr.mobile.ui.views.NewShotBarView;
import com.shootr.mobile.ui.views.PrivateMessageChannelTimelineView;
import com.shootr.mobile.ui.views.nullview.NullNewShotBarView;
import com.shootr.mobile.ui.widgets.MessageBox;
import com.shootr.mobile.ui.widgets.PreCachingLayoutManager;
import com.shootr.mobile.util.AnalyticsTool;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.CrashReportTool;
import com.shootr.mobile.util.FeedbackMessage;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.MenuItemValueHolder;
import com.shootr.mobile.util.WritePermissionManager;
import java.io.File;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class PrivateMessageTimelineFragment extends BaseFragment
    implements PrivateMessageChannelTimelineView, NewShotBarView {

  public static final String EXTRA_ID_TARGET_USER = "targetUser";
  public static final String EXTRA_ID_CHANNEL = "idChannel";

  private static final int REQUEST_STREAM_DETAIL = 1;

  //region Fields
  @Inject PrivateMessageTimelinePresenter presenter;
  @Inject NewMessageBarPresenter newShotBarPresenter;

  @Inject ImageLoader imageLoader;
  @Inject AndroidTimeUtils timeUtils;
  @Inject ToolbarDecorator toolbarDecorator;
  @Inject FeedbackMessage feedbackMessage;
  @Inject @TemporaryFilesDir File tmpFiles;
  @Inject AnalyticsTool analyticsTool;
  @Inject WritePermissionManager writePermissionManager;
  @Inject CrashReportTool crashReportTool;
  @Inject SessionRepository sessionRepository;

  @BindView(R.id.timeline_shot_list) RecyclerView messageRecycler;
  @BindView(R.id.timeline_swipe_refresh) SwipeRefreshLayout swipeRefreshLayout;
  @BindView(R.id.timeline_empty) View emptyView;
  @BindView(R.id.timeline_checking_for_shots) TextView checkingForShotsView;
  @BindView(R.id.timeline_new_shot_bar) MessageBox newShotBar;
  @BindView(R.id.new_shots_notificator_container) RelativeLayout newShotsNotificatorContainer;
  @BindView(R.id.new_shots_notificator_text) TextView newShotsNotificatorText;

  @BindString(R.string.analytics_action_private_message) String analyticsActionSendPrivateMessage;
  @BindString(R.string.analytics_label_private_message) String analyticsLabelSendPrivateMessage;
  @BindString(R.string.analytics_source_timeline) String timelineSource;
  @BindString(R.string.not_allowed_blocked_user) String messageBlockedUserError;

  private MessagesTimelineAdapter adapter;
  private PhotoPickerController photoPickerController;
  private NewShotBarView newShotBarViewDelegate;
  private View footerProgress;
  private MenuItemValueHolder showHoldingShotsMenuItem = new MenuItemValueHolder();
  private MenuItemValueHolder showAllShotsMenuItem = new MenuItemValueHolder();
  private MenuItemValueHolder addToFavoritesMenuItem = new MenuItemValueHolder();
  private MenuItemValueHolder removeFromFavoritesMenuItem = new MenuItemValueHolder();
  private MenuItemValueHolder muteMenuItem = new MenuItemValueHolder();
  private MenuItemValueHolder unmuteMenuItem = new MenuItemValueHolder();
  private PreCachingLayoutManager preCachingLayoutManager;
  private Unbinder unbinder;
  private String idTargetUser;
  private String streamTitle;
  private String idChannel;
  //endregion

  public static PrivateMessageTimelineFragment newInstance(Bundle fragmentArguments) {
    PrivateMessageTimelineFragment fragment = new PrivateMessageTimelineFragment();
    fragment.setArguments(fragmentArguments);
    return fragment;
  }

  //region Lifecycle methods
  @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View fragmentView = inflater.inflate(R.layout.timeline_private_messages, container, false);
    unbinder = ButterKnife.bind(this, fragmentView);
    preCachingLayoutManager = new PreCachingLayoutManager(getContext());
    messageRecycler.setLayoutManager(preCachingLayoutManager);
    messageRecycler.setHasFixedSize(false);
    newShotBar.setVisibility(View.VISIBLE);
    return fragmentView;
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
    newShotBarPresenter.setView(new NullNewShotBarView());
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    idTargetUser = getArguments().getString(EXTRA_ID_TARGET_USER);
    idChannel = getArguments().getString(EXTRA_ID_CHANNEL);
    initializeViews();
    setHasOptionsMenu(true);
    setStreamTitleClickListener(idTargetUser);
    setupPresentersInitialization(idTargetUser, idChannel);
  }

  protected void setupPresentersInitialization(String idTargetuser, String idChannel) {
    presenter.setIsFirstLoad(true);
    presenter.setIsFirstShotPosition(true);
    initializePresenters(idTargetuser, idChannel);
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == REQUEST_STREAM_DETAIL
        && resultCode == NewStreamActivity.RESULT_EXIT_STREAM) {
      if (getActivity() != null) {
        getActivity().finish();
      }
    } else if (requestCode == REQUEST_STREAM_DETAIL && resultCode == Activity.RESULT_OK) {
      String updatedTitle = data.getStringExtra(StreamDetailActivity.EXTRA_STREAM_TITLE);
      setStreamTitle(updatedTitle);
    } else {
      photoPickerController.onActivityResult(requestCode, resultCode, data);
    }
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.timeline, menu);
    showHoldingShotsMenuItem.bindRealMenuItem(menu.findItem(R.id.menu_showing_holding_shots));
    showHoldingShotsMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    showAllShotsMenuItem.bindRealMenuItem(menu.findItem(R.id.menu_showing_all_shots));
    showAllShotsMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    addToFavoritesMenuItem.bindRealMenuItem(menu.findItem(R.id.menu_stream_add_favorite));
    removeFromFavoritesMenuItem.bindRealMenuItem(menu.findItem(R.id.menu_stream_remove_favorite));
    muteMenuItem.bindRealMenuItem(menu.findItem(R.id.menu_mute_stream));
    unmuteMenuItem.bindRealMenuItem(menu.findItem(R.id.menu_unmute_stream));
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.menu_showing_holding_shots:
        return true;
      case R.id.menu_showing_all_shots:
        return true;
      case R.id.menu_stream_add_favorite:
        return true;
      case R.id.menu_stream_remove_favorite:
        return true;
      case R.id.menu_mute_stream:
        return true;
      case R.id.menu_unmute_stream:
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override public void onResume() {
    super.onResume();
    presenter.resume();
    newShotBarPresenter.resume();
  }

  @Override public void onPause() {
    super.onPause();
    presenter.pause();
    newShotBarPresenter.pause();
  }

  private void initializePresenters(String idTargetUser, String idChannel) {
    presenter.initialize(this, idChannel, idTargetUser);
    newShotBarPresenter.initialize(this, this.idTargetUser);
  }

  //endregion

  //region Views manipulation
  private void initializeViews() {
    writePermissionManager.init(getActivity());
    setupFooter();
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
            newShotBarPresenter.newMessageImagePicked(imageFile);
          }

          @Override public void onError(Exception e) {
            Timber.e(e, "Error selecting image");
          }

          @Override public void startPickerActivityForResult(Intent intent, int requestCode) {
            startActivityForResult(intent, requestCode);
          }

          @Override public void openEditTopicDialog() {
            newShotBarPresenter.openEditTopicCustomDialog();
          }

          @Override public void onCheckIn() {
            /* no-op */
          }

          @Override public boolean hasWritePermission() {
            return writePermissionManager.hasWritePermission();
          }

          @Override public void requestWritePermissionToUser() {
            writePermissionManager.requestWritePermissionToUser();
          }
        })
        .build();
  }

  private void setupFooter() {
    View footerView = LayoutInflater.from(getActivity())
        .inflate(R.layout.item_list_loading, messageRecycler, false);
    footerProgress = ButterKnife.findById(footerView, R.id.loading_progress);
    footerProgress.setVisibility(View.GONE);
  }

  private void setupListAdapter() {

    adapter = new MessagesTimelineAdapter(imageLoader, timeUtils, new OnAvatarClickListener() {
      @Override public void onAvatarClick(String userId, View avatarView) {
        openProfile(userId);
      }
    }, new OnVideoClickListener() {
      @Override public void onVideoClick(String url) {
        openVideo(url);
      }
    }, new OnUsernameClickListener() {
      @Override public void onUsernameClick(String username) {
        openProfileFromUsername(username);
      }
    }, new OnImageClickListener() {
      @Override public void onImageClick(View sharedImage, BaseMessageModel shot) {
        openImage(sharedImage, shot.getImage().getImageUrl());
      }
    }, new OnUrlClickListener() {
      @Override public void onClick() {
        //TODO analythics
      }
    });
    messageRecycler.setAdapter(adapter);
  }

  private void setupSwipeRefreshLayout() {
    swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override public void onRefresh() {
        presenter.refresh();
      }
    });
    swipeRefreshLayout.setColorSchemeResources(R.color.refresh_1, R.color.refresh_2,
        R.color.refresh_3, R.color.refresh_4);
  }

  private void setupListScrollListeners() {
    messageRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
      }

      @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if (messageRecycler != null) {
          if (preCachingLayoutManager.findFirstVisibleItemPosition() == 0) {
            hideNewShotsIndicator();
          } else {
            presenter.setIsFirstShotPosition(false);
          }
          checkIfEndOfListVisible();
        }
      }
    });
  }

  private void checkIfEndOfListVisible() {
    int lastItemPosition = messageRecycler.getAdapter().getItemCount() - 1;
    int lastVisiblePosition = preCachingLayoutManager.findLastVisibleItemPosition();
    if (lastItemPosition == lastVisiblePosition && lastItemPosition >= 0) {
      presenter.showingLastMessage(adapter.getLastMessage());
    }
  }
  //endregion

  private void openProfile(String idUser) {
    Intent profileIntent = ProfileActivity.getIntent(getActivity(), idUser);
    startActivity(profileIntent);
    getActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.stay);
  }

  private void openImage(View sharedImage, String imageUrl) {
    Intent intent = PhotoViewActivity.getIntentForActivity(getContext(), imageUrl, imageUrl);
    startActivity(intent);
  }

  private void openProfileFromUsername(String username) {
    Intent intentForUser = ProfileActivity.getIntentWithUsername(getActivity(), username);
    startActivity(intentForUser);
  }

  private void openVideo(String url) {
    Uri uri = Uri.parse(url);
    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
    startActivity(intent);
  }

  private void setStreamTitle(String streamTitle) {
    this.streamTitle = streamTitle;
    toolbarDecorator.setTitle(streamTitle);
  }

  private void setAvatarImage(String imageUrl, String username) {
    if (imageUrl != null) {
      toolbarDecorator.setAvatarImage(imageUrl, username);
    }
  }

  private void setMutedUser(boolean isMuted) {
    toolbarDecorator.setMutedUser(isMuted);
  }

  private void setStreamTitleClickListener(final String idChannel) {
    toolbarDecorator.setTitleClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        /* no-op */
      }
    });
  }

  private void setupNewShotBarDelegate() {
    newShotBar.init(getActivity(), photoPickerController, imageLoader, feedbackMessage,
        new MessageBox.OnActionsClick() {
          @Override public void onTopicClick() {
            /* no-op */
          }

          @Override public void onNewShotClick() {
            Intent newShotIntent = PostNewShotActivity.IntentBuilder //
                .from(getActivity()) //
                .isPrivateMessage(true)
                .withIdTargetUser(idTargetUser)
                .setStreamData(idTargetUser, streamTitle)
                .build();
            startActivity(newShotIntent);
          }

          @Override public void onShotWithImageClick(File image) {
            Intent newShotIntent = PostNewShotActivity.IntentBuilder //
                .from(getActivity()) //
                .isPrivateMessage(true).withImage(image) //
                .withIdTargetUser(idTargetUser).setStreamData(idTargetUser, streamTitle).build();
            startActivity(newShotIntent);
          }

          @Override public void onAttachClick() {
            newShotBarPresenter.newMessageFromImage();
          }

          @Override public void onSendClick() {
            sendPrivateMessageToMixPanel();
          }

          @Override public void onCheckInClick() {
            /* no-op */
          }
        }, true, idTargetUser);
  }

  private void sendPrivateMessageToMixPanel() {
    AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
    builder.setContext(getContext());
    builder.setActionId(analyticsActionSendPrivateMessage);
    builder.setLabelId(analyticsLabelSendPrivateMessage);
    builder.setSource(timelineSource);
    builder.setIdTargetUser(idTargetUser);
    builder.setTargetUsername(streamTitle);
    builder.setUser(sessionRepository.getCurrentUser());
    analyticsTool.analyticsSendAction(builder);
    analyticsTool.appsFlyerSendAction(builder);
  }

  @OnClick(R.id.new_shots_notificator_text) public void goToTopOfTimeline() {
    messageRecycler.scrollToPosition(0);
    presenter.setNewShotsNumber(0);
    presenter.setIsFirstShotPosition(true);
  }

  //region View methods
  @Override public void setMessages(List<PrivateMessageModel> privateMessageModels) {
    hideEmpty();
    adapter.setMessages(privateMessageModels);
    adapter.notifyDataSetChanged();
  }

  @Override public void hideShots() {
    messageRecycler.setVisibility(View.GONE);
  }

  @Override public void showShots() {
    hideEmpty();
    if (messageRecycler != null) {
      messageRecycler.setVisibility(View.VISIBLE);
    }
  }

  @Override public void addOldMessages(List<PrivateMessageModel> oldMessages) {
    adapter.addMessagesBelow(oldMessages);
  }

  @Override public void showBlockedUserError() {
    feedbackMessage.showLong(getView(), messageBlockedUserError);
  }

  @Override public void showLoadingOldShots() {
    footerProgress.setVisibility(View.VISIBLE);
  }

  @Override public void hideLoadingOldShots() {
    footerProgress.setVisibility(View.GONE);
  }

  @Override public void showCheckingForShots() {
    checkingForShotsView.setVisibility(View.VISIBLE);
  }

  @Override public void hideCheckingForShots() {
    if (checkingForShotsView != null) {
      checkingForShotsView.setVisibility(View.GONE);
    }
  }

  @Override public void setTitle(String title) {
    setStreamTitle(title);
  }

  @Override public void setVerified(boolean isVerified) {
    toolbarDecorator.setVerifiedStream(isVerified);
  }

  @Override public void sendAnalythicsEnterTimeline() {
    /*no-op*/
  }

  @Override public void setImage(String avatarImage, String username) {
    setAvatarImage(avatarImage, username);
  }

  @Override public void setMuted(boolean isMuted) {
    setMutedUser(isMuted);
  }

  @Override public void showNewShotsIndicator(Integer numberNewShots) {
    try {
      if (newShotsNotificatorContainer != null) {
        newShotsNotificatorContainer.setVisibility(View.VISIBLE);
        String indicatorText =
            getResources().getQuantityString(R.plurals.new_shots_indicator, numberNewShots,
                numberNewShots);
        newShotsNotificatorText.setText(indicatorText);
      }
    } catch (NullPointerException error) {
      crashReportTool.logException(error);
    }
  }

  @Override public void hideNewShotsIndicator() {
    try {
      newShotsNotificatorContainer.setVisibility(View.GONE);
      presenter.setIsFirstShotPosition(true);
    } catch (NullPointerException error) {
      crashReportTool.logException(error);
    }
  }

  @Override public void setRemainingCharactersCount(int remainingCharacters) {
    /* no-op */
  }

  @Override public void setRemainingCharactersColorValid() {
    /* no-op */
  }

  @Override public void setRemainingCharactersColorInvalid() {
    /* no-op */
  }

  @Override public void addAbove(List<PrivateMessageModel> privateMessageModels) {
    adapter.addMessagesAbove(privateMessageModels);
    adapter.notifyItemRangeInserted(0, privateMessageModels.size());
  }

  @Override public void addMessages(List<PrivateMessageModel> privateMessageModels) {
    if (messageRecycler != null) {
      messageRecycler.setVisibility(View.VISIBLE);
      hideLoading();
      adapter.addMessages(privateMessageModels);
      presenter.setIsFirstShotPosition(true);
      presenter.setNewShotsNumber(0);
      messageRecycler.smoothScrollToPosition(0);
    }
  }

  @Override public void updateMessagesInfo(List<PrivateMessageModel> privateMessageModels) {
    adapter.setMessages(privateMessageModels);
    adapter.notifyDataSetChanged();
  }

  @Override public void showEmpty() {
    if (emptyView != null) {
      if (adapter.getItemCount() == 0) {
        emptyView.setVisibility(View.VISIBLE);
      }
    }
  }

  @Override public void hideEmpty() {
    if (emptyView != null) {
      emptyView.setVisibility(View.GONE);
    }
  }

  @Override public void showLoading() {
    swipeRefreshLayout.setRefreshing(true);
  }

  @Override public void hideLoading() {
    if (swipeRefreshLayout != null) {
      swipeRefreshLayout.setRefreshing(false);
    }
  }

  @Override public void showError(String message) {
    feedbackMessage.showLong(getView(), message);
  }

  @Override public void openNewShotView() {
    newShotBarViewDelegate.openNewShotView();
  }

  @Override public void pickImage() {
    newShotBar.pickPrivateMessageOptions();
  }

  @Override public void showHolderOptions() {
    /* no-op */
  }

  @Override public void showPrivateMessageOptions() {
    /* no-op */
  }

  @Override public void openNewShotViewWithImage(File image) {
    newShotBar.openNewShotViewWithImage(image);
  }

  @Override public void openEditTopicDialog() {
    newShotBar.openEditTopicDialog();
  }

  @Override public void showDraftsButton() {
    newShotBar.showDraftsButton();
  }

  @Override public void hideDraftsButton() {
    newShotBar.hideDraftsButton();
  }
}
