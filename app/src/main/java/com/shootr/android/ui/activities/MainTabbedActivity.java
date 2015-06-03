package com.shootr.android.ui.activities;

import android.support.design.widget.TabLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.shootr.android.ui.ToolbarDecorator;
import com.shootr.android.ui.fragments.ActivityTimelineFragment;
import com.shootr.android.ui.fragments.EventsListFragment;
import com.shootr.android.ui.fragments.PeopleFragment;
import java.util.Locale;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import com.shootr.android.R;

public class MainTabbedActivity extends BaseToolbarDecoratedActivity {

    @InjectView(R.id.pager) ViewPager viewPager;
    @InjectView(R.id.tab_layout) TabLayout tabLayout;

    private SectionsPagerAdapter sectionsPagerAdapter;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main_tabbed;
    }

    @Override
    protected void initializeViews(Bundle savedInstanceState) {
        ButterKnife.inject(this);

        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(sectionsPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void initializePresenter() {
        //TODO
    }

    @Override
    protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
        //TODO
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return ActivityTimelineFragment.newInstance();
                case 1:
                    return EventsListFragment.newInstance(); //TODO
                case 2:
                    return PeopleFragment.newInstance();
                default:
                    throw new IllegalStateException(String.format("Item for position %d doesn't exists", position));
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.drawer_activity_title).toUpperCase(l);
                case 1:
                    return getString(R.string.drawer_events_title).toUpperCase(l);
                case 2:
                    return getString(R.string.drawer_friends_title).toUpperCase(l);
                default:
                    throw new IllegalStateException(String.format("Item title for position %d doesn't exists", position));
            }
        }
    }
}
