package com.shootr.android.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.shootr.android.R;
import com.shootr.android.domain.utils.LocaleProvider;
import com.shootr.android.ui.ToolbarDecorator;
import com.shootr.android.util.VersionUtils;
import javax.inject.Inject;

public class SupportActivity extends BaseToolbarDecoratedActivity {

    @Inject LocaleProvider localeProvider;

    @Bind(R.id.support_version_number) TextView versionNumber;

    @BindString(R.string.terms_of_service_base_url) String termsOfServiceBaseUrl;
    @BindString(R.string.privay_policy_service_base_url) String privacyPolicyServiceBaseUrl;

    @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
        /* no-op */
    }

    @Override protected int getLayoutResource() {
        return R.layout.activity_support;
    }

    @Override protected void initializeViews(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        versionNumber.setText(VersionUtils.getVersionName(getApplication()));
    }

    @Override protected void initializePresenter() {
        /* no-op */
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

    @OnClick(R.id.support_terms_service_text)
    public void onTermsAndServiceClick() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(termsOfServiceBaseUrl + localeProvider.getLanguage()));
        startActivity(browserIntent);
    }

    @OnClick(R.id.privacy_policy_text)
    public void onPrivacyPolicyClick() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(privacyPolicyServiceBaseUrl + localeProvider.getLanguage()));
        startActivity(browserIntent);
    }

}
