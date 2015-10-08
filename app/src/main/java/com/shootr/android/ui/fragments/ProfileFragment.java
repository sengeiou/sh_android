package com.shootr.android.ui.fragments;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
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
import com.path.android.jobqueue.JobManager;
import com.shootr.android.R;
import com.shootr.android.ShootrApplication;
import com.shootr.android.data.bus.Main;
import com.shootr.android.data.entity.FollowEntity;
import com.shootr.android.domain.exception.ShootrError;
import com.shootr.android.domain.exception.ShootrServerException;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.task.events.CommunicationErrorEvent;
import com.shootr.android.task.events.profile.UserInfoResultEvent;
import com.shootr.android.task.jobs.follows.GetUsersFollowsJob;
import com.shootr.android.task.jobs.profile.GetUserInfoJob;
import com.shootr.android.task.jobs.profile.RemoveProfilePhotoJob;
import com.shootr.android.task.jobs.profile.UploadProfilePhotoJob;
import com.shootr.android.task.jobs.shots.GetLatestShotsJob;
import com.shootr.android.ui.activities.AllShotsActivity;
import com.shootr.android.ui.activities.ChangePasswordActivity;
import com.shootr.android.ui.activities.ListingActivity;
import com.shootr.android.ui.activities.NewStreamActivity;
import com.shootr.android.ui.activities.PhotoViewActivity;
import com.shootr.android.ui.activities.ProfileContainerActivity;
import com.shootr.android.ui.activities.ProfileEditActivity;
import com.shootr.android.ui.activities.ShotDetailActivity;
import com.shootr.android.ui.activities.StreamDetailActivity;
import com.shootr.android.ui.activities.SupportActivity;
import com.shootr.android.ui.activities.UserFollowsContainerActivity;
import com.shootr.android.ui.activities.registro.LoginSelectionActivity;
import com.shootr.android.ui.adapters.TimelineAdapter;
import com.shootr.android.ui.adapters.UserListAdapter;
import com.shootr.android.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.android.ui.adapters.listeners.OnNiceShotListener;
import com.shootr.android.ui.adapters.listeners.OnUserClickListener;
import com.shootr.android.ui.adapters.listeners.OnUsernameClickListener;
import com.shootr.android.ui.adapters.listeners.OnVideoClickListener;
import com.shootr.android.ui.base.BaseFragment;
import com.shootr.android.ui.base.BaseToolbarActivity;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.presenter.ProfilePresenter;
import com.shootr.android.ui.presenter.ReportShotPresenter;
import com.shootr.android.ui.presenter.SuggestedPeoplePresenter;
import com.shootr.android.ui.views.ProfileView;
import com.shootr.android.ui.views.ReportShotView;
import com.shootr.android.ui.views.SuggestedPeopleView;
import com.shootr.android.ui.widgets.FollowButton;
import com.shootr.android.ui.widgets.SuggestedPeopleListView;
import com.shootr.android.util.Clipboard;
import com.shootr.android.util.CustomContextMenu;
import com.shootr.android.util.ErrorMessageFactory;
import com.shootr.android.util.FeedbackMessage;
import com.shootr.android.util.FileChooserUtils;
import com.shootr.android.util.ImageLoader;
import com.shootr.android.util.IntentFactory;
import com.shootr.android.util.Intents;
import com.shootr.android.util.MenuItemValueHolder;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class ProfileFragment extends BaseFragment implements ProfileView, SuggestedPeopleView, UserListAdapter.FollowUnfollowAdapterCallback,
  ReportShotView {

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
    @Bind(R.id.profile_listing) TextView listingText;
    @Bind(R.id.profile_listing_number) TextView listingNumber;

    @Bind(R.id.profile_open_stream_container) View openStreamContainerView;

    @Bind(R.id.profile_marks_followers) TextView followersTextView;
    @Bind(R.id.profile_marks_following) TextView followingTextView;

    @Bind(R.id.profile_follow_button) FollowButton followButton;

    @Bind(R.id.profile_shots_empty) View shotsListEmpty;
    @Bind(R.id.profile_shots_list) ViewGroup shotsList;

    @Bind(R.id.profile_all_shots_container) View allShotContainer;

    @Bind(R.id.profile_avatar_loading) ProgressBar avatarLoadingView;

    @Bind(R.id.profile_suggested_people) SuggestedPeopleListView suggestedPeopleListView;

    @BindString(R.string.report_base_url) String reportBaseUrl;

    @Inject @Main Bus bus;
    @Inject ImageLoader imageLoader;
    @Inject JobManager jobManager;
    @Inject SessionRepository sessionRepository;
    @Inject ErrorMessageFactory errorMessageFactory;
    @Inject IntentFactory intentFactory;
    @Inject FeedbackMessage feedbackMessage;

    @Inject ProfilePresenter profilePresenter;
    @Inject SuggestedPeoplePresenter suggestedPeoplePresenter;
    @Inject ReportShotPresenter reportShotPresenter;

    //endregion

    // Args
    String idUser;
    String username;

    UserModel user;
    private OnAvatarClickListener avatarClickListener;
    private OnVideoClickListener videoClickListener;
    private OnUsernameClickListener onUsernameClickListener;
    private OnNiceShotListener onNiceShotListener;
    private boolean uploadingPhoto;
    private TimelineAdapter latestsShotsAdapter;
    private ProgressDialog progress;
    private MenuItemValueHolder logoutMenuItem = new MenuItemValueHolder();
    private MenuItemValueHolder supportMenuItem = new MenuItemValueHolder();
    private MenuItemValueHolder changePasswordMenuItem = new MenuItemValueHolder();
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
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        injectArguments();
        setHasOptionsMenu(true);
        initializeViews();
        initializePresenter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    private void injectArguments() {
        Bundle arguments = getArguments();
        idUser = (String) arguments.getSerializable(ARGUMENT_USER);
        username = (String) arguments.getSerializable(ARGUMENT_USERNAME);
    }
    private void initializeViews() {
        ButterKnife.bind(this, getView());
        avatarClickListener = new OnAvatarClickListener() {
            @Override
            public void onAvatarClick(String userId, View avatarView) {
                onShotAvatarClick(avatarView);
            }
        };

        onUsernameClickListener =  new OnUsernameClickListener() {
            @Override
            public void onUsernameClick(String username) {
                goToUserProfile(username);
            }
        };

        videoClickListener = new OnVideoClickListener() {
            @Override
            public void onVideoClick(String url) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        };

        onNiceShotListener = new OnNiceShotListener() {
            @Override
            public void markNice(String idShot) {
                profilePresenter.markNiceShot(idShot);
            }

            @Override
            public void unmarkNice(String idShot) {
                profilePresenter.unmarkNiceShot(idShot);
            }
        };
        suggestedPeopleListView.setAdapter(getSuggestedPeopleAdapter());
        suggestedPeopleListView.setOnUserClickListener(new OnUserClickListener() {
            @Override public void onUserClick(String idUser) {
                Intent suggestedUserIntent = ProfileContainerActivity.getIntent(getActivity(), idUser);
                startActivity(suggestedUserIntent);
            }
        });
    }

    private void initializePresenter() {
        if (idUser != null) {
            profilePresenter.initializeWithIdUser(this, idUser);
        } else {
            profilePresenter.initializeWithUsername(this, username);
        }
        suggestedPeoplePresenter.initialize(this);
        reportShotPresenter.initialize(this);
    }

    //endregion

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_profile, menu);
        logoutMenuItem.bindRealMenuItem(menu.findItem(R.id.menu_profile_logout));
        supportMenuItem.bindRealMenuItem(menu.findItem(R.id.menu_profile_support));
        changePasswordMenuItem.bindRealMenuItem(menu.findItem(R.id.menu_profile_change_password));
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void goToUserProfile(String username) {
        Intent intentForUser = ProfileContainerActivity.getIntentWithUsername(getActivity(), username);
        startActivity(intentForUser);
    }

    @Override
    public void onResume() {
        super.onResume();
        profilePresenter.resume();
        suggestedPeoplePresenter.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        profilePresenter.resume();
        suggestedPeoplePresenter.pause();
    }

    @OnClick(R.id.profile_avatar)
    public void onAvatarClick() {
        profilePresenter.avatarClicked();
    }

    private void takePhotoFromCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File pictureTemporaryFile = getCameraPhotoFile();
        if (!pictureTemporaryFile.exists()) {
            try {
                pictureTemporaryFile.getParentFile().mkdirs();
                pictureTemporaryFile.createNewFile();
            } catch (IOException e) {
                Timber.e(e, "No se pudo crear el archivo temporal para la foto de perfil");
                //TODO cancelar operación y avisar al usuario
            }
        }
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(pictureTemporaryFile));
        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
    }

    private void choosePhotoFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.photo_edit_gallery)),
          REQUEST_CHOOSE_PHOTO);
    }

    private void removePhoto() {
        new AlertDialog.Builder(getActivity()).setMessage(R.string.photo_edit_remove_confirmation)
          .setPositiveButton(R.string.remove, new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                  RemoveProfilePhotoJob removeProfilePhotoJob =
                    ShootrApplication.get(getActivity()).getObjectGraph().get(RemoveProfilePhotoJob.class);
                  jobManager.addJobInBackground(removeProfilePhotoJob);
              }
          })
          .setNegativeButton(R.string.cancel, null).show();
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            File changedPhotoFile;
            if (requestCode == REQUEST_CHOOSE_PHOTO) {
                Uri selectedImageUri = data.getData();
                changedPhotoFile = new File(FileChooserUtils.getPath(getActivity(), selectedImageUri));
                uploadPhoto(changedPhotoFile);
            } else if (requestCode == REQUEST_TAKE_PHOTO) {
                changedPhotoFile = getCameraPhotoFile();
                uploadPhoto(changedPhotoFile);
            } else {
                String streamId = data.getStringExtra(NewStreamActivity.KEY_STREAM_ID);
                String title = data.getStringExtra(NewStreamActivity.KEY_STREAM_TITLE);
                profilePresenter.streamCreated(streamId);
            }
        }
    }

    private File getCameraPhotoFile() {
        return new File(getActivity().getExternalFilesDir("tmp"), "profileUpload.jpg");
    }

    private void uploadPhoto(File changedPhotoFile) {
        uploadingPhoto = true;
        showLoadingPhoto();
        UploadProfilePhotoJob job =
          ShootrApplication.get(getActivity()).getObjectGraph().get(UploadProfilePhotoJob.class);
        job.init(changedPhotoFile);
        jobManager.addJobInBackground(job);
    }

    private void showLoadingPhoto() {
        avatarImageView.setVisibility(View.INVISIBLE);
        avatarLoadingView.setVisibility(View.VISIBLE);
    }

    private void hideLoadingPhoto() {
        avatarImageView.setVisibility(View.VISIBLE);
        avatarLoadingView.setVisibility(View.GONE);
    }

    @Subscribe
    public void onCommunicationError(CommunicationErrorEvent event) {
        String messageForError;
        Exception exceptionProduced = event.getException();
        if (exceptionProduced != null && exceptionProduced instanceof ShootrServerException) {
            ShootrError shootrError = ((ShootrServerException) exceptionProduced).getShootrError();
            messageForError = errorMessageFactory.getMessageForError(shootrError);
        } else {
            messageForError = errorMessageFactory.getCommunicationErrorMessage();
        }
        feedbackMessage.show(getView(), messageForError);
        hideLoadingPhoto();
    }

    private void loadBasicProfileUsingJob(String idUser) {
        Context context = getActivity();
        GetUserInfoJob job = ShootrApplication.get(context).getObjectGraph().get(GetUserInfoJob.class);
        job.init(idUser);
        jobManager.addJobInBackground(job);
    }

    @Subscribe
    public void userInfoReceived(UserInfoResultEvent event) {
        if (event.getResult() != null) {
            setUserInfo(event.getResult());
        }
    }

    private void setTitle(String title) {
        ((BaseToolbarActivity) getActivity()).getSupportActionBar().setTitle(title);
    }

    private void setBasicUserInfo(UserModel user) {
        this.user = user;
        setTitle(user.getUsername());
        nameTextView.setText(user.getName());
        followingTextView.setText(String.valueOf(user.getNumFollowings()));
        followersTextView.setText(String.valueOf(user.getNumFollowers()));

        String website = user.getWebsite();
        if (website != null) {
            websiteTextView.setText(website);
            websiteTextView.setVisibility(View.VISIBLE);
        } else {
            websiteTextView.setVisibility(View.GONE);
        }

        imageLoader.loadProfilePhoto(user.getPhoto(), avatarImageView);
    }

    public void setUserInfo(UserModel user) {
        setBasicUserInfo(user);
        String bio = user.getBio();
        if (bio != null) {
            bioTextView.setText(bio);
            bioTextView.setVisibility(View.VISIBLE);
        } else {
            bioTextView.setVisibility(View.GONE);
        }
        setMainButtonStatus(user.getRelationship());
    }

    @Override public void showAllShots() {

    }

    @Override public void hideAllShots() {

    }

    private void setMainButtonStatus(int followRelationship) {
        if (isCurrentUser()) {
            followButton.setEditProfile();
        } else {
            boolean isFollowing = followRelationship == FollowEntity.RELATIONSHIP_FOLLOWING
              || followRelationship == FollowEntity.RELATIONSHIP_BOTH;
            setFollowing(isFollowing);
        }
    }

    @OnClick(R.id.profile_marks_following)
    public void openFollowingList() {
        openUserFollowsList(GetUsersFollowsJob.FOLLOWING);
    }

    @OnClick(R.id.profile_marks_followers)
    public void openFollowersList() {
        openUserFollowsList(GetUsersFollowsJob.FOLLOWERS);
    }

    @OnClick(R.id.profile_follow_button)
    public void onMainButonClick() {
        if (followButton.isEditProfile()) {
            editProfile();
        } else if (followButton.isFollowing()) {
            unfollowUser();
        } else {
            followUser();
        }
    }

    @OnClick(R.id.profile_website)
    public void onWebsiteClick() {
        Intent linkIntent = new Intent(Intent.ACTION_VIEW);
        String url = user.getWebsite();
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }
        linkIntent.setData(Uri.parse(url));
        startActivity(linkIntent);
    }

    public void followUser() {
        profilePresenter.follow();
    }

    public void unfollowUser() {
        new AlertDialog.Builder(getActivity()).setMessage(String.format(getString(R.string.unfollow_dialog_message), user.getUsername()))
          .setPositiveButton(getString(R.string.unfollow_dialog_yes), new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                  profilePresenter.unfollow();
              }
          })
          .setNegativeButton(getString(R.string.unfollow_dialog_no), null)
          .create()
          .show();
    }

    private void openUserFollowsList(int followType) {
        if (idUser == null) {
            return;
        }
        startActivityForResult(UserFollowsContainerActivity.getIntent(getActivity(), idUser, followType), 677);
    }

    private void loadLatestShots() {
        GetLatestShotsJob getLatestShotsJob =
          ShootrApplication.get(getActivity()).getObjectGraph().get(GetLatestShotsJob.class);

        getLatestShotsJob.init(idUser);

        jobManager.addJobInBackground(getLatestShotsJob);
    }

    private void shareShot(ShotModel shotModel) {
        Intent shareIntent = intentFactory.shareShotIntent(getActivity(), shotModel);
        Intents.maybeStartActivity(getActivity(), shareIntent);
    }

    private void setShotItemBackgroundRetainPaddings(View shotView) {
        int paddingBottom = shotView.getPaddingBottom();
        int paddingLeft = shotView.getPaddingLeft();
        int paddingRight = shotView.getPaddingRight();
        int paddingTop = shotView.getPaddingTop();
        shotView.setBackgroundDrawable(getSelectableBackground());
        shotView.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
    }

    private void openShot(ShotModel shot) {
        Intent intent = ShotDetailActivity.getIntentForActivity(getActivity(), shot);
        startActivity(intent);
    }

    private Drawable getSelectableBackground() {
        TypedArray a = getActivity().getTheme().obtainStyledAttributes(new int[] { R.attr.selectableItemBackground });
        Drawable drawable = a.getDrawable(0);
        a.recycle();
        return drawable;
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

    private boolean isCurrentUser() {
        return idUser != null && idUser.equals(sessionRepository.getCurrentUserId());
    }

    @Override public void showListingButtonWithCount(Integer listingCount) {
        openStreamContainerView.setVisibility(View.GONE);
        listingContainerView.setVisibility(View.VISIBLE);
        listingNumber.setVisibility(View.VISIBLE);
        listingNumber.setText(String.valueOf(listingCount));
    }

    @Override public void showListingWithoutCount() {
        openStreamContainerView.setVisibility(View.GONE);
        listingContainerView.setVisibility(View.VISIBLE);
        listingNumber.setVisibility(View.GONE);
    }

    @Override public void navigateToListing(String idUser) {
        Intent intent = ListingActivity.getIntent(this.getActivity(), idUser, isCurrentUser());
        this.startActivity(intent);
    }

    @Override public void showLogoutInProgress() {
        if(getActivity() != null) {
            progress = ProgressDialog.show(getActivity(),
              null,
              getActivity().getString(R.string.sign_out_message),
              true);
        }
    }

    @Override public void showError() {
        feedbackMessage.show(getView(), getActivity().getString(R.string.communication_error));
    }

    @Override public void hideLogoutInProgress() {
        if(getActivity() != null) {
            progress.dismiss();
        }
    }

    @Override public void navigateToWelcomeScreen() {
        if(getActivity() != null) {
            new Handler().postDelayed(new Runnable() {
                @Override public void run() {
                    hideLogoutInProgress();
                    redirectToWelcome();
                }
            }, LOGOUT_DISMISS_DELAY);
        }
    }

    private void redirectToWelcome() {
        Intent intent = new Intent(getActivity(), LoginSelectionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
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

    @Override public void showOpenStream() {
        listingContainerView.setVisibility(View.GONE);
        openStreamContainerView.setVisibility(View.VISIBLE);
    }

    @Override public void navigateToCreatedStreamDetail(String streamId) {
        startActivity(StreamDetailActivity.getIntent(getActivity(), streamId));
    }

    @Override public void showShotShared() {
        feedbackMessage.show(getView(), getActivity().getString(R.string.shot_shared_message));
    }

    @OnClick(R.id.profile_listing)
    public void onListingClick() {
        profilePresenter.clickListing();
    }

    @OnClick(R.id.profile_all_shots_button)
    public void onAllShotsClick() {
        startActivity(AllShotsActivity.newIntent(getActivity(), user.getIdUser()));
    }

    @Override public void renderSuggestedPeopleList(List<UserModel> users) {
        suggestedPeopleAdapter.setItems(users);
        suggestedPeopleAdapter.notifyDataSetChanged();
    }

    private UserListAdapter getSuggestedPeopleAdapter() {
        if (suggestedPeopleAdapter == null) {
            suggestedPeopleAdapter = new UserListAdapter(getActivity(), imageLoader);
            suggestedPeopleAdapter.setCallback(this);
        }
        return suggestedPeopleAdapter;
    }

    @Override public void showError(String messageForError) {
        feedbackMessage.show(getView(), messageForError);
    }

    @Override public void showEditProfileButton() {

    }

    @Override public void showFollowButton() {

    }

    @Override public void showUnfollowButton() {

    }

    @Override public void showAddPhoto() {

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
          .listener(new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                  switch (which) {
                      case R.id.menu_photo_gallery:
                          choosePhotoFromGallery();
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
          });

        if (showRemove) {
            menuBuilder.sheet(R.id.menu_photo_remove, R.drawable.ic_photo_remove, R.string.photo_edit_remove);
        }

        menuBuilder.show();
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
        new AlertDialog.Builder(getActivity()).setMessage("Unfollow "+userModel.getUsername() + "?")
          .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                  suggestedPeoplePresenter.unfollowUser(userModel);
              }
          })
          .setNegativeButton("No", null)
          .create()
          .show();
    }

    @Override
    public void setFollowing(Boolean following) {
        followButton.setFollowing(following);
    }

    @OnClick(R.id.profile_open_stream)
    public void onOpenStreamClick() {
        startActivityForResult(new Intent(getActivity(), NewStreamActivity.class), REQUEST_NEW_STREAM);
    }

    @Override public void goToReport(String sessionToken, ShotModel shotModel) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(reportBaseUrl,
          sessionToken,
          shotModel.getIdShot())));
        startActivity(browserIntent);
    }

    @Override public void showEmailNotConfirmedError() {
        AlertDialog.Builder builder =
          new AlertDialog.Builder(getActivity());

        builder.setMessage(getString(R.string.alert_report_confirmed_email_message))
          .setTitle(getString(R.string.alert_report_confirmed_email_title))
          .setPositiveButton(getString(R.string.alert_report_confirmed_email_ok), null);

        builder.create().show();
    }

    @Override public void showContextMenu(final ShotModel shotModel) {
        new CustomContextMenu.Builder(getActivity())
          .addAction(getActivity().getString(R.string.menu_share_shot_via_shootr), new Runnable() {
              @Override public void run() {
                  profilePresenter.shareShot(shotModel);
              }
          })
          .addAction(getActivity().getString(R.string.menu_share_shot_via), new Runnable() {
              @Override public void run() {
                  shareShot(shotModel);
              }
          }).addAction(getActivity().getString(R.string.menu_copy_text), new Runnable() {
            @Override public void run() {
                Clipboard.copyShotComment(getActivity(), shotModel);
            }
        }).addAction(getActivity().getString(R.string.report_context_menu_report), new Runnable() {
            @Override public void run() {
                reportShotPresenter.report(shotModel);
            }
        }).show();
    }

    @Override public void showHolderContextMenu(final ShotModel shotModel) {
        new CustomContextMenu.Builder(getActivity())
          .addAction(getActivity().getString(R.string.menu_share_shot_via_shootr), new Runnable() {
              @Override public void run() {
                  profilePresenter.shareShot(shotModel);
              }
          })
          .addAction(getActivity().getString(R.string.menu_share_shot_via), new Runnable() {
              @Override public void run() {
                  shareShot(shotModel);
              }
          }).addAction(getActivity().getString(R.string.menu_copy_text), new Runnable() {
            @Override public void run() {
                Clipboard.copyShotComment(getActivity(), shotModel);
            }
        }).addAction(getActivity().getString(R.string.report_context_menu_delete), new Runnable() {
            @Override public void run() {
                openDeleteConfirmation(shotModel);
            }
        }).show();
    }

    private void openDeleteConfirmation(final ShotModel shotModel) {
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

    @Override public void notifyDeletedShot(ShotModel shotModel) {
        profilePresenter.refreshLatestShots();
    }
}
