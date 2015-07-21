package com.shootr.android.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.shootr.android.R;
import com.shootr.android.domain.utils.LocaleProvider;
import com.shootr.android.ui.ToolbarDecorator;
import com.shootr.android.util.VersionUtils;
import javax.inject.Inject;

public class SupportActivity extends BaseToolbarDecoratedActivity {

    public static final String TERMS_OF_SERVICE_BASE_URL = "http://docs.shootr.com/#/terms/";
    public static final String PRIVACY_POLICY_SERVICE_BASE_URL = "http://docs.shootr.com/#/privacy/";

    @Inject LocaleProvider localeProvider;

    @Bind(R.id.support_terms_service_container) FrameLayout termsAndService;
    @Bind(R.id.support_privacy_policy_container) FrameLayout privacyPolicy;
    @Bind(R.id.support_version_number) TextView versionNumber;

    @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
        /* no-op */
    }

    @Override protected int getLayoutResource() {
        return R.layout.activity_support;
    }

    @Override protected void initializeViews(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        versionNumber.setVisibility(View.VISIBLE);
        versionNumber.setText(String.valueOf(VersionUtils.getVersionName(getApplication())));
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

    @OnClick(R.id.support_terms_service_container)
    public void onTermsAndServiceClick() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(TERMS_OF_SERVICE_BASE_URL + localeProvider.getLanguage()));
        startActivity(browserIntent);
    }

    @OnClick(R.id.support_privacy_policy_container)
    public void onPrivacyPolicyClick() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_POLICY_SERVICE_BASE_URL + localeProvider.getLanguage()));
        startActivity(browserIntent);
    }

}
