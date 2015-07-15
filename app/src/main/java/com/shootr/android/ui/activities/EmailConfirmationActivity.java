package com.shootr.android.ui.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
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
        intent.putExtra(ProfileEditActivity.EXTRA_USER_EMAIL, email);
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
        String email = getIntent().getStringExtra(ProfileEditActivity.EXTRA_USER_EMAIL);
        presenter.initialize(this, email);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override public void showConfirmationToUser(String email) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Confirm Your Email") //
          .setMessage("Click the link in the email we've just sent to " + email) //
          .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialogInterface, int i) {
                /* no-op */
              }
        }).show();
    }

    @Override public void showUserEmail(String userEmail) {
        email.setText(userEmail);
    }

    @Override public void showError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }

    @Override public void showEmailError(String errorMessage) {
        email.setError(errorMessage);
    }

    @Override public void updateDoneButton() {
        // TODO Change visibility
    }

    @OnTextChanged(R.id.email_confirmation_email)
    public void onEmailChanged() {
        presenter.onEmailEdited(email.getText().toString());
    }
}
