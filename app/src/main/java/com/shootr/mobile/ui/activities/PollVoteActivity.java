package com.shootr.mobile.ui.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.ToolbarDecorator;
import com.shootr.mobile.ui.adapters.PollVoteAdapter;
import com.shootr.mobile.ui.adapters.listeners.OnPollOptionClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnPollOptionLongClickListener;
import com.shootr.mobile.ui.model.PollModel;
import com.shootr.mobile.ui.model.PollOptionModel;
import com.shootr.mobile.ui.presenter.PollVotePresenter;
import com.shootr.mobile.ui.views.PollVoteView;
import com.shootr.mobile.util.BackStackHandler;
import com.shootr.mobile.util.FeedbackMessage;
import com.shootr.mobile.util.InitialsLoader;
import com.shootr.mobile.util.MenuItemValueHolder;
import com.shootr.mobile.util.SwipeDialog;
import javax.inject.Inject;

public class PollVoteActivity extends BaseToolbarDecoratedActivity implements PollVoteView {

  private static final String EXTRA_STREAM = "idStream";
  public static final String EXTRA_STREAM_TITLE = "streamTitle";
  public static final String EXTRA_ID_POLL = "idPoll";
  public static final String EXTRA_ID_USER_OWNER = "userIdOwner";
  private static final String NO_TITLE = "";
  private static final int COLUMNS_NUMBER = 4;

  @BindView(R.id.poll_option_list) RecyclerView pollOptionsRecycler;
  @BindView(R.id.poll_question) TextView pollQuestion;
  @BindView(R.id.pollvote_progress) ProgressBar progressBar;
  @BindView(R.id.poll_results) TextView viewResults;
  @BindView(R.id.stream_title) TextView streamTitle;

  @Inject InitialsLoader initialsLoader;
  @Inject PollVotePresenter presenter;
  @Inject FeedbackMessage feedbackMessage;
  @Inject BackStackHandler backStackHandler;

  private PollVoteAdapter pollVoteAdapter;
  private MenuItemValueHolder ignorePollMenu = new MenuItemValueHolder();

  public static Intent newIntent(Context context, String idStream, String streamTitle) {
    Intent intent = new Intent(context, PollVoteActivity.class);
    intent.putExtra(EXTRA_STREAM, idStream);
    intent.putExtra(EXTRA_STREAM_TITLE, streamTitle == null ? NO_TITLE : streamTitle);
    return intent;
  }

  public static Intent newIntentWithIdPoll(Context context, String idPoll, String streamTitle) {
    Intent intent = new Intent(context, PollVoteActivity.class);
    intent.putExtra(EXTRA_ID_POLL, idPoll);
    intent.putExtra(EXTRA_STREAM_TITLE, streamTitle == null ? NO_TITLE : streamTitle);
    return intent;
  }

  @Override protected int getLayoutResource() {
    return R.layout.activity_poll_vote;
  }

  @Override protected void initializePresenter() {
    String idStream = getIntent().getStringExtra(EXTRA_STREAM);
    String idStreamOwner = getIntent().getStringExtra(EXTRA_ID_USER_OWNER);
    if (idStream != null) {
      presenter.initialize(this, idStream, idStreamOwner);
    } else {
      presenter.initializeWithIdPoll(this, getIntent().getStringExtra(EXTRA_ID_POLL));
    }
  }

  @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
    /* no - op */
  }

  @Override protected void initializeViews(Bundle savedInstanceState) {
    ButterKnife.bind(this);
    setupActionbar();
    pollVoteAdapter = new PollVoteAdapter(new OnPollOptionClickListener() {
      @Override public void onClickPressed(PollOptionModel pollOptionModel) {
        presenter.voteOption(pollOptionModel.getIdPollOption());
      }
    }, new OnPollOptionLongClickListener() {
      @Override public void onLongPress(PollOptionModel pollOptionModel) {
        setupPollOptionDialog(pollOptionModel);
      }
    }, imageLoader, initialsLoader);
    GridLayoutManager layoutManager =
        new GridLayoutManager(this, COLUMNS_NUMBER);
    pollOptionsRecycler.setLayoutManager(layoutManager);
    pollOptionsRecycler.setAdapter(pollVoteAdapter);
  }

  private void setupPollOptionDialog(PollOptionModel pollOptionModel) {
    SwipeDialog.newPollImageDialog(pollOptionModel, imageLoader, this.getLayoutInflater())
        .show(getFragmentManager(), SwipeDialog.DIALOG);
  }

  private void setupActionbar() {
    ActionBar actionBar = getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);
    actionBar.setDisplayShowHomeEnabled(false);
    actionBar.setHomeAsUpIndicator(R.drawable.ic_navigation_close_white_24);
  }

  @Override public void renderPoll(PollModel pollModel) {
    String title = getIntent().getStringExtra(EXTRA_STREAM_TITLE);
    streamTitle.setText(title);
    pollQuestion.setText(pollModel.getQuestion());
    pollVoteAdapter.setPollOptionModels(pollModel.getPollOptionModels());
    pollVoteAdapter.notifyDataSetChanged();
  }

  @Override public void showPollVotes(Long votes) {
    Integer pollVotes = votes.intValue();
    String pollVotesText =
        getResources().getQuantityString(R.plurals.poll_votes_count, pollVotes, pollVotes);
    getToolbarDecorator().setSubtitle(pollVotesText);
  }

  @Override public void ignorePoll() {
    finish();
  }

  @Override public void goToResults(String idPoll) {
    String title = getIntent().getStringExtra(EXTRA_STREAM_TITLE);
    Intent intent = PollResultsActivity.newLiveResultsIntent(this, idPoll, title);
    startActivity(intent);
    finish();
  }

  @Override public void showError(String message) {
    feedbackMessage.show(getView(), message);
  }

  @Override public void showTimeoutAlert() {
    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
    alertDialogBuilder.setMessage(R.string.connection_lost) //
        .setPositiveButton(getString(R.string.poll_vote_timeout_retry), new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
            presenter.retryVote();
          }
        }).show();
  }

  @Override public void showViewResultsButton() {
    viewResults.setVisibility(View.VISIBLE);
  }

  @Override public void showResultsWithoutVotingDialog() {
    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
    alertDialogBuilder //
        .setMessage(getString(R.string.poll_results_dialog)) //
        .setPositiveButton(getString(R.string.poll_results),
            new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialogInterface, int action) {
                presenter.showPollResultsWithoutVoting();
              }
            }).setNegativeButton(getString(R.string.cancel), null).show();
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_poll_vote, menu);
    ignorePollMenu.bindRealMenuItem(menu.findItem(R.id.menu_ignore_poll));
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      backStackHandler.handleBackStack(this);
      finish();
      return true;
    } else if (item.getItemId() == R.id.menu_show_results) {
      presenter.onShowPollResults();
      return true;
    } else if (item.getItemId() == R.id.menu_ignore_poll) {
      presenter.ignorePoll();
      return true;
    } else {
      return super.onOptionsItemSelected(item);
    }
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
    pollOptionsRecycler.setVisibility(View.GONE);
  }

  @Override public void hideLoading() {
    pollOptionsRecycler.setVisibility(View.VISIBLE);
    progressBar.setVisibility(View.GONE);
  }

  @OnClick(R.id.poll_results) public void goToResults() {
    presenter.viewResults();
  }
}
