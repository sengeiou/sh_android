package com.shootr.android.ui.fragments;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cocosw.bottomsheet.BottomSheet;
import com.path.android.jobqueue.JobManager;
import com.shootr.android.R;
import com.shootr.android.ShootrApplication;
import com.shootr.android.data.bus.Main;
import com.shootr.android.data.entity.FollowEntity;
import com.shootr.android.domain.User;
import com.shootr.android.domain.exception.ShootrError;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.exception.ShootrServerException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.user.GetUserByUsernameInteractor;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.service.dataservice.dto.UserDtoFactory;
import com.shootr.android.task.events.CommunicationErrorEvent;
import com.shootr.android.task.events.ConnectionNotAvailableEvent;
import com.shootr.android.task.events.follows.FollowUnFollowResultEvent;
import com.shootr.android.task.events.profile.UploadProfilePhotoEvent;
import com.shootr.android.task.events.profile.UserInfoResultEvent;
import com.shootr.android.task.events.shots.LatestShotsResultEvent;
import com.shootr.android.task.jobs.follows.GetFollowUnFollowUserOfflineJob;
import com.shootr.android.task.jobs.follows.GetFollowUnfollowUserOnlineJob;
import com.shootr.android.task.jobs.profile.GetUserInfoJob;
import com.shootr.android.task.jobs.profile.RemoveProfilePhotoJob;
import com.shootr.android.task.jobs.profile.UploadProfilePhotoJob;
import com.shootr.android.task.jobs.shots.GetLatestShotsJob;
import com.shootr.android.ui.activities.EventDetailActivity;
import com.shootr.android.ui.activities.PhotoViewActivity;
import com.shootr.android.ui.activities.ProfileContainerActivity;
import com.shootr.android.ui.activities.ProfileEditActivity;
import com.shootr.android.ui.activities.ShotDetailActivity;
import com.shootr.android.ui.activities.UserFollowsContainerActivity;
import com.shootr.android.ui.adapters.TimelineAdapter;
import com.shootr.android.ui.base.BaseFragment;
import com.shootr.android.ui.base.BaseToolbarActivity;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.model.mappers.UserModelMapper;
import com.shootr.android.ui.widgets.FollowButton;
import com.shootr.android.util.AndroidTimeUtils;
import com.shootr.android.util.ErrorMessageFactory;
import com.shootr.android.util.FileChooserUtils;
import com.shootr.android.util.PicassoWrapper;
import com.shootr.android.util.UsernameClickListener;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import timber.log.Timber;

public class ProfileFragment extends BaseFragment {

    private static final int REQUEST_CHOOSE_PHOTO = 1;
    private static final int REQUEST_TAKE_PHOTO = 2;

    public static final String ARGUMENT_USER = "user";
    public static final String ARGUMENT_USERNAME = "username";
    public static final String TAG = "profile";

    //region injected
    @InjectView(R.id.profile_name) TextView nameTextView;
    @InjectView(R.id.profile_bio) TextView bioTextView;
    @InjectView(R.id.profile_website) TextView websiteTextView;
    @InjectView(R.id.profile_avatar) ImageView avatarImageView;

    @InjectView(R.id.profile_watching_container) View watchingContainerView;
    @InjectView(R.id.profile_watching_title) TextView watchingTitleView;

    @InjectView(R.id.profile_marks_followers) TextView followersTextView;
    @InjectView(R.id.profile_marks_following) TextView followingTextView;

    @InjectView(R.id.profile_follow_button) FollowButton followButton;

    @InjectView(R.id.profile_shots_empty) View shotsListEmpty;
    @InjectView(R.id.profile_shots_list) ViewGroup shotsList;

    @InjectView(R.id.profile_avatar_loading) ProgressBar avatarLoadingView;

    @Inject @Main Bus bus;
    @Inject PicassoWrapper picasso;
    @Inject JobManager jobManager;
    @Inject AndroidTimeUtils timeUtils;
    @Inject SessionRepository sessionRepository;
    @Inject ErrorMessageFactory errorMessageFactory;

    @Inject
    GetUserByUsernameInteractor getUserByUsernameInteractor;
    @Inject
    UserModelMapper userModelMapper;

    //endregion

    // Args
    String idUser;
    String username;

