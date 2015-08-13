package com.shootr.android.ui.fragments;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.shootr.android.R;
import com.shootr.android.ui.ToolbarDecorator;
import com.shootr.android.ui.activities.BaseToolbarDecoratedActivity;
import com.shootr.android.ui.activities.DraftsActivity;
import com.shootr.android.ui.activities.NewStreamActivity;
import com.shootr.android.ui.activities.PhotoViewActivity;
import com.shootr.android.ui.activities.PostNewShotActivity;
import com.shootr.android.ui.activities.ProfileContainerActivity;
import com.shootr.android.ui.activities.ShotDetailActivity;
import com.shootr.android.ui.activities.StreamDetailActivity;
import com.shootr.android.ui.adapters.TimelineAdapter;
import com.shootr.android.ui.adapters.listeners.NiceShotListener;
import com.shootr.android.ui.adapters.listeners.OnShotClickListener;
import com.shootr.android.ui.base.BaseFragment;
import com.shootr.android.ui.component.PhotoPickerController;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.presenter.FavoriteStatusPresenter;
import com.shootr.android.ui.presenter.NewShotBarPresenter;
import com.shootr.android.ui.presenter.ReportShotPresenter;
import com.shootr.android.ui.presenter.StreamTimelinePresenter;
import com.shootr.android.ui.presenter.WatchNumberPresenter;
import com.shootr.android.ui.views.FavoriteStatusView;
import com.shootr.android.ui.views.NewShotBarView;
import com.shootr.android.ui.views.NullNewShotBarView;
import com.shootr.android.ui.views.NullWatchNumberView;
import com.shootr.android.ui.views.ReportShotView;
import com.shootr.android.ui.views.StreamTimelineView;
import com.shootr.android.ui.views.WatchNumberView;
import com.shootr.android.ui.views.nullview.NullFavoriteStatusView;
import com.shootr.android.ui.views.nullview.NullStreamTimelineView;
import com.shootr.android.ui.widgets.BadgeDrawable;
import com.shootr.android.ui.widgets.ListViewScrollObserver;
import com.shootr.android.util.AndroidTimeUtils;
import com.shootr.android.util.CustomContextMenu;
import com.shootr.android.util.MenuItemValueHolder;
import com.shootr.android.util.PicassoWrapper;
import com.shootr.android.util.UsernameClickListener;
import java.io.File;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class StreamTimelineFragment extends BaseFragment
  implements StreamTimelineView, NewShotBarView, WatchNumberView, FavoriteStatusView, ReportShotView {

    public static final String EXTRA_STREAM_ID = "streamId";
    public static final String EXTRA_STREAM_TITLE = "streamTitle";
    private static final int REQUEST_STREAM_DETAIL = 1;
    public static final String CLIPBOARD_LABEL = "clipboard_label";

    //region Fields
    @Inject StreamTimelinePresenter streamTimelinePresenter;
    @Inject NewShotBarPresenter newShotBarPresenter;
    @Inject WatchNumberPresenter watchNumberPresenter;
    @Inject FavoriteStatusPresenter favoriteStatusPresenter;
    @Inject ReportShotPresenter reportShotPresenter;

    @Inject PicassoWrapper picasso;

    @Inject AndroidTimeUtils timeUtils;

    @Bind(R.id.timeline_shot_list) ListView listView;
    @Bind(R.id.timeline_swipe_refresh) SwipeRefreshLayout swipeRefreshLayout;

    @Bind(R.id.timeline_empty) View emptyView;
    @Bind(R.id.shot_bar_drafts) View draftsButton;

    @BindString(R.string.report_base_url) String reportBaseUrl;

    @Deprecated
    private TimelineAdapter adapter;
    private View.OnClickListener avatarClickListener;
    private View.OnClickListener imageClickListener;
    private TimelineAdapter.VideoClickListener videoClickListener;
    private UsernameClickListener usernameClickListener;
    private NiceShotListener niceShotListener;

    private PhotoPickerController photoPickerController;
    private NewShotBarView newShotBarViewDelegate;
    private ToolbarDecorator toolbarDecorator;
    private MenuItem watchersMenuItem;
    private BadgeDrawable watchersBadgeDrawable;
    private Integer watchNumberCount;
    private View footerProgress;
    private MenuItemValueHolder addToFavoritesMenuItem = new MenuItemValueHolder();
    private MenuItemValueHolder removeFromFavoritesMenuItem = new MenuItemValueHolder();
    //endregion

    public static StreamTimelineFragment newInstance(String streamId, String streamTitle) {
        Bundle fragmentArguments = new Bundle();
        fragmentArguments.putString(EXTRA_STREAM_ID, streamId);
        fragmentArguments.putString(EXTRA_STREAM_TITLE, streamTitle);
        return newInstance(fragmentArguments);
    }

    public static StreamTimelineFragment newInstance(Bundle fragmentArguments) {
        StreamTimelineFragment fragment = new StreamTimelineFragment();
        fragment.setArguments(fragmentArguments);
        return fragment;
    }

    //region Lifecycle methods
    @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.timeline_stream, container, false);
        ButterKnife.bind(this, fragmentView);
        return fragmentView;
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        streamTimelinePresenter.setView(new NullStreamTimelineView());
        newShotBarPresenter.setView(new NullNewShotBarView());
        watchNumberPresenter.setView(new NullWatchNumberView());
        favoriteStatusPresenter.setView(new NullFavoriteStatusView());
    }

    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        String idStream = getArguments().getString(EXTRA_STREAM_ID);
        initializeToolbar();
        initializePresenters(idStream);
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_STREAM_DETAIL && resultCode == NewStreamActivity.RESULT_EXIT_STREAM) {
            if (getActivity() != null) {
                getActivity().finish();
            }
        } else {
            photoPickerController.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.timeline, menu);
        watchersMenuItem = menu.findItem(R.id.menu_info);
        watchersMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        addToFavoritesMenuItem.bindRealMenuItem(menu.findItem(R.id.menu_add_favorite));
        addToFavoritesMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        removeFromFavoritesMenuItem.bindRealMenuItem(menu.findItem(R.id.menu_remove_favorite));
        removeFromFavoritesMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        if (isAdded()) {
            LayerDrawable icon = (LayerDrawable) getResources().getDrawable(R.drawable.badge_circle);
            icon.setDrawableByLayerId(R.id.ic_people, getResources().getDrawable(R.drawable.ic_action_ic_one_people));
            setupWatchNumberBadgeIcon(getActivity(), icon);
            watchersMenuItem.setIcon(icon);
            watchersMenuItem.getIcon();
            updateWatchNumberIcon();
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_info:
                watchNumberPresenter.onWatchNumberClick();
                return true;
            case R.id.menu_add_favorite:
                favoriteStatusPresenter.addToFavorites();
                return true;
            case R.id.menu_remove_favorite:
                favoriteStatusPresenter.removeFromFavorites();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override public void onResume() {
        super.onResume();
        streamTimelinePresenter.resume();
        newShotBarPresenter.resume();
        watchNumberPresenter.resume();
        favoriteStatusPresenter.resume();
    }

    @Override public void onPause() {
        super.onPause();
        streamTimelinePresenter.pause();
        newShotBarPresenter.pause();
        watchNumberPresenter.pause();
        favoriteStatusPresenter.pause();
    }

    private void initializeToolbar() {
        //FIXME So coupling. Much bad. Such ugly.
        toolbarDecorator = ((BaseToolbarDecoratedActivity) getActivity()).getToolbarDecorator();
    }

    private void initializePresenters(String idStream) {
        streamTimelinePresenter.initialize(this, idStream);
        newShotBarPresenter.initialize(this);
        watchNumberPresenter.initialize(this, idStream);
        favoriteStatusPresenter.initialize(this, idStream);
        reportShotPresenter.initialize(this);
    }

    //endregion

    private void setupNewShotBarDelegate() {
        newShotBarViewDelegate = new NewShotBarViewDelegate(photoPickerController, draftsButton) {
            @Override public void openNewShotView() {
                Intent newShotIntent = PostNewShotActivity.IntentBuilder //
                  .from(getActivity()) //
                  .build();
                startActivity(newShotIntent);
            }

            @Override public void openNewShotViewWithImage(File image) {
                Intent newShotIntent = PostNewShotActivity.IntentBuilder //
                  .from(getActivity()) //
                  .withImage(image) //
                  .build();
                startActivity(newShotIntent);
            }
        };
    }

    public void setupWatchNumberBadgeIcon(Context context, LayerDrawable icon) {
        // Reuse drawable if possible
        if (watchersBadgeDrawable == null) {
            Drawable reuse = icon.findDrawableByLayerId(R.id.ic_badge);
            if (reuse != null && reuse instanceof BadgeDrawable) {
                watchersBadgeDrawable = (BadgeDrawable) reuse;
            } else {
                watchersBadgeDrawable = new BadgeDrawable(context);
            }
        }
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_badge, watchersBadgeDrawable);
    }

    private void updateWatchNumberIcon() {
        if (watchersBadgeDrawable != null && watchersMenuItem != null && watchNumberCount != null) {
            watchersBadgeDrawable.setCount(watchNumberCount);
            watchersMenuItem.setVisible(true);
        }
    }

    //region Views manipulation
    private void initializeViews() {
        setupListAdapter();
        setupSwipeRefreshLayout();
        setupListScrollListeners();
        setupPhotoPicker();
        setupNewShotBarDelegate();
    }

    private void setupPhotoPicker() {
        photoPickerController = new PhotoPickerController.Builder().onActivity(getActivity())
          .withHandler(new PhotoPickerController.Handler() {
              @Override public void onSelected(File imageFile) {
                  newShotBarPresenter.newShotImagePicked(imageFile);
              }

              @Override public void onError(Exception e) {
                  //TODO mostrar algo
                  Timber.e(e, "Error selecting image");
              }

              @Override public void startPickerActivityForResult(Intent intent, int requestCode) {
                  startActivityForResult(intent, requestCode);
              }
          })
          .build();
    }

    private void setupListAdapter() {
        avatarClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = ((TimelineAdapter.ViewHolder) v.getTag()).position;
                openProfile(position);
            }
        };

        imageClickListener = new View.OnClickListener() {
            @Override public void onClick(View v) {
                int position = ((TimelineAdapter.ViewHolder) v.getTag()).position;
                openImage(position);
            }
        };

        usernameClickListener = new UsernameClickListener() {
            @Override
            public void onClick(String username) {
                goToUserProfile(username);
            }
        };

        videoClickListener = new TimelineAdapter.VideoClickListener() {
            @Override
            public void onClick(String url) {
                onVideoClick(url);
            }
        };

        niceShotListener = new NiceShotListener() {
            @Override
            public void markNice(String idShot) {
                streamTimelinePresenter.markNiceShot(idShot);
            }

            @Override
            public void unmarkNice(String idShot) {
                streamTimelinePresenter.unmarkNiceShot(idShot);
            }
        };

        View footerView = LayoutInflater.from(getActivity()).inflate(R.layout.item_list_loading, listView, false);
        footerProgress = ButterKnife.findById(footerView, R.id.loading_progress);

        footerProgress.setVisibility(View.GONE);

        listView.addFooterView(footerView, null, false);

        adapter = new TimelineAdapter(getActivity(),
          picasso,
          avatarClickListener,
          imageClickListener,
          videoClickListener,
          niceShotListener,
          new OnShotClickListener() {
              @Override public void onShotClick(ShotModel shot) {
                  openShot(shot);
              }

              @Override public boolean onShotLongClick(ShotModel shotModel) {
                  openContextualMenu(shotModel);
                  return true;
              }
          },
          usernameClickListener,
          timeUtils);
        listView.setAdapter(adapter);
    }

    private void openContextualMenu(final ShotModel shotModel) {
        CustomContextMenu.Builder builder = new CustomContextMenu.Builder(getActivity());
        builder.addAction(getActivity().getString(R.string.report_context_menu_copy_text), new Runnable() {
            @Override public void run() {
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(CLIPBOARD_LABEL, shotModel.getComment());
                clipboard.setPrimaryClip(clip);
            }
        });
        builder.addAction(getActivity().getString(R.string.report_context_menu_report), new Runnable() {
            @Override
            public void run() {
                reportShotPresenter.loadReport(shotModel);
            }
        });
        builder.show();
    }

    private void startProfileContainerActivity(String username) {
        Intent intentForUser = ProfileContainerActivity.getIntentWithUsername(getActivity(), username);
        startActivity(intentForUser);
    }

    private void goToUserProfile(String username) {
        startProfileContainerActivity(username);
    }

    private void onVideoClick(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    private void setupSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override public void onRefresh() {
                streamTimelinePresenter.refresh();
            }
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.refresh_1,
          R.color.refresh_2,
          R.color.refresh_3,
          R.color.refresh_4);
    }

    private void setupListScrollListeners() {
        new ListViewScrollObserver(listView).setOnScrollUpAndDownListener(new ListViewScrollObserver.OnListViewScrollListener() {
            @Override public void onScrollUpDownChanged(int delta, int scrollPosition, boolean exact) {
                if (delta < -10) {
                    // going down
                } else if (delta > 10) {
                    // going up
                }
            }

            @Override public void onScrollIdle() {
                checkIfEndOfListVisible();
            }
        });
    }

    private void checkIfEndOfListVisible() {
        int lastItemPosition = listView.getAdapter().getCount() - 1;
        int lastVisiblePosition = listView.getLastVisiblePosition();
        if (lastItemPosition == lastVisiblePosition) {
            streamTimelinePresenter.showingLastShot(adapter.getLastShot());
        }
    }
    //endregion

    public void openShot(ShotModel shotModel) {
        Intent intent = ShotDetailActivity.getIntentForActivity(getActivity(), shotModel);
        startActivity(intent);
    }

    public void openProfile(int position) {
        ShotModel shotVO = adapter.getItem(position);
        Intent profileIntent = ProfileContainerActivity.getIntent(getActivity(), shotVO.getIdUser());
        startActivity(profileIntent);
    }

    public void openImage(int position) {
        ShotModel shotVO = adapter.getItem(position);
        String imageUrl = shotVO.getImage();
        if (imageUrl != null) {
            Intent intentForImage = PhotoViewActivity.getIntentForActivity(getActivity(), imageUrl);
            startActivity(intentForImage);
        }
    }

    @OnClick(R.id.shot_bar_text)
    public void startNewShot() {
        newShotBarPresenter.newShotFromTextBox();
    }

    @OnClick(R.id.shot_bar_photo)
    public void startNewShotWithPhoto() {
        newShotBarPresenter.newShotFromImage();
    }

    @OnClick(R.id.shot_bar_drafts)
    public void openDrafts() {
        startActivity(new Intent(getActivity(), DraftsActivity.class));
    }

    //region View methods
    @Override public void setShots(List<ShotModel> shots) {
        adapter.setShots(shots);
    }

    @Override public void hideShots() {
        listView.setVisibility(View.GONE);
    }

    @Override public void showShots() {
        listView.setVisibility(View.VISIBLE);
    }

    @Override public void addNewShots(List<ShotModel> newShots) {
        adapter.addShotsAbove(newShots);
    }

    @Override public void addOldShots(List<ShotModel> oldShots) {
        adapter.addShotsBelow(oldShots);
    }

    @Override public void showLoadingOldShots() {
        footerProgress.setVisibility(View.VISIBLE);
    }

    @Override public void hideLoadingOldShots() {
        footerProgress.setVisibility(View.GONE);
    }

    @Override public void navigateToStreamDetail(String idStream) {
        Intent intent = new Intent(getActivity(), StreamDetailActivity.class);
        intent.putExtra(EXTRA_STREAM_ID, idStream);
        startActivityForResult(intent, REQUEST_STREAM_DETAIL);
    }

    @Override public void showEmpty() {
        emptyView.setVisibility(View.VISIBLE);
    }

    @Override public void hideEmpty() {
        emptyView.setVisibility(View.GONE);
    }

    @Override public void showLoading() {
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override public void hideLoading() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override public void showError(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override public void openNewShotView() {
        newShotBarViewDelegate.openNewShotView();
    }

    @Override public void pickImage() {
        newShotBarViewDelegate.pickImage();
    }

    @Override public void openNewShotViewWithImage(File image) {
        newShotBarViewDelegate.openNewShotViewWithImage(image);
    }

    @Override public void showDraftsButton() {
        newShotBarViewDelegate.showDraftsButton();
    }

    @Override public void hideDraftsButton() {
        newShotBarViewDelegate.hideDraftsButton();
    }

    @Override public void showWatchingPeopleCount(Integer count) {
        watchNumberCount = count;
        updateWatchNumberIcon();
    }

    @Override public void hideWatchingPeopleCount() {
        watchNumberCount = null;
        updateWatchNumberIcon();
    }

    @Override
    public void showAddToFavoritesButton() {
        addToFavoritesMenuItem.setVisible(true);
    }

    @Override
    public void hideAddToFavoritesButton() {
        addToFavoritesMenuItem.setVisible(false);
    }

    @Override
    public void showRemoveFromFavoritesButton() {
        removeFromFavoritesMenuItem.setVisible(true);
    }

    @Override
    public void hideRemoveFromFavoritesButton() {
        removeFromFavoritesMenuItem.setVisible(false);
    }

    @Override
    public void showAddedToFavorites() {
        Toast.makeText(getActivity(), R.string.added_to_favorites, Toast.LENGTH_SHORT).show();
    }

    @Override public void goToReport(String sessionToken, ShotModel shotModel) {
        Uri.parse(String.format(reportBaseUrl, sessionToken, shotModel.getIdShot()));
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(reportBaseUrl,
          sessionToken,
          shotModel.getIdShot())));
        startActivity(browserIntent);
    }

    @Override public void showConfirmationMessage() {
        AlertDialog.Builder builder =
          new AlertDialog.Builder(getActivity());

        builder.setMessage(getActivity().getString(R.string.alert_report_confirmed_email_message))
          .setTitle(getActivity().getString(R.string.alert_report_confirmed_email_title))
          .setPositiveButton(getActivity().getString(R.string.alert_report_confirmed_email_ok), null);

        builder.create().show();
    }
    //endregion
}
