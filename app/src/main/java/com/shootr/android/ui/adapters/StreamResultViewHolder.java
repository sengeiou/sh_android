package com.shootr.android.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.shootr.android.R;
import com.shootr.android.ui.adapters.listeners.OnStreamClickListener;
import com.shootr.android.ui.model.StreamResultModel;
import com.shootr.android.util.PicassoWrapper;

public class StreamResultViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{

    private static final String[] CONTEXT_MENU_OPTIONS = {"Add to Favorites", "Cancel"};

    private final OnStreamClickListener onStreamClickListener;
    private final PicassoWrapper picasso;


    @Bind(R.id.stream_picture) ImageView picture;
    @Bind(R.id.stream_title) TextView title;
    @Bind(R.id.stream_author) TextView author;
    @Bind(R.id.stream_watchers) TextView watchers;

    public StreamResultViewHolder(View itemView, OnStreamClickListener onStreamClickListener, PicassoWrapper picasso) {
        super(itemView);
        this.onStreamClickListener = onStreamClickListener;
        this.picasso = picasso;
        ButterKnife.bind(this, itemView);
        itemView.setOnCreateContextMenuListener(this);
    }

    public void render(StreamResultModel streamResultModel) {
        this.setClickListener(streamResultModel);
        title.setText(streamResultModel.getStreamModel().getTitle());
        int watchersCount = streamResultModel.getWatchers();
        if (watchersCount > 0) {
            watchers.setVisibility(View.VISIBLE);
            watchers.setText(getWatchersText(watchersCount));
        } else {
            watchers.setVisibility(View.GONE);
        }
        author.setText(streamResultModel.getStreamModel().getAuthorUsername());

        //TODO usar tamaño predefinido con picasso para mejorar rendimiento
        String pictureUrl = streamResultModel.getStreamModel().getPicture();
        picasso.loadStreamPicture(pictureUrl).into(picture);
    }

    private void setClickListener(final StreamResultModel streamResult) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                onStreamClickListener.onStreamClick(streamResult);
            }
        });
    }

    private String getWatchersText(int watchers) {
        return String.valueOf(watchers);
    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        for (String contextMenuOption : CONTEXT_MENU_OPTIONS) {
            contextMenu.add(0, 0, position, contextMenuOption);
        }
    }
}
