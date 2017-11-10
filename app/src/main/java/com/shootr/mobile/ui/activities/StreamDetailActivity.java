package com.shootr.mobile.ui.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.eftimoff.androidplayer.Player;
import com.eftimoff.androidplayer.actions.property.PropertyAction;
import com.shootr.mobile.R;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.ui.adapters.StreamDetailAdapter;
import com.shootr.mobile.ui.adapters.listeners.OnFollowUnfollowListener;
import com.shootr.mobile.ui.adapters.listeners.OnFollowUnfollowStreamListener;
import com.shootr.mobile.ui.adapters.listeners.OnUserClickListener;
import com.shootr.mobile.ui.base.BaseActivity;
import com.shootr.mobile.ui.model.StreamModel;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.presenter.StreamDetailPresenter;
import com.shootr.mobile.ui.views.StreamDetailView;
import com.shootr.mobile.util.AnalyticsTool;
import com.shootr.mobile.util.CrashReportTool;
import com.shootr.mobile.util.FeedbackMessage;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.InitialsLoader;
import com.shootr.mobile.util.Intents;
import com.shootr.mobile.util.MenuItemValueHolder;
import com.shootr.mobile.util.NumberFormatUtil;
import com.shootr.mobile.util.ShareManager;
import com.shootr.mobile.util.WritePermissionManager;
import com.sloydev.collapsingavatartoolbar.CollapsingAvatarToolbar;
import java.util.List;
import javax.inject.Inject;

public class StreamDetailActivity extends BaseActivity implements StreamDetailView {

  private static final int REQUEST_EDIT_STREAM = 3;

  private static final String EXTRA_STREAM_ID = "streamId";
  private static final int NO_CONTRIBUTORS = 0;
  public static final String EXTRA_STREAM_TITLE = "title";
  private int counterToolbarPrintTimes = 0;

  @BindView(R.id.toolbar) Toolbar toolbar;
  @BindView(R.id.toolbar_dummy_content) View toolbarDummyContent;
  @BindView(R.id.collapsing_avatar_toolbar) CollapsingAvatarToolbar collapsingAvatarToolbar;
  @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout collapsingToolbar;
  @BindView(R.id.stream_title_container) View streamTitleContainer;
  @BindView(R.id.cat_avatar) View streamPictureContainer;
  @BindView(R.id.stream_avatar) ImageView streamPicture;
  @BindView(R.id.stream_avatar_without_text) ImageView streamPictureWithoutText;
  @BindView(R.id.image_toolbar_detail_stream) ImageView toolbarImage;
  @BindView(R.id.stream_photo_edit_loading) View streamPictureLoading;
  @BindView(R.id.cat_title) TextView streamTitle;
  @BindView(R.id.subtitle) TextView streamSubtitle;
  @BindView(R.id.blurLayout) FrameLayout blurLayout;
  @BindView(R.id.appbar) AppBarLayout appBarLayout;
  @BindView(R.id.stream_verified) ImageView verifiedStream;

  @BindView(R.id.list) RecyclerView recyclerView;
  @BindView(R.id.loading_progress) View progressView;

  @BindString(R.string.shared_stream_notification) String streamNotification;
  @BindString(R.string.analytics_screen_stream_detail) String analyticsScreenStreamDetail;
  @BindString(R.string.analytics_action_follow) String analyticsActionFollow;
  @BindString(R.string.analytics_label_follow) String analyticsLabelFollow;
  @BindString(R.string.analytics_action_mute) String analyticsActionMute;
  @BindString(R.string.analytics_label_mute) String analyticsLabelMute;
  @BindString(R.string.analytics_action_external_share_stream) String analyticsActionExternalShare;
  @BindString(R.string.analytics_label_external_share_stream) String analyticsLabelExternalShare;
  @BindString(R.string.analytics_source_stream_detail) String streamDetailSource;

