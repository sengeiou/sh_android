package gm.mobi.android.ui.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import com.path.android.jobqueue.JobManager;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import gm.mobi.android.GolesApplication;
import gm.mobi.android.R;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.task.events.follows.SearchPeopleEvent;
import gm.mobi.android.task.jobs.follows.SearchPeopleJob;
import gm.mobi.android.ui.adapters.UserListAdapter;
import gm.mobi.android.ui.base.BaseSignedInActivity;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class FindFriendsActivity extends BaseSignedInActivity {

    @Inject Picasso picasso;
    @Inject JobManager jobManager;
    @Inject Bus bus;

    private SearchView searchView;

    @InjectView(R.id.search_results_list) ListView resultsList;

    private UserListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!restoreSessionOrLogin()) return;

        setContainerContent(R.layout.activity_search);
        ButterKnife.inject(this);

        setupActionBar();
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
    }

    @Override protected void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String queryText) {
                startSearch(queryText);
                return true;
            }

            @Override public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        searchView.setQueryHint(getString(R.string.activity_find_friends_hint));
        searchView.setIconifiedByDefault(false);
        searchView.setIconified(false);
        return true;
    }

    public void startSearch(String query) {
        Timber.d("Searching \"%s\"", query);
        setLoading(true);
        setEmpty(false);
        startJob(query);
    }

    public void startJob(String searchString) {
        SearchPeopleJob job = GolesApplication.get(getApplicationContext()).getObjectGraph().get(SearchPeopleJob.class);
        job.init(searchString);
        jobManager.addJobInBackground(job);
    }

    @Subscribe
    public void getSearchResult(SearchPeopleEvent event) {
        setLoading(false);
        List<User> results = event.getSearchUsers();
        if (results != null && results.size() > 0) {
            setListContent(results);
        } else {
            setEmpty(true);
        }
    }

    private void setListContent(List<User> users) {
        if (adapter == null) {
            adapter = new UserListAdapter(this, picasso, users);
            resultsList.setAdapter(adapter);
        } else {
            adapter.setItems(users);
        }
    }

    private void setLoading(boolean loading) {
        //TODO
    }

    private void setEmpty(boolean empty) {
        //TODO
        Toast.makeText(this, "Devuelve vacío", Toast.LENGTH_LONG).show();
    }
}
