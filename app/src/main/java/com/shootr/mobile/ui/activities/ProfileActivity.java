package com.shootr.mobile.ui.activities;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.amulyakhare.textdrawable.TextDrawable;
import com.cocosw.bottomsheet.BottomSheet;
import com.github.clans.fab.FloatingActionMenu;
import com.shootr.mobile.BuildConfig;
import com.shootr.mobile.R;
import com.shootr.mobile.domain.model.shot.HighlightedShot;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.utils.UserFollowingRelationship;
import com.shootr.mobile.ui.activities.registro.LoginSelectionActivity;
import com.shootr.mobile.ui.adapters.ProfileShotAdapter;
import com.shootr.mobile.ui.adapters.UserListAdapter;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnNiceShotListener;
import com.shootr.mobile.ui.adapters.listeners.OnReshootClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnShotLongClick;
import com.shootr.mobile.ui.adapters.listeners.OnUrlClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnUserClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnUsernameClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnVideoClickListener;
import com.shootr.mobile.ui.adapters.listeners.ShotClickListener;
import com.shootr.mobile.ui.base.BaseActivity;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.presenter.ProfilePresenter;
import com.shootr.mobile.ui.presenter.ReportShotPresenter;
import com.shootr.mobile.ui.presenter.SuggestedPeoplePresenter;
import com.shootr.mobile.ui.views.ProfileView;
import com.shootr.mobile.ui.views.ReportShotView;
import com.shootr.mobile.ui.views.SuggestedPeopleView;
import com.shootr.mobile.ui.widgets.FollowButton;
import com.shootr.mobile.ui.widgets.PreCachingLayoutManager;
import com.shootr.mobile.ui.widgets.ScrollableAppLayout;
import com.shootr.mobile.ui.widgets.SuggestedPeopleListView;
import com.shootr.mobile.util.AnalyticsTool;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.BackStackHandler;
import com.shootr.mobile.util.Clipboard;
import com.shootr.mobile.util.CustomContextMenu;
import com.shootr.mobile.util.FeedbackMessage;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.InitialsLoader;
import com.shootr.mobile.util.IntentFactory;
import com.shootr.mobile.util.Intents;
import com.shootr.mobile.util.MenuItemValueHolder;
import com.shootr.mobile.util.NumberFormatUtil;
import com.shootr.mobile.util.ShareManager;
import com.shootr.mobile.util.WritePermissionManager;
import java.io.File;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class ProfileActivity extends BaseActivity
    implements ProfileView, SuggestedPeopleView, UserListAdapter.FollowUnfollowAdapterCallback,
    ReportShotView {

  public static final String EXTRA_USER = "user";
  public static final String EXTRA_USERNAME = "username";
  public static final String EURO = " €";
  public static final int LOGOUT_DISMISS_DELAY = 1500;

  private static final int REQUEST_CHOOSE_PHOTO = 1;
  private static final int REQUEST_NEW_STREAM = 3;
  private static final int REQUEST_TAKE_PHOTO = 2;
  private static final int REQUEST_CROP_PHOTO = 88;

  //region injected
  @BindView(R.id.profile_name) TextView nameTextView;
  @BindView(R.id.profile_bio) TextView bioTextView;
  @BindView(R.id.profile_website) TextView websiteTextView;
  @BindView(R.id.profile_avatar) ImageView avatarImageView;
  @BindView(R.id.toolbar) Toolbar toolbar;
  @BindView(R.id.toolbar_layout) CollapsingToolbarLayout collapsingToolbarLayout;
  @BindView(R.id.channel_button) FloatingActionButton channelButton;
  @BindView(R.id.user_muted) ImageView userMuted;
  @BindView(R.id.balance_tab_container) FrameLayout balanceContainer;
  @BindView(R.id.profile_marks_followers) TextView followersTextView;
  @BindView(R.id.profile_marks_following) TextView followingTextView;

  @BindView(R.id.profile_follow_button) FollowButton followButton;

  @BindView(R.id.reshoots_people_title) TextView reshootsHeader;
  @BindView(R.id.profile_shots_list) RecyclerView shotsList;

  @BindView(R.id.profile_all_shots_container) View allShotContainer;

  @BindView(R.id.profile_avatar_loading) ProgressBar avatarLoadingView;
  @BindView(R.id.profile_loading) ProgressBar progressBar;

  @BindView(R.id.profile_suggested_people) SuggestedPeopleListView suggestedPeopleListView;

  @BindView(R.id.profile_user_verified) ImageView userVerified;
  @BindView(R.id.profile_container) CoordinatorLayout profileContainer;
  @BindView(R.id.fab_menu) FloatingActionMenu floatingMenu;
  @BindView(R.id.profile_detail_container) NestedScrollView nestedScrollView;
  @BindView(R.id.app_bar) ScrollableAppLayout appBarLayout;
  @BindView(R.id.stream_number) TextView streamNumber;
  @BindView(R.id.balance_number) TextView balanceNumber;

  @BindString(R.string.report_base_url) String reportBaseUrl;
  @BindString(R.string.analytics_screen_me) String analyticsScreenMe;
  @BindString(R.string.analytics_screen_userProfile) String analyticsScreenUserProfile;
  @BindString(R.string.analytics_action_follow) String analyticsActionFollow;
  @BindString(R.string.analytics_label_follow) String analyticsLabelFollow;
  @BindString(R.string.analytics_action_nice) String analyticsActionNice;
  @BindString(R.string.analytics_label_nice) String analyticsLabelNice;
  @BindString(R.string.analytics_action_share_shot) String analyticsActionShareShot;
  @BindString(R.string.analytics_label_share_shot) String analyticsLabelShareShot;
  @BindString(R.string.analytics_action_external_share) String analyticsActionExternalShare;
  @BindString(R.string.analytics_label_external_share) String analyticsLabelExternalShare;
  @BindString(R.string.analytics_source_profile) String profileSource;
  @BindString(R.string.analytics_source_friends) String whoToFollowSource;
  @BindString(R.string.block_unblock_user) String unblockUser;
  @BindString(R.string.report_context_menu_block) String blockUser;
  @BindString(R.string.context_menu_mute) String muteUser;
  @BindString(R.string.context_menu_unmute) String unmuteUser;

  @Inject ImageLoader imageLoader;
  @Inject IntentFactory intentFactory;
  @Inject ShareManager shareManager;
  @Inject FeedbackMessage feedbackMessage;
  @Inject ProfilePresenter profilePresenter;
  @Inject SuggestedPeoplePresenter suggestedPeoplePresenter;
  @Inject ReportShotPresenter reportShotPresenter;
  @Inject AndroidTimeUtils timeUtils;
  @Inject AnalyticsTool analyticsTool;
  @Inject WritePermissionManager writePermissionManager;
  @Inject NumberFormatUtil numberFormatUtil;
  @Inject BackStackHandler backStackHandler;
  @Inject SessionRepository sessionRepository;
  @Inject InitialsLoader initialsLoader;

  //endregion

  private ProfileShotAdapter profileShotsAdapter;
  private ProgressDialog progress;
  private MenuItemValueHolder logoutMenuItem = new MenuItemValueHolder();
  private MenuItemValueHolder supportMenuItem = new MenuItemValueHolder();
  private MenuItemValueHolder shareProfileMenuItem = new MenuItemValueHolder();
  private MenuItemValueHolder changePasswordMenuItem = new MenuItemValueHolder();
  private MenuItemValueHolder blockUserMenuItem = new MenuItemValueHolder();
  private MenuItemValueHolder reportUserMenuItem = new MenuItemValueHolder();
  private MenuItemValueHolder muteUserMenuItem = new MenuItemValueHolder();
  private MenuItemValueHolder settingsMenuItem = new MenuItemValueHolder();
  private UserListAdapter suggestedPeopleAdapter;
  private PreCachingLayoutManager preCachingLayoutManager;

  private String idUser;
  private String username;

  public static Intent getIntent(Context context, String idUser) {
    Intent i = new Intent(context, ProfileActivity.class);
    i.putExtra(EXTRA_USER, idUser);
    return i;
  }

  public static Intent getIntentFromSearch(Context context, String idUser) {
    Intent i = new Intent(context, ProfileActivity.class);
    i.putExtra(EXTRA_USER, idUser);
    return i;
  }

  public static Intent getIntentWithUsername(Context context, String username) {
    Intent i = new Intent(context, ProfileActivity.class);
    i.putExtra(EXTRA_USERNAME, username);
    return i;
  }

  @Override protected int getLayoutResource() {
    return R.layout.activity_profile;
  }

  @Override protected void initializeViews(Bundle savedInstanceState) {
    final Context context = this;
    ButterKnife.bind(this, getView());
    writePermissionManager.init(this);
    setupToolbar();
    collapsingToolbarLayout.setTitle(" ");
    idUser = getIntent().getStringExtra(EXTRA_USER);
    username = getIntent().getStringExtra(EXTRA_USERNAME);
    OnAvatarClickListener avatarClickListener = new OnAvatarClickListener() {
      @Override public void onAvatarClick(String userId, View avatarView) {
        onShotAvatarClick(avatarView);
      }
    };

    OnUsernameClickListener onUsernameClickListener = new OnUsernameClickListener() {
      @Override public void onUsernameClick(String username) {
        goToUserProfile(username);
      }
    };

    OnVideoClickListener videoClickListener = new OnVideoClickListener() {
      @Override public void onVideoClick(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
      }
    };

    OnNiceShotListener onNiceShotListener = new OnNiceShotListener() {
      @Override public void markNice(ShotModel shot) {
        profilePresenter.markNiceShot(shot.getIdShot());
        sendAnalytics(shot);
      }

      @Override public void unmarkNice(String idShot) {
        profilePresenter.unmarkNiceShot(idShot);
      }
    };

    suggestedPeopleListView.setAdapter(getSuggestedPeopleAdapter());
    suggestedPeopleListView.setOnUserClickListener(new OnUserClickListener() {
      @Override public void onUserClick(String idUser) {
        Intent suggestedUserIntent = ProfileActivity.getIntent(context, idUser);
        startActivity(suggestedUserIntent);
      }
    });

    setupProfileShots(avatarClickListener, onUsernameClickListener, videoClickListener,
        onNiceShotListener);

    floatingMenu.setClosedOnTouchOutside(true);
    appBarLayout.collapseToolbar();
  }

  private void setupProfileShots(final OnAvatarClickListener avatarClickListener,
      final OnUsernameClickListener onUsernameClickListener,
      final OnVideoClickListener videoClickListener, final OnNiceShotListener onNiceShotListener) {

    profileShotsAdapter =
        new ProfileShotAdapter(imageLoader, avatarClickListener, videoClickListener,
            onNiceShotListener, timeUtils, new ShotClickListener() {
          @Override public void onClick(ShotModel shot) {
            openShot(shot);
          }
        }, new OnShotLongClick() {
          @Override public void onShotLongClick(ShotModel shot) {
            reportShotPresenter.onShotLongPressed(shot);
          }
        }, new OnUrlClickListener() {
          @Override public void onClick() {
            /* no-op */
          }
        }, new OnReshootClickListener() {
          @Override public void onReshootClick(ShotModel shot) {
            profilePresenter.reshoot(shot);
            sendShareShotAnalythics(shot);
          }

          @Override public void onUndoReshootClick(ShotModel shot) {
            profilePresenter.undoReshoot(shot);
          }
        }, numberFormatUtil);

    preCachingLayoutManager = new PreCachingLayoutManager(this);
    shotsList.setLayoutManager(preCachingLayoutManager);
    shotsList.setHasFixedSize(false);
    shotsList.setItemAnimator(new DefaultItemAnimator() {
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

    shotsList.setAdapter(profileShotsAdapter);
  }

  private void sendAnalytics(ShotModel shot) {
    AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
    builder.setContext(getBaseContext());
    builder.setActionId(analyticsActionNice);
    builder.setLabelId(analyticsLabelNice);
    builder.setSource(profileSource);
    builder.setUser(sessionRepository.getCurrentUser());
    builder.setIdTargetUser(shot.getIdUser());
    builder.setTargetUsername(shot.getUsername());
    analyticsTool.analyticsSendAction(builder);
  }

  @Override public void resetTimelineAdapter() {
    profileShotsAdapter.notifyDataSetChanged();
  }

  @Override public void goToChannelsList() {
    Intent intent = new Intent(this, ChannelsContainerActivity.class);
    startActivity(intent);
  }

  @Override public void goToChannelTimeline(String idTargetUser) {
    startActivity(PrivateMessageTimelineActivity.newIntent(this, idTargetUser));
  }

  @Override public void hideEditMenu() {
    floatingMenu.setVisibility(View.GONE);
  }

  @Override public void showEditMenu() {
    floatingMenu.setVisibility(View.VISIBLE);
  }

  @Override protected void initializePresenter() {
    if (idUser != null) {
      profilePresenter.initializeWithIdUser(this, idUser);
    } else {
      profilePresenter.initializeWithUsername(this, username);
    }
    suggestedPeoplePresenter.initialize(this);
    reportShotPresenter.initialize(this);
  }

  private void setupToolbar() {
    setSupportActionBar(toolbar);
    ActionBar actionBar = getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);
    actionBar.setDisplayShowHomeEnabled(true);
  }

  @Override public void onBackPressed() {
    super.onBackPressed();
    overridePendingTransition(R.anim.stay, R.anim.slide_out_bottom);
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_profile, menu);
    logoutMenuItem.bindRealMenuItem(menu.findItem(R.id.menu_profile_logout));
    supportMenuItem.bindRealMenuItem(menu.findItem(R.id.menu_profile_support));
    shareProfileMenuItem.bindRealMenuItem(menu.findItem(R.id.menu_profile_share));
    changePasswordMenuItem.bindRealMenuItem(menu.findItem(R.id.menu_profile_change_password));
    blockUserMenuItem.bindRealMenuItem(menu.findItem(R.id.menu_profile_block_user));
    muteUserMenuItem.bindRealMenuItem(menu.findItem(R.id.menu_profile_mute_user));
    reportUserMenuItem.bindRealMenuItem(menu.findItem(R.id.menu_profile_report_user));
    settingsMenuItem.bindRealMenuItem(menu.findItem(R.id.menu_profile_settings));
    return super.onCreateOptionsMenu(menu);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        backStackHandler.handleBackStack(this);
        overridePendingTransition(R.anim.stay, R.anim.slide_out_bottom);
        return true;
      case R.id.menu_profile_logout:
        profilePresenter.logoutSelected();
        return true;
      case R.id.menu_profile_change_password:
        startActivity(new Intent(this, ChangePasswordActivity.class));
        return true;
      case R.id.menu_profile_support:
        startActivity(new Intent(this, SupportActivity.class));
        return true;
      case R.id.menu_profile_block_user:
        profilePresenter.blockMenuClicked();
        return true;
      case R.id.menu_profile_report_user:
        profilePresenter.reportUserClicked();
        return true;
      case R.id.menu_profile_settings:
        startActivity(new Intent(this, SettingsActivity.class));
        return true;
      case R.id.menu_profile_mute_user:
        profilePresenter.muteMenuClicked();
        return true;
      case R.id.menu_profile_share:
        profilePresenter.shareProfileMenuClicked();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override public void onResume() {
    super.onResume();
    profilePresenter.resume();
    suggestedPeoplePresenter.resume();
  }

  @Override public void onPause() {
    super.onPause();
    profilePresenter.pause();
    suggestedPeoplePresenter.pause();
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    Timber.d("onActivityResult" + String.valueOf(requestCode));
    if (resultCode == Activity.RESULT_OK) {
      if (requestCode == REQUEST_CHOOSE_PHOTO) {
        cropGalleryPicture(data);
      } else if (requestCode == REQUEST_TAKE_PHOTO) {
        cropCameraPicture();
      } else if (requestCode == REQUEST_NEW_STREAM) {
        String streamId = data.getStringExtra(NewStreamActivity.KEY_STREAM_ID);
        profilePresenter.streamCreated(streamId);
      } else if (requestCode == REQUEST_CROP_PHOTO) {
        Timber.d("REQUEST_CROP_PHOTO");
        File photoFile = getCameraPhotoFile();
        profilePresenter.uploadPhoto(photoFile);
      }
    }
  }
  //endregion

  //region Photo methods
  private void cropCameraPicture() {
    Intent intent = new Intent(this, CropPictureActivity.class);
    intent.putExtra(CropPictureActivity.EXTRA_PHOTO_TYPE, true);
    intent.putExtra(CropPictureActivity.EXTRA_URI, "");
    intent.putExtra(CropPictureActivity.EXTRA_IMAGE_NAME, "profileUpload.jpg");
    startActivityForResult(intent, REQUEST_CROP_PHOTO);
  }

  private void cropGalleryPicture(Intent data) {
    Uri selectedImageUri = data.getData();
    Intent intent = new Intent(this, CropPictureActivity.class);
    intent.putExtra(CropPictureActivity.EXTRA_PHOTO_TYPE, false);
    intent.putExtra(CropPictureActivity.EXTRA_IMAGE_NAME, "profileUpload.jpg");
    intent.putExtra(CropPictureActivity.EXTRA_URI, selectedImageUri.toString());
    startActivityForResult(intent, REQUEST_CROP_PHOTO);
  }

  private void takePhotoFromCamera() {
    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    Uri temporaryPhotoUri =
        FileProvider.getUriForFile(ProfileActivity.this, BuildConfig.APPLICATION_ID + ".provider",
            getCameraPhotoFile());
    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, temporaryPhotoUri);
    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
  }

  private void choosePhotoFromGallery() {
    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    startActivityForResult(intent, REQUEST_CHOOSE_PHOTO);
  }

  private void removePhoto() {
    profilePresenter.removePhoto();
  }

  private File getCameraPhotoFile() {
    return new File(getExternalFilesDir("tmp"), "profileUpload.jpg");
  }
  //endregion

  //region Shot methods
  private void shareShot(ShotModel shotModel) {
    Intent shareIntent = shareManager.shareShotIntent(this, shotModel);
    Intents.maybeStartActivity(this, shareIntent);
  }

  private void openShot(ShotModel shot) {
    Intent intent =
        NewShotDetailActivity.getIntentForActivity(this, shot.getIdShot());
    startActivity(intent);
  }

  private void onShotAvatarClick(View avatarItem) {
    animateBigAvatarClick();

    float scale = 0.8f;
    ObjectAnimator animX = ObjectAnimator.ofFloat(avatarItem, "scaleX", 1f, scale, 1f);
    ObjectAnimator animY = ObjectAnimator.ofFloat(avatarItem, "scaleY", 1f, scale, 1f);
    AnimatorSet animatorSet = new AnimatorSet();
    animatorSet.playTogether(animX, animY);
    animatorSet.setDuration(200);
    animatorSet.setInterpolator(new LinearInterpolator());
    animatorSet.start();
  }

  private void openDeleteShotConfirmation(final ShotModel shotModel) {
    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

    alertDialogBuilder.setMessage(R.string.delete_shot_confirmation_message);
    alertDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int id) {
        reportShotPresenter.deleteShot(shotModel);
      }
    });
    alertDialogBuilder.setNegativeButton(R.string.cancel, null);
    AlertDialog alertDialog = alertDialogBuilder.create();
    alertDialog.show();
  }
  //endregion

  private void goToUserProfile(String username) {
    Intent intentForUser = ProfileActivity.getIntentWithUsername(this, username);
    startActivity(intentForUser);
  }

  private void followUser() {
    profilePresenter.follow();
    sendFollowAnalytics();
  }

  private void sendFollowAnalytics() {
    AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
    builder.setContext(getBaseContext());
    builder.setActionId(analyticsActionFollow);
    builder.setLabelId(analyticsLabelShareShot);
    builder.setSource(profileSource);
    builder.setUser(sessionRepository.getCurrentUser());
    builder.setIdTargetUser(idUser);
    builder.setIsStrategic(profilePresenter.getIsStrategic());
    builder.setTargetUsername(profilePresenter.getUsername());
    analyticsTool.analyticsSendAction(builder);
    analyticsTool.appsFlyerSendAction(builder);
  }

  private void sendWhoToFollowAnalytics(UserModel userModel) {
    AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
    builder.setContext(getBaseContext());
    builder.setActionId(analyticsActionFollow);
    builder.setLabelId(analyticsLabelShareShot);
    builder.setSource(whoToFollowSource);
    builder.setUser(sessionRepository.getCurrentUser());
    builder.setIdTargetUser(userModel.getIdUser());
    builder.setTargetUsername(userModel.getUsername());
    analyticsTool.analyticsSendAction(builder);
  }

  private void unfollowUser() {
    profilePresenter.unfollow();
  }

  private void animateBigAvatarClick() {
    float scale = 1.1f;
    ObjectAnimator animX = ObjectAnimator.ofFloat(avatarImageView, "scaleX", 1f, scale, 1f);
    ObjectAnimator animY = ObjectAnimator.ofFloat(avatarImageView, "scaleY", 1f, scale, 1f);
    AnimatorSet animatorSet = new AnimatorSet();
    animatorSet.playTogether(animX, animY);
    animatorSet.setDuration(200);
    animatorSet.setInterpolator(new LinearInterpolator());
    animatorSet.start();
  }

  private void editProfile() {
    startActivity(new Intent(this, ProfileEditActivity.class));
  }

  private void renderBio(UserModel userModel) {
    String bio = userModel.getBio();
    if (bio != null) {
      bioTextView.setText(bio);
      bioTextView.setVisibility(View.VISIBLE);
    } else {
      bioTextView.setVisibility(View.GONE);
    }
  }

  private void renderWebsite(UserModel userModel) {
    String website = userModel.getWebsite();
    if (website != null) {
      websiteTextView.setText(website);
      websiteTextView.setVisibility(View.VISIBLE);
    } else {
      websiteTextView.setVisibility(View.GONE);
    }
  }

  private void redirectToWelcome() {
    Intent intent = new Intent(this, LoginSelectionActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(intent);
  }

  private UserListAdapter getSuggestedPeopleAdapter() {
    if (suggestedPeopleAdapter == null) {
      suggestedPeopleAdapter = new UserListAdapter(this, imageLoader);
      suggestedPeopleAdapter.setCallback(this);
    }
    return suggestedPeopleAdapter;
  }

  //region Click listeners
  @OnClick(R.id.profile_avatar) public void onAvatarClick() {
    profilePresenter.avatarClicked();
  }

  @OnClick(R.id.following_container) public void openFollowingList() {
    profilePresenter.followingButtonClicked();
  }

  @OnClick(R.id.followers_container) public void openFollowersList() {
    profilePresenter.followersButtonClicked();
  }

  @OnClick(R.id.profile_follow_button) public void onMainButonClick() {
    if (followButton.isEditProfile()) {
      editProfile();
    } else if (followButton.isFollowing()) {
      unfollowUser();
    } else {
      followUser();
    }
  }

  @OnClick(R.id.profile_website) public void onWebsiteClick() {
    profilePresenter.websiteClicked();
  }

  @OnClick(R.id.streams_container) public void onListingClick() {
    profilePresenter.clickListing();
  }

  @OnClick(R.id.profile_all_shots_button) public void onAllShotsClick() {
    profilePresenter.allShotsClicked();
  }
  //endregion

  //region View methods

  @Override public void showAllShotsButton() {
    allShotContainer.setVisibility(View.VISIBLE);
  }

  @Override public void hideAllShotsButton() {
    allShotContainer.setVisibility(View.GONE);
  }

  @Override public void shareProfileVia(UserModel user) {
    Intent shareIntent = shareManager.shareProfileIntent(this, user);
    Intents.maybeStartActivity(this, shareIntent);
  }

  @Override public void setUserInfo(UserModel userModel) {
    collapsingToolbarLayout.setTitle("@" + userModel.getUsername());
    collapsingToolbarLayout.setExpandedTitleTextColor(
        android.content.res.ColorStateList.valueOf(Color.WHITE));
    nameTextView.setText(userModel.getName());
    renderWebsite(userModel);
    renderBio(userModel);
    setupAvatar(userModel);
    followersTextView.setText(numberFormatUtil.formatNumbers(userModel.getNumFollowers()));
    followingTextView.setText(numberFormatUtil.formatNumbers(userModel.getNumFollowings()));
  }

  private void setupAvatar(UserModel userModel) {
    if (userModel.getPhoto() != null && !userModel.getPhoto().isEmpty()) {
      imageLoader.loadProfilePhoto(userModel.getPhoto(), avatarImageView);
    } else {
      setupInitials(userModel);
    }
  }

  private void setupInitials(UserModel userModel) {
    try {
      String initials = initialsLoader.getLetters(userModel.getUsername());
      int backgroundColor = initialsLoader.getColorForLetters(initials);
      TextDrawable textDrawable = initialsLoader.getRectTextDrawable(initials, backgroundColor);
      avatarImageView.setImageDrawable(textDrawable);
    } catch (NullPointerException error) {
      avatarImageView.setImageResource(R.drawable.ic_contact_picture_default);
    }
  }

  @Override public void navigateToListing(String idUser, boolean isCurrentUser) {
    Intent intent = ListingActivity.getIntent(this, idUser, isCurrentUser);
    this.startActivity(intent);
  }

  @Override public void showLogoutInProgress() {
    if (this != null) {
      progress = ProgressDialog.show(this, null, this.getString(R.string.sign_out_message), true);
    }
  }

  @Override public void hideLogoutInProgress() {
    try {
      if (this != null) {
        progress.dismiss();
      }
    } catch (Exception error) {
      /* no-op */
    }
  }

  @Override public void navigateToWelcomeScreen() {
    if (this != null) {
      new Handler().postDelayed(new Runnable() {
        @Override public void run() {
          hideLogoutInProgress();
          redirectToWelcome();
        }
      }, LOGOUT_DISMISS_DELAY);
    }
  }

  @Override public void showLogoutButton() {
    logoutMenuItem.setVisible(true);
  }

  @Override public void showSupportButton() {
    supportMenuItem.setVisible(true);
  }

  @Override public void showChangePasswordButton() {
    changePasswordMenuItem.setVisible(true);
  }

  @Override public void unblockUser(UserModel userModel) {
    reportShotPresenter.unblockUserClicked(userModel);
  }

  @Override public void showReportUserButton() {
    reportUserMenuItem.setVisible(true);
  }

  @Override public void goToReportEmail(String currentUserId, String idUser) {
    Intent reportEmailIntent = intentFactory.reportEmailIntent(this, currentUserId, idUser);
    Intents.maybeStartActivity(this, reportEmailIntent);
  }

  @Override public void showMuteUserButton() {
    muteUserMenuItem.setVisible(true);
    muteUserMenuItem.setTitle(muteUser);
  }

  @Override public void showUnmuteUserButton() {
    muteUserMenuItem.setTitle(unmuteUser);
  }

  @Override public void showBlockUserButton() {
    blockUserMenuItem.setVisible(true);
    blockUserMenuItem.setTitle(blockUser);
  }

  @Override public void showUnblockUserButton() {
    blockUserMenuItem.setTitle(unblockUser);
  }

  @Override public void blockUser(UserModel userModel) {
    reportShotPresenter.blockUserClicked(userModel);
  }

  @Override public void showBlockedMenu(UserModel userModel) {
    unblockUser(userModel);
  }

  @Override public void navigateToCreatedStreamDetail(String streamId) {
    startActivity(StreamDetailActivity.getIntent(this, streamId));
  }

  @Override public void notifyReshoot(String idShot, boolean mark) {
    profileShotsAdapter.reshoot(idShot, mark);
  }

  @Override public void renderSuggestedPeopleList(List<UserModel> users) {
    suggestedPeopleAdapter.setItems(users);
    suggestedPeopleAdapter.notifyDataSetChanged();
  }

  @Override public void showError(String messageForError) {
    feedbackMessage.show(getView(), messageForError);
  }

  @Override public void showEditProfileButton() {
    followButton.setEditProfile();
  }

  @Override public void showFollowButton() {
    followButton.setFollowing(false);
  }

  @Override public void showUnfollowButton() {
    followButton.setFollowing(true);
  }

  @Override public void showAddPhoto() {
    avatarImageView.setImageResource(R.drawable.profile_photo_add);
  }

  @Override public void openPhoto(String photo) {
    String photoBig = photo.replace("_thumbnail", ""); // <-- Chapuza Carlos, chapuza!!
    Intent intentForPhoto = PhotoViewActivity.getIntentForActivity(this, photoBig, photo);
    startActivity(intentForPhoto);
  }

  @Override public void openEditPhotoMenu(boolean showRemove, String photo) {
    BottomSheet.Builder menuBuilder = new BottomSheet.Builder(this);
    if (showRemove) {
      menuBuilder.sheet(R.id.menu_photo_watch, R.drawable.ic_stream_author_24_gray50,
          R.string.watch_profile_photo);
    }
    menuBuilder.sheet(R.id.menu_photo_gallery, R.drawable.ic_photo_library,
        R.string.photo_edit_gallery);
    menuBuilder.sheet(R.id.menu_photo_take, R.drawable.ic_photo_camera, R.string.photo_edit_take);
    menuBuilder.title(R.string.title_menu_photo);
    menuBuilder.listener(photoDialogListener(photo));

    if (showRemove) {
      menuBuilder.sheet(R.id.menu_photo_remove, R.drawable.ic_photo_remove,
          R.string.photo_edit_remove);
    }

    menuBuilder.show();
  }

  @NonNull public DialogInterface.OnClickListener photoDialogListener(final String photo) {
    return new DialogInterface.OnClickListener() {
      @Override public void onClick(DialogInterface dialog, int which) {
        switch (which) {
          case R.id.menu_photo_gallery:
            handleChoosePhotoFromGallery();
            break;
          case R.id.menu_photo_take:
            takePhotoFromCamera();
            break;
          case R.id.menu_photo_remove:
            removePhoto();
            break;
          case R.id.menu_photo_watch:
            openPhoto(photo);
            break;
          default:
            break;
        }
      }
    };
  }

  public void handleChoosePhotoFromGallery() {
    if (writePermissionManager.hasWritePermission()) {
      choosePhotoFromGallery();
    } else {
      writePermissionManager.requestWritePermissionToUser();
    }
  }

  @Override public void goToWebsite(String website) {
    Intent intent = intentFactory.openUrlIntent(website);
    Intents.maybeStartActivity(this, intent);
  }

  @Override public void goToFollowersList(String idUser) {
    Intent intent =
        UserFollowsContainerActivity.getIntent(this, idUser, UserFollowingRelationship.FOLLOWERS);
    startActivity(intent);
  }

  @Override public void goToFollowingList(String idUser) {
    Intent intent =
        UserFollowsContainerActivity.getIntent(this, idUser, UserFollowingRelationship.FOLLOWING);
    startActivity(intent);
  }

  @Override public void renderLastShots(List<ShotModel> shots) {
    profileShotsAdapter.setShots(shots);
    profileShotsAdapter.notifyDataSetChanged();
  }

  @Override public void showUnfollowConfirmation(String username) {
    new AlertDialog.Builder(this).setMessage(
        String.format(getString(R.string.unfollow_dialog_message), username))
        .setPositiveButton(getString(R.string.unfollow_dialog_yes),
            new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                profilePresenter.confirmUnfollow();
              }
            })
        .setNegativeButton(getString(R.string.unfollow_dialog_no), null)
        .create()
        .show();
  }

  @Override public void goToAllShots(String idUser) {
    startActivity(AllShotsActivity.newIntent(this, idUser, profilePresenter.isCurrentUser()));
  }

  @Override public void showLatestShots() {
    shotsList.setVisibility(View.VISIBLE);
  }

  @Override public void hideLatestShots() {
    reshootsHeader.setVisibility(View.GONE);
    shotsList.setVisibility(View.GONE);
  }

  @Override public void showReshotsHeader() {
    reshootsHeader.setVisibility(View.VISIBLE);
  }

  @Override public void showLoadingPhoto() {
    avatarImageView.setVisibility(View.INVISIBLE);
    avatarLoadingView.setVisibility(View.VISIBLE);
  }

  @Override public void showLoading() {
    collapsingToolbarLayout.setTitle(" ");
    progressBar.setVisibility(View.VISIBLE);
  }

  @Override public void hideLoading() {
    progressBar.setVisibility(View.GONE);
  }

  @Override public void hideLoadingPhoto() {
    avatarImageView.setVisibility(View.VISIBLE);
    avatarLoadingView.setVisibility(View.GONE);
  }

  @Override public void showRemovePhotoConfirmation() {
    new AlertDialog.Builder(this).setMessage(R.string.photo_edit_remove_confirmation)
        .setPositiveButton(R.string.remove, new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
            profilePresenter.removePhotoConfirmed();
          }
        })
        .setNegativeButton(R.string.cancel, null)
        .show();
  }

  @Override public void setupAnalytics(boolean isCurrentUser) {
    if (isCurrentUser) {
      analyticsTool.analyticsStart(this, analyticsScreenMe);
    } else {
      analyticsTool.analyticsStart(this, analyticsScreenUserProfile);
    }
  }

  @Override public void showVerifiedUser() {
    userVerified.setVisibility(View.VISIBLE);
  }

  @Override public void hideVerifiedUser() {
    userVerified.setVisibility(View.GONE);
  }

  @Override public void showUserMuted() {
    userMuted.setVisibility(View.VISIBLE);
    showUnmuteUserButton();
    profilePresenter.setUserMuted(true);
  }

  @Override public void hideUserMuted() {
    userMuted.setVisibility(View.GONE);
    showMuteUserButton();
    profilePresenter.setUserMuted(false);
  }

  @Override public void showStreamsCount() {
    /* no-op */
  }

  @Override public void showBalance() {
    if (sessionRepository.isPromotedShotActivated()) {
      balanceContainer.setVisibility(View.VISIBLE);
    }
  }

  @Override public void setStreamsCount(Integer streamCount) {
    streamNumber.setText(String.valueOf(streamCount));
  }

  @Override public void setBalance(float balance) {
    balanceNumber.setText(String.valueOf(balance) + EURO);
  }

  @Override public void refreshSuggestedPeople(List<UserModel> suggestedPeople) {
    getSuggestedPeopleAdapter().notifyDataSetChanged();
  }

  @Override public void follow(int position) {
    suggestedPeoplePresenter.followUser(getSuggestedPeopleAdapter().getItem(position));
    getSuggestedPeopleAdapter().getItem(position).setFollowing(true);
    getSuggestedPeopleAdapter().notifyDataSetChanged();
    sendWhoToFollowAnalytics(getSuggestedPeopleAdapter().getItem(position));
  }

  @Override public void unFollow(final int position) {
    final UserModel userModel = getSuggestedPeopleAdapter().getItem(position);
    new AlertDialog.Builder(this).setMessage("Unfollow " + userModel.getUsername() + "?")
        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
            suggestedPeoplePresenter.unfollowUser(userModel);
            getSuggestedPeopleAdapter().getItem(position).setFollowing(false);
            getSuggestedPeopleAdapter().notifyDataSetChanged();
          }
        })
        .setNegativeButton("No", null)
        .create()
        .show();
  }

  @Override public void setFollowing(Boolean following) {
    followButton.setFollowing(following);
  }

  @Override public void handleReport(String sessionToken, ShotModel shotModel) {
    reportShotPresenter.reportClicked(sessionToken, shotModel);
  }

  @Override public void showHolderContextMenu(ShotModel shot) {
    showAuthorContextMenuWithPin(shot);
  }

  @Override public void showHolderContextMenuWithDismissHighlight(ShotModel shot) {
    /* no-op */
  }

  @Override public void goToReport(String sessionToken, ShotModel shotModel) {
    Intent browserIntent = new Intent(Intent.ACTION_VIEW,
        Uri.parse(String.format(reportBaseUrl, sessionToken, shotModel.getIdShot())));
    startActivity(browserIntent);
  }

  @Override public void showEmailNotConfirmedError() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);

    builder.setMessage(getString(R.string.alert_report_confirmed_email_message))
        .setTitle(getString(R.string.alert_report_confirmed_email_title))
        .setPositiveButton(getString(R.string.alert_report_confirmed_email_ok), null);

    builder.create().show();
  }

  @Override public void showContextMenu(final ShotModel shotModel) {
    getBaseContextMenuOptions(shotModel).addAction(R.string.report_context_menu_report,
        new Runnable() {
          @Override public void run() {
            reportShotPresenter.report(shotModel);
          }
        }).show();
  }

  @Override public void showAuthorContextMenuWithPin(final ShotModel shotModel) {
    getBaseContextMenuOptions(shotModel).addAction(R.string.report_context_menu_delete,
        new Runnable() {
          @Override public void run() {
            openDeleteShotConfirmation(shotModel);
          }
        }).show();
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
    showUnblockUserButton();
    profilePresenter.setUserBlocked(true);
  }

  @Override public void showUserUnblocked() {
    feedbackMessage.show(getView(), R.string.user_unblocked);
    showBlockUserButton();
    profilePresenter.setUserBlocked(false);
  }

  @Override public void showBlockUserConfirmation() {
    new AlertDialog.Builder(this).setMessage(R.string.block_user_dialog_message)
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

  @Override public void showAuthorContextMenuWithoutPin(final ShotModel shotModel) {
    getBaseContextMenuOptions(shotModel).addAction(R.string.report_context_menu_delete,
        new Runnable() {
          @Override public void run() {
            openDeleteShotConfirmation(shotModel);
          }
        }).show();
  }

  @Override public void showAuthorContextMenuWithPinAndHighlight(ShotModel shot) {
    /* no-op */
  }

  @Override public void showAuthorContextMenuWithPinAndDismissHighlight(ShotModel shot,
      HighlightedShot highlightedShot) {
    /* no-op */
  }

  @Override public void showAuthorContextMenuWithoutPinAndHighlight(ShotModel shot) {
    /* no-op */
  }

  @Override public void showAuthorContextMenuWithoutPinAndDismissHighlight(ShotModel shot) {
    /* no-op */
  }

  @Override public void showContributorContextMenuWithPinAndHighlight(ShotModel shot) {
    /* no-op */
  }

  @Override public void showContributorContextMenuWithPinAndDismissHighlight(ShotModel shot) {
    /* no-op */
  }

  @Override public void showContributorContextMenuWithoutPinAndHighlight(ShotModel shot) {
    /* no-op */
  }

  @Override public void showContributorContextMenuWithoutPinAndDismissHighlight(ShotModel shot) {
    /* no-op */
  }

  @Override public void showContributorContextMenu(ShotModel shot) {
    /* no-op */
  }

  @Override public void showContributorContextMenuWithDismissHighlight(ShotModel shot) {
    /* no-op */
  }

  private CustomContextMenu.Builder getBaseContextMenuOptions(final ShotModel shotModel) {
    final Context context = this;
    return new CustomContextMenu.Builder(this).addAction(
        shotModel.isReshooted() ? R.string.undo_reshoot : R.string.menu_share_shot_via_shootr,
        new Runnable() {
          @Override public void run() {
            if (shotModel.isReshooted()) {
              profilePresenter.undoReshoot(shotModel);
            } else {
              profilePresenter.reshoot(shotModel);
              sendShareShotAnalythics(shotModel);
            }
          }
        }).addAction(R.string.menu_share_shot_via, new Runnable() {
      @Override public void run() {
        shareShot(shotModel);
        sendExternalShareAnalythics(shotModel);
      }
    }).addAction(R.string.menu_copy_text, new Runnable() {
      @Override public void run() {
        Clipboard.copyShotComment(context, shotModel);
      }
    });
  }

  private void sendExternalShareAnalythics(ShotModel shot) {
    AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
    builder.setContext(getBaseContext());
    builder.setActionId(analyticsActionExternalShare);
    builder.setLabelId(analyticsLabelExternalShare);
    builder.setSource(profileSource);
    builder.setUser(sessionRepository.getCurrentUser());
    analyticsTool.analyticsSendAction(builder);
  }

  private void sendShareShotAnalythics(ShotModel shot) {
    AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
    builder.setContext(getBaseContext());
    builder.setActionId(analyticsActionShareShot);
    builder.setLabelId(analyticsLabelShareShot);
    builder.setSource(profileSource);
    builder.setUser(sessionRepository.getCurrentUser());
    builder.setIdTargetUser(shot.getIdUser());
    builder.setTargetUsername(shot.getUsername());
    analyticsTool.analyticsSendAction(builder);
  }

  @Override public void notifyDeletedShot(ShotModel shotModel) {
    profilePresenter.refreshLatestShots();
  }

  @Override public void showUserSettings() {
    settingsMenuItem.setVisible(true);
  }

  @Override public void hideChannelButton() {
    channelButton.setVisibility(View.GONE);
  }

  @OnClick(R.id.channel_button) public void onChannelClick() {
    profilePresenter.onChannelClick();
  }

  @OnClick(R.id.fab_edit_photo) public void onFabEditClick() {
    closeFabMenu();
    profilePresenter.avatarClicked();
  }

  @OnClick(R.id.fab_new_stream) public void onAddStream() {
    closeFabMenu();
    Intent intent = new Intent(this, NewStreamActivity.class);
    intent.putExtra(NewStreamActivity.SOURCE, profileSource);
    startActivityForResult(intent, REQUEST_NEW_STREAM);
  }

  @OnClick(R.id.streams_tab_container) public void onStreamsClick() {
    profilePresenter.clickListing();
  }

  private void closeFabMenu() {
    if (floatingMenu != null) {
      floatingMenu.close(true);
    }
  }

  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    if (requestCode == 1) {
      if (grantResults.length > 0) {
        onAvatarClick();
      }
    }
  }
}
