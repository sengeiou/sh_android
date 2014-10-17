package gm.mobi.android.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.squareup.otto.Subscribe;
import gm.mobi.android.GolesApplication;
import gm.mobi.android.R;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.task.events.CommunicationErrorEvent;
import gm.mobi.android.task.events.ConnectionNotAvailableEvent;
import gm.mobi.android.task.events.follows.FollowsResultEvent;
import gm.mobi.android.task.jobs.follows.GetPeopleJob;
import gm.mobi.android.ui.activities.FindFriendsActivity;
import gm.mobi.android.ui.adapters.PeopleAdapter;
import gm.mobi.android.ui.adapters.UserListAdapter;

public class PeopleFragment extends UserFollowsFragment {

    private PeopleAdapter peopleAdapter;

    //TODO este fragment será independiente con diferentes vistas y comportamiento del UserFollowsFragment

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        User currentUser = GolesApplication.get(getActivity()).getCurrentUser();
        userId = currentUser.getIdUser();
        followType = FOLLOWING;
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void startJob() {
        GolesApplication golesApplication = GolesApplication.get(getActivity());
        GetPeopleJob job = golesApplication.getObjectGraph().get(GetPeopleJob.class);
        job.init(golesApplication.getCurrentUser().getIdUser());
        jobManager.addJobInBackground(job);
    }

    @Override
    public void onResume() {
        super.onResume();
        startJob();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void findFriends() {
        startActivity(new Intent(getActivity(), FindFriendsActivity.class));
    }

    @Subscribe @Override
    public void showUserList(FollowsResultEvent event) {
        super.showUserList(event);
    }

    @Override public UserListAdapter getAdapter() {
        if (peopleAdapter == null) {
            peopleAdapter = new PeopleAdapter(getActivity(), picasso);
        }
        return peopleAdapter;
    }

    @Subscribe @Override
    public void onConnectionNotAvailable(ConnectionNotAvailableEvent event) {
        super.onConnectionNotAvailable(event);
    }

    @Subscribe @Override
    public void onCommunicationError(CommunicationErrorEvent event) {
        super.onCommunicationError(event);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.people, menu);
        // Little hack for ActionBarCompat
        menu.findItem(R.id.menu_search).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                findFriends();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
