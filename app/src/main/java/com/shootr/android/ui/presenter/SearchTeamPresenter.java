package com.shootr.android.ui.presenter;

import com.path.android.jobqueue.JobManager;
import com.shootr.android.ui.views.SearchTeamView;
import com.squareup.otto.Bus;
import dagger.ObjectGraph;
import java.util.Arrays;
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
        currentTeamName = team;
        if (isSearchInterfaceReady) {
            setSearchQueryAndExecute(currentTeamName);
        }
    }

    public void search(String queryText) {
        //TODO launch job
        List<String> teams =
          Arrays.asList("Sevilla", "Barcelona", "Real Madrid", "La Palma del Condado", "Recreativo de Huelva");
        onSearchResultReceived(teams);
    }

    //TODO recibir evento
    public void onSearchResultReceived(List<String> teams) {
        searchTeamView.renderResults(teams);
    }

    public void searchInterfaceReady() {
        isSearchInterfaceReady = true;
        setSearchQueryAndExecute(currentTeamName);
    }

    private void setSearchQueryAndExecute(String teamName) {
        searchTeamView.setCurrentSearchText(teamName);
        this.search(teamName);
    }

    @Override public void resume() {
        bus.register(this);
    }

    @Override public void pause() {
        bus.unregister(this);
    }
}
