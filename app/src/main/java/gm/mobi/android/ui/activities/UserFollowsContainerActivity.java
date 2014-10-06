package gm.mobi.android.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import gm.mobi.android.R;
import gm.mobi.android.ui.base.BaseSignedInActivity;
import gm.mobi.android.ui.fragments.UserFollowsFragment;
import gm.mobi.android.ui.fragments.UserFollowsFragmentBuilder;
import timber.log.Timber;

public class UserFollowsContainerActivity extends BaseSignedInActivity {

    private static final String EXTRA_USER_ID = "userid";
    private static final String EXTRA_FOLLOW_TYPE = "followtype";
    private Integer followType;
    private Long userId;

    public static Intent getIntent(Context context, Long userId, Integer followType) {
        Intent i = new Intent(context, UserFollowsContainerActivity.class);
        i.putExtra(EXTRA_USER_ID, userId);
        i.putExtra(EXTRA_FOLLOW_TYPE, followType);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!restoreSessionOrLogin()) return;

        setContainerContent(R.layout.activity_fragment_container);

        getIntentExtras();
        setScreenTitle();

        boolean shouldManuallyPlaceFragment = savedInstanceState == null;
        if (shouldManuallyPlaceFragment) {
            setUserListFragment();
        }
    }

    private void getIntentExtras() {
        followType = getIntent().getIntExtra(EXTRA_FOLLOW_TYPE, -1);
        userId = getIntent().getLongExtra(EXTRA_USER_ID, 0L);
        if (userId == 0L) {
            Timber.e("Consulted following list of user id %d", userId);
            finish();
        }
    }

    private void setUserListFragment() {
        UserFollowsFragment userFollowsFragment = UserFollowsFragmentBuilder.newUserFollowsFragment(followType, userId);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //TODO check que no hubiera ya uno? necesario?
        transaction.add(R.id.container, userFollowsFragment, UserFollowsFragment.TAG);
        transaction.commit();
    }

    private void setScreenTitle() {
        String title = null;
        switch (followType) {
            case UserFollowsFragment.FOLLOWERS:
                title = getString(R.string.activity_followers_title);
                break;
            case UserFollowsFragment.FOLLOWING:
                title = getString(R.string.activity_following_title);
                break;
        }
        getSupportActionBar().setTitle(title);
    }
}
