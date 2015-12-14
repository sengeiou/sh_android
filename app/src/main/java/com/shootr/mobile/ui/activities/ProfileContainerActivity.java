package com.shootr.mobile.ui.activities;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.ToolbarDecorator;
import com.shootr.mobile.ui.fragments.ProfileFragment;
import java.util.List;
import timber.log.Timber;

public class ProfileContainerActivity extends BaseToolbarDecoratedActivity {

    public static final String EXTRA_USER = "user";
    public static final String EXTRA_USERNAME = "username";
    String idUser;
    String username;

    public static Intent getIntent(Context context, String idUser) {
        Intent i = new Intent(context, ProfileContainerActivity.class);
        i.putExtra(EXTRA_USER, idUser);
        return i;
    }

    public static Intent getIntentWithUsername(Context context, String username) {
        Intent i = new Intent(context, ProfileContainerActivity.class);
        i.putExtra(EXTRA_USERNAME, username);
        return i;
    }

    @Override protected int getLayoutResource() {
        return R.layout.activity_fragment_container;
    }

    @Override protected void initializeViews(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            idUser = (String) getIntent().getSerializableExtra(EXTRA_USER);
            username = (String) getIntent().getSerializableExtra(EXTRA_USERNAME);
            if (idUser == null && username == null) {
                Timber.e("Se intentó abrir la pantalla de perfil con sin pasarle user");
                finish();
                return;
            }
            ProfileFragment profileFragment = null;
            if(idUser != null){
                profileFragment = ProfileFragment.newInstance(idUser);
            }else{
                profileFragment = ProfileFragment.newInstanceFromUsername(username);
            }
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.container, profileFragment, ProfileFragment.TAG);
            transaction.commit();
        }
    }

    @Override protected void initializePresenter() {
        /* no-op */
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            handleBackIntent();
            return true;
        }else{
            return super.onOptionsItemSelected(item);
        }
    }

    public void handleBackIntent() {
        ActivityManager mngr = (ActivityManager) getSystemService( ACTIVITY_SERVICE );
        List<ActivityManager.RunningTaskInfo> taskList = mngr.getRunningTasks(10);
        if(taskList.get(0).numActivities == 1 &&
          taskList.get(0).topActivity.getClassName().equals(this.getClass().getName())) {
            Intent intent = new Intent(this, MainTabbedActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            finish();
        }
    }

    @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
        ActionBar actionBar = toolbarDecorator.getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }
}
