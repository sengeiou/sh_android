package com.shootr.mobile.ui.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.ToolbarDecorator;
import com.shootr.mobile.ui.adapters.PollResultsAdapter;
import com.shootr.mobile.ui.adapters.listeners.OnPollOptionClickListener;
import com.shootr.mobile.ui.model.PollModel;
import com.shootr.mobile.ui.model.PollOptionModel;
import com.shootr.mobile.ui.presenter.PollResultsPresenter;
import com.shootr.mobile.ui.views.PollResultsView;
import com.shootr.mobile.util.FeedbackMessage;
import com.shootr.mobile.util.InitialsLoader;
import com.shootr.mobile.util.MenuItemValueHolder;
import com.shootr.mobile.util.PercentageUtils;
import javax.inject.Inject;

public class PollResultsActivity extends BaseToolbarDecoratedActivity implements PollResultsView {
  private static final String EXTRA_STREAM = "streamId";
  private static final String EXTRA_RESULTS = "results";

  @Bind(R.id.results_recycler) RecyclerView results;

  @Inject InitialsLoader initialsLoader;
  @Inject PercentageUtils percentageUtils;
  @Inject FeedbackMessage feedbackMessage;
  @Inject PollResultsPresenter presenter;

  private PollResultsAdapter adapter;

  private MenuItemValueHolder ignorePollMenu = new MenuItemValueHolder();

  public static Intent newResultsIntent(Context context, String idStream) {
    Intent intent = new Intent(context, PollResultsActivity.class);
    intent.putExtra(EXTRA_STREAM, idStream);
    intent.putExtra(EXTRA_RESULTS, context.getResources().getString(R.string.poll_results));
    return intent;
  }

  public static Intent newLiveResultsIntent(Context context, String idStream) {
    Intent intent = new Intent(context, PollResultsActivity.class);
    intent.putExtra(EXTRA_STREAM, idStream);
    intent.putExtra(EXTRA_RESULTS, context.getResources().getString(R.string.poll_live_results));
    return intent;
  }

  @Override protected int getLayoutResource() {
    return R.layout.activity_poll_results;
  }

  @Override protected void initializeViews(Bundle savedInstanceState) {
    ButterKnife.bind(this);
    adapter = new PollResultsAdapter(new OnPollOptionClickListener() {
      @Override public void onClickPressed(PollOptionModel pollOptionModel) {
        setupPollOptionDialog(pollOptionModel);
      }
    }, this, imageLoader, initialsLoader, percentageUtils);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
    results.setLayoutManager(linearLayoutManager);
    results.setAdapter(adapter);
  }

  private void setupPollOptionDialog(PollOptionModel pollOptionModel) {
    LayoutInflater inflater = this.getLayoutInflater();
    View dialogView = inflater.inflate(R.layout.dialog_poll_option_image, null);
    setupPollOptionText(dialogView, pollOptionModel);
    setupPollOptionImage(dialogView, pollOptionModel);
    createOptionDialog(dialogView);
  }

  private void setupPollOptionImage(View dialogView, PollOptionModel pollOptionModel) {
    ImageView pollOptionImage = (ImageView) dialogView.findViewById(R.id.poll_option_image);
    imageLoader.load(pollOptionModel.getImageUrl(), pollOptionImage);
  }

  private void setupPollOptionText(View dialogView, PollOptionModel pollOptionModel) {
    TextView pollOptionText = (TextView) dialogView.findViewById(R.id.poll_option_text);
    pollOptionText.setText(pollOptionModel.getText());
  }

  private void createOptionDialog(View dialogView) {
    AlertDialog alertDialog = new AlertDialog.Builder(this).setView(dialogView).create();
    alertDialog.getWindow().getAttributes().windowAnimations = R.style.growAnimation;
    alertDialog.show();
  }

  @Override protected void initializePresenter() {
    presenter.initialize(this, getIntent().getStringExtra(EXTRA_STREAM));
  }

  @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
    getToolbarDecorator().setTitle(getIntent().getStringExtra(EXTRA_RESULTS));
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_poll_results, menu);
    ignorePollMenu.bindRealMenuItem(menu.findItem(R.id.menu_ignore_poll));
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      finish();
      return true;
    } else if (item.getItemId() == R.id.menu_ignore_poll) {
      presenter.ignorePoll();
      return true;
    } else {
      return super.onOptionsItemSelected(item);
    }
  }

  @Override public void renderPollResults(PollModel pollModel) {
    adapter.setPollModel(pollModel);
  }

  @Override public void showError(String message) {
    feedbackMessage.show(getView(), message);
  }

  @Override public void ignorePoll() {
    finish();
  }

  @Override protected void onResume() {
    super.onResume();
    presenter.resume();
  }

  @Override protected void onPause() {
    super.onPause();
    presenter.pause();
  }
}
