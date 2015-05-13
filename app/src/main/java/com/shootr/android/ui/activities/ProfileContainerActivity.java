package com.shootr.android.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import com.shootr.android.R;
import com.shootr.android.ui.base.BaseSignedInActivity;
import com.shootr.android.ui.fragments.ProfileFragment;
import timber.log.Timber;

public class ProfileContainerActivity extends BaseSignedInActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!restoreSessionOrLogin()){
            return;
        }
        setContainerContent(R.layout.activity_fragment_container);

        setupActionBar();

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
            //TODO check que no hubiera ya uno? necesario?
            transaction.add(R.id.container, profileFragment, ProfileFragment.TAG);
            transaction.commit();
        }
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }else{
            return super.onOptionsItemSelected(item);
        }
    }
}
