package com.shootr.android.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ShareCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.shootr.android.R;
import com.shootr.android.ui.ToolbarDecorator;
import com.shootr.android.ui.adapters.ListingStreamsAdapter;
import com.shootr.android.ui.adapters.listeners.OnFavoriteClickListener;
import com.shootr.android.ui.adapters.listeners.OnStreamClickListener;
import com.shootr.android.ui.model.StreamResultModel;
import com.shootr.android.ui.presenter.ListingListPresenter;
import com.shootr.android.ui.views.ListingView;
import com.shootr.android.util.CustomContextMenu;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import java.util.List;
import javax.inject.Inject;

public class ListingActivity extends BaseToolbarDecoratedActivity implements ListingView {

    private static final String EXTRA_ID_USER = "idUser";
    public static final int REQUEST_NEW_STREAM = 3;

    @Bind(R.id.listing_list) RecyclerView listingList;
    @Bind(R.id.listing_loading) View loadingView;
    @Bind(R.id.listing_empty_title) View emptyView;

    @Inject ListingListPresenter presenter;

    private ListingStreamsAdapter adapter;

    public static Intent getIntent(Context context, String idUser) {
        Intent intent = new Intent(context, ListingActivity.class);
        intent.putExtra(EXTRA_ID_USER, idUser);
        return intent;
    }

    @Override protected int getLayoutResource() {
        return R.layout.activity_listing;
    }

    @Override protected void initializeViews(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        adapter = new ListingStreamsAdapter(picasso, new OnStreamClickListener() {
            @Override public void onStreamClick(StreamResultModel stream) {
                presenter.selectStream(stream);
            }
            @Override
            public boolean onStreamLongClick(StreamResultModel stream) {
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
        });

        listingList.setAdapter(adapter);
        listingList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override protected void initializePresenter() {
        Intent intent = getIntent();
        String idUser = intent.getStringExtra(EXTRA_ID_USER);
        presenter.initialize(this, idUser);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_NEW_STREAM && resultCode == Activity.RESULT_OK) {
            String streamId = data.getStringExtra(NewStreamActivity.KEY_STREAM_ID);
            presenter.streamCreated(streamId);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.pause();
    }

    private void openContextualMenu(final StreamResultModel stream) {
        new CustomContextMenu.Builder(this)
          .addAction(getString(R.string.add_to_favorites_menu_title), new Runnable() {
              @Override
              public void run() {
                  presenter.addToFavorite(stream);
              }
          })
          .addAction((getString(R.string.recomment_via_shootr)), new Runnable() {
              @Override public void run() {
                  presenter.recommendStream(stream);
              }
          })
          .addAction((getString(R.string.recommend_via)), new Runnable() {
              @Override public void run() {
                  if (stream.getStreamModel().getPicture() != null) {
                      shareStreamWithImage(stream);
                  } else {
                      shareStream(stream);
                  }
              }
          }).show();
    }

    private void shareStream(StreamResultModel stream) {
        Intent intent = ShareCompat.IntentBuilder.from(this)
          .setType("text/plain")
          .setText(String.format(this.getString(R.string.recommend_stream_message),
            stream.getStreamModel().getTitle(),
            stream.getStreamModel().getIdStream()))
          .setChooserTitle(getString(R.string.recommend_via))
          .createChooserIntent();
        startActivity(intent);
    }

    private void shareStreamWithImage(final StreamResultModel stream) {
        picasso.load(stream.getStreamModel().getPicture()).into(new Target() {
            @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT,
                  String.format(getString(R.string.recommend_stream_message),
                    stream.getStreamModel().getTitle(),
                    stream.getStreamModel().getIdStream()));
                String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "", null);
                Uri streamImageUri = Uri.parse(path);
                intent.putExtra(Intent.EXTRA_STREAM, streamImageUri);
                intent.setType("image/*");
                startActivity(Intent.createChooser(intent, getString(R.string.recommend_via)));
            }

            @Override public void onBitmapFailed(Drawable errorDrawable) {
                //TODO handle error
            }

            @Override public void onPrepareLoad(Drawable placeHolderDrawable) {
                /* no-op */
            }
        });
    }

    @Override public void renderStreams(List<StreamResultModel> streams) {
        adapter.setStreams(streams);
    }

    @Override public void navigateToStreamTimeline(String idStream, String title) {
        startActivity(StreamTimelineActivity.newIntent(this, idStream, title));
    }

    @Override public void hideLoading() {
        loadingView.setVisibility(View.GONE);
    }

    @Override public void showError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override public void showLoading() {
        loadingView.setVisibility(View.VISIBLE);
    }

    @Override public void setFavoriteStreams(List<StreamResultModel> favoriteStreams) {
        adapter.setFavoriteStreams(favoriteStreams);
    }

    @Override public void hideContent() {
        listingList.setVisibility(View.GONE);
    }

    @Override public void navigateToCreatedStreamDetail(String streamId) {
        startActivity(StreamDetailActivity.getIntent(this, streamId));
    }

    @Override public void showStreamRecommended() {
        Toast.makeText(this, getString(R.string.stream_recommended_notification), Toast.LENGTH_SHORT).show();
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
