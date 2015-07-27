package com.shootr.android.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.shootr.android.R;
import com.shootr.android.ui.adapters.listeners.OnStreamClickListener;
import com.shootr.android.ui.model.StreamResultModel;
import com.shootr.android.util.PicassoWrapper;
import java.util.Collections;
import java.util.List;

public class FavoriteStreamsAdapter extends RecyclerView.Adapter<StreamResultViewHolder> {

    private final PicassoWrapper picasso;

    private OnStreamClickListener onStreamClickListener;
    private List<StreamResultModel> streams = Collections.EMPTY_LIST;

    public FavoriteStreamsAdapter(PicassoWrapper picasso, OnStreamClickListener onStreamClickListener) {
        this.picasso = picasso;
        this.onStreamClickListener = onStreamClickListener;
    }

    public void setStreams(List<StreamResultModel> streams) {
        boolean wasEmpty = this.streams.isEmpty();
        this.streams = streams;
        if (wasEmpty) {
            notifyItemRangeInserted(0, streams.size());
        } else {
            notifyDataSetChanged();
        }
    }

    @Override
    public StreamResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_stream, parent, false);
        return new StreamResultViewHolder(view, onStreamClickListener, picasso);
    }

    @Override
    public void onBindViewHolder(StreamResultViewHolder holder, int position) {
        StreamResultModel streamResultModel = streams.get(position);
        holder.render(streamResultModel);
    }

    @Override
    public int getItemCount() {
        return streams.size();
    }
}
