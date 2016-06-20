package com.shootr.mobile.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
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
import com.shootr.mobile.util.SwipeDialog;
import javax.inject.Inject;

public class PollResultsActivity extends BaseToolbarDecoratedActivity implements PollResultsView {
  private static final String EXTRA_ID_POLL = "pollId";
  private static final String EXTRA_RESULTS = "results";

  @Bind(R.id.results_recycler) RecyclerView results;
  @Bind(R.id.pollresults_progress) ProgressBar progressBar;

  @Inject InitialsLoader initialsLoader;
  @Inject PercentageUtils percentageUtils;
  @Inject FeedbackMessage feedbackMessage;
  @Inject PollResultsPresenter presenter;

  private PollResultsAdapter adapter;

  private MenuItemValueHolder ignorePollMenu = new MenuItemValueHolder();

  public static Intent newResultsIntent(Context context, String idPoll) {
    Intent intent = new Intent(context, PollResultsActivity.class);
    intent.putExtra(EXTRA_ID_POLL, idPoll);
    intent.putExtra(EXTRA_RESULTS, context.getResources().getString(R.string.poll_results));
    return intent;
  }

  public static Intent newLiveResultsIntent(Context context, String idPoll) {
    Intent intent = new Intent(context, PollResultsActivity.class);
    intent.putExtra(EXTRA_ID_POLL, idPoll);
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
    SwipeDialog.newPollImageDialog(pollOptionModel, imageLoader, this.getLayoutInflater())
        .show(getFragmentManager(), SwipeDialog.DIALOG);
  }

  @Override protected void initializePresenter() {
    presenter.initialize(this, getIntent().getStringExtra(EXTRA_ID_POLL));
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
    adapter.notifyDataSetChanged();
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

  @Override public void showEmpty() {
    /* no-op */
  }

  @Override public void hideEmpty() {
    /* no-op */
  }

  @Override public void showLoading() {
    progressBar.setVisibility(View.VISIBLE);
    results.setVisibility(View.GONE);
  }

  @Override public void hideLoading() {
    results.setVisibility(View.VISIBLE);
    progressBar.setVisibility(View.GONE);
  }
}
