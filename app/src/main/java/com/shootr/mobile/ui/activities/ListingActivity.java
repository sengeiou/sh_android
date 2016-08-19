package com.shootr.mobile.ui.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.ToolbarDecorator;
import com.shootr.mobile.ui.adapters.ListingAdapter;
import com.shootr.mobile.ui.adapters.listeners.OnFavoriteClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnStreamClickListener;
import com.shootr.mobile.ui.model.StreamResultModel;
import com.shootr.mobile.ui.presenter.ListingListPresenter;
import com.shootr.mobile.ui.views.ListingView;
import com.shootr.mobile.util.AnalyticsTool;
import com.shootr.mobile.util.CustomContextMenu;
import com.shootr.mobile.util.FeedbackMessage;
import com.shootr.mobile.util.InitialsLoader;
import com.shootr.mobile.util.Intents;
import com.shootr.mobile.util.ShareManager;
import java.util.List;
import javax.inject.Inject;

public class ListingActivity extends BaseToolbarDecoratedActivity implements ListingView {

    private static final String EXTRA_ID_USER = "idUser";
    private static final String EXTRA_IS_CURRENT_USER = "is_current_user";
    public static final int REQUEST_NEW_STREAM = 3;

    @Bind(R.id.listing_list) RecyclerView listingList;
    @Bind(R.id.listing_loading) View loadingView;
    @Bind(R.id.listing_empty_title) View emptyView;
    @Bind(R.id.listing_add_stream) FloatingActionButton addStream;
    @BindString(R.string.shared_stream_notification) String sharedStream;
    @BindString(R.string.analytics_screen_user_streams) String analyticsScreenuserStreams;

    @Inject ListingListPresenter presenter;
    @Inject ShareManager shareManager;
    @Inject FeedbackMessage feedbackMessage;
    @Inject AnalyticsTool analyticsTool;
    @Inject InitialsLoader initialsLoader;

    private ListingAdapter adapter;

    public static Intent getIntent(Context context, String idUser, Boolean isCurrentUser) {
        Intent intent = new Intent(context, ListingActivity.class);
        intent.putExtra(EXTRA_ID_USER, idUser);
        intent.putExtra(EXTRA_IS_CURRENT_USER, isCurrentUser);
        return intent;
    }

    @Override protected int getLayoutResource() {
        return R.layout.activity_listing;
    }

    @Override protected void initializeViews(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        analyticsTool.analyticsStart(getBaseContext(), analyticsScreenuserStreams);
        Boolean isCurrentUser = getIntent().getBooleanExtra(EXTRA_IS_CURRENT_USER, false);
        adapter = new ListingAdapter(imageLoader, isCurrentUser, new OnStreamClickListener() {
            @Override public void onStreamClick(StreamResultModel stream) {
                presenter.selectStream(stream);
            }

            @Override public boolean onStreamLongClick(StreamResultModel stream) {
                openContextualMenu(stream);
                return true;
            }
        }, new OnFavoriteClickListener() {
            @Override public void onFavoriteClick(StreamResultModel stream) {
                presenter.addToFavorite(stream);
            }

            @Override public void onRemoveFavoriteClick(StreamResultModel stream) {
                presenter.removeFromFavorites(stream);
            }
        }, initialsLoader);

        listingList.setAdapter(adapter);
        listingList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override protected void initializePresenter() {
        Intent intent = getIntent();
        String idUser = intent.getStringExtra(EXTRA_ID_USER);
        Boolean isCurrentUser = intent.getBooleanExtra(EXTRA_IS_CURRENT_USER, false);
        presenter.initialize(this, idUser, isCurrentUser);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
        /* no-op */
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_NEW_STREAM && resultCode == Activity.RESULT_OK) {
            String streamId = data.getStringExtra(NewStreamActivity.KEY_STREAM_ID);
            presenter.streamCreated(streamId);
        }
    }

    @Override protected void onResume() {
        super.onResume();
        presenter.resume();
    }

    @Override protected void onPause() {
        super.onPause();
        analyticsTool.analyticsStop(getBaseContext(), this);
        presenter.pause();
    }

    private void openContextualMenu(StreamResultModel stream) {
        presenter.openContextualMenu(stream);
    }

    private void shareStream(StreamResultModel stream) {
        Intent shareIntent = shareManager.shareStreamIntent(this, stream.getStreamModel());
        Intents.maybeStartActivity(this, shareIntent);
    }

    @Override public void renderHoldingStreams(List<StreamResultModel> streams) {
        adapter.setCreatedStreams(streams);
    }

    @Override public void renderFavoritedStreams(List<StreamResultModel> listingUserFavoritedStreams) {
        adapter.setFavoritedStreams(listingUserFavoritedStreams);
    }