  @Inject ImageLoader imageLoader;
  @Inject StreamDetailPresenter streamDetailPresenter;
  @Inject ShareManager shareManager;
  @Inject FeedbackMessage feedbackMessage;
  @Inject AnalyticsTool analyticsTool;
  @Inject WritePermissionManager writePermissionManager;
  @Inject CrashReportTool crashReportTool;
  @Inject InitialsLoader initialsLoader;
  @Inject SessionRepository sessionRepository;
  @Inject NumberFormatUtil numberFormatUtil;

  private StreamDetailAdapter adapter;
  private MenuItemValueHolder editMenuItem = new MenuItemValueHolder();
  private MenuItemValueHolder dataInfoMenuItem = new MenuItemValueHolder();
  private MenuItemValueHolder removeMenuItem = new MenuItemValueHolder();
  private MenuItemValueHolder restoreMenuItem = new MenuItemValueHolder();
  private String idStream;

  public static Intent getIntent(Context context, String streamId) {
    Intent intent = new Intent(context, StreamDetailActivity.class);
    intent.putExtra(EXTRA_STREAM_ID, streamId);
    return intent;
  }

  @Override protected int getLayoutResource() {
    return R.layout.activity_stream_detail;
  }

  @Override protected void initializeViews(Bundle savedInstanceState) {
    ButterKnife.bind(this);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle(null);
    writePermissionManager.init(this);
    setupAdapter();

    setupAnimation();
  }

  private void sendAnalythics() {
    analyticsTool.analyticsStart(getBaseContext(), analyticsScreenStreamDetail);
    AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
    builder.setContext(getBaseContext());
    builder.setActionId(analyticsScreenStreamDetail);
    builder.setLabelId(analyticsScreenStreamDetail);
    builder.setSource(analyticsScreenStreamDetail);
    builder.setIdStream(idStream);
    if (sessionRepository.getCurrentUser() != null) {
      builder.setUser(sessionRepository.getCurrentUser());
      builder.setStreamName(sessionRepository.getCurrentUser().getWatchingStreamTitle());
    }
    analyticsTool.analyticsSendAction(builder);
  }

  private void setupAnimation() {
    final PropertyAction headerAction = PropertyAction.newPropertyAction(appBarLayout).
        interpolator(new DecelerateInterpolator()).
        translationY(-700).
        duration(600).
        alpha(0.4f).
        build();
    final PropertyAction bottomAction = PropertyAction.newPropertyAction(recyclerView).
        translationY(500).
        duration(200).
        alpha(0f).
        build();

    Player.init().
        animate(headerAction).
        then().
        animate(bottomAction).
        play();
  }

