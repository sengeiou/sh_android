package com.shootr.android.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import com.shootr.android.R;
import com.shootr.android.ui.ToolbarDecorator;
import com.shootr.android.ui.fragments.ProfileFragment;
import com.shootr.android.ui.presenter.AllShotsPresenter;
import com.shootr.android.ui.views.AllShotsView;
import javax.inject.Inject;

public class AllShotsActivity extends BaseToolbarDecoratedActivity implements AllShotsView {

    @Inject AllShotsPresenter presenter;

    public static Intent newIntent(Context context, String userId) {
        Intent intent = new Intent(context, AllShotsActivity.class);
        intent.putExtra(ProfileFragment.ARGUMENT_USER, userId);
        return intent;
    }

    @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {

    }

    @Override protected int getLayoutResource() {
        return R.layout.activity_all_shots;
    }

    @Override protected void initializeViews(Bundle savedInstanceState) {
        if (getIntent().getExtras() == null) {
            throw new RuntimeException("No intent extras, no party");
        }
    }

    @Override protected void initializePresenter() {
        String userId = getIntent().getStringExtra(ProfileFragment.ARGUMENT_USER);
        presenter.initialize(this, userId);
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
