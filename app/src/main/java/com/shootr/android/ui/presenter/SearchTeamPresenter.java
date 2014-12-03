package com.shootr.android.ui.presenter;

import com.path.android.jobqueue.JobManager;
import com.shootr.android.task.events.CommunicationErrorEvent;
import com.shootr.android.task.events.ConnectionNotAvailableEvent;
import com.shootr.android.task.events.profile.SearchTeamResultEvent;
import com.shootr.android.task.jobs.profile.SearchTeamJob;
import com.shootr.android.ui.model.TeamModel;
import com.shootr.android.ui.views.SearchTeamView;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import dagger.ObjectGraph;
import java.util.List;
import javax.inject.Inject;

public class SearchTeamPresenter implements Presenter {


    private SearchTeamView searchTeamView;
    private ObjectGraph objectGraph;

    private final Bus bus;
    private final JobManager jobManager;
    private String currentTeamName;
    private boolean isSearchInterfaceReady;

    @Inject public SearchTeamPresenter(Bus bus, JobManager jobManager) {
        this.bus = bus;
        this.jobManager = jobManager;
    }

    public void initialize(SearchTeamView searchTeamView, ObjectGraph objectGraph) {
        this.searchTeamView = searchTeamView;
        this.objectGraph = objectGraph;
    }

    public void setCurrentTeamName(String team) {
        if (team != null && !team.isEmpty()) {
            currentTeamName = team;
            enableDeleteTeam();
            if (isSearchInterfaceReady) {
                setSearchQuery(currentTeamName);
            }
        }
    }

    private void enableDeleteTeam() {
        searchTeamView.enableDeleteTeam(currentTeamName);
    }

    public void search(String queryText) {
        this.hideKeyboard();
        if (queryText.length() < 3) {
            this.searchTeamView.notifyMinimunThreeCharacters();
            return;
        }
        this.executeSearch(queryText);
    }

    public void selectTeam(TeamModel selectedTeam) {
        String teamName = selectedTeam.getName();
        Long teamId = selectedTeam.getIdTeam();
        searchTeamView.deliverSelectedTeam(teamName, teamId);
    }

    public void removeTeam() {
        searchTeamView.deliverSelectedTeam(null, null);
    }

    private void executeSearch(String queryText) {
        searchTeamView.showLoading();
        searchTeamView.hideEmpty();
        searchTeamView.hideResults();
        SearchTeamJob job = objectGraph.get(SearchTeamJob.class);
        job.init(queryText);
        jobManager.addJobInBackground(job);
    }

    @Subscribe
    public void onSearchResultReceived(SearchTeamResultEvent event) {
        List<TeamModel> teams = event.getResult();
        if (!teams.isEmpty()) {
            showResults(teams);
        } else {
            showEmpty();
        }
    }

    private void showEmpty() {
        searchTeamView.showEmpty();
        searchTeamView.hideLoading();
        searchTeamView.hideResults();
    }

    private void showResults(List<TeamModel> teams) {
        searchTeamView.renderResults(teams);
        searchTeamView.hideEmpty();
        searchTeamView.hideLoading();
    }

    public void searchInterfaceReady() {
        isSearchInterfaceReady = true;
        if (currentTeamName != null) {
            setSearchQuery(currentTeamName);
        }
    }

    private void setSearchQuery(String teamName) {
        searchTeamView.setCurrentSearchText(teamName);
    }

    @Subscribe
    public void onCommunicationError(CommunicationErrorEvent event) {
        showEmpty();
        searchTeamView.alertComunicationError();
    }

    @Subscribe
    public void onConnectionNotAvailable(ConnectionNotAvailableEvent event) {
        showEmpty();
        this.searchTeamView.alertConnectionNotAvailable();
    }

    private void hideKeyboard() {
        this.searchTeamView.hideKeyboard();
    }


    @Override public void resume() {
        bus.register(this);
    }

    @Override public void pause() {
        bus.unregister(this);
    }

}
