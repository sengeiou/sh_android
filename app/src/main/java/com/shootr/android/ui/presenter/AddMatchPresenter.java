package com.shootr.android.ui.presenter;

import com.path.android.jobqueue.JobManager;
import com.shootr.android.task.events.CommunicationErrorEvent;
import com.shootr.android.task.events.ConnectionNotAvailableEvent;
import com.shootr.android.task.events.info.SearchMatchResultEvent;
import com.shootr.android.task.jobs.info.SearchMatchJob;
import com.shootr.android.ui.model.MatchModel;
import com.shootr.android.ui.views.AddMatchView;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import dagger.ObjectGraph;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class AddMatchPresenter implements Presenter {

    private final JobManager jobManager;
    private final Bus bus;
    private AddMatchView addMatchView;
    private ObjectGraph objectGraph;
    private String currentSearchQuery;

    @Inject public AddMatchPresenter(JobManager jobManager, Bus bus) {
        this.jobManager = jobManager;
        this.bus = bus;
    }

    public void initialize(AddMatchView addMatchView, ObjectGraph objectGraph) {
        this.addMatchView = addMatchView;
        this.objectGraph = objectGraph;
    }

    public void search(String searchQuery) {
        currentSearchQuery = searchQuery;
        this.executeSearch();
        this.hideKeyboard();
    }

    private void hideKeyboard() {
        this.addMatchView.hideKeyboard();
    }

    private void executeSearch() {
        this.addMatchView.showLoading();
        this.addMatchView.hideResults();
        this.addMatchView.hideEmpty();
        SearchMatchJob job = objectGraph.get(SearchMatchJob.class);
        job.init(currentSearchQuery);
        jobManager.addJobInBackground(job);
    }

    @Subscribe
    public void onSearchResultsReceived(SearchMatchResultEvent event) {
        List<MatchModel> matchModels = event.getResult();
        if (!matchModels.isEmpty()) {
            showResults(matchModels);
        } else {
            showEmpty();
        }
    }

    private void showResults(List<MatchModel> matchModels) {
        this.addMatchView.renderResults(matchModels);
        this.addMatchView.hideEmpty();
        this.addMatchView.hideLoading();
    }

    private void showEmpty() {
        this.addMatchView.showEmpty();
        this.addMatchView.hideResults();
        this.addMatchView.hideLoading();
    }

    @Subscribe
    public void onConnectionNotAvailable(ConnectionNotAvailableEvent event) {
        this.addMatchView.alertConnectionNotAvailable();
        showEmpty();
    }

    @Subscribe
    public void onComunicationError(CommunicationErrorEvent event) {
        this.addMatchView.alertComunicationError();
        showEmpty();
    }

    @Override public void resume() {
        bus.register(this);
    }

    @Override public void pause() {
        bus.unregister(this);
    }
}
