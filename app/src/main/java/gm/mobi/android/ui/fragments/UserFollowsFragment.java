package gm.mobi.android.ui.fragments;

import android.content.Context;
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
import gm.mobi.android.GolesApplication;
import gm.mobi.android.R;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.service.dataservice.dto.UserDtoFactory;
import gm.mobi.android.task.events.CommunicationErrorEvent;
import gm.mobi.android.task.events.ConnectionNotAvailableEvent;
import gm.mobi.android.task.events.follows.FollowUnFollowResultEvent;
import gm.mobi.android.task.events.follows.FollowsResultEvent;
import gm.mobi.android.task.jobs.follows.GetFollowUnFollowUserOfflineJob;
import gm.mobi.android.task.jobs.follows.GetFollowUnfollowUserJob;
import gm.mobi.android.task.jobs.follows.GetUsersFollowsJob;
import gm.mobi.android.ui.activities.ProfileContainerActivity;
import gm.mobi.android.ui.adapters.UserListAdapter;
import gm.mobi.android.ui.base.BaseFragment;
import gm.mobi.android.ui.model.UserVO;
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

    UserVO user;

    //CurrentUser
    User currentUser;

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
        currentUser = GolesApplication.get(getActivity()).getCurrentUser();
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
        GetUsersFollowsJob job = GolesApplication.get(getActivity()).getObjectGraph().get(GetUsersFollowsJob.class);
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
        List<UserVO> usersFollowing = event.getResult();
        if (usersFollowing.size() == 0) {
            setEmpty(true);
        } else {
            setListContent(usersFollowing);
        }
    }

    protected void setListContent(List<UserVO> usersFollowing) {
        emptyTextView.setVisibility(View.GONE);
        getAdapter().setItems(usersFollowing);
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
        startActivity(ProfileContainerActivity.getIntent(getActivity(), user));
    }

    public void startFollowUnfollowUserJob(UserVO userVO, Context context, int followType){
        //Proceso de encolamiento
        GetFollowUnFollowUserOfflineJob job2 = GolesApplication.get(context).getObjectGraph().get(GetFollowUnFollowUserOfflineJob.class);
        job2.init(currentUser,user,followType);
        jobManager.addJobInBackground(job2);
        //Al instante
        GetFollowUnfollowUserJob job = GolesApplication.get(context).getObjectGraph().get(GetFollowUnfollowUserJob.class);
        job.init(currentUser,userVO, followType);
        jobManager.addJobInBackground(job);
    }

    public void followUser(UserVO user){
        startFollowUnfollowUserJob(user, getActivity(), UserDtoFactory.FOLLOW_TYPE);
    }

    public void unfollowUser(UserVO user){
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
        unfollowUser(user);
    }

    @Subscribe
    public void onFollowUnfollowReceived(FollowUnFollowResultEvent event){
        if(isThereInternetConnection()){
            startJob();
        }

    }

    public boolean isThereInternetConnection(){
        return networkUtil.isConnected(getActivity().getApplication());
    }


}
