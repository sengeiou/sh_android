package com.shootr.mobile.ui.activities.registro;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import com.shootr.mobile.R;
import com.shootr.mobile.domain.utils.LocaleProvider;
import com.shootr.mobile.ui.activities.MainTabbedActivity;
import com.shootr.mobile.ui.base.BaseActivity;
import com.shootr.mobile.ui.presenter.PrivacyLawPresenter;
import com.shootr.mobile.ui.views.PrivacyLawView;
import javax.inject.Inject;

public class PrivacyLawsActivity extends BaseActivity implements PrivacyLawView {

  private static final String AUTOMATIC_PRIVACY = "AUTOMATIC_PRIVACY";

  @Inject PrivacyLawPresenter presenter;
  @Inject LocaleProvider localeProvider;

  private Button acceptButton;
  private WebView webView;

  private boolean isAutomatic = false;

  public static Intent newIntent(@NonNull Context context) {
    Intent intent = new Intent(context, PrivacyLawsActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
    intent.putExtra(AUTOMATIC_PRIVACY, true);
    return intent;
  }

  @Override protected int getLayoutResource() {
    return R.layout.activity_privacy_laws;
  }

  @Override protected boolean requiresUserLogin() {
    return false;
  }

  @Override protected void initializeViews(Bundle savedInstanceState) {
    isAutomatic = getIntent().getBooleanExtra(AUTOMATIC_PRIVACY, false);
    acceptButton = (Button) findViewById(R.id.accept_button);
    acceptButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if (!isAutomatic) {
          setResult(RESULT_OK);
          finish();
        } else {
          presenter.onAcceptTerms();
        }
      }
    });

    setupWebView();
  }

  private void setupWebView() {
    String privacyUrl =
        String.format(getResources().getString(R.string.privacy_policy_service_url),
            localeProvider.getLanguage());
    webView = (WebView) findViewById(R.id.webview);
    WebSettings webSettings = webView.getSettings();
    webSettings.setJavaScriptEnabled(true);
    webView.loadUrl(privacyUrl);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      webView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
        @Override public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX,
            int oldScrollY) {
          acceptButton.setEnabled(true);
        }
      });
    } else {
      acceptButton.setEnabled(true);
    }
  }

  @Override protected void initializePresenter() {
    presenter.initialize(this);
  }

  @Override public void onBackPressed() {
    setResult(RESULT_CANCELED);
    finish();
  }

  @Override public void exit() {
    Intent intent = new Intent(this, MainTabbedActivity.class);
    startActivity(intent);
    finish();
  }
}
