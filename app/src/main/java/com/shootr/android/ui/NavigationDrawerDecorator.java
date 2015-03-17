package com.shootr.android.ui;

import android.app.Activity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.shootr.android.R;

public class NavigationDrawerDecorator implements ViewContainerDecorator {

    private final Activity activity;
    private DrawerLayout drawerLayout;
    private View navDrawerItemsListContainer;

    public NavigationDrawerDecorator(Activity activity) {
        this.activity = activity;
    }

    @Override public ViewGroup decorateContainer(ViewGroup originalRoot) {
        View inflatedView = LayoutInflater.from(activity).inflate(R.layout.activity_navdrawer_decorator, originalRoot, true);
        drawerLayout = (DrawerLayout) inflatedView.findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        navDrawerItemsListContainer = drawerLayout.findViewById(R.id.navdrawer_items_list);
        ViewGroup newContainer = (ViewGroup) inflatedView.findViewById(R.id.navdrawer_content);
        populateMenu();
        return newContainer;
    }

    public void populateMenu() {

    }

    public void bindToolbar(Toolbar toolbar) {
        toolbar.setNavigationIcon(R.drawable.ic_drawer);
        ActionBarDrawerToggle drawerToggle =
          new ActionBarDrawerToggle(activity, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.setDrawerListener(drawerToggle);
    }
}
