package gm.mobi.android.ui.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import gm.mobi.android.R;
import java.util.List;

import static butterknife.ButterKnife.findById;

public class MenuAdapter extends BindableAdapter<MenuAdapter.MenuItem> {

    public static final int TYPE_ITEM = 1;
    private int mSelectedPosition = -1;
    private List<MenuItem> mItems;

    public MenuAdapter(Context context, List<MenuItem> items) {
        super(context);
        mItems = items;
    }

    public void setSelectedPosition(int position) {
        //TODO Sucks monkeyballs. The ideal thing would be to implement custom checkable views, and check them through the ListView
        mSelectedPosition = position;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public MenuItem getItem(int position) {
        if (position == getCount()) {
            return null;
        }
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_ITEM;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEnabled(int position) {
        return getItemViewType(position) > 0;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public void bindView(MenuItem listItem, int position, View view) {
        switch (getItemViewType(position)) {
            case TYPE_ITEM:
                TitleAndIconMenuItem menuItem = (TitleAndIconMenuItem) listItem;
                TextView itemTitle = findById(view, R.id.drawer_item_text);
                ImageView itemIcon = findById(view, R.id.drawer_item_icon);
                itemTitle.setText(menuItem.title);
                //itemIcon.setImageResource(menuItem.icon);
                //setSelectedStateToItem(position == mSelectedPosition, itemTitle);
                break;
        }
    }

    private void setSelectedStateToItem(boolean selectedStateEnabled, TextView titleView) {
        if (selectedStateEnabled) {
            titleView.setTextAppearance(getContext(), R.style.MenuDrawer_Widget_TitleText_Selected);
        } else {
            titleView.setTextAppearance(getContext(), R.style.MenuDrawer_Widget_TitleText);
        }
    }

    @Override
    public View newView(LayoutInflater inflater, int position, ViewGroup container) {
        switch (getItemViewType(position)) {
            case TYPE_ITEM:
                return inflater.inflate(R.layout.drawer_item, container, false);
            default:
                return null;
        }
    }


    public static interface MenuItem {
    }

    public static class TitleAndIconMenuItem implements MenuItem {
        public String title;
        int icon;

        public TitleAndIconMenuItem(String title, int iconRes) {
            this.title = title;
            icon = iconRes;
        }
    }

    public static class FragmentMenuItem extends TitleAndIconMenuItem {

        public Class fragmentClass;
        public Bundle extras;

        public FragmentMenuItem(String title, int iconRes, Class fragmentClass) {
            super(title, iconRes);
            this.fragmentClass = fragmentClass;
        }

        public FragmentMenuItem(String title, int iconRes, Class fragmentClass, Bundle extras) {
            this(title, iconRes, fragmentClass);
            this.extras = extras;
        }
    }
}