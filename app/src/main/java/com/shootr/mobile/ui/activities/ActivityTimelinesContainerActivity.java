package com.shootr.mobile.ui.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;

import com.shootr.mobile.R;
import com.shootr.mobile.ui.ToolbarDecorator;
import com.shootr.mobile.ui.fragments.ActivityTimelineFragment;
import com.shootr.mobile.ui.fragments.MeActivityTimelineFragment;
import com.shootr.mobile.util.BackStackHandler;

import java.util.Locale;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ActivityTimelinesContainerActivity extends BaseToolbarDecoratedActivity {

    @Bind(R.id.activity_pager)
    ViewPager viewPager;
    @Bind(R.id.activity_tab_layout)
    TabLayout tabLayout;

    @Inject BackStackHandler backStackHandler;

    private ToolbarDecorator toolbarDecorator;

    @Override
    protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
        this.toolbarDecorator = toolbarDecorator;
        this.toolbarDecorator.getActionBar().setDisplayShowHomeEnabled(true);
        this.toolbarDecorator.getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_fragment_container;
    }

    @Override
    protected void initializeViews(Bundle savedInstanceState) {
        ButterKnife.bind(this);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.setPageMargin(getResources().getDimensionPixelOffset(R.dimen.view_pager_margin));
        viewPager.setPageMarginDrawable(R.drawable.page_margin);
        viewPager.setOffscreenPageLimit(1);

        tabLayout.setupWithViewPager(viewPager);
        setupTabLayoutListener();
        viewPager.setCurrentItem(0);
    }

    @Override
    protected void initializePresenter() {
        /* no-op */
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            backStackHandler.handleBackStack(this);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void setupTabLayoutListener() {
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                /* no-op */
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                Fragment currentPage = getSupportFragmentManager().findFragmentByTag("android:switcher:"
                        + R.id.activity_pager
                        + ":"
                        + viewPager.getCurrentItem());
                scrollToTop(currentPage, viewPager.getCurrentItem());
            }
        });
    }

    private void scrollToTop(Fragment currentPage, int currentItem) {
        if (currentPage != null && currentItem == 0) {
            ((MeActivityTimelineFragment) currentPage).scrollListToTop();
        } else if (currentPage != null && currentItem == 1) {
            ((ActivityTimelineFragment) currentPage).scrollListToTop();
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return MeActivityTimelineFragment.newInstance();
                case 1:
                    return ActivityTimelineFragment.newInstance();
                default:
                    throw new IllegalStateException(String.format("Item for position %d doesn't exists", position));
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.drawer_activity_me).toUpperCase(l);
                case 1:
                    return getString(R.string.drawer_following_activity_title).toUpperCase(l);
                default:
                    throw new IllegalStateException(String.format("Item title for position %d doesn't exists",
                            position));
            }
        }
    }
}
