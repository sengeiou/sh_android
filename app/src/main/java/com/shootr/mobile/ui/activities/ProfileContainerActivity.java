package com.shootr.mobile.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.ToolbarDecorator;
import com.shootr.mobile.ui.fragments.ProfileFragment;
import com.shootr.mobile.util.BackStackHandler;
import javax.inject.Inject;
import timber.log.Timber;

public class ProfileContainerActivity extends BaseToolbarDecoratedActivity {

    public static final String EXTRA_USER = "user";
    public static final String EXTRA_USERNAME = "username";

    @Inject BackStackHandler backStackHandler;

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
            backStackHandler.handleBackStack(this);
            return true;
        }else{
            return super.onOptionsItemSelected(item);
        }
    }

    @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
        ActionBar actionBar = toolbarDecorator.getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }
}
