package com.shootr.android.ui.presenter;

import com.path.android.jobqueue.JobManager;
import com.shootr.android.task.events.profile.SearchTeamResultEvent;
import com.shootr.android.task.jobs.profile.SearchTeamJob;
import com.shootr.android.ui.model.TeamModel;
import com.shootr.android.ui.views.SearchTeamView;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
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
        SearchTeamJob job = objectGraph.get(SearchTeamJob.class);
        job.init(queryText);
        jobManager.addJobInBackground(job);
    }

    public void selectTeam(TeamModel selectedTeam) {
        String teamName = selectedTeam.getName();
        Long teamId = selectedTeam.getIdTeam();
        searchTeamView.deliverSelectedTeam(teamName, teamId);
    }

    @Subscribe
    public void onSearchResultReceived(SearchTeamResultEvent event) {
        List<TeamModel> teams = event.getResult();
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
