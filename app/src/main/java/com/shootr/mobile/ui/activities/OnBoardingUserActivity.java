package com.shootr.mobile.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.shootr.mobile.R;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.ui.adapters.OnBoardingAdapter;
import com.shootr.mobile.ui.adapters.listeners.OnBoardingFavoriteClickListener;
import com.shootr.mobile.ui.base.BaseActivity;
import com.shootr.mobile.ui.model.OnBoardingModel;
import com.shootr.mobile.ui.presenter.OnBoardingPresenter;
import com.shootr.mobile.ui.views.OnBoardingView;
import com.shootr.mobile.util.AnalyticsTool;
import com.shootr.mobile.util.FeedbackMessage;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.InitialsLoader;
import java.util.List;
import javax.inject.Inject;

import static android.view.View.GONE;

public class OnBoardingUserActivity extends BaseActivity implements OnBoardingView {

  @BindView(R.id.users_list) RecyclerView usersList;
  @BindView(R.id.get_started_button) Button getStartedButton;
  @BindView(R.id.get_started_progress) ProgressBar progressBar;
  @BindView(R.id.activity_on_boarding_user) RelativeLayout container;
  @BindString(R.string.analytics_source_on_boarding) String onBoardingSource;
  @BindString(R.string.analytics_action_follow) String analyticsActionFollow;
  @BindString(R.string.analytics_label_follow) String analyticsLabelFollow;

  @Inject ImageLoader imageLoader;
  @Inject InitialsLoader initialsLoader;
  @Inject OnBoardingPresenter presenter;
  @Inject FeedbackMessage feedbackMessage;
  @Inject AnalyticsTool analyticsTool;
  @Inject SessionRepository sessionRepository;

  private OnBoardingAdapter adapter;

  @Override protected int getLayoutResource() {
    return R.layout.activity_on_boarding_user;
  }

  @Override protected void initializeViews(Bundle savedInstanceState) {
    ButterKnife.bind(this);
    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    usersList.setLayoutManager(layoutManager);
    LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_slide_right);
    usersList.setLayoutAnimation(animation);
    usersList.setAdapter(getOnBoardingAdapter());
  }

  private OnBoardingAdapter getOnBoardingAdapter() {
    if (adapter == null) {
      adapter = new OnBoardingAdapter(new OnBoardingFavoriteClickListener() {
        @Override public void onFavoriteClick(OnBoardingModel onBoardingUser) {
          presenter.putUserFavorite(onBoardingUser);
          adapter.updateFavorite(onBoardingUser);
        }

        @Override public void onRemoveFavoriteClick(OnBoardingModel onBoardingUser) {
          presenter.removeUserFavorite(onBoardingUser.getUserModel().getIdUser());
          adapter.updateFavorite(onBoardingUser);
        }
      }, imageLoader, initialsLoader, OnBoardingAdapter.USER_ONBOARDING);
    }
    return adapter;
  }

  @Override protected void initializePresenter() {
    presenter.initialize(this, OnBoardingPresenter.USER_ONBOARDING);
  }

  @Override public void showLoading() {
    usersList.setVisibility(GONE);
    progressBar.setVisibility(View.VISIBLE);
  }

  @Override public void hideLoading() {
    usersList.setVisibility(View.VISIBLE);
    progressBar.setVisibility(GONE);
  }

  @Override public void renderOnBoardingList(List<OnBoardingModel> onBoardingModels) {
    adapter.setOnBoardingModelList(onBoardingModels);
    adapter.notifyDataSetChanged();
  }

  @Override public void sendStreamAnalytics(String idStream, String streamTitle, boolean isStrategic) {
    /* no-op */
  }

  @Override public void sendUserAnalytics(boolean isStrategic) {
    AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
    builder.setContext(this);
    builder.setActionId(analyticsActionFollow);
    builder.setLabelId(analyticsLabelFollow);
    builder.setSource(onBoardingSource);
    builder.setUser(sessionRepository.getCurrentUser());
    builder.setIsStrategic(isStrategic);
    analyticsTool.analyticsSendAction(builder);
    analyticsTool.appsFlyerSendAction(builder);
  }

  @Override public void goNextScreen() {
    Intent i = new Intent(this, SplashScreenActivity.class);
    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(i);
    overridePendingTransition(R.anim.activity_back_in, R.anim.stay);
    finish();
  }

  @Override public void hideGetStarted() {
    getStartedButton.setVisibility(View.INVISIBLE);
  }

  @Override public void showError(String errorMessage) {
    feedbackMessage.show(getView(), errorMessage);
  }

  @OnClick(R.id.continue_container) public void onGetStartedClick() {
    presenter.continueClicked();
  }
}
