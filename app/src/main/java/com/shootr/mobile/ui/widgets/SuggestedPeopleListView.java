package com.shootr.mobile.ui.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.UserListAdapter;
import com.shootr.mobile.ui.adapters.listeners.OnUserClickListener;
import com.shootr.mobile.ui.model.UserModel;

public class SuggestedPeopleListView extends FrameLayout {

    @BindView(R.id.suggested_people_list) LinearLayout suggestedPeopleList;
    @BindView(R.id.suggested_people_title) TextView suggestedPeopleTitle;
    private UserListAdapter userListAdapter;
    private OnUserClickListener onUserClickListener;
    private Drawable selectableBackground;

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
        TypedArray a = getContext().getTheme().obtainStyledAttributes(new int[] { R.attr.selectableItemBackground });
        selectableBackground = a.getDrawable(0);
        a.recycle();
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
                @Override public void onClick(View v) {
                    UserModel user = userListAdapter.getItem(position);
                    onUserClickListener.onUserClick(user.getIdUser());
                }
            });
            setItemBackgroundRetainPaddings(itemView);
            suggestedPeopleList.addView(itemView);
        }
        if (userListAdapter.getCount() > 0) {
            suggestedPeopleTitle.setVisibility(VISIBLE);
        } else {
            suggestedPeopleTitle.setVisibility(GONE);
        }
    }

    private void setItemBackgroundRetainPaddings(View itemView) {
        int paddingBottom = itemView.getPaddingBottom();
        int paddingLeft = itemView.getPaddingLeft();
        int paddingRight = itemView.getPaddingRight();
        int paddingTop = itemView.getPaddingTop();
        itemView.setBackgroundDrawable(selectableBackground.getConstantState().newDrawable());
        itemView.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
    }
}
