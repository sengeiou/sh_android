package com.shootr.mobile.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import com.shootr.mobile.R;
import com.shootr.mobile.domain.utils.UserFollowingRelationship;
import com.shootr.mobile.ui.base.BaseSignedInActivity;
import com.shootr.mobile.ui.fragments.FollowFragment;
import com.shootr.mobile.ui.fragments.StreamFollowersFragment;
import com.shootr.mobile.ui.fragments.UserFollowsFragment;
import timber.log.Timber;

public class UserFollowsContainerActivity extends BaseSignedInActivity {

    private static final String EXTRA_USER_ID = "userid";
    private static final String EXTRA_FOLLOW_TYPE = "followtype";
    private static final String EXTRA_STREAM_FOLLOWERS = "streamFollowersType";
    private static final String EXTRA_STREAM_ID = "streamId";
    private Integer followType;
    private String userId;
    private String streamId;
    private boolean isStreamFollowers;

    public static Intent getIntent(Context context, String userId, Integer followType) {
        Intent i = new Intent(context, UserFollowsContainerActivity.class);
        i.putExtra(EXTRA_USER_ID, userId);
        i.putExtra(EXTRA_FOLLOW_TYPE, followType);
        i.putExtra(EXTRA_STREAM_FOLLOWERS, false);
        return i;
    }

    public static Intent getIntentStreamsFollowersIntent(Context context, String streamId) {
        Intent i = new Intent(context, UserFollowsContainerActivity.class);
        i.putExtra(EXTRA_STREAM_ID, streamId);
        i.putExtra(EXTRA_STREAM_FOLLOWERS, true);
        return i;
    }


    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!restoreSessionOrLogin()) {
            return;
        }

        setContainerContent(R.layout.user_follows_fragment_container);
        getIntentExtras();
        setScreenTitle();
        setupActionBar();

        boolean shouldManuallyPlaceFragment = savedInstanceState == null;
        if (shouldManuallyPlaceFragment) {
            setUserListFragment();
        }
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    private void getIntentExtras() {
        followType = getIntent().getIntExtra(EXTRA_FOLLOW_TYPE, -1);
        userId = getIntent().getStringExtra(EXTRA_USER_ID);
        streamId = getIntent().getStringExtra(EXTRA_STREAM_ID);
        isStreamFollowers = getIntent().getBooleanExtra(EXTRA_STREAM_FOLLOWERS, false);
        if (!isStreamFollowers && userId == null) {
            Timber.e("Consulted following list of user id %d", userId);
            finish();
        }
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void setUserListFragment() {
        if (!isStreamFollowers) {
            FollowFragment userFollowsFragment = FollowFragment.newInstance(userId, followType);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            //TODO check que no hubiera ya uno? necesario?
            transaction.add(R.id.container, userFollowsFragment, UserFollowsFragment.TAG);
            transaction.commit();
        } else {
            StreamFollowersFragment streamFollowersFragment =
                StreamFollowersFragment.newInstance(streamId);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.container, streamFollowersFragment, UserFollowsFragment.TAG);
            transaction.commit();
        }
    }

    private void setScreenTitle() {
        String title =
            followType.equals(UserFollowingRelationship.FOLLOWERS) || isStreamFollowers ? getString(
                R.string.activity_followers_title) : getString(R.string.activity_following_title);
        getSupportActionBar().setTitle(title);
    }
}
