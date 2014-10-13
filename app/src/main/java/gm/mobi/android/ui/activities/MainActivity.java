package gm.mobi.android.ui.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;
import com.path.android.jobqueue.JobManager;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import gm.mobi.android.GolesApplication;
import gm.mobi.android.R;
import gm.mobi.android.data.prefs.BooleanPreference;
import gm.mobi.android.data.prefs.InitialSetupCompleted;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.sync.SyncConfigurator;
import gm.mobi.android.task.jobs.loginregister.GCMRegistrationJob;
import gm.mobi.android.ui.adapters.MenuAdapter;
import gm.mobi.android.ui.base.BaseSignedInActivity;
import gm.mobi.android.ui.fragments.DummyFragment;
import gm.mobi.android.ui.fragments.InitialSetupFragment;
import gm.mobi.android.ui.fragments.PeopleFragment;
import gm.mobi.android.ui.fragments.TimelineFragment;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class MainActivity extends BaseSignedInActivity {

    private static final String EXTRA_CURRENT_TITLE = "title";
    private static final String EXTRA_DRAWER_POSITION = "position";

    private static final int DRAWER_POSITION_TIMELINE = 0;
    private static final int DRAWER_POSITION_PEOPLE = 1;
    private static final int DRAWER_POSITION_WATCHING = 2;
    private static final int DRAWER_POSITION_DEFAULT = DRAWER_POSITION_TIMELINE;

    @Inject Bus bus;
    @Inject Picasso picasso;
    @Inject SyncConfigurator syncConfigurator;
    @Inject JobManager jobManager;
    @Inject @InitialSetupCompleted BooleanPreference initialSetupCompleted;

    @InjectView(R.id.drawer_layout) DrawerLayout drawerLayout;
    @InjectView(R.id.menu_drawer_list) ListView drawerList;
    @InjectView(R.id.menu_drawer_profile_avatar) ImageView currentUserAvatar;
    @InjectView(R.id.menu_drawer_profile_name) TextView currentUserName;
    @InjectView(R.id.menu_drawer_profile_email) TextView currentUserUsername;

    private ActionBarDrawerToggle drawerToggle;
    private ActionBar actionBar;

    private String currentTitle;
    private User currentUser;
    private int currentSelectedDrawerPosition = -1;
    private MenuAdapter menuAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!restoreSessionOrLogin()) {
            // Stop execution if there is no user logged in
            return;
        }

        setContainerContent(R.layout.main_activity);
        ButterKnife.inject(this);

        actionBar = getSupportActionBar();
        currentUser = GolesApplication.get(this).getCurrentUser();

        startGCMRegistration();
        setupSyncing();
        setupNavigationDrawer();
        if (needsSetup()) {
            initialSetup();
        } else {
            normalSetup(savedInstanceState);
        }
    }

    private void startGCMRegistration() {
        GCMRegistrationJob job = GolesApplication.get(this).getObjectGraph().get(GCMRegistrationJob.class);
        job.init();
        jobManager.addJobInBackground(job);
    }

    private boolean needsSetup() {
        return !initialSetupCompleted.get();
    }

    private void initialSetup() {
        lockMenuDrawer(true);
        setScreenTitle("Setup...");

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();

        InitialSetupFragment initFragment = new InitialSetupFragment();
        //TODO in unexpected situations there might be already a fragment there. Replace in that case
        fragmentTransaction.add(R.id.main_content, initFragment);
        fragmentTransaction.commit();
    }

    private void normalSetup(Bundle savedInstanceState) {
        lockMenuDrawer(false);

        // Coloca el Fragment que toque, por defecto el Timeline
        /* Si se está re-creando la Activity por un cambio de configuración, comprueba el savedInstanceState.
         * Si es null se está creando por primera vez, y tengo que añadir manualmente los fragments.
         * Si no es null, el sistema se encargará de añadirlos automáticamente por mi.
         * Vía https://plus.google.com/+AndroidDevelopers/posts/3exHM3ZuCYM (Protip de Bruno Olivieira
         */
        if (savedInstanceState == null) {
            selectDrawerItem(DRAWER_POSITION_DEFAULT);
        } else {
            // Por lo menos restaura el título que tenía puesto, ya que los fragments los pone el sistema solo.
            setScreenTitle(savedInstanceState.getString(EXTRA_CURRENT_TITLE));
            // Y el item seleccionado del Drawer
            setActiveDrawerPosition(savedInstanceState.getInt(EXTRA_DRAWER_POSITION, -1));
        }
    }

    private void setupSyncing() {
        //TODO no sync for now
        //syncConfigurator.setupDefaultSyncing();
    }

    @OnItemClick(R.id.menu_drawer_list)
    public void selectDrawerItem(int position) {
        if (currentSelectedDrawerPosition == position) {
            showDrawer(false);
            return;
        }
        MenuAdapter.MenuItem selectedItem = menuAdapter.getItem(position);
        //TODO generalizar esto, dejando la responsabilidad de qué hacer en el propio item
        if (selectedItem instanceof MenuAdapter.FragmentMenuItem) {
            MenuAdapter.FragmentMenuItem fragmentMenuItem =
                (MenuAdapter.FragmentMenuItem) selectedItem;
            replaceShownFragment(fragmentMenuItem.fragmentClass, fragmentMenuItem.extras);
            setScreenTitle(fragmentMenuItem.title);
            setActiveDrawerPosition(position);
            drawerLayout.closeDrawer(Gravity.LEFT);
        }
    }

    private void setActiveDrawerPosition(int position) {
        currentSelectedDrawerPosition = position;
        //menuAdapter.setSelectedPosition(position);
        drawerList.setItemChecked(position, true);
    }

    private void replaceShownFragment(Class fragmentClass, Bundle fragmentArguments) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        String newFragmentName = fragmentClass.getName();

        // Quita el actual
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.main_content);
        if (currentFragment != null) {
            transaction.remove(currentFragment);
        }

        // Pone el nuevo. Attach si existe, add si no.
        Fragment recycledOrNewFragment = fragmentManager.findFragmentByTag(newFragmentName);
        if (recycledOrNewFragment == null) {
            Timber.d("Instantiating new fragment %s with arguments %s ", newFragmentName, fragmentArguments);
            recycledOrNewFragment = Fragment.instantiate(this, newFragmentName, fragmentArguments);
            transaction.add(R.id.main_content, recycledOrNewFragment, newFragmentName);
        } else {
            transaction.add(R.id.main_content, recycledOrNewFragment, newFragmentName);
            //transaction.attach(recycledOrNewFragment);
        }

        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        transaction.commit();
    }

    private void setupNavigationDrawer() {
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        drawerToggle = new ActionBarDrawerToggle(
            this,                  /* host Activity */
            drawerLayout,         /* DrawerLayout object */
            R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
            R.string.drawer_open,  /* "open drawer" description */
            R.string.drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                setTitle(currentTitle);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                setTitle(R.string.app_name);
            }
        };

        drawerLayout.setDrawerListener(drawerToggle);

        menuAdapter = new MenuAdapter(this, getDrawerMenu());
        drawerList.setAdapter(menuAdapter);
        drawerList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        setUserInfoInMenu();
    }

    private void lockMenuDrawer(boolean lock) {
        drawerLayout.setDrawerLockMode(
            lock ? DrawerLayout.LOCK_MODE_LOCKED_CLOSED : DrawerLayout.LOCK_MODE_UNLOCKED);
        getSupportActionBar().setDisplayHomeAsUpEnabled(!lock);
    }

    private void setUserInfoInMenu() {
        currentUserName.setText(currentUser.getName());
        currentUserUsername.setText("@"+currentUser.getUserName());
        picasso.load(currentUser.getPhoto()).into(currentUserAvatar);
    }

    @OnClick(R.id.menu_drawer_profile)
    public void openProfileFromDrawer() {
        startActivity(ProfileContainerActivity.getIntent(this, currentUser));
    }

    private void setScreenTitle(String title) {
        actionBar.setTitle(title);
        currentTitle = title;
    }

    private void showDrawer(boolean show) {
        if (show) {
            drawerLayout.openDrawer(Gravity.LEFT);
        } else {
            drawerLayout.closeDrawer(Gravity.LEFT);
        }
    }

    private List<MenuAdapter.MenuItem> getDrawerMenu() {
        MenuAdapter.MenuItem[] menuItems = {
            new MenuAdapter.FragmentMenuItem("Timeline", R.drawable.ic_drawer_timeline, TimelineFragment.class),
            new MenuAdapter.FragmentMenuItem("People", R.drawable.ic_drawer_people, PeopleFragment.class),
            new MenuAdapter.FragmentMenuItem("Watching", R.drawable.ic_drawer_timeline, DummyFragment.class),
        };
        return Arrays.asList(menuItems);
    }

    @Subscribe
    public void initialSetupCompleted(InitialSetupFragment.InitialSetupCompletedEvent event) {
        Timber.d("Initial setup completed, proceding to normal setup");
        initialSetupCompleted.set(true);
        normalSetup(null);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        if (drawerToggle != null) {
            drawerToggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (drawerToggle != null) {
            drawerToggle.onConfigurationChanged(newConfig);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EXTRA_CURRENT_TITLE, currentTitle);
        outState.putInt(EXTRA_DRAWER_POSITION, currentSelectedDrawerPosition);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            showDrawer(false);
        }else{
            Fragment f = getSupportFragmentManager().findFragmentById(R.id.main_content);
            if (f instanceof TimelineFragment || f instanceof InitialSetupFragment) {
                super.onBackPressed();
            } else {
                selectDrawerItem(0);
            }
        }
    }

}
