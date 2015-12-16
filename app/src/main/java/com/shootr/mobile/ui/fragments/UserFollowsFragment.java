package com.shootr.mobile.ui.fragments;

import android.app.Activity;
import android.app.AlertDialog;
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
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import com.shootr.mobile.R;
import com.shootr.mobile.data.entity.FollowEntity;
import com.shootr.mobile.ui.activities.ProfileContainerActivity;
import com.shootr.mobile.ui.adapters.UserListAdapter;
import com.shootr.mobile.ui.base.BaseFragment;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.presenter.UserFollowsPresenter;
import com.shootr.mobile.ui.views.UserFollowsView;
import com.shootr.mobile.util.FeedbackMessage;
import com.shootr.mobile.util.ImageLoader;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class UserFollowsFragment extends BaseFragment implements UserListAdapter.FollowUnfollowAdapterCallback,
  UserFollowsView {

    public static final String TAG = "follows";

    private static final String ARGUMENT_FOLLOW_TYPE = "followtype";
    private static final String ARGUMENT_USER_ID = "userId";

    @Inject ImageLoader imageLoader;
    @Inject FeedbackMessage feedbackMessage;

    @Bind(R.id.userlist_list) ListView userlistListView;
    @Bind(R.id.userlist_progress) ProgressBar progressBar;
    @Bind(com.shootr.mobile.R.id.userlist_empty) TextView emptyTextView;

    @Inject UserFollowsPresenter userFollowsPresenter;

    // Args
    String userId;
    Integer followType;

    UserModel user;

    private UserListAdapter userListAdapter;

    public static UserFollowsFragment newInstance(String userId, Integer followType) {
        UserFollowsFragment fragment = new UserFollowsFragment();
        fragment.setArguments(createArguments(userId, followType));
        return fragment;
    }

    public static Bundle createArguments(String userId, Integer followType) {
        Bundle bundle = new Bundle();
        bundle.putString(ARGUMENT_USER_ID, userId);
        bundle.putInt(ARGUMENT_FOLLOW_TYPE, followType);
        return bundle;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectArguments();
        setHasOptionsMenu(true);
    }

    public void injectArguments() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            userId = arguments.getString(ARGUMENT_USER_ID);
            followType = arguments.getInt(ARGUMENT_FOLLOW_TYPE);
        } else {
            Timber.w("UserFollowsFragment has null arguments, which might cause a NullPointerException");
        }
    }

    @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        return inflater.inflate(com.shootr.mobile.R.layout.activity_userlist, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }


    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        userlistListView.setAdapter(getAdapter());
    }

    protected void setListContent(List<UserModel> usersFollowing) {
        emptyTextView.setVisibility(View.GONE);
        getAdapter().setItems(usersFollowing);
        getAdapter().notifyDataSetChanged();
    }

    @OnItemClick(R.id.userlist_list)
    public void openUserProfile(int position) {
        user = getAdapter().getItem(position);
        startActivityForResult(ProfileContainerActivity.getIntent(getActivity(), user.getIdUser()), 666);
    }

    private void followUser(UserModel user){
        userFollowsPresenter.follow(user);
    }

    public void unfollowUser(final UserModel user){
        userFollowsPresenter.unfollow(user);
    }

    @Override public void onResume() {
        super.onResume();
        userFollowsPresenter.initialize(this, userId, followType);
    }

    @Override public void onPause() {
        super.onPause();
    }

    public void setLoadingView(Boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.INVISIBLE);
        userlistListView.setVisibility(loading ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    public void setEmpty(Boolean empty) {
        emptyTextView.setVisibility(empty ? View.VISIBLE : View.GONE);
        userlistListView.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public UserListAdapter getAdapter() {
        if (userListAdapter == null) {
            userListAdapter = new UserListAdapter(getActivity(), imageLoader);
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
        new AlertDialog.Builder(getActivity()).setMessage("Unfollow "+user.getUsername()+"?")
          .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                unfollowUser(user);
              }
          })
          .setNegativeButton("No", null)
          .create()
          .show();
    }

    @Override
    public void updateFollow(String idUser, Boolean following) {
        List<UserModel> usersInList = userListAdapter.getItems();
        for (int i = 0; i < usersInList.size(); i++) {
            UserModel userModel = usersInList.get(i);
            if (userModel.getIdUser().equals(idUser)) {
                userModel.setRelationship(following? FollowEntity.RELATIONSHIP_FOLLOWING : FollowEntity.RELATIONSHIP_NONE);
                userListAdapter.notifyDataSetChanged();
                break;
            }
        }
    }

    @Override public void showUserBlockedError() {
        feedbackMessage.showLong(getView(), R.string.error_following_user_blocked);
    }

    @Override public void showNoFollowers() {
        emptyTextView.setText(R.string.follower_list_empty);
    }

    @Override public void showNoFollowing() {
        emptyTextView.setText(R.string.following_list_empty);
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode == Activity.RESULT_OK && data!=null){
            Bundle bundleUser = data.getExtras();
            int i=0;int index =0;
            UserModel userModel = new UserModel();
            userModel.setPhoto(bundleUser.getString("PHOTO"));
            userModel.setUsername(bundleUser.getString("USER_NAME"));
            userModel.setName(bundleUser.getString("NAME"));
            userModel.setRelationship(bundleUser.getInt("RELATIONSHIP"));
            userModel.setIdUser(bundleUser.getString("ID_USER"));
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

    @Override public void showError(String messageForError) {
        feedbackMessage.show(getView(), messageForError);
    }

    @Override public void showUsers(List<UserModel> userModels) {
        setListContent(userModels);
    }
}
