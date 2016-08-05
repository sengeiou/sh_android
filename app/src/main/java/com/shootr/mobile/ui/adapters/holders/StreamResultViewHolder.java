package com.shootr.mobile.ui.adapters.holders;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.style.TextAppearanceSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import com.amulyakhare.textdrawable.TextDrawable;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.listeners.OnStreamClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnUnwatchClickListener;
import com.shootr.mobile.ui.model.StreamModel;
import com.shootr.mobile.ui.model.StreamResultModel;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.InitialsLoader;
import com.shootr.mobile.util.Truss;
import java.util.Collections;
import java.util.List;

import static com.shootr.mobile.domain.utils.Preconditions.checkNotNull;

public class StreamResultViewHolder extends RecyclerView.ViewHolder {

    private final OnStreamClickListener onStreamClickListener;
    private final ImageLoader imageLoader;
    private final InitialsLoader initialsLoader;
    private List<String> mutedStreamIds;
    private OnUnwatchClickListener unwatchClickListener;

    private boolean showsFavoritesText = false;
    private boolean isWatchingStateEnabled = false;

    @Bind(R.id.stream_picture) ImageView picture;
    @Bind(R.id.stream_picture_without_text) ImageView pictureWithoutText;
    @Bind(R.id.stream_title) TextView title;
    @Bind(R.id.stream_muted) ImageView mute;
    @Bind(R.id.stream_watchers) TextView watchers;
    @Nullable @Bind(R.id.stream_remove) ImageView removeButton;
    @Nullable @Bind(R.id.stream_subtitle) TextView subtitle;
    @Nullable @Bind(R.id.stream_subtitle_description) TextView subtitleDescription;
    @Nullable @Bind(R.id.stream_actions_container) View actionsContainer;

    @BindString(R.string.watching_stream_connected) String connected;
    @BindString(R.string.watching_stream_connected_muted) String connectedAndMuted;

    public StreamResultViewHolder(View itemView, OnStreamClickListener onStreamClickListener,
        ImageLoader imageLoader, InitialsLoader initialsLoader, List<String> mutedStreamIds) {
        super(itemView);
        this.onStreamClickListener = onStreamClickListener;
        this.imageLoader = imageLoader;
        this.initialsLoader = initialsLoader;
        this.mutedStreamIds = mutedStreamIds;
        ButterKnife.bind(this, itemView);
    }

    public StreamResultViewHolder(View itemView, OnStreamClickListener onStreamClickListener,
        ImageLoader imageLoader, InitialsLoader initialsLoader) {
        super(itemView);
        this.onStreamClickListener = onStreamClickListener;
        this.imageLoader = imageLoader;
        this.initialsLoader = initialsLoader;
        this.mutedStreamIds = Collections.emptyList();
        ButterKnife.bind(this, itemView);
    }

    public void enableWatchingState(OnUnwatchClickListener unwatchClickListener) {
        checkNotNull(removeButton, "The view used in this ViewHolder doesn't contain the unwatch button.");
        checkNotNull(unwatchClickListener);
        this.isWatchingStateEnabled = true;
        this.unwatchClickListener = unwatchClickListener;
        this.removeButton.setVisibility(View.VISIBLE);
        this.setUnwatchClickListener();
    }

    public void disableWatchingState() {
        checkNotNull(removeButton, "The view used in this ViewHolder doesn't contain the unwatch button.");
        unwatchClickListener = null;
        isWatchingStateEnabled = false;
        removeButton.setVisibility(View.GONE);
    }

    public void render(StreamResultModel streamResultModel, boolean showSeparator) {
        this.setClickListener(streamResultModel);
        title.setText(streamResultModel.getStreamModel().getTitle());
        setMutedVisibility(streamResultModel);
        renderSubtitle(streamResultModel.getStreamModel());
        int watchersCount = streamResultModel.getWatchers();
        if (watchersCount > 0 || showsFavoritesText) {
            watchers.setVisibility(View.VISIBLE);
            watchers.setText(getFavoritesText(watchersCount));
        } else {
            watchers.setVisibility(View.GONE);
        }
        setupStreamPicture(streamResultModel);
    }

    public void render(StreamResultModel streamResultModel, boolean showSeparator,
      List<StreamResultModel> favoritedStreams) {
        this.setClickListener(streamResultModel);
        title.setText(streamResultModel.getStreamModel().getTitle());
        setMutedVisibility(streamResultModel);
        renderSubtitle(streamResultModel.getStreamModel());
        int watchersCount = streamResultModel.getWatchers();
        if (watchersCount > 0 || (showsFavoritesText && !favoritedStreams.contains(streamResultModel))) {
            renderHolderSubtitle(streamResultModel);
            if (watchersCount > 0) {
                watchers.setVisibility(View.VISIBLE);
                watchers.setText(String.valueOf(watchersCount));
            } else {
                watchers.setVisibility(View.GONE);
            }
        } else {
            watchers.setVisibility(View.GONE);
        }
        setupStreamPicture(streamResultModel);
    }