    @Override public void navigateToStreamTimeline(String idStream, String tag, String authorId) {
        startActivity(StreamTimelineActivity.newIntent(this, idStream, tag, authorId));
    }

    @Override public void hideLoading() {
        loadingView.setVisibility(View.GONE);
    }

    @Override public void showError(String errorMessage) {
        feedbackMessage.show(getView(), errorMessage);
    }

    @Override public void showLoading() {
        loadingView.setVisibility(View.VISIBLE);
    }

    @Override public void setCurrentUserFavorites(List<StreamResultModel> favoriteStreams) {
        adapter.setFavoriteStreams(favoriteStreams);
    }

    @Override public void hideContent() {
        listingList.setVisibility(View.GONE);
    }

    @Override public void navigateToCreatedStreamDetail(String streamId) {
        startActivity(StreamDetailActivity.getIntent(this, streamId));
    }

    @Override public void showStreamShared() {
        feedbackMessage.show(getView(), sharedStream);
    }

    @Override public void showSectionTitles() {
        adapter.setShowTitles(true);
    }

    @Override public void askRemoveStreamConfirmation() {
        new AlertDialog.Builder(this).setMessage(R.string.remove_stream_confirmation)
          .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                  presenter.removeStream();
              }
          })
          .setNegativeButton(R.string.cancel, null)
          .show();
    }

    @Override public void updateStreams() {
        adapter.notifyDataSetChanged();
    }

    @Override public void showAddStream() {
        addStream.setVisibility(View.VISIBLE);
    }

    @Override public void hideAddStream() {
        addStream.setVisibility(View.GONE);
    }

    @Override public void showContextMenuWithoutAddFavorite(final StreamResultModel stream) {
        CustomContextMenu.Builder builder = new CustomContextMenu.Builder(this);
        builder.addAction(R.string.share_via_shootr, new Runnable() {
            @Override public void run() {
                presenter.shareStream(stream);
            }
        }).addAction(R.string.share_via, new Runnable() {
            @Override public void run() {
                shareStream(stream);
            }
        }).show();
    }

    @Override public void showCurrentUserContextMenuWithAddFavorite(final StreamResultModel stream) {
        CustomContextMenu.Builder builder = new CustomContextMenu.Builder(this);
        builder.addAction(R.string.edit_stream_listing_context_menu, new Runnable() {
            @Override public void run() {
                Intent intent =
                  NewStreamActivity.newIntent(ListingActivity.this, stream.getStreamModel().getIdStream());
                startActivity(intent);
            }
        });
        addBaseContextMenuOptions(builder, stream);
        builder.addAction(R.string.remove_stream_listing_context_menu, new Runnable() {
            @Override public void run() {
                presenter.remove(stream.getStreamModel().getIdStream());
            }
        }).show();
    }

    @Override public void showCurrentUserContextMenuWithoutAddFavorite(final StreamResultModel stream) {
        CustomContextMenu.Builder builder = new CustomContextMenu.Builder(this);
        builder.addAction(R.string.edit_stream_listing_context_menu, new Runnable() {
            @Override public void run() {
                Intent intent =
                  NewStreamActivity.newIntent(ListingActivity.this, stream.getStreamModel().getIdStream());
                startActivity(intent);
            }
        });
        builder.addAction(R.string.share_via_shootr, new Runnable() {
            @Override public void run() {
                presenter.shareStream(stream);
            }
        }).addAction(R.string.share_via, new Runnable() {
            @Override public void run() {
                shareStream(stream);
            }
        });
        builder.addAction(R.string.remove_stream_listing_context_menu, new Runnable() {
            @Override public void run() {
                presenter.remove(stream.getStreamModel().getIdStream());
            }
        }).show();
    }

    @Override public void showContextMenuWithAddFavorite(final StreamResultModel stream) {
        CustomContextMenu.Builder builder = new CustomContextMenu.Builder(this);
        addBaseContextMenuOptions(builder, stream);
        builder.show();
    }

    private void addBaseContextMenuOptions(CustomContextMenu.Builder builder, final StreamResultModel stream) {
        builder.addAction(R.string.add_to_favorites_menu_title, new Runnable() {
            @Override public void run() {
                presenter.addToFavorite(stream);
            }
        }).addAction(R.string.share_via_shootr, new Runnable() {
            @Override public void run() {
                presenter.shareStream(stream);
            }
        }).addAction(R.string.share_via, new Runnable() {
            @Override public void run() {
                shareStream(stream);
            }
        });
    }

    @Override public void showContent() {
        listingList.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.listing_add_stream) public void onAddStream() {
        startActivityForResult(new Intent(this, NewStreamActivity.class), REQUEST_NEW_STREAM);
    }

    @Override public void showEmpty() {
        emptyView.setVisibility(View.VISIBLE);
    }

    @Override public void hideEmpty() {
        emptyView.setVisibility(View.GONE);
    }
}
