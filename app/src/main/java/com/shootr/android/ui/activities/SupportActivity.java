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
import com.shootr.android.util.IntentFactory;
import com.shootr.android.util.Intents;
import com.shootr.android.util.VersionUtils;
import javax.inject.Inject;

public class SupportActivity extends BaseToolbarDecoratedActivity {

    @Inject LocaleProvider localeProvider;
    @Inject IntentFactory intentFactory;

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
        String termsUrl = String.format(termsOfServiceBaseUrl, localeProvider.getLanguage());
        Intent termsIntent = intentFactory.openUrlIntent(termsUrl);
        Intents.maybeStartActivity(this, termsIntent);
    }

    @OnClick(R.id.privacy_policy_text)
    public void onPrivacyPolicyClick() {
        String privacyUrl = String.format(privacyPolicyServiceBaseUrl, localeProvider.getLanguage());
        Intent privacyIntent = intentFactory.openUrlIntent(privacyUrl);
        Intents.maybeStartActivity(this, privacyIntent);
    }

}
