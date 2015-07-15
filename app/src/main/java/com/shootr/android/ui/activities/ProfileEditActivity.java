package com.shootr.android.ui.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.shootr.android.R;
import com.shootr.android.ui.base.BaseSignedInActivity;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.presenter.ProfileEditPresenter;
import com.shootr.android.ui.views.ProfileEditView;
import com.shootr.android.ui.widgets.FloatLabelLayout;
import com.shootr.android.ui.widgets.MaxLinesInputFilter;
import javax.inject.Inject;

public class ProfileEditActivity extends BaseSignedInActivity implements ProfileEditView {

    private static final int BIO_MAX_LINES = 1;
    private static final int BIO_MAX_LENGTH = 150;
    public static final String EXTRA_USER_EMAIL = "user_email";

    @Inject ProfileEditPresenter presenter;

    @Bind(R.id.scroll) ScrollView scroll;
    @Bind(R.id.profile_edit_name) TextView name;
    @Bind(R.id.profile_edit_username) TextView username;
    @Bind(R.id.profile_edit_website) TextView website;
    @Bind(R.id.profile_edit_bio) TextView bio;
    @Bind(R.id.profile_edit_email) TextView email;
    @Bind(R.id.profile_edit_email_layout) FloatLabelLayout emailLayout;
    private MenuItem menuItemDone;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!restoreSessionOrLogin()) {
            return;
        }
        setContainerContent(R.layout.activity_profile_edit);

        ButterKnife.bind(this);

        scrollViewFocusHack();
        limitBioFilters();
        initializePresenter();
        setupActionBar();
    }

    private void limitBioFilters() {
        bio.setFilters(new InputFilter[] {
          new MaxLinesInputFilter(BIO_MAX_LINES), new InputFilter.LengthFilter(BIO_MAX_LENGTH)
        });
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_action_navigation_close);
    }

    private void scrollViewFocusHack() {
        scroll.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
        scroll.setFocusable(true);
        scroll.setFocusableInTouchMode(true);
        scroll.setOnTouchListener(new View.OnTouchListener() {
            @Override public boolean onTouch(View v, MotionEvent event) {
                v.requestFocusFromTouch();
                return false;
            }
        });
    }

    private void initializePresenter() {
        presenter.initialize(this, getObjectGraph());
    }

    @Override protected void onResume() {
        super.onResume();
        presenter.resume();
    }

    @Override protected void onPause() {
        super.onPause();
        presenter.pause();
    }

    @Override public void renderUserInfo(UserModel userModel) {
        name.setText(userModel.getName());
        username.setText(userModel.getUsername());
        website.setText(userModel.getWebsite());
        bio.setText(userModel.getBio());
        email.setText(userModel.getEmail());
        email.setKeyListener(null);
    }

    @Override public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(name.getWindowToken(), 0);
    }

    @Override public void showUpdatedSuccessfulAlert() {
        Toast.makeText(this, R.string.profile_updated, Toast.LENGTH_SHORT).show();
    }

    @Override public void closeScreen() {
        finish();
    }

    @Override public String getUsername() {
        return username.getText().toString();
    }

    @Override public String getName() {
        return name.getText().toString();
    }

    @Override public String getBio() {
        return bio.getText().toString();
    }

    @Override public String getWebsite() {
        return website.getText().toString();
    }

    @Override public void showUsernameValidationError(String errorMessage) {
        username.setError(errorMessage);
    }

    @Override public void showNameValidationError(String errorMessage) {
        name.setError(errorMessage);
    }

    @Override public void showWebsiteValidationError(String errorMessage) {
        website.setError(errorMessage);
    }

    @Override public void showBioValidationError(String errorMessage) {
        bio.setError(errorMessage);
    }

    @Override public void showDiscardConfirmation() {
        new AlertDialog.Builder(this).setMessage(R.string.discard_profile_confirmation)
          .setPositiveButton(R.string.discard, new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                  presenter.confirmDiscard();
              }
          })
          .setNegativeButton(R.string.cancel, null)
          .show();
    }

    @Override public void showLoadingIndicator() {
        menuItemDone.setActionView(R.layout.item_list_loading);
    }

    @Override public void hideLoadingIndicator() {
        menuItemDone.setActionView(null);

    }

    @Override public void alertComunicationError() {
        Toast.makeText(this, R.string.communication_error, Toast.LENGTH_SHORT).show();
    }

    @Override public void alertConnectionNotAvailable() {
        Toast.makeText(this, R.string.connection_lost, Toast.LENGTH_SHORT).show();
    }

    @Override public void showEmailNotConfirmedError(String error) {
        email.setError(error);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_edit, menu);
        menuItemDone = menu.findItem(R.id.menu_done);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_done) {
            presenter.done();
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            presenter.discard();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.profile_edit_email_layout)
    public void onEmailClick() {
        startActivity(EmailConfirmationActivity.newIntent(this, email.getText().toString()));
    }
}
