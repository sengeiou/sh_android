package com.shootr.mobile.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.base.BaseActivity;
import com.shootr.mobile.ui.presenter.WelcomePagePresenter;
import com.shootr.mobile.ui.views.WelcomePageView;
import com.shootr.mobile.util.FeedbackMessage;
import javax.inject.Inject;

public class WelcomePageActivity extends BaseActivity implements WelcomePageView {

  @Inject WelcomePagePresenter presenter;
  @Inject FeedbackMessage feedbackMessage;

  @BindView(R.id.button_get_started) View getStartedButton;
  @BindView(R.id.get_started_progress) View loading;

  @Override protected int getLayoutResource() {
    return R.layout.activity_welcome_page;
  }

  @Override protected void initializeViews(Bundle savedInstanceState) {
    ButterKnife.bind(this);
  }

  @Override protected void initializePresenter() {
    presenter.initialize(this);
  }

  @Override protected boolean requiresUserLogin() {
    return false;
  }

  @Override public void showError(String errorMessage) {
    feedbackMessage.show(getView(), errorMessage);
  }

  @Override public void goToStreamList() {
    finish();
    Intent i = new Intent(this, MainTabbedActivity.class);
    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(i);
  }

  @Override public void showSpinner() {
    loading.setVisibility(View.VISIBLE);
  }

  @Override public void hideGetStarted() {
    getStartedButton.setVisibility(View.GONE);
  }

  @OnClick(R.id.button_get_started) public void onGetStartedClick() {
    presenter.getStartedClicked();
  }
}
