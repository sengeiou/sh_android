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
import gm.mobi.android.task.events.ConnectionNotAvailableEvent;
import gm.mobi.android.task.events.follows.FollowsResultEvent;
import gm.mobi.android.task.jobs.follows.GetPeopleJob;
import gm.mobi.android.ui.activities.FindFriendsActivity;

public class PeopleFragment extends UserFollowsFragment {

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

    public void findFriends() {
        startActivity(new Intent(getActivity(), FindFriendsActivity.class));
    }

    @Subscribe @Override
    public void onConnectionNotAvailable(ConnectionNotAvailableEvent event) {
        super.onConnectionNotAvailable(event);
    }

    @Subscribe @Override
    public void showUserList(FollowsResultEvent event) {
        super.showUserList(event);
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
