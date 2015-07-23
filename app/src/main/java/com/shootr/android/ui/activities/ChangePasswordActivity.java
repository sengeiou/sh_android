package com.shootr.android.ui.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.shootr.android.R;
import com.shootr.android.ui.ToolbarDecorator;
import com.shootr.android.ui.presenter.ChangePasswordPresenter;
import com.shootr.android.ui.views.ChangePasswordView;
import javax.inject.Inject;

public class ChangePasswordActivity extends BaseToolbarDecoratedActivity implements ChangePasswordView {

    @Bind(R.id.current_password) EditText currentPasswordInput;
    @Bind(R.id.new_password) EditText newPasswordInput;
    @Bind(R.id.new_password_again) EditText newPasswordAgainInput;
    @Bind(R.id.change_password_button) View changePasswordButton;

    @Inject ChangePasswordPresenter changePasswordPresenter;

    @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
        /* no-op */
    }

    @Override protected int getLayoutResource() {
        return R.layout.activity_change_password;
    }

    @Override protected void initializeViews(Bundle savedInstanceState) {
        ButterKnife.bind(this);
    }

    @Override protected void initializePresenter() {
        changePasswordPresenter.initialize(this);
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

    @OnClick(R.id.change_password_button)
    public void onChangePasswordButtonPressed() {
        changePasswordPresenter.attempToChangePassword(currentPasswordInput.getText().toString(),
          newPasswordInput.getText().toString(), currentPasswordInput.getText().toString());
    }

    @Override public void showCurrentPasswordError(String errorMessage) {
        currentPasswordInput.setError(errorMessage);
    }

    @Override public void showNewPasswordError(String errorMessage) {
        newPasswordInput.setError(errorMessage);
    }

    @Override public void showNewPasswordAgainError(String errorMessage) {
        newPasswordAgainInput.setError(errorMessage);
    }

    @Override public void showError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }
}
