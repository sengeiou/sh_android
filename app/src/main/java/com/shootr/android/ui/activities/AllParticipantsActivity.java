package com.shootr.android.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import com.shootr.android.R;
import com.shootr.android.ui.ToolbarDecorator;
import com.shootr.android.ui.presenter.AllParticipantsPresenter;
import com.shootr.android.ui.views.AllParticipantsView;
import javax.inject.Inject;

public class AllParticipantsActivity extends BaseToolbarDecoratedActivity implements AllParticipantsView {

    private static final String EXTRA_STREAM = "stream";

    @Inject AllParticipantsPresenter allParticipantsPresenter;

    public static Intent newIntent(Context context, String idStream) {
        Intent intent = new Intent(context, AllParticipantsActivity.class);
        intent.putExtra(EXTRA_STREAM, idStream);
        return intent;
    }

    @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
        //TODO
    }

    @Override protected int getLayoutResource() {
        return R.layout.activity_all_participants;
    }

    @Override protected void initializeViews(Bundle savedInstanceState) {
        //TODO
    }

    @Override protected void initializePresenter() {
        String idStream = getIntent().getStringExtra(EXTRA_STREAM);
        allParticipantsPresenter.initialize(this, idStream);
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

    @Override public void showEmpty() {

    }

    @Override public void hideEmpty() {

    }

    @Override public void showLoading() {

    }

    @Override public void hideLoading() {

    }

    @Override public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
