package gm.mobi.android.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.path.android.jobqueue.JobManager;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import gm.mobi.android.GolesApplication;
import gm.mobi.android.R;
import gm.mobi.android.task.events.ConnectionNotAvailableEvent;
import gm.mobi.android.task.events.follows.FollowsResultEvent;
import gm.mobi.android.task.jobs.follows.GetFollowingsJob;
import gm.mobi.android.ui.base.BaseFragment;
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
        jobManager.addJobInBackground(new GetFollowingsJob(getActivity(), GolesApplication.get(getActivity()).getCurrentUser()));
    }

    @Subscribe
    public void followingsReceived(FollowsResultEvent event) {
        // Did it go ok?
        if (event.getStatus() == FollowsResultEvent.STATUS_SUCCESS) {
            // Yey!
            setupFinished();
        } else {
            Timber.w("Setup invalid. Something went wrong...");
            //TODO notificar al usuario y dar opción de retry
        }
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

/*
    @Subscribe
    public void oçnGetFollowingResult(FollowsResultEvent event) {
        mFollowingList = event.getFollows();
        List<Integer> followingIds = event.getFollowingIds();
        if (event.getStatus() == FollowsResultEvent.STATUS_SUCCESS && mFollowingList != null) {
            //Aquí llamamos al siguiente Job, que será obtener los users objects de los followings que hemos retornado
            jobManager.addJobInBackground(new Use GetUsersJobApplicationContext(), followingIds, db));
        }

    }

    @Subscribe
    public void onGetUsersFollowingResult(UsersResultEvent event){
        mFollowingUserList = event.getUsers();
        List<Integer> followingIds = event.getFollowingUserIds();
        if(event.getStatus() == UsersResultEvent.STATUS_SUCCESS && mFollowingUserList != null){
            //Aquí llamamos a obtener los shots
            jobManager.addJobInBackground(new ShotsJob(getApplicationContext(), db));
        }
    }

    @Subscribe
    public void onGetShotsResult(ShotsResultEvent event){
        mShotList = event.getAllShots();
        if(event.getStatus() == ShotsResultEvent.STATUS_SUCCESS && mShotList!=null){
            //Aquí deberíamos pintar el fragment del timeline
            Toast.makeText(getApplicationContext(), "Ha descargado todos los shots", Toast.LENGTH_LONG).show();
            android.support.v4.app.FragmentTransaction ft =  getSupportFragmentManager().beginTransaction();
            ft.add(new TimelineFragment(),"TIME_LINE_FRAGMENT");
            ft.commit();
        }
    }*/

    public static class InitialSetupCompletedEvent {
    }
}
