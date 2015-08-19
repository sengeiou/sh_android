package com.shootr.android.ui.adapters;

import android.view.View;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.shootr.android.R;
import com.shootr.android.ui.adapters.listeners.OnStreamClickListener;
import com.shootr.android.util.PicassoWrapper;

public class StreamResultViewHolder extends CurrentUserStreamViewHolder{

    @Bind(R.id.stream_author) TextView author;

    public StreamResultViewHolder(View itemView, OnStreamClickListener onStreamClickListener, PicassoWrapper picasso) {
        super(itemView, onStreamClickListener, picasso);
        ButterKnife.bind(this, itemView);
    }

    public void renderAuthor(String authorUsername) {
        author.setText(authorUsername);
    }
}
