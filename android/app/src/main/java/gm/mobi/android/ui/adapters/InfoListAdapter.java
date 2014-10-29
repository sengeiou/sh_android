package gm.mobi.android.ui.adapters;

import android.content.Context;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.squareup.picasso.Picasso;
import gm.mobi.android.R;
import gm.mobi.android.ui.model.MatchModel;
import gm.mobi.android.ui.model.UserWatchingModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InfoListAdapter extends BindableAdapter<Object> {

    private static final int TYPE_COUNT = 2;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_USER = 1;

    private Map<MatchModel, List<UserWatchingModel>> itemsMap;
    private List<Object> itemsList;
    private Picasso picasso;


    public InfoListAdapter(Context context, Picasso picasso) {
        super(context);
        this.picasso = picasso;
        this.itemsList = new ArrayList<>();
    }

    public void setContent(Map<MatchModel, List<UserWatchingModel>> itemsMap) {
        this.itemsMap = itemsMap;
        this.itemsList.clear();

        for (MatchModel match : itemsMap.keySet()) {
            itemsList.add(match);
            for (UserWatchingModel user : itemsMap.get(match)) {
                itemsList.add(user);
            }
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
            return TYPE_HEADER;
        } else if (item instanceof UserWatchingModel) {
            return TYPE_USER;
        } else {
            throwItemUnknownException(position, item.getClass().getName());
            return -1;
        }
    }

    @Override public boolean areAllItemsEnabled() {
        return false;
    }

    @Override public boolean isEnabled(int position) {
        return getItemViewType(position) == TYPE_USER;
    }

    @Override public View newView(LayoutInflater inflater, int position, ViewGroup container) {
        View v = null;
        switch (getItemViewType(position)) {
            case TYPE_HEADER:
                v = inflater.inflate(R.layout.info_header_view, container, false);
                v.setTag(new HeaderViewHolder(v));
                break;
            case TYPE_USER:
                v = inflater.inflate(R.layout.item_list_info_user, container, false);
                v.setTag(new UserViewHolder(v));
                break;
            default:
                throwItemUnknownException(position, "???");
        }
        return v;
    }

    @Override public void bindView(Object item, int position, View view) {
        switch (getItemViewType(position)) {
            case TYPE_HEADER:
                bindHeader((MatchModel) item, position, view);
                break;
            case TYPE_USER:
                bindUser((UserWatchingModel) item, position, view);
                break;
        }
    }

    public void bindHeader(MatchModel match, int position, View view) {
        HeaderViewHolder vh = (HeaderViewHolder) view.getTag();
        vh.title.setText(match.getTitle());
    }

    public void bindUser(UserWatchingModel user, int position, View view) {
        UserViewHolder vh = (UserViewHolder) view.getTag();
        vh.name.setText(user.getUserName());
        vh.watching.setText(user.getStatus());
        if (!user.getPhoto().isEmpty()) {
            picasso.load(user.getPhoto()).into(vh.avatar);
        } else {
            picasso.load(R.drawable.ic_contact_picture_default);
        }
    }


    private void throwItemUnknownException(int position, String className) {
        throw new RuntimeException(
          "Item's type " + className + " at position " + position + " does not match any View Type");
    }

    public static class HeaderViewHolder {
        @InjectView(R.id.info_header_match_title) TextView title;

        public HeaderViewHolder(View view) {
            ButterKnife.inject(this, view);
        }

    }
    public static class UserViewHolder {
        @InjectView(R.id.info_user_avatar) ImageView avatar;
        @InjectView(R.id.info_user_name) TextView name;
        @InjectView(R.id.info_user_watching) TextView watching;

        public UserViewHolder(View view) {
            ButterKnife.inject(this, view);
        }

    }
}
