package com.shootr.android.ui.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import com.shootr.android.R;
import com.shootr.android.ui.activities.FindFriendsActivity;
import com.shootr.android.ui.activities.ProfileContainerActivity;
import com.shootr.android.ui.adapters.UserListAdapter;
import com.shootr.android.ui.adapters.listeners.OnUserClickListener;
import com.shootr.android.ui.adapters.recyclerview.FriendsAdapter;
import com.shootr.android.ui.base.BaseFragment;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.presenter.PeoplePresenter;
import com.shootr.android.ui.presenter.SuggestedPeoplePresenter;
import com.shootr.android.ui.views.PeopleView;
import com.shootr.android.ui.views.SuggestedPeopleView;
import com.shootr.android.ui.views.nullview.NullPeopleView;
import com.shootr.android.ui.views.nullview.NullSuggestedPeopleView;
import com.shootr.android.util.FeedbackMessage;
import com.shootr.android.util.ImageLoader;
import java.util.List;
import javax.inject.Inject;

public class PeopleFragment extends BaseFragment implements PeopleView, SuggestedPeopleView, UserListAdapter.FollowUnfollowAdapterCallback {

    public static final int REQUEST_CAN_CHANGE_DATA = 1;
    @Inject ImageLoader imageLoader;
    @Inject PeoplePresenter presenter;
    @Inject SuggestedPeoplePresenter suggestedPeoplePresenter;
    @Inject FeedbackMessage feedbackMessage;

    @Bind(R.id.userlist_list) ListView userlistListView;
    @Bind(R.id.userlist_progress) ProgressBar progressBar;

    @Bind(R.id.userlist_empty) TextView emptyTextView;

    private FriendsAdapter peopleAdapter;
    private UserListAdapter suggestedPeopleAdapter;

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
        suggestedPeoplePresenter.initialize(this);
        setEmptyMessageForPeople();
        userlistListView.setAdapter(getPeopleAdapter());
    }

    @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_userlist, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        presenter.setView(new NullPeopleView());
        suggestedPeoplePresenter.setView(new NullSuggestedPeopleView());
    }

    @Override public void onResume() {
        super.onResume();
        presenter.resume();
        suggestedPeoplePresenter.resume();
    }

    @Override public void onPause() {
        super.onPause();
        presenter.pause();
        suggestedPeoplePresenter.pause();
    }

    @OnItemClick(R.id.userlist_list)
    public void onUserClick(int position) {
        if (position == 0) {
            onInviteFriendClick();
        } else {
            // TODO not going through the presenter? You naughty boy...
            UserModel user = getPeopleAdapter().getItem(position);
            openUserProfile(user.getIdUser());
        }
    }

    private void openUserProfile(String idUser) {
        startActivityForResult(ProfileContainerActivity.getIntent(getActivity(), idUser),
          REQUEST_CAN_CHANGE_DATA);
    }

    public void onInviteFriendClick() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, getActivity().getString(R.string.invite_friends_message));
        intent.setType("text/plain");
        startActivity(Intent.createChooser(intent, getActivity().getString(R.string.invite_friends_title)));
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
            suggestedPeopleAdapter = getSuggestedPeopleAdapter();
            peopleAdapter = new FriendsAdapter(getActivity(), imageLoader, suggestedPeopleAdapter, new OnUserClickListener() {
                @Override
                public void onUserClick(String idUser) {
                    openUserProfile(idUser);
                }
            });
        }
        return peopleAdapter;
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override public void renderUserList(List<UserModel> people) {
        getPeopleAdapter().setItems(people);
        getPeopleAdapter().notifyDataSetChanged();
    }

    @Override public void showPeopleList() {
        userlistListView.setVisibility(View.VISIBLE);
    }

    @Override public void showEmpty() {
        getPeopleAdapter().removeItems();
        getPeopleAdapter().notifyDataSetChanged();
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

    @Override public void renderSuggestedPeopleList(List<UserModel> users) {
        suggestedPeopleAdapter.setItems(users);
        suggestedPeopleAdapter.notifyDataSetChanged();
        getPeopleAdapter().notifyDataSetChanged();
    }

    @Override public void showError(String message) {
        feedbackMessage.show(getView(), message);
    }

    @Override public void refreshSuggestedPeople(List<UserModel> suggestedPeople) {
        getSuggestedPeopleAdapter().setItems(suggestedPeople);
        getSuggestedPeopleAdapter().notifyDataSetChanged();
    }

    @Override public void follow(int position) {
        suggestedPeoplePresenter.followUser(getSuggestedPeopleAdapter().getItem(position));
    }

    @Override public void unFollow(final int position) {
        final UserModel userModel = getSuggestedPeopleAdapter().getItem(position);
        new AlertDialog.Builder(getActivity()).setMessage("Unfollow "+userModel.getUsername() + "?")
          .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                  suggestedPeoplePresenter.unfollowUser(userModel);
              }
          })
          .setNegativeButton("No", null)
          .create()
          .show();
    }

    private UserListAdapter getSuggestedPeopleAdapter() {
        if (suggestedPeopleAdapter == null) {
            suggestedPeopleAdapter = new UserListAdapter(getActivity(), imageLoader);
            suggestedPeopleAdapter.setCallback(this);
        }
        return suggestedPeopleAdapter;
    }

}
