package gm.mobi.android.ui.activities;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.ButterKnife;
import gm.mobi.android.R;
import gm.mobi.android.constant.SyncConstants;
import gm.mobi.android.data.prefs.BooleanPreference;
import gm.mobi.android.data.prefs.InitialSetupCompleted;
import gm.mobi.android.sync.ShootrAccountGenerator;
import gm.mobi.android.ui.base.BaseFragment;
import gm.mobi.android.ui.base.BaseSignedInActivity;
import gm.mobi.android.ui.fragments.InitialSetupFragment;
import gm.mobi.android.ui.fragments.TimelineFragment;

import static gm.mobi.android.constant.SyncConstants.AUTHORITY;

public class MainActivity extends BaseSignedInActivity {


    ContentResolver mContentResolver;
    private Account mAccount;
    @Inject Bus bus;
    @Inject @InitialSetupCompleted BooleanPreference initialSetupCompleted;

    //TODO recibir parámetros para indicar si viene de registro, login o nueva
    public static Intent getIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!restoreSessionOrLogin()) {
            // Stop execution if there is no user logged in
            return;
        }

        setContainerContent(R.layout.main_activity);
        ButterKnife.inject(this);

        mContentResolver = getContentResolver();
        mAccount = ShootrAccountGenerator.createSyncAccount(getApplicationContext());

        //TODO setup navigation drawer
        if (needsSetup()) {
            initialSetup();
        } else {
            normalSetup();
        }


    }


    private boolean needsSetup() {
        return !initialSetupCompleted.get();
    }

    private void initialSetup() {
        //TODO lock navigation drawer
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();

        InitialSetupFragment initFragment = new InitialSetupFragment();
        fragmentTransaction.add(R.id.main_content, initFragment);
        fragmentTransaction.commit();
    }


    private void normalSetup() {
        //TODO unlock navigation drawer
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();

        //TODO advanced management
        BaseFragment currentFragment = (BaseFragment) fm.findFragmentById(R.id.main_content);
        if (currentFragment != null) {
            fragmentTransaction.remove(currentFragment);
        }
        TimelineFragment timelineFragment = new TimelineFragment();
        fragmentTransaction.add(R.id.main_content, timelineFragment, "timeline");

        fragmentTransaction.commit();
    }

    @Subscribe
    public void initialSetupCompleted(InitialSetupFragment.InitialSetupCompletedEvent event) {
        initialSetupCompleted.set(true);
        normalSetup();

    }

    private void setPeriodicSync(){
        //Retrieve Followings
        //Retrieve Followins's users
        //Retrieve Shots


        // Is it possible to sync?
        ContentResolver.setIsSyncable(mAccount, AUTHORITY, 0);
        // Should sync be done automatically by Android when the provider
        // sends a notifyChange() with syncToNetwork set to true?
        ContentResolver.setSyncAutomatically(mAccount, AUTHORITY, true);
        // Set some sync parameters
        Bundle params = new Bundle();
        params.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, false);
        params.putBoolean(ContentResolver.SYNC_EXTRAS_DO_NOT_RETRY, false);
        params.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, false);

        params.putInt(SyncConstants.CALL_TYPE,SyncConstants.GET_SHOTS_CALL_TYPE);
        // How often should automatic sync be done? (say 15 minutes)
        ContentResolver.addPeriodicSync(mAccount, AUTHORITY, params,SyncConstants.SYNC_INTERVAL_FOR_SHOTS);
        // Request a sync right now
        ContentResolver.requestSync(mAccount, AUTHORITY, params);

        Bundle bundleShots = new Bundle();
        bundleShots.putInt(SyncConstants.CALL_TYPE,SyncConstants.GET_SHOTS_CALL_TYPE);
//        ContentResolver.requestSync(mAccount,SyncConstants.AUTHORITY,bundleShots);
        ContentResolver.addPeriodicSync(mAccount, AUTHORITY,bundleShots,SyncConstants.SYNC_INTERVAL_FOR_SHOTS);
    }



    @Override
    protected void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bus.register(this);
        ContentResolver.setSyncAutomatically(mAccount, AUTHORITY, true);
        setPeriodicSync();
    }
}
