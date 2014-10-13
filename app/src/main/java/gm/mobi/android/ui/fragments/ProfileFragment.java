package gm.mobi.android.ui.fragments;

import android.content.Context;
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
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import gm.mobi.android.GolesApplication;
import gm.mobi.android.R;
import gm.mobi.android.db.objects.Follow;
import gm.mobi.android.db.objects.Team;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.task.events.profile.UserInfoResultEvent;
import gm.mobi.android.task.jobs.profile.GetUserInfoJob;
import gm.mobi.android.ui.activities.UserFollowsContainerActivity;
import gm.mobi.android.ui.base.BaseActivity;
import gm.mobi.android.ui.base.BaseFragment;
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

    @InjectView(R.id.profile_follow_button) View followButton;
    @InjectView(R.id.profile_following_button) View followingButton;

    @Inject Bus bus;
    @Inject Picasso picasso;
    @Inject JobManager jobManager;

    // Args
    User user;

    public static ProfileFragment newInstance(User user) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(ARGUMENT_USER, user);
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
        user = (User) arguments.getSerializable(ARGUMENT_USER);
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
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setBasicUserInfo(user);
        retrieveUserInfo();
    }

    private void retrieveUserInfo() {
        Context context = getActivity();
        User currentUser = GolesApplication.get(context).getCurrentUser();

        GetUserInfoJob job = GolesApplication.get(context).getObjectGraph().get(GetUserInfoJob.class);
        job.init(user.getIdUser(),currentUser);
        jobManager.addJobInBackground(job);
        //TODO loading
    }

    @Subscribe
    public void userInfoReceived(UserInfoResultEvent event) {
        user = event.getResult();
        String team = event.getFavouriteTeam();
        setUserInfo(user, event.getRelationship(), team);

    }

    private void setTitle(String title) {
        ((BaseActivity) getActivity()).getSupportActionBar().setTitle(title);
    }

    private void setBasicUserInfo(User user) {
        setTitle(user.getUserName());
        nameTextView.setText(user.getName());
        websiteTextView.setText(user.getWebsite());
        rankTextView.setText(getString(R.string.profile_rank_format, String.valueOf(user.getRank())));
        pointsTextView.setText(String.valueOf(user.getPoints()));
        followingTextView.setText(String.valueOf(user.getNumFollowings()));
        followersTextView.setText(String.valueOf(user.getNumFollowers()));
        String photo = user.getPhoto();
        if(photo !=null && !photo.isEmpty()){
            picasso.load(photo).into(avatarImageView);
        }
    }

    private void setUserInfo(User user, int relationshipWithUser, String favTeamName) {
        setBasicUserInfo(user);
        bioTextView.setText(favTeamName == null ? user.getBio() : getString(R.string.profile_bio_format,favTeamName,user.getBio()));
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
        openUserFollowsList(UserFollowsFragment.FOLLOWING);
    }

    @OnClick(R.id.profile_marks_followers_box)
    public void openFollowersList() {
        openUserFollowsList(UserFollowsFragment.FOLLOWERS);
    }

    private void openUserFollowsList(int followType) {
        if(user==null) return;
        startActivity(UserFollowsContainerActivity.getIntent(getActivity(), user.getIdUser(), followType));
    }
}
