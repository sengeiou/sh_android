package com.shootr.android.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.shootr.android.data.SessionManager;
import com.squareup.otto.Subscribe;
import com.shootr.android.ShootrApplication;
import com.shootr.android.R;
import com.shootr.android.db.objects.UserEntity;
import com.shootr.android.task.events.CommunicationErrorEvent;
import com.shootr.android.task.events.ConnectionNotAvailableEvent;
import com.shootr.android.task.events.follows.FollowsResultEvent;
import com.shootr.android.task.jobs.follows.GetPeopleJob;
import com.shootr.android.ui.activities.FindFriendsActivity;
import com.shootr.android.ui.adapters.PeopleAdapter;
import com.shootr.android.ui.adapters.UserListAdapter;
import javax.inject.Inject;

public class PeopleFragment extends UserFollowsFragment {

    private PeopleAdapter peopleAdapter;
    @Inject SessionManager sessionManager;
    //TODO este fragment será independiente con diferentes vistas y comportamiento del UserFollowsFragment

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        UserEntity currentUser = ShootrApplication.get(getActivity()).getCurrentUser();
        userId = currentUser.getIdUser();
        followType = FOLLOWING;
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void startJob() {
        ShootrApplication shootrApplication = ShootrApplication.get(getActivity());
        GetPeopleJob job = shootrApplication.getObjectGraph().get(GetPeopleJob.class);
        job.init(sessionManager.getCurrentUserId());
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
