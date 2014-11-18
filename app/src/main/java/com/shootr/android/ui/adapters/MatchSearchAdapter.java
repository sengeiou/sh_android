package com.shootr.android.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.shootr.android.R;
import com.shootr.android.ui.model.MatchModel;
import com.shootr.android.ui.model.MatchSearchResultModel;
import java.util.ArrayList;
import java.util.List;

public class MatchSearchAdapter extends BindableAdapter<MatchSearchResultModel> {

    private List<MatchSearchResultModel> items;

    public MatchSearchAdapter(Context context) {
        super(context);
        items = new ArrayList<>(0);
    }

    @Override public int getCount() {
        return items.size();
    }

    @Override public MatchSearchResultModel getItem(int position) {
        return items.get(position);
    }

    @Override public long getItemId(int position) {
        return items.get(position).getIdMatch();
    }

    @Override public View newView(LayoutInflater inflater, int position, ViewGroup container) {
        View view = inflater.inflate(R.layout.item_list_search_match, container, false);
        view.setTag(new ViewHolder(view));
        return view;
    }

    @Override public void bindView(MatchSearchResultModel item, int position, View view) {
        final ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.title.setText(item.getTitle());
        viewHolder.date.setText(item.getDatetime());
        if (item.isAddedAlready()) {
            viewHolder.title.setTextColor(view.getResources().getColor(R.color.disabled));
            viewHolder.date.setTextColor(view.getResources().getColor(R.color.disabled));
        } else {
            viewHolder.title.setTextColor(view.getResources().getColor(R.color.gray_10));
            viewHolder.date.setTextColor(view.getResources().getColor(R.color.gray_30));
        }
    }

    public void setItems(List<MatchSearchResultModel> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override public boolean isEnabled(int position) {
        return !getItem(position).isAddedAlready();
    }

    @Override public boolean areAllItemsEnabled() {
        return true;
    }

    public List<MatchSearchResultModel> getItems() {
        return items;
    }

    public static class ViewHolder {

        @InjectView(R.id.match_title) TextView title;
        @InjectView(R.id.match_date) TextView date;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
