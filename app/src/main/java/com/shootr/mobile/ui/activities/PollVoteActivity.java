package com.shootr.mobile.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.ToolbarDecorator;
import com.shootr.mobile.ui.adapters.PollOptionAdapter;
import com.shootr.mobile.ui.adapters.holders.PollOptionHolder;
import com.shootr.mobile.ui.adapters.listeners.OnPollOptionClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnPollOptionLongClickListener;
import com.shootr.mobile.ui.model.PollModel;
import com.shootr.mobile.ui.model.PollOptionModel;
import com.shootr.mobile.ui.presenter.PollVotePresenter;
import com.shootr.mobile.ui.views.PollVoteView;
import com.shootr.mobile.util.InitialsLoader;
import com.shootr.mobile.util.MenuItemValueHolder;
import javax.inject.Inject;

public class PollVoteActivity extends BaseToolbarDecoratedActivity implements PollVoteView {

  private static final String EXTRA_STREAM = "idStream";

  @Bind(R.id.poll_option_list) GridView gridView;
  @Bind(R.id.poll_question) TextView pollQuestion;

  @Inject InitialsLoader initialsLoader;
  @Inject PollOptionHolder pollOptionHolder;
  @Inject PollVotePresenter presenter;

  private PollOptionAdapter pollOptionAdapter;
  private MenuItemValueHolder ignorePollMenu = new MenuItemValueHolder();

  public static Intent newIntent(Context context, String idStream) {
    Intent intent = new Intent(context, PollVoteActivity.class);
    intent.putExtra(EXTRA_STREAM, idStream);
    return intent;
  }

  @Override protected int getLayoutResource() {
    return R.layout.activity_poll_vote;
  }

  @Override protected void initializePresenter() {
    presenter.initialize(this, getIntent().getStringExtra(EXTRA_STREAM));
  }

  @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
    /* no - op */
  }

  @Override protected void initializeViews(Bundle savedInstanceState) {
    ButterKnife.bind(this);
    setupActionbar();
    pollOptionAdapter = new PollOptionAdapter(this, new OnPollOptionClickListener() {
      @Override public void onClickPressed(PollOptionModel pollOptionModel) {
        presenter.voteOption(pollOptionModel.getIdPollOption());
      }
    }, new OnPollOptionLongClickListener() {
      @Override public void onLongPress(PollOptionModel pollOptionModel) {
        //TODO
      }
    }, imageLoader, initialsLoader, pollOptionHolder);
    gridView.setAdapter(pollOptionAdapter);
    ViewCompat.setNestedScrollingEnabled(gridView, true);
  }

  private void setupActionbar() {
    ActionBar actionBar = getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);
    actionBar.setDisplayShowHomeEnabled(false);
    actionBar.setHomeAsUpIndicator(R.drawable.ic_navigation_close_white_24);
  }

  @Override public void renderPoll(PollModel pollModel) {
    pollQuestion.setText(pollModel.getQuestion());
    pollOptionAdapter.setPollOptionModels(pollModel.getPollOptionModels());
    pollOptionAdapter.notifyDataSetChanged();
  }

  @Override public void ignorePoll() {
    finish();
  }

  @Override public void goToResults(String idStream) {
    Intent intent = PollResultsActivity.newResultsIntent(this, idStream);
    startActivity(intent);
    finish();
  }

  @Override public void showError(String message) {
    //TODO
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_poll_vote, menu);
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
}
