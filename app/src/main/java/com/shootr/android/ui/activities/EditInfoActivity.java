package com.shootr.android.ui.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import butterknife.ButterKnife;
import com.shootr.android.R;
import com.shootr.android.ui.base.BaseSignedInActivity;

public class EditInfoActivity extends BaseSignedInActivity {

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!restoreSessionOrLogin()) return;

        setContainerContent(R.layout.activity_edit_info);
        ButterKnife.inject(this);

        setupActionBar();

    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("$Sevilla-Levante");
    }
}
