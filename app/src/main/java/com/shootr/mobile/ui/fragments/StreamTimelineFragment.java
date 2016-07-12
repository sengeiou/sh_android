package com.shootr.mobile.ui.fragments;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
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
import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.shootr.mobile.R;
import com.shootr.mobile.domain.dagger.TemporaryFilesDir;
import com.shootr.mobile.ui.ToolbarDecorator;
import com.shootr.mobile.ui.activities.DraftsActivity;
import com.shootr.mobile.ui.activities.NewStreamActivity;
import com.shootr.mobile.ui.activities.PhotoViewActivity;
import com.shootr.mobile.ui.activities.PollResultsActivity;
import com.shootr.mobile.ui.activities.PollVoteActivity;
import com.shootr.mobile.ui.activities.PostNewShotActivity;
import com.shootr.mobile.ui.activities.ProfileContainerActivity;
import com.shootr.mobile.ui.activities.ShotDetailActivity;
import com.shootr.mobile.ui.activities.StreamDetailActivity;
import com.shootr.mobile.ui.adapters.ShotsTimelineAdapter;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnImageClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnImageLongClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnNiceShotListener;
import com.shootr.mobile.ui.adapters.listeners.OnReplyShotListener;
import com.shootr.mobile.ui.adapters.listeners.OnShotLongClick;
import com.shootr.mobile.ui.adapters.listeners.OnUsernameClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnVideoClickListener;
import com.shootr.mobile.ui.adapters.listeners.ShotClickListener;
import com.shootr.mobile.ui.base.BaseFragment;
import com.shootr.mobile.ui.component.PhotoPickerController;
import com.shootr.mobile.ui.model.PollModel;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.presenter.NewShotBarPresenter;
import com.shootr.mobile.ui.presenter.PinShotPresenter;
import com.shootr.mobile.ui.presenter.ReportShotPresenter;
import com.shootr.mobile.ui.presenter.StreamPollIndicatorPresenter;
import com.shootr.mobile.ui.presenter.StreamTimelineOptionsPresenter;
import com.shootr.mobile.ui.presenter.StreamTimelinePresenter;
import com.shootr.mobile.ui.presenter.WatchNumberPresenter;
import com.shootr.mobile.ui.views.NewShotBarView;
import com.shootr.mobile.ui.views.PinShotView;
import com.shootr.mobile.ui.views.ReportShotView;
import com.shootr.mobile.ui.views.StreamPollView;
import com.shootr.mobile.ui.views.StreamTimelineOptionsView;
import com.shootr.mobile.ui.views.StreamTimelineView;
import com.shootr.mobile.ui.views.WatchNumberView;
import com.shootr.mobile.ui.views.nullview.NullNewShotBarView;
import com.shootr.mobile.ui.views.nullview.NullStreamTimelineOptionsView;
import com.shootr.mobile.ui.views.nullview.NullStreamTimelineView;
import com.shootr.mobile.ui.views.nullview.NullWatchNumberView;
import com.shootr.mobile.util.AnalyticsTool;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.Clipboard;
import com.shootr.mobile.util.CrashReportTool;
import com.shootr.mobile.util.CustomContextMenu;
import com.shootr.mobile.util.FeedbackMessage;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.Intents;
import com.shootr.mobile.util.MenuItemValueHolder;
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
    ReportShotView, PinShotView, StreamPollView {

  public static final String EXTRA_STREAM_ID = "streamId";
  public static final String EXTRA_STREAM_TITLE = "streamTitle";
  public static final String EXTRA_ID_USER = "userId";

  public static final String EXTRA_READ_WRITE_MODE = "readWriteMode";
  private static final int REQUEST_STREAM_DETAIL = 1;
  private static final String POLL_STATUS_SHOWING = "showing";
  private static final String POLL_STATUS_INVISIBLE = "invisible";
  private static final String POLL_STATUS_GONE = "gone";

  //region Fields
  @Inject StreamTimelinePresenter streamTimelinePresenter;
  @Inject NewShotBarPresenter newShotBarPresenter;
  @Inject WatchNumberPresenter watchNumberPresenter;
  @Inject StreamTimelineOptionsPresenter streamTimelineOptionsPresenter;
  @Inject ReportShotPresenter reportShotPresenter;
  @Inject PinShotPresenter pinShotPresenter;
  @Inject StreamPollIndicatorPresenter streamPollIndicatorPresenter;

  @Inject ImageLoader imageLoader;
  @Inject AndroidTimeUtils timeUtils;
  @Inject ToolbarDecorator toolbarDecorator;
  @Inject ShareManager shareManager;
  @Inject FeedbackMessage feedbackMessage;
  @Inject @TemporaryFilesDir File tmpFiles;
  @Inject AnalyticsTool analyticsTool;
  @Inject WritePermissionManager writePermissionManager;
  @Inject CrashReportTool crashReportTool;

  @Bind(R.id.timeline_shot_list) RecyclerView shotsTimeline;
  @Bind(R.id.timeline_swipe_refresh) SwipeRefreshLayout swipeRefreshLayout;
  @Bind(R.id.timeline_new_shots_indicator_container) RelativeLayout timelineNewShotsIndicator;
  @Bind(R.id.timeline_indicator) RelativeLayout timelineIndicatorContainer;
  @Bind(R.id.timeline_empty) View emptyView;
  @Bind(R.id.timeline_checking_for_shots) TextView checkingForShotsView;
  @Bind(R.id.shot_bar_drafts) View draftsButton;
  @Bind(R.id.timeline_new_shots_indicator_text) TextView timelineIndicatorText;
  @Bind(R.id.timeline_view_only_stream_indicator) View timelineViewOnlyStreamIndicator;
  @Bind(R.id.timeline_new_shot_bar) View newShotBarContainer;
  @Bind(R.id.timeline_message) TextView streamMessage;
  @Bind(R.id.timeline_poll_indicator) RelativeLayout timelinePollIndicator;
  @Bind(R.id.poll_question) TextView pollQuestion;
  @Bind(R.id.poll_action) TextView pollAction;
  @BindString(R.string.report_base_url) String reportBaseUrl;
  @BindString(R.string.added_to_favorites) String addToFavorites;
  @BindString(R.string.shot_shared_message) String shotShared;
  @BindString(R.string.analytics_screen_stream_timeline) String analyticsScreenStreamTimeline;
  @BindString(R.string.poll_vote) String pollVoteString;
  @BindString(R.string.poll_view) String pollViewString;
  @BindString(R.string.poll_results) String pollResultsString;

  private ShotsTimelineAdapter adapter;
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
  private int charCounterColorError;
  private int charCounterColorNormal;
  private EditText newTopicText;
  private TextView topicCharCounter;
  private LinearLayoutManager linearLayoutManager;
  private String pollIndicatorStatus;
  private AlertDialog shotImageDialog;
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
    ButterKnife.bind(this, fragmentView);
    linearLayoutManager = new LinearLayoutManager(getContext());
    shotsTimeline.setLayoutManager(linearLayoutManager);
    newShotBarContainer.setVisibility(View.GONE);
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
    setStreamTitle(getArguments().getString(EXTRA_STREAM_TITLE));
    Integer streamMode = getArguments().getInt(EXTRA_READ_WRITE_MODE, 0);
    setStreamTitleClickListener(idStream);
    setupPresentersInitialization(idStream, streamAuthorIdUser, streamMode);
    analyticsTool.analyticsStart(getContext(), analyticsScreenStreamTimeline);
  }

  protected void setupPresentersInitialization(String idStream, String streamAuthorIdUser,
      Integer streamMode) {
    streamTimelinePresenter.setIsFirstLoad(true);
    streamTimelinePresenter.setIsFirstShotPosition(true);
    if (streamAuthorIdUser != null) {
      initializePresentersWithStreamAuthorId(idStream, streamAuthorIdUser, streamMode);
    } else {
      initializePresenters(idStream, streamAuthorIdUser, streamMode);
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
    if (isAdded()) {
      updateWatchNumberIcon();
    }
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.menu_showing_holding_shots:
        streamTimelinePresenter.onHoldingShotsClick();
        return true;
      case R.id.menu_showing_all_shots:
        streamTimelinePresenter.onAllStreamShotsClick();
        return true;
      case R.id.menu_stream_add_favorite:
        streamTimelineOptionsPresenter.addToFavorites();
        return true;
      case R.id.menu_stream_remove_favorite:
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
    streamPollIndicatorPresenter.resume();
  }

  @Override public void onPause() {
    super.onPause();
    streamTimelinePresenter.pause();
    newShotBarPresenter.pause();
    watchNumberPresenter.pause();
    streamTimelineOptionsPresenter.pause();
    streamPollIndicatorPresenter.pause();
  }

  private void initializePresentersWithStreamAuthorId(String idStream, String streamAuthorIdUser,
      Integer streamMode) {
    streamTimelinePresenter.initialize(this, idStream, streamAuthorIdUser, streamMode);
    pinShotPresenter.initialize(this);
    newShotBarPresenter.initializeWithIdStreamAuthor(this, idStream, streamAuthorIdUser, true);
    watchNumberPresenter.initialize(this, idStream);
    streamTimelineOptionsPresenter.initialize(this, idStream);
    reportShotPresenter.initialize(this);
    streamPollIndicatorPresenter.initialize(this, idStream, streamAuthorIdUser);
  }

  private void initializePresenters(String idStream, String streamAuthorIdUser,
      Integer streamMode) {
    streamTimelinePresenter.initialize(this, idStream, streamMode);
    pinShotPresenter.initialize(this);
    newShotBarPresenter.initializeWithIdStreamAuthor(this, idStream, streamAuthorIdUser, true);
    watchNumberPresenter.initialize(this, idStream);
    streamTimelineOptionsPresenter.initialize(this, idStream);
    reportShotPresenter.initialize(this);
    streamPollIndicatorPresenter.initialize(this, idStream, streamAuthorIdUser);
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
    adapter = new ShotsTimelineAdapter(getActivity(), //
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
        }, new OnReplyShotListener() {
      @Override public void reply(ShotModel shotModel) {
        Intent newShotIntent = PostNewShotActivity.IntentBuilder //
            .from(getActivity()) //
            .inReplyTo(shotModel.getIdShot(), shotModel.getUsername()).build();
        startActivity(newShotIntent);
      }
    }, null, false, new ShotClickListener() {
      @Override public void onClick(ShotModel shot) {
        Intent intent = ShotDetailActivity.getIntentForActivityFromTimeline(getActivity(), shot);
        startActivity(intent);
      }
    }, new OnShotLongClick() {
      @Override public void onShotLongClick(ShotModel shot) {
        String streamAuthorIdUser = getArguments().getString(EXTRA_ID_USER);
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
      @Override public void onImageClick(View sharedImage, ShotModel shot) {
        openImage(sharedImage, shot.getImage().getImageUrl());
      }
    });
    shotsTimeline.setAdapter(adapter);
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

        if (dy > 10) {
          hideNewShotsIndicator();
        }

        if (shotsTimeline != null) {
          if (linearLayoutManager.findFirstVisibleItemPosition() == 0) {
            streamTimelinePresenter.setIsFirstShotPosition(true);
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
    int lastVisiblePosition = linearLayoutManager.findLastVisibleItemPosition();
    if (lastItemPosition == lastVisiblePosition && lastItemPosition >= 0) {
      streamTimelinePresenter.showingLastShot(adapter.getLastShot());
    }
  }
  //endregion

  private void openProfile(String idUser) {
    Intent profileIntent = ProfileContainerActivity.getIntent(getActivity(), idUser);
    startActivity(profileIntent);
  }

  private void openImage(View sharedImage, String imageUrl) {
    Intent intent = PhotoViewActivity.getIntentForActivity(getContext(), imageUrl, imageUrl);
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
      ActivityOptions activityOptions =
          ActivityOptions.makeSceneTransitionAnimation(getActivity(), sharedImage,
              sharedImage.getTransitionName());
      startActivity(intent, activityOptions.toBundle());
    } else {
      startActivity(intent);
    }
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
    Intent shareIntent = shareManager.shareShotIntent(getActivity(), shotModel);
    Intents.maybeStartActivity(getActivity(), shareIntent);
  }

  private void copyShotCommentToClipboard(ShotModel shotModel) {
    Clipboard.copyShotComment(getActivity(), shotModel);
  }

  private void setStreamTitle(String streamTitle) {
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
    newShotBarViewDelegate =
        new NewShotBarViewDelegate(photoPickerController, draftsButton, feedbackMessage) {
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

          @Override public void openEditTopicDialog() {
            setupTopicCustomDialog();
          }
        };
  }

  private void setupImageDialog(ShotModel shot) {
    LayoutInflater inflater = getActivity().getLayoutInflater();
    View dialogView = inflater.inflate(R.layout.dialog_shot_image, null);
    TextView user = (TextView) dialogView.findViewById(R.id.shot_user_name);
    ImageView image = (ImageView) dialogView.findViewById(R.id.shot_image);
    CircleImageView avatar = (CircleImageView) dialogView.findViewById(R.id.shot_avatar);

    user.setText(shot.getUsername());
    imageLoader.load(shot.getImage().getImageUrl(), image);
    imageLoader.load(shot.getPhoto(), avatar);

    shotImageDialog = new AlertDialog.Builder(getActivity()).setView(dialogView).create();

    shotImageDialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;

    shotImageDialog.show();
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
      toolbarDecorator.setSubtitle(watchNumberCount);
    }
  }

  @OnClick(R.id.shot_bar_text) public void startNewShot() {
    newShotBarPresenter.newShotFromTextBox();
  }

  @OnClick(R.id.shot_bar_photo) public void startNewShotWithPhoto() {
    newShotBarPresenter.newShotFromImage();
  }

  @OnClick(R.id.shot_bar_drafts) public void openDraftsClicked() {
    startActivity(new Intent(getActivity(), DraftsActivity.class));
  }

  @OnClick(R.id.timeline_new_shots_indicator_text) public void goToTopOfTimeline() {
    shotsTimeline.smoothScrollToPosition(0);
    if (streamMessage.getText().toString().isEmpty()) {
      timelineNewShotsIndicator.setVisibility(View.GONE);
      timelineIndicatorContainer.setVisibility(View.GONE);
    }
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
    feedbackMessage.show(getView(), R.string.showing_shots_by_holder);
  }

  @Override public void showHoldingShots() {
    showHoldingShotsMenuItem.setVisible(true);
  }

  @Override public void hideAllStreamShots() {
    showAllShotsMenuItem.setVisible(false);
  }

  @Override public void setTitle(String title) {
    setStreamTitle(title);
  }

  @Override public void showNewShotsIndicator(Integer numberNewShots) {
    timelineNewShotsIndicator.setVisibility(View.VISIBLE);
    timelineIndicatorContainer.setVisibility(View.VISIBLE);
    timelineIndicatorText.setVisibility(View.VISIBLE);
    String indicatorText =
        getResources().getQuantityString(R.plurals.new_shots_indicator, numberNewShots,
            numberNewShots);
    timelineIndicatorText.setText(indicatorText);
    if (pollIndicatorStatus.equals(POLL_STATUS_SHOWING)) {
      timelinePollIndicator.setVisibility(View.GONE);
      pollIndicatorStatus = POLL_STATUS_INVISIBLE;
    }
  }

  @Override public void hideNewShotsIndicator() {
    timelineIndicatorText.setVisibility(View.GONE);
    streamTimelinePresenter.setNewShotsNumber(0);
    if (streamMessage.getText().toString().isEmpty()) {
      timelineNewShotsIndicator.setVisibility(View.GONE);
    }
    if (pollIndicatorStatus != null && pollIndicatorStatus.equals(POLL_STATUS_INVISIBLE)) {
      pollIndicatorStatus = POLL_STATUS_SHOWING;
      timelinePollIndicator.setVisibility(View.VISIBLE);
      timelineIndicatorContainer.setVisibility(View.VISIBLE);
    }
  }

  @Override public void showPinnedMessage(String topic) {
    if (timelineNewShotsIndicator != null) {
      timelineNewShotsIndicator.setVisibility(View.VISIBLE);
      timelineIndicatorContainer.setVisibility(View.VISIBLE);
      streamMessage.setVisibility(View.VISIBLE);
      streamMessage.setText(topic);
    }
  }

  @Override public void hidePinnedMessage() {
    if (streamMessage != null) {
      streamMessage.setVisibility(View.GONE);
      timelineNewShotsIndicator.setVisibility(View.GONE);
      timelineIndicatorContainer.setVisibility(View.GONE);
    }
    if (pollIndicatorStatus != null && pollIndicatorStatus.equals(POLL_STATUS_SHOWING)) {
      timelineNewShotsIndicator.setVisibility(View.GONE);
      timelinePollIndicator.setVisibility(View.VISIBLE);
      timelineIndicatorContainer.setVisibility(View.VISIBLE);
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
    int index = linearLayoutManager.findFirstVisibleItemPosition() + shotModels.size();
    View v = shotsTimeline.getChildAt(0);
    int top = (v == null) ? 0 : v.getTop();

    adapter.addShotsAbove(shotModels);
    adapter.notifyDataSetChanged();

    linearLayoutManager.scrollToPositionWithOffset(index, top);
  }

  @Override public void updateShotsInfo(List<ShotModel> shots) {
    adapter.setShots(shots);
    adapter.notifyDataSetChanged();
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

  @Override public void showAuthorContextMenuWithoutPin(final ShotModel shotModel) {
    getBaseContextMenuOptions(shotModel).addAction(R.string.report_context_menu_delete,
        new Runnable() {
          @Override public void run() {
            openDeleteConfirmation(shotModel);
          }
        }).show();
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
    newShotBarViewDelegate.openNewShotView();
  }

  @Override public void pickImage() {
    if (writePermissionManager.hasWritePermission()) {
      newShotBarViewDelegate.pickImage();
    } else {
      writePermissionManager.requestWritePermissionToUser();
    }
  }

  @Override public void showHolderOptions() {
    if (writePermissionManager.hasWritePermission()) {
      newShotBarViewDelegate.showHolderOptions();
    } else {
      writePermissionManager.requestWritePermissionToUser();
    }
  }

  @Override public void openNewShotViewWithImage(File image) {
    newShotBarViewDelegate.openNewShotViewWithImage(image);
  }

  @Override public void openEditTopicDialog() {
    newShotBarViewDelegate.openEditTopicDialog();
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
    new CustomContextMenu.Builder(getActivity()).addAction(R.string.menu_share_shot_via_shootr,
        new Runnable() {
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
  }

  @Override public void showAuthorContextMenuWithPin(final ShotModel shotModel) {
    new CustomContextMenu.Builder(getActivity()).addAction(R.string.menu_pin_shot, new Runnable() {
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
    streamTimelinePresenter.onShotDeleted(adapter.getItemCount());
  }

  @Override public void showPollIndicatorWithViewAction(final PollModel pollModel) {
    setupPollIndicator(pollModel);
    if (canSetPollAction()) {
      pollAction.setText(pollViewString.toUpperCase());
    }
  }

  @Override public void showPollIndicatorWithVoteAction(PollModel pollModel) {
    setupPollIndicator(pollModel);
    if (canSetPollAction()) {
      pollAction.setText(pollVoteString.toUpperCase());
    }
  }

  @Override public void showPollIndicatorWithResultsAction(PollModel pollModel) {
    setupPollIndicator(pollModel);
    if (canSetPollAction()) {
      pollAction.setText(pollResultsString.toUpperCase());
    }
  }

  private boolean canSetPollAction() {
    return pollAction != null;
  }

  private void setupPollIndicator(PollModel pollModel) {
    pollIndicatorStatus = POLL_STATUS_SHOWING;
    if (timelinePollIndicator != null) {
      timelineIndicatorContainer.setVisibility(View.VISIBLE);
      timelinePollIndicator.setVisibility(View.VISIBLE);
      pollQuestion.setText(pollModel.getQuestion());
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
    Intent intent = PollVoteActivity.newIntent(getContext(), idStream);
    intent.putExtra(PollVoteActivity.EXTRA_ID_USER_OWNER, streamAuthorIdUser);
    startActivity(intent);
  }

  @Override public void goToPollResults(String idPoll) {
    Intent intent = PollResultsActivity.newResultsIntent(getContext(), idPoll);
    startActivity(intent);
  }

  @Override public void goToPollLiveResults(String idPoll) {
    Intent intent = PollResultsActivity.newLiveResultsIntent(getContext(), idPoll);
    startActivity(intent);
  }

  @OnClick(R.id.poll_action) public void onActionPressed() {
    streamPollIndicatorPresenter.onActionPressed();
  }
  //endregion
}
