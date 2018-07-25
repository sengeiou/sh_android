package com.shootr.mobile.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.shootr.mobile.R;
import com.shootr.mobile.domain.dagger.TemporaryFilesDir;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.ui.ToolbarDecorator;
import com.shootr.mobile.ui.adapters.NewShotDetailAdapter;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnImageClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnImageLongClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnNiceShotListener;
import com.shootr.mobile.ui.adapters.listeners.OnOpenShotMenuListener;
import com.shootr.mobile.ui.adapters.listeners.OnReshootClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnShotLongClick;
import com.shootr.mobile.ui.adapters.listeners.OnUrlClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnUsernameClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnVideoClickListener;
import com.shootr.mobile.ui.adapters.listeners.ShareClickListener;
import com.shootr.mobile.ui.adapters.listeners.ShotClickListener;
import com.shootr.mobile.ui.component.PhotoPickerController;
import com.shootr.mobile.ui.fragments.BottomYoutubeVideoPlayer;
import com.shootr.mobile.ui.fragments.NewShotBarViewDelegate;
import com.shootr.mobile.ui.model.BaseMessageModel;
import com.shootr.mobile.ui.model.PrintableModel;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.model.StreamModel;
import com.shootr.mobile.ui.presenter.NewShotBarPresenter;
import com.shootr.mobile.ui.presenter.NewShotDetailPresenter;
import com.shootr.mobile.ui.views.NewShotBarView;
import com.shootr.mobile.ui.views.NewShotDetailView;
import com.shootr.mobile.util.AnalyticsTool;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.BackStackHandler;
import com.shootr.mobile.util.Clipboard;
import com.shootr.mobile.util.CrashReportTool;
import com.shootr.mobile.util.ExternalVideoUtils;
import com.shootr.mobile.util.FeedbackMessage;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.Intents;
import com.shootr.mobile.util.MenuItemValueHolder;
import com.shootr.mobile.util.NumberFormatUtil;
import com.shootr.mobile.util.ShareManager;
import com.shootr.mobile.util.TimeFormatter;
import com.shootr.mobile.util.WritePermissionManager;
import java.io.File;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class NewShotDetailActivity extends BaseToolbarDecoratedActivity
    implements NewShotDetailView, NewShotBarView {

  public static final String EXTRA_SHOT = "shot";
  public static final String EXTRA_ID_SHOT = "idShot";
  public static final String EXTRA_IS_IN_TIMELINE = "isIntimeline";
  private static final int OFFSET = 500;
  private static final int OFFSET_WITH_REPLIES = 400;

  @BindView(R.id.shot_detail_list) RecyclerView detailList;
  @BindView(R.id.shot_bar_text) TextView replyPlaceholder;
  @BindView(R.id.shot_bar_drafts) View replyDraftsButton;
  @BindView(R.id.detail_new_shot_bar) View newShotBar;
  @BindView(R.id.container) View container;
  @BindView(R.id.stream_name) TextView streamName;
  @BindView(R.id.show_action) TextView showParents;
  @BindView(R.id.progress) ProgressBar progressBar;
  @BindString(R.string.shot_shared_message) String shotShared;
  @BindString(R.string.analytics_screen_shot_detail) String analyticsScreenShotDetail;
  @BindString(R.string.analytics_action_checkin) String analyticsActionCheckin;
  @BindString(R.string.analytics_action_photo) String analyticsActionPhoto;
  @BindString(R.string.analytics_label_photo) String analyticsLabelPhoto;
  @BindString(R.string.analytics_action_nice) String analyticsActionNice;
  @BindString(R.string.analytics_label_nice) String analyticsLabelNice;
  @BindString(R.string.analytics_action_share_shot) String analyticsActionShareShot;
  @BindString(R.string.analytics_label_share_shot) String analyticsLabelShareShot;
  @BindString(R.string.analytics_action_external_share) String analyticsActionExternalShare;
  @BindString(R.string.analytics_label_external_share) String analyticsLabelExternalShare;
  @BindString(R.string.analytics_label_open_link) String analyticsLabelOpenlink;
  @BindString(R.string.analytics_action_open_link) String analyticsActionOpenLink;
  @BindString(R.string.analytics_source_shot_detail) String shotDetailSource;
  @BindString(R.string.analytics_source_ctashot_detail) String ctaShotSource;
  @BindString(R.string.stream_checked) String streamChecked;
  @BindString(R.string.analytics_action_open_video) String analyticsActionOpenVideo;
  @BindString(R.string.analytics_label_open_video) String analyticsLabelOpenVideo;
  @BindString(R.string.show_previous) String showPrevious;
  @BindString(R.string.hide_previous) String hidePrevious;

  @Inject ImageLoader imageLoader;
  @Inject TimeFormatter timeFormatter;
  @Inject NumberFormatUtil numberFormatUtil;
  @Inject AndroidTimeUtils timeUtils;
  @Inject NewShotDetailPresenter detailPresenter;
  @Inject NewShotBarPresenter newShotBarPresenter;
  @Inject ShareManager shareManager;
  @Inject FeedbackMessage feedbackMessage;
  @Inject @TemporaryFilesDir File tmpFiles;
  @Inject WritePermissionManager writePermissionManager;
  @Inject BackStackHandler backStackHandler;
  @Inject CrashReportTool crashReportTool;
  @Inject AnalyticsTool analyticsTool;
  @Inject SessionRepository sessionRepository;
  @Inject ExternalVideoUtils externalVideoUtils;

  private PhotoPickerController photoPickerController;
  private NewShotBarViewDelegate newShotBarViewDelegate;
  private NewShotDetailAdapter detailAdapter;

  private MenuItemValueHolder copyShotMenuItem = new MenuItemValueHolder();
  private MenuItemValueHolder reshootMenuItem = new MenuItemValueHolder();
  private MenuItemValueHolder undoReshootMenuItem = new MenuItemValueHolder();

  private LinearLayoutManager linearLayoutManager;
  private String idUser;
  private ShotModel mainShot;

  public static Intent getIntentForActivity(Context context, String idShot) {
    Intent intent = new Intent(context, NewShotDetailActivity.class);
    intent.putExtra(EXTRA_ID_SHOT, idShot);
    return intent;
  }

  @Override protected int getLayoutResource() {
    return R.layout.activity_new_shot_detail;
  }

  @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
    toolbarDecorator.hideTitle();
  }

  @Override protected void initializeViews(Bundle savedInstanceState) {
    ButterKnife.bind(this);
    sendScreenToAnalythics();
    writePermissionManager.init(this);
    setupPhotoPicker();
    setupAdapter();
  }

  @Override public void initializeNewShotBarPresenter(String streamId) {
    newShotBarPresenter.initialize(this, streamId, false);
  }

  @Override protected void initializePresenter() {
    String idShot = getIntent().getStringExtra(EXTRA_ID_SHOT);
    detailPresenter.initialize(this, idShot);
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    photoPickerController.onActivityResult(requestCode, resultCode, data);
  }

  @Override protected void onResume() {
    super.onResume();
    detailPresenter.resume();
    newShotBarPresenter.resume();
  }

  @Override protected void onPause() {
    super.onPause();
    detailPresenter.pause();
    newShotBarPresenter.pause();
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_shot_detail, menu);
    copyShotMenuItem.bindRealMenuItem(menu.findItem(R.id.menu_copy_text));
    reshootMenuItem.bindRealMenuItem(menu.findItem(R.id.menu_reshoot));
    undoReshootMenuItem.bindRealMenuItem(menu.findItem(R.id.menu_undo_reshoot));
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      backStackHandler.handleBackStack(this);
      return true;
    } else if (item.getItemId() == R.id.menu_reshoot) {
      reshoot();
    } else if (item.getItemId() == R.id.menu_undo_reshoot) {
      detailPresenter.undoReshoot();
      reshootMenuItem.setVisible(true);
      undoReshootMenuItem.setVisible(false);
    } else if (item.getItemId() == R.id.menu_share_via) {
      externalShare();
    } else if (item.getItemId() == R.id.menu_copy_text) {
      Clipboard.copyShotComment(this, mainShot);
    }
    return super.onOptionsItemSelected(item);
  }

  private void externalShare() {
    detailPresenter.shareShot();
    sendExternalShareAnalythics();
  }

  private void reshoot() {
    detailPresenter.reshoot();
    reshootMenuItem.setVisible(false);
    undoReshootMenuItem.setVisible(true);
  }

  private void setupAdapter() {

    detailAdapter =
        new NewShotDetailAdapter(imageLoader, numberFormatUtil, new OnUsernameClickListener() {
          @Override public void onUsernameClick(String username) {
            /* no-op */
          }
        }, new OnAvatarClickListener() {
          @Override public void onAvatarClick(String userId, View avatarView) {
            openProfile(userId);
          }
        }, new OnVideoClickListener() {
          @Override public void onVideoClick(String url) {
            onShotVideoClick(url);
          }
        }, timeFormatter, getResources(), new OnNiceShotListener() {
          @Override public void markNice(ShotModel shot) {
            detailPresenter.markNiceShot(shot);
            sendAnalythics(shot);
          }

          @Override public void unmarkNice(String idShot) {
            detailPresenter.unmarkNiceShot(idShot);
          }
        }, new OnUrlClickListener() {
          @Override public void onClick() {
            detailPresenter.storeClickCount();
            sendOpenlinkAnalythics();
          }
        }, new OnReshootClickListener() {
          @Override public void onReshootClick(ShotModel shot) {
            detailPresenter.reshoot(shot);
          }

          @Override public void onUndoReshootClick(ShotModel shot) {
            detailPresenter.undoReshoot(shot);
          }
        }, new ShareClickListener() {
          @Override public void onClickListener() {
            reshoot();
          }
        }, new ShareClickListener() {
          @Override public void onClickListener() {
            externalShare();
          }
        }, new OnImageClickListener() {
          @Override public void onImageClick(View sharedImage, BaseMessageModel shot) {
            sendImageAnalythics(shot);
            openImage(shot.getImage().getImageUrl());
          }
        }, new ShotClickListener() {
          @Override public void onClick(ShotModel shot) {
            goToNicers(shot.getIdShot());
          }
        }, new ShotClickListener() {
          @Override public void onClick(ShotModel shot) {
            openStreamTimeline(shot.getStreamId());
          }
        }, new ShotClickListener() {
          @Override public void onClick(ShotModel shot) {
            openShot(shot);
          }
        }, timeUtils, new OnShotLongClick() {
          @Override public void onShotLongClick(ShotModel shot) {

          }
        }, new OnOpenShotMenuListener() {
          @Override public void openMenu(ShotModel shot) {

          }
        }, new OnImageLongClickListener() {
          @Override public void onImageLongClick(ShotModel shot) {

          }
        }, new View.OnTouchListener() {
          @Override public boolean onTouch(View v, MotionEvent event) {
            return false;
          }
        });

    setupDetailList();
  }

  private void setupDetailList() {
    linearLayoutManager = new LinearLayoutManager(this);
    detailList.setLayoutManager(linearLayoutManager);
    detailList.setAdapter(detailAdapter);
  }

  private void setupPhotoPicker() {
    if (tmpFiles != null) {
      setupPhotoControllerWithTmpFilesDir();
    } else {
      crashReportTool.logException("Picker must have a temporary files directory.");
    }
  }

  private void setupPhotoControllerWithTmpFilesDir() {
    photoPickerController = new PhotoPickerController.Builder().onActivity(this)
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
            /* no-op */
          }

          @Override public void onCheckIn() {
            detailPresenter.callCheckIn();
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

  public void setupNewShotBarDelegate(final ShotModel shotModel) {
    if (shotModel != null) {
      idUser = shotModel.getIdUser();
    }
    newShotBarViewDelegate =
        new NewShotBarViewDelegate(photoPickerController, replyDraftsButton, feedbackMessage) {
          @Override public void openNewShotView() {
            Intent newShotIntent = PostNewShotActivity.IntentBuilder //
                .from(NewShotDetailActivity.this) //
                .inReplyTo(shotModel.getIdShot(), shotModel.getUsername()) //
                .setStreamData(shotModel.getStreamId(), shotModel.getStreamTitle()).build();
            startActivity(newShotIntent);
          }

          @Override public void openNewShotViewWithImage(File image) {
            Intent newShotIntent = PostNewShotActivity.IntentBuilder //
                .from(NewShotDetailActivity.this) //
                .withImage(image) //
                .inReplyTo(shotModel.getIdShot(), shotModel.getUsername()) //
                .setStreamData(shotModel.getStreamId(), shotModel.getStreamTitle()).build();
            startActivity(newShotIntent);
          }

          @Override public void openEditTopicDialog() {
            /* no-op */
          }
        };
  }

  @Override public void shareShot(ShotModel mainShot) {
    Intent shareIntent = shareManager.shareShotIntent(this, mainShot);
    Intents.maybeStartActivity(this, shareIntent);
  }

  @Override public void showChecked() {
    feedbackMessage.show(getView(), streamChecked);
  }

  @Override public void renderStreamTitle(StreamModel streamModel) {
    streamName.setText(streamModel.getTitle());
  }

  private void onShotVideoClick(String url) {
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
    sendOpenVideoAnalytics();
    BottomYoutubeVideoPlayer bottomYoutubeVideoPlayer = new BottomYoutubeVideoPlayer();
    bottomYoutubeVideoPlayer.setVideoId(videoId);
    bottomYoutubeVideoPlayer.setVideoPlayerCallback(
        new BottomYoutubeVideoPlayer.VideoPlayerCallback() {
          @Override public void onDismiss() {
            /* no-op */
          }
        });

    bottomYoutubeVideoPlayer.show(getSupportFragmentManager(), bottomYoutubeVideoPlayer.getTag());
  }

  private void openImage(String imageUrl) {

    Intent intentForImage = PhotoViewActivity.getIntentForActivity(this, imageUrl);
    startActivity(intentForImage);
  }

  private void openProfile(String idUser) {
    Intent intentForUser = ProfileActivity.getIntent(this, idUser);
    startActivity(intentForUser);
    overridePendingTransition(R.anim.slide_in_up, R.anim.stay);
  }

  private void openShot(ShotModel shotModel) {
    startActivity(NewShotDetailActivity.getIntentForActivity(this, shotModel.getIdShot()));
  }

  private void goToNicers(String idShot) {
    startActivity(NicersActivity.newIntent(this, idShot));
  }

  public void openStreamTimeline(String idStream) {
    startActivity(StreamTimelineActivity.newIntent(this, idStream));
  }

  @Override public void setReplyUsername(String username) {
    replyPlaceholder.setText(getString(R.string.reply_placeholder_pattern, username));
  }

  @Override public void renderShowParents() {
    showParents.setVisibility(View.VISIBLE);
    showParents.setText(showPrevious);
  }

  @Override public void renderHideParents() {
    showParents.setVisibility(View.VISIBLE);
    showParents.setText(hidePrevious);
  }

  @Override public void hideParents() {
    detailAdapter.hideParents();
  }

  @Override public void showParents() {
    detailAdapter.showParents();
    detailList.smoothScrollToPosition(0);
  }

  @Override public void updateMainItem(ShotModel shotModel) {
    detailAdapter.updateMainShot(shotModel);
  }

  @Override public void updateParent(ShotModel shotModel) {
    detailAdapter.updateParents(shotModel);
  }

  @Override public void updatePromoted(ShotModel shotModel) {
    detailAdapter.updatePromotedShot(shotModel);
  }

  @Override public void updateSubscribers(ShotModel shotModel) {
    detailAdapter.updateSubscriberShot(shotModel);
  }

  @Override public void updateOther(ShotModel shotModel) {
    detailAdapter.updateOther(shotModel);
  }

  @Override public void addPromotedShot(ShotModel shotModel) {
    detailAdapter.addPromotedShot(shotModel);
  }

  @Override public void addSubscriberShot(ShotModel shotModel) {
    detailAdapter.addSubscriberShot(shotModel);
  }

  @Override public void addOtherShot(ShotModel shotModel) {
    detailAdapter.addOtherShot(shotModel);
  }

  @Override public void showLoading() {
    if (progressBar != null) {
      progressBar.setVisibility(View.VISIBLE);
    }
  }

  @Override public void hideLoading() {
    if (progressBar != null) {
      progressBar.setVisibility(View.GONE);
    }
  }

  @Override public void showNewShotTextBox() {
    newShotBar.setVisibility(View.VISIBLE);
  }

  @Override public void showViewOnlyTextBox() {
    newShotBar.setVisibility(View.GONE);
  }

  @Override public void showUndoReshootMenu() {
    reshootMenuItem.setVisible(false);
    undoReshootMenuItem.setVisible(true);
  }

  @Override public void showReshootMenu() {
    reshootMenuItem.setVisible(true);
    undoReshootMenuItem.setVisible(false);
  }

  @Override
  public void renderShotDetail(List<PrintableModel> mainShot, List<PrintableModel> promotedItem,
      List<PrintableModel> subscribersItem, List<PrintableModel> basicItems,
      List<PrintableModel> parents) {
    this.mainShot = (ShotModel) mainShot.get(0);
    detailAdapter.renderItems(mainShot, promotedItem, subscribersItem, basicItems, parents);
  }

  @Override public void showError(String errorMessage) {
    feedbackMessage.show(getView(), errorMessage);
  }

  @Override public void openNewShotView() {
    newShotBarViewDelegate.openNewShotView();
  }

  @Override public void pickImage() {
    newShotBarViewDelegate.pickImage();
  }

  @Override public void showHolderOptions() {
    /* no-op */
  }

  @Override public void showPrivateMessageOptions() {
    /* no-op */
  }

  @Override public void openNewShotViewWithImage(File image) {
    newShotBarViewDelegate.openNewShotViewWithImage(image);
  }

  @Override public void openEditTopicDialog() {
    /* no-op */
  }

  @Override public void showDraftsButton() {
    newShotBarViewDelegate.showDraftsButton();
  }

  @Override public void hideDraftsButton() {
    newShotBarViewDelegate.hideDraftsButton();
  }

  @Override public void onStart() {
    super.onStart();
    sendScreenToAnalythics();
  }

  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    if (requestCode == 1) {
      if (grantResults.length > 0) {
        pickImage();
      }
    }
  }

  private void sendCheckinAnalythics() {
    AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
    builder.setContext(getBaseContext());
    builder.setActionId(analyticsActionCheckin);
    builder.setLabelId(analyticsActionCheckin);
    builder.setSource(ctaShotSource);
    builder.setUser(sessionRepository.getCurrentUser());
    if (mainShot != null) {
      builder.setIdStream(mainShot.getStreamId());
      builder.setStreamName(mainShot.getStreamTitle());
      builder.setIdShot(mainShot.getIdShot());
    }
    analyticsTool.analyticsSendAction(builder);
  }

  public void sendImageAnalythics(BaseMessageModel shot) {
    AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
    builder.setContext(getBaseContext());
    builder.setActionId(analyticsActionPhoto);
    builder.setLabelId(analyticsLabelPhoto);
    builder.setSource(shotDetailSource);
    builder.setUser(sessionRepository.getCurrentUser());
    builder.setIdStream(((ShotModel) shot).getStreamId());
    builder.setStreamName(((ShotModel) shot).getStreamTitle());
    analyticsTool.analyticsSendAction(builder);
  }

  @OnClick(R.id.show_action) public void onShowParentsClick() {
    detailPresenter.showParentsPressed();
  }

  @OnClick(R.id.shot_bar_text) public void onReplyClick() {
    newShotBarPresenter.newShotFromTextBox();
  }

  @OnClick(R.id.shot_bar_photo) public void onStartNewShotWithPhoto() {
    newShotBarPresenter.newShotFromImage();
  }

  @OnClick(R.id.shot_bar_drafts) public void openDrafts() {
    startActivity(new Intent(this, DraftsActivity.class));
  }

  private void sendScreenToAnalythics() {
    analyticsTool.analyticsStart(getBaseContext(), analyticsScreenShotDetail);
    AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
    builder.setContext(getBaseContext());
    builder.setActionId(analyticsScreenShotDetail);
    builder.setLabelId(analyticsScreenShotDetail);
    builder.setSource(analyticsScreenShotDetail);
    if (sessionRepository.getCurrentUser() != null) {
      builder.setUser(sessionRepository.getCurrentUser());
      builder.setStreamName(sessionRepository.getCurrentUser().getWatchingStreamTitle());
    }
    analyticsTool.analyticsSendAction(builder);
  }

  private void sendExternalShareAnalythics() {
    AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
    builder.setContext(getBaseContext());
    builder.setActionId(analyticsActionExternalShare);
    builder.setLabelId(analyticsLabelExternalShare);
    builder.setSource(shotDetailSource);
    builder.setUser(sessionRepository.getCurrentUser());
    builder.setIdTargetUser(idUser);
    analyticsTool.analyticsSendAction(builder);
  }

  private void sendShareShotAnalythics() {
    AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
    builder.setContext(getBaseContext());
    builder.setActionId(analyticsActionShareShot);
    builder.setLabelId(analyticsLabelShareShot);
    builder.setSource(shotDetailSource);
    builder.setUser(sessionRepository.getCurrentUser());
    builder.setIdTargetUser(idUser);
    if (mainShot != null) {
      builder.setTargetUsername(mainShot.getUsername());
      builder.setIdStream(mainShot.getStreamId());
      builder.setStreamName(mainShot.getStreamTitle());
    }
    analyticsTool.analyticsSendAction(builder);
  }

  private void sendOpenVideoAnalytics() {
    AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
    builder.setContext(getBaseContext());
    builder.setActionId(analyticsActionOpenVideo);
    builder.setLabelId(analyticsLabelOpenVideo);
    builder.setSource(shotDetailSource);
    builder.setUser(sessionRepository.getCurrentUser());
    builder.setIdTargetUser(idUser);
    if (mainShot != null) {
      builder.setTargetUsername(mainShot.getUsername());
      builder.setIdStream(mainShot.getStreamId());
      builder.setStreamName(mainShot.getStreamTitle());
    }
    analyticsTool.analyticsSendAction(builder);
  }

  private void sendOpenlinkAnalythics() {
    AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
    builder.setContext(getBaseContext());
    builder.setActionId(analyticsActionOpenLink);
    builder.setLabelId(analyticsLabelOpenlink);
    builder.setSource(shotDetailSource);
    builder.setUser(sessionRepository.getCurrentUser());
    analyticsTool.analyticsSendAction(builder);
  }

  private void sendAnalythics(ShotModel shot) {
    AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
    builder.setContext(getBaseContext());
    builder.setActionId(analyticsActionNice);
    builder.setLabelId(analyticsLabelNice);
    builder.setSource(shotDetailSource);
    builder.setUser(sessionRepository.getCurrentUser());
    builder.setIdTargetUser(shot.getIdUser());
    builder.setTargetUsername(shot.getUsername());
    builder.setIdStream(shot.getStreamId());
    builder.setStreamName(shot.getStreamTitle());
    analyticsTool.analyticsSendAction(builder);
  }

  @OnClick(R.id.stream_name) public void goToTimeline() {
    openStreamTimeline(mainShot.getStreamId());
  }
}