    UserModel user;
    private View.OnClickListener avatarClickListener;
    private View.OnClickListener imageClickListener;
    private UsernameClickListener usernameClickListener;
    private BottomSheet.Builder editPhotoBottomSheet;
    private boolean uploadingPhoto;
    private TimelineAdapter latestsShotsAdapter;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectArguments();
        setHasOptionsMenu(true);
        avatarClickListener = new View.OnClickListener() {
            @Override public void onClick(View v) {
                onShotAvatarClick(v);
            }
        };
        imageClickListener = new View.OnClickListener() {
            @Override public void onClick(View v) {
                int position = ((TimelineAdapter.ViewHolder) v.getTag()).position;
                openShotImage(position);
            }
        };
        usernameClickListener =  new UsernameClickListener() {
            @Override
            public void onClick(String username) {
                goToUserProfile(username);
            }
        };
    }

    private void startProfileContainerActivity(String username) {
        Intent intentForUser = ProfileContainerActivity.getIntentWithUsername(getActivity(), username);
        startActivity(intentForUser);
    }

    private void goToUserProfile(String username) {
        startProfileContainerActivity(username);
    }

    private void userNotFoundNotification(){
        Toast.makeText(getActivity(), "User not found", Toast.LENGTH_LONG).show();
    }

    private void openShotImage(int position) {
        if (latestsShotsAdapter != null) {
            ShotModel shot = latestsShotsAdapter.getItem(position);
            String imageUrl = shot.getImage();
            if (imageUrl != null) {
                Intent intentForImage = PhotoViewActivity.getIntentForActivity(getActivity(), imageUrl);
                startActivity(intentForImage);
            }
        }
    }

    private void injectArguments() {
        Bundle arguments = getArguments();
        idUser = (String) arguments.getSerializable(ARGUMENT_USER);
        username = (String) arguments.getSerializable(ARGUMENT_USERNAME);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.register(this);
        if (!uploadingPhoto) {
            retrieveUserInfo();
        }
    }

    @Override
    public void onPause() {
        super.onDetach();
        bus.unregister(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupPhotoBottomSheet();
    }


    private void setupPhotoBottomSheet() {
        //TODO quitar opción de hacer foto si no hay hasSystemFeature(PackageManager.FEATURE_CAMERA)
        if (isCurrentUser()) {
            boolean canRemovePhoto = sessionRepository.getCurrentUser().getPhoto() != null;
            editPhotoBottomSheet = new BottomSheet.Builder(getActivity()).title(R.string.change_photo).sheet(
              canRemovePhoto ? R.menu.profile_photo_bottom_sheet_remove : R.menu.profile_photo_bottom_sheet) //TODO right now there is no other way to hide an element
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
                      }
                  }
              });
        }
    }

    @OnClick(R.id.profile_avatar)
    public void onAvatarClick() {
        if (isCurrentUser()) {
            if (editPhotoBottomSheet != null) {
                editPhotoBottomSheet.show();
            }
        } else {
            openPhotoBig();
        }
    }

    private void openPhotoBig() {
        FragmentActivity context = getActivity();
        String preview = user.getPhoto();
        boolean canOpenPhoto = preview != null;
        if (canOpenPhoto) {
            String photoBig = preview.replace("_thumbnail", ""); // <-- Chapuza Carlos, chapuza!!
            Intent intentForPhoto = PhotoViewActivity.getIntentForActivity(context, photoBig, preview);
            startActivity(intentForPhoto);
        }
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
          .setNegativeButton(R.string.cancel, null)
          .show();
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

    @Subscribe
    public void onPhotoUploaded(UploadProfilePhotoEvent event) {
        uploadingPhoto = false;
        UserModel updateduser = event.getResult();
        hideLoadingPhoto();
        setUserInfo(updateduser);
        retrieveUserInfo();
        setupPhotoBottomSheet(); //TODO needed to refresh the remove button visibility. Remove this when it is not neccesary
    }

    @Subscribe
    public void onConnectionNotAvailable(ConnectionNotAvailableEvent event) {
        Toast.makeText(getActivity(), R.string.connection_lost, Toast.LENGTH_SHORT).show();
        hideLoadingPhoto();
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
        Toast.makeText(getActivity(), messageForError, Toast.LENGTH_SHORT).show();
        hideLoadingPhoto();
    }

    private void retrieveUserInfo() {
        if(idUser != null){
            loadProfileUsingJob(idUser);
        }else{
            getUserByUsernameInteractor.searchUserByUsername(username, new Interactor.Callback<User>() {
                @Override
                public void onLoaded(User user) {
                    if (user != null) {
                        loadProfileUsingJob(user.getIdUser());
                    } else {
                        userNotFoundNotification();
                    }
                }
            }, new Interactor.ErrorCallback() {
                @Override
                public void onError(ShootrException error) {
                    Timber.e(error, "Error while searching user by username");
                }
            });
        }

        //TODO loading
    }

    private void loadProfileUsingJob(String idUser) {
        Context context = getActivity();
        GetUserInfoJob job = ShootrApplication.get(context).getObjectGraph().get(GetUserInfoJob.class);
        job.init(idUser);
        jobManager.addJobInBackground(job);
        loadLatestShots();
    }

    public void startFollowUnfollowUserJob(Context context, int followType) {
        GetFollowUnFollowUserOfflineJob job2 =
          ShootrApplication.get(context).getObjectGraph().get(GetFollowUnFollowUserOfflineJob.class);
        job2.init(idUser, followType);
        jobManager.addJobInBackground(job2);

        GetFollowUnfollowUserOnlineJob job =
          ShootrApplication.get(context).getObjectGraph().get(GetFollowUnfollowUserOnlineJob.class);
        jobManager.addJobInBackground(job);
    }

    @Subscribe
    public void userInfoReceived(UserInfoResultEvent event) {
        if (event.getResult() != null) {
            setUserInfo(event.getResult());
        }
    }

    @Subscribe
    public void onFollowUnfollowReceived(FollowUnFollowResultEvent event) {
        Pair<String, Boolean> result = event.getResult();
        if (result != null) {
            String idUserFromResult = result.first;
            Boolean following = result.second;
            if (idUserFromResult.equals(this.idUser)) {
                followButton.setFollowing(following);
            }
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

        if (user.getEventWatchingTitle() != null) {
            watchingTitleView.setText(user.getEventWatchingTitle());
            watchingContainerView.setVisibility(View.VISIBLE);
        } else {
            watchingContainerView.setVisibility(View.GONE);
        }

        String photo = user.getPhoto();
        boolean isValidPhoto = photo != null && !photo.isEmpty();
        if (isValidPhoto) {
            picasso.loadProfilePhoto(photo).into(avatarImageView);
        } else {
            if (isCurrentUser()) {
                avatarImageView.setImageResource(R.drawable.profile_photo_add);
            } else {
                avatarImageView.setImageResource(R.drawable.ic_contact_picture_default);
            }
        }
    }

    private void setUserInfo(UserModel user) {
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

    private void setMainButtonStatus(int followRelationship) {
        if (isCurrentUser()) {
            followButton.setEditProfile();
        } else {
            boolean isFollowing = followRelationship == FollowEntity.RELATIONSHIP_FOLLOWING
              || followRelationship == FollowEntity.RELATIONSHIP_BOTH;
            followButton.setFollowing(isFollowing);
        }
    }

    @OnClick(R.id.profile_marks_following)
    public void openFollowingList() {
        openUserFollowsList(UserFollowsFragment.FOLLOWING);
    }

    @OnClick(R.id.profile_marks_followers)
    public void openFollowersList() {
        openUserFollowsList(UserFollowsFragment.FOLLOWERS);
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

    @OnClick(R.id.profile_watching_container)
    public void onWatchingClick() {
        startActivity(EventDetailActivity.getIntent(getActivity(), user.getEventWatchingId()));
    }

    public void followUser() {
        startFollowUnfollowUserJob(getActivity(), UserDtoFactory.FOLLOW_TYPE);
    }

    public void unfollowUser() {

        new AlertDialog.Builder(getActivity()).setMessage("Unfollow " + user.getUsername() + "?")
          .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                  startFollowUnfollowUserJob(getActivity(), UserDtoFactory.UNFOLLOW_TYPE);
              }
          })
          .setNegativeButton("No", null)
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

    @Subscribe
    public void onLatestShotsLoaded(LatestShotsResultEvent event) {
        List<ShotModel> shots = event.getResult();
        if (shots != null && !shots.isEmpty()) {
            shotsList.removeAllViews();
            latestsShotsAdapter =
              new TimelineAdapter(getActivity(), picasso, avatarClickListener,
                      imageClickListener, usernameClickListener, timeUtils){
                  @Override protected boolean shouldShowTag() {
                      return true;
                  }
              };
            latestsShotsAdapter.setShots(shots);
            for (int i = 0; i < latestsShotsAdapter.getCount(); i++) {
                View shotView = latestsShotsAdapter.getView(i, null, shotsList);
                setShotItemBackgroundRetainPaddings(shotView);
                final int finalI = i;
                shotView.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        ShotModel shot = latestsShotsAdapter.getItem(finalI);
                        openShot(shot);
                    }
                });
                shotsList.addView(shotView);
            }
            shotsList.setVisibility(View.VISIBLE);
            shotsListEmpty.setVisibility(View.GONE);
        } else {
            shotsList.setVisibility(View.GONE);
            shotsListEmpty.setVisibility(View.VISIBLE);
        }
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
}
