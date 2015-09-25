package com.shootr.android.ui.adapters;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.style.TextAppearanceSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import com.shootr.android.R;
import com.shootr.android.ui.adapters.listeners.OnStreamClickListener;
import com.shootr.android.ui.adapters.listeners.OnUnwatchClickListener;
import com.shootr.android.ui.model.StreamModel;
import com.shootr.android.ui.model.StreamResultModel;
import com.shootr.android.util.ImageLoader;
import com.shootr.android.util.Truss;

import static com.shootr.android.domain.utils.Preconditions.checkNotNull;

public class StreamResultViewHolder extends RecyclerView.ViewHolder {

    private final OnStreamClickListener onStreamClickListener;
    private final ImageLoader imageLoader;
    private OnUnwatchClickListener unwatchClickListener;

    private boolean showsWatchersText = false;
    private boolean isWatchingStateEnabled = false;

    @Bind(R.id.stream_picture) ImageView picture;
    @Bind(R.id.stream_title) TextView title;
    @Bind(R.id.stream_watchers) TextView watchers;
    @Bind(R.id.separator) View separator;
    @Nullable @Bind(R.id.stream_remove) ImageView removeButton;
    @Nullable @Bind(R.id.stream_author) TextView author;
    @Nullable @Bind(R.id.stream_actions_container) View actionsContainer;

    @BindString(R.string.watching_stream_connected) String connected;

    public StreamResultViewHolder(View itemView, OnStreamClickListener onStreamClickListener, ImageLoader imageLoader) {
        super(itemView);
        this.onStreamClickListener = onStreamClickListener;
        this.imageLoader = imageLoader;
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
        renderAuthor(streamResultModel.getStreamModel());
        int watchersCount = streamResultModel.getWatchers();
        if (watchersCount > 0 || showsWatchersText) {
            watchers.setVisibility(View.VISIBLE);
            watchers.setText(getWatchersText(watchersCount));
        } else {
            watchers.setVisibility(View.GONE);
        }

        String pictureUrl = streamResultModel.getStreamModel().getPicture();
        imageLoader.loadStreamPicture(pictureUrl, picture);
        separator.setVisibility(showSeparator ? View.VISIBLE : View.GONE);
    }

    private void setClickListener(final StreamResultModel streamResult) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStreamClickListener.onStreamClick(streamResult);
            }
        });
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return onStreamClickListener.onStreamLongClick(streamResult);
            }
        });
    }

    private void setUnwatchClickListener() {
        checkNotNull(actionsContainer, "The view used in this ViewHolder doesn't contain the unwatch button.");
        actionsContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unwatchClickListener.onUnwatchClick();
            }
        });
    }

    private String getWatchersText(int watchers) {
        if (showsWatchersText) {
            return itemView.getContext()
              .getResources()
              .getQuantityString(R.plurals.listing_watchers, watchers, watchers);
        } else {
            return String.valueOf(watchers);
        }
    }

    protected void renderAuthor(StreamModel stream) {
        if (author != null) {
            if (isWatchingStateEnabled) {
                author.setText(getAuthorWithConnected(stream));
            } else {
                author.setText(getAuthorWithDescription(stream));
            }
        }
    }

    private CharSequence getAuthorWithConnected(StreamModel stream) {
        return new Truss()
          .pushSpan(new TextAppearanceSpan(itemView.getContext(), R.style.InlineConnectedAppearance))
          .append(connected)
          .popSpan()
          .build();
    }

    private CharSequence getAuthorWithDescription(StreamModel stream) {
        if (stream.getDescription() == null) {
            return stream.getAuthorUsername();
        } else {
            return new Truss().append(stream.getAuthorUsername())
              .pushSpan(new TextAppearanceSpan(itemView.getContext(), R.style.InlineDescriptionAppearance))
              .append(" ")
              .append(stream.getDescription())
              .popSpan()
              .build();
        }
    }

    public void setShowsWatchersText(Boolean showWatchersText) {
        this.showsWatchersText = showWatchersText;
    }
}
