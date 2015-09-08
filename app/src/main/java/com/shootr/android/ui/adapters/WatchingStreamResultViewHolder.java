package com.shootr.android.ui.adapters;

import android.text.style.TextAppearanceSpan;
import android.view.View;
import android.widget.ImageView;
import butterknife.Bind;
import butterknife.BindString;
import com.shootr.android.R;
import com.shootr.android.ui.adapters.listeners.OnStreamClickListener;
import com.shootr.android.ui.adapters.listeners.OnUnwatchClickListener;
import com.shootr.android.ui.model.StreamModel;
import com.shootr.android.ui.model.StreamResultModel;
import com.shootr.android.util.ImageLoader;
import com.shootr.android.util.Truss;

public class WatchingStreamResultViewHolder extends StreamResultViewHolder {

    private final OnUnwatchClickListener unwatchClickListener;

    @Bind(R.id.stream_remove) ImageView remove;
    @BindString(R.string.watching_stream_connected) String connected;

    public WatchingStreamResultViewHolder(View itemView, OnStreamClickListener onStreamClickListener,
      ImageLoader imageLoader, OnUnwatchClickListener unwatchClickListener) {
        super(itemView, onStreamClickListener, imageLoader);
        this.unwatchClickListener = unwatchClickListener;
    }

    @Override
    public void render(StreamResultModel streamResultModel, boolean showSeparator) {
        super.render(streamResultModel, showSeparator);
        remove.setVisibility(View.VISIBLE);
        renderAuthor(streamResultModel.getStreamModel());
        setUnwatchClickListener();
    }

    private void setUnwatchClickListener() {
        remove.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                unwatchClickListener.onUnwatchClick();
            }
        });
    }

    private void renderAuthor(StreamModel stream) {
        if (author != null) {
            CharSequence authorText = new Truss().append(stream.getAuthorUsername())
              .append(" · ")
              .pushSpan(new TextAppearanceSpan(itemView.getContext(), R.style.InlineConnectedAppearance))
              .append(connected)
              .popSpan()
              .build();
            author.setText(authorText);
        }
    }
}
