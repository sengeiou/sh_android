package gm.mobi.android.ui.activities;

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
import gm.mobi.android.ui.fragments.ProfileFragmentBuilder;
import timber.log.Timber;

public class ProfileContainerActivity extends BaseSignedInActivity {

    private static final String EXTRA_USER_ID = "userid";

    public static Intent getIntent(Context context, Long userId) {
        Intent i = new Intent(context, ProfileContainerActivity.class);
        i.putExtra(EXTRA_USER_ID, userId);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!restoreSessionOrLogin()) return;
        setContainerContent(R.layout.activity_fragment_container);

        setupActionBar();

        if (savedInstanceState == null) {
            Long userId = getIntent().getLongExtra(EXTRA_USER_ID, 0);
            if (userId < 1) {
                Timber.e("Se intentó abrir la pantalla de perfil con id inválido: %d", userId);
                finish();
            }
            ProfileFragment profileFragment = ProfileFragmentBuilder.newProfileFragment(userId);
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
}
