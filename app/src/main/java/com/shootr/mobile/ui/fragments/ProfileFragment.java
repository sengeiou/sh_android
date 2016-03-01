package com.shootr.mobile.ui.fragments;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.cocosw.bottomsheet.BottomSheet;
import com.shootr.mobile.R;
import com.shootr.mobile.domain.dagger.TemporaryFilesDir;
import com.shootr.mobile.domain.utils.UserFollowingRelationship;
import com.shootr.mobile.ui.ToolbarDecorator;
import com.shootr.mobile.ui.activities.AllShotsActivity;
import com.shootr.mobile.ui.activities.ChangePasswordActivity;
import com.shootr.mobile.ui.activities.ListingActivity;
import com.shootr.mobile.ui.activities.NewStreamActivity;
import com.shootr.mobile.ui.activities.PhotoViewActivity;
import com.shootr.mobile.ui.activities.ProfileContainerActivity;
import com.shootr.mobile.ui.activities.ProfileEditActivity;
import com.shootr.mobile.ui.activities.ShotDetailActivity;
import com.shootr.mobile.ui.activities.StreamDetailActivity;
import com.shootr.mobile.ui.activities.SupportActivity;
import com.shootr.mobile.ui.activities.UserFollowsContainerActivity;
import com.shootr.mobile.ui.activities.registro.LoginSelectionActivity;
import com.shootr.mobile.ui.adapters.TimelineAdapter;
import com.shootr.mobile.ui.adapters.UserListAdapter;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnHideClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnNiceShotListener;
import com.shootr.mobile.ui.adapters.listeners.OnShotClick;
import com.shootr.mobile.ui.adapters.listeners.OnShotLongClick;
import com.shootr.mobile.ui.adapters.listeners.OnUserClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnUsernameClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnVideoClickListener;
import com.shootr.mobile.ui.base.BaseFragment;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.presenter.ProfilePresenter;
import com.shootr.mobile.ui.presenter.ReportShotPresenter;
import com.shootr.mobile.ui.presenter.SuggestedPeoplePresenter;
import com.shootr.mobile.ui.views.ProfileView;
import com.shootr.mobile.ui.views.ReportShotView;
import com.shootr.mobile.ui.views.SuggestedPeopleView;
import com.shootr.mobile.ui.widgets.FollowButton;
import com.shootr.mobile.ui.widgets.ShotListView;
import com.shootr.mobile.ui.widgets.SuggestedPeopleListView;
import com.shootr.mobile.util.AnalyticsTool;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.Clipboard;
import com.shootr.mobile.util.CustomContextMenu;
import com.shootr.mobile.util.FeedbackMessage;
import com.shootr.mobile.util.FileChooserUtils;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.IntentFactory;
import com.shootr.mobile.util.Intents;
import com.shootr.mobile.util.MenuItemValueHolder;
import com.shootr.mobile.util.WritePermissionManager;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import javax.inject.Inject;
import timber.log.Timber;

