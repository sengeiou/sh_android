package com.shootr.android.ui.presenter;

import com.path.android.jobqueue.JobManager;
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
        if (!searchQuery.equals(currentSearchQuery)) {
            currentSearchQuery = searchQuery;
            this.executeSearch();
        }
        this.hideKeyboard();
    }

    private void hideKeyboard() {
        this.addMatchView.hideKeyboard();
    }

    private void executeSearch() {
        this.addMatchView.showLoading();
        this.addMatchView.hideResults();
        SearchMatchJob job = objectGraph.get(SearchMatchJob.class);
        jobManager.addJobInBackground(job);
    }

    @Subscribe
    public void onSearchResultsReceived(SearchMatchResultEvent event) {
        List<MatchModel> matchModels = event.getResult();
        if (!matchModels.isEmpty()) {
            this.addMatchView.renderResults(matchModels);
            this.addMatchView.hideEmpty();
            this.addMatchView.hideLoading();
        } else {
            this.addMatchView.showEmpty();
            this.addMatchView.hideResults();
            this.addMatchView.hideLoading();
        }
    }

    @Override public void resume() {
        bus.register(this);
    }

    @Override public void pause() {
        bus.unregister(this);
    }
}
