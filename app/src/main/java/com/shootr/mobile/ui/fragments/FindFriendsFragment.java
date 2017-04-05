package com.shootr.mobile.ui.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import com.shootr.mobile.R;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.ui.activities.DiscoverSearchActivity;
import com.shootr.mobile.ui.activities.ProfileActivity;
import com.shootr.mobile.ui.adapters.UserListAdapter;
import com.shootr.mobile.ui.base.BaseSearchFragment;
import com.shootr.mobile.ui.model.SearchableModel;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.presenter.FindFriendsPresenter;
import com.shootr.mobile.ui.views.FindFriendsView;
import com.shootr.mobile.ui.widgets.ListViewScrollObserver;
import com.shootr.mobile.util.AnalyticsTool;
import com.shootr.mobile.util.FeedbackMessage;
import com.shootr.mobile.util.ImageLoader;
import java.util.List;
import javax.inject.Inject;

public class FindFriendsFragment extends BaseSearchFragment
    implements FindFriendsView, UserListAdapter.FollowUnfollowAdapterCallback, SearchFragment {

  @Inject ImageLoader imageLoader;
  @Inject FeedbackMessage feedbackMessage;
  @Inject FindFriendsPresenter findFriendsPresenter;
  @Inject AnalyticsTool analyticsTool;
  @Inject SessionRepository sessionRepository;

  @BindView(R.id.find_friends_search_results_list) ListView resultsListView;
  @BindView(R.id.find_friends_search_results_empty) TextView emptyOrErrorView;
  @BindView(R.id.userlist_progress) ProgressBar progressBar;
  @BindString(R.string.analytics_action_follow) String analyticsActionFollow;
  @BindString(R.string.analytics_label_follow) String analyticsLabelFollow;
  @BindString(R.string.analytics_source_discover_user_search) String discoverUserSearch;

  private View progressViewContent;
  private View progressView;
  private UserListAdapter adapter;
  private boolean hasMoreItemsToLoad;

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_find_friends, container, false);
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    ButterKnife.bind(this, getView());
    setupViews();
    findFriendsPresenter.initialize(this, false);
  }

  @Override public void renderFriends(List<UserModel> friends) {
    adapter.setItems(friends);
    adapter.notifyDataSetChanged();
  }

  @Override public void setCurrentQuery(String query) {
  }

  @Override public void hideKeyboard() {
    ((DiscoverSearchActivity) getActivity()).hideKeyboard();
  }

  @Override public void showContent() {
    resultsListView.setVisibility(View.VISIBLE);
  }

  @Override public void hideContent() {
    resultsListView.setVisibility(View.GONE);
  }

  @Override public void setHasMoreItemsToLoad(Boolean hasMoreItems) {
    this.hasMoreItemsToLoad = hasMoreItems;
  }

  @Override public void addFriends(List<UserModel> friends) {
    adapter.addItems(friends);
    adapter.notifyDataSetChanged();
  }

  @Override public void hideProgress() {
    setLoading(false);
    progressView.setVisibility(View.GONE);
  }

  @Override public void restoreState() {
    findFriendsPresenter.initialize(this, true);
  }

  @Override public void follow(int position) {
    UserModel user = adapter.getItem(position);
    findFriendsPresenter.followUser(user);
    sendAnalytics(user);
  }

  private void sendAnalytics(UserModel user) {
    AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
    builder.setContext(getContext());
    builder.setActionId(analyticsActionFollow);
    builder.setLabelId(analyticsLabelFollow);
    builder.setSource(discoverUserSearch);
    builder.setUser(sessionRepository.getCurrentUser());
    builder.setIdTargetUser(user.getIdUser());
    builder.setTargetUsername(user.getUsername());
    analyticsTool.analyticsSendAction(builder);
  }

  @Override public void unFollow(int position) {
    final UserModel userModel = adapter.getItem(position);
    new AlertDialog.Builder(getContext()).setMessage(String.format(getString(R.string.unfollow_dialog_message),
        userModel.getUsername()))
        .setPositiveButton(getString(R.string.unfollow_dialog_yes), new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
            findFriendsPresenter.unfollowUser(userModel);
          }
        })
        .setNegativeButton(getString(R.string.unfollow_dialog_no), null)
        .create()
        .show();
  }

  @Override public void showEmpty() {
    emptyOrErrorView.setVisibility(View.VISIBLE);
  }

  @Override public void hideEmpty() {
    emptyOrErrorView.setVisibility(View.GONE);
  }

  @Override public void showLoading() {
    progressBar.setVisibility(View.VISIBLE);
  }

  @Override public void hideLoading() {
    progressBar.setVisibility(View.GONE);
  }

  @Override public void showError(String message) {
    feedbackMessage.show(getView(), message);
  }

  private void setupViews() {
    progressView = getLoadingView();
    progressView.setVisibility(View.GONE);
    progressViewContent = ButterKnife.findById(progressView, R.id.loading_progress);
    resultsListView.addFooterView(progressView, null, false);

    if (adapter == null) {
      adapter = new UserListAdapter(getContext(), imageLoader);
      adapter.setCallback(this);
    }
    resultsListView.setAdapter(adapter);
    new ListViewScrollObserver(resultsListView)
        .setOnScrollUpAndDownListener(new ListViewScrollObserver.OnListViewScrollListener() {
          @Override public void onScrollUpDownChanged(int delta, int scrollPosition, boolean exact) {
                /* no-op */
          }

          @Override public void onScrollIdle() {
            int lastVisiblePosition = resultsListView.getLastVisiblePosition();
            int loadingFooterPosition = resultsListView.getAdapter().getCount() - 1;
            boolean shouldStartLoadingMore = lastVisiblePosition >= loadingFooterPosition;
            if (shouldStartLoadingMore && hasMoreItemsToLoad) {
              progressView.setVisibility(View.VISIBLE);
              findFriendsPresenter.makeNextRemoteSearch();
            }
            hideKeyboard();
          }
        });
  }

  private View getLoadingView() {
    return LayoutInflater.from(getContext()).inflate(R.layout.item_list_loading, resultsListView, false);
  }

  private void setLoading(boolean loading) {
    progressViewContent.setVisibility(loading ? View.VISIBLE : View.GONE);
  }

  @Override public void search(String query) {
    findFriendsPresenter.searchFriends(query);
  }

  @Override public void searchChanged(String query) {
    hideEmpty();
    findFriendsPresenter.queryTextChanged(query);
  }

  @OnItemClick(R.id.find_friends_search_results_list) public void openUserProfile(int position) {
    UserModel user = adapter.getItem(position);
    startActivityForResult(ProfileActivity.getIntent(getContext(), user.getIdUser()), 666);
  }

  @Override public void renderSearchItems(List<SearchableModel> searchableModels) {
    //TODO
  }
}
