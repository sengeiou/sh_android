package com.shootr.mobile.ui.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.daasuu.bl.BubbleLayout;
import com.mikepenz.actionitembadge.library.ActionItemBadge;
import com.shootr.mobile.R;
import com.shootr.mobile.data.prefs.CheckInShowcaseStatus;
import com.shootr.mobile.data.prefs.ShowcasePreference;
import com.shootr.mobile.data.prefs.ShowcaseStatus;
import com.shootr.mobile.domain.dagger.TemporaryFilesDir;
import com.shootr.mobile.domain.model.shot.HighlightedShot;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.utils.LocaleProvider;
import com.shootr.mobile.ui.ToolbarDecorator;
import com.shootr.mobile.ui.activities.NewStreamActivity;
import com.shootr.mobile.ui.activities.PhotoViewActivity;
import com.shootr.mobile.ui.activities.PollResultsActivity;
import com.shootr.mobile.ui.activities.PollVoteActivity;
import com.shootr.mobile.ui.activities.PostNewShotActivity;
import com.shootr.mobile.ui.activities.ProfileActivity;
import com.shootr.mobile.ui.activities.ShotDetailActivity;
import com.shootr.mobile.ui.activities.StreamDetailActivity;
import com.shootr.mobile.ui.adapters.ShotsTimelineAdapter;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnCtaClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnHideHighlightShot;
import com.shootr.mobile.ui.adapters.listeners.OnImageClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnImageLongClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnNiceShotListener;
import com.shootr.mobile.ui.adapters.listeners.OnOpenShotMenuListener;
import com.shootr.mobile.ui.adapters.listeners.OnReshootClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnShotLongClick;
import com.shootr.mobile.ui.adapters.listeners.OnUrlClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnUsernameClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnVideoClickListener;
import com.shootr.mobile.ui.adapters.listeners.ShotClickListener;
import com.shootr.mobile.ui.base.BaseFragment;
import com.shootr.mobile.ui.component.PhotoPickerController;
import com.shootr.mobile.ui.model.BaseMessageModel;
import com.shootr.mobile.ui.model.HighlightedShotModel;
import com.shootr.mobile.ui.model.PollModel;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.presenter.HighlightedShotPresenter;
import com.shootr.mobile.ui.presenter.NewShotBarPresenter;
import com.shootr.mobile.ui.presenter.PinShotPresenter;
import com.shootr.mobile.ui.presenter.ReportShotPresenter;
import com.shootr.mobile.ui.presenter.StreamPollIndicatorPresenter;
import com.shootr.mobile.ui.presenter.StreamTimelineOptionsPresenter;
import com.shootr.mobile.ui.presenter.StreamTimelinePresenter;
import com.shootr.mobile.ui.presenter.WatchNumberPresenter;
import com.shootr.mobile.ui.views.HighlightedShotsView;
import com.shootr.mobile.ui.views.NewShotBarView;
import com.shootr.mobile.ui.views.PinShotView;
import com.shootr.mobile.ui.views.ReportShotView;
import com.shootr.mobile.ui.views.StreamPollView;
import com.shootr.mobile.ui.views.StreamTimelineOptionsView;
import com.shootr.mobile.ui.views.StreamTimelineView;
import com.shootr.mobile.ui.views.WatchNumberView;
import com.shootr.mobile.ui.views.nullview.NullHighlightedShotsView;
import com.shootr.mobile.ui.views.nullview.NullNewShotBarView;
import com.shootr.mobile.ui.views.nullview.NullStreamTimelineOptionsView;
import com.shootr.mobile.ui.views.nullview.NullStreamTimelineView;
import com.shootr.mobile.ui.views.nullview.NullWatchNumberView;
import com.shootr.mobile.ui.widgets.ClickableTextView;
import com.shootr.mobile.ui.widgets.CustomActionItemBadge;
import com.shootr.mobile.ui.widgets.MessageBox;
import com.shootr.mobile.ui.widgets.PreCachingLayoutManager;
import com.shootr.mobile.util.AnalyticsTool;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.Clipboard;
import com.shootr.mobile.util.CrashReportTool;
import com.shootr.mobile.util.CustomContextMenu;
import com.shootr.mobile.util.FeedbackMessage;
import com.shootr.mobile.util.FormatNumberUtils;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.IntentFactory;
import com.shootr.mobile.util.Intents;
import com.shootr.mobile.util.MenuItemValueHolder;
import com.shootr.mobile.util.NumberFormatUtil;
import com.shootr.mobile.util.ShareManager;
import com.shootr.mobile.util.WritePermissionManager;
import de.hdodenhof.circleimageview.CircleImageView;
import java.io.File;
import java.util.List;
import java.util.Locale;
import javax.inject.Inject;
import timber.log.Timber;

