package com.shootr.mobile.ui.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.shootr.mobile.R;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.ui.activities.PhotoViewActivity;
import com.shootr.mobile.ui.activities.ProfileActivity;
import com.shootr.mobile.ui.activities.ShotDetailActivity;
import com.shootr.mobile.ui.activities.StreamTimelineActivity;
import com.shootr.mobile.ui.adapters.DiscoverTimelineAdapter;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnDiscoverTimelineFavoriteClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnDiscoveredStreamClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnImageClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnImageLongClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnNiceShotListener;
import com.shootr.mobile.ui.adapters.listeners.OnOpenShotMenuListener;
import com.shootr.mobile.ui.adapters.listeners.OnReshootClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnShotLongClick;
import com.shootr.mobile.ui.adapters.listeners.OnUrlClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnUsernameClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnVideoClickListener;
import com.shootr.mobile.ui.adapters.listeners.ShotClickListener;
import com.shootr.mobile.ui.base.BaseFragment;
import com.shootr.mobile.ui.model.BaseMessageModel;
import com.shootr.mobile.ui.model.DiscoverTimelineModel;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.model.StreamModel;
import com.shootr.mobile.ui.presenter.DiscoverTimelinePresenter;
import com.shootr.mobile.ui.views.DiscoverTimelineView;
import com.shootr.mobile.ui.widgets.BottomOffsetDecoration;
import com.shootr.mobile.ui.widgets.PreCachingLayoutManager;
import com.shootr.mobile.util.AnalyticsTool;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.Clipboard;
import com.shootr.mobile.util.CustomContextMenu;
import com.shootr.mobile.util.FeedbackMessage;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.InitialsLoader;
import com.shootr.mobile.util.Intents;
import com.shootr.mobile.util.NumberFormatUtil;
import com.shootr.mobile.util.ShareManager;
import com.shootr.mobile.util.ShotTextSpannableBuilder;
import java.util.List;
import javax.inject.Inject;

public class DiscoverTimelineFragment extends BaseFragment implements DiscoverTimelineView {

  @Inject DiscoverTimelinePresenter discoverPresenter;
  @Inject ImageLoader imageLoader;
  @Inject FeedbackMessage feedbackMessage;
  @Inject AndroidTimeUtils timeUtils;
  @Inject AnalyticsTool analyticsTool;
  @Inject SessionRepository sessionRepository;
  @Inject NumberFormatUtil numberFormatUtil;
  @Inject InitialsLoader initialsLoader;
  @Inject ShareManager shareManager;

  @BindString(R.string.analytics_action_favorite_stream) String analyticsActionFavoriteStream;
  @BindString(R.string.analytics_label_favorite_stream) String analyticsLabelFavoriteStream;
  @BindString(R.string.analytics_action_share_shot) String analyticsActionShareShot;
  @BindString(R.string.analytics_label_share_shot) String analyticsLabelShareShot;
  @BindString(R.string.analytics_action_external_share) String analyticsActionExternalShare;
  @BindString(R.string.analytics_label_external_share) String analyticsLabelExternalShare;
  @BindString(R.string.analytics_action_nice) String analyticsActionNice;
  @BindString(R.string.analytics_label_nice) String analyticsLabelNice;
  @BindString(R.string.analytics_source_discover_timeline) String timelineSource;
  @BindString(R.string.shot_shared_message) String shotShared;

  @BindView(R.id.discover_recycler) RecyclerView discoverList;
  @BindView(R.id.discover_empty) View empty;
  @BindView(R.id.discover_loading) View loading;

  private DiscoverTimelineAdapter adapter;
  private Unbinder unbinder;

  public static Fragment newInstance() {
    return new DiscoverTimelineFragment();
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    setHasOptionsMenu(true);
    View fragmentView = inflater.inflate(R.layout.fragment_discover_timeline, container, false);
    unbinder = ButterKnife.bind(this, fragmentView);
    return fragmentView;
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    initializePresenter();
    initializeViews();
  }

  private void initializeViews() {
    setuppDiscoverList();
    setupAdapter();
    discoverList.setAdapter(adapter);
  }

  private void setuppDiscoverList() {
    PreCachingLayoutManager preCachingLayoutManager = new PreCachingLayoutManager(getContext());
    discoverList.setLayoutManager(preCachingLayoutManager);
    discoverList.setHasFixedSize(false);
    discoverList.setItemAnimator(new DefaultItemAnimator() {
      @Override
      public boolean canReuseUpdatedViewHolder(@NonNull RecyclerView.ViewHolder viewHolder) {
        return true;
      }

      @Override
      public boolean canReuseUpdatedViewHolder(@NonNull RecyclerView.ViewHolder viewHolder,
          @NonNull List<Object> payloads) {
        return true;
      }
    });
    discoverList.addItemDecoration(new BottomOffsetDecoration(200));
  }

