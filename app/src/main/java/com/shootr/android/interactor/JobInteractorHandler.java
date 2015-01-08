package com.shootr.android.interactor;

import com.path.android.jobqueue.JobManager;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.task.JobWrapper;
import com.shootr.android.task.NetworkConnection;
import com.squareup.otto.Bus;
import javax.inject.Inject;

public class JobInteractorHandler implements InteractorHandler {

    private final Bus bus;
    private final NetworkConnection networkConnection;
    private final JobManager jobManager;


    @Inject public JobInteractorHandler(Bus bus, NetworkConnection networkConnection, JobManager jobManager) {
        this.bus = bus;
        this.networkConnection = networkConnection;
        this.jobManager = jobManager;
    }

    @Override public void execute(Interactor interactor) {
        JobWrapper interactorJob = new JobWrapper(interactor, bus, networkConnection);
        jobManager.addJobInBackground(interactorJob);
    }

    @Override public void sendUiMessage(Object objectToUi) {
        bus.post(objectToUi);
    }
}
