package com.shootr.android.ui;

import android.app.Activity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.TextView;
import com.shootr.android.R;
import java.util.ArrayList;
import java.util.List;
import timber.log.Timber;

public class NavigationDrawerDecorator implements ViewContainerDecorator {

    public static final int NAVDRAWER_ITEM_TIMELINE = 0;
    public static final int NAVDRAWER_ITEM_PEOPLE = 1;

    private static final int[] NAV_DRAWER_ICON_RES =
      { R.drawable.ic_drawer_timeline_mask, R.drawable.ic_drawer_people_mask };
    private static final int[] NAV_DRAWER_TITLE_RES = { R.string.drawer_timeline_title, R.string.drawer_people_title };

    private final Activity activity;
    private DrawerLayout drawerLayout;
    private ViewGroup navDrawerItemsListContainer;
    private View[] navDrawerItemViews;
    private final int currentNavigationDrawerItem;

    public NavigationDrawerDecorator(Activity activity, int currentNavigationDrawerItem) {
        this.activity = activity;
        this.currentNavigationDrawerItem = currentNavigationDrawerItem;
    }

    @Override public ViewGroup decorateContainer(ViewGroup originalRoot) {
        View inflatedView =
          LayoutInflater.from(activity).inflate(R.layout.activity_navdrawer_decorator, originalRoot, true);
        drawerLayout = (DrawerLayout) inflatedView.findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        navDrawerItemsListContainer = (ViewGroup) drawerLayout.findViewById(R.id.navdrawer_items_list);
        ViewGroup newContainer = (ViewGroup) inflatedView.findViewById(R.id.navdrawer_content);
        populateMenu();
        return newContainer;
    }

    public void populateMenu() {
        List<Integer> navItemList = new ArrayList<>();
        navItemList.add(NAVDRAWER_ITEM_TIMELINE);
        navItemList.add(NAVDRAWER_ITEM_PEOPLE);
        createNavDrawerItems(navItemList);
    }

    private void createNavDrawerItems(List<Integer> navDrawerItems) {
        if (navDrawerItemsListContainer == null) {
            return;
        }
        navDrawerItemsListContainer.removeAllViews();
        navDrawerItemViews = new View[navDrawerItems.size()];
        for (int i = 0; i < navDrawerItems.size(); i++) {
            int itemId = navDrawerItems.get(i);
            navDrawerItemViews[i] = makeNavDraweItem(itemId, navDrawerItemsListContainer);
            navDrawerItemsListContainer.addView(navDrawerItemViews[i]);
        }
    }

    private View makeNavDraweItem(final int itemId, ViewGroup parent) {
        View itemView = LayoutInflater.from(activity).inflate(R.layout.drawer_item, parent, false);
        ImageView iconView = (ImageView) itemView.findViewById(R.id.drawer_item_icon);
        TextView textView = (TextView) itemView.findViewById(R.id.drawer_item_text);

        int iconRes = NAV_DRAWER_ICON_RES[itemId];
        if (iconRes > 0) {
            iconView.setImageResource(iconRes);
        } else {
            iconView.setVisibility(View.GONE);
        }

        int titleRes = NAV_DRAWER_TITLE_RES[itemId];
        textView.setText(titleRes);

        formatNavDrawerItem(itemView, itemId);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                onNavDrawerItemClicked(itemId);
            }
        });
        return itemView;
    }

    private void onNavDrawerItemClicked(int itemId) {
        //TODO
        Timber.d("Click: %d", itemId);
    }

    private void formatNavDrawerItem(View itemView, int itemId) {
        boolean selected = isSelected(itemId);

        if (selected) {
            itemView.setBackgroundResource(R.drawable.selected_navdrawer_item_background);
        }

        ImageView iconView = (ImageView) itemView.findViewById(R.id.drawer_item_icon);
        iconView.setColorFilter(selected ? activity.getResources().getColor(R.color.navdrawer_icon_tint_selected)
          : activity.getResources().getColor(R.color.navdrawer_icon_tint));

        Checkable checkable = (Checkable) itemView;
        checkable.setChecked(selected);
    }

    private boolean isSelected(int itemId) {
        return itemId == currentNavigationDrawerItem;
    }

    public void bindToolbar(Toolbar toolbar) {
        toolbar.setNavigationIcon(R.drawable.ic_drawer);
        ActionBarDrawerToggle drawerToggle =
          new ActionBarDrawerToggle(activity, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.setDrawerListener(drawerToggle);
    }
}