    private void setupStreamPicture(StreamResultModel streamResultModel) {
        String pictureUrl = streamResultModel.getStreamModel().getPicture();
        if (pictureUrl != null) {
            picture.setVisibility(View.VISIBLE);
            pictureWithoutText.setVisibility(View.GONE);
            imageLoader.loadStreamPicture(pictureUrl, picture);
        } else {
            picture.setVisibility(View.GONE);
            pictureWithoutText.setVisibility(View.VISIBLE);
            setupInitials(streamResultModel);
        }
    }

    private void setupInitials(StreamResultModel streamResultModel) {
        String initials = initialsLoader.getLetters(streamResultModel.getStreamModel().getTitle());
        int backgroundColor = initialsLoader.getColorForLetters(initials);
        TextDrawable textDrawable = initialsLoader.getTextDrawable(initials, backgroundColor);
        pictureWithoutText.setImageDrawable(textDrawable);
    }

    public void setMutedVisibility(StreamResultModel streamResultModel) {
        if (!isWatchingStateEnabled) {
            if (streamIsMuted(streamResultModel.getStreamModel().getIdStream())) {
                mute.setVisibility(View.VISIBLE);
            } else {
                mute.setVisibility(View.GONE);
            }
        } else {
            mute.setVisibility(View.GONE);
        }
    }

    public boolean streamIsMuted(String idStream) {
        return mutedStreamIds != null && mutedStreamIds.contains(idStream);
    }

    private void setClickListener(final StreamResultModel streamResult) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                onStreamClickListener.onStreamClick(streamResult);
            }
        });
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override public boolean onLongClick(View v) {
                return onStreamClickListener.onStreamLongClick(streamResult);
            }
        });
    }

    private void setUnwatchClickListener() {
        checkNotNull(actionsContainer, "The view used in this ViewHolder doesn't contain the unwatch button.");
        actionsContainer.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                if (unwatchClickListener != null) {
                    unwatchClickListener.onUnwatchClick();
                }
            }
        });
    }

    private String getFavoritesText(int favorites) {
        if (showsFavoritesText) {
            return itemView.getContext()
              .getResources()
              .getQuantityString(R.plurals.listing_favorites, favorites, favorites);
        } else {
            return String.valueOf(favorites);
        }
    }

    protected void renderSubtitle(StreamModel stream) {
        if (isWatchingStateEnabled) {
            setupConnectedSubtitle(stream);
        } else {
            setupAuthorAndDescriptionSubtitle(stream);
        }
    }

    private void setupConnectedSubtitle(StreamModel stream) {
        if (subtitle != null && subtitleDescription != null) {
            subtitle.setText(getConnectedSubtitle(stream));
            subtitleDescription.setVisibility(View.GONE);
        }
    }

    private void setupAuthorAndDescriptionSubtitle(StreamModel stream) {
        if (subtitle != null && subtitleDescription != null) {
            subtitle.setText(stream.getAuthorUsername());
            if (stream.getDescription() != null) {
                subtitleDescription.setVisibility(View.VISIBLE);
                subtitleDescription.setText(stream.getDescription());
            } else {
                subtitleDescription.setVisibility(View.GONE);
            }
        }
    }

    private void renderHolderSubtitle(StreamResultModel stream) {
        if (subtitle != null) {
            if (isWatchingStateEnabled) {
                subtitle.setText(getConnectedSubtitle(stream.getStreamModel()));
            } else {
                String favorites = subtitle.getContext()
                  .getResources()
                  .getQuantityString(R.plurals.listing_favorites,
                    stream.getStreamModel().getTotalFavorites(),
                    stream.getStreamModel().getTotalFavorites());
                subtitle.setText(favorites);
            }
        }
    }

    private CharSequence getConnectedSubtitle(StreamModel stream) {
        if (streamIsMuted(stream.getIdStream())) {
            return new Truss().pushSpan(new TextAppearanceSpan(itemView.getContext(),
              R.style.InlineConnectedAppearance)).append(connectedAndMuted).popSpan().build();
        } else {
            return new Truss().pushSpan(new TextAppearanceSpan(itemView.getContext(),
              R.style.InlineConnectedAppearance)).append(connected).popSpan().build();
        }
    }

    public void setShowsFavoritesText(Boolean showFavoritesText) {
        this.showsFavoritesText = showFavoritesText;
    }

    public void setMutedStreamIds(List<String> mutedStreamsIds) {
        this.mutedStreamIds = mutedStreamsIds;
    }
}
