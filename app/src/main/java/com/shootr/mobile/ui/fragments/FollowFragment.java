package com.shootr.mobile.ui.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
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
import com.shootr.mobile.ui.activities.StreamTimelineActivity;
import com.shootr.mobile.ui.adapters.SearchAdapter;
import com.shootr.mobile.ui.adapters.listeners.FavoriteClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnFollowUnfollowListener;
import com.shootr.mobile.ui.adapters.listeners.OnSearchStreamClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnUserClickListener;
import com.shootr.mobile.ui.base.BaseFragment;
import com.shootr.mobile.ui.model.FollowModel;
import com.shootr.mobile.ui.model.StreamModel;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.presenter.FollowPresenter;
import com.shootr.mobile.ui.views.FollowView;
import com.shootr.mobile.util.AnalyticsTool;
import com.shootr.mobile.util.FeedbackMessage;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.InitialsLoader;
import com.shootr.mobile.util.NumberFormatUtil;
import com.shootr.mobile.util.ShareManager;
import javax.inject.Inject;
import timber.log.Timber;

public class FollowFragment extends BaseFragment
    implements FollowView {

  private static final String ARGUMENT_FOLLOW_TYPE = "followtype";
  private static final String ARGUMENT_USER_ID = "userId";
  public static final String TAG = "follows";

  private SearchAdapter adapter;

  @BindView(R.id.userlist_list) RecyclerView userList;
  @BindView(R.id.userlist_progress) ProgressBar progressBar;
  @BindView(R.id.userlist_empty) TextView emptyTextView;
  @BindString(R.string.analytics_screen_user_follower) String analyticsScreenUserFollower;
  @BindString(R.string.analytics_screen_user_following) String analyticsScreenUserFollowing;
  @BindString(R.string.analytics_action_follow) String analyticsActionFollow;
  @BindString(R.string.analytics_label_follow) String analyticsLabelFollow;
  @BindString(R.string.analytics_source_followers) String followsSource;
  @BindString(R.string.analytics_source_following) String followingsSource;
  @BindString(R.string.analytics_action_favorite_stream) String analyticsActionFavoriteStream;
  @BindString(R.string.analytics_label_favorite_stream) String analyticsLabelFavoriteStream;
  @BindString(R.string.follower_list_empty) String noFollowersSource;
  @BindString(R.string.following_list_empty) String noFollowingsSource;

  @Inject FollowPresenter followPresenter;
  @Inject FeedbackMessage feedbackMessage;
  @Inject ShareManager shareManager;
  @Inject InitialsLoader initialsLoader;
  @Inject ImageLoader imageLoader;
  @Inject NumberFormatUtil numberFormatUtil;
  @Inject SessionRepository sessionRepository;
  @Inject AnalyticsTool analyticsTool;

  private String idUser;
  private int followType;
  private LinearLayoutManager layoutManager;

  public static FollowFragment newInstance(String userId, Integer followType) {
    FollowFragment fragment = new FollowFragment();
    fragment.setArguments(createArguments(userId, followType));
    return fragment;
  }

  public static Bundle createArguments(String userId, Integer followType) {
    Bundle bundle = new Bundle();
    bundle.putString(ARGUMENT_USER_ID, userId);
    bundle.putInt(ARGUMENT_FOLLOW_TYPE, followType);
    return bundle;
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.activity_userlist, container, false);
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    injectArguments();
    setHasOptionsMenu(true);
  }

  public void injectArguments() {
    Bundle arguments = getArguments();
    if (arguments != null) {
      idUser = arguments.getString(ARGUMENT_USER_ID);
      followType = arguments.getInt(ARGUMENT_FOLLOW_TYPE);
    } else {
      Timber.w("UserFollowsFragment has null arguments, which might cause a NullPointerException");
    }
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    ButterKnife.bind(this, getView());
    userList.setLayoutManager(new LinearLayoutManager(getContext()));
    setupViews();
    followPresenter.initialize(this, idUser, followType);
    userList.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
      }

      @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if (userList != null) {
          checkIfEndOfListVisible();
        }
      }
    });
  }

  private void checkIfEndOfListVisible() {
    int lastItemPosition = userList.getAdapter().getItemCount() - 1;
    int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();
    if (lastItemPosition == lastVisiblePosition && lastItemPosition >= 0) {
      followPresenter.showingLastShot();
    }
  }

  private void initializeStreamListAdapter() {

    adapter = new SearchAdapter(imageLoader, numberFormatUtil, initialsLoader, new OnFollowUnfollowListener() {
      @Override public void onFollow(UserModel user) {
        followPresenter.followUser(user);
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
        followPresenter.selectStream(stream);
        navigateToStreamTimeline(stream.getIdStream(), stream.getTitle());
      }

      @Override public void onStreamLongClick(StreamModel stream) {
        /* no-op */
      }
    }, new FavoriteClickListener() {
      @Override public void onFavoriteClick(StreamModel stream) {
        followPresenter.addToFavorites(stream);
        adapter.markFavorite(stream);
        sendFavoriteAnalytics(stream);
      }

      @Override public void onRemoveFavoriteClick(StreamModel stream) {
        setupRemoveFromFavoriteDialog(stream);
      }
    });

    userList.setAdapter(adapter);
  }

  private void navigateToStreamTimeline(String idStream, String streamTitle) {
    startActivity(StreamTimelineActivity.newIntent(getContext(), idStream, streamTitle));
  }

  private void unfollowUser(UserModel user) {
    followPresenter.unfollowUser(user);
    adapter.unfollowUser(user);
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

  private void sendFavoriteAnalytics(StreamModel stream) {
    AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
    builder.setContext(getContext());
    builder.setActionId(analyticsActionFavoriteStream);
    builder.setLabelId(analyticsLabelFavoriteStream);
    if (followType == 0) {
      builder.setSource(followsSource);
    } else {
      builder.setSource(followingsSource);
    }
    builder.setUser(sessionRepository.getCurrentUser());
    builder.setStreamName(stream.getTitle());
    builder.setIdStream(stream.getIdStream());
    analyticsTool.analyticsSendAction(builder);
    analyticsTool.appsFlyerSendAction(builder);
  }

  private void sendAnalytics(UserModel user) {
    AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
    builder.setContext(getContext());
    builder.setActionId(analyticsActionFollow);
    builder.setLabelId(analyticsLabelFollow);
    if (followType == 0) {
      builder.setSource(followsSource);
    } else {
      builder.setSource(followingsSource);
    }
    builder.setUser(sessionRepository.getCurrentUser());
    builder.setIdTargetUser(user.getIdUser());
    builder.setTargetUsername(user.getUsername());
    analyticsTool.analyticsSendAction(builder);
  }

  @Override public void showContent() {
    /* no-op */
  }

  @Override public void hideContent() {
    /* no-op */
  }

  @Override public void showFollow(UserModel userModel) {
    /* no-op */
  }

  @Override public void showUnfollow(UserModel userModel) {
    /* no-op */
  }

  @Override public void showError(String errorMessage) {
    feedbackMessage.show(getView(), errorMessage);
  }

  @Override public void showNoFollowing() {
    progressBar.setVisibility(View.GONE);
    emptyTextView.setVisibility(View.VISIBLE);
    emptyTextView.setText(noFollowersSource);
  }

  @Override public void showProgressView() {

  }

  @Override public void hideProgressView() {

  }

  @Override public void registerAnalytics(boolean followers) {

  }

  @Override public void renderItems(FollowModel followings) {
    userList.setVisibility(View.VISIBLE);
    progressBar.setVisibility(View.GONE);
    adapter.setItems(followings.getData());
    adapter.notifyDataSetChanged();
  }

  @Override public void renderMoreItems(FollowModel followings) {
    adapter.addItems(followings.getData());
  }

  private void setupRemoveFromFavoriteDialog(final StreamModel streamModel) {
    Spanned text;

    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
      text = Html.fromHtml(
          String.format(getString(R.string.remove_from_favorites_dialog), streamModel.getTitle()),
          Html.FROM_HTML_MODE_LEGACY);
    } else {
      text = Html.fromHtml(
          String.format(getString(R.string.remove_from_favorites_dialog), streamModel.getTitle()));
    }

    new AlertDialog.Builder(getContext()).setMessage(text)
        .setPositiveButton(getString(R.string.remove_favorite),
            new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                followPresenter.removeFromFavorites(streamModel);
                adapter.unmarkFavorite(streamModel);
              }
            })
        .setNegativeButton(getString(R.string.cancel), null)
        .create()
        .show();
  }

  private void setupViews() {
    layoutManager = new LinearLayoutManager(getContext());
    userList.setLayoutManager(layoutManager);
    initializeStreamListAdapter();
  }

  @Override public void onResume() {
    super.onResume();
    followPresenter.resume();
  }

  @Override public void onPause() {
    super.onPause();
    followPresenter.pause();
  }
}