  private void initializePresenter() {
    discoverPresenter.initialize(this);
  }

  private void setupAdapter() {
    adapter = new DiscoverTimelineAdapter(imageLoader, initialsLoader, new OnDiscoveredStreamClickListener() {
      @Override public void onStreamClick(String streamId) {
        discoverPresenter.streamClicked(streamId);
      }
    }, new OnDiscoverTimelineFavoriteClickListener() {
      @Override public void onFavoriteClick(StreamModel streamModel) {
        discoverPresenter.addStreamToFavorites(streamModel);
        sendFavoriteAnalytics(streamModel.getIdStream(), streamModel.getTitle());
      }

      @Override public void onRemoveFavoriteClick(StreamModel streamModel) {
        setupRemoveFromFavoriteDialog(streamModel);
      }
    }, new OnAvatarClickListener() {
      @Override public void onAvatarClick(String userId, View avatarView) {
        discoverPresenter.onAvatarClicked(userId);
      }
    }, new OnVideoClickListener() {
      @Override public void onVideoClick(String url) {
        openVideo(url);
      }
    }, new OnNiceShotListener() {
      @Override public void markNice(ShotModel shot) {
        discoverPresenter.markNiceShot(shot);
        sendNiceAnalytics(shot);
      }

      @Override public void unmarkNice(String idShot) {
        discoverPresenter.unmarkNiceShot(idShot);
      }
    }, new OnOpenShotMenuListener() {
      @Override public void openMenu(ShotModel shot) {
        getBaseContextMenuOptions(shot).show();
      }
    }, new OnUsernameClickListener() {
      @Override public void onUsernameClick(String username) {
        /* no-op */
      }
    }, timeUtils, numberFormatUtil, new ShotTextSpannableBuilder(), new ShotClickListener() {
      @Override public void onClick(ShotModel shotModel) {
        discoverPresenter.shotClicked(shotModel);
      }
    }, new OnShotLongClick() {
      @Override public void onShotLongClick(ShotModel shot) {
        getBaseContextMenuOptions(shot).show();
      }
    }, new OnImageLongClickListener() {
      @Override public void onImageLongClick(ShotModel shot) {
        /* no-op */
      }
    }, new View.OnTouchListener() {
      @Override public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
      }
    }, new OnImageClickListener() {
      @Override public void onImageClick(View sharedImage, BaseMessageModel shot) {
        openImage(sharedImage, shot.getImage().getImageUrl());
      }
    }, new OnUrlClickListener() {
      @Override public void onClick() {
        /* no-op */
      }
    }, new OnReshootClickListener() {
      @Override public void onReshootClick(ShotModel shot) {
        discoverPresenter.reshoot(shot);
        sendReshootAnalytics(shot);
      }

      @Override public void onUndoReshootClick(ShotModel shot) {
        discoverPresenter.undoReshoot(shot);
        //TODO sendReshootAnalytics(shot);
      }
    });
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
                discoverPresenter.removeFromFavorites(streamModel);
              }
            })
        .setNegativeButton(getString(R.string.cancel), null)
        .create()
        .show();
  }

  private void copyShotCommentToClipboard(ShotModel shotModel) {
    Clipboard.copyShotComment(getActivity(), shotModel);
  }

  private CustomContextMenu.Builder getBaseContextMenuOptions(final ShotModel shotModel) {
    return new CustomContextMenu.Builder(getActivity()).addAction(
        R.string.menu_share_shot_via_shootr, new Runnable() {
          @Override public void run() {
            discoverPresenter.reshoot(shotModel);
            sendReshootAnalytics(shotModel);
          }
        }).addAction(R.string.menu_share_shot_via, new Runnable() {
      @Override public void run() {
        shareShotIntent(shotModel);
        sendShareExternalShotAnalytics(shotModel);
      }
    }).addAction(R.string.menu_copy_text, new Runnable() {
      @Override public void run() {
        copyShotCommentToClipboard(shotModel);
      }
    });
  }

  private void shareShotIntent(ShotModel shotModel) {
    Intent shareIntent = shareManager.shareShotIntent(getActivity(), shotModel);
    Intents.maybeStartActivity(getActivity(), shareIntent);
  }

  private void openImage(View sharedImage, String imageUrl) {
    Intent intent = PhotoViewActivity.getIntentForActivity(getContext(), imageUrl, imageUrl);
    startActivity(intent);
  }

  private void openVideo(String url) {
    Uri uri = Uri.parse(url);
    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
    startActivity(intent);
  }

  private void sendFavoriteAnalytics(String idStream, String streamTitle) {
    AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
    builder.setContext(getContext());
    builder.setActionId(analyticsActionFavoriteStream);
    builder.setLabelId(analyticsLabelFavoriteStream);
    builder.setSource(timelineSource);
    builder.setIdStream(idStream);
    builder.setStreamName((streamTitle));
    builder.setUser(sessionRepository.getCurrentUser());
    analyticsTool.analyticsSendAction(builder);
    analyticsTool.appsFlyerSendAction(builder);
  }

  private void sendNiceAnalytics(ShotModel shot) {
    AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
    builder.setContext(getContext());
    builder.setActionId(analyticsActionNice);
    builder.setLabelId(analyticsLabelNice);
    builder.setSource(timelineSource);
    builder.setUser(sessionRepository.getCurrentUser());
    builder.setIdTargetUser(shot.getIdUser());
    builder.setTargetUsername(shot.getUsername());
    builder.setIdStream(shot.getStreamId());
    builder.setStreamName(shot.getStreamTitle());
    analyticsTool.analyticsSendAction(builder);
  }

  private void sendReshootAnalytics(ShotModel shot) {
    AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
    builder.setContext(getContext());
    builder.setAction(getString(R.string.menu_share_shot_via_shootr));
    builder.setActionId(analyticsActionShareShot);
    builder.setLabelId(analyticsLabelShareShot);
    builder.setSource(timelineSource);
    builder.setUser(sessionRepository.getCurrentUser());
    builder.setIdTargetUser(shot.getIdUser());
    builder.setTargetUsername(shot.getUsername());
    analyticsTool.analyticsSendAction(builder);
  }

  private void sendShareExternalShotAnalytics(ShotModel shot) {
    AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
    builder.setContext(getContext());
    builder.setActionId(analyticsActionExternalShare);
    builder.setLabelId(analyticsLabelExternalShare);
    builder.setSource(timelineSource);
    builder.setUser(sessionRepository.getCurrentUser());
    analyticsTool.analyticsSendAction(builder);
  }

  @Override public void renderDiscover(DiscoverTimelineModel discoverTimelineModel) {
    adapter.setItems(discoverTimelineModel);
    adapter.notifyDataSetChanged();
  }

  @Override public void navigateToStreamTimeline(String idStream) {
    startActivity(StreamTimelineActivity.newIntent(getActivity(), idStream));
  }

  @Override public void navigateToShotDetail(ShotModel shotModel) {
    startActivity(ShotDetailActivity.getIntentForActivity(getActivity(), shotModel));
  }

  @Override public void navigateToUserProfile(String userId) {
    startActivity(ProfileActivity.getIntent(getActivity(), userId));
  }

  @Override public void scrollListToTop() {
    if (discoverList != null) {
      discoverList.smoothScrollToPosition(0);
    }
  }

  @Override public void showEmpty() {
    if (empty != null) {
      empty.setVisibility(View.VISIBLE);
    }
  }

  @Override public void renderNewFavorite(StreamModel streamModel) {
    adapter.addFavorite(streamModel);
  }

  @Override public void removeFavorite(StreamModel streamModel) {
    adapter.removeFavorite(streamModel);
  }

  @Override public void renderNiceMarked(ShotModel shotModel) {
    adapter.markNice(shotModel);
  }

  @Override public void renderNiceUnmarked(String idShot) {
    adapter.unmarkNice(idShot);
  }

  @Override public void showReshot(ShotModel shotModel, boolean mark) {
    adapter.reshoot(shotModel, mark);
  }

  @Override public void hideEmpty() {
    if (empty != null) {
      empty.setVisibility(View.GONE);
    }
  }

  @Override public void showLoading() {
    if (loading != null) {
      loading.setVisibility(View.VISIBLE);
    }
  }

  @Override public void hideLoading() {
    if (loading != null) {
      loading.setVisibility(View.GONE);
    }
  }

  @Override public void showError(String message) {
    feedbackMessage.show(getView(), message);
  }

  @Override public void onResume() {
    super.onResume();
    discoverPresenter.resume();
  }

  @Override public void onPause() {
    super.onPause();
    discoverPresenter.pause();
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }
}
