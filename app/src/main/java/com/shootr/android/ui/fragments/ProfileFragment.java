package com.shootr.android.ui.fragments;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.cocosw.bottomsheet.BottomSheet;
import com.path.android.jobqueue.JobManager;
import com.shootr.android.data.SessionManager;
import com.shootr.android.task.events.shots.LatestShotsResultEvent;
import com.shootr.android.task.jobs.follows.GetFollowUnfollowUserOnlineJob;
import com.shootr.android.task.jobs.shots.GetLastShotsJob;
import com.shootr.android.ui.activities.ProfileEditActivity;
import com.shootr.android.ui.adapters.TimelineAdapter;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.util.PicassoWrapper;
import com.shootr.android.util.TimeUtils;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.shootr.android.ShootrApplication;
import com.shootr.android.R;
import com.shootr.android.db.objects.FollowEntity;
import com.shootr.android.db.objects.UserEntity;
import com.shootr.android.exception.ShootrError;
import com.shootr.android.service.ShootrServerException;
import com.shootr.android.service.dataservice.dto.UserDtoFactory;
import com.shootr.android.task.events.CommunicationErrorEvent;
import com.shootr.android.task.events.ConnectionNotAvailableEvent;
import com.shootr.android.task.events.follows.FollowUnFollowResultEvent;
import com.shootr.android.task.events.profile.UploadProfilePhotoEvent;
import com.shootr.android.task.events.profile.UserInfoResultEvent;
import com.shootr.android.task.jobs.follows.GetFollowUnFollowUserOfflineJob;
import com.shootr.android.task.jobs.profile.GetUserInfoJob;
import com.shootr.android.task.jobs.profile.RemoveProfilePhotoJob;
import com.shootr.android.task.jobs.profile.UploadProfilePhotoJob;
import com.shootr.android.ui.activities.UserFollowsContainerActivity;
import com.shootr.android.ui.base.BaseActivity;
import com.shootr.android.ui.base.BaseFragment;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.widgets.FollowButton;
import com.shootr.android.util.ErrorMessageFactory;
import com.shootr.android.util.FileChooserUtils;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class ProfileFragment extends BaseFragment {

    private static final int REQUEST_CHOOSE_PHOTO = 1;
    private static final int REQUEST_TAKE_PHOTO = 2;

    public static final String ARGUMENT_USER = "user";
    public static final String TAG = "profile";

    @InjectView(R.id.profile_name) TextView nameTextView;
    @InjectView(R.id.profile_rank) TextView rankTextView;
    @InjectView(R.id.profile_bio) TextView bioTextView;
    @InjectView(R.id.profile_website) TextView websiteTextView;
    @InjectView(R.id.profile_avatar) ImageView avatarImageView;

    @InjectView(R.id.profile_marks_points) TextView pointsTextView;
    @InjectView(R.id.profile_marks_followers) TextView followersTextView;
    @InjectView(R.id.profile_marks_following_text) TextView followingTextView;

    @InjectView(R.id.profile_follow_button) FollowButton followButton;

    @InjectView(R.id.profile_shots_empty) View shotsListEmpty;
    @InjectView(R.id.profile_shots_list) ViewGroup shotsList;

    @InjectView(R.id.profile_avatar_loading) ProgressBar avatarLoadingView;

    @Inject Bus bus;
    @Inject PicassoWrapper picasso;
    @Inject JobManager jobManager;
    @Inject TimeUtils timeUtils;
    @Inject SessionManager sessionManager;
    @Inject ErrorMessageFactory errorMessageFactory;


    // Args
    Long idUser;

    UserEntity currentUser;
    UserModel user;
    private View.OnClickListener avatarClickListener;
    private BottomSheet.Builder editPhotoBottomSheet;
    private boolean uploadingPhoto;

    public static ProfileFragment newInstance(Long idUser) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle arguments = new Bundle();
        //TODO  pasar idUser
        arguments.putSerializable(ARGUMENT_USER, idUser);
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
    }

    private void injectArguments() {
        Bundle arguments = getArguments();
        idUser = (Long) arguments.getSerializable(ARGUMENT_USER);
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
            boolean canRemovePhoto = sessionManager.getCurrentUser().getPhoto() != null;
            editPhotoBottomSheet = new BottomSheet.Builder(getActivity()).title(R.string.change_photo)
              .sheet(canRemovePhoto ? R.menu.profile_photo_bottom_sheet_remove : R.menu.profile_photo_bottom_sheet) //TODO right now there is no other way to hide an element
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
            avatarImageView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    editPhotoBottomSheet.show();
                }
            });
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
        startActivityForResult(Intent.createChooser(intent, getString(R.string.photo_edit_choose)),
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
            }else if (requestCode == REQUEST_TAKE_PHOTO) {
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
        avatarImageView.setVisibility(View.GONE);
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
        Context context = getActivity();
        currentUser = ShootrApplication.get(context).getCurrentUser();

        GetUserInfoJob job = ShootrApplication.get(context).getObjectGraph().get(GetUserInfoJob.class);
        job.init(idUser);
        jobManager.addJobInBackground(job);

        loadLatestShots();
        //TODO loading
    }

    public void startFollowUnfollowUserJob(UserEntity currentUser, Context context, int followType){
        GetFollowUnFollowUserOfflineJob job2 = ShootrApplication.get(context).getObjectGraph().get(
          GetFollowUnFollowUserOfflineJob.class);
        job2.init(currentUser,idUser,followType);
        jobManager.addJobInBackground(job2);

        GetFollowUnfollowUserOnlineJob
          job = ShootrApplication.get(context).getObjectGraph().get(GetFollowUnfollowUserOnlineJob.class);
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
        if (event.getResult() != null) {
            setUserInfo(event.getResult());
        }
    }

    private void setTitle(String title) {
        ((BaseActivity) getActivity()).getSupportActionBar().setTitle(title);
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
        String favTeamName = user.getFavoriteTeamName();
        String bio = user.getBio();
        if (bio == null) {
            bio = "";
        }
        bioTextView.setText(favTeamName == null ? bio : getString(R.string.profile_bio_format, favTeamName, bio));
        setMainButtonStatus(user.getRelationship());
    }

    private void setMainButtonStatus(int followRelationship) {
        if(followRelationship == FollowEntity.RELATIONSHIP_OWN){
            followButton.setEditProfile();
        }else{
            followButton.setFollowing(followRelationship == FollowEntity.RELATIONSHIP_FOLLOWING);
        }
    }

    @OnClick(R.id.profile_marks_following_box)
    public void openFollowingList() {
        openUserFollowsList(UserFollowsFragment.FOLLOWING);
    }

    @OnClick(R.id.profile_marks_followers_box)
    public void openFollowersList() {
        openUserFollowsList(UserFollowsFragment.FOLLOWERS);
    }

    @OnClick(R.id.profile_follow_button)
    public void onMainButonClick() {
        if (followButton.isEditProfile()) {
            editProfile();
        }else if (followButton.isFollowing()) {
            unfollowUser();
        } else {
            followUser();
        }
    }

    public void followUser(){
        startFollowUnfollowUserJob(currentUser, getActivity(), UserDtoFactory.FOLLOW_TYPE);
    }

    public void unfollowUser(){

        new AlertDialog.Builder(getActivity()).setMessage("Unfollow "+user.getUsername()+"?")
          .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                  startFollowUnfollowUserJob(currentUser, getActivity(), UserDtoFactory.UNFOLLOW_TYPE);

              }
          })
          .setNegativeButton("No", null)
          .create()
          .show();

    }

    private void openUserFollowsList(int followType) {
        if(idUser==null){
            return;
        }
        startActivityForResult(UserFollowsContainerActivity.getIntent(getActivity(), idUser, followType), 677);
    }

    private void loadLatestShots() {
        GetLastShotsJob getLastShotsJob =
          ShootrApplication.get(getActivity()).getObjectGraph().get(GetLastShotsJob.class);

        getLastShotsJob.init(idUser);

        jobManager.addJobInBackground(getLastShotsJob);
    }

    @Subscribe
    public void onLatestShotsLoaded(LatestShotsResultEvent event) {
        List<ShotModel> shots = event.getResult();
        if (shots != null && !shots.isEmpty()) {
            shotsList.removeAllViews();
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            for (ShotModel shot : shots) {
                View shotView = inflater.inflate(R.layout.item_list_shot, shotsList, false);
                TimelineAdapter.ViewHolder vh = new TimelineAdapter.ViewHolder(shotView, avatarClickListener);
                bindShotView(vh, shot);
                shotsList.addView(shotView);
            }
            shotsList.setVisibility(View.VISIBLE);
            shotsListEmpty.setVisibility(View.GONE);
        } else {
            shotsList.setVisibility(View.GONE);
            shotsListEmpty.setVisibility(View.VISIBLE);
        }
    }

    private void bindShotView(TimelineAdapter.ViewHolder vh, ShotModel item) {
        vh.name.setText(item.getUsername());

        vh.text.setText(item.getComment());

        long timestamp = item.getCsysBirth().getTime();
        vh.timestamp.setText(timeUtils.getElapsedTime(getActivity(), timestamp));
        TimelineAdapter.addLinks(vh.text);

        String photo = item.getPhoto();
        picasso.loadProfilePhoto(photo).into(vh.avatar);
        vh.avatar.setTag(vh);
    }

    private void onShotAvatarClick(View v) {
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
        return idUser!= null && idUser.equals(sessionManager.getCurrentUserId());
    }
}
