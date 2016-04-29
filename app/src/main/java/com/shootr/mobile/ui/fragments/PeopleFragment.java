package com.shootr.mobile.ui.fragments;

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
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.activities.FindFriendsActivity;
import com.shootr.mobile.ui.activities.ProfileContainerActivity;
import com.shootr.mobile.ui.adapters.UserListAdapter;
import com.shootr.mobile.ui.adapters.listeners.OnUserClickListener;
import com.shootr.mobile.ui.adapters.recyclerview.FriendsAdapter;
import com.shootr.mobile.ui.base.BaseFragment;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.presenter.PeoplePresenter;
import com.shootr.mobile.ui.presenter.SuggestedPeoplePresenter;
import com.shootr.mobile.ui.views.PeopleView;
import com.shootr.mobile.ui.views.SuggestedPeopleView;
import com.shootr.mobile.ui.views.nullview.NullPeopleView;
import com.shootr.mobile.ui.views.nullview.NullSuggestedPeopleView;
import com.shootr.mobile.util.AnalyticsTool;
import com.shootr.mobile.util.FeedbackMessage;
import com.shootr.mobile.util.ImageLoader;
import java.util.List;
import javax.inject.Inject;

public class PeopleFragment extends BaseFragment
  implements PeopleView, SuggestedPeopleView, UserListAdapter.FollowUnfollowAdapterCallback {

    public static final int REQUEST_CAN_CHANGE_DATA = 1;
    @Inject ImageLoader imageLoader;
    @Inject PeoplePresenter presenter;
    @Inject SuggestedPeoplePresenter suggestedPeoplePresenter;
    @Inject FeedbackMessage feedbackMessage;
    @Inject AnalyticsTool analyticsTool;

    @Bind(R.id.userlist_list) ListView userlistListView;
    @Bind(R.id.userlist_progress) ProgressBar progressBar;

    @Bind(R.id.userlist_empty) TextView emptyTextView;

    @BindString(R.string.analytics_screen_friends) String analyticsScreenFriends;

    private FriendsAdapter peopleAdapter;
    private UserListAdapter suggestedPeopleAdapter;

    public static PeopleFragment newInstance() {
        return new PeopleFragment();
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        analyticsTool.analyticsStart(getContext(), analyticsScreenFriends);
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

    @Override public void onDestroyView() {
        super.onDestroyView();
        analyticsTool.analyticsStop(getContext(), getActivity());
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

    @OnItemClick(R.id.userlist_list) public void onUserClick(int position) {
        if (position == 0) {
            onInviteFriendClick();
        } else {
            // TODO not going through the presenter? You naughty boy...
            UserModel user = getPeopleAdapter().getItem(position);
            openUserProfile(user.getIdUser());
        }
    }

    private void openUserProfile(String idUser) {
        startActivityForResult(ProfileContainerActivity.getIntent(getActivity(), idUser), REQUEST_CAN_CHANGE_DATA);
    }

    public void onInviteFriendClick() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, getActivity().getString(R.string.invite_friends_message));
        intent.setType("text/plain");
        startActivity(Intent.createChooser(intent, getActivity().getString(R.string.invite_friends_title)));
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
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
            peopleAdapter =
              new FriendsAdapter(getActivity(), imageLoader, suggestedPeopleAdapter, new OnUserClickListener() {
                  @Override public void onUserClick(String idUser) {
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
        new AlertDialog.Builder(getActivity()).setMessage(String.format(getString(R.string.unfollow_dialog_message),
          userModel.getUsername()))
          .setPositiveButton(getString(R.string.unfollow_dialog_yes), new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                  suggestedPeoplePresenter.unfollowUser(userModel);
              }
          })
          .setNegativeButton(getString(R.string.unfollow_dialog_no), null)
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

    public void scrollListToTop() {
        userlistListView.setSelection(0);
    }
}
