package com.shootr.android.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.shootr.android.R;
import com.shootr.android.ui.base.BaseNoToolbarActivity;

public class UpdateWarningActivity extends BaseNoToolbarActivity {

    @InjectView(R.id.update_warning_button) TextView updateButton;

    public static Intent newIntent(@NonNull Context context) {
        return new Intent(context, UpdateWarningActivity.class);
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerContent(R.layout.activity_update_warning);
        ButterKnife.inject(this);
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
