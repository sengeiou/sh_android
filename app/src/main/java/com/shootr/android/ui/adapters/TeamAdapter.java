package com.shootr.android.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.shootr.android.R;
import java.util.ArrayList;
import java.util.List;

public class TeamAdapter extends BindableAdapter<String>{

    List<String> teams;

    public TeamAdapter(Context context) {
        super(context);
        teams = new ArrayList<>(0);
    }

    public void setContent(List<String> teams) {
        this.teams = teams;
        this.notifyDataSetChanged();
    }

    @Override public int getCount() {
        return teams.size();
    }

    @Override public String getItem(int position) {
        return teams.get(position);
    }

    @Override public long getItemId(int position) {
        return position;
    }

    @Override public View newView(LayoutInflater inflater, int position, ViewGroup container) {
        return inflater.inflate(R.layout.item_list_search_team, container, false);
    }

    @Override public void bindView(String item, int position, View view) {
        TextView name = (TextView) view.findViewById(R.id.team_name);
        name.setText(item);
    }
}
