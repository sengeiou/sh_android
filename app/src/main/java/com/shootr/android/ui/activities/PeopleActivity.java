package com.shootr.android.ui.activities;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import com.shootr.android.R;
import com.shootr.android.ui.NavigationDrawerDecorator;
import com.shootr.android.ui.adapters.PeopleAdapter;
import com.shootr.android.ui.adapters.UserListAdapter;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.presenter.PeoplePresenter;
import com.shootr.android.ui.views.PeopleView;
import com.shootr.android.util.PicassoWrapper;
import java.util.List;
import javax.inject.Inject;

public class PeopleActivity extends BaseNavDrawerToolbarActivity implements PeopleView {

    public static final int REQUEST_REFRESH_DATA_WHEN_RETURNS = 1;

    @Inject PicassoWrapper picasso;
    @Inject PeoplePresenter presenter;

    @InjectView(R.id.userlist_list) ListView userlistListView;
    @InjectView(R.id.userlist_progress) ProgressBar progressBar;

    @InjectView(R.id.userlist_empty) TextView emptyTextView;

    private UserListAdapter userListAdapter;

    @Override protected int getNavDrawerItemId() {
        return NavigationDrawerDecorator.NAVDRAWER_ITEM_PEOPLE;
    }

    @Override protected int getLayoutResource() {
        return R.layout.activity_userlist;
    }

    @Override protected void initializeViews() {
        ButterKnife.inject(this);
        userlistListView.setAdapter(getPeopleAdapter());
        setEmptyMessageForPeople();
    }

    @Override protected void initializePresenter() {
        presenter.initialize(this);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.people, menu);
        return true;
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

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        presenter.refresh();
    }

    @Override public void onResume() {
        super.onResume();
        presenter.resume();
    }

    @Override public void onPause() {
        super.onPause();
        presenter.pause();
    }

    @OnItemClick(R.id.userlist_list)
    public void openUserProfile(int position) {
        // TODO not going through the presenter? You naughty boy...
        UserModel user = getPeopleAdapter().getItem(position);
        startActivityForResult(ProfileContainerActivity.getIntent(this, user.getIdUser()),
          REQUEST_REFRESH_DATA_WHEN_RETURNS);
    }

    private void findFriends() {
        // TODO not going through the presenter? You naughty boy...
        startActivityForResult(new Intent(this, FindFriendsActivity.class), REQUEST_REFRESH_DATA_WHEN_RETURNS);
    }

    private void setEmptyMessageForPeople() {
        emptyTextView.setText(R.string.following_list_empty);
    }

    private UserListAdapter getPeopleAdapter() {
        if (userListAdapter == null) {
            userListAdapter = new PeopleAdapter(this, picasso);
        }
        return userListAdapter;
    }

    @Override public void renderUserList(List<UserModel> people) {
        userlistListView.setVisibility(View.VISIBLE);
        getPeopleAdapter().setItems(people);
        getPeopleAdapter().notifyDataSetChanged();
    }

    @Override public void showEmpty() {
        userlistListView.setVisibility(View.GONE);
        emptyTextView.setVisibility(View.VISIBLE);
    }

    @Override public void hideEmpty() {
        emptyTextView.setVisibility(View.GONE);
    }

    @Override public void showLoading() {
        userlistListView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override public void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    @Override public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