public class ProfileFragment extends BaseFragment
  implements ProfileView, SuggestedPeopleView, UserListAdapter.FollowUnfollowAdapterCallback, ReportShotView {

    private static final int REQUEST_CHOOSE_PHOTO = 1;
    private static final int REQUEST_TAKE_PHOTO = 2;
    private static final int REQUEST_NEW_STREAM = 3;

    public static final String ARGUMENT_USER = "user";
    public static final String ARGUMENT_USERNAME = "username";
    public static final String TAG = "profile";
    public static final int LOGOUT_DISMISS_DELAY = 1500;

    //region injected
    @Bind(R.id.profile_name) TextView nameTextView;
    @Bind(R.id.profile_bio) TextView bioTextView;
    @Bind(R.id.profile_website) TextView websiteTextView;
    @Bind(R.id.profile_avatar) ImageView avatarImageView;

    @Bind(R.id.profile_listing_container) View listingContainerView;

    @Bind(R.id.profile_open_stream_container) View openStreamContainerView;
    @Bind(R.id.profile_streams_number) TextView streamsCountView;

    @Bind(R.id.profile_marks_followers) TextView followersTextView;
    @Bind(R.id.profile_marks_following) TextView followingTextView;

    @Bind(R.id.profile_follow_button) FollowButton followButton;

    @Bind(R.id.profile_shots_empty) View shotsListEmpty;
    @Bind(R.id.profile_shots_list) ShotListView shotsList;

    @Bind(R.id.profile_all_shots_container) View allShotContainer;

    @Bind(R.id.profile_avatar_loading) ProgressBar avatarLoadingView;

    @Bind(R.id.profile_suggested_people) SuggestedPeopleListView suggestedPeopleListView;

    @Bind(R.id.profile_user_verified) ImageView userVerified;

    @BindString(R.string.report_base_url) String reportBaseUrl;
    @BindString(R.string.analytics_screen_me) String analyticsScreenMe;
    @BindString(R.string.analytics_screen_userProfile) String analyticsScreenUserProfile;
    @BindString(R.string.confirmationMessage) String confirmationMessage;
    @BindString(R.string.confirmHideShot) String confirmHideShotAlertDialogMessage;
    @BindString(R.string.cancelHideShot) String cancelHideShotAlertDialogMessage;

    @Inject ImageLoader imageLoader;
    @Inject IntentFactory intentFactory;
    @Inject FeedbackMessage feedbackMessage;
    @Inject ToolbarDecorator toolbarDecorator;
    @Inject ProfilePresenter profilePresenter;
    @Inject SuggestedPeoplePresenter suggestedPeoplePresenter;
    @Inject ReportShotPresenter reportShotPresenter;
    @Inject @TemporaryFilesDir File externalFilesDir;
    @Inject AndroidTimeUtils timeUtils;
    @Inject AnalyticsTool analyticsTool;
    @Inject WritePermissionManager writePermissionManager;

    //endregion

    String idUserArgument;
    String usernameArgument;

    private TimelineAdapter latestsShotsAdapter;
    private ProgressDialog progress;
    private MenuItemValueHolder logoutMenuItem = new MenuItemValueHolder();
    private MenuItemValueHolder supportMenuItem = new MenuItemValueHolder();
    private MenuItemValueHolder changePasswordMenuItem = new MenuItemValueHolder();
    private MenuItemValueHolder blockUserMenuItem = new MenuItemValueHolder();
    private MenuItemValueHolder reportUserMenuItem = new MenuItemValueHolder();
    private UserListAdapter suggestedPeopleAdapter;

    //region Construction
    public static ProfileFragment newInstance(String idUser) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(ARGUMENT_USER, idUser);
        fragment.setArguments(arguments);
        return fragment;
    }

    public static ProfileFragment newInstanceFromUsername(String username) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(ARGUMENT_USERNAME, username);
        fragment.setArguments(arguments);
        return fragment;
    }
    //endregion

    //region Initialization
    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        injectArguments();
        setHasOptionsMenu(true);
        initializeViews();
        initializePresenter();
    }

    @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    private void injectArguments() {
        Bundle arguments = getArguments();
        idUserArgument = (String) arguments.getSerializable(ARGUMENT_USER);
        usernameArgument = (String) arguments.getSerializable(ARGUMENT_USERNAME);
    }

    private void initializeViews() {
        ButterKnife.bind(this, getView());
        writePermissionManager.init(getActivity());
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
            @Override public void markNice(String idShot) {
                profilePresenter.markNiceShot(idShot);
            }

            @Override public void unmarkNice(String idShot) {
                profilePresenter.unmarkNiceShot(idShot);
            }
        };

        OnHideClickListener onHideClickListener = new OnHideClickListener() {
            @Override public void onHideClick(String idSHot) {
                profilePresenter.showUnpinShotAlert(idSHot);
            }
        };
        suggestedPeopleListView.setAdapter(getSuggestedPeopleAdapter());
        suggestedPeopleListView.setOnUserClickListener(new OnUserClickListener() {
            @Override public void onUserClick(String idUser) {
                Intent suggestedUserIntent = ProfileContainerActivity.getIntent(getActivity(), idUser);
                startActivity(suggestedUserIntent);
            }
        });

        latestsShotsAdapter = new TimelineAdapter(getActivity(),
          imageLoader,
          timeUtils,
          avatarClickListener,
          videoClickListener,
          onNiceShotListener,
          onUsernameClickListener,
          onHideClickListener,
          profilePresenter.isCurrentUser()) {
            @Override protected boolean shouldShowShortTitle() {
                return true;
            }
        };
        shotsList.setAdapter(latestsShotsAdapter);
        shotsList.setOnShotClick(new OnShotClick() {
            @Override public void onShotClick(ShotModel shot) {
                openShot(shot);
            }
        });
        shotsList.setOnShotLongClick(new OnShotLongClick() {
            @Override public void onShotLongClick(ShotModel shot) {
                reportShotPresenter.onShotLongPressed(shot);
            }
        });
    }

    @Override public void resetTimelineAdapter() {
        latestsShotsAdapter.setIsCurrentUser(profilePresenter.isCurrentUser());
        latestsShotsAdapter.notifyDataSetChanged();
    }

    @Override public void showHideShotConfirmation(final String idShot) {
        new AlertDialog.Builder(getActivity()).setMessage(confirmationMessage)
          .setPositiveButton(confirmHideShotAlertDialogMessage, new DialogInterface.OnClickListener() {

              public void onClick(DialogInterface dialog, int whichButton) {
                  profilePresenter.hideShot(idShot);
              }
          })
          .setNegativeButton(cancelHideShotAlertDialogMessage, null)
          .show();
    }

    private void initializePresenter() {
        if (idUserArgument != null) {
            profilePresenter.initializeWithIdUser(this, idUserArgument);
        } else {
            profilePresenter.initializeWithUsername(this, usernameArgument);
        }
        suggestedPeoplePresenter.initialize(this);
        reportShotPresenter.initialize(this);
    }

    //endregion

    //region Lifecycle
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_profile, menu);
        logoutMenuItem.bindRealMenuItem(menu.findItem(R.id.menu_profile_logout));
        supportMenuItem.bindRealMenuItem(menu.findItem(R.id.menu_profile_support));
        changePasswordMenuItem.bindRealMenuItem(menu.findItem(R.id.menu_profile_change_password));
        blockUserMenuItem.bindRealMenuItem(menu.findItem(R.id.menu_profile_block_user));
        reportUserMenuItem.bindRealMenuItem(menu.findItem(R.id.menu_profile_report_user));
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_profile_logout:
                profilePresenter.logoutSelected();
                return true;
            case R.id.menu_profile_change_password:
                startActivity(new Intent(getActivity(), ChangePasswordActivity.class));
                return true;
            case R.id.menu_profile_support:
                startActivity(new Intent(this.getActivity(), SupportActivity.class));
                return true;
            case R.id.menu_profile_block_user:
                profilePresenter.blockMenuClicked();
                return true;
            case R.id.menu_profile_report_user:
                profilePresenter.reportUserClicked();
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
        analyticsTool.analyticsStop(getContext(), getActivity());
        profilePresenter.pause();
        suggestedPeoplePresenter.pause();
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            File changedPhotoFile;
            if (requestCode == REQUEST_CHOOSE_PHOTO) {
                Uri selectedImageUri = data.getData();
                try {
                    changedPhotoFile = new File(FileChooserUtils.getPath(getActivity(), selectedImageUri));
                    profilePresenter.uploadPhoto(changedPhotoFile);
                } catch (NullPointerException error) {
                    feedbackMessage.show(getView(), R.string.error_message_invalid_image);
                }
            } else if (requestCode == REQUEST_TAKE_PHOTO) {
                changedPhotoFile = getCameraPhotoFile();
                profilePresenter.uploadPhoto(changedPhotoFile);
            } else if (requestCode == REQUEST_NEW_STREAM) {
                String streamId = data.getStringExtra(NewStreamActivity.KEY_STREAM_ID);
                profilePresenter.streamCreated(streamId);
            }
        }
    }
    //endregion

    //region Photo methods
    private void takePhotoFromCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri temporaryPhotoUri = Uri.fromFile(getCameraPhotoFile());
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
        File photoFile = new File(externalFilesDir, "profileUpload.jpg");
        if (!photoFile.exists()) {
            try {
                photoFile.getParentFile().mkdirs();
                photoFile.createNewFile();
            } catch (IOException e) {
                Timber.e(e, "No se pudo crear el archivo temporal para la foto de perfil");
                throw new IllegalStateException(e);
            }
        }
        return photoFile;
    }
    //endregion

    //region Shot methods
    private void shareShot(ShotModel shotModel) {
        Intent shareIntent = intentFactory.shareShotIntent(getActivity(), shotModel);
        Intents.maybeStartActivity(getActivity(), shareIntent);
    }

    private void openShot(ShotModel shot) {
        Intent intent = ShotDetailActivity.getIntentForActivity(getActivity(), shot);
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
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

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
        Intent intentForUser = ProfileContainerActivity.getIntentWithUsername(getActivity(), username);
        startActivity(intentForUser);
    }

    private void followUser() {
        profilePresenter.follow();
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
        startActivity(new Intent(getActivity(), ProfileEditActivity.class));
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
        Intent intent = new Intent(getActivity(), LoginSelectionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private UserListAdapter getSuggestedPeopleAdapter() {
        if (suggestedPeopleAdapter == null) {
            suggestedPeopleAdapter = new UserListAdapter(getActivity(), imageLoader);
            suggestedPeopleAdapter.setCallback(this);
        }
        return suggestedPeopleAdapter;
    }

    //region Click listeners
    @OnClick(R.id.profile_avatar) public void onAvatarClick() {
        profilePresenter.avatarClicked();
    }

    @OnClick({ R.id.profile_marks_following, R.id.profile_following }) public void openFollowingList() {
        profilePresenter.followingButtonClicked();
    }

    @OnClick({ R.id.profile_marks_followers, R.id.profile_followers }) public void openFollowersList() {
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

    @OnClick(R.id.profile_listing) public void onListingClick() {
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

    @Override public void showListing() {
        openStreamContainerView.setVisibility(View.GONE);
        listingContainerView.setVisibility(View.VISIBLE);
    }

    @Override public void setUserInfo(UserModel userModel) {
        toolbarDecorator.setTitle(userModel.getUsername());
        nameTextView.setText(userModel.getName());
        renderWebsite(userModel);
        renderBio(userModel);
        imageLoader.loadProfilePhoto(userModel.getPhoto(), avatarImageView);
        followersTextView.setText(String.valueOf(userModel.getNumFollowers()));
        followingTextView.setText(String.valueOf(userModel.getNumFollowings()));
    }

    @Override public void navigateToListing(String idUser, boolean isCurrentUser) {
        Intent intent = ListingActivity.getIntent(this.getActivity(), idUser, isCurrentUser);
        this.startActivity(intent);
    }

    @Override public void showLogoutInProgress() {
        if (getActivity() != null) {
            progress =
              ProgressDialog.show(getActivity(), null, getActivity().getString(R.string.sign_out_message), true);
        }
    }

    @Override public void hideLogoutInProgress() {
        if (getActivity() != null) {
            progress.dismiss();
        }
    }

    @Override public void navigateToWelcomeScreen() {
        if (getActivity() != null) {
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
        Intent reportEmailIntent = intentFactory.reportEmailIntent(getActivity(), currentUserId, idUser);
        Intents.maybeStartActivity(getActivity(), reportEmailIntent);
    }

    @Override public void showBanUserConfirmation(final UserModel userModel) {
        new AlertDialog.Builder(getActivity()).setMessage(R.string.ban_user_dialog_message)
          .setPositiveButton(getString(R.string.block_user_dialog_ban), new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                  reportShotPresenter.confirmBan(userModel);
              }
          })
          .setNegativeButton(getString(R.string.block_user_dialog_cancel), null)
          .create()
          .show();
    }

    @Override public void showBlockUserButton() {
        blockUserMenuItem.setVisible(true);
    }

    @Override public void showDefaultBlockMenu(UserModel userModel) {
        new CustomContextMenu.Builder(getActivity()).addAction(R.string.block_ignore_user, new Runnable() {
            @Override public void run() {
                profilePresenter.blockUserClicked();
            }
        }).addAction(R.string.block_cannot_shoot_streams, new Runnable() {
            @Override public void run() {
                profilePresenter.banUserClicked();
            }
        }).show();
    }

    @Override public void showBlockedMenu(UserModel userModel) {
        new CustomContextMenu.Builder(getActivity()).addAction(R.string.block_unblock_user, new Runnable() {
              @Override public void run() {
                  profilePresenter.unblockUserClicked();
              }
          }).addAction(R.string.block_cannot_shoot_streams, new Runnable() {
            @Override public void run() {
                profilePresenter.banUserClicked();
            }
        }).show();
    }

    @Override public void showBannedMenu(UserModel userModel) {
        new CustomContextMenu.Builder(getActivity()).addAction(R.string.block_ignore_user, new Runnable() {
              @Override public void run() {
                  profilePresenter.blockUserClicked();
              }
          }).addAction(R.string.can_shoot_streams, new Runnable() {
            @Override public void run() {
                profilePresenter.unbanUserClicked();
            }
        }).show();
    }

    @Override public void showBlockAndBannedMenu(UserModel userModel) {
        new CustomContextMenu.Builder(getActivity()).addAction(R.string.block_unblock_user, new Runnable() {
            @Override public void run() {
                profilePresenter.unblockUserClicked();
            }
        }).addAction(R.string.can_shoot_streams, new Runnable() {
            @Override public void run() {
                profilePresenter.unbanUserClicked();
            }
        }).show();
    }

    @Override public void confirmUnban(final UserModel userModel) {
        reportShotPresenter.confirmUnBan(userModel);
    }

    @Override public void blockUser(UserModel userModel) {
        reportShotPresenter.blockUserClicked(userModel);
    }

    @Override public void navigateToCreatedStreamDetail(String streamId) {
        startActivity(StreamDetailActivity.getIntent(getActivity(), streamId));
    }

    @Override public void showShotShared() {
        feedbackMessage.show(getView(), getActivity().getString(R.string.shot_shared_message));
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
        Intent intentForPhoto = PhotoViewActivity.getIntentForActivity(getActivity(), photoBig, photo);
        startActivity(intentForPhoto);
    }

    @Override public void openEditPhotoMenu(boolean showRemove) {
        BottomSheet.Builder menuBuilder = new BottomSheet.Builder(getActivity()) //
          .sheet(R.id.menu_photo_gallery, R.drawable.ic_photo_library, R.string.photo_edit_gallery) //
          .sheet(R.id.menu_photo_take, R.drawable.ic_photo_camera, R.string.photo_edit_take) //
          .title(R.string.change_photo) //
          .listener(photoDialogListener());

        if (showRemove) {
            menuBuilder.sheet(R.id.menu_photo_remove, R.drawable.ic_photo_remove, R.string.photo_edit_remove);
        }

        menuBuilder.show();
    }

    @NonNull public DialogInterface.OnClickListener photoDialogListener() {
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
        Intents.maybeStartActivity(getActivity(), intent);
    }

    @Override public void goToFollowersList(String idUser) {
        Intent intent =
          UserFollowsContainerActivity.getIntent(getActivity(), idUser, UserFollowingRelationship.FOLLOWERS);
        startActivity(intent);
    }

    @Override public void goToFollowingList(String idUser) {
        Intent intent =
          UserFollowsContainerActivity.getIntent(getActivity(), idUser, UserFollowingRelationship.FOLLOWING);
        startActivity(intent);
    }

    @Override public void renderLastShots(List<ShotModel> shots) {
        latestsShotsAdapter.setShots(shots);
        latestsShotsAdapter.notifyDataSetChanged();
    }

    @Override public void showUnfollowConfirmation(String username) {
        new AlertDialog.Builder(getActivity()).setMessage(String.format(getString(R.string.unfollow_dialog_message),
          username)).setPositiveButton(getString(R.string.unfollow_dialog_yes), new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {
                profilePresenter.confirmUnfollow();
            }
        }).setNegativeButton(getString(R.string.unfollow_dialog_no), null).create().show();
    }

    @Override public void goToAllShots(String idUser) {
        startActivity(AllShotsActivity.newIntent(getActivity(), idUser, profilePresenter.isCurrentUser()));
    }

    @Override public void showLatestShots() {
        shotsList.setVisibility(View.VISIBLE);
    }

    @Override public void hideLatestShots() {
        shotsList.setVisibility(View.GONE);
    }

    @Override public void showLatestShotsEmpty() {
        shotsListEmpty.setVisibility(View.VISIBLE);
    }

    @Override public void hideLatestShotsEmpty() {
        shotsListEmpty.setVisibility(View.GONE);
    }

    @Override public void showLoadingPhoto() {
        avatarImageView.setVisibility(View.INVISIBLE);
        avatarLoadingView.setVisibility(View.VISIBLE);
    }

    @Override public void hideLoadingPhoto() {
        avatarImageView.setVisibility(View.VISIBLE);
        avatarLoadingView.setVisibility(View.GONE);
    }

    @Override public void showRemovePhotoConfirmation() {
        new AlertDialog.Builder(getActivity()).setMessage(R.string.photo_edit_remove_confirmation)
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
            analyticsTool.analyticsStart(getContext(), analyticsScreenMe);
        } else {
            analyticsTool.analyticsStart(getContext(), analyticsScreenUserProfile);
        }
    }

    @Override public void showVerifiedUser() {
        userVerified.setVisibility(View.VISIBLE);
    }

    @Override public void hideVerifiedUser() {
        userVerified.setVisibility(View.GONE);
    }

    @Override public void showStreamsCount() {
        streamsCountView.setVisibility(View.VISIBLE);
    }

    @Override public void setStreamsCount(Integer streamCount) {
        streamsCountView.setText(String.valueOf(streamCount));
    }

    @Override public void hideStreamsCount() {
        streamsCountView.setVisibility(View.GONE);
    }

    @Override public void refreshSuggestedPeople(List<UserModel> suggestedPeople) {
        getSuggestedPeopleAdapter().setItems(suggestedPeople);
        getSuggestedPeopleAdapter().notifyDataSetChanged();
    }

    @Override public void follow(int position) {
        suggestedPeoplePresenter.followUser(getSuggestedPeopleAdapter().getItem(position));
    }

    @Override public void unFollow(final int position) {
        final UserModel userModel = getSuggestedPeopleAdapter().getItem(position);
        new AlertDialog.Builder(getActivity()).setMessage("Unfollow " + userModel.getUsername() + "?")
          .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                  suggestedPeoplePresenter.unfollowUser(userModel);
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

    @Override public void showHolderContextMenu(ShotModel shot) {
        showAuthorContextMenuWithPin(shot);
    }

    @Override public void goToReport(String sessionToken, ShotModel shotModel) {
        Intent browserIntent =
          new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(reportBaseUrl, sessionToken, shotModel.getIdShot())));
        startActivity(browserIntent);
    }

    @Override public void showEmailNotConfirmedError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(getString(R.string.alert_report_confirmed_email_message))
          .setTitle(getString(R.string.alert_report_confirmed_email_title))
          .setPositiveButton(getString(R.string.alert_report_confirmed_email_ok), null);

        builder.create().show();
    }

    @Override public void showContextMenu(final ShotModel shotModel) {
        getBaseContextMenuOptions(shotModel).addAction(R.string.report_context_menu_report, new Runnable() {
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
        getBaseContextMenuOptions(shotModel).addAction(R.string.report_context_menu_delete, new Runnable() {
              @Override public void run() {
                  openDeleteShotConfirmation(shotModel);
              }
          }).show();
    }

    @Override public void showContextMenuWithUnblock(final ShotModel shotModel) {
        getBaseContextMenuOptions(shotModel).addAction(R.string.report_context_menu_unblock, new Runnable() {
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
          .setPositiveButton(getString(R.string.block_user_dialog_block), new DialogInterface.OnClickListener() {
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
        feedbackMessage.show(getView(), R.string.user_banned);
    }

    @Override public void showUserUnbanned() {
        feedbackMessage.show(getView(), R.string.user_unbanned);
    }

    @Override public void showAuthorContextMenuWithoutPin(final ShotModel shotModel) {
        getBaseContextMenuOptions(shotModel).addAction(R.string.report_context_menu_delete, new Runnable() {
              @Override public void run() {
                  openDeleteShotConfirmation(shotModel);
              }
          }).show();
    }

    @Override public void notifyPinnedShot(ShotModel shotModel) {
        /* no-op */
    }

    @Override public void showPinned() {
        /* no-op */
    }

    private CustomContextMenu.Builder getBaseContextMenuOptions(final ShotModel shotModel) {
        return new CustomContextMenu.Builder(getActivity()).addAction(R.string.menu_share_shot_via_shootr,
          new Runnable() {
              @Override public void run() {
                  profilePresenter.shareShot(shotModel);
              }
          }).addAction(R.string.menu_share_shot_via, new Runnable() {
            @Override public void run() {
                shareShot(shotModel);
            }
        }).addAction(R.string.menu_copy_text, new Runnable() {
            @Override public void run() {
                Clipboard.copyShotComment(getActivity(), shotModel);
            }
        });
    }

    @Override public void notifyDeletedShot(ShotModel shotModel) {
        profilePresenter.refreshLatestShots();
    }
    //endregion
}
