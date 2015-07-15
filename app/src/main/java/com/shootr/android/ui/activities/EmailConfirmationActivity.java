package com.shootr.android.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.shootr.android.R;
import com.shootr.android.ui.ToolbarDecorator;
import com.shootr.android.ui.presenter.EmailConfirmationPresenter;
import com.shootr.android.ui.views.EmailConfirmationView;
import javax.inject.Inject;

public class EmailConfirmationActivity extends BaseToolbarDecoratedActivity implements EmailConfirmationView {

    @Bind(R.id.email_confirmation_email) TextView email;
    @Bind(R.id.email_confirmation_confirm_button) View confirmButton;
    @Bind(R.id.email_confirmation_confirm_progress) View progress;

    @Inject EmailConfirmationPresenter presenter;

    public static Intent newIntent(Context context, String email) {
        Intent intent = new Intent(context, EmailConfirmationActivity.class);
        intent.putExtra(ProfileEditActivity.EXTRA_EVENT_ID, email);
        return intent;
    }

    @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
        toolbarDecorator.getActionBar().setDisplayShowHomeEnabled(false);
        toolbarDecorator.getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override protected int getLayoutResource() {
        return R.layout.activity_email_confirmation;
    }

    @Override protected void initializeViews(Bundle savedInstanceState) {
        ButterKnife.bind(this);
    }

    @Override protected void initializePresenter() {
        presenter.initialize(this);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
