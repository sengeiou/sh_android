package com.shootr.android.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import com.shootr.android.R;
import com.shootr.android.ui.adapters.TeamAdapter;
import com.shootr.android.ui.base.BaseSignedInActivity;
import com.shootr.android.ui.model.TeamModel;
import com.shootr.android.ui.presenter.SearchTeamPresenter;
import com.shootr.android.ui.views.SearchTeamView;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class SearchTeamActivity extends BaseSignedInActivity implements SearchTeamView {

    @InjectView(R.id.search_team_list) ListView list;
    @InjectView(R.id.search_results_empty) TextView emptyOrErrorView;
    @InjectView(R.id.search_loading) ProgressBar loadingView;

    @Inject SearchTeamPresenter presenter;

    private TeamAdapter adapter;
    private SearchView searchView;
    private String restoredQuery;
    private View removeTeamItem;
    private View maxResultsIndicator;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerContent(R.layout.activity_search_team);
        ButterKnife.inject(this);
        initializeViews();
        setupActionbar();
        initializePresenter();
    }

    private void initializeViews() {
        adapter = new TeamAdapter(this);
        list.setAdapter(adapter);
    }

    private void setupActionbar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
    }

    private void initializePresenter() {
        presenter.initialize(this, getObjectGraph());
        String currentTeamName = getIntent().getStringExtra(ProfileEditActivity.EXTRA_TEAM_NAME);
        presenter.setCurrentTeamName(currentTeamName);
    }

    @OnItemClick(R.id.search_team_list)
    public void onTeamSelected(int position) {
        if (position < adapter.getCount()) {
            TeamModel selectedTeam = adapter.getItem(position);
            presenter.selectTeam(selectedTeam);
        } else {
            presenter.removeTeam();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        searchView = (SearchView) searchItem.getActionView();
        setupSearchView();
        return true;
    }

    @Override protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ProfileEditActivity.EXTRA_TEAM_NAME, String.valueOf(searchView.getQuery()));
    }

    @Override protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        restoredQuery = savedInstanceState.getString(ProfileEditActivity.EXTRA_TEAM_NAME);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupSearchView() {
        searchView.setQueryHint(getString(R.string.activity_search_team_hint));
        searchView.setIconifiedByDefault(false);
        searchView.setIconified(false);

        SearchView.SearchAutoComplete searchAutoComplete =
          (SearchView.SearchAutoComplete) searchView.findViewById(R.id.search_src_text);
        searchAutoComplete.setHintTextColor(getResources().getColor(R.color.hint_black));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String queryText) {
                presenter.search(queryText);
                return true;
            }

            @Override public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        presenter.searchInterfaceReady();

        if (restoredQuery != null) {
            searchView.setQuery(restoredQuery, true);
        }
    }

    @Override public void setCurrentSearchText(String searchText) {
        if (searchView != null) {
            searchView.setQuery(searchText, false);
        } else {
            Timber.w("Tried to set query text before searchView is initialized");
        }
    }

    @Override public void renderResults(List<TeamModel> teams) {
        list.setVisibility(View.VISIBLE);
        adapter.setContent(teams);
    }

    @Override public void hideResults() {
        list.setVisibility(View.GONE);
    }

    @Override public void showLoading() {
        loadingView.setVisibility(View.VISIBLE);
    }

    @Override public void hideLoading() {
        loadingView.setVisibility(View.GONE);
    }

    @Override public void showEmpty() {
        emptyOrErrorView.setVisibility(View.VISIBLE);
    }

    @Override public void hideEmpty() {
        emptyOrErrorView.setVisibility(View.GONE);
    }

    @Override public void showMaxResultsIndicator() {
        maxResultsIndicator = LayoutInflater.from(this).inflate(R.layout.item_list_search_team_max_items, list, false);
        list.addFooterView(maxResultsIndicator, null, false);
    }

    @Override public void hideMaxResultsIndicator() {
        list.removeFooterView(maxResultsIndicator);
    }

    @Override public void enableDeleteTeam(String teamName) {
        if (removeTeamItem == null) {
            removeTeamItem = LayoutInflater.from(this).inflate(R.layout.item_list_search_team_remove, list, false);
        }
        TextView removeTeamItemTitle = (TextView) removeTeamItem.findViewById(R.id.team_remove_title);
        String removeTeamItemTitleText = getString(R.string.edit_profile_team_remove_footer, teamName);
        removeTeamItemTitle.setText(removeTeamItemTitleText);
        list.addFooterView(removeTeamItem);
        list.setAdapter(adapter);
    }

    @Override public void disableDeleteTeam() {
        if (removeTeamItem != null) {
            list.removeFooterView(removeTeamItem);
        }
    }

    @Override public void deliverSelectedTeam(String teamName, Long teamId) {
        Intent data = new Intent();
        data.putExtra(ProfileEditActivity.EXTRA_TEAM_NAME, teamName);
        data.putExtra(ProfileEditActivity.EXTRA_TEAM_ID, teamId);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override public void alertComunicationError() {
        Toast.makeText(this, R.string.communication_error, Toast.LENGTH_SHORT).show();
    }

    @Override public void alertConnectionNotAvailable() {
        Toast.makeText(this, R.string.connection_lost, Toast.LENGTH_SHORT).show();
    }

    @Override public void notifyMinimunThreeCharacters() {
        Toast.makeText(this, getString(R.string.search_warning_minimun_characters), Toast.LENGTH_SHORT).show();
    }

    @Override public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
    }
}
