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
import java.util.ArrayList;
import java.util.List;

public class MatchSearchAdapter extends BindableAdapter<MatchModel> {

    private List<MatchModel> items;

    public MatchSearchAdapter(Context context) {
        super(context);
        items = new ArrayList<>(0);
    }

    @Override public int getCount() {
        return items.size();
    }

    @Override public MatchModel getItem(int position) {
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

    @Override public void bindView(MatchModel item, int position, View view) {
        final ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.title.setText(item.getTitle());
        viewHolder.date.setText(item.getDatetime());
    }

    public void setItems(List<MatchModel> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public List<MatchModel> getItems() {
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
