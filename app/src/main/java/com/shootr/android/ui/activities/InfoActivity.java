package com.shootr.android.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import com.path.android.jobqueue.JobManager;
import com.shootr.android.util.PicassoWrapper;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.shootr.android.ShootrApplication;
import com.shootr.android.R;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.task.events.CommunicationErrorEvent;
import com.shootr.android.task.events.ConnectionNotAvailableEvent;
import com.shootr.android.task.events.info.WatchingInfoResult;
import com.shootr.android.task.jobs.info.GetWatchingInfoJob;
import com.shootr.android.ui.adapters.InfoListAdapter;
import com.shootr.android.ui.base.BaseSignedInActivity;
import com.shootr.android.ui.model.MatchModel;
import com.shootr.android.ui.model.UserWatchingModel;
import java.util.Collection;
import java.util.Map;
import javax.inject.Inject;
import timber.log.Timber;

@Deprecated
public class InfoActivity extends BaseSignedInActivity {

    private static final int REQUEST_CODE_EDIT = 1;
    private static final int REQUEST_CODE_ADD = 2;

    @Inject PicassoWrapper picasso;
    @Inject JobManager jobManager;
    @Inject Bus bus;
    @Inject SessionRepository sessionRepository;
    ActionBar actionBar;
    @InjectView(R.id.info_empty) LinearLayout emptyView;
    @InjectView(R.id.info_items_list) ListView listView;
    InfoListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!restoreSessionOrLogin()) {
            // Stop execution if there is no user logged in
            return;
        }

        setContainerContent(R.layout.activity_info);

        ButterKnife.inject(this);

        View.OnClickListener editInfoClickListener = new View.OnClickListener() {
            @Override public void onClick(View v) {
                //TODO demasiado acoplado al adapter y viewholder, buscar un patrón mejor
                int position = ((InfoListAdapter.UserViewHolder) v.getTag()).position;
                UserWatchingModel user = (UserWatchingModel) adapter.getItem(position);
                MatchModel matchCorrespondingToItem = adapter.getMatchCorrespondingToItem(position);

                editInfoForMatch(matchCorrespondingToItem, user);
            }
        };
        setupActionBar();

        adapter = new InfoListAdapter(this, picasso, sessionRepository.getCurrentUserId(), editInfoClickListener);

        listView.setAdapter(adapter);
        retrieveInfoList();
    }

    private void editInfoForMatch(MatchModel match, UserWatchingModel user) {
        Intent editIntent = EditInfoActivity.getIntent(this, match.getIdMatch(), user.isWatching(), match.getTitle(), user.getPlace());
        startActivityForResult(editIntent, REQUEST_CODE_EDIT);
    }

    @OnItemClick(R.id.info_items_list)
    public void onListItemClick(int position) {
        UserWatchingModel userWatchingModel = (UserWatchingModel) adapter.getItem(position);
        Long idUser = userWatchingModel.getIdUser();
        startActivity(ProfileContainerActivity.getIntent(this, idUser));
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            retrieveInfoList();
        }
    }

    private void setupActionBar() {
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    public void retrieveInfoList() {
        GetWatchingInfoJob job = ShootrApplication.get(this).getObjectGraph().get(GetWatchingInfoJob.class);
        job.init(false);
        jobManager.addJobInBackground(job);
    }

    @Subscribe
    public void receivedInfoList(WatchingInfoResult event) {
        Map<MatchModel, Collection<UserWatchingModel>> result = event.getResult();
        if (result != null && !result.isEmpty()) {
            adapter.setContent(result);
            setEmpty(false);
        } else {
            setEmpty(true);
        }
    }

    private void setEmpty(boolean empty) {
        emptyView.setVisibility(empty ? View.VISIBLE : View.GONE);
        listView.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    @Subscribe
    public void onConnectionNotAvailable(ConnectionNotAvailableEvent event) {
        Toast.makeText(this, R.string.connection_lost, Toast.LENGTH_SHORT).show();
        Timber.i("Connection not available");
    }

    @Subscribe
    public void onConnectionError(CommunicationErrorEvent event) {
        Toast.makeText(this, R.string.communication_error, Toast.LENGTH_SHORT).show();
    }

    @Override protected void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override protected void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }else if (item.getItemId() == R.id.menu_add) {
            startActivityForResult(new Intent(this, AddMatchActivity.class), REQUEST_CODE_ADD);
            return true;
        }else{
            return super.onOptionsItemSelected(item);
        }
    }
}
