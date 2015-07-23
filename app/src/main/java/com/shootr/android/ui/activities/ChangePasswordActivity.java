package com.shootr.android.ui.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.shootr.android.R;
import com.shootr.android.ui.ToolbarDecorator;

public class ChangePasswordActivity extends BaseToolbarDecoratedActivity {

    @Bind(R.id.current_password) EditText currentPasswordInput;
    @Bind(R.id.new_password) EditText newPasswordInput;
    @Bind(R.id.new_password_again) EditText newPasswordAgainInput;
    @Bind(R.id.change_password_button) View changePasswordButton;

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
