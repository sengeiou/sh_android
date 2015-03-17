package com.shootr.android.ui;

import android.app.Activity;
import android.os.Handler;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
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

    private static final int NAVDRAWER_LAUNCH_DELAY = 250;

    private static final int MAIN_CONTENT_FADEOUT_DURATION = 150;
    private static final int MAIN_CONTENT_FADEIN_DURATION = 250;

    private static final int[] NAV_DRAWER_ICON_RES =
      { R.drawable.ic_drawer_timeline_mask, R.drawable.ic_drawer_people_mask };
    private static final int[] NAV_DRAWER_TITLE_RES = { R.string.drawer_timeline_title, R.string.drawer_people_title };

    private final Activity activity;
    private DrawerLayout drawerLayout;
    private ViewGroup navDrawerItemsListContainer;
    private View[] navDrawerItemViews;
    private final int currentNavigationDrawerItem;
    private OnNavDrawerItemClickedListener clickListener;
    private Handler mainThreadHanlder;
    private ViewGroup decoratedContainer;

    public NavigationDrawerDecorator(Activity activity, int currentNavigationDrawerItem) {
        this.activity = activity;
        this.currentNavigationDrawerItem = currentNavigationDrawerItem;
        this.mainThreadHanlder = new Handler();
    }

    @Override public ViewGroup decorateContainer(ViewGroup originalRoot) {
        View inflatedView =
          LayoutInflater.from(activity).inflate(R.layout.activity_navdrawer_decorator, originalRoot, true);
        drawerLayout = (DrawerLayout) inflatedView.findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        navDrawerItemsListContainer = (ViewGroup) drawerLayout.findViewById(R.id.navdrawer_items_list);
        decoratedContainer = (ViewGroup) inflatedView.findViewById(R.id.navdrawer_content);
        populateMenu();
        return decoratedContainer;
    }

    public void fadeOutContent() {
        decoratedContainer.animate().alpha(0).setDuration(MAIN_CONTENT_FADEOUT_DURATION);
    }

    public void fadeInContent() {
        decoratedContainer.setAlpha(0);
        decoratedContainer.animate().alpha(1).setDuration(MAIN_CONTENT_FADEIN_DURATION);
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

    private void onNavDrawerItemClicked(final int itemId) {
        if (isSelected(itemId)) {
            drawerLayout.closeDrawer(Gravity.START);
            return;
        }

        if (clickListener == null) {
            Timber.d("Clicked navdrawer item, but no listener is setted");
            return;
        }

        // launch the target Activity after a short delay, to allow the close animation to play
        mainThreadHanlder.postDelayed(new Runnable() {
            @Override
            public void run() {
                clickListener.goToNavDrawerItem(itemId);
            }
        }, NAVDRAWER_LAUNCH_DELAY);

        // change the active item on the list so the user can see the item changed
        setSelectedNavDrawerItem(itemId);
        // fade out the main content
        fadeOutContent();

        drawerLayout.closeDrawer(Gravity.START);
    }

    private void setSelectedNavDrawerItem(int itemId) {
        if (navDrawerItemViews != null) {
            /*for (int i = 0; i < navDrawerItemViews.length; i++) {
                if (i < mNavDrawerItems.size()) {
                    int thisItemId = mNavDrawerItems.get(i);
                    formatNavDrawerItem(mNavDrawerItemViews[i], thisItemId, itemId == thisItemId);
                }
            }*/
        }
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

    public void setNavDrawerItemClickListener(OnNavDrawerItemClickedListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface OnNavDrawerItemClickedListener {

        void goToNavDrawerItem(int itemId);
    }
}
