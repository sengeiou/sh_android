package com.shootr.mobile.ui.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.shootr.mobile.R;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.ui.activities.ProfileActivity;
import com.shootr.mobile.ui.activities.SearchActivity;
import com.shootr.mobile.ui.adapters.SearchAdapter;
import com.shootr.mobile.ui.adapters.listeners.FavoriteClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnFollowUnfollowListener;
import com.shootr.mobile.ui.adapters.listeners.OnSearchStreamClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnUserClickListener;
import com.shootr.mobile.ui.base.BaseFragment;
import com.shootr.mobile.ui.model.SearchableModel;
import com.shootr.mobile.ui.model.StreamModel;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.presenter.SearchItemsPresenter;
import com.shootr.mobile.ui.views.SearchUserView;
import com.shootr.mobile.util.AnalyticsTool;
import com.shootr.mobile.util.FeedbackMessage;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.InitialsLoader;
import java.util.List;
import javax.inject.Inject;

public class FindFriendsFragment extends BaseFragment implements SearchUserView, SearchFragment {

  @Inject ImageLoader imageLoader;
  @Inject InitialsLoader initialsLoader;
  @Inject FeedbackMessage feedbackMessage;
  @Inject SearchItemsPresenter presenter;
  @Inject AnalyticsTool analyticsTool;
  @Inject SessionRepository sessionRepository;

  @BindView(R.id.find_friends_search_results_list) RecyclerView resultsListView;
  @BindView(R.id.find_friends_search_results_empty) TextView emptyOrErrorView;
  @BindView(R.id.userlist_progress) ProgressBar progressBar;
  @BindString(R.string.analytics_action_follow) String analyticsActionFollow;
  @BindString(R.string.analytics_label_follow) String analyticsLabelFollow;
  @BindString(R.string.analytics_source_discover_user_search) String discoverUserSearch;

  private SearchAdapter adapter;

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_find_friends, container, false);
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    ButterKnife.bind(this, getView());
    setupViews();
    presenter.initialize(this, null);
  }

  @Override public void hideKeyboard() {
    ((SearchActivity) getActivity()).hideKeyboard();
  }

  @Override public void showContent() {
    resultsListView.setVisibility(View.VISIBLE);
  }

  @Override public void hideContent() {
    resultsListView.setVisibility(View.GONE);
  }

  @Override public void showFollow(UserModel userModel) {

  }

  @Override public void showUnfollow(UserModel userModel) {

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

  @Override public void showError(String message) {
    feedbackMessage.show(getView(), message);
  }

  private void setupViews() {
    adapter = new SearchAdapter(imageLoader, initialsLoader, new OnFollowUnfollowListener() {
      @Override public void onFollow(UserModel user) {
        presenter.followUser(user);
        adapter.followUser(user);
        sendAnalytics(user);
      }

      @Override public void onUnfollow(UserModel user) {
        showUnfollowConfirmation(user);
      }
    }, new OnUserClickListener() {
      @Override public void onUserClick(String idUser) {
        startActivityForResult(ProfileActivity.getIntentFromSearch(getContext(), idUser), 666);
      }
    }, new OnSearchStreamClickListener() {
      @Override public void onStreamClick(StreamModel stream) {
        /* no-op */
      }

      @Override public void onStreamLongClick(StreamModel stream) {
        /* no-op */
      }
    }, new FavoriteClickListener() {
      @Override public void onFavoriteClick(StreamModel stream) {
        /* no-op */
      }

      @Override public void onRemoveFavoriteClick(StreamModel stream) {
        /* no-op */
      }
    });

    resultsListView.setLayoutManager(new LinearLayoutManager(getContext()));
    resultsListView.setAdapter(adapter);
  }

  private void unfollowUser(UserModel user) {
    presenter.unfollowUser(user);
    adapter.unfollowUser(user);
  }

  @Override public void renderSearchItems(List<SearchableModel> searchableModels) {
    adapter.setItems(searchableModels);
    adapter.notifyDataSetChanged();
  }

  private void showUnfollowConfirmation(final UserModel userModel) {
    new AlertDialog.Builder(getContext()).setMessage(
        String.format(getString(R.string.unfollow_dialog_message), userModel.getUsername()))
        .setPositiveButton(getString(R.string.unfollow_dialog_yes),
            new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                unfollowUser(userModel);
              }
            })
        .setNegativeButton(getString(R.string.unfollow_dialog_no), null)
        .create()
        .show();
  }

}
