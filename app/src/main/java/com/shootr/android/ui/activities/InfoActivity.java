package com.shootr.android.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import com.path.android.jobqueue.JobManager;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.shootr.android.ShootrApplication;
import com.shootr.android.R;
import com.shootr.android.data.SessionManager;
import com.shootr.android.task.events.CommunicationErrorEvent;
import com.shootr.android.task.events.ConnectionNotAvailableEvent;
import com.shootr.android.task.events.info.WatchingInfoResult;
import com.shootr.android.task.jobs.info.GetWatchingInfoJob;
import com.shootr.android.ui.adapters.InfoListAdapter;
import com.shootr.android.ui.base.BaseSignedInActivity;
import com.shootr.android.ui.model.MatchModel;
import com.shootr.android.ui.model.UserWatchingModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import timber.log.Timber;

public class InfoActivity extends BaseSignedInActivity {

    @Inject Picasso picasso;
    @Inject JobManager jobManager;
    @Inject Bus bus;
    @Inject SessionManager sessionManager;

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
        setupActionBar();
        ButterKnife.inject(this);

        View.OnClickListener editInfoClickListener = new View.OnClickListener() {
            @Override public void onClick(View v) {
                //TODO demasiado acoplado al adapter y viewholder, buscar un patrón mejor
                int position = ((InfoListAdapter.UserViewHolder) v.getTag()).position;
                MatchModel matchCorrespondingToItem = adapter.getMatchCorrespondingToItem(position);
                editInfoForMatch(matchCorrespondingToItem.getIdMatch());
            }
        };

        adapter = new InfoListAdapter(this, picasso, sessionManager.getCurrentUserId(), editInfoClickListener);

        listView.setAdapter(adapter);
        retrieveInfoList();
    }

    private void editInfoForMatch(Long idMatch) {
        startActivity(new Intent(this, EditInfoActivity.class));
    }

    @OnItemClick(R.id.info_items_list)
    public void onListItemClick(int position) {
        UserWatchingModel userWatchingModel = (UserWatchingModel)adapter.getItem(position);
        Long idUser = userWatchingModel.getIdUser();
        startActivity(ProfileContainerActivity.getIntent(this, idUser));
    }

    private Map<MatchModel, List<UserWatchingModel>> getDummyInfo() {
        ArrayMap<MatchModel, List<UserWatchingModel>> resMap = new ArrayMap<>();

        MatchModel match = new MatchModel();
        match.setTitle("Atlético-Barcelona");
        MatchModel match2 = new MatchModel();
        match2.setTitle("Sevilla-Manchester City");
        MatchModel match3 = new MatchModel();
        match3.setTitle("R.Madrid-La Palma del Condado");

        UserWatchingModel user = new UserWatchingModel();
        user.setUserName("Dummy");
        user.setStatus("Watching");
        user.setPhoto("http://www.pak101.com/funnypictures/Animals/2012/8/2/the_monopoly_cat_vbgkd_Pak101(dot)com.jpg");

        UserWatchingModel me = new UserWatchingModel();
        me.setIdUser(sessionManager.getCurrentUserId());
        me.setUserName("rafa");
        me.setStatus("Not watching");
        me.setPhoto("http://img1.wikia.nocookie.net/__cb20110606042636/es.futurama/images/c/c6/Futurama_fry_looking_squint2.jpg");

        List<UserWatchingModel> userList = new ArrayList<>();
        userList.add(me);
        userList.add(user);
        userList.add(user);

        resMap.put(match, userList);
        resMap.put(match2, userList);
        resMap.put(match3, userList);
        return resMap;
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    public void retrieveInfoList(){
        GetWatchingInfoJob job = ShootrApplication.get(this).getObjectGraph().get(GetWatchingInfoJob.class);
        job.init(false);
        jobManager.addJobInBackground(job);
    }

    @Subscribe
    public void receivedInfoList(WatchingInfoResult event){
        Map<MatchModel, Collection<UserWatchingModel>> result = event.getResult();
        if(result!=null && result.size()>0){
            adapter.setContent(result);
            setEmpty(false);
        }else{
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
