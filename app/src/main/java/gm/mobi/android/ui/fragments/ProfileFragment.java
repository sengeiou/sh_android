package gm.mobi.android.ui.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.path.android.jobqueue.JobManager;
import com.path.android.jobqueue.network.NetworkUtil;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import gm.mobi.android.ShootrApplication;
import gm.mobi.android.R;
import gm.mobi.android.db.objects.FollowEntity;
import gm.mobi.android.db.objects.UserEntity;
import gm.mobi.android.service.dataservice.dto.UserDtoFactory;
import gm.mobi.android.task.events.follows.FollowUnFollowResultEvent;
import gm.mobi.android.task.events.profile.UserInfoResultEvent;
import gm.mobi.android.task.jobs.follows.GetFollowUnFollowUserOfflineJob;
import gm.mobi.android.task.jobs.follows.GetFollowUnfollowUserJob;
import gm.mobi.android.task.jobs.profile.GetUserInfoJob;
import gm.mobi.android.ui.activities.UserFollowsContainerActivity;
import gm.mobi.android.ui.base.BaseActivity;
import gm.mobi.android.ui.base.BaseFragment;
import gm.mobi.android.ui.model.UserModel;
import gm.mobi.android.ui.widgets.FollowButton;
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

    @Inject Bus bus;
    @Inject Picasso picasso;
    @Inject JobManager jobManager;
    @Inject NetworkUtil networkUtil;

    // Args
    Long idUser;

    UserEntity currentUser;

    static UserModel user;

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
        startGetUserInfoJob(currentUser,context);
        //TODO loading
    }

    public void startGetUserInfoJob(UserEntity currentUser, Context context){
        GetUserInfoJob job = ShootrApplication.get(context).getObjectGraph().get(GetUserInfoJob.class);
        job.init(idUser,currentUser);
        jobManager.addJobInBackground(job);
    }

    public void startFollowUnfollowUserJob(UserEntity currentUser, Context context, int followType){
        GetFollowUnFollowUserOfflineJob job2 = ShootrApplication.get(context).getObjectGraph().get(GetFollowUnFollowUserOfflineJob.class);
        job2.init(currentUser,idUser,followType);
        jobManager.addJobInBackground(job2);

        GetFollowUnfollowUserJob job = ShootrApplication.get(context).getObjectGraph().get(GetFollowUnfollowUserJob.class);
        jobManager.addJobInBackground(job);

    }

    @Subscribe
    public void userInfoReceived(UserInfoResultEvent event) {
        if(event.getResult()!=null)
        setUserInfo(event.getResult());
    }

    @Subscribe
    public void onFollowUnfollowReceived(FollowUnFollowResultEvent event){
        if(event.getResult()!=null)
        setUserInfo(event.getResult());

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
        startFollowUnfollowUserJob(currentUser, getActivity(),UserDtoFactory.FOLLOW_TYPE);
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
        startActivityForResult(UserFollowsContainerActivity.getIntent(getActivity(), idUser, followType),677);
    }
}
