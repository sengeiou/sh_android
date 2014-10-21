package gm.mobi.android.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import gm.mobi.android.R;
import gm.mobi.android.ui.base.BaseSignedInActivity;
import gm.mobi.android.ui.fragments.ProfileFragment;
import gm.mobi.android.ui.model.UserModel;
import timber.log.Timber;

public class ProfileContainerActivity extends BaseSignedInActivity {

    private static final String EXTRA_USER = "user";
    Long idUser;

    public static Intent getIntent(Context context, Long idUser) {
        Intent i = new Intent(context, ProfileContainerActivity.class);
        i.putExtra(EXTRA_USER, idUser);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!restoreSessionOrLogin()) return;
        setContainerContent(R.layout.activity_fragment_container);

        setupActionBar();

        if (savedInstanceState == null) {
            idUser = (Long) getIntent().getSerializableExtra(EXTRA_USER);
            if (idUser == null) {
                Timber.e("Se intentó abrir la pantalla de perfil con sin pasarle user");
                finish();
            }
            ProfileFragment profileFragment = ProfileFragment.newInstance(idUser);
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
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override public void onBackPressed() {
        Intent data = new Intent();
        Bundle bundleUser = new Bundle();
        UserModel user = ProfileFragment.getUser();
        bundleUser.putLong("ID_USER", user.getIdUser());
        bundleUser.putString("USER_NAME", user.getUserName());
        bundleUser.putString("NAME", user.getName());
        bundleUser.putString("FAVORITE_TEAM", user.getFavoriteTeamName());
        bundleUser.putInt("RELATIONSHIP", user.getRelationship());
        bundleUser.putString("PHOTO", user.getPhoto());
        data.putExtras(bundleUser);
        setResult(Activity.RESULT_OK,data);
        super.onBackPressed();

    }
}
