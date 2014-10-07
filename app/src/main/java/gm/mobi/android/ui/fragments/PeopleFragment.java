package gm.mobi.android.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import com.squareup.otto.Subscribe;
import gm.mobi.android.GolesApplication;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.task.events.ConnectionNotAvailableEvent;
import gm.mobi.android.task.events.follows.FollowsResultEvent;
import gm.mobi.android.task.jobs.follows.GetPeopleJob;

public class PeopleFragment extends UserFollowsFragment {

    //TODO este fragment será independiente con diferentes vistas y comportamiento del UserFollowsFragment

    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        User currentUser = GolesApplication.get(getActivity()).getCurrentUser();
        userId = currentUser.getIdUser();
        followType = FOLLOWING;
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void startJob() {
        GetPeopleJob job = GolesApplication.get(getActivity()).getObjectGraph().get(GetPeopleJob.class);
        job.init();
        jobManager.addJobInBackground(job);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Subscribe @Override public void onConnectionNotAvailable(ConnectionNotAvailableEvent event) {
        super.onConnectionNotAvailable(event);
    }

    @Subscribe @Override public void showUserList(FollowsResultEvent event) {
        super.showUserList(event);
    }

}
