package gm.mobi.android.ui.activities;

import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import com.squareup.picasso.Picasso;
import gm.mobi.android.R;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.ui.adapters.UserListAdapter;
import gm.mobi.android.ui.base.BaseSignedInActivity;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class FindFriendsActivity extends BaseSignedInActivity {

    @Inject Picasso picasso;

    private SearchView searchView;

    @InjectView(R.id.search_text) EditText searchText;
    @InjectView(R.id.search_results_list) ListView resultsList;
    @InjectView(R.id.search_button) Button searchButton;

    private UserListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!restoreSessionOrLogin()) return;

        setContainerContent(R.layout.activity_search);
        ButterKnife.inject(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*getMenuInflater().inflate(R.menu.search, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        searchView = (SearchView) searchItem.getActionView();

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Timber.d("Click!");
            }
        });
        searchView.setQueryHint("Hint, dude");
        searchView.setIconifiedByDefault(false);
        searchView.setIconified(false);*/
        return true;
    }

    @OnClick(R.id.search_button)
    public void searchButton() {
        startSearch(searchText.getText().toString());
    }

    @OnEditorAction(R.id.search_text)
    public boolean onSearchPressed() {
        startSearch(searchText.getText().toString());
        return true;
    }

    public void startSearch(String query) {
        setLoading(true);
        setEmpty(false);
        //TODO lanzar job de búsqueda
        Timber.d("Searching \"%s\", dude", query);
    }

    //TODO llamar desde evento de resultado
    public void showResults(List<User> results) {
        setLoading(false);
        if (results.size() > 0) {
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
    }
}
