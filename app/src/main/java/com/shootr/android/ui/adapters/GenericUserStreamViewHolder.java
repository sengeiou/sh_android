package com.shootr.android.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.shootr.android.R;
import com.shootr.android.ui.adapters.listeners.OnStreamClickListener;
import com.shootr.android.ui.model.StreamResultModel;
import com.shootr.android.util.PicassoWrapper;

public class GenericUserStreamViewHolder extends RecyclerView.ViewHolder{

    private final OnStreamClickListener onStreamClickListener;
    private final PicassoWrapper picasso;

    @Bind(R.id.stream_picture) ImageView picture;
    @Bind(R.id.stream_title) TextView title;
    @Bind(R.id.stream_watchers) TextView watchers;
    @Bind(R.id.separator) View separator;

    public GenericUserStreamViewHolder(View itemView, OnStreamClickListener onStreamClickListener,
      PicassoWrapper picasso) {
        super(itemView);
        this.onStreamClickListener = onStreamClickListener;
        this.picasso = picasso;
        ButterKnife.bind(this, itemView);
    }

    public void render(StreamResultModel streamResultModel, boolean showSeparator) {
        this.setClickListener(streamResultModel);
        title.setText(streamResultModel.getStreamModel().getTitle());
        int watchersCount = streamResultModel.getWatchers();
        if (watchersCount > 0) {
            watchers.setVisibility(View.VISIBLE);
            watchers.setText(getWatchersText(watchersCount));
        } else {
            watchers.setVisibility(View.GONE);
        }

        //TODO usar tamaño predefinido con picasso para mejorar rendimiento
        String pictureUrl = streamResultModel.getStreamModel().getPicture();
        picasso.loadStreamPicture(pictureUrl).into(picture);
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

    private String getWatchersText(int watchers) {
        return String.valueOf(watchers);
    }

}
