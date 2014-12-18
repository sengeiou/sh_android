package com.shootr.android.ui.presenter;

import com.path.android.jobqueue.JobManager;
import com.shootr.android.task.events.CommunicationErrorEvent;
import com.shootr.android.task.events.ConnectionNotAvailableEvent;
import com.shootr.android.task.events.follows.FollowsResultEvent;
import com.shootr.android.task.jobs.follows.GetPeopleJob;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.views.PeopleView;
import com.shootr.android.util.ErrorMessageFactory;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import dagger.ObjectGraph;
import java.util.List;
import javax.inject.Inject;

public class PeoplePresenter implements Presenter, CommunicationPresenter {

    private final JobManager jobManager;
    private final Bus bus;
    private final ErrorMessageFactory errorMessageFactory;

    private PeopleView peopleView;
    private ObjectGraph objectGraph;

    @Inject public PeoplePresenter(JobManager jobManager, Bus bus, ErrorMessageFactory errorMessageFactory) {
        this.jobManager = jobManager;
        this.bus = bus;
        this.errorMessageFactory = errorMessageFactory;
    }

    public void initialize(PeopleView peopleView, ObjectGraph objectGraph) {
        this.peopleView = peopleView;
        this.objectGraph = objectGraph;
        this.loadPeopleList();
    }

    public void loadPeopleList() {
        this.showViewLoading();
        this.hideViewEmpty();
        this.getPeopleList();
    }

    private void getPeopleList() {
        GetPeopleJob job = objectGraph.get(GetPeopleJob.class);
        jobManager.addJobInBackground(job);
    }

    @Subscribe
    public void onPeopleListLoaded(FollowsResultEvent event) {
        this.hideViewLoading();
        List<UserModel> people = event.getResult();
        if (people != null && !people.isEmpty()) {
            this.showPeopleListInView(people);
            this.hideViewEmpty();
        } else {
            this.showViewEmtpy();
        }
    }

    private void showPeopleListInView(List<UserModel> people) {
        peopleView.renderUserList(people);
    }

    private void showViewLoading() {
        peopleView.showLoading();
    }

    private void hideViewLoading() {
        peopleView.hideLoading();
    }

    private void showViewEmtpy() {
        peopleView.showEmpty();
    }

    private void hideViewEmpty() {
        peopleView.hideEmpty();
    }

    private void showErrorInView(String errorMessage) {
        peopleView.showError(errorMessage);
    }

    @Subscribe @Override
    public void onCommunicationError(CommunicationErrorEvent event) {
        this.hideViewLoading();
        this.showErrorInView(errorMessageFactory.getCommunicationErrorMessage());
    }

    @Subscribe @Override
    public void onConnectionNotAvailable(ConnectionNotAvailableEvent event) {
        this.hideViewLoading();
        this.showErrorInView(errorMessageFactory.getConnectionNotAvailableMessage());
    }

    @Override public void resume() {
        bus.register(this);
    }

    @Override public void pause() {
        bus.unregister(this);
    }
}
