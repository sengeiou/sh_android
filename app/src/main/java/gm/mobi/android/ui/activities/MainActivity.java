package gm.mobi.android.ui.activities;

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
import gm.mobi.android.ui.base.BaseFragment;
import gm.mobi.android.ui.base.BaseSignedInActivity;
import gm.mobi.android.ui.fragments.InitialSetupFragment;
import gm.mobi.android.ui.fragments.TimelineFragment;

public class MainActivity extends BaseSignedInActivity {

    @Inject Bus bus;

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

        //TODO setup navigation drawer
        if (needsSetup()) {
            initialSetup();
        } else {
            normalSetup();
        }

    }


    private boolean needsSetup() {
        return true;
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
        normalSetup();
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
    }
}
