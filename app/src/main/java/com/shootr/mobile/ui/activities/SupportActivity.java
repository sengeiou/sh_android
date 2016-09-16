package com.shootr.mobile.ui.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import com.shootr.mobile.R;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.utils.LocaleProvider;
import com.shootr.mobile.ui.ToolbarDecorator;
import com.shootr.mobile.ui.presenter.SupportPresenter;
import com.shootr.mobile.ui.views.EasterEggActivity;
import com.shootr.mobile.ui.views.SupportView;
import com.shootr.mobile.util.AnalyticsTool;
import com.shootr.mobile.util.IntentFactory;
import com.shootr.mobile.util.Intents;
import com.shootr.mobile.util.VersionUtils;
import java.util.Locale;
import javax.inject.Inject;

public class SupportActivity extends BaseToolbarDecoratedActivity implements SupportView {

    @Inject LocaleProvider localeProvider;
    @Inject IntentFactory intentFactory;
    @Inject SupportPresenter supportPresenter;
    @Inject AnalyticsTool analyticsTool;

    @BindView(R.id.support_version_number) TextView versionNumber;
    @BindView(R.id.support_blog_text) TextView blog;
    @BindView(R.id.support_help_text) TextView help;

    @BindString(R.string.terms_of_service_base_url) String termsOfServiceBaseUrl;
    @BindString(R.string.privay_policy_service_base_url) String privacyPolicyServiceBaseUrl;
    @BindString(R.string.analytics_screen_support) String analyticsScreenStreamSupport;

    //region lifecycle methods
    @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
        /* no-op */
    }

    @Override protected int getLayoutResource() {
        return R.layout.activity_support;
    }

    @Override protected void initializeViews(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        analyticsTool.analyticsStart(getBaseContext(), analyticsScreenStreamSupport);
        versionNumber.setText(VersionUtils.getVersionName(getApplication()));
    }

    @Override protected void initializePresenter() {
        supportPresenter.initialize(this);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
    //endregion

    //region Click listeners
    @OnClick(R.id.support_terms_service_text) public void onTermsAndServiceClick() {
        String termsUrl = String.format(termsOfServiceBaseUrl, localeProvider.getLanguage());
        Intent termsIntent = intentFactory.openEmbededUrlIntent(this, termsUrl);
        Intents.maybeStartActivity(this, termsIntent);
    }

    @OnClick(R.id.privacy_policy_text) public void onPrivacyPolicyClick() {
        String termsUrl = String.format(privacyPolicyServiceBaseUrl, localeProvider.getLanguage());
        Intent termsIntent = intentFactory.openEmbededUrlIntent(this, termsUrl);
        Intents.maybeStartActivity(this, termsIntent);
    }

    @OnClick(R.id.support_blog_text) public void onBlogClick() {
        supportPresenter.blogClicked();
    }

    @OnClick(R.id.support_help_text) public void onHelpClick() {
        supportPresenter.helpClicked();
    }

    @OnLongClick(R.id.support_version_container) public boolean onVersionLongClick() {
        Intent intent = new Intent(this, EasterEggActivity.class);
        startActivity(intent);
        return true;
    }
    //endregion

    @Override public void showError() {
        help.setEnabled(false);
        help.setTextColor(getResources().getColor(R.color.gray_60));
        blog.setEnabled(false);
        blog.setTextColor(getResources().getColor(R.color.gray_60));
    }

    @Override public void goToStream(Stream blog) {
        Intent intent = StreamTimelineActivity.newIntent(this, blog.getId(), blog.getTitle(), blog.getAuthorId());
        startActivity(intent);
    }

    @Override public void handleReport() {
        supportPresenter.setUpAlertDialog(Locale.getDefault().getLanguage());
    }

    @Override public void showAlertDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder //
          .setMessage(getString(R.string.language_support_alert)) //
          .setPositiveButton(getString(R.string.email_confirmation_ok), null).show();
    }

    @Override protected void onPause() {
        super.onPause();
        analyticsTool.analyticsStop(getBaseContext(), this);
    }
}
