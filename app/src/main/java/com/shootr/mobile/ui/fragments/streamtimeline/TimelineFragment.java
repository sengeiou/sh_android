package com.shootr.mobile.ui.fragments.streamtimeline;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.AutoTransition;
import android.transition.ChangeBounds;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.daasuu.bl.BubbleLayout;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.mikepenz.actionitembadge.library.ActionItemBadge;
import com.shootr.mobile.R;
import com.shootr.mobile.data.prefs.CheckInShowcaseStatus;
import com.shootr.mobile.data.prefs.ShowcasePreference;
import com.shootr.mobile.data.prefs.ShowcaseStatus;
import com.shootr.mobile.domain.dagger.TemporaryFilesDir;
import com.shootr.mobile.domain.model.TimelineType;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.utils.LocaleProvider;
import com.shootr.mobile.ui.FloatingVideoService;
import com.shootr.mobile.ui.ToolbarDecorator;
import com.shootr.mobile.ui.activities.HiddenPollResultsActivity;
import com.shootr.mobile.ui.activities.NewStreamActivity;
import com.shootr.mobile.ui.activities.PhotoViewActivity;
import com.shootr.mobile.ui.activities.PollOptionVotedActivity;
import com.shootr.mobile.ui.activities.PollResultsActivity;
import com.shootr.mobile.ui.activities.PollVoteActivity;
import com.shootr.mobile.ui.activities.PostNewShotActivity;
import com.shootr.mobile.ui.activities.PostPromotedShotActivity;
import com.shootr.mobile.ui.activities.PrivateMessageTimelineActivity;
import com.shootr.mobile.ui.activities.ProfileActivity;
import com.shootr.mobile.ui.activities.PromotedShotIntroActivity;
import com.shootr.mobile.ui.activities.ShotDetailActivity;
import com.shootr.mobile.ui.activities.StreamDetailActivity;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnCtaClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnDeleteHighlightedItemClick;
import com.shootr.mobile.ui.adapters.listeners.OnImageClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnImageLongClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnItemsInserted;
import com.shootr.mobile.ui.adapters.listeners.OnNiceShotListener;
import com.shootr.mobile.ui.adapters.listeners.OnOpenShotMenuListener;
import com.shootr.mobile.ui.adapters.listeners.OnPollActionClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnReshootClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnShotLongClick;
import com.shootr.mobile.ui.adapters.listeners.OnUrlClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnUsernameClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnVideoClickListener;
import com.shootr.mobile.ui.adapters.listeners.PromotedItemClickListener;
import com.shootr.mobile.ui.adapters.listeners.ShotClickListener;
import com.shootr.mobile.ui.adapters.streamtimeline.PromotedItemsAdapter;
import com.shootr.mobile.ui.adapters.streamtimeline.StreamTimelineAdapter;
import com.shootr.mobile.ui.base.BaseFragment;
import com.shootr.mobile.ui.component.PhotoPickerController;
import com.shootr.mobile.ui.fragments.BottomYoutubeVideoPlayer;
import com.shootr.mobile.ui.model.BaseMessageModel;
import com.shootr.mobile.ui.model.ExternalVideoModel;
import com.shootr.mobile.ui.model.PollModel;
import com.shootr.mobile.ui.model.PrintableModel;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.model.StreamModel;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.presenter.LongPressShotPresenter;
import com.shootr.mobile.ui.presenter.NewShotBarPresenter;
import com.shootr.mobile.ui.presenter.StreamTimelineOptionsPresenter;
import com.shootr.mobile.ui.presenter.streamtimeline.StreamTimelinePresenter;
import com.shootr.mobile.ui.views.NewShotBarView;
import com.shootr.mobile.ui.views.StreamTimelineOptionsView;
import com.shootr.mobile.ui.views.streamtimeline.FixedItemView;
import com.shootr.mobile.ui.views.streamtimeline.LongPressView;
import com.shootr.mobile.ui.views.streamtimeline.StreamTimelineView;
import com.shootr.mobile.ui.widgets.AvatarView;
import com.shootr.mobile.ui.widgets.CustomActionItemBadge;
import com.shootr.mobile.ui.widgets.PreCachingLayoutManager;
import com.shootr.mobile.ui.widgets.PromotedMessageBox;
import com.shootr.mobile.ui.widgets.PromotedShotActivationInfoDialog;
import com.shootr.mobile.util.AnalyticsTool;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.Clipboard;
import com.shootr.mobile.util.CrashReportTool;
import com.shootr.mobile.util.CustomContextMenu;
import com.shootr.mobile.util.ExternalVideoUtils;
import com.shootr.mobile.util.FeedbackMessage;
import com.shootr.mobile.util.FormatNumberUtils;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.IntentFactory;
import com.shootr.mobile.util.Intents;
import com.shootr.mobile.util.MenuItemValueHolder;
import com.shootr.mobile.util.NumberFormatUtil;
import com.shootr.mobile.util.ShareManager;
import com.shootr.mobile.util.ShotTextSpannableBuilder;
import com.shootr.mobile.util.WritePermissionManager;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import javax.inject.Inject;
import timber.log.Timber;

import static android.app.Activity.RESULT_OK;