  private void setupAdapter() {
    adapter = new StreamDetailAdapter(imageLoader, //
        new View.OnClickListener() {
          @Override public void onClick(View v) {
            streamDetailPresenter.clickAuthor();
          }
        }, // author
        new View.OnClickListener() {
          @Override public void onClick(View view) {
            streamDetailPresenter.contributorsClicked();
          }
        }, new View.OnClickListener() {
      @Override public void onClick(View v) {
        streamDetailPresenter.clickMedia();
      }
    }, // media
        new View.OnClickListener() {
          @Override public void onClick(View view) {
            startActivity(
                UserFollowsContainerActivity.getIntentStreamsFollowersIntent(getBaseContext(),
                    idStream));
          }
        }, new CompoundButton.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if (isChecked) {
          streamDetailPresenter.onMuteChecked();
          sendMuteAnalytics();
        } else {
          streamDetailPresenter.onUnmuteChecked();
        }
      }
    }, new View.OnClickListener() {
      @Override public void onClick(View view) {
        streamDetailPresenter.clickAllParticipants();
      }
    }, // all participants
        new OnUserClickListener() {
          @Override public void onUserClick(String idUser) {
            navigateToUser(idUser);
          }
        }, // participant
        new OnFollowUnfollowListener() {
          @Override public void onFollow(UserModel user) {
            streamDetailPresenter.follow(user.getIdUser());
            sendAnalythics(user);
          }

          @Override public void onUnfollow(final UserModel user) {
            new AlertDialog.Builder(StreamDetailActivity.this).setMessage(
                String.format(getString(R.string.unfollow_dialog_message), user.getUsername()))
                .setPositiveButton(getString(R.string.unfollow_dialog_yes),
                    new DialogInterface.OnClickListener() {
                      @Override public void onClick(DialogInterface dialog, int which) {
                        streamDetailPresenter.unfollow(user.getIdUser());
                      }
                    })
                .setNegativeButton(getString(R.string.unfollow_dialog_no), null)
                .create()
                .show();
          }
        }, new OnFollowUnfollowStreamListener() {
      @Override public void onFollow(StreamModel stream) {
        streamDetailPresenter.addStreamAsFollowing();
        adapter.setButtonFollowingState(true);
      }

      @Override public void onUnfollow(StreamModel stream) {
        new AlertDialog.Builder(StreamDetailActivity.this).setMessage(
            String.format(getString(R.string.unfollowstream_dialog_message), stream.getTitle()))
            .setPositiveButton(getString(R.string.unfollowstream_dialog_yes),
                new DialogInterface.OnClickListener() {
                  @Override public void onClick(DialogInterface dialog, int which) {
                    streamDetailPresenter.unfollowStream();
                    adapter.setButtonFollowingState(false);
                  }
                })
            .setNegativeButton(getString(R.string.unfollowstream_dialog_no), null)
            .create()
            .show();
      }
    }, numberFormatUtil); //follow
    recyclerView.setAdapter(adapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
  }

  private void sendMuteAnalytics() {
    AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
    builder.setContext(getBaseContext());
    builder.setActionId(analyticsActionMute);
    builder.setLabelId(analyticsLabelMute);
    builder.setSource(streamDetailSource);
    builder.setUser(sessionRepository.getCurrentUser());
    analyticsTool.analyticsSendAction(builder);
  }

  private void sendAnalythics(UserModel user) {
    AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
    builder.setContext(getBaseContext());
    builder.setActionId(analyticsActionFollow);
    builder.setLabelId(analyticsLabelFollow);
    builder.setSource(streamDetailSource);
    builder.setUser(sessionRepository.getCurrentUser());
    builder.setIsStrategic(user.isStrategic());
    builder.setIdTargetUser(user.getIdUser());
    builder.setTargetUsername(user.getUsername());
    analyticsTool.analyticsSendAction(builder);
    analyticsTool.appsFlyerSendAction(builder);
  }

  @Override protected void initializePresenter() {
    idStream = getIntent().getStringExtra(EXTRA_STREAM_ID);
    streamDetailPresenter.initialize(this, idStream);
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.stream, menu);
    editMenuItem.bindRealMenuItem(menu.findItem(R.id.stream_detail_menu_edit));
    dataInfoMenuItem.bindRealMenuItem(menu.findItem(R.id.stream_detail_menu_data_info));
    removeMenuItem.bindRealMenuItem(menu.findItem(R.id.stream_detail_menu_remove));
    restoreMenuItem.bindRealMenuItem(menu.findItem(R.id.stream_detail_menu_restore));
    dataInfoMenuItem.setVisible(true);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        finish();
        return true;
      case R.id.stream_detail_menu_edit:
        streamDetailPresenter.editStreamInfo();
        return true;
      case R.id.stream_detail_menu_data_info:
        streamDetailPresenter.dataInfoClicked();
        return true;
      case R.id.stream_detail_share_shot_via:
        shareVia();
        return true;
      case R.id.stream_detail_share_shot:
        streamDetailPresenter.shareStreamViaShootr();
        return true;
      case R.id.stream_detail_menu_remove:
        streamDetailPresenter.removeStream();
        return true;
      case R.id.stream_detail_menu_restore:
        streamDetailPresenter.restoreStream();
        return true;
      default:
        return false;
    }
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == REQUEST_EDIT_STREAM && resultCode == RESULT_OK) {
      streamDetailPresenter.resultFromEditStreamInfo();
    } else if (requestCode == REQUEST_EDIT_STREAM
        && resultCode == NewStreamActivity.RESULT_EXIT_STREAM) {
      setResult(NewStreamActivity.RESULT_EXIT_STREAM);
      finish();
    }
  }

  private void shareVia() {
    streamDetailPresenter.shareStreamVia();
    AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
    builder.setContext(getBaseContext());
    builder.setActionId(analyticsActionExternalShare);
    builder.setLabelId(analyticsLabelExternalShare);
    builder.setSource(streamDetailSource);
    builder.setUser(sessionRepository.getCurrentUser());
    analyticsTool.analyticsSendAction(builder);
  }

  @Override protected void onResume() {
    super.onResume();
    streamDetailPresenter.resume();
  }

  @Override protected void onPause() {
    super.onPause();
    streamDetailPresenter.pause();
  }

  private void setTitleResultForPreviousActivity(String title) {
    setResult(RESULT_OK, new Intent().putExtra(EXTRA_STREAM_TITLE, title));
  }

  //region Edit photo
  @OnClick(R.id.cat_avatar) public void onPhotoClick() {
    streamDetailPresenter.photoClick();
  }

  //endregion

  //region View methods
  @Override public void setStreamTitle(String title) {
    streamTitle.setText(title);
    setTitleResultForPreviousActivity(title);
  }

  @Override public void setStreamVerified(boolean isVerified) {
    if (isVerified) {
      verifiedStream.setVisibility(View.VISIBLE);
    } else {
      verifiedStream.setVisibility(View.GONE);
    }
  }

  @Override public void setStreamAuthor(String author) {
    adapter.setAuthorName(author);
  }

  @Override public void setStream(StreamModel streamModel) {
    adapter.setStream(streamModel);
  }

  @Override public void setStreamPicture(String picture) {
    imageLoader.loadStreamPicture(picture, streamPicture, new ImageLoader.CompletedCallback() {
      @Override public void onCompleted(Bitmap bitmap) {
        changeToolbarColor(bitmap);
      }
    });
  }

  @Override public void setupStreamInitials(StreamModel streamModel) {
    String initials = initialsLoader.getLetters(streamModel.getTitle());
    int backgroundColor = initialsLoader.getColorForLetters(initials);
    TextDrawable textDrawable = initialsLoader.getTextDrawable(initials, backgroundColor);
    streamPictureWithoutText.setImageDrawable(textDrawable);
    changeStatusBarColor(backgroundColor);
  }

  private void changeStatusBarColor(int backgroundColor) {
    int darkColor = getDarkColor(backgroundColor);
    blurLayout.setBackgroundColor(darkColor);
    collapsingToolbar.setContentScrimColor(darkColor);
    collapsingToolbar.setStatusBarScrimColor(darkColor);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      int color = getDarkColor(darkColor);
      Window window = getWindow();
      window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      window.setStatusBarColor(color);
    }
  }

  private int getDarkColor(int color) {
    float[] hsv = new float[3];
    Color.colorToHSV(color, hsv);
    hsv[2] *= 0.9f;
    return Color.HSVToColor(hsv);
  }

  @Override public void loadBlurStreamPicture(String picture) {
    imageLoader.loadBlurStreamPicture(picture, toolbarImage,
        new RequestListener<String, GlideDrawable>() {
          @Override
          public boolean onException(Exception e, String model, Target<GlideDrawable> target,
              boolean isFirstResource) {
            return false;
          }

          @Override public boolean onResourceReady(GlideDrawable resource, String model,
              Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {

            if (counterToolbarPrintTimes == 0) {
              counterToolbarPrintTimes++;
            }
            return false;
          }
        });
  }

  @Override public void showPicture() {
    streamPicture.setVisibility(View.VISIBLE);
  }

  @Override public void hideNoTextPicture() {
    streamPictureWithoutText.setVisibility(View.GONE);
  }

  @Override public void hidePicture() {
    streamPicture.setVisibility(View.GONE);
  }

  @Override public void showNoTextPicture() {
    streamPictureWithoutText.setVisibility(View.VISIBLE);
  }

  @Override public void showStreamFollower(int streamFollowers) {
    adapter.setFollowersNumber(streamFollowers);
  }

  private void changeToolbarColor(Bitmap bitmap) {
    try {
      blurLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.gray_40));
      Palette palette = Palette.from(bitmap).generate();
      collapsingToolbar.setContentScrimColor(getDarkVibrantColor(palette));
      collapsingToolbar.setStatusBarScrimColor(getDarkVibrantColor(palette));
      changeStatusBarColor(palette);
    } catch (IllegalArgumentException | IllegalStateException exception) {
      crashReportTool.logException("IllegalArgumentException. Bitmap is not valid");
    }
  }

  private int getDarkVibrantColor(Palette palette) {
    return palette.getDarkVibrantColor(getResources().getColor(R.color.gray_material));
  }

  private void changeStatusBarColor(Palette palette) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      Window window = getWindow();
      window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      window.setStatusBarColor(getDarkVibrantColor(palette));
    }
  }

  @Override public void showEditPicturePlaceholder() {
    streamPicture.setImageResource(R.drawable.ic_stream_picture_edit);
  }

  @Override public void zoomPhoto(String picture) {
    Bundle animationBundle = ActivityOptionsCompat.makeScaleUpAnimation(streamPictureContainer,
        streamPictureContainer.getLeft(), streamPictureContainer.getTop(),
        streamPictureContainer.getWidth(), streamPictureContainer.getBottom()).toBundle();
    Intent photoIntent = PhotoViewActivity.getIntentForActivity(this, picture);
    ActivityCompat.startActivity(this, photoIntent, animationBundle);
  }

  @Override public void setWatchers(List<UserModel> watchers) {
    adapter.setParticipants(watchers);
    adapter.notifyDataSetChanged();
  }

  @Override public void navigateToEditStream(String idStream) {
    Intent editIntent =
        new Intent(this, NewStreamActivity.class).putExtra(NewStreamActivity.KEY_STREAM_ID,
            idStream);
    startActivityForResult(editIntent, REQUEST_EDIT_STREAM);
  }

  @Override public void navigateToUser(String userId) {
    Intent userProfileIntent = ProfileActivity.getIntent(this, userId);
    startActivity(userProfileIntent);
  }

  @Override public void showDetail() {
    recyclerView.setVisibility(View.VISIBLE);
  }

  @Override public void showEditStreamButton() {
    editMenuItem.setVisible(true);
    collapsingAvatarToolbar.setCollapseChangedListener(
        new CollapsingAvatarToolbar.CollapseChangedListener() {

          boolean needsToAdjustPadding = true;

          @Override public void onCollapseChanged(float collapseProgress) {
            if (collapseProgress == 0f) {
              setPaddingRight(0);
              needsToAdjustPadding = true;
            } else if (needsToAdjustPadding) {
              int paddingRight = getExtraPaddingForMenu();
              setPaddingRight(paddingRight);
              needsToAdjustPadding = false;
            }
          }

          protected int getExtraPaddingForMenu() {
            int limit = toolbarDummyContent.getRight();
            int currRight = streamTitleContainer.getRight();
            return currRight - limit;
          }

          protected void setPaddingRight(int paddingRight) {
            streamTitleContainer.setPadding(0, 0, paddingRight, 0);
          }
        });
  }

  @Override public void navigateToMedia(String idStream, Integer streamMediaCount) {
    Intent intent = StreamMediaActivity.newIntent(this, idStream, streamMediaCount);
    this.startActivity(intent);
  }

  @Override public void setStreamDescription(String description) {
    adapter.setDescription(description);
  }

  @Override public void hideStreamDescription() {
    adapter.setDescription(null);
  }

  @Override public void showAllParticipantsButton() {
    adapter.showAllParticipants();
  }

  @Override public void setFollowingNumber(Integer numberOfFollowing, Integer totalWatchers) {
    adapter.setFollowingNumber(numberOfFollowing, totalWatchers);
  }

  @Override public void showStreamShared() {
    feedbackMessage.show(getView(), streamNotification);
  }

  @Override public void shareStreamVia(StreamModel stream) {
    Intent shareIntent = shareManager.shareStreamIntent(this, stream);
    Intents.maybeStartActivity(this, shareIntent);
  }

  @Override public void goToAllParticipants(String idStream) {
    startActivity(AllParticipantsActivity.newIntent(this, idStream));
  }

  @Override public void setMuteStatus(Boolean isChecked) {
    adapter.setMuteStatus(isChecked);
  }

  @Override public void setFollowingStream(Boolean isFollowing) {
    adapter.setFollowing(isFollowing);
  }

  @Override public void goToStreamDataInfo(StreamModel streamModel) {
    Intent intent = new Intent(this, StreamDataInfoActivity.class);
    intent.putExtra(StreamDataInfoActivity.ARGUMENT_PARTICIPANTS_NUMBER,
        streamModel.getHistoricWatchers());
    intent.putExtra(StreamDataInfoActivity.ARGUMENT_SHOTS_NUMBER, streamModel.getTotalShots());
    intent.putExtra(StreamDataInfoActivity.ARGUMENT_FAVORITES_NUMBER,
        streamModel.getTotalFollowers());
    intent.putExtra(StreamDataInfoActivity.ARGUMENT_UNIQUE_SHOTS, streamModel.getUniqueShots());
    intent.putExtra(StreamDataInfoActivity.ARGUMENT_STREAM_NAME, streamModel.getTitle());
    startActivity(intent);
  }

  @Override public void goToContributorsActivityAsHolder(String idStream) {
    startActivity(ContributorsActivity.newIntent(this, idStream, true));
  }

  @Override public void goToContributorsActivity(String idStream) {
    startActivity(ContributorsActivity.newIntent(this, idStream, false));
  }

  @Override public void hideContributorsNumber(boolean isStreamAuthor) {
    adapter.setContributorsNumber(NO_CONTRIBUTORS, isStreamAuthor);
  }

  @Override public void showContributorsNumber(Integer contributorsNumber, boolean isStreamAuthor) {
    adapter.setContributorsNumber(contributorsNumber, isStreamAuthor);
  }

  @Override public void showRestoreStreamButton() {
    restoreMenuItem.setVisible(true);
  }

  @Override public void showRemoveStreamButton() {
    removeMenuItem.setVisible(true);
  }

  @Override public void askRemoveStreamConfirmation() {
    new AlertDialog.Builder(this).setMessage(R.string.remove_stream_confirmation)
        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
            streamDetailPresenter.confirmRemoveStream();
          }
        })
        .setNegativeButton(R.string.cancel, null)
        .show();
  }

  @Override public void hideRestoreButton() {
    restoreMenuItem.setVisible(false);
  }

  @Override public void hideRemoveButton() {
    removeMenuItem.setVisible(false);
  }

  @Override public void showRestoreStreamFeedback() {
    feedbackMessage.show(getView(), R.string.stream_restored_feedback);
  }

  @Override public void showRemovedFeedback() {
    feedbackMessage.showForever(getView(), R.string.stream_removed_feedback,
        R.string.restore_stream_snackbar_action, new View.OnClickListener() {
          @Override public void onClick(View v) {
            streamDetailPresenter.restoreStream();
          }
        });
  }

  @Override public void showLoading() {
    progressView.setVisibility(View.VISIBLE);
  }

  @Override public void hideLoading() {
    progressView.setVisibility(View.GONE);
  }

  @Override public void showError(String message) {
    feedbackMessage.show(getView(), message);
  }
  //endregion

  @Override public void onStart() {
    super.onStart();
    sendAnalythics();
  }
}
