package gm.mobi.android.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.path.android.jobqueue.JobManager;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import gm.mobi.android.GolesApplication;
import gm.mobi.android.R;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.task.events.CommunicationErrorEvent;
import gm.mobi.android.task.events.ConnectionNotAvailableEvent;
import gm.mobi.android.task.events.follows.FollowsResultEvent;
import gm.mobi.android.task.jobs.follows.GetFollowingsJob;
import gm.mobi.android.ui.base.BaseFragment;
import javax.inject.Inject;
import timber.log.Timber;

public class InitialSetupFragment extends BaseFragment {

    @Inject JobManager jobManager;
    @Inject Bus bus;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_initial, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        performInitialSetup();
    }


    /**
     * The initial setup consists of:
     * - Downloading and storing the user's following, if any.
     */
    private void performInitialSetup() {
        User currentUser = ((GolesApplication)getActivity().getApplication()).getCurrentUser();
        startGetFollowingJob(getActivity(), currentUser);
    }

    private void startGetFollowingJob(Context context, User currentUser){
        GetFollowingsJob job = GolesApplication.get(context).getObjectGraph().get(GetFollowingsJob.class);
        job.init(currentUser);
        jobManager.addJobInBackground(job);
    }


    @Subscribe
    public void followingsReceived(FollowsResultEvent event) {
            setupFinished();
    }

    @Subscribe
    public void onCommunicationError(CommunicationErrorEvent event) {
        Timber.w("Setup invalid. Something went wrong...");
        //TODO retry
    }

    @Subscribe
    public void onConnectionNotAvailable(ConnectionNotAvailableEvent event) {
        Timber.w("Connection unavailable. Retry, maybe.");
        //TODO notificar al usuario y dar opción de retry
    }

    private void setupFinished() {
        bus.post(new InitialSetupCompletedEvent());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        bus.register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        bus.unregister(this);
    }

    public static class InitialSetupCompletedEvent {
    }
}
