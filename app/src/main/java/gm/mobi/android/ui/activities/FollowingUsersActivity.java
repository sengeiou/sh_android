package gm.mobi.android.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import butterknife.ButterKnife;
import gm.mobi.android.R;
import gm.mobi.android.ui.base.BaseSignedInActivity;
import gm.mobi.android.ui.fragments.FollowingUsersFragment;
import gm.mobi.android.ui.fragments.FollowingUsersFragmentBuilder;
import gm.mobi.android.ui.fragments.ProfileFragment;
import gm.mobi.android.ui.fragments.ProfileFragmentBuilder;
import timber.log.Timber;

public class FollowingUsersActivity extends BaseSignedInActivity {

    private static final String EXTRA_USER_ID = "userid";
    private static final String EXTRA_SCREEN_TITLE = "title";



    public static Intent getIntent(Context context, Long userId, String userName) {
        Intent i = new Intent(context, FollowingUsersActivity.class);
        i.putExtra(EXTRA_USER_ID, userId);
        i.putExtra(EXTRA_SCREEN_TITLE, context.getResources().getString(
            R.string.activity_following_title, userName));
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!restoreSessionOrLogin()) return;

        setContainerContent(R.layout.activity_fragment_container);

        if (savedInstanceState == null) {
            String title = getIntent().getStringExtra(EXTRA_SCREEN_TITLE);
            Long userId = getIntent().getLongExtra(EXTRA_USER_ID, 0L);
            if (userId == 0L) {
                Timber.e("Consulted following list of user id %d", userId);
                finish();
            }
            FollowingUsersFragment followingUsersFragment =
                FollowingUsersFragmentBuilder.newFollowingUsersFragment(title, userId);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            //TODO check que no hubiera ya uno? necesario?
            transaction.add(R.id.container, followingUsersFragment, FollowingUsersFragment.TAG);
            transaction.commit();
        }

    }
}
