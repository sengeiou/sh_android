package com.shootr.android.ui.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;
import com.shootr.android.util.PicassoWrapper;
import com.squareup.picasso.Picasso;
import com.shootr.android.R;
import com.shootr.android.ui.model.MatchModel;
import com.shootr.android.ui.model.UserWatchingModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class InfoListAdapter extends BindableAdapter<Object> {

    private static final int TYPE_COUNT = 3;

    private static final int TYPE_MATCH = 0;
    private static final int TYPE_USER = 1;
    private static final int TYPE_ME = 2;

    private List<Object> itemsList;
    private PicassoWrapper picasso;
    private View.OnClickListener editButtonListener;
    private Long currentUserId;
    private int watchingColorLive;
    private int watchingColorNotLive;
    private final String watchingText;
    private final String notWatchingText;

    public InfoListAdapter(Context context, PicassoWrapper picasso, Long currentUserId, View.OnClickListener editButtonListener) {
        super(context);
        this.picasso = picasso;
        this.editButtonListener = editButtonListener;
        this.itemsList = new ArrayList<>();
        this.currentUserId = currentUserId;
        Resources resources = context.getResources();
        this.watchingColorNotLive = resources.getColor(R.color.watching_not_live);
        this.watchingText = resources.getString(R.string.watching_text);
        this.notWatchingText = resources.getString(R.string.watching_not_text);
    }

    public void setContent(Map<MatchModel, Collection<UserWatchingModel>> itemsMap) {
        this.itemsList.clear();

        for (MatchModel match : itemsMap.keySet()) {
            itemsList.add(match);
            itemsList.addAll(itemsMap.get(match));
        }
        notifyDataSetChanged();
    }

    @Override public int getCount() {
        return itemsList.size();
    }

    @Override public Object getItem(int position) {
        return itemsList.get(position);
    }

    @Override public long getItemId(int position) {
        return position;
    }

    @Override public int getViewTypeCount() {
        return TYPE_COUNT;
    }

    @Override public int getItemViewType(int position) {
        Object item = getItem(position);
        if (item instanceof MatchModel) {
            return TYPE_MATCH;
        } else if (item instanceof UserWatchingModel) {
            if (isCurrentUser((UserWatchingModel) item)) {
                return TYPE_ME;
            }
            return TYPE_USER;
        } else {
            throwItemUnknownException(position, item.getClass().getName());
            return -1;
        }
    }

    private boolean isCurrentUser(UserWatchingModel item) {
        return currentUserId.equals(item.getIdUser());
    }

    @Override public boolean areAllItemsEnabled() {
        return false;
    }

    @Override public boolean isEnabled(int position) {
        int itemViewType = getItemViewType(position);
        return itemViewType == TYPE_USER || itemViewType == TYPE_ME;
    }

    @Override public View newView(LayoutInflater inflater, int position, ViewGroup container) {
        View v = null;
        switch (getItemViewType(position)) {
            case TYPE_MATCH:
                v = inflater.inflate(R.layout.info_header_view, container, false);
                v.setTag(new HeaderViewHolder(v));
                break;
            case TYPE_USER:
                v = inflater.inflate(R.layout.item_list_info_user, container, false);
                v.setTag(new UserViewHolder(v));
                break;
            case TYPE_ME:
                v = inflater.inflate(R.layout.item_list_info_user_join, container, false);
                v.setTag(new UserViewHolder(v, editButtonListener));
                break;
            default:
                throwItemUnknownException(position, "???");
        }
        return v;
    }

    @Override public void bindView(Object item, int position, View view) {
        switch (getItemViewType(position)) {
            case TYPE_MATCH:
                bindMatch((MatchModel) item, position, view);
                break;
            case TYPE_USER:
            case TYPE_ME:
                bindUser((UserWatchingModel) item, position, view);
                break;
            default:
                break;
        }
    }

    public void bindMatch(MatchModel match, int position, View view) {
        HeaderViewHolder vh = (HeaderViewHolder) view.getTag();
        vh.title.setText(match.getTitle());
        vh.timestamp.setText(match.getDatetime());
    }

    public void bindUser(UserWatchingModel user, int position, View view) {
        UserViewHolder vh = (UserViewHolder) view.getTag();
        vh.position = position;
        vh.name.setText(user.getUserName());
        String subtitle;
        if (user.isWatching()) {
            if (user.getPlace() != null) {
                subtitle = user.getPlace();
            } else {
                subtitle = watchingText;
            }
        } else {
            subtitle = notWatchingText;
        }
        vh.watching.setText(subtitle);
        picasso.load(user.getPhoto()).into(vh.avatar);
        if (vh.edit != null) {
            vh.edit.setTag(vh);
        }
    }

    public MatchModel getMatchCorrespondingToItem(int position) {
        boolean isItemMatchType = false;
        boolean moreItemsAbove = true;
        Object currentItem = null;
        int currentPosition = position;
        while (!isItemMatchType && moreItemsAbove) {
            currentItem = getItem(currentPosition );
            isItemMatchType = currentItem instanceof MatchModel;
            moreItemsAbove = currentPosition  > 0;
            currentPosition--;
        }

        if (isItemMatchType) {
            return (MatchModel) currentItem;
        } else {
            return null;
        }
    }

    private void throwItemUnknownException(int position, String className) {
        throw new RuntimeException(
          "Item's type " + className + " at position " + position + " does not match any View Type");
    }

    public static class HeaderViewHolder {
        @InjectView(R.id.info_header_match_title) TextView title;
        @InjectView(R.id.info_header_match_timestamp) TextView timestamp;

        public HeaderViewHolder(View view) {
            ButterKnife.inject(this, view);
        }

    }
    public static class UserViewHolder {
        @InjectView(R.id.info_user_avatar) ImageView avatar;
        @InjectView(R.id.info_user_name) TextView name;
        @InjectView(R.id.info_user_watching) TextView watching;
        @Optional @InjectView(R.id.info_user_edit) View edit;
        public int position;

        public UserViewHolder(View view) {
            ButterKnife.inject(this, view);
        }

        public UserViewHolder(View view, View.OnClickListener editButtonClickListener) {
            this(view);
            edit.setOnClickListener(editButtonClickListener);
        }

    }
}
