package com.shootr.android.ui.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import com.path.android.jobqueue.JobManager;
import com.path.android.jobqueue.network.NetworkUtil;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.shootr.android.ShootrApplication;
import com.shootr.android.R;
import com.shootr.android.db.objects.UserEntity;
import com.shootr.android.service.dataservice.dto.UserDtoFactory;
import com.shootr.android.task.events.CommunicationErrorEvent;
import com.shootr.android.task.events.ConnectionNotAvailableEvent;
import com.shootr.android.task.events.follows.FollowUnFollowResultEvent;
import com.shootr.android.task.events.follows.FollowsResultEvent;
import com.shootr.android.task.jobs.follows.GetFollowUnFollowUserOfflineJob;
import com.shootr.android.task.jobs.follows.GetFollowUnfollowUserJob;
import com.shootr.android.task.jobs.follows.GetUsersFollowsJob;
import com.shootr.android.ui.activities.ProfileContainerActivity;
import com.shootr.android.ui.adapters.UserListAdapter;
import com.shootr.android.ui.base.BaseFragment;
import com.shootr.android.ui.model.UserModel;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class UserFollowsFragment extends BaseFragment implements UserListAdapter.FollowUnfollowAdapterCallback{

    public static final String TAG = "follows";
    public static final int FOLLOWING = UserDtoFactory.GET_FOLLOWING;
    public static final int FOLLOWERS = UserDtoFactory.GET_FOLLOWERS;

    private static final String ARGUMENT_FOLLOW_TYPE = "followtype";
    private static final String ARGUMENT_USER_ID = "userId";

    @Inject Picasso picasso;
    @Inject Bus bus;
    @Inject JobManager jobManager;
    @Inject NetworkUtil networkUtil;

    @InjectView(R.id.userlist_list) ListView userlistListView;
    @InjectView(R.id.userlist_progress) ProgressBar progressBar;
    @InjectView(R.id.userlist_empty) TextView emptyTextView;

    // Args
    Long userId;
    Integer followType;

    UserModel user;

    //CurrentUser
    UserEntity currentUser;

    private UserListAdapter userListAdapter;

    public static UserFollowsFragment newInstance(Long userId, Integer followType) {
        UserFollowsFragment fragment = new UserFollowsFragment();
        fragment.setArguments(createArguments(userId, followType));
        return fragment;
    }

    public static Bundle createArguments(Long userId, Integer followType) {
        Bundle bundle = new Bundle();
        bundle.putLong(ARGUMENT_USER_ID, userId);
        bundle.putInt(ARGUMENT_FOLLOW_TYPE, followType);
        return bundle;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectArguments();
        currentUser = ShootrApplication.get(getActivity()).getCurrentUser();
    }

    public void injectArguments() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            userId = arguments.getLong(ARGUMENT_USER_ID);
            followType = arguments.getInt(ARGUMENT_FOLLOW_TYPE);
        } else {
            Timber.w("UserFollowsFragment has null arguments, which might cause a NullPointerException");
        }
    }

    @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_userlist, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        userlistListView.setAdapter(getAdapter());
    }


    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setEmptyMessage();
        retrieveUsers();
    }

    private void retrieveUsers() {
        startJob();
        setLoadingView(true);
        setEmpty(false);
    }

    public void startJob(){
        GetUsersFollowsJob job = ShootrApplication.get(getActivity()).getObjectGraph().get(GetUsersFollowsJob.class);
        job.init(userId, followType);
        jobManager.addJobInBackground(job);
    }

    private void setEmptyMessage() {
        emptyTextView.setText(followType.equals(UserDtoFactory.GET_FOLLOWERS) ? R.string.follower_list_empty
          : R.string.following_list_empty);
    }

    @Subscribe
    public void showUserList(FollowsResultEvent event) {
        setLoadingView(false);
        List<UserModel> usersFollowing = event.getResult();
        if (usersFollowing.size() == 0) {
            setEmpty(true);
        } else {
            setListContent(usersFollowing);
        }
    }

    protected void setListContent(List<UserModel> usersFollowing) {
        emptyTextView.setVisibility(View.GONE);
        getAdapter().setItems(usersFollowing);

        getAdapter().notifyDataSetChanged();
    }

    @Subscribe
    public void onCommunicationError(CommunicationErrorEvent event) {
        Toast.makeText(getActivity(), R.string.communication_error, Toast.LENGTH_SHORT).show();
    }

    @Subscribe
    public void onConnectionNotAvailable(ConnectionNotAvailableEvent event) {
        Toast.makeText(getActivity(), R.string.connection_lost, Toast.LENGTH_SHORT).show();
        setLoadingView(false);
    }

    @OnItemClick(R.id.userlist_list)
    public void openUserProfile(int position) {
        user = getAdapter().getItem(position);
        startActivityForResult(ProfileContainerActivity.getIntent(getActivity(), user.getIdUser()), 666);
    }

    public void startFollowUnfollowUserJob(UserModel userVO, Context context, int followType){
        //Proceso de insercción en base de datos
        GetFollowUnFollowUserOfflineJob job2 = ShootrApplication.get(context).getObjectGraph().get(GetFollowUnFollowUserOfflineJob.class);
        job2.init(currentUser,userVO.getIdUser(),followType);
        jobManager.addJobInBackground(job2);

        //Al instante
        GetFollowUnfollowUserJob job = ShootrApplication.get(context).getObjectGraph().get(GetFollowUnfollowUserJob.class);
        jobManager.addJobInBackground(job);
    }

    public void followUser(UserModel user){
        startFollowUnfollowUserJob(user, getActivity(), UserDtoFactory.FOLLOW_TYPE);
    }

    public void unfollowUser(UserModel user){
        startFollowUnfollowUserJob(user,getActivity(),UserDtoFactory.UNFOLLOW_TYPE);
    }

    @Override public void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override public void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    private void setLoadingView(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.INVISIBLE);
        userlistListView.setVisibility(loading ? View.INVISIBLE : View.VISIBLE);
    }

    private void setEmpty(boolean empty) {
        emptyTextView.setVisibility(empty ? View.VISIBLE : View.GONE);
        userlistListView.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    public UserListAdapter getAdapter() {
        if (userListAdapter == null) {
            userListAdapter = new UserListAdapter(getActivity(), picasso);
            userListAdapter.setCallback(this);
        }
        return userListAdapter;
    }

    @Override public void follow(int position) {
        user = getAdapter().getItem(position);
        followUser(user);
    }


    @Override public void unFollow(int position) {
        user = getAdapter().getItem(position);
        new AlertDialog.Builder(getActivity()).setMessage("Unfollow "+user.getUserName()+"?")
          .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                unfollowUser(user);
              }
          })
          .setNegativeButton("No", new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {

              }
          })
          .create()
          .show();
    }

    @Subscribe
    public void onFollowUnfollowReceived(FollowUnFollowResultEvent event){
            UserModel userVO = event.getResult();
        if(userVO!=null){
            List<UserModel> userVOs = getAdapter().getItems();
            int i = 0,index = 0;
            for(UserModel userModel:userVOs){
                if(userModel.getIdUser().equals(userVO.getIdUser())){
                    index = i;
                }
                i++;
            }
            userVOs.remove(index);
            getAdapter().removeItems();
            userVOs.add(index,userVO);
            getAdapter().setItems(userVOs);

            getAdapter().notifyDataSetChanged();
        }
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode == Activity.RESULT_OK && data!=null){
            Bundle bundleUser = data.getExtras();
            int i=0;int index =0;
            UserModel userModel = new UserModel();
            userModel.setPhoto(bundleUser.getString("PHOTO"));
            userModel.setUserName(bundleUser.getString("USER_NAME"));
            userModel.setName(bundleUser.getString("NAME"));
            userModel.setFavoriteTeamName(bundleUser.getString("FAVORITE_TEAM"));
            userModel.setRelationship(bundleUser.getInt("RELATIONSHIP"));
            userModel.setIdUser(bundleUser.getLong("ID_USER"));
            List<UserModel> userModels = getAdapter().getItems();
            for(UserModel userM: userModels){
                if(userM.getIdUser().equals(userModel.getIdUser())){
                    index = i;
                }
                i++;
            }
            userModels.remove(index);
            getAdapter().removeItems();
            userModels.add(index,userModel);
            getAdapter().setItems(userModels);
            getAdapter().notifyDataSetChanged();
        }
    }


}