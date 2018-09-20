package com.shootr.mobile.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import butterknife.ButterKnife;
import com.shootr.mobile.R;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.ui.ToolbarDecorator;
import com.shootr.mobile.ui.fragments.SearchStreamToShareFragment;
import com.shootr.mobile.ui.fragments.streamtimeline.TimelineFragment;
import com.shootr.mobile.ui.model.SearchableModel;
import com.shootr.mobile.ui.presenter.SearchPresenter;
import com.shootr.mobile.util.AnalyticsTool;
import java.util.List;
import javax.inject.Inject;

public class SearchToShareActivity extends BaseToolbarDecoratedActivity
    implements com.shootr.mobile.ui.views.SearchView {

  public static final String EXTRA_QUERY = "query";

  @Inject SearchPresenter presenter;
  @Inject AnalyticsTool analyticsTool;
  @Inject SessionRepository sessionRepository;

  private SearchView searchView;
  private String currentSearchQuery;
  private Bundle savedInstanceState;

  private SearchStreamToShareFragment currentFragment;

  private ToolbarDecorator toolbarDecorator;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override protected int getLayoutResource() {
    return R.layout.activity_search_to_share;
  }

  @Override protected void initializeViews(Bundle savedInstanceState) {
    ButterKnife.bind(this);
    setupActionBar();
    this.savedInstanceState = savedInstanceState;
    setupAndAddFragment(savedInstanceState);
  }

  @Override protected void initializePresenter() {
    presenter.initialize(this);
    presenter.initialSearch(SearchPresenter.TYPE_STREAM);
  }

  @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
    this.toolbarDecorator = toolbarDecorator;
    this.toolbarDecorator.hideElevation();
    this.toolbarDecorator.getActionBar().setDisplayShowHomeEnabled(true);
    this.toolbarDecorator.getActionBar().setDisplayHomeAsUpEnabled(true);
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.search, menu);
    MenuItem searchItem = menu.findItem(R.id.menu_search);
    createSearchView(searchItem);
    setupQuery();

    SearchView.SearchAutoComplete searchAutoComplete =
        (SearchView.SearchAutoComplete) searchView.findViewById(
            android.support.v7.appcompat.R.id.search_src_text);
    searchAutoComplete.setHintTextColor(getResources().getColor(R.color.hint_black));
    searchAutoComplete.setTextColor(getResources().getColor(R.color.icons));
    searchView.requestFocus();
    searchView.setQueryHint(getResources().getString(R.string.activity_find_streams_hint));
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      finish();
      return true;
    } else {
      return super.onOptionsItemSelected(item);
    }
  }

  @Override public void finish() {
    super.finish();
    overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
  }

  private void setupQuery() {
    if (searchView != null) {
      searchView.clearFocus();
    }
    if (currentSearchQuery != null) {
      searchView.setQuery(currentSearchQuery, false);
      searchView.clearFocus();
    }
  }

  private void createSearchView(MenuItem searchItem) {
    searchView = (SearchView) searchItem.getActionView();

    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
      @Override public boolean onQueryTextSubmit(String queryText) {
        if (currentFragment != null) {
          presenter.search(queryText, SearchPresenter.TYPE_STREAM);
          hideKeyboard();
        }
        return true;
      }

      @Override public boolean onQueryTextChange(String query) {
        if (currentFragment != null) {
          presenter.search(query, SearchPresenter.TYPE_STREAM);
          currentSearchQuery = query;
        }
        return false;
      }
    });
    searchView.setQueryHint(getResources().getString(R.string.menu_search_streams));
    searchView.setIconifiedByDefault(false);
    searchView.setIconified(false);
    if (savedInstanceState != null) {
      searchView.setQuery(savedInstanceState.getCharSequence(EXTRA_QUERY), false);
    }
  }

  public void hideKeyboard() {
    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
  }

  private void setupActionBar() {
    ActionBar actionBar = getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);
    actionBar.setDisplayShowHomeEnabled(false);
  }

  @Override protected void onResume() {
    super.onResume();
    presenter.resume();
    if (searchView != null) {
      searchView.clearFocus();
    }
  }

  @Override protected void onPause() {
    super.onPause();
    presenter.pause();
  }

  @Override public void renderSearch(List<SearchableModel> searchableModelList) {

  }

  @Override public void renderUsersSearch(List<SearchableModel> searchableModelList) {

  }

  @Override public void renderStreamsSearch(List<SearchableModel> searchableModelList) {
    if (currentFragment != null) {
      currentFragment.renderSearchItems(searchableModelList);
    }
  }

  private void setupAndAddFragment(Bundle savedInstanceState) {
    boolean fragmentAlreadyAddedBySystem = savedInstanceState != null;
    Bundle fragmentArguments = getIntent().getExtras();


      if (getIntent().getType() != null && getIntent().getType().startsWith("image/")
          || getIntent().getType() != null && getIntent().getType().startsWith("text/plain")) {
        currentFragment = SearchStreamToShareFragment.newInstance(fragmentArguments);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.container, currentFragment, TimelineFragment.TAG);
        transaction.commit();
      } else {
        finish();
      }

  }

}