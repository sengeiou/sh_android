package com.shootr.android.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import com.shootr.android.R;
import com.shootr.android.ui.activities.FindFriendsActivity;
import com.shootr.android.ui.activities.ProfileContainerActivity;
import com.shootr.android.ui.adapters.recyclerview.FriendsAdapter;
import com.shootr.android.ui.base.BaseFragment;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.presenter.PeoplePresenter;
import com.shootr.android.ui.views.PeopleView;
import com.shootr.android.ui.views.nullview.NullPeopleView;
import com.shootr.android.util.PicassoWrapper;
import java.util.List;
import javax.inject.Inject;

public class PeopleFragment extends BaseFragment implements PeopleView{

    public static final int REQUEST_CAN_CHANGE_DATA = 1;
    @Inject PicassoWrapper picasso;
    @Inject PeoplePresenter presenter;

    @InjectView(R.id.userlist_list) ListView userlistListView;
    @InjectView(R.id.userlist_progress) ProgressBar progressBar;

    @InjectView(R.id.userlist_empty) TextView emptyTextView;
    private FriendsAdapter peopleAdapter;

    public static PeopleFragment newInstance() {
        return new PeopleFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        presenter.setView(this);
        presenter.initialize();
    }

    @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_userlist, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.inject(this, view);
        userlistListView.setAdapter(getPeopleAdapter());
        setEmptyMessageForPeople();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
        presenter.setView(new NullPeopleView());
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
        startActivityForResult(ProfileContainerActivity.getIntent(getActivity(), user.getIdUser()),
          REQUEST_CAN_CHANGE_DATA);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.people, menu);
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

    private void findFriends() {
        // TODO not going through the presenter? You naughty boy...
        startActivityForResult(new Intent(getActivity(), FindFriendsActivity.class), REQUEST_CAN_CHANGE_DATA);
        getActivity().overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
    }

    private void setEmptyMessageForPeople() {
        emptyTextView.setText(R.string.following_list_empty);
    }

    private FriendsAdapter getPeopleAdapter() {
        if (peopleAdapter == null) {
            peopleAdapter = new FriendsAdapter(getActivity(), picasso);
        }
        return peopleAdapter;
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        presenter.refresh();
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
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
