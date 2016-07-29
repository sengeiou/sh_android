package com.shootr.mobile.ui.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.ToolbarDecorator;
import com.shootr.mobile.ui.adapters.UserListAdapter;
import com.shootr.mobile.ui.adapters.listeners.OnUserClickListener;
import com.shootr.mobile.ui.adapters.recyclerview.FriendsAdapter;
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

public class FriendsActivity extends BaseToolbarDecoratedActivity implements PeopleView,
    SuggestedPeopleView, UserListAdapter.FollowUnfollowAdapterCallback {

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
  @BindString(R.string.analytics_action_follow) String analyticsActionFollow;
  @BindString(R.string.analytics_label_follow) String analyticsLabelFollow;

  private FriendsAdapter peopleAdapter;
  private UserListAdapter suggestedPeopleAdapter;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ButterKnife.bind(this);
    analyticsTool.analyticsStart(this, analyticsScreenFriends);
    presenter.setView(this);
    presenter.initialize();
    suggestedPeoplePresenter.initialize(this);
    setEmptyMessageForPeople();
    userlistListView.setAdapter(getPeopleAdapter());
  }

  @Override protected int getLayoutResource() {
    return R.layout.activity_friends;
  }

  @Override protected void initializeViews(Bundle savedInstanceState) {

  }

  @Override protected void initializePresenter() {

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

    analyticsTool.analyticsStop(this, this);
    ButterKnife.unbind(this);
    presenter.setView(new NullPeopleView());
    suggestedPeoplePresenter.setView(new NullSuggestedPeopleView());
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
    startActivityForResult(ProfileActivity.getIntent(this, idUser),
        REQUEST_CAN_CHANGE_DATA);
    this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
  }

  public void onInviteFriendClick() {
    Intent intent = new Intent();
    intent.setAction(Intent.ACTION_SEND);
    intent.putExtra(Intent.EXTRA_TEXT, this.getString(R.string.invite_friends_message));
    intent.setType("text/plain");
    startActivity(
        Intent.createChooser(intent, this.getString(R.string.invite_friends_title)));
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
      case android.R.id.home:
        finish();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  private void findFriends() {
    // TODO not going through the presenter? You naughty boy...
    startActivityForResult(new Intent(this, FindFriendsActivity.class),
        REQUEST_CAN_CHANGE_DATA);
    this.overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
  }

  private void setEmptyMessageForPeople() {
    emptyTextView.setText(R.string.following_list_empty);
  }

  private FriendsAdapter getPeopleAdapter() {
    if (peopleAdapter == null) {
      suggestedPeopleAdapter = getSuggestedPeopleAdapter();
      peopleAdapter = new FriendsAdapter(this, imageLoader, suggestedPeopleAdapter,
          new OnUserClickListener() {
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
    analyticsTool.analyticsSendAction(this, analyticsActionFollow, analyticsLabelFollow);
  }

  @Override public void unFollow(final int position) {
    final UserModel userModel = getSuggestedPeopleAdapter().getItem(position);
    new AlertDialog.Builder(this).setMessage(
        String.format(getString(R.string.unfollow_dialog_message), userModel.getUsername()))
        .setPositiveButton(getString(R.string.unfollow_dialog_yes),
            new DialogInterface.OnClickListener() {
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
      suggestedPeopleAdapter = new UserListAdapter(this, imageLoader);
      suggestedPeopleAdapter.setCallback(this);
    }
    return suggestedPeopleAdapter;
  }

  public void scrollListToTop() {
    if (userlistListView != null) {
      userlistListView.setSelection(0);
    }
  }

  @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {

  }
}