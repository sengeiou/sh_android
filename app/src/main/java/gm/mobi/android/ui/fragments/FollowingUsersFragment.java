package gm.mobi.android.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import com.hannesdorfmann.fragmentargs.FragmentArgs;
import com.hannesdorfmann.fragmentargs.annotation.Arg;
import com.path.android.jobqueue.JobManager;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import gm.mobi.android.R;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.task.events.follows.FollowsResultEvent;
import gm.mobi.android.task.jobs.follows.GetUsersFollowingJob;
import gm.mobi.android.ui.activities.ProfileContainerActivity;
import gm.mobi.android.ui.adapters.UserListAdapter;
import gm.mobi.android.ui.base.BaseActivity;
import gm.mobi.android.ui.base.BaseFragment;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class FollowingUsersFragment extends BaseFragment {

    public static final String TAG = "following";

    @Inject Picasso picasso;
    @Inject Bus bus;
    @Inject JobManager jobManager;

    @InjectView(R.id.userlist_list) ListView userlistListView;
    @InjectView(R.id.userlist_progress) ProgressBar progressBar;

    @Arg String screenTitle;
    @Arg Long userId;

    private UserListAdapter userListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentArgs.inject(this);
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
        retrieveUsers(userId);
        setScreenTitle();
    }

    private void retrieveUsers(Long userId) {
        jobManager.addJobInBackground(new GetUsersFollowingJob(getActivity(), userId));
        setLoadingView(true);
    }

    @Subscribe
    public void showUserList(FollowsResultEvent event) {
        List<User> users = event.getFollows();
        if (userListAdapter == null) {
            userListAdapter = new UserListAdapter(getActivity(), picasso, users);
            userlistListView.setAdapter(userListAdapter);
        } else {
            userListAdapter.setItems(users);
        }
        setLoadingView(false);
    }

    @OnItemClick(R.id.userlist_list)
    public void openUserProfile(int position) {
        User user = userListAdapter.getItem(position);
        startActivity(ProfileContainerActivity.getIntent(getActivity(), user.getIdUser()));
    }

    private void setScreenTitle() {
        ((BaseActivity) getActivity()).getSupportActionBar().setTitle(screenTitle);
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
}
