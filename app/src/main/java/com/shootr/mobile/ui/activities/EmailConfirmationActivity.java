package com.shootr.mobile.ui.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.ToolbarDecorator;
import com.shootr.mobile.ui.presenter.EmailConfirmationPresenter;
import com.shootr.mobile.ui.views.EmailConfirmationView;
import com.shootr.mobile.util.FeedbackMessage;
import javax.inject.Inject;

public class EmailConfirmationActivity extends BaseToolbarDecoratedActivity implements EmailConfirmationView {

    @Bind(com.shootr.mobile.R.id.email_confirmation_email) TextView email;
    @Bind(com.shootr.mobile.R.id.email_confirmation_confirm_button) View confirmButton;

    @Inject EmailConfirmationPresenter presenter;
    @Inject FeedbackMessage feedbackMessage;

    public static Intent newIntent(Context context, String email) {
        Intent intent = new Intent(context, EmailConfirmationActivity.class);
        intent.putExtra(ProfileEditActivity.EXTRA_USER_EMAIL, email);
        return intent;
    }

    //region Initialization
    @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
        toolbarDecorator.getActionBar().setDisplayShowHomeEnabled(false);
        toolbarDecorator.getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override protected int getLayoutResource() {
        return com.shootr.mobile.R.layout.activity_email_confirmation;
    }

    @Override protected void initializeViews(Bundle savedInstanceState) {
        ButterKnife.bind(this);
    }

    @Override protected void initializePresenter() {
        String userEmail = getIntent().getStringExtra(ProfileEditActivity.EXTRA_USER_EMAIL);
        presenter.initialize(this, userEmail);
    }
    //endregion

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @OnTextChanged(com.shootr.mobile.R.id.email_confirmation_email)
    public void onEmailChanged(CharSequence email) {
        presenter.onEmailEdited(email.toString());
    }

    @OnClick(com.shootr.mobile.R.id.email_confirmation_confirm_button)
    public void onDoneButtonClick() {
        presenter.done(email.getText().toString());
    }

    //region View methods
    @Override public void showConfirmationEmailSentAlert(String email, final Runnable alertCallback) {
        if(!isFinishing()) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(getString(R.string.email_confirmation_title)) //
              .setMessage(getString(R.string.email_confirmation_message_content, email)) //
              .setPositiveButton(getString(com.shootr.mobile.R.string.email_confirmation_ok), new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                      if (alertCallback != null) {
                          alertCallback.run();
                      }
                  }
              }).show();
        }
    }

    @Override public void showUserEmail(String userEmail) {
        email.setText(userEmail);
    }

    @Override public void showError(String errorMessage) {
        feedbackMessage.showLong(getView(), errorMessage);
    }

    @Override public void showEmailError(String errorMessage) {
        email.setError(errorMessage);
    }

    @Override public void showDoneButton() {
        confirmButton.setVisibility(View.VISIBLE);
    }

    @Override public void hideDoneButton() {
        confirmButton.setVisibility(View.GONE);
    }

    @Override public void closeScreen() {
        this.finish();
    }
    //endregion

}
