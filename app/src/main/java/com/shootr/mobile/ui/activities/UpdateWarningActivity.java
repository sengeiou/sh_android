package com.shootr.mobile.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.shootr.mobile.R;
import com.shootr.mobile.ui.base.BaseNoToolbarActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UpdateWarningActivity extends BaseNoToolbarActivity {

    @Bind(R.id.update_warning_button) TextView updateButton;

    public static Intent newIntent(@NonNull Context context) {
        Intent intent = new Intent(context, UpdateWarningActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerContent(R.layout.activity_update_warning);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.update_warning_button)
    public void onUpdateClick() {
        goToAppDownload(getString(R.string.update_shootr_version_url));
    }

    private void goToAppDownload(String url) {
        Uri uriUrl = Uri.parse(url);
        Intent lastVersionDownload = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(lastVersionDownload);
    }
}
