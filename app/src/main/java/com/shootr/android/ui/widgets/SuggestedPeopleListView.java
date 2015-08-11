package com.shootr.android.ui.widgets;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.shootr.android.R;
import com.shootr.android.ui.adapters.UserListAdapter;
import com.shootr.android.ui.adapters.listeners.OnUserClickListener;
import com.shootr.android.ui.model.UserModel;

public class SuggestedPeopleListView extends FrameLayout {

    @Bind(R.id.suggested_people_list) LinearLayout suggestedPeopleList;
    @Bind(R.id.suggested_people_title) TextView suggestedPeopleTitle;

    private UserListAdapter userListAdapter;
    private OnUserClickListener onUserClickListener;

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

    public void setOnUserClickListener(OnUserClickListener onUserClickListener) {
        this.onUserClickListener = onUserClickListener;
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
            final int position = i;
            View itemView = userListAdapter.getView(i, null, suggestedPeopleList);
            itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserModel user = userListAdapter.getItem(position);
                    onUserClickListener.onUserClick(user.getIdUser());
                }
            });
            //TODO set selectable background
            suggestedPeopleList.addView(itemView);
        }
        if(userListAdapter.getCount() > 0) {
            suggestedPeopleTitle.setVisibility(VISIBLE);
        }
    }
}
