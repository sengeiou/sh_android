package com.shootr.mobile.ui.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.shootr.mobile.R;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.ui.adapters.OnBoardingStreamsAdapter;
import com.shootr.mobile.ui.adapters.listeners.OnBoardingFavoriteClickListener;
import com.shootr.mobile.ui.base.BaseActivity;
import com.shootr.mobile.ui.model.OnBoardingModel;
import com.shootr.mobile.ui.presenter.OnBoardingStreamPresenter;
import com.shootr.mobile.ui.views.OnBoardingView;
import com.shootr.mobile.util.AnalyticsTool;
import com.shootr.mobile.util.FeedbackMessage;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.InitialsLoader;
import java.util.List;
import javax.inject.Inject;

import static android.view.View.GONE;

public class OnBoardingStreamActivity extends BaseActivity implements OnBoardingView {

  @BindView(R.id.streams_list) RecyclerView streamsList;
  @BindView(R.id.get_started_button) Button getStartedButton;
  @BindView(R.id.get_started_progress) ProgressBar progressBar;
  @BindView(R.id.activity_on_boarding_stream) RelativeLayout container;
  @BindString(R.string.analytics_source_on_boarding) String onBoardingSource;
  @BindString(R.string.analytics_action_favorite_stream) String analyticsActionFavoriteStream;
  @BindString(R.string.analytics_label_favorite_stream) String analyticsLabelFavoriteStream;

  @Inject ImageLoader imageLoader;
  @Inject InitialsLoader initialsLoader;
  @Inject OnBoardingStreamPresenter presenter;
  @Inject FeedbackMessage feedbackMessage;
  @Inject AnalyticsTool analyticsTool;
  @Inject SessionRepository sessionRepository;

  private OnBoardingStreamsAdapter adapter;

  @Override protected int getLayoutResource() {
    return R.layout.activity_on_boarding_stream;
  }

  @Override protected void initializeViews(Bundle savedInstanceState) {
    ButterKnife.bind(this);
    animateView(getStartedButton);
    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    streamsList.setLayoutManager(layoutManager);
    streamsList.setAdapter(getOnBoardingAdapter());
  }

  private OnBoardingStreamsAdapter getOnBoardingAdapter() {
    if (adapter == null) {
      adapter = new OnBoardingStreamsAdapter(new OnBoardingFavoriteClickListener() {
        @Override public void onFavoriteClick(OnBoardingModel onBoardingStream) {
          presenter.putFavorite(onBoardingStream);
          adapter.updateFavorite(onBoardingStream);
        }

        @Override public void onRemoveFavoriteClick(OnBoardingModel onBoardingStream) {
          presenter.removeFavorite(onBoardingStream.getStreamModel().getIdStream());
          adapter.updateFavorite(onBoardingStream);
        }
      }, imageLoader, initialsLoader);
    }
    return adapter;
  }

  @Override protected void initializePresenter() {
    presenter.initialize(this, OnBoardingStreamPresenter.STREAM_ONBOARDING);
  }

  @Override public void showLoading() {
    streamsList.setVisibility(GONE);
    progressBar.setVisibility(View.VISIBLE);
  }

  @Override public void hideLoading() {
    streamsList.setVisibility(View.VISIBLE);
    progressBar.setVisibility(GONE);
  }

  @Override public void renderOnBoardingList(List<OnBoardingModel> onBoardingStreamModels) {
    container.setVisibility(View.VISIBLE);
    adapter.setOnBoardingStreamModelList(onBoardingStreamModels);
    adapter.notifyDataSetChanged();
  }

  @Override public void sendStreamAnalytics(String idStream, String streamTitle, boolean isStrategic) {
    AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
    builder.setContext(this);
    builder.setActionId(analyticsActionFavoriteStream);
    builder.setLabelId(analyticsLabelFavoriteStream);
    builder.setSource(onBoardingSource);
    builder.setUser(sessionRepository.getCurrentUser());
    builder.setStreamName(streamTitle);
    builder.setIsStrategic(isStrategic);
    builder.setIdStream(idStream);
    analyticsTool.analyticsSendAction(builder);
    analyticsTool.appsFlyerSendAction(builder);
  }

  @Override public void goToUserOnboardingList() {
    finish();
    //TODO ir a la pantalla del onboarding de usuarios
    //TODO esto comentado ha de ir en el metodo goToStream de la pantalla de users
    /*Intent i = new Intent(this, MainTabbedActivity.class);
    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(i);*/
  }

  @Override public void hideGetStarted() {
    getStartedButton.setVisibility(View.INVISIBLE);
  }

  @Override public void showError(String errorMessage) {
    feedbackMessage.show(getView(), errorMessage);
  }

  @OnClick(R.id.get_started_button) public void onGetStartedClick() {
    presenter.continueClicked();
  }

  private void animateView(final View viewToShow) {
    if (viewToShow.getVisibility() == View.INVISIBLE) {
      viewToShow.setVisibility(View.VISIBLE);
      viewToShow.setScaleX(0);
      viewToShow.setScaleY(0);
      ObjectAnimator scaleX = ObjectAnimator.ofFloat(viewToShow, "scaleX", 0f, 1f);
      ObjectAnimator scaleY = ObjectAnimator.ofFloat(viewToShow, "scaleY", 0f, 1f);
      AnimatorSet set = new AnimatorSet();
      set.playTogether(scaleX, scaleY);
      set.setDuration(500);
      set.setInterpolator(new DecelerateInterpolator());
      set.addListener(new AnimatorListenerAdapter() {
        @Override public void onAnimationEnd(Animator animation) {
          viewToShow.setScaleX(1f);
          viewToShow.setScaleY(1f);
        }
      });
      set.start();
    }
  }
}
