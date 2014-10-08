package gm.mobi.android.ui.fragments;

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
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import gm.mobi.android.GolesApplication;
import gm.mobi.android.R;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.service.dataservice.dto.UserDtoFactory;
import gm.mobi.android.task.events.ConnectionNotAvailableEvent;
import gm.mobi.android.task.events.ResultEvent;
import gm.mobi.android.task.events.follows.FollowsResultEvent;
import gm.mobi.android.task.jobs.follows.GetUsersFollowsJob;
import gm.mobi.android.ui.activities.ProfileContainerActivity;
import gm.mobi.android.ui.adapters.UserListAdapter;
import gm.mobi.android.ui.base.BaseFragment;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class UserFollowsFragment extends BaseFragment {

    public static final String TAG = "follows";
    public static final int FOLLOWING = UserDtoFactory.GET_FOLLOWING;
    public static final int FOLLOWERS = UserDtoFactory.GET_FOLLOWERS;

    private static final String ARGUMENT_FOLLOW_TYPE = "followtype";
    private static final String ARGUMENT_USER_ID = "userId";

    @Inject Picasso picasso;
    @Inject Bus bus;
    @Inject JobManager jobManager;

    @InjectView(R.id.userlist_list) ListView userlistListView;
    @InjectView(R.id.userlist_progress) ProgressBar progressBar;
    @InjectView(R.id.userlist_empty) TextView emptyTextView;

    // Args
    Long userId;
    Integer followType;

    private UserListAdapter userListAdapter;

    public static UserFollowsFragment newInstance(Long userId, int followType) {
        UserFollowsFragment fragment = new UserFollowsFragment();
        fragment.setArguments(createArguments(userId, followType));
        return fragment;
    }

    public static Bundle createArguments(Long userId, int followType) {
        Bundle bundle = new Bundle();
        bundle.putLong("userId", userId);
        bundle.putInt("followType", followType);
        return bundle;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectArguments();
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
        job.init(userId);
        jobManager.addJobInBackground(job);
    }

    private void setEmptyMessage() {
        int emptyStringRes = 0;
        switch (followType) {
            case FOLLOWERS:
                emptyStringRes = R.string.follower_list_empty;
                break;
            case FOLLOWING:
                emptyStringRes = R.string.following_list_empty;
                break;
        }
        emptyTextView.setText(emptyStringRes);
    }

    @Subscribe
    public void showUserList(FollowsResultEvent event) {
        setLoadingView(false);
        if (event.getStatus() == ResultEvent.STATUS_INVALID) {
            onCommunicationError();
            return;
        }
        List<User> users = event.getFollows();
        if (users.size() == 0) {
            setEmpty(true);
        } else {
            if (userListAdapter == null) {
                userListAdapter = new UserListAdapter(getActivity(), picasso, users);
            } else {
                userListAdapter.setItems(users);
            }
            userlistListView.setAdapter(userListAdapter);
        }
    }

    public void onCommunicationError() {
        Toast.makeText(getActivity(), R.string.communication_error, Toast.LENGTH_SHORT).show();
    }

    @Subscribe
    public void onConnectionNotAvailable(ConnectionNotAvailableEvent event) {
        Toast.makeText(getActivity(), R.string.connection_lost, Toast.LENGTH_SHORT).show();
        setLoadingView(false);
    }

    @OnItemClick(R.id.userlist_list)
    public void openUserProfile(int position) {
        User user = userListAdapter.getItem(position);
        startActivity(ProfileContainerActivity.getIntent(getActivity(), user));
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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
