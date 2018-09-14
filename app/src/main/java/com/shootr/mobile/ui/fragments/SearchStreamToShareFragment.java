package com.shootr.mobile.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.shootr.mobile.R;
import com.shootr.mobile.domain.dagger.TemporaryFilesDir;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.ui.activities.PostNewShotActivity;
import com.shootr.mobile.ui.activities.StreamTimelineActivity;
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
import com.shootr.mobile.ui.views.SearchStreamView;
import com.shootr.mobile.util.FeedbackMessage;
import com.shootr.mobile.util.FileChooserUtils;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.InitialsLoader;
import com.shootr.mobile.util.NumberFormatUtil;
import java.io.File;
import java.util.List;
import javax.inject.Inject;

public class SearchStreamToShareFragment extends BaseFragment
    implements SearchStreamView, SearchFragment {

  private SearchAdapter adapter;

  @BindView(R.id.find_streams_list) RecyclerView streamsList;
  @BindView(R.id.find_streams_empty) View emptyView;
  @BindString(R.string.analytics_action_favorite_stream) String analyticsActionFavoriteStream;
  @BindString(R.string.analytics_label_favorite_stream) String analyticsLabelFavoriteStream;

  @Inject SearchItemsPresenter searchItemsPresenter;
  @Inject FeedbackMessage feedbackMessage;
  @Inject InitialsLoader initialsLoader;
  @Inject ImageLoader imageLoader;
  @Inject NumberFormatUtil numberFormatUtil;
  @Inject SessionRepository sessionRepository;
  @Inject @TemporaryFilesDir File tmpFiles;

  private File imageFileToSend;

  private String currentStreamTitle;
  private String currentIdStream;
  private String textToSend;

  public static SearchStreamToShareFragment newInstance(Bundle fragmentArguments) {
    SearchStreamToShareFragment fragment = new SearchStreamToShareFragment();
    fragment.setArguments(fragmentArguments);
    return fragment;
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_find_streams, container, false);
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    ButterKnife.bind(this, getView());
    streamsList.setLayoutManager(new LinearLayoutManager(getContext()));
    setupViews();
    setupImageFile();
    String sharedText = getArguments().getString(Intent.EXTRA_TEXT);
    if (sharedText != null) {
      textToSend = sharedText;
    }
    searchItemsPresenter.initialize(null, this);
  }

  private void setupImageFile() {
    Uri imageUri = (Uri) getArguments().getParcelable(Intent.EXTRA_STREAM);
    if (imageUri != null) {
      File file = FileChooserUtils.getFileFromUri(getContext(), imageUri, tmpFiles);
      if (file != null) {
        imageFileToSend = file;
      }
    }
  }

  private void initializeStreamListAdapter() {

    adapter = new SearchAdapter(imageLoader, numberFormatUtil, initialsLoader,
        new OnFollowUnfollowListener() {
          @Override public void onFollow(UserModel user) {
        /* no-op */
          }

          @Override public void onUnfollow(UserModel user) {
        /* no-op */
          }
        }, new OnUserClickListener() {
      @Override public void onUserClick(String idUser) {
        /* no-op */
      }
    }, new OnSearchStreamClickListener() {
      @Override public void onStreamClick(StreamModel stream) {
        navigateToStreamTimeline(stream.getIdStream(), stream.getTitle());
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

    streamsList.setAdapter(adapter);
    streamsList.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        hideKeyboard();
      }
    });
  }

  @Override public void hideKeyboard() {
    final InputMethodManager imm =
        (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
  }

  @Override public void showError(String errorMessage) {
    feedbackMessage.show(getView(), errorMessage);
  }

  @Override
  public void navigateToStreamTimeline(String idStream, String streamTitle) {
    currentIdStream = idStream;
    currentStreamTitle = streamTitle;
    Intent newShotIntent = PostNewShotActivity.IntentBuilder //
        .from(getActivity()) //
        .withImage(imageFileToSend)
        .withText(textToSend)
        .setStreamData(idStream, streamTitle)
        .openTimelineAfterSendShot()
        .build();
    getActivity().startActivity(newShotIntent);
    getActivity().finish();
  }

  @Override public void showAddedToFavorites(StreamModel streamModel) {
    /* no-op */
  }

  @Override public void showRemovedFromFavorites(StreamModel streamModel) {
    /* no-op */
  }

  @Override public void showStreamShared() {

  }

  @Override public void openContextualMenuWithAddFavorite(final StreamModel stream) {
    /* no-op */
  }

  @Override public void openContextualMenuWithUnmarkFavorite(final StreamModel stream) {
    /* no-op */
  }

  private void setupViews() {
    streamsList.setLayoutManager(new LinearLayoutManager(getContext()));
    initializeStreamListAdapter();
  }

  @Override public void onResume() {
    super.onResume();
    searchItemsPresenter.resume();
  }

  @Override public void onPause() {
    super.onPause();
    searchItemsPresenter.pause();
  }

  @Override public void renderSearchItems(List<SearchableModel> searchableModels) {
    adapter.setItems(searchableModels);
    adapter.notifyDataSetChanged();
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    startActivity(StreamTimelineActivity.newIntent(getContext(), currentIdStream));
    getActivity().finish();
  }
}