public class TimelineFragment extends BaseFragment
    implements StreamTimelineView, NewShotBarView, FixedItemView, LongPressView,
    StreamTimelineOptionsView, YouTubePlayer.PlaybackEventListener,
    YouTubePlayer.OnFullscreenListener, YouTubePlayer.OnInitializedListener,
    YouTubePlayer.PlayerStateChangeListener {

  public static final String EXTRA_STREAM_ID = "streamId";
  public static final String EXTRA_STREAM_TITLE = "streamTitle";
  private static final int DRAW_OVER_OTHER_APP_PERMISSION_REQUEST_CODE = 1222;

  private static final String API = "AIzaSyAamKWr6yMmLmhSsLvWA1cKOBYXPytC6_I";

  private static final int FOLLOWERS = 0;
  private static final int CONNECTED = 1;
  private static final int REQUEST_STREAM_DETAIL = 1;

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
  @Inject LocaleProvider localeProvider;
  @Inject IntentFactory intentFactory;
  @Inject ExternalVideoUtils externalVideoUtils;

  @Inject StreamTimelinePresenter timelinePresenter;
  @Inject StreamTimelineOptionsPresenter streamTimelineOptionsPresenter;
  @Inject NewShotBarPresenter newShotBarPresenter;
  @Inject LongPressShotPresenter longPressShotPresenter;

  @BindView(R.id.item_recycler) RecyclerView itemsList;
  @BindView(R.id.promoted_recycler) RecyclerView promotedList;
  @BindView(R.id.timeline_new_shot_bar) PromotedMessageBox newShotBarContainer;
  @BindView(R.id.timeline_view_only_stream_indicator) View timelineViewOnlyStreamIndicator;
  @BindView(R.id.timeline_empty) TextView emptyView;
  @BindView(R.id.timeline_checking_for_shots) TextView checkingForShotsView;
  @BindView(R.id.new_shots_notificator_text) TextView newShotsNotificatorText;
  @BindView(R.id.filter_showcase) BubbleLayout filterShowcase;
  @BindView(R.id.player) FrameLayout player;
  @BindView(R.id.container) ConstraintLayout container;
  //@BindView(R.id.timeline_swipe_refresh) SwipeRefreshLayout swipeRefreshLayout;
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
  @BindString(R.string.analytics_action_checkin) String analyticsActionCheckin;
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
  @BindString(R.string.analytics_source_checkin_showcase_timeline) String checkinShowCaseTimeline;
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
  @BindString(R.string.shot_timeline_empty_title) String emptyTimeline;
  @BindString(R.string.no_filter_shots) String emptyFilter;
  @BindString(R.string.top_indicator) String topIndicator;
  @BindString(R.string.filtered_by_important) String filteredByImportant;
  @BindString(R.string.filtered_by_nicest) String filteredByNicest;
  @BindString(R.string.nicest_timeline_empty_title) String empty_nicest_timeline;
  @BindString(R.string.shot_timeline_empty_title) String empty_shot_timeline;

  private MenuItemValueHolder addToFavoritesMenuItem = new MenuItemValueHolder();
  private MenuItemValueHolder removeFromFavoritesMenuItem = new MenuItemValueHolder();
  private MenuItemValueHolder muteMenuItem = new MenuItemValueHolder();
  private MenuItemValueHolder unmuteMenuItem = new MenuItemValueHolder();
  private MenuItemValueHolder showVideoMenuItem = new MenuItemValueHolder();
  private MenuItemValueHolder hideVideoMenuItem = new MenuItemValueHolder();
  private MenuItemValueHolder filterMenuItem = new MenuItemValueHolder();

  private PhotoPickerController photoPickerController;
  private PreCachingLayoutManager preCachingLayoutManager;
  private Unbinder unbinder;
  private String idStream;
  private String streamTitle;
  private StreamTimelineAdapter adapter;
  private PromotedItemsAdapter promotedAdapter;
  private AlertDialog shotImageDialog;

  private EditText newTopicText;
  private TextView topicCharCounter;

  private int charCounterColorError;
  private int charCounterColorNormal;
  private Integer[] watchNumberCount;
  private boolean isFullScreen;
  private YouTubePlayer videoPlayer;
  private boolean videoHasBeenPlayed;
  private YouTubePlayerSupportFragment youTubePlayerSupportFragment;
  private boolean videoAnimationPlaying = false;
  private boolean shouldLoadVideAfterResume = false;
  private ExternalVideoModel currentVideoModel;

  public static TimelineFragment newInstance(Bundle fragmentArguments) {
    TimelineFragment fragment = new TimelineFragment();
    fragment.setArguments(fragmentArguments);
    return fragment;
  }

  @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View fragmentView = inflater.inflate(R.layout.stream_timeline, container, false);
    unbinder = ButterKnife.bind(this, fragmentView);
    preCachingLayoutManager = new PreCachingLayoutManager(getContext());
    preCachingLayoutManager.setInitialPrefetchItemCount(10);
    itemsList.setLayoutManager(preCachingLayoutManager);
    itemsList.setHasFixedSize(false);
    itemsList.setItemAnimator(new DefaultItemAnimator() {
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

    LinearLayoutManager promotedLinearLayout =
        new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
    promotedList.setLayoutManager(promotedLinearLayout);
    promotedList.setHasFixedSize(false);

    return fragmentView;
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    setHasOptionsMenu(true);
    idStream = getArguments().getString(EXTRA_STREAM_ID);
    sessionRepository.resetFilter(idStream);
    sessionRepository.resetMultipleFilter(idStream);
    initializePresenters();
    initializeViews();
  }

  private void initializePresenters() {
    timelinePresenter.initialize(this, this, idStream);
    newShotBarPresenter.initialize(this, idStream, true);
    longPressShotPresenter.initialize(this, idStream);
    streamTimelineOptionsPresenter.initialize(this, idStream);
  }

  private void initializeViews() {
    charCounterColorError = getResources().getColor(R.color.error);
    charCounterColorNormal = getResources().getColor(R.color.gray_70);
    writePermissionManager.init(getActivity());
    player.setVisibility(View.GONE);
    setupPromotedList();
    setupListAdapter();
    //setupSwipeRefreshLayout();
    setupListScrollListeners();
    setupPhotoPicker();
    setupNewShotBarDelegate();
  }

  private void setupPromotedList() {
    promotedAdapter = new PromotedItemsAdapter(new PromotedItemClickListener() {
      @Override public void markSeen(String type, String idItem) {
        timelinePresenter.markSeen(type, idItem);
      }

      @Override public void onPollClick(PollModel pollModel) {
        timelinePresenter.onPollActionClick(pollModel);
      }

      @Override public void onHighlightedClick(ShotModel shotModel) {
        goToShotDetail(shotModel);
      }

      @Override public void onPromotedShotClick(ShotModel shotModel) {
        goToShotDetail(shotModel);
      }

      @Override public void onAddPromotedPressed() {
        handlePromotedShotIntro();
      }

      @Override public void onUserFollowingClick(UserModel user) {
        goToChannelTimeline(user.getIdUser());
      }
    }, new OnShotLongClick() {
      @Override public void onShotLongClick(ShotModel shot) {
        longPressShotPresenter.onLongClickPressed(shot);
      }
    }, imageLoader);
    promotedList.setAdapter(promotedAdapter);
  }

  private void goToNewPromotedShot() {
    Intent newShotIntent = PostPromotedShotActivity.IntentBuilder //
        .from(getActivity()) //
        .setStreamData(idStream, streamTitle).build();
    getActivity().startActivity(newShotIntent);
  }

  private void showIntroSS() {
    Intent promotedShotIntroIntent =
        PromotedShotIntroActivity.getIntentForActivity(getActivity(), idStream, streamTitle);
    startActivity(promotedShotIntroIntent);
  }

  private void setupListScrollListeners() {
    itemsList.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
      }

      @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if (itemsList != null) {

          if (preCachingLayoutManager.findFirstVisibleItemPosition() > 3) {
            showNewShotsIndicator();
          }

          timelinePresenter.setIsFirstShotPosition(
              preCachingLayoutManager.findFirstCompletelyVisibleItemPosition() == 0);
          checkIfEndOfListVisible();
        }
      }
    });
  }

  @OnClick(R.id.new_shots_notificator_text) public void goToTopOfTimeline() {
    newShotsNotificatorText.setText(topIndicator);
    itemsList.scrollToPosition(0);
  }

  private void checkIfEndOfListVisible() {
    int lastItemPosition = itemsList.getAdapter().getItemCount() - 1;
    int lastVisiblePosition = preCachingLayoutManager.findLastVisibleItemPosition();
    if (lastItemPosition == lastVisiblePosition && lastItemPosition >= 0) {
      timelinePresenter.showingLastShot();
    }
  }

  private void setupListAdapter() {
    adapter = new StreamTimelineAdapter(imageLoader, //
        //
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
            timelinePresenter.markNiceShot(shot);
            sendNiceAnalytics(shot);
          }

          @Override public void unmarkNice(String idShot) {
            timelinePresenter.unmarkNiceShot(idShot);
          }
        }, //
        new OnUsernameClickListener() {
          @Override public void onUsernameClick(String username) {
            openProfileFromUsername(username);
          }
        }, timeUtils, new ShotTextSpannableBuilder(), new ShotClickListener() {
      @Override public void onClick(ShotModel shot) {
        goToShotDetail(shot);
      }
    }, new OnShotLongClick() {
      @Override public void onShotLongClick(ShotModel shot) {
        longPressShotPresenter.onLongClickPressed(shot);
      }
    }, new OnOpenShotMenuListener() {
      @Override public void openMenu(ShotModel shot) {
        longPressShotPresenter.onLongClickPressed(shot);
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
    }, new OnDeleteHighlightedItemClick() {
      @Override public void onHideClick(PrintableModel printableModel) {
        timelinePresenter.deleteHighlightedItem(printableModel);
      }
    }, new OnUrlClickListener() {
      @Override public void onClick() {
        sendOpenlinkAnalythics();
      }
    }, new OnUrlClickListener() {
      @Override public void onClick() {
        timelinePresenter.storeClickCount();
        sendOpenlinkAnalythics();
      }
    }, new OnReshootClickListener() {
      @Override public void onReshootClick(ShotModel shot) {
        timelinePresenter.reshoot(shot);
        sendReshootAnalytics(shot);
      }

      @Override public void onUndoReshootClick(ShotModel shot) {
        timelinePresenter.undoReshoot(shot);
        sendReshootAnalytics(shot);
      }
    }, new OnCtaClickListener() {
      @Override public void onCtaClick(ShotModel shotModel) {
        timelinePresenter.onCtaPressed(shotModel);
        sendCheckinAnalythics();
      }
    }, new OnItemsInserted() {
      @Override public void onInserted(int itemsInsertedCount) {
        timelinePresenter.onItemsInserted(itemsInsertedCount);
      }
    }, numberFormatUtil, new View.OnClickListener() {
      @Override public void onClick(View view) {
        if (timelinePresenter.canFixItem()) {
          openEditTopicDialog();
        }
      }
    }, new OnPollActionClickListener() {
      @Override public void onPollClick(PollModel pollModel) {
        timelinePresenter.onPollActionClick(pollModel);
      }
    }, timelinePresenter.canFixItem());

    itemsList.setAdapter(adapter);
  }

  private void goToShotDetail(ShotModel shot) {
    Intent intent = ShotDetailActivity.getIntentForActivityFromTimeline(getActivity(), shot,
        sessionRepository.isNewShotDetail());
    startActivity(intent);
  }

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

    String videoId = externalVideoUtils.getVideoId(url);

    if (videoId != null) {
      openExternalVideoInApp(videoId);
    } else {
      Uri uri = Uri.parse(url);
      Intent intent = new Intent(Intent.ACTION_VIEW, uri);
      startActivity(intent);
    }
  }

  private void openExternalVideoInApp(String videoId) {
    if (videoPlayer != null) {
      shouldLoadVideAfterResume = true;
      changePlayerVisibility(false);
      videoPlayer.release();
    }
    timelinePresenter.onInAppVideoStarted();

    BottomYoutubeVideoPlayer bottomYoutubeVideoPlayer = new BottomYoutubeVideoPlayer();
    bottomYoutubeVideoPlayer.setVideoId(videoId);
    bottomYoutubeVideoPlayer.setVideoPlayerCallback(
        new BottomYoutubeVideoPlayer.VideoPlayerCallback() {
          @Override public void onDismiss() {
            if (shouldLoadVideAfterResume) {
              shouldLoadVideAfterResume = false;
              renderExternalVideo(currentVideoModel);
              hideVideoMenuItem.setVisible(false);
            }
          }
        });

    bottomYoutubeVideoPlayer.show(getActivity().getSupportFragmentManager(),
        bottomYoutubeVideoPlayer.getTag());
  }

  private void shareShotIntent(ShotModel shotModel) {
    Intent shareIntent = shareManager.shareShotIntent(getActivity(), shotModel);
    Intents.maybeStartActivity(getActivity(), shareIntent);
  }

  private void copyShotCommentToClipboard(ShotModel shotModel) {
    Clipboard.copyShotComment(getActivity(), shotModel);
  }

  private void setupImageDialog(ShotModel shot) {
    sendImageAnalytics();
    LayoutInflater inflater = getActivity().getLayoutInflater();
    View dialogView = inflater.inflate(R.layout.dialog_shot_image, null);
    TextView user = (TextView) dialogView.findViewById(R.id.shot_user_name);
    ImageView image = (ImageView) dialogView.findViewById(R.id.shot_image);
    AvatarView avatar = (AvatarView) dialogView.findViewById(R.id.shot_avatar);

    user.setText(shot.getUsername());
    loadImages(shot, image, avatar);

    shotImageDialog = new AlertDialog.Builder(getActivity()).setView(dialogView).create();
    shotImageDialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
    shotImageDialog.show();
  }

  private void loadImages(ShotModel shot, ImageView image, AvatarView avatar) {
    imageLoader.load(shot.getImage().getImageUrl(), image);
    imageLoader.loadProfilePhoto(shot.getAvatar(), avatar, shot.getUsername());
  }

  //region newShotBar
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
            timelinePresenter.onMenuCheckInClick();
            sendCheckinAnalythics();
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

  private void setupNewShotBarDelegate() {
    newShotBarContainer.init(getActivity(), photoPickerController, imageLoader, feedbackMessage,
        new PromotedMessageBox.OnActionsClick() {
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

          @Override public void onPromotedClick() {
            handlePromotedShotIntro();
          }

          @Override public void onPromotedShowInfoClick() {
            timelinePresenter.onPromotedActivationButtonClick();
          }
        }, false, null, false, null, idStream);
  }

  private void handlePromotedShotIntro() {
    if (!sessionRepository.hasShownIntroPromotedShot()) {
      showIntroSS();
      sessionRepository.setShowIntroPromotedShot(true);
    } else {
      goToNewPromotedShot();
    }
  }

  @Override public void openNewShotView() {
    newShotBarContainer.getNewShotBarViewDelegate().openNewShotView();
  }

  @Override public void pickImage() {
    newShotBarContainer.pickImage();
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
    if (newShotBarContainer != null) {
      newShotBarContainer.showDraftsButton();
    }
  }

  @Override public void hideDraftsButton() {
    if (isAdded()) {
      newShotBarContainer.hideDraftsButton();
    }
  }

  //endregion

  //region streamMessage
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
        timelinePresenter.textChanged(charSequence.toString());
      }

      @Override public void afterTextChanged(Editable editable) {
        /* no-op */
      }
    });
    if (timelinePresenter.getCurrentTopicText() != null) {
      newTopicText.setText(timelinePresenter.getCurrentTopicText());
    }
  }

  private void createTopicDialog(View dialogView) {
    new AlertDialog.Builder(getActivity()).setView(dialogView)
        .setTitle(getString(R.string.topic))
        .setPositiveButton(getString(R.string.done_pin_topic),
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int whichButton) {
                timelinePresenter.editStreamMessage(newTopicText.getText().toString());
              }
            })
        .create()
        .show();
  }

  @Override public void showPinMessageNotification(final String message) {
    new AlertDialog.Builder(getActivity()).setTitle(R.string.title_pin_message_notification)
        .setMessage(getString(R.string.pin_message_notification_confirmation_text))
        .setPositiveButton(getString(R.string.pin_message_notification_confirmation_yes),
            new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                timelinePresenter.notifyMessage(message, true);
              }
            })
        .setNegativeButton(getString(R.string.pin_message_notification_confirmation_no),
            new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                timelinePresenter.notifyMessage(message, false);
              }
            })
        .create()
        .show();
  }
  //endregion

  @Override public void renderItems(List<PrintableModel> items, PrintableModel itemForReposition,
      int offset) {
    if (isAdded()) {
      checkingForShotsView.setVisibility(View.GONE);
      itemsList.scrollToPosition(0);
      if (timelinePresenter.getFilterType().equals(TimelineType.NICEST)) {
        adapter.setNicestShotList(items);
      } else {
        adapter.setShotList(items);
      }
      if (itemForReposition != null && adapter.indexOf(itemForReposition) != -1) {
        preCachingLayoutManager.scrollToPositionWithOffset(adapter.indexOf(itemForReposition),
            offset);
      } else {
        itemsList.scrollToPosition(0);
      }
    }
  }

  @Override public void renderExternalVideo(ExternalVideoModel externalVideoModel) {
    createFloatingWidget();
    showVideoVisibilityMenu();
    currentVideoModel = externalVideoModel;
    changeStatusBarColor();
    player.setVisibility(View.VISIBLE);
    if (youTubePlayerSupportFragment == null) {
      youTubePlayerSupportFragment = YouTubePlayerSupportFragment.newInstance();
      FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
      transaction.add(R.id.player, youTubePlayerSupportFragment).commit();
      youTubePlayerSupportFragment.initialize(API, this);
    } else {
      if (player != null) {
        try {
          if (videoPlayer != null) {
            videoPlayer.cueVideo(currentVideoModel.getVideoId());
          }
        } catch (IllegalStateException error) {
          youTubePlayerSupportFragment.initialize(API, this);
        }
      }
    }
  }

  @Override public void renderPolls(List<PrintableModel> items) {
    if (isAdded()) {
      checkingForShotsView.setVisibility(View.GONE);
      promotedAdapter.setPolls(items);
    }
  }

  @Override public void renderHighlightedItems(List<PrintableModel> items) {
    promotedAdapter.setHighlightedList(items);
  }

  @Override public void addNewItems(List<PrintableModel> items) {
    if (isAdded()) {
      emptyView.setVisibility(View.GONE);
      adapter.notifyItemRangeChanged(preCachingLayoutManager.findFirstVisibleItemPosition(),
          preCachingLayoutManager.findLastVisibleItemPosition());
      adapter.addNewItems(items);
    }
  }

  @Override public void addOldItems(List<PrintableModel> oldItems) {
    adapter.addOldItems(oldItems);
  }

  @Override public void updateItem(PrintableModel updatedItem) {
    adapter.updateItem(updatedItem);
  }

  @Override public void updateNicestItem(PrintableModel updatedItem) {
    adapter.updateNicestItem(updatedItem);
  }

  @Override public void addMyItem(PrintableModel shotModel) {
    adapter.addNewItem(shotModel);
  }

  @Override public void smoothToTop() {
    if (isAdded()) {
      itemsList.smoothScrollToPosition(0);
    }
  }

  @Override public void clearTimeline() {
    adapter.clearItems();
  }

  @Override public void removeHighlightedItem() {
    adapter.removeHighlightedItems();
  }

  @Override public void goToTop() {
    if (isAdded()) {
      itemsList.scrollToPosition(0);
    }
  }

  @Override public void refreshShotsInfo() {
    /* no-op */
  }

  @Override public void showGenericItemsMenuItem() {
    toolbarDecorator.hideFilterSubtitle();
  }

  @Override public void showImportantItemsMenuItem() {
    toolbarDecorator.putFilterSubtitle(filteredByImportant);
  }

  @Override public void showNicestItemsMenuItem() {
    toolbarDecorator.putFilterSubtitle(filteredByNicest);
  }

  @Override public void hidePinnedMessage() {

  }

  @Override public void showViewOnlyTextBox() {
    timelineViewOnlyStreamIndicator.setVisibility(View.VISIBLE);
    newShotBarContainer.hideMessageBox();
  }

  @Override public void showNewShotTextBox() {
    if (timelineViewOnlyStreamIndicator != null) {
      timelineViewOnlyStreamIndicator.setVisibility(View.GONE);
      newShotBarContainer.showMessageBox();
    }
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
    timelinePresenter.storeClickCount();
    sendOpenCtaLinkAnalythics(shotModel);
  }

  @Override public void showFilterAlert() {
    if (filterMenuItem != null) {
      CustomActionItemBadge.updateFilterAlert(getActivity(), filterMenuItem,
          filterMenuItem.getIcon(), " ");
    }
  }

  private void hideFilterAlert() {
    if (filterMenuItem != null) {
      ActionItemBadge.update(getActivity(), filterMenuItem, filterMenuItem.getIcon(),
          ActionItemBadge.BadgeStyles.RED, null);
    }
  }

  @Override public void renderNice(PrintableModel shotModel) {
    /* no-op */
  }

  @Override public void renderUnnice(String idShot) {
    /* no-op */
  }

  @Override public void setReshoot(String idShot, boolean mark) {
    /* no-op */
  }

  @Override public void handleNewNicestItem(PrintableModel shotModel) {
    adapter.handleNicestItem(shotModel);
  }

  @Override public void hideExternalVideo() {
    if (videoPlayer != null) {
      videoPlayer.release();
    }

    if (player != null) {
      player.setVisibility(View.GONE);
    }

    hideVideoMenuItem.setVisible(false);
    showVideoMenuItem.setVisible(false);
    videoPlayer = null;
  }

  @Override public void showPromotedButton() {
    if (sessionRepository.isPromotedShotActivated()) {
      if (newShotBarContainer != null) {
        newShotBarContainer.setCanShowPromotedButton(true);
        newShotBarContainer.setPromotedButtonState(PromotedMessageBox.PROMOTED_ENABLED);
        newShotBarContainer.showPromotedButton();
        promotedAdapter.setShouldShowAddSuperShot(true);
        promotedAdapter.showAddPromoted();
      }
    } else {
      hidePromotedButton();
    }
  }

  @Override public void showPromotedWithInfoState() {
    if (sessionRepository.isPromotedShotActivated()) {
      if (newShotBarContainer != null) {
        newShotBarContainer.setCanShowPromotedButton(true);
        newShotBarContainer.setPromotedButtonState(PromotedMessageBox.PROMOTED_SHOW_INFO);
        newShotBarContainer.showPromotedButton();
        promotedAdapter.setShouldShowAddSuperShot(false);
        promotedAdapter.hideAddPromoted();
      }
    } else {
      hidePromotedButton();
    }
  }

  @Override public void openPromotedActivationDialog(StreamModel streamModel) {
    Bundle args = new Bundle();
    args.putSerializable(PromotedShotActivationInfoDialog.STREAM, streamModel);
    PromotedShotActivationInfoDialog promotedShotInfoDialog = new PromotedShotActivationInfoDialog();
    promotedShotInfoDialog.setArguments(args);
    promotedShotInfoDialog.show(getActivity().getFragmentManager(), PromotedShotActivationInfoDialog.TAG);
  }

  @Override public void addNewHighlighted(List<PrintableModel> printableModels) {
    promotedList.setVisibility(View.VISIBLE);
    promotedAdapter.addNewHighlighted(printableModels);
  }

  @Override public void addNewPoll(List<PrintableModel> printableModels) {
    promotedList.setVisibility(View.VISIBLE);
    promotedAdapter.addNewPoll(printableModels);
  }

  @Override public void addNewPromoted(List<PrintableModel> printableModels) {
    promotedList.setVisibility(View.VISIBLE);
    promotedAdapter.addNewPromoted(printableModels);
  }

  @Override public void showPromotedList() {
    if (isAdded()) {
      promotedList.setVisibility(View.VISIBLE);
    }
  }

  @Override public void updateHighlighted(PrintableModel printableModel) {
    promotedAdapter.updateHighlighted(printableModel);
  }

  @Override public void updatePoll(PrintableModel printableModel) {
    promotedAdapter.updatePolls(printableModel);
  }

  @Override public void updatePromoted(PrintableModel printableModel) {
    promotedAdapter.updatePromoted(printableModel);
  }

  @Override public void renderPromoteds(List<PrintableModel> printableModels) {
    promotedAdapter.setPromotedList(printableModels);
  }

  @Override public void hidePromotedButton() {
    if (newShotBarContainer != null) {
      newShotBarContainer.setCanShowPromotedButton(false);
      newShotBarContainer.hidePromotedButton();
      promotedAdapter.setShouldShowAddSuperShot(false);
      promotedAdapter.hideAddPromoted();
    }
  }

  @Override public void renderFollowings(List<PrintableModel> printableModels) {
    promotedAdapter.setFollowingList(printableModels);
  }

  @Override public void addNewFollowing(List<PrintableModel> printableModels) {
    promotedAdapter.addNewUser(printableModels);
  }

  @Override public void updateFollowing(PrintableModel printableModel) {
    promotedAdapter.updateFollowing(printableModel);
  }

  @Override public void showLoadingOldShots() {
    adapter.showLoadingMoreItems();
  }

  @Override public void hideLoadingOldShots() {
    adapter.hideLoadingMoreItems();
  }

  @Override public void showCheckingForShots() {
    if (isAdded()) {
      checkingForShotsView.setVisibility(View.VISIBLE);
    }
  }

  @Override public void hideCheckingForShots() {
    if (checkingForShotsView != null) {
      checkingForShotsView.setVisibility(View.GONE);
    }
  }

  @Override public void setFixedItemsIds(ArrayList<String> fixedItemsIds) {
    longPressShotPresenter.setFixedItemsIds(fixedItemsIds);
  }

  @Override public void setTitle(String title) {
    streamTitle = title;
    toolbarDecorator.setTitle(streamTitle);
    toolbarDecorator.setTitleClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        navigateToStreamDetail(idStream);
      }
    });
  }

  private void navigateToStreamDetail(String idStream) {
    startActivityForResult(StreamDetailActivity.getIntent(getActivity(), idStream),
        REQUEST_STREAM_DETAIL);
  }

  @Override public void sendAnalythicsEnterTimeline() {
    sendTimelineAnalytics();
  }

  @Override public void showNewShotsIndicator(Integer numberNewShots) {
    try {
      newShotsNotificatorText.setVisibility(View.VISIBLE);
      String indicatorText =
          getResources().getQuantityString(R.plurals.new_shots_indicator, numberNewShots,
              numberNewShots);
      newShotsNotificatorText.setText(indicatorText);
    } catch (NullPointerException error) {
      crashReportTool.logException(error);
    }
  }

  public void showNewShotsIndicator() {
    try {
      newShotsNotificatorText.setVisibility(View.VISIBLE);
    } catch (NullPointerException error) {
      crashReportTool.logException(error);
    }
  }

  @Override public void hideNewShotsIndicator() {
    if (newShotsNotificatorText != null) {
      newShotsNotificatorText.setVisibility(View.GONE);
    }
  }

  @Override public void setRemainingCharactersCount(int remainingCharacters) {
    topicCharCounter.setText(String.valueOf(remainingCharacters));
  }

  @Override public void setRemainingCharactersColorValid() {
    topicCharCounter.setTextColor(charCounterColorNormal);
  }

  @Override public void setRemainingCharactersColorInvalid() {
    topicCharCounter.setTextColor(charCounterColorError);
  }

  @Override public void showEmpty() {
    emptyView.setText(empty_shot_timeline);
    emptyView.setVisibility(View.VISIBLE);
  }

  @Override public void showEmptyNicest() {
    emptyView.setText(empty_nicest_timeline);
    emptyView.setVisibility(View.VISIBLE);
  }

  @Override public void handleReport(String sessionToken, ShotModel shotModel) {
    longPressShotPresenter.reportClicked(Locale.getDefault().getLanguage(), sessionToken,
        shotModel);
  }

  @Override public void showEmailNotConfirmedError() {
    new AlertDialog.Builder(getActivity()).setMessage(
        getActivity().getString(R.string.alert_report_confirmed_email_message))
        .setTitle(getActivity().getString(R.string.alert_report_confirmed_email_title))
        .setPositiveButton(getActivity().getString(R.string.alert_report_confirmed_email_ok), null)
        .create()
        .show();
  }

  @Override public void notifyDeletedShot(ShotModel shotModel) {
    //TODO DELETE SHOT FROM ADAPTER
  }

  @Override public void goToReport(String sessionToken, ShotModel shotModel) {
    Intent browserIntent = new Intent(Intent.ACTION_VIEW,
        Uri.parse(String.format(reportBaseUrl, sessionToken, shotModel.getIdShot())));
    startActivity(browserIntent);
  }

  @Override
  public void showAlertLanguageSupportDialog(final String sessionToken, final ShotModel shotModel) {
    goToReport(sessionToken, shotModel);
  }

  @Override
  public void showContextMenu(HashMap<Integer, Boolean> menus, final ShotModel shotModel) {
    CustomContextMenu.Builder customContextMenu = new CustomContextMenu.Builder(getActivity());

    if (menus.get(LongPressShotPresenter.HIGHLIGHT)) {
      customContextMenu.addAction(R.string.menu_highlight_shot, new Runnable() {
        @Override public void run() {
          timelinePresenter.highlightItem(shotModel);
        }
      });
    }

    if (menus.get(LongPressShotPresenter.DISMISS_HIGHLIGHT)) {
      customContextMenu.addAction(R.string.remove_highlight, new Runnable() {
        @Override public void run() {
          timelinePresenter.onDismissHighlightShot(shotModel, shotModel.getIdUser());
        }
      });
    }

    if (menus.get(LongPressShotPresenter.RESHOOT)) {
      customContextMenu.addAction(R.string.menu_share_shot_via_shootr, new Runnable() {
        @Override public void run() {
          timelinePresenter.reshoot(shotModel);
          sendReshootAnalytics(shotModel);
        }
      });
    }

    if (menus.get(LongPressShotPresenter.UNDO_RESHOOT)) {
      customContextMenu.addAction(R.string.undo_reshoot, new Runnable() {
        @Override public void run() {
          timelinePresenter.undoReshoot(shotModel);
        }
      });
    }

    if (menus.get(LongPressShotPresenter.SHARE_VIA)) {
      customContextMenu.addAction(R.string.menu_share_shot_via, new Runnable() {
        @Override public void run() {
          shareShotIntent(shotModel);
          sendShareExternalShotAnalytics(shotModel);
        }
      });
    }

    if (menus.get(LongPressShotPresenter.COPY_TEXT)) {
      customContextMenu.addAction(R.string.menu_copy_text, new Runnable() {
        @Override public void run() {
          copyShotCommentToClipboard(shotModel);
        }
      });
    }

    if (menus.get(LongPressShotPresenter.DELETE)) {
      customContextMenu.addAction(R.string.report_context_menu_delete, new Runnable() {
        @Override public void run() {
          openDeleteConfirmation(shotModel);
        }
      });
    }

    if (menus.get(LongPressShotPresenter.REPORT)) {
      customContextMenu.addAction(R.string.report_context_menu_report, new Runnable() {
        @Override public void run() {
          longPressShotPresenter.report(shotModel);
        }
      });
    }

    customContextMenu.show();
  }

  private void openDeleteConfirmation(final ShotModel shotModel) {
    new AlertDialog.Builder(getActivity()).setMessage(R.string.delete_shot_confirmation_message)
        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
            longPressShotPresenter.deleteShot(shotModel);
          }
        })
        .setNegativeButton(R.string.cancel, null)
        .create()
        .show();
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == REQUEST_STREAM_DETAIL
        && resultCode == NewStreamActivity.RESULT_EXIT_STREAM) {
      if (getActivity() != null) {
        getActivity().finish();
      }
    } else if (requestCode == REQUEST_STREAM_DETAIL && resultCode == RESULT_OK) {
      String updatedTitle = data.getStringExtra(StreamDetailActivity.EXTRA_STREAM_TITLE);
      setTitle(updatedTitle);
    } else {
      photoPickerController.onActivityResult(requestCode, resultCode, data);
    }
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    timelinePresenter.destroy();
    if (videoPlayer != null) {
      videoPlayer.release();
    }
  }

  @Override public void hideFixedShot() {

  }

  @Override public void showDismissDialog(final ShotModel shotModel) {
    new AlertDialog.Builder(getActivity()).setMessage(getString(R.string.highlight_shot_dialog))
        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
            timelinePresenter.deleteHighlightedItem(shotModel);
          }
        })
        .setNegativeButton(getString(R.string.cancel), null)
        .create()
        .show();
  }

  @Override public void setHighlightShotBackground(Boolean isAdmin) {

  }

  @Override public void renderNewFixedItem(ShotModel shotModel) {

  }

  @Override public void onPause() {
    super.onPause();
    timelinePresenter.pause();
    calculateItemForReposition();
    if (videoPlayer != null && videoPlayer.isPlaying()) {
      timelinePresenter.onBackPressedWhilePlayingVideo(videoPlayer.getCurrentTimeMillis());
    }
  }

  private void calculateItemForReposition() {
    try {
      int firstVisiblePosition = preCachingLayoutManager.findFirstCompletelyVisibleItemPosition();
      View v = preCachingLayoutManager.findViewByPosition(firstVisiblePosition);

      PrintableModel shotStored =
          adapter.itemForIndex(firstVisiblePosition > 0 ? firstVisiblePosition : 0);
      int offset = v == null ? 0 : v.getTop();

      timelinePresenter.putItemForReposition(shotStored, offset);
    } catch (IndexOutOfBoundsException exception) {
      /* no-op */
    }
  }

  @Override public void onResume() {
    super.onResume();
    timelinePresenter.resume();
  }

  @Override public void onDestroy() {
    super.onDestroy();
    unbinder.unbind();
  }

  //region activity menu
  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.new_timeline, menu);
    addToFavoritesMenuItem.bindRealMenuItem(menu.findItem(R.id.menu_stream_add_favorite));
    removeFromFavoritesMenuItem.bindRealMenuItem(menu.findItem(R.id.menu_stream_remove_favorite));
    muteMenuItem.bindRealMenuItem(menu.findItem(R.id.menu_mute_stream));
    unmuteMenuItem.bindRealMenuItem(menu.findItem(R.id.menu_unmute_stream));
    showVideoMenuItem.bindRealMenuItem(menu.findItem(R.id.menu_show_video));
    showVideoMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    hideVideoMenuItem.bindRealMenuItem(menu.findItem(R.id.menu_hide_video));
    hideVideoMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    filterMenuItem.bindRealMenuItem(menu.findItem(R.id.menu_filter_shots));
    filterMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.menu_show_video:
        changePlayerVisibility(true);
        return true;
      case R.id.menu_hide_video:
        changePlayerVisibility(false);
        return true;
      case R.id.menu_filter_shots:
        showFilteringPopUpWindows();
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

  private void showFilteringPopUpWindows() {
    PopupWindow popupWindow = popupDisplay();

    popupWindow.showAsDropDown(getActivity().findViewById(R.id.menu_filter_shots), (int) dp(-116),
        (int) dp(-48));
  }

  public PopupWindow popupDisplay() {

    final PopupWindow popupWindow = new PopupWindow();

    LayoutInflater inflater =
        (LayoutInflater) getContext().getSystemService(getContext().LAYOUT_INFLATER_SERVICE);

    View view = inflater.inflate(R.layout.menu_filter_layout, null);
    ImageView imgNone = (ImageView) view.findViewById(R.id.none_filter_selected);
    ImageView imgImportant = (ImageView) view.findViewById(R.id.important_filter_selected);
    ImageView imgNicest = (ImageView) view.findViewById(R.id.nicest_filter_selected);
    TextView txtNone = (TextView) view.findViewById(R.id.none_filter);
    TextView txtImportant = (TextView) view.findViewById(R.id.important_filter);
    TextView txtNicest = (TextView) view.findViewById(R.id.nicest_filter);

    View.OnClickListener onClickListener = new View.OnClickListener() {
      @Override public void onClick(View view) {
        switch (view.getId()) {
          case R.id.none_filter:
            timelinePresenter.onDesactivateFilterClick();
            sendFilterOffAnalytics();
            break;
          case R.id.important_filter:
            timelinePresenter.onActivateFilterClick();
            sendFilterOnAnalytics();
            filterShowcase.setVisibility(View.GONE);
            setShowcasePreference();
            break;
          case R.id.nicest_filter:
            timelinePresenter.onActivateFilterNicestClick();
            setShowcasePreference();
            break;
          default:
            break;
        }
        popupWindow.dismiss();
      }
    };

    txtNone.setOnClickListener(onClickListener);
    txtImportant.setOnClickListener(onClickListener);
    txtNicest.setOnClickListener(onClickListener);

    switch (timelinePresenter.getFilterType()) {
      case TimelineType.MAIN:
        imgNone.setVisibility(View.VISIBLE);
        imgImportant.setVisibility(View.INVISIBLE);
        imgNicest.setVisibility(View.INVISIBLE);
        break;
      case TimelineType.IMPORTANT:
        imgNone.setVisibility(View.INVISIBLE);
        imgImportant.setVisibility(View.VISIBLE);
        imgNicest.setVisibility(View.INVISIBLE);
        break;
      case TimelineType.NICEST:
        imgNone.setVisibility(View.INVISIBLE);
        imgImportant.setVisibility(View.INVISIBLE);
        imgNicest.setVisibility(View.VISIBLE);
        break;
      default:
        break;
    }

    popupWindow.setFocusable(true);
    popupWindow.setWidth((int) dp(200f));
    popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
    popupWindow.setContentView(view);

    return popupWindow;
  }

  private float dp(float dp) {
    float scale = getResources().getDisplayMetrics().density;
    return (int) (dp * scale + 0.5f);
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

  @Override public void showError(String errorMessage) {
    feedbackMessage.showLong(getView(), errorMessage);
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

  //endregion

  @Override public void goToPollVote(String idStream, String streamAuthorIdUser) {
    Intent intent = PollVoteActivity.newIntent(getContext(), idStream, streamTitle);
    intent.putExtra(PollVoteActivity.EXTRA_ID_USER_OWNER, streamAuthorIdUser);
    startActivity(intent);
  }

  @Override public void goToOptionVoted(PollModel pollModel) {
    Intent intent = PollOptionVotedActivity.getIntentForActivity(getContext(), pollModel);
    startActivity(intent);
  }

  @Override public void goToHiddenResults(String question) {
    Intent intent = HiddenPollResultsActivity.newResultsIntent(getContext(), question);
    startActivity(intent);
  }

  @Override public void goToPollResults(String idPoll, String idStream) {
    Intent intent =
        PollResultsActivity.newResultsIntent(getContext(), idPoll, streamTitle, idStream);
    startActivity(intent);
  }

  @Override public void goToPollLiveResults(String idPoll, String idStream) {
    Intent intent =
        PollResultsActivity.newLiveResultsIntent(getContext(), idPoll, streamTitle, idStream,
            false);
    startActivity(intent);
  }

  @Override public void setupFilterShowcase() {
    ShowcaseStatus filterShowcaseStatus = checkInShowcasePreferences.get();
    if (filterShowcaseStatus.shouldShowShowcase()) {
      if (filterShowcase != null) {
        filterShowcase.setVisibility(View.VISIBLE);
        filterShowcase.setOnClickListener(new View.OnClickListener() {
          @Override public void onClick(View view) {
            filterShowcase.setVisibility(View.GONE);
            setShowcasePreference();
          }
        });
        filterShowcaseStatus.setTimesViewed(filterShowcaseStatus.getTimesViewed() + 1);
        checkInShowcasePreferences.set(filterShowcaseStatus);
      }
    } else {
      if (filterShowcase != null) {
        filterShowcase.setVisibility(View.GONE);
      }
    }
  }

  @Override public void showWatchingPeopleCount(Integer[] peopleWatchingCount) {
    watchNumberCount = peopleWatchingCount;
    updateWatchNumberIcon();
  }

  @Override public void hideWatchingPeopleCount() {
    watchNumberCount = null;
    updateWatchNumberIcon();
  }

  @Override public void hideEmpty() {
    if (emptyView != null) {
      emptyView.setVisibility(View.GONE);
    }
  }

  @Override public void updateFixedItem(List<PrintableModel> printableModels) {
    adapter.updateFixedItem(printableModels.get(0));
  }

  @Override public void showVideoVisibilityMenu() {
    hideVideoMenuItem.setVisible(true);
  }

  @Override public void resumeVideo() {
    createFloatingWidget();
    if (videoPlayer != null && videoHasBeenPlayed) {
      timelinePresenter.onVideoStarted();
    }
  }

  private void updateWatchNumberIcon() {
    if (watchNumberCount != null && timelinePresenter.getFilterType().equals(TimelineType.MAIN)) {
      toolbarDecorator.showSubtitle();
      toolbarDecorator.setSubtitle(handleSubtitle());
    } else {
      toolbarDecorator.hideSubtitle();
    }
  }

  private String handleSubtitle() {
    String result = "";
    if (isAdded()) {
      long followers = watchNumberCount[FOLLOWERS], connected = watchNumberCount[CONNECTED];
      if (followers > 0) {
        result = getContext().getResources()
            .getQuantityString(R.plurals.total_followers_pattern, watchNumberCount[FOLLOWERS],
                formatNumberUtils.formatNumbers(Long.valueOf(watchNumberCount[FOLLOWERS])));
      }
      if (followers > 0 && connected > 0) {
        result += ", ";
      }
      if (connected > 0) {
        result += getContext().getResources()
            .getQuantityString(R.plurals.total_watchers_pattern, watchNumberCount[CONNECTED],
                formatNumberUtils.formatNumbers(Long.valueOf(watchNumberCount[CONNECTED])));
      }
    }
    return result;
  }

  private void setShowcasePreference() {
    if (checkInShowcasePreferences.get().shouldShowShowcase()) {
      ShowcaseStatus checkInShowcaseStatus = checkInShowcasePreferences.get();
      filterShowcase.setVisibility(View.GONE);
      checkInShowcaseStatus.setShouldShowShowcase(false);
      checkInShowcasePreferences.set(checkInShowcaseStatus);
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
    //TODO builder.setIsStrategic(streamTimelinePresenter.isStrategic());
    analyticsTool.analyticsSendAction(builder);
    analyticsTool.appsFlyerSendAction(builder);
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
    builder.setIdShot(shotModel.getIdShot());
    analyticsTool.analyticsSendAction(builder);
  }

  private void sendCheckinAnalythics() {
    AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
    builder.setContext(getContext());
    builder.setActionId(analyticsActionCheckin);
    builder.setLabelId(analyticsActionCheckin);
    builder.setSource(checkinShowCaseTimeline);
    builder.setUser(sessionRepository.getCurrentUser());
    builder.setIdStream(idStream);
    builder.setStreamName((streamTitle != null) ? streamTitle
        : sessionRepository.getCurrentUser().getWatchingStreamTitle());
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

  @Override public void onPlaying() {
    ChangeBounds transition = new ChangeBounds();
    transition.setInterpolator(new AccelerateDecelerateInterpolator());
    TransitionManager.beginDelayedTransition(container, transition);
    toolbarDecorator.hideToolbar();
  }

  @Override public void onPaused() {
    ChangeBounds transition = new ChangeBounds();
    transition.setInterpolator(new AccelerateDecelerateInterpolator());
    TransitionManager.beginDelayedTransition(container, transition);
    toolbarDecorator.showToolbar();
  }

  @Override public void onStopped() {
    /* no-op */
  }

  @Override public void onBuffering(boolean b) {
    /* no-op */
  }

  @Override public void onSeekTo(int i) {
    /* no-op */
  }

  @Override public void onFullscreen(boolean isFullScreen) {
    this.isFullScreen = isFullScreen;
  }

  public boolean onBackPressed() {
    if (isFullScreen) {
      if (videoPlayer != null && isFullScreen) {
        videoPlayer.setFullscreen(false);
        return true;
      }
    }
    return false;
  }

  public void createFloatingWidget() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(getContext())) {
      Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
          Uri.parse("package:" + getContext().getPackageName()));
      startActivityForResult(intent, DRAW_OVER_OTHER_APP_PERMISSION_REQUEST_CODE);
    } else {
      FloatingVideoService.startService(getContext());
    }
  }

  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {

    if (requestCode == DRAW_OVER_OTHER_APP_PERMISSION_REQUEST_CODE) {
      if (grantResults.length > 0) {
        createFloatingWidget();
      }
    } else {
      super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
  }

  @Override
  public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer,
      boolean b) {
    videoPlayer = youTubePlayer;
    videoPlayer.cueVideo(currentVideoModel.getVideoId());
    videoPlayer.setPlaybackEventListener(TimelineFragment.this);
    videoPlayer.setManageAudioFocus(true);
    videoPlayer.setOnFullscreenListener(TimelineFragment.this);
    videoPlayer.setPlayerStateChangeListener(this);
  }

  @Override public void onInitializationFailure(YouTubePlayer.Provider provider,
      YouTubeInitializationResult youTubeInitializationResult) {
    Toast.makeText(getContext(), "fallo", Toast.LENGTH_LONG).show();
    Log.d("youtube", "fallo " + youTubeInitializationResult);
  }

  @Override public void onLoading() {
    /* no-op */
  }

  @Override public void onLoaded(String s) {
    /* no-op */
  }

  @Override public void onAdStarted() {
    /* no-op */
  }

  @Override public void onVideoStarted() {
    videoHasBeenPlayed = true;
    timelinePresenter.onVideoStarted();
  }

  @Override public void onVideoEnded() {
    toolbarDecorator.showToolbar();
  }

  @Override public void onError(YouTubePlayer.ErrorReason errorReason) {
    toolbarDecorator.showToolbar();
  }

  public void changePlayerVisibility(final boolean show) {

    if (!videoAnimationPlaying) {
      ConstraintSet constraintSet = new ConstraintSet();

      constraintSet.clone(getContext(),
          show ? R.layout.stream_timeline : R.layout.stream_timeline_hidden_video);
      constraintSet.applyTo(container);

      Transition transition = new AutoTransition();
      transition.setInterpolator(new DecelerateInterpolator());
      transition.addListener(new Transition.TransitionListener() {
        @Override public void onTransitionStart(@NonNull Transition transition) {
          videoAnimationPlaying = true;
          if (show) {
            hideVideoMenuItem.setVisible(true);
            showVideoMenuItem.setVisible(false);
          } else {
            hideVideoMenuItem.setVisible(false);
            showVideoMenuItem.setVisible(true);
          }
        }

        @Override public void onTransitionEnd(@NonNull Transition transition) {
          videoAnimationPlaying = false;
        }

        @Override public void onTransitionCancel(@NonNull Transition transition) {
          videoAnimationPlaying = false;
        }

        @Override public void onTransitionPause(@NonNull Transition transition) {
          videoAnimationPlaying = false;
        }

        @Override public void onTransitionResume(@NonNull Transition transition) {
          videoAnimationPlaying = true;
        }
      });
      TransitionManager.beginDelayedTransition(container, transition);
    }
  }

  private void changeStatusBarColor() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      Window window = getActivity().getWindow();
      window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      window.setStatusBarColor(getContext().getResources().getColor(R.color.black));
    }
  }

  private void goToChannelTimeline(String idTargetUser) {
    startActivity(PrivateMessageTimelineActivity.newIntent(getContext(), idTargetUser));
  }
}
