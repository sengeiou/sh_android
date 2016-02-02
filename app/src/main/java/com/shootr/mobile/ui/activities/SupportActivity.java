package com.shootr.mobile.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import com.shootr.mobile.domain.Stream;
import com.shootr.mobile.domain.utils.LocaleProvider;
import com.shootr.mobile.ui.ToolbarDecorator;
import com.shootr.mobile.ui.presenter.SupportPresenter;
import com.shootr.mobile.ui.views.SupportView;
import com.shootr.mobile.util.IntentFactory;
import com.shootr.mobile.util.Intents;
import com.shootr.mobile.util.VersionUtils;
import javax.inject.Inject;

public class SupportActivity extends BaseToolbarDecoratedActivity implements SupportView {

    @Inject LocaleProvider localeProvider;
    @Inject IntentFactory intentFactory;
    @Inject SupportPresenter supportPresenter;

    @Bind(com.shootr.mobile.R.id.support_version_number) TextView versionNumber;
    @Bind(com.shootr.mobile.R.id.support_blog_text) TextView blog;
    @Bind(com.shootr.mobile.R.id.support_help_text) TextView help;

    @BindString(com.shootr.mobile.R.string.terms_of_service_base_url) String termsOfServiceBaseUrl;
    @BindString(com.shootr.mobile.R.string.privay_policy_service_base_url) String privacyPolicyServiceBaseUrl;

    //region lifecycle methods
    @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
        /* no-op */
    }

    @Override protected int getLayoutResource() {
        return com.shootr.mobile.R.layout.activity_support;
    }

    @Override protected void initializeViews(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        versionNumber.setText(VersionUtils.getVersionName(getApplication()));
    }

    @Override protected void initializePresenter() {
        supportPresenter.initialize(this);
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
    //endregion

    //region Click listeners
    @OnClick(com.shootr.mobile.R.id.support_terms_service_text)
    public void onTermsAndServiceClick() {
        String termsUrl = String.format(termsOfServiceBaseUrl, localeProvider.getLanguage());
        Intent termsIntent = intentFactory.openEmbededUrlIntent(this, termsUrl);
        Intents.maybeStartActivity(this, termsIntent);
    }

    @OnClick(com.shootr.mobile.R.id.privacy_policy_text)
    public void onPrivacyPolicyClick() {
        String termsUrl = String.format(privacyPolicyServiceBaseUrl, localeProvider.getLanguage());
        Intent termsIntent = intentFactory.openEmbededUrlIntent(this, termsUrl);
        Intents.maybeStartActivity(this, termsIntent);
    }

    @OnClick(com.shootr.mobile.R.id.support_blog_text)
    public void onBlogClick() {
        supportPresenter.blogClicked();
    }

    @OnClick(com.shootr.mobile.R.id.support_help_text)
    public void onHelpClick() {
        supportPresenter.helpClicked();
    }

    @OnLongClick(com.shootr.mobile.R.id.support_version_container)
    public boolean onVersionLongClick() {
        Toast.makeText(this, com.shootr.mobile.R.string.app_easter_egg, Toast.LENGTH_SHORT).show();
        return true;
    }

    //endregion

    @Override public void showError() {
        help.setEnabled(false);
        help.setTextColor(getResources().getColor(com.shootr.mobile.R.color.gray_60));
        blog.setEnabled(false);
        blog.setTextColor(getResources().getColor(com.shootr.mobile.R.color.gray_60));
    }

    @Override public void goToStream(Stream blog) {
        Intent intent = StreamTimelineActivity.newIntent(this, blog.getId(), blog.getShortTitle(), blog.getAuthorId());
        startActivity(intent);
    }

    @Override public void showSupportLanguageDialog() {
        /* implements */
    }
}
