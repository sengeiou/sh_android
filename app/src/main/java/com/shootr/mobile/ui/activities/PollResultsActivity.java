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
import butterknife.BindView;
import butterknife.ButterKnife;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.ToolbarDecorator;
import com.shootr.mobile.ui.adapters.PollResultsAdapter;
import com.shootr.mobile.ui.adapters.listeners.OnPollOptionClickListener;
import com.shootr.mobile.ui.model.PollModel;
import com.shootr.mobile.ui.model.PollOptionModel;
import com.shootr.mobile.ui.presenter.PollResultsPresenter;
import com.shootr.mobile.ui.views.PollResultsView;
import com.shootr.mobile.util.BackStackHandler;
import com.shootr.mobile.util.CustomContextMenu;
import com.shootr.mobile.util.FeedbackMessage;
import com.shootr.mobile.util.InitialsLoader;
import com.shootr.mobile.util.Intents;
import com.shootr.mobile.util.MenuItemValueHolder;
import com.shootr.mobile.util.PercentageUtils;
import com.shootr.mobile.util.ShareManager;
import com.shootr.mobile.util.SwipeDialog;
import javax.inject.Inject;

public class PollResultsActivity extends BaseToolbarDecoratedActivity implements PollResultsView {
  private static final String EXTRA_ID_POLL = "pollId";
  private static final String EXTRA_RESULTS = "results";
  private static final String EXTRA_STREAM_TITLE = "title";
  private static final String NO_TITLE = "";

  @BindView(R.id.results_recycler) RecyclerView results;
  @BindView(R.id.pollresults_progress) ProgressBar progressBar;

  @Inject InitialsLoader initialsLoader;
  @Inject PercentageUtils percentageUtils;
  @Inject FeedbackMessage feedbackMessage;
  @Inject PollResultsPresenter presenter;
  @Inject BackStackHandler backStackHandler;
  @Inject ShareManager shareManager;

  private PollResultsAdapter adapter;

  private MenuItemValueHolder ignorePollMenu = new MenuItemValueHolder();

  public static Intent newResultsIntent(Context context, String idPoll, String streamTitle) {
    Intent intent = new Intent(context, PollResultsActivity.class);
    intent.putExtra(EXTRA_ID_POLL, idPoll);
    intent.putExtra(EXTRA_RESULTS, context.getResources().getString(R.string.poll_results));
    intent.putExtra(EXTRA_STREAM_TITLE, streamTitle);
    return intent;
  }

  public static Intent newLiveResultsIntent(Context context, String idPoll, String streamTitle) {
    Intent intent = new Intent(context, PollResultsActivity.class);
    intent.putExtra(EXTRA_ID_POLL, idPoll);
    intent.putExtra(EXTRA_RESULTS, context.getResources().getString(R.string.poll_live_results));
    intent.putExtra(EXTRA_STREAM_TITLE, streamTitle);
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
      backStackHandler.handleBackStack(this);
      finish();
      return true;
    } else if (item.getItemId() == R.id.menu_share) {
      openSharePollMenu();
    } else if (item.getItemId() == R.id.menu_ignore_poll) {
      presenter.ignorePoll();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override public void renderPollResults(PollModel pollModel) {
    String title = getIntent().getStringExtra(EXTRA_STREAM_TITLE);
    pollModel.setStreamTitle(title == null ? NO_TITLE : title);
    adapter.setPollModel(pollModel);
    adapter.notifyDataSetChanged();
  }

  @Override public void showError(String message) {
    feedbackMessage.show(getView(), message);
  }

  @Override public void ignorePoll() {
    finish();
  }

  @Override public void share(PollModel pollModel) {
    Intent shareIntent = shareManager.sharePollIntent(this, pollModel);
    Intents.maybeStartActivity(this, shareIntent);
  }

  @Override public void showPollVotes(Long votes) {
    Integer pollVotes = votes.intValue();
    String pollVotesText =
        getResources().getQuantityString(R.plurals.poll_votes_count, pollVotes, pollVotes);
    getToolbarDecorator().setSubtitle(pollVotesText);
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

  private void openSharePollMenu() {
    new CustomContextMenu.Builder(this).addAction(R.string.menu_share_shot_via_shootr, new Runnable() {
      @Override public void run() {
        presenter.shareViaShootr();
      }
    }).addAction(R.string.menu_share_shot_via, new Runnable() {
      @Override public void run() {
        presenter.share();
      }
    }).show();
  }
}
