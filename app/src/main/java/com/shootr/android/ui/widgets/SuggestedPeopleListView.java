package com.shootr.android.ui.widgets;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.shootr.android.R;
import com.shootr.android.ui.adapters.UserListAdapter;

public class SuggestedPeopleListView extends FrameLayout {

    @Bind(R.id.suggested_people_list) LinearLayout suggestedPeopleList;

    private UserListAdapter userListAdapter;

    public SuggestedPeopleListView(Context context) {
        super(context);
        init();
    }

    public SuggestedPeopleListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SuggestedPeopleListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.include_suggested_people, this, true);
        ButterKnife.bind(this);
    }

    public void setAdapter(UserListAdapter adapter) {
        userListAdapter = adapter;
        userListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override public void onChanged() {
                renderUsers();
            }
        });
        userListAdapter.notifyDataSetChanged();
    }

    private void renderUsers() {
        suggestedPeopleList.removeAllViews();
        for (int i = 0; i < userListAdapter.getCount(); i++) {
            View itemView = userListAdapter.getView(i, null, suggestedPeopleList);
            suggestedPeopleList.addView(itemView);
        }
    }
}
