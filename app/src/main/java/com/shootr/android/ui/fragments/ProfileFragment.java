package com.shootr.android.ui.fragments;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.path.android.jobqueue.JobManager;
import com.shootr.android.task.events.shots.LatestShotsResultEvent;
import com.shootr.android.task.jobs.ShootrBaseJob;
import com.shootr.android.task.jobs.shots.GetLastShotsJob;
import com.shootr.android.ui.adapters.TimelineAdapter;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.util.TimeUtils;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.shootr.android.ShootrApplication;
import com.shootr.android.R;
import com.shootr.android.db.objects.FollowEntity;
import com.shootr.android.db.objects.UserEntity;
import com.shootr.android.service.dataservice.dto.UserDtoFactory;
import com.shootr.android.task.events.follows.FollowUnFollowResultEvent;
import com.shootr.android.task.events.profile.UserInfoResultEvent;
import com.shootr.android.task.jobs.follows.GetFollowUnFollowUserOfflineJob;
import com.shootr.android.task.jobs.follows.GetFollowUnfollowUserJob;
import com.shootr.android.task.jobs.profile.GetUserInfoJob;
import com.shootr.android.ui.activities.UserFollowsContainerActivity;
import com.shootr.android.ui.base.BaseActivity;
import com.shootr.android.ui.base.BaseFragment;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.widgets.FollowButton;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;

public class ProfileFragment extends BaseFragment {

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

    @Inject Bus bus;
    @Inject Picasso picasso;
    @Inject JobManager jobManager;
    @Inject TimeUtils timeUtils;


    // Args
    Long idUser;

    UserEntity currentUser;
    static UserModel user;
    private View.OnClickListener avatarClickListener;

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
        retrieveUserInfo();
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
    }

    private void retrieveUserInfo() {
        Context context = getActivity();
        currentUser = ShootrApplication.get(context).getCurrentUser();
        startGetUserInfoJob(currentUser, context);
        loadLatestShots();
        //TODO loading
    }

    public void startGetUserInfoJob(UserEntity currentUser, Context context){
        GetUserInfoJob job = ShootrApplication.get(context).getObjectGraph().get(GetUserInfoJob.class);
        job.init(idUser,currentUser);
        jobManager.addJobInBackground(job);
    }

    public void startFollowUnfollowUserJob(UserEntity currentUser, Context context, int followType){
        GetFollowUnFollowUserOfflineJob job2 = ShootrApplication.get(context).getObjectGraph().get(
          GetFollowUnFollowUserOfflineJob.class);
        job2.init(currentUser,idUser,followType);
        jobManager.addJobInBackground(job2);

        GetFollowUnfollowUserJob job = ShootrApplication.get(context).getObjectGraph().get(GetFollowUnfollowUserJob.class);
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

    public static UserModel getUser(){
        return user;
    }

    private void setTitle(String title) {
        ((BaseActivity) getActivity()).getSupportActionBar().setTitle(title);
    }

    private void setBasicUserInfo(UserModel user) {
        this.user = user;
        setTitle(user.getUserName());
        nameTextView.setText(user.getName());
        websiteTextView.setText(user.getWebsite());
        followingTextView.setText(String.valueOf(user.getNumFollowings()));
        followersTextView.setText(String.valueOf(user.getNumFollowers()));
        String photo = user.getPhoto();
        if(photo !=null && !photo.isEmpty()){
            picasso.load(photo).into(avatarImageView);
        }
    }

    private void setUserInfo(UserModel user) {
        setBasicUserInfo(user);
        String favTeamName = user.getFavoriteTeamName();
        bioTextView.setText(favTeamName == null ? user.getBio() : getString(R.string.profile_bio_format,favTeamName,user.getBio()));
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
            //TODO
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

        new AlertDialog.Builder(getActivity()).setMessage("Unfollow "+user.getUserName()+"?")
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
        if(idUser==null) return;
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
        if (shots != null && shots.size() > 0) {
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

        String photo = item.getPhoto();
        boolean isValidPhotoUrl = photo != null && !photo.isEmpty();
        if (isValidPhotoUrl) {
            picasso.load(photo).into(vh.avatar);
        } else picasso.load(R.drawable.ic_contact_picture_default).into(vh.avatar);
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
}
