package gm.mobi.android.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import com.path.android.jobqueue.JobManager;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import gm.mobi.android.R;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.task.events.follows.FollowsResultEvent;
import gm.mobi.android.task.jobs.follows.GetUsersFollowingJob;
import gm.mobi.android.ui.adapters.UserListAdapter;
import gm.mobi.android.ui.base.BaseSignedInActivity;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class FollowingUsersActivity extends BaseSignedInActivity {

    private static final String EXTRA_USER_ID = "userid";
    private static final String EXTRA_USER_NAME = "userName";

    @Inject Picasso picasso;
    @Inject Bus bus;
    @Inject JobManager jobManager;
    @InjectView(R.id.userlist_list) ListView userlistListView;

    private UserListAdapter userListAdapter;

    public static Intent getIntent(Context context, Long userId, String userName) {
        Intent i = new Intent(context, FollowingUsersActivity.class);
        i.putExtra(EXTRA_USER_ID, userId);
        i.putExtra(EXTRA_USER_NAME, userName);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!restoreSessionOrLogin()) return;

        setContainerContent(R.layout.activity_userlist);
        ButterKnife.inject(this);

        String userName = getIntent().getStringExtra(EXTRA_USER_NAME);
        long userId = getIntent().getLongExtra(EXTRA_USER_ID, 0L);
        if (userId == 0L) {
            Timber.e("Consulted following list of user id %d", userId);
            finish();
        }
        setScreenTitle(userName);
        retrieveUsers(userId);
    }

    private void retrieveUsers(Long userId) {
        jobManager.addJobInBackground(new GetUsersFollowingJob(this, userId));
    }

    @Subscribe
    public void showUserList(FollowsResultEvent event) {
        List<User> users = event.getFollows();
        if (userListAdapter == null) {
            userListAdapter = new UserListAdapter(this, picasso, users);
            userlistListView.setAdapter(userListAdapter);
        } else {
            userListAdapter.setItems(users);
        }
    }

    @OnItemClick(R.id.userlist_list)
    public void openUserProfile(int position) {
        User user = userListAdapter.getItem(position);
        startActivity(ProfileContainerActivity.getIntent(this, user.getIdUser()));
    }

    private void setScreenTitle(String userName) {
        getSupportActionBar().setTitle(getString(R.string.activity_following_title, userName));
    }

    @Override protected void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override protected void onPause() {
        super.onPause();
        bus.unregister(this);
    }
}