public class StreamTimelineFragment extends BaseFragment
    implements StreamTimelineView, NewShotBarView, WatchNumberView, StreamTimelineOptionsView,
    ReportShotView, PinShotView, StreamPollView, HighlightedShotsView {

  public static final String EXTRA_STREAM_ID = "streamId";
  public static final String EXTRA_STREAM_TITLE = "streamTitle";
  public static final String EXTRA_ID_USER = "userId";
  public static final String TAG = "timeline";

  public static final String EXTRA_READ_WRITE_MODE = "readWriteMode";
  private static final int REQUEST_STREAM_DETAIL = 1;
  private static final String POLL_STATUS_SHOWING = "showing";
  private static final String POLL_STATUS_INVISIBLE = "invisible";
  private static final String POLL_STATUS_GONE = "gone";
  private static final int FOLLOWINGS = 0;
  private static final int PARTICIPANTS = 1;

  //region Fields
  @Inject StreamTimelinePresenter streamTimelinePresenter;
  @Inject NewShotBarPresenter newShotBarPresenter;
  @Inject WatchNumberPresenter watchNumberPresenter;
  @Inject StreamTimelineOptionsPresenter streamTimelineOptionsPresenter;
  @Inject ReportShotPresenter reportShotPresenter;
  @Inject PinShotPresenter pinShotPresenter;
  @Inject StreamPollIndicatorPresenter streamPollIndicatorPresenter;
  @Inject HighlightedShotPresenter highlightedShotPresenter;
  @Inject LocaleProvider localeProvider;
  @Inject IntentFactory intentFactory;

  @Inject ImageLoader imageLoader;
  @Inject AndroidTimeUtils timeUtils;
  @Inject ToolbarDecorator toolbarDecorator;
  @Inject ShareManager shareManager;
  @Inject FeedbackMessage feedbackMessage;
  @Inject @TemporaryFilesDir File tmpFiles;
  @Inject AnalyticsTool analyticsTool;
  @Inject WritePermissionManager writePermissionManager;
  @Inject CrashReportTool crashReportTool;
  @Inject NumberFormatUtil numberFormatUtil;
  @Inject SessionRepository sessionRepository;
  @Inject FormatNumberUtils formatNumberUtils;
  @Inject @CheckInShowcaseStatus ShowcasePreference checkInShowcasePreferences;

  @BindView(R.id.timeline_shot_list) RecyclerView shotsTimeline;
  @BindView(R.id.timeline_swipe_refresh) SwipeRefreshLayout swipeRefreshLayout;
  @BindView(R.id.timeline_new_shots_indicator_container) RelativeLayout timelineNewShotsIndicator;
  @BindView(R.id.timeline_indicator) RelativeLayout timelineIndicatorContainer;
  @BindView(R.id.timeline_empty) View emptyView;
  @BindView(R.id.timeline_checking_for_shots) TextView checkingForShotsView;
  @BindView(R.id.timeline_view_only_stream_indicator) View timelineViewOnlyStreamIndicator;
  @BindView(R.id.timeline_new_shot_bar) MessageBox newShotBarContainer;
  @BindView(R.id.timeline_message) ClickableTextView streamMessage;
  @BindView(R.id.timeline_poll_indicator) RelativeLayout timelinePollIndicator;
  @BindView(R.id.poll_question) TextView pollQuestion;
  @BindView(R.id.poll_action) TextView pollAction;
  @BindView(R.id.new_shots_notificator_container) RelativeLayout newShotsNotificatorContainer;
  @BindView(R.id.new_shots_notificator_text) TextView newShotsNotificatorText;
  @BindView(R.id.check_in_showcase) BubbleLayout checkInShowcase;
  @BindString(R.string.report_base_url) String reportBaseUrl;
  @BindString(R.string.added_to_favorites) String addToFavorites;
  @BindString(R.string.shot_shared_message) String shotShared;
  @BindString(R.string.stream_checked) String streamChecked;
  @BindString(R.string.analytics_screen_stream_timeline) String analyticsScreenStreamTimeline;
  @BindString(R.string.poll_vote) String pollVoteString;
  @BindString(R.string.poll_view) String pollViewString;
  @BindString(R.string.timeline_poll_results) String pollResultsString;
  @BindString(R.string.analytics_action_photo) String analyticsActionPhoto;
  @BindString(R.string.analytics_label_photo) String analyticsLabelPhoto;
  @BindString(R.string.analytics_action_nice) String analyticsActionNice;
  @BindString(R.string.analytics_label_nice) String analyticsLabelNice;
  @BindString(R.string.analytics_action_favorite_stream) String analyticsActionFavoriteStream;
  @BindString(R.string.analytics_label_favorite_stream) String analyticsLabelFavoriteStream;
  @BindString(R.string.analytics_action_filter_on_stream) String analyticsActionFilterOnStream;
  @BindString(R.string.analytics_label_filter_on_stream) String analyticsLabelFilterOnStream;
  @BindString(R.string.analytics_action_filter_off_stream) String analyticsActionFilterOffStream;
  @BindString(R.string.analytics_label_filter_off_stream) String analyticsLabelFilterOffStream;
  @BindString(R.string.analytics_action_share_shot) String analyticsActionShareShot;
  @BindString(R.string.analytics_label_share_shot) String analyticsLabelShareShot;
  @BindString(R.string.analytics_action_external_share) String analyticsActionExternalShare;
  @BindString(R.string.analytics_label_external_share) String analyticsLabelExternalShare;
  @BindString(R.string.analytics_source_timeline) String timelineSource;
  @BindString(R.string.analytics_label_open_link) String analyticsLabelOpenlink;
  @BindString(R.string.analytics_label_open_cta_link) String analyticsLabelOpenCtaLink;
  @BindString(R.string.analytics_action_open_link) String analyticsActionOpenLink;
  @BindString(R.string.analytics_action_open_cta_link) String analyticsActionOpenCtaLink;
  @BindString(R.string.analytics_label_open_pin_message_link) String
      analyticsLabelOpenPinMessagelink;
  @BindString(R.string.analytics_action_open_pin_message_link) String
      analyticsActionOpenPinMessageLink;
  @BindString(R.string.analytics_action_timeline) String analyticsTimelineAction;
  @BindString(R.string.analytics_label_timeline) String timelineLabelAnalytics;
  @BindString(R.string.analytics_action_timeline_scroll) String analyticsTimelineScrollAction;
  @BindString(R.string.analytics_label_timeline_scroll) String analyticsLabelTimelineScrollAction;
  @BindString(R.string.analytics_action_mute) String analyticsActionMute;
  @BindString(R.string.analytics_label_mute) String analyticsLabelMute;
  @BindString(R.string.analytics_action_shot) String analyticsActionSendShot;
  @BindString(R.string.analytics_label_shot) String analyticsLabelSendShot;

  private ShotsTimelineAdapter adapter;
  private PhotoPickerController photoPickerController;
  private Integer[] watchNumberCount;
  private View footerProgress;
  private MenuItemValueHolder showHoldingShotsMenuItem = new MenuItemValueHolder();
  private MenuItemValueHolder showAllShotsMenuItem = new MenuItemValueHolder();
  private MenuItemValueHolder addToFavoritesMenuItem = new MenuItemValueHolder();
  private MenuItemValueHolder removeFromFavoritesMenuItem = new MenuItemValueHolder();
  private MenuItemValueHolder muteMenuItem = new MenuItemValueHolder();
  private MenuItemValueHolder unmuteMenuItem = new MenuItemValueHolder();
  private int charCounterColorError;
  private int charCounterColorNormal;
  private EditText newTopicText;
  private TextView topicCharCounter;
  private PreCachingLayoutManager preCachingLayoutManager;
  private String pollIndicatorStatus;
  private AlertDialog shotImageDialog;
  private Unbinder unbinder;
  private boolean scrollMoved;
  private String idStream;
  private String streamTitle;
  private String streamAuthorIdUser;
  private Menu menu;
  //endregion

  public static StreamTimelineFragment newInstance(Bundle fragmentArguments) {
    StreamTimelineFragment fragment = new StreamTimelineFragment();
    fragment.setArguments(fragmentArguments);
    return fragment;
  }

  //region Lifecycle methods
  @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View fragmentView = inflater.inflate(R.layout.timeline_stream, container, false);
    unbinder = ButterKnife.bind(this, fragmentView);
    preCachingLayoutManager = new PreCachingLayoutManager(getContext());
    shotsTimeline.setLayoutManager(preCachingLayoutManager);
    shotsTimeline.setHasFixedSize(false);
    shotsTimeline.setOnTouchListener(new View.OnTouchListener() {
      @Override public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
          if (!scrollMoved) {
            sendTimelineScrollAnalytics();
            scrollMoved = true;
          }
        }
        return false;
      }
    });
    shotsTimeline.setItemAnimator(new DefaultItemAnimator() {
      @Override
      public boolean canReuseUpdatedViewHolder(@NonNull RecyclerView.ViewHolder viewHolder) {
        return true;
      }

      @Override
      public boolean canReuseUpdatedViewHolder(@NonNull RecyclerView.ViewHolder viewHolder,
          @NonNull List<Object> payloads) {
        return true;
      }
    });
    newShotBarContainer.setVisibility(View.GONE);
    return fragmentView;
  }

  @Override public void setupCheckInShowcase() {
    ShowcaseStatus checkInShowcaseStatus = checkInShowcasePreferences.get();
    if (checkInShowcaseStatus.shouldShowShowcase()) {
      checkInShowcase.setVisibility(View.VISIBLE);
      checkInShowcase.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          checkInShowcase.setVisibility(View.GONE);
          setShowcasePreference();
        }
      });
      checkInShowcaseStatus.setTimesViewed(checkInShowcaseStatus.getTimesViewed() + 1);
      checkInShowcasePreferences.set(checkInShowcaseStatus);
    } else {
      checkInShowcase.setVisibility(View.GONE);
    }
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
    streamTimelinePresenter.setView(new NullStreamTimelineView());
    newShotBarPresenter.setView(new NullNewShotBarView());
    watchNumberPresenter.setView(new NullWatchNumberView());
    streamTimelineOptionsPresenter.setView(new NullStreamTimelineOptionsView());
    highlightedShotPresenter.setView(new NullHighlightedShotsView());
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    initializeViews();
    setHasOptionsMenu(true);
    streamAuthorIdUser = getArguments().getString(EXTRA_ID_USER);
    idStream = getArguments().getString(EXTRA_STREAM_ID);
    setStreamTitle(getArguments().getString(EXTRA_STREAM_TITLE));
    Integer streamMode = getArguments().getInt(EXTRA_READ_WRITE_MODE, 0);
    setStreamTitleClickListener(idStream);
    setupPresentersInitialization(idStream, streamAuthorIdUser, streamMode);
  }

  @Override public void sendAnalythicsEnterTimeline() {
    sendTimelineAnalytics();
  }

  protected void setupPresentersInitialization(String idStream, String streamAuthorIdUser,
      Integer streamMode) {
    streamTimelinePresenter.setIsFirstLoad(true);
    streamTimelinePresenter.setIsFirstShotPosition(true);
    if (streamAuthorIdUser != null) {
      initializePresentersWithStreamAuthorId(idStream, streamAuthorIdUser, streamMode);
    } else {
      initializePresenters(idStream, null, streamMode);
    }
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
    this.menu = menu;
    if (isAdded()) {
      updateWatchNumberIcon();
    }
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.menu_showing_holding_shots:
        streamTimelinePresenter.onHoldingShotsClick();
        sendFilterOnAnalytics();
        return true;
      case R.id.menu_showing_all_shots:
        streamTimelinePresenter.onAllStreamShotsClick();
        hideFilterAlert();
        sendFilterOffAnalytics();
        return true;
      case R.id.menu_stream_add_favorite:
        streamTimelineOptionsPresenter.addToFavorites();
        sendFavoriteAnalytics();
        return true;
      case R.id.menu_stream_remove_favorite:
        streamTimelineOptionsPresenter.removeFromFavorites();
        return true;
      case R.id.menu_mute_stream:
        streamTimelineOptionsPresenter.mute();
        sendMuteAnalytics();
        return true;
      case R.id.menu_unmute_stream:
        streamTimelineOptionsPresenter.unmute();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  private void sendMuteAnalytics() {
    AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
    builder.setContext(getContext());
    builder.setActionId(analyticsActionMute);
    builder.setLabelId(analyticsLabelMute);
    builder.setSource(timelineSource);
    builder.setStreamName((streamTitle != null) ? streamTitle
        : sessionRepository.getCurrentUser().getWatchingStreamTitle());
    builder.setIdStream(idStream);
    builder.setUser(sessionRepository.getCurrentUser());
    analyticsTool.analyticsSendAction(builder);
  }

  private void sendTimelineAnalytics() {
    AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
    builder.setContext(getContext());
    builder.setActionId(analyticsTimelineAction);
    builder.setLabelId(timelineLabelAnalytics);
    builder.setUser(sessionRepository.getCurrentUser());
    builder.setIdStream(idStream);
    builder.setStreamName((streamTitle != null) ? streamTitle
        : sessionRepository.getCurrentUser().getWatchingStreamTitle());
    analyticsTool.analyticsSendAction(builder);
    analyticsTool.appsFlyerSendAction(builder);
  }

  private void sendTimelineScrollAnalytics() {
    AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
    builder.setContext(getContext());
    builder.setActionId(analyticsTimelineScrollAction);
    builder.setLabelId(analyticsLabelTimelineScrollAction);
    builder.setUser(sessionRepository.getCurrentUser());
    builder.setIdStream(idStream);
    builder.setStreamName((streamTitle != null) ? streamTitle
        : sessionRepository.getCurrentUser().getWatchingStreamTitle());
    analyticsTool.analyticsSendAction(builder);
  }

  private void sendFilterOnAnalytics() {
    AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
    builder.setContext(getContext());
    builder.setActionId(analyticsActionFilterOnStream);
    builder.setLabelId(analyticsLabelFilterOnStream);
    builder.setSource(timelineSource);
    builder.setIdStream(idStream);
    builder.setStreamName((streamTitle != null) ? streamTitle
        : sessionRepository.getCurrentUser().getWatchingStreamTitle());
    builder.setUser(sessionRepository.getCurrentUser());
    analyticsTool.analyticsSendAction(builder);
  }

  private void sendFilterOffAnalytics() {
    AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
    builder.setContext(getContext());
    builder.setActionId(analyticsActionFilterOffStream);
    builder.setLabelId(analyticsLabelFilterOffStream);
    builder.setSource(timelineSource);
    builder.setIdStream(idStream);
    builder.setStreamName((streamTitle != null) ? streamTitle
        : sessionRepository.getCurrentUser().getWatchingStreamTitle());
    builder.setUser(sessionRepository.getCurrentUser());
    analyticsTool.analyticsSendAction(builder);
  }

  private void sendFavoriteAnalytics() {
    AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
    builder.setContext(getContext());
    builder.setActionId(analyticsActionFavoriteStream);
    builder.setLabelId(analyticsLabelFavoriteStream);
    builder.setSource(timelineSource);
    builder.setIdStream(idStream);
    builder.setStreamName((streamTitle != null) ? streamTitle
        : sessionRepository.getCurrentUser().getWatchingStreamTitle());
    builder.setUser(sessionRepository.getCurrentUser());
    analyticsTool.analyticsSendAction(builder);
    analyticsTool.appsFlyerSendAction(builder);
  }

  @Override public void onResume() {
    super.onResume();
    streamTimelinePresenter.resume();
    newShotBarPresenter.resume();
    watchNumberPresenter.resume();
    streamTimelineOptionsPresenter.resume();
    streamPollIndicatorPresenter.resume();
    highlightedShotPresenter.resume();
  }

  @Override public void onPause() {
    super.onPause();
    streamTimelinePresenter.pause();
    newShotBarPresenter.pause();
    watchNumberPresenter.pause();
    streamTimelineOptionsPresenter.pause();
    streamPollIndicatorPresenter.pause();
    highlightedShotPresenter.pause();
  }

  private void initializePresentersWithStreamAuthorId(String idStream, String streamAuthorIdUser,
      Integer streamMode) {
    streamTimelinePresenter.initialize(this, idStream, streamAuthorIdUser, streamMode);
    pinShotPresenter.initialize(this);
    newShotBarPresenter.initializeWithIdStreamAuthor(this, idStream, streamAuthorIdUser, true);
    watchNumberPresenter.initialize(this, idStream);
    streamTimelineOptionsPresenter.initialize(this, idStream);
    reportShotPresenter.initializeWithIdStream(this, idStream);
    streamPollIndicatorPresenter.initialize(this, idStream, streamAuthorIdUser);
    highlightedShotPresenter.initialize(this, idStream);
  }

  private void initializePresenters(String idStream, String streamAuthorIdUser,
      Integer streamMode) {
    streamTimelinePresenter.initialize(this, idStream, streamMode);
    pinShotPresenter.initialize(this);
    newShotBarPresenter.initializeWithIdStreamAuthor(this, idStream, streamAuthorIdUser, true);
    watchNumberPresenter.initialize(this, idStream);
    streamTimelineOptionsPresenter.initialize(this, idStream);
    reportShotPresenter.initializeWithIdStream(this, idStream);
    streamPollIndicatorPresenter.initialize(this, idStream, streamAuthorIdUser);
    highlightedShotPresenter.initialize(this, idStream);
  }

  //endregion

  //region Views manipulation
  private void initializeViews() {
    charCounterColorError = getResources().getColor(R.color.error);
    charCounterColorNormal = getResources().getColor(R.color.gray_70);
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
            newShotBarPresenter.newShotImagePicked(imageFile);
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
            streamTimelinePresenter.onMenuCheckInClick();
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
        .inflate(R.layout.item_list_loading, shotsTimeline, false);
    footerProgress = ButterKnife.findById(footerView, R.id.loading_progress);
    footerProgress.setVisibility(View.GONE);
  }

  private void setupListAdapter() {
    adapter = new ShotsTimelineAdapter(imageLoader, //
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
          @Override public void markNice(ShotModel shot) {
            streamTimelinePresenter.markNiceShot(shot);
            sendNiceAnalytics(shot);
          }

          @Override public void unmarkNice(String idShot) {
            streamTimelinePresenter.unmarkNiceShot(idShot);
          }
        }, //
        new OnUsernameClickListener() {
          @Override public void onUsernameClick(String username) {
            openProfileFromUsername(username);
          }
        }, new ShotClickListener() {
      @Override public void onClick(ShotModel shot) {
        Intent intent = ShotDetailActivity.getIntentForActivityFromTimeline(getActivity(), shot);
        startActivity(intent);
      }
    }, new OnShotLongClick() {
      @Override public void onShotLongClick(ShotModel shot) {
        reportShotPresenter.onShotLongPressedWithStreamAuthor(shot, streamAuthorIdUser);
      }
    }, new OnOpenShotMenuListener() {
      @Override public void openMenu(ShotModel shot) {
        reportShotPresenter.onShotLongPressedWithStreamAuthor(shot, streamAuthorIdUser);
      }
    }, new OnImageLongClickListener() {
      @Override public void onImageLongClick(ShotModel shot) {
        setupImageDialog(shot);
      }
    }, new View.OnTouchListener() {

      @Override public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction()) {
          case MotionEvent.ACTION_UP:
          case MotionEvent.ACTION_CANCEL:
            if (shotImageDialog != null) {
              shotImageDialog.hide();
            }
          default:
            break;
        }
        return false;
      }
    }, new OnImageClickListener() {
      @Override public void onImageClick(View sharedImage, BaseMessageModel shot) {
        openImage(sharedImage, shot.getImage().getImageUrl());
      }
    }, new OnUrlClickListener() {
      @Override public void onClick() {
        highlightedShotPresenter.storeClickCount();
        sendOpenlinkAnalythics();
      }
    }, new OnUrlClickListener() {
      @Override public void onClick() {
        sendOpenlinkAnalythics();
      }
    }, new OnHideHighlightShot() {
      @Override public void onHideClick(HighlightedShotModel highlightedShotModel) {
        highlightedShotPresenter.onDismissHighlightShot(highlightedShotModel.getIdHighlightedShot(),
            streamAuthorIdUser);
      }
    }, new OnReshootClickListener() {
      @Override public void onReshootClick(ShotModel shot) {
        streamTimelinePresenter.shareShot(shot);
        sendReshootAnalytics(shot);
      }
    }, new OnCtaClickListener() {
      @Override public void onCtaClick(ShotModel shotModel) {
        streamTimelinePresenter.onCtaPressed(shotModel);
      }
    }, numberFormatUtil,
        highlightedShotPresenter.currentUserIsAdmin(getArguments().getString(EXTRA_ID_USER)));
    shotsTimeline.setAdapter(adapter);
  }

  private void sendOpenlinkAnalythics() {
    AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
    builder.setContext(getContext());
    builder.setActionId(analyticsActionOpenLink);
    builder.setLabelId(analyticsLabelOpenlink);
    builder.setSource(timelineSource);
    builder.setUser(sessionRepository.getCurrentUser());
    analyticsTool.analyticsSendAction(builder);
  }

  private void sendOpenCtaLinkAnalythics(ShotModel shotModel) {
    AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
    builder.setContext(getContext());
    builder.setActionId(analyticsActionOpenCtaLink);
    builder.setLabelId(analyticsLabelOpenCtaLink);
    builder.setSource(timelineSource);
    builder.setUser(sessionRepository.getCurrentUser());
    builder.setIdStream(shotModel.getStreamId());
    builder.setStreamName(shotModel.getStreamTitle());
    analyticsTool.analyticsSendAction(builder);
  }

  private void sendNiceAnalytics(ShotModel shot) {
    AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
    builder.setContext(getContext());
    builder.setActionId(analyticsActionNice);
    builder.setLabelId(analyticsLabelNice);
    builder.setSource(timelineSource);
    builder.setUser(sessionRepository.getCurrentUser());
    builder.setIdTargetUser(shot.getIdUser());
    builder.setTargetUsername(shot.getUsername());
    builder.setIdStream(shot.getStreamId());
    builder.setStreamName(shot.getStreamTitle());
    analyticsTool.analyticsSendAction(builder);
  }

  private void setupSwipeRefreshLayout() {
    swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override public void onRefresh() {
        streamTimelinePresenter.refresh();
      }
    });
    swipeRefreshLayout.setColorSchemeResources(R.color.refresh_1, R.color.refresh_2,
        R.color.refresh_3, R.color.refresh_4);
  }

  private void setupListScrollListeners() {
    shotsTimeline.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
      }

      @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if (shotsTimeline != null) {
          if (preCachingLayoutManager.findFirstVisibleItemPosition() == 0) {
            hideNewShotsIndicator();
          } else {
            streamTimelinePresenter.setIsFirstShotPosition(false);
          }
          checkIfEndOfListVisible();
        }
      }
    });
  }

  private void checkIfEndOfListVisible() {
    int lastItemPosition = shotsTimeline.getAdapter().getItemCount() - 1;
    int lastVisiblePosition = preCachingLayoutManager.findLastVisibleItemPosition();
    if (lastItemPosition == lastVisiblePosition && lastItemPosition >= 0) {
      streamTimelinePresenter.showingLastShot(adapter.getLastShot());
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
    sendImageAnalytics();
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

  private void shareShotIntent(ShotModel shotModel) {
    Intent shareIntent = shareManager.shareShotIntent(getActivity(), shotModel);
    Intents.maybeStartActivity(getActivity(), shareIntent);
  }

  private void copyShotCommentToClipboard(ShotModel shotModel) {
    Clipboard.copyShotComment(getActivity(), shotModel);
  }

  private void setStreamTitle(String streamTitle) {
    this.streamTitle = streamTitle;
    toolbarDecorator.setTitle(streamTitle);
  }

  private void setStreamTitleClickListener(final String idStream) {
    toolbarDecorator.setTitleClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        navigateToStreamDetail(idStream);
      }
    });
  }

  private void setupNewShotBarDelegate() {
    newShotBarContainer.init(getActivity(), photoPickerController, imageLoader, feedbackMessage,
        new MessageBox.OnActionsClick() {
          @Override public void onTopicClick() {
            setupTopicCustomDialog();
          }

          @Override public void onNewShotClick() {
            Intent newShotIntent = PostNewShotActivity.IntentBuilder //
                .from(getActivity()) //
                .setStreamData(idStream, streamTitle).build();
            getActivity().startActivity(newShotIntent);
          }

          @Override public void onShotWithImageClick(File image) {
            Intent newShotIntent = PostNewShotActivity.IntentBuilder //
                .from(getActivity()) //
                .withImage(image) //
                .setStreamData(idStream, streamTitle).build();
            getActivity().startActivity(newShotIntent);
          }

          @Override public void onAttachClick() {
            newShotBarPresenter.newShotFromImage();
          }

          @Override public void onSendClick() {
            sendShotToMixPanel();
          }

          @Override public void onCheckInClick() {
            //TODO
          }
        }, false, null);
  }

  private void sendShotToMixPanel() {
    AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
    builder.setContext(getContext());
    builder.setActionId(analyticsActionSendShot);
    builder.setLabelId(analyticsLabelSendShot);
    builder.setSource(timelineSource);
    builder.setUser(sessionRepository.getCurrentUser());
    if (streamTitle != null) {
      builder.setStreamName(streamTitle);
    }
    if (idStream != null) {
      builder.setIdStream(idStream);
    }
    analyticsTool.analyticsSendAction(builder);
    analyticsTool.appsFlyerSendAction(builder);
  }

  private void setupImageDialog(ShotModel shot) {
    sendImageAnalytics();
    LayoutInflater inflater = getActivity().getLayoutInflater();
    View dialogView = inflater.inflate(R.layout.dialog_shot_image, null);
    TextView user = (TextView) dialogView.findViewById(R.id.shot_user_name);
    ImageView image = (ImageView) dialogView.findViewById(R.id.shot_image);
    CircleImageView avatar = (CircleImageView) dialogView.findViewById(R.id.shot_avatar);

    user.setText(shot.getUsername());
    loadImages(shot, image, avatar);

    shotImageDialog = new AlertDialog.Builder(getActivity()).setView(dialogView).create();
    shotImageDialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
    shotImageDialog.show();
  }

  private void sendImageAnalytics() {
    AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
    builder.setContext(getContext());
    builder.setActionId(analyticsActionPhoto);
    builder.setLabelId(analyticsLabelPhoto);
    builder.setSource(timelineSource);
    builder.setUser(sessionRepository.getCurrentUser());
    builder.setIdStream(idStream);
    builder.setStreamName((streamTitle != null) ? streamTitle
        : sessionRepository.getCurrentUser().getWatchingStreamTitle());
    analyticsTool.analyticsSendAction(builder);
  }

  private void loadImages(ShotModel shot, ImageView image, CircleImageView avatar) {
    imageLoader.load(shot.getImage().getImageUrl(), image);
    imageLoader.loadProfilePhoto(shot.getAvatar(), avatar);
  }

  private void setupTopicCustomDialog() {
    LayoutInflater inflater = getActivity().getLayoutInflater();
    View dialogView = inflater.inflate(R.layout.dialog_edit_topic, null);
    setupNewTopicText(dialogView);
    createTopicDialog(dialogView);
  }

  private void setupNewTopicText(View dialogView) {
    newTopicText = (EditText) dialogView.findViewById(R.id.new_topic_text);
    topicCharCounter = (TextView) dialogView.findViewById(R.id.new_topic_char_counter);
    newTopicText.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                /* no - op */
      }

      @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (streamTimelinePresenter.isInitialized()) {
          streamTimelinePresenter.textChanged(charSequence.toString());
        }
      }

      @Override public void afterTextChanged(Editable editable) {
                /* no-op */
      }
    });
    if (streamTimelinePresenter.getStreamTopic() != null) {
      newTopicText.setText(streamTimelinePresenter.getStreamTopic());
    }
  }

  private void createTopicDialog(View dialogView) {
    new AlertDialog.Builder(getActivity()).setView(dialogView)
        .setTitle(getString(R.string.topic))
        .setPositiveButton(getString(R.string.done_pin_topic),
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int whichButton) {
                streamTimelinePresenter.editStream(newTopicText.getText().toString());
              }
            })
        .create()
        .show();
  }

  private void updateWatchNumberIcon() {
    if (watchNumberCount != null) {
      toolbarDecorator.setSubtitle(
          getContext().getString(R.string.stream_subtitle_pattern_multiple_participants,
              formatNumberUtils.formatNumbers(watchNumberCount[FOLLOWINGS].longValue()),
              formatNumberUtils.formatNumbers(watchNumberCount[PARTICIPANTS].longValue())));
    }
  }

  private void updateParticipants() {
    if (watchNumberCount != null) {
      toolbarDecorator.setSubtitle(getContext().getResources()
          .getQuantityString(R.plurals.total_watchers_pattern, watchNumberCount[1],
              formatNumberUtils.formatNumbers(watchNumberCount[1].longValue())));
    }
  }

  @OnClick(R.id.new_shots_notificator_text) public void goToTopOfTimeline() {
    shotsTimeline.scrollToPosition(0);
    streamTimelinePresenter.setNewShotsNumber(0);
    streamTimelinePresenter.setIsFirstShotPosition(true);
  }

  //region View methods
  @Override public void setShots(List<ShotModel> shots) {
    adapter.setShots(shots);
    adapter.notifyDataSetChanged();
  }

  @Override public void hideShots() {
    shotsTimeline.setVisibility(View.GONE);
  }

  @Override public void showShots() {
    shotsTimeline.setVisibility(View.VISIBLE);
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
    startActivityForResult(StreamDetailActivity.getIntent(getActivity(), idStream),
        REQUEST_STREAM_DETAIL);
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
    toolbarDecorator.putFilterSubtitle();
  }

  @Override public void showHoldingShots() {
    showHoldingShotsMenuItem.setVisible(true);
    toolbarDecorator.hideFilterSubtitle();
  }

  @Override public void hideAllStreamShots() {
    showAllShotsMenuItem.setVisible(false);
  }

  @Override public void setTitle(String title) {
    setStreamTitle(title);
  }

  @Override public void setImage(String avatarImage) {
    /*no-op*/
  }

  @Override public void showNewShotsIndicator(Integer numberNewShots) {
    try {
      newShotsNotificatorContainer.setVisibility(View.VISIBLE);
      String indicatorText =
          getResources().getQuantityString(R.plurals.new_shots_indicator, numberNewShots,
              numberNewShots);
      newShotsNotificatorText.setText(indicatorText);
    } catch (NullPointerException error) {
      crashReportTool.logException(error);
    }
  }

  @Override public void hideNewShotsIndicator() {
    try {
      newShotsNotificatorContainer.setVisibility(View.GONE);
      streamTimelinePresenter.setIsFirstShotPosition(true);
    } catch (NullPointerException error) {
      crashReportTool.logException(error);
    }
  }

  @Override public void showPinnedMessage(String topic) {
    if (timelineNewShotsIndicator != null) {
      timelineNewShotsIndicator.setVisibility(View.VISIBLE);
      timelineIndicatorContainer.setVisibility(View.VISIBLE);
      streamMessage.setVisibility(View.VISIBLE);
      streamMessage.setText(topic);
      streamMessage.addLinks(new OnUrlClickListener() {
        @Override public void onClick() {
          sendPinMessageOpenlinkAnalythics();
        }
      });
      streamMessage.setLinkTextColor(Color.WHITE);
    }
  }

  private void sendPinMessageOpenlinkAnalythics() {
    AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
    builder.setContext(getContext());
    builder.setActionId(analyticsActionOpenPinMessageLink);
    builder.setLabelId(analyticsLabelOpenPinMessagelink);
    builder.setSource(timelineSource);
    builder.setUser(sessionRepository.getCurrentUser());
    builder.setIdStream(idStream);
    builder.setStreamName((streamTitle != null) ? streamTitle
        : sessionRepository.getCurrentUser().getWatchingStreamTitle());
    analyticsTool.analyticsSendAction(builder);
  }

  @Override public void hidePinnedMessage() {
    try {
      if (streamMessage != null) {
        streamMessage.setVisibility(View.GONE);
        if (timelineNewShotsIndicator != null) {
          timelineNewShotsIndicator.setVisibility(View.GONE);
        }
        if (timelineIndicatorContainer != null) {
          timelineIndicatorContainer.setVisibility(View.GONE);
        }
      }
      if (pollIndicatorStatus != null && pollIndicatorStatus.equals(POLL_STATUS_SHOWING)) {
        if (timelineNewShotsIndicator != null) {
          timelineNewShotsIndicator.setVisibility(View.GONE);
        }
        timelinePollIndicator.setVisibility(View.VISIBLE);
        timelineIndicatorContainer.setVisibility(View.VISIBLE);
      }
    } catch (NullPointerException error) {
      /* no-op */
    }
  }

  @Override public void setRemainingCharactersCount(int remainingCharacters) {
    if (topicCharCounter != null) {
      topicCharCounter.setText(String.valueOf(remainingCharacters));
    }
  }

  @Override public void setRemainingCharactersColorValid() {
    if (topicCharCounter != null) {
      topicCharCounter.setTextColor(charCounterColorNormal);
    }
  }

  @Override public void setRemainingCharactersColorInvalid() {
    if (topicCharCounter != null) {
      topicCharCounter.setTextColor(charCounterColorError);
    }
  }

  @Override public void showPinMessageNotification(final String message) {
    new AlertDialog.Builder(getActivity()).setTitle(R.string.title_pin_message_notification)
        .setMessage(getString(R.string.pin_message_notification_confirmation_text))
        .setPositiveButton(getString(R.string.pin_message_notification_confirmation_yes),
            new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                streamTimelinePresenter.notifyMessage(message, true);
              }
            })
        .setNegativeButton(getString(R.string.pin_message_notification_confirmation_no),
            new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                streamTimelinePresenter.notifyMessage(message, false);
              }
            })
        .create()
        .show();
  }

  @Override public void addAbove(List<ShotModel> shotModels) {
    adapter.addShotsAbove(shotModels);
    adapter.notifyItemRangeInserted(0, shotModels.size());
  }

  @Override public void addShots(List<ShotModel> shotModels) {
    adapter.addShots(shotModels);
    streamTimelinePresenter.setIsFirstShotPosition(true);
    streamTimelinePresenter.setNewShotsNumber(0);
    shotsTimeline.smoothScrollToPosition(0);
  }

  @Override public void showChecked() {
    feedbackMessage.show(getView(), streamChecked);
  }

  @Override public void openCtaAction(String link) {
    String termsUrl = String.format(link, localeProvider.getLanguage());
    Intent termsIntent = intentFactory.openEmbededUrlIntent(getActivity(), termsUrl);
    Intents.maybeStartActivity(getActivity(), termsIntent);
  }

  @Override public void storeCtaClickLink(ShotModel shotModel) {
    highlightedShotPresenter.storeClickCount();
    sendOpenCtaLinkAnalythics(shotModel);
  }

  @Override public void showFilterAlert() {
    if (showHoldingShotsMenuItem != null) {
      CustomActionItemBadge.updateFilterAlert(getActivity(), showHoldingShotsMenuItem,
          showHoldingShotsMenuItem.getIcon(), " ");
    }
  }

  @Override public void renderNice(ShotModel shotModel) {
    adapter.markNice(shotModel);
  }

  @Override public void renderUnnice(String idShot) {
    adapter.unmarkNice(idShot);
  }

  private void hideFilterAlert() {
    if (showHoldingShotsMenuItem != null) {
      ActionItemBadge.update(getActivity(), showHoldingShotsMenuItem,
          showHoldingShotsMenuItem.getIcon(), ActionItemBadge.BadgeStyles.RED, null);
    }
  }

  @Override public void updateShotsInfo(List<ShotModel> shots) {
    adapter.setShots(shots);
    adapter.notifyDataSetChanged();
    highlightedShotPresenter.refreshHighlight();
  }

  @Override public void hideStreamViewOnlyIndicator() {
    timelineViewOnlyStreamIndicator.setVisibility(View.GONE);
    newShotBarContainer.setVisibility(View.VISIBLE);
  }

  @Override public void showStreamViewOnlyIndicator() {
    timelineViewOnlyStreamIndicator.setVisibility(View.VISIBLE);
    newShotBarContainer.setVisibility(View.INVISIBLE);
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
    getBaseContextMenuOptions(shotModel).addAction(R.string.report_context_menu_unblock,
        new Runnable() {
          @Override public void run() {
            reportShotPresenter.unblockUser(shotModel);
          }
        }).show();
  }

  @Override public void showBlockFollowingUserAlert() {
    feedbackMessage.showLong(getView(), R.string.block_user_error);
  }

  @Override public void showUserBlocked() {
    feedbackMessage.show(getView(), R.string.user_blocked);
  }

  @Override public void showUserUnblocked() {
    feedbackMessage.show(getView(), R.string.user_unblocked);
  }

  @Override public void showBlockUserConfirmation() {
    new AlertDialog.Builder(getActivity()).setMessage(R.string.block_user_dialog_message)
        .setPositiveButton(getString(R.string.block_user_dialog_block),
            new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                reportShotPresenter.confirmBlock();
              }
            })
        .setNegativeButton(getString(R.string.block_user_dialog_cancel), null)
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

  public void notifyPinnedShot(ShotModel shotModel) {
    adapter.onPinnedShot(shotModel);
  }

  public void showPinned() {
    feedbackMessage.show(getView(), R.string.shot_pinned);
  }

  @Override public void hidePinShotButton() {
        /* no-op */
  }

  @Override public void showPinShotButton() {
        /* no-op */
  }

  @Override public void openNewShotView() {
    newShotBarContainer.getNewShotBarViewDelegate().openNewShotView();
  }

  @Override public void pickImage() {
    setShowcasePreference();
    newShotBarContainer.pickImage();
  }

  private void setShowcasePreference() {
    if (checkInShowcasePreferences.get().shouldShowShowcase()) {
      ShowcaseStatus checkInShowcaseStatus = checkInShowcasePreferences.get();
      checkInShowcase.setVisibility(View.GONE);
      checkInShowcaseStatus.setShouldShowShowcase(false);
      checkInShowcasePreferences.set(checkInShowcaseStatus);
    }
  }

  @Override public void showHolderOptions() {
    newShotBarContainer.showHolderOptions();
  }

  @Override public void showPrivateMessageOptions() {
    /* no-op */
  }

  @Override public void openNewShotViewWithImage(File image) {
    newShotBarContainer.openNewShotViewWithImage(image);
  }

  @Override public void openEditTopicDialog() {
    newShotBarContainer.openEditTopicDialog();
  }

  @Override public void showDraftsButton() {
    newShotBarContainer.showDraftsButton();
  }

  @Override public void hideDraftsButton() {
    newShotBarContainer.hideDraftsButton();
  }

  @Override public void showWatchingPeopleCount(Integer[] peopleWatchingCount) {
    watchNumberCount = peopleWatchingCount;
    updateWatchNumberIcon();
  }

  @Override public void showParticipantsCount(Integer[] peopleWatchingCount) {
    watchNumberCount = peopleWatchingCount;
    updateParticipants();
  }

  @Override public void showAuthorContextMenuWithoutPin(final ShotModel shotModel) {
    getBaseContextMenuOptions(shotModel).addAction(R.string.menu_highlight_shot, new Runnable() {
      @Override public void run() {
        highlightedShotPresenter.highlightShot(shotModel.getIdShot());
      }
    }).addAction(R.string.report_context_menu_delete, new Runnable() {
      @Override public void run() {
        openDeleteConfirmation(shotModel);
      }
    }).show();
  }

  @Override public void showAuthorContextMenuWithPinAndHighlight(final ShotModel shotModel) {
    new CustomContextMenu.Builder(getActivity()).addAction(R.string.menu_share_shot_via_shootr,
        new Runnable() {
          @Override public void run() {
            streamTimelinePresenter.shareShot(shotModel);
            sendReshootAnalytics(shotModel);
          }
        }).addAction(R.string.menu_highlight_shot, new Runnable() {
      @Override public void run() {
        highlightedShotPresenter.highlightShot(shotModel.getIdShot());
      }
    }).addAction(R.string.menu_pin_shot, new Runnable() {
      @Override public void run() {
        pinShotPresenter.pinToProfile(shotModel);
      }
    }).addAction(R.string.menu_share_shot_via, new Runnable() {
      @Override public void run() {
        shareShotIntent(shotModel);
        sendShareExternalShotAnalytics(shotModel);
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

  private void sendShareExternalShotAnalytics(ShotModel shot) {
    AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
    builder.setContext(getContext());
    builder.setActionId(analyticsActionExternalShare);
    builder.setLabelId(analyticsLabelExternalShare);
    builder.setSource(timelineSource);
    builder.setUser(sessionRepository.getCurrentUser());
    analyticsTool.analyticsSendAction(builder);
  }

  private void sendReshootAnalytics(ShotModel shot) {
    AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
    builder.setContext(getContext());
    builder.setAction(getString(R.string.menu_share_shot_via_shootr));
    builder.setActionId(analyticsActionShareShot);
    builder.setLabelId(analyticsLabelShareShot);
    builder.setSource(timelineSource);
    builder.setUser(sessionRepository.getCurrentUser());
    builder.setIdTargetUser(shot.getIdUser());
    builder.setTargetUsername(shot.getUsername());
    analyticsTool.analyticsSendAction(builder);
  }

  @Override public void showAuthorContextMenuWithPinAndDismissHighlight(final ShotModel shotModel,
      final HighlightedShot highlightedShot) {
    new CustomContextMenu.Builder(getActivity()).addAction(R.string.menu_share_shot_via_shootr,
        new Runnable() {
          @Override public void run() {
            streamTimelinePresenter.shareShot(shotModel);
            sendReshootAnalytics(shotModel);
          }
        }).addAction(R.string.remove_highlight, new Runnable() {
      @Override public void run() {
        highlightedShotPresenter.onDismissHighlightShot(highlightedShot.getIdHighlightedShot(),
            streamAuthorIdUser);
      }
    }).addAction(R.string.menu_pin_shot, new Runnable() {
      @Override public void run() {
        pinShotPresenter.pinToProfile(shotModel);
      }
    }).addAction(R.string.menu_share_shot_via, new Runnable() {
      @Override public void run() {
        shareShotIntent(shotModel);
        sendShareExternalShotAnalytics(shotModel);
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

  @Override public void showAuthorContextMenuWithoutPinAndHighlight(final ShotModel shotModel) {
    getBaseContextMenuOptions(shotModel).addAction(R.string.menu_highlight_shot, new Runnable() {
      @Override public void run() {
        highlightedShotPresenter.highlightShot(shotModel.getIdShot());
      }
    }).addAction(R.string.report_context_menu_delete, new Runnable() {
      @Override public void run() {
        openDeleteConfirmation(shotModel);
      }
    }).show();
  }

  @Override
  public void showAuthorContextMenuWithoutPinAndDismissHighlight(final ShotModel shotModel) {
    getBaseContextMenuOptions(shotModel).addAction(R.string.remove_highlight, new Runnable() {
      @Override public void run() {
        highlightedShotPresenter.onMenuDismissHighlightShot();
      }
    }).addAction(R.string.report_context_menu_delete, new Runnable() {
      @Override public void run() {
        openDeleteConfirmation(shotModel);
      }
    }).show();
  }

  @Override public void showContributorContextMenuWithPinAndHighlight(final ShotModel shotModel) {
    new CustomContextMenu.Builder(getActivity()).addAction(R.string.menu_share_shot_via_shootr,
        new Runnable() {
          @Override public void run() {
            streamTimelinePresenter.shareShot(shotModel);
            sendReshootAnalytics(shotModel);
          }
        }).addAction(R.string.menu_highlight_shot, new Runnable() {
      @Override public void run() {
        highlightedShotPresenter.highlightShot(shotModel.getIdShot());
      }
    }).addAction(R.string.menu_pin_shot, new Runnable() {
      @Override public void run() {
        pinShotPresenter.pinToProfile(shotModel);
      }
    }).addAction(R.string.menu_share_shot_via, new Runnable() {
      @Override public void run() {
        shareShotIntent(shotModel);
        sendShareExternalShotAnalytics(shotModel);
      }
    }).addAction(R.string.menu_copy_text, new Runnable() {
      @Override public void run() {
        copyShotCommentToClipboard(shotModel);
      }
    }).show();
  }

  @Override
  public void showContributorContextMenuWithPinAndDismissHighlight(final ShotModel shotModel) {
    new CustomContextMenu.Builder(getActivity()).addAction(R.string.menu_share_shot_via_shootr,
        new Runnable() {
          @Override public void run() {
            streamTimelinePresenter.shareShot(shotModel);
            sendReshootAnalytics(shotModel);
          }
        }).addAction(R.string.remove_highlight, new Runnable() {
      @Override public void run() {
        highlightedShotPresenter.onMenuDismissHighlightShot();
      }
    }).addAction(R.string.menu_pin_shot, new Runnable() {
      @Override public void run() {
        pinShotPresenter.pinToProfile(shotModel);
      }
    }).addAction(R.string.menu_share_shot_via, new Runnable() {
      @Override public void run() {
        sendShareExternalShotAnalytics(shotModel);
      }
    }).addAction(R.string.menu_copy_text, new Runnable() {
      @Override public void run() {
        copyShotCommentToClipboard(shotModel);
      }
    }).show();
  }

  @Override
  public void showContributorContextMenuWithoutPinAndHighlight(final ShotModel shotModel) {
    new CustomContextMenu.Builder(getActivity()).addAction(R.string.menu_share_shot_via_shootr,
        new Runnable() {
          @Override public void run() {
            streamTimelinePresenter.shareShot(shotModel);
            sendReshootAnalytics(shotModel);
          }
        }).addAction(R.string.menu_highlight_shot, new Runnable() {
      @Override public void run() {
        highlightedShotPresenter.highlightShot(shotModel.getIdShot());
      }
    }).addAction(R.string.menu_share_shot_via, new Runnable() {
      @Override public void run() {
        shareShotIntent(shotModel);
        sendShareExternalShotAnalytics(shotModel);
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

  @Override
  public void showContributorContextMenuWithoutPinAndDismissHighlight(final ShotModel shotModel) {
    new CustomContextMenu.Builder(getActivity()).addAction(R.string.menu_share_shot_via_shootr,
        new Runnable() {
          @Override public void run() {
            streamTimelinePresenter.shareShot(shotModel);
            sendReshootAnalytics(shotModel);
          }
        }).addAction(R.string.remove_highlight, new Runnable() {
      @Override public void run() {
        highlightedShotPresenter.onMenuDismissHighlightShot();
      }
    }).addAction(R.string.menu_share_shot_via, new Runnable() {
      @Override public void run() {
        shareShotIntent(shotModel);
        sendShareExternalShotAnalytics(shotModel);
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

  @Override public void showContributorContextMenu(final ShotModel shot) {
    new CustomContextMenu.Builder(getActivity()).addAction(R.string.menu_highlight_shot,
        new Runnable() {
          @Override public void run() {
            highlightedShotPresenter.highlightShot(shot.getIdShot());
          }
        }).addAction(R.string.menu_share_shot_via_shootr, new Runnable() {
      @Override public void run() {
        streamTimelinePresenter.shareShot(shot);
        sendReshootAnalytics(shot);
      }
    }).addAction(R.string.menu_share_shot_via, new Runnable() {
      @Override public void run() {
        shareShotIntent(shot);
        sendShareExternalShotAnalytics(shot);
      }
    }).addAction(R.string.menu_copy_text, new Runnable() {
      @Override public void run() {
        copyShotCommentToClipboard(shot);
      }
    }).show();
  }

  @Override public void showContributorContextMenuWithDismissHighlight(final ShotModel shot) {
    new CustomContextMenu.Builder(getActivity()).addAction(R.string.menu_share_shot_via_shootr,
        new Runnable() {
          @Override public void run() {
            streamTimelinePresenter.shareShot(shot);
            sendReshootAnalytics(shot);
          }
        }).addAction(R.string.remove_highlight, new Runnable() {
      @Override public void run() {
        highlightedShotPresenter.onMenuDismissHighlightShot();
      }
    }).addAction(R.string.menu_share_shot_via, new Runnable() {
      @Override public void run() {
        shareShotIntent(shot);
        sendShareExternalShotAnalytics(shot);
      }
    }).addAction(R.string.menu_copy_text, new Runnable() {
      @Override public void run() {
        copyShotCommentToClipboard(shot);
      }
    }).show();
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

  @Override
  public void showAlertLanguageSupportDialog(final String sessionToken, final ShotModel shotModel) {
    new AlertDialog.Builder(getContext()).setMessage(getString(R.string.language_support_alert))
        .setPositiveButton(getString(R.string.email_confirmation_ok),
            new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                goToReport(sessionToken, shotModel);
              }
            })
        .show();
  }

  @Override public void showHolderContextMenu(final ShotModel shot) {
    new CustomContextMenu.Builder(getActivity()).addAction(R.string.menu_highlight_shot,
        new Runnable() {
          @Override public void run() {
            highlightedShotPresenter.highlightShot(shot.getIdShot());
          }
        }).addAction(R.string.menu_share_shot_via_shootr, new Runnable() {
      @Override public void run() {
        streamTimelinePresenter.shareShot(shot);
        sendReshootAnalytics(shot);
      }
    }).addAction(R.string.menu_share_shot_via, new Runnable() {
      @Override public void run() {
        shareShotIntent(shot);
        sendShareExternalShotAnalytics(shot);
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

  @Override public void showHolderContextMenuWithDismissHighlight(final ShotModel shot) {
    new CustomContextMenu.Builder(getActivity()).addAction(R.string.remove_highlight,
        new Runnable() {
          @Override public void run() {
            highlightedShotPresenter.onMenuDismissHighlightShot();
          }
        }).addAction(R.string.menu_share_shot_via_shootr, new Runnable() {
      @Override public void run() {
        streamTimelinePresenter.shareShot(shot);
        sendReshootAnalytics(shot);
      }
    }).addAction(R.string.menu_share_shot_via, new Runnable() {
      @Override public void run() {
        shareShotIntent(shot);
        sendShareExternalShotAnalytics(shot);
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

  @Override public void goToReport(String sessionToken, ShotModel shotModel) {
    Intent browserIntent = new Intent(Intent.ACTION_VIEW,
        Uri.parse(String.format(reportBaseUrl, sessionToken, shotModel.getIdShot())));
    startActivity(browserIntent);
  }

  @Override public void showEmailNotConfirmedError() {
    new AlertDialog.Builder(getActivity()).setMessage(
        getActivity().getString(R.string.alert_report_confirmed_email_message))
        .setTitle(getActivity().getString(R.string.alert_report_confirmed_email_title))
        .setPositiveButton(getActivity().getString(R.string.alert_report_confirmed_email_ok), null)
        .create()
        .show();
  }

  @Override public void showContextMenu(final ShotModel shotModel) {
    try {
      getBaseContextMenuOptions(shotModel).addAction(R.string.report_context_menu_report,
          new Runnable() {
            @Override public void run() {
              reportShotPresenter.report(shotModel);
            }
          }).addAction(R.string.report_context_menu_block, new Runnable() {
        @Override public void run() {
          reportShotPresenter.blockUserClicked(shotModel);
        }
      }).show();
    } catch (NullPointerException error) {
      crashReportTool.logException(error);
    }
  }

  @Override public void showAuthorContextMenuWithPin(final ShotModel shotModel) {
    new CustomContextMenu.Builder(getActivity()).addAction(R.string.menu_share_shot_via_shootr,
        new Runnable() {
          @Override public void run() {
            streamTimelinePresenter.shareShot(shotModel);
            sendReshootAnalytics(shotModel);
          }
        }).addAction(R.string.menu_pin_shot, new Runnable() {
      @Override public void run() {
        pinShotPresenter.pinToProfile(shotModel);
      }
    }).addAction(R.string.menu_share_shot_via, new Runnable() {
      @Override public void run() {
        shareShotIntent(shotModel);
        sendShareExternalShotAnalytics(shotModel);
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
    new AlertDialog.Builder(getActivity()).setMessage(R.string.delete_shot_confirmation_message)
        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
            reportShotPresenter.deleteShot(shotModel);
          }
        })
        .setNegativeButton(R.string.cancel, null)
        .create()
        .show();
  }

  private CustomContextMenu.Builder getBaseContextMenuOptions(final ShotModel shotModel) {
    return new CustomContextMenu.Builder(getActivity()).addAction(
        R.string.menu_share_shot_via_shootr, new Runnable() {
          @Override public void run() {
            streamTimelinePresenter.shareShot(shotModel);
            sendReshootAnalytics(shotModel);
          }
        }).addAction(R.string.menu_share_shot_via, new Runnable() {
      @Override public void run() {
        shareShotIntent(shotModel);
        sendShareExternalShotAnalytics(shotModel);
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
    streamTimelinePresenter.onShotDeleted(adapter.getItemCount());
  }

  @Override public void showPollIndicatorWithViewAction(final PollModel pollModel) {
    setupPollIndicator(pollModel);
    if (canSetPollAction()) {
      pollAction.setText(pollViewString.toUpperCase());
      timelinePollIndicator.setBackgroundColor(
          getContext().getResources().getColor(R.color.poll_view));
    }
  }

  @Override public void showPollIndicatorWithVoteAction(PollModel pollModel) {
    setupPollIndicator(pollModel);
    if (canSetPollAction()) {
      pollAction.setText(pollVoteString.toUpperCase());
      pollAction.setTypeface(null, Typeface.BOLD);
      timelinePollIndicator.setBackgroundColor(
          getContext().getResources().getColor(R.color.poll_vote));
    }
  }

  @Override public void showPollIndicatorWithResultsAction(PollModel pollModel) {
    setupPollIndicator(pollModel);
    if (canSetPollAction()) {
      pollAction.setText(pollResultsString.toUpperCase());
      timelinePollIndicator.setBackgroundColor(
          getContext().getResources().getColor(R.color.poll_results));
    }
  }

  private boolean canSetPollAction() {
    return pollAction != null;
  }

  private void setupPollIndicator(PollModel pollModel) {
    if (timelinePollIndicator != null && timelineNewShotsIndicator != null) {
      timelineNewShotsIndicator.setVisibility(View.GONE);
      pollIndicatorStatus = POLL_STATUS_SHOWING;
      timelineIndicatorContainer.setVisibility(View.VISIBLE);
      timelinePollIndicator.setVisibility(View.VISIBLE);
      pollQuestion.setText(pollModel.getQuestion());
      pollQuestion.post(new Runnable() {
        @Override public void run() {
          if (pollQuestion != null && pollQuestion.getLineCount() > 1) {
            pollQuestion.setTextSize(12);
          }
        }
      });
    }
  }

  @Override public void hidePollIndicator() {
    pollIndicatorStatus = POLL_STATUS_GONE;
    if (timelinePollIndicator != null) {
      timelinePollIndicator.setVisibility(View.GONE);
      timelineIndicatorContainer.setVisibility(View.GONE);
    }
    streamTimelinePresenter.onHidePoll();
  }

  @Override public void goToPollVote(String idStream, String streamAuthorIdUser) {
    Intent intent = PollVoteActivity.newIntent(getContext(), idStream, streamTitle);
    intent.putExtra(PollVoteActivity.EXTRA_ID_USER_OWNER, streamAuthorIdUser);
    startActivity(intent);
  }

  @Override public void goToPollResults(String idPoll, String idStream) {
    Intent intent =
        PollResultsActivity.newResultsIntent(getContext(), idPoll, streamTitle, idStream);
    startActivity(intent);
  }

  @Override public void goToPollLiveResults(String idPoll, String idStream) {
    Intent intent =
        PollResultsActivity.newLiveResultsIntent(getContext(), idPoll, streamTitle, idStream);
    startActivity(intent);
  }

  @OnClick(R.id.poll_action) public void onActionPressed() {
    streamPollIndicatorPresenter.onActionPressed();
  }

  @OnClick(R.id.timeline_new_shots_indicator_container) public void onShotBarPressed() {
    if (timelineNewShotsIndicator.getVisibility() != View.GONE) {
      newShotBarPresenter.editTopicPressed();
    }
  }

  @Override public void showHighlightedShot(HighlightedShotModel highlightedShot) {
    setHighlightShot(highlightedShot);
  }

  @Override public void hideHighlightedShots() {
    adapter.removeHighlightShot();
  }

  @Override public void refreshHighlightedShots(HighlightedShotModel highlightedShot) {
    setHighlightShot(highlightedShot);
    shotsTimeline.setAdapter(adapter);
  }

  @Override public void showDismissDialog(final String idHighlightShot) {
    new AlertDialog.Builder(getActivity()).setMessage(getString(R.string.highlight_shot_dialog))
        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
            highlightedShotPresenter.removeHighlightShot(idHighlightShot);
          }
        })
        .setNegativeButton(getString(R.string.cancel), null)
        .create()
        .show();
  }

  @Override public void updateHighlightShotInfo(HighlightedShotModel highlightedShotModel) {
    //adapter.setHighlightedShot(highlightedShotModel);
    adapter.updateHighligthShotInfo(highlightedShotModel);
  }

  @Override public void setHighlightShotBackground(Boolean isAdmin) {
    adapter.setHighlightShotBackground(isAdmin);
  }

  private void setHighlightShot(HighlightedShotModel highlightedShotModel) {
    adapter.setHighlightedShot(highlightedShotModel);
  }

  @Override public void onStart() {
    super.onStart();
    analyticsTool.analyticsStart(getContext(), analyticsScreenStreamTimeline);
  }
  //endregion
}
