package com.shootr.mobile.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.LinearLayout;
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
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.presenter.OnBoardingPresenter;
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
  @BindView(R.id.continue_container) LinearLayout getStartedButton;
  @BindView(R.id.get_started_progress) ProgressBar progressBar;
  @BindView(R.id.activity_on_boarding_stream) RelativeLayout container;
  @BindString(R.string.analytics_source_on_boarding) String onBoardingSource;
  @BindString(R.string.analytics_action_favorite_stream) String analyticsActionFavoriteStream;
  @BindString(R.string.analytics_label_favorite_stream) String analyticsLabelFavoriteStream;

  @Inject ImageLoader imageLoader;
  @Inject InitialsLoader initialsLoader;
  @Inject OnBoardingPresenter presenter;
  @Inject FeedbackMessage feedbackMessage;
  @Inject AnalyticsTool analyticsTool;
  @Inject SessionRepository sessionRepository;

  private OnBoardingAdapter adapter;

  @Override protected int getLayoutResource() {
    return R.layout.activity_on_boarding_stream;
  }

  @Override protected void initializeViews(Bundle savedInstanceState) {
    ButterKnife.bind(this);
    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    streamsList.setLayoutManager(layoutManager);
    LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_from_bottom);
    streamsList.setLayoutAnimation(animation);
    streamsList.setAdapter(getOnBoardingAdapter());
  }

  private OnBoardingAdapter getOnBoardingAdapter() {
    if (adapter == null) {
      adapter = new OnBoardingAdapter(new OnBoardingFavoriteClickListener() {
        @Override public void onFavoriteClick(OnBoardingModel onBoardingStream) {
          presenter.putFavorite(onBoardingStream);
          adapter.updateFavorite(onBoardingStream);
        }

        @Override public void onRemoveFavoriteClick(OnBoardingModel onBoardingStream) {
          presenter.removeFavorite(onBoardingStream.getStreamModel().getIdStream());
          adapter.updateFavorite(onBoardingStream);
        }
      }, imageLoader, initialsLoader, OnBoardingAdapter.STREAM_ONBOARDING);
    }
    return adapter;
  }

  @Override protected void initializePresenter() {
    presenter.initialize(this, OnBoardingPresenter.STREAM_ONBOARDING);
  }

  @Override public void showLoading() {
    streamsList.setVisibility(GONE);
    progressBar.setVisibility(View.VISIBLE);
  }

  @Override public void hideLoading() {
    streamsList.setVisibility(View.VISIBLE);
    progressBar.setVisibility(GONE);
    getStartedButton.setVisibility(View.VISIBLE);
  }

  @Override public void renderOnBoardingList(List<OnBoardingModel> onBoardingModels) {
    adapter.setOnBoardingModelList(onBoardingModels);
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

  @Override public void sendUserAnalytics(UserModel userModel) {
    /* no-op */
  }

  @Override public void goNextScreen() {
    Intent i = new Intent(this, OnBoardingUserActivity.class);
    startActivity(i);
    overridePendingTransition(R.anim.slide_in_up, R.anim.stay);
    finish();
  }

  @Override public void hideGetStarted() {
    getStartedButton.setVisibility(View.GONE);
  }

  @Override public void showError(String errorMessage) {
    feedbackMessage.show(getView(), errorMessage);
    goNextScreen();
  }

  @OnClick(R.id.continue_container) public void onGetStartedClick() {
    presenter.continueClicked();
  }
}
