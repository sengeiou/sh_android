package gm.mobi.android.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.OnClick;
import com.hannesdorfmann.fragmentargs.FragmentArgs;
import com.hannesdorfmann.fragmentargs.annotation.Arg;
import com.path.android.jobqueue.JobManager;
import com.squareup.otto.Bus;

import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import gm.mobi.android.GolesApplication;
import gm.mobi.android.db.objects.Follow;
import gm.mobi.android.task.events.profile.UserInfoResultEvent;
import gm.mobi.android.task.jobs.profile.GetUserInfoJob;
import gm.mobi.android.ui.activities.FollowingUsersActivity;
import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import gm.mobi.android.R;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.ui.base.BaseActivity;
import gm.mobi.android.ui.base.BaseFragment;
import timber.log.Timber;

public class ProfileFragment extends BaseFragment {



    public static final String TAG = "profile";

    @InjectView(R.id.profile_name) TextView nameTextView;
    @InjectView(R.id.profile_rank) TextView rankTextView;
    @InjectView(R.id.profile_bio) TextView bioTextView;
    @InjectView(R.id.profile_website) TextView websiteTextView;
    @InjectView(R.id.profile_avatar) ImageView avatarImageView;

    @InjectView(R.id.profile_marks_points) TextView pointsTextView;
    @InjectView(R.id.profile_marks_followers) TextView followersTextView;
    @InjectView(R.id.profile_marks_following_text) TextView followingTextView;

    @InjectView(R.id.profile_follow_button) View followButton;
    @InjectView(R.id.profile_following_button) View followingButton;

    @Inject Bus bus;
    @Inject Picasso picasso;
    @Inject JobManager jobManager;

    @Arg Long userId;
    User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentArgs.inject(this);
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
        retrieveUserInfo();
    }

    private void retrieveUserInfo() {
        Context context = getActivity();
        User currentUser = GolesApplication.get(context).getCurrentUser();
        jobManager.addJobInBackground(new GetUserInfoJob(context, userId, currentUser));
        //TODO loading
    }

    @Subscribe
    public void userInfoReceived(UserInfoResultEvent event) {
        user = event.getUser();
        setUserInfo(user, event.getRelationship(), event.getFavouriteTeam().getClubName());

    }

    private void setTitle(String title) {
        ((BaseActivity) getActivity()).getSupportActionBar().setTitle(title);
    }

    private void setUserInfo(User user, int relationshipWithUser, String favTeamName) {
        setTitle(user.getUserName());
        nameTextView.setText(user.getName());
        bioTextView.setText(favTeamName+". "+user.getBio());
        websiteTextView.setText(user.getWebsite());
        rankTextView.setText(getString(R.string.profile_rank_format, String.valueOf(user.getRank())));
        picasso.load(user.getPhoto()).into(avatarImageView);
        pointsTextView.setText(String.valueOf(user.getPoints()));
        followingTextView.setText(String.valueOf(user.getNumFollowings()));
        followersTextView.setText(String.valueOf(user.getNumFollowers()));

        setMainButtonStatus(relationshipWithUser);
    }

    private void setMainButtonStatus(int relationshipWithUser) {
        boolean iAmFollowing = relationshipWithUser == Follow.RELATIONSHIP_FOLLOWING
            || relationshipWithUser == Follow.RELATIONSHIP_BOTH;
        followingButton.setVisibility(iAmFollowing ? View.VISIBLE : View.GONE);
        followButton.setVisibility(iAmFollowing ? View.GONE : View.VISIBLE);
    }

    @OnClick(R.id.profile_marks_following_box)
    public void openFollowingList() {
        if(user==null) return;
        startActivity(FollowingUsersActivity.getIntent(getActivity(), userId, user.getName()));
    }
}
