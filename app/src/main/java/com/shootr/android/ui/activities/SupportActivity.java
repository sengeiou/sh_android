package com.shootr.android.ui.activities;

import android.content.Intent;
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
import com.shootr.android.ui.views.SupportView;
import com.shootr.android.util.IntentFactory;
import com.shootr.android.util.Intents;
import com.shootr.android.util.VersionUtils;
import javax.inject.Inject;

public class SupportActivity extends BaseToolbarDecoratedActivity implements SupportView {

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
        Intent termsIntent = intentFactory.openEmbededUrlIntent(this, termsUrl);
        Intents.maybeStartActivity(this, termsIntent);
    }

    @OnClick(R.id.support_blog_text)
    public void onBlogClick() {
        // TODO
    }

    @OnClick(R.id.support_help_text)
    public void onHelpClick() {
        // TODO
    }

}
