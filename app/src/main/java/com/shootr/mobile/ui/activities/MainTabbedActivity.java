package com.shootr.mobile.ui.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.ToolbarDecorator;
import com.shootr.mobile.ui.fragments.FavoritesFragment;
import com.shootr.mobile.ui.fragments.PeopleFragment;
import com.shootr.mobile.ui.fragments.StreamsListFragment;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.presenter.MainScreenPresenter;
import com.shootr.mobile.ui.views.MainScreenView;
import com.shootr.mobile.ui.widgets.BadgeDrawable;
import com.shootr.mobile.util.FeedbackMessage;
import com.shootr.mobile.util.MenuItemValueHolder;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import timber.log.Timber;

import static com.shootr.mobile.domain.utils.Preconditions.checkNotNull;

public class MainTabbedActivity extends BaseToolbarDecoratedActivity implements MainScreenView {

    @Bind(R.id.pager) ViewPager viewPager;
    @Bind(R.id.tab_layout) TabLayout tabLayout;
    @BindString(R.string.multiple_activities_action) String multipleActivitiesAction;
    @Inject MainScreenPresenter mainScreenPresenter;
    @Inject FeedbackMessage feedbackMessage;

    private ToolbarDecorator toolbarDecorator;
    private BadgeDrawable activityBadgeIcon;
    private MenuItemValueHolder activityMenu = new MenuItemValueHolder();

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main_tabbed;
    }

    @Override
    protected void initializeViews(Bundle savedInstanceState) {
        ButterKnife.bind(this);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.setPageMargin(getResources().getDimensionPixelOffset(R.dimen.view_pager_margin));
        viewPager.setPageMarginDrawable(R.drawable.page_margin);
        viewPager.setOffscreenPageLimit(2);

        tabLayout.setupWithViewPager(viewPager);
        setupTabLayoutListener();
        viewPager.setCurrentItem(1);

        Uri data=getIntent().getData();
        if (data==null) {
            Timber.d("INTENT FAILED");
        }
        else {
            String address = data.toString();
            Pattern shareStreamPattern = Pattern.compile("#/st");
            Matcher matcher = shareStreamPattern.matcher(address);
            if (matcher.find()) {
                Timber.d("INTENT OK "+data.toString());
            } else {
                Timber.d("SHARE SHOT");
            }
        }

    }

    private void setupTabLayoutListener() {
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {
                /* no-op */
            }

            @Override public void onTabReselected(TabLayout.Tab tab) {
                Fragment currentPage = getSupportFragmentManager().findFragmentByTag("android:switcher:"
                  + R.id.pager
                  + ":"
                  + viewPager.getCurrentItem());
                scrollToTop(currentPage, viewPager.getCurrentItem());
            }
        });
    }

    private void scrollToTop(Fragment currentPage, int currentItem) {
        if (currentPage != null && currentItem == 0) {
            ((FavoritesFragment) currentPage).scrollListToTop();
        } else if (currentPage != null && currentItem == 1) {
            ((StreamsListFragment) currentPage).scrollListToTop();
        } else if (currentPage != null && currentItem==2) {
            ((PeopleFragment) currentPage).scrollListToTop();
        }
    }

    @Override
    protected void initializePresenter() {
        mainScreenPresenter.initialize(this);
    }

    @Override
    protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
        this.toolbarDecorator = toolbarDecorator;
        this.toolbarDecorator.getActionBar().setDisplayShowHomeEnabled(false);
        this.toolbarDecorator.getActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mainScreenPresenter.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mainScreenPresenter.pause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        activityMenu.bindRealMenuItem(menu.findItem(R.id.menu_activity));

        LayerDrawable activityIcon = (LayerDrawable) getResources().getDrawable(R.drawable.activity_badge_circle);
        checkNotNull(activityIcon);
        setupActivityBadgeIcon(activityIcon);
        activityMenu.setIcon(activityIcon);
        activityMenu.getIcon();

        return true;
    }

    public void setupActivityBadgeIcon(LayerDrawable icon) {
        // Reuse drawable if possible
        if (activityBadgeIcon == null) {
            Drawable reuse = icon.findDrawableByLayerId(R.id.ic_badge);
            if (reuse != null && reuse instanceof BadgeDrawable) {
                activityBadgeIcon = (BadgeDrawable) reuse;
            } else {
                activityBadgeIcon = new BadgeDrawable(this);
            }
        }
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_badge, activityBadgeIcon);
    }

    private void updateWatchNumberIcon(int count) {
        if (activityBadgeIcon == null ) {
            LayerDrawable activityIcon = (LayerDrawable) getResources().getDrawable(R.drawable.activity_badge_circle);
            setupActivityBadgeIcon(activityIcon);
        }
        activityBadgeIcon.setCount(count);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_activity) {
            navigateToActivity();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void navigateToActivity() {
        startActivity(new Intent(this, ActivityTimelineContainerActivity.class));
    }

    @Override
    public void setUserData(final UserModel userModel) {
        toolbarDecorator.setTitle(userModel.getUsername());
        toolbarDecorator.setAvatarImage(userModel.getPhoto());
        setToolbarClickListener(userModel);
    }

    @Override
    public void showActivityBadge(int count) {
       updateWatchNumberIcon(count);
    }

    @Override public void showHasMultipleActivities(Integer badgeCount) {
        String multipleActivities = getString(R.string.multiple_activity_notification, badgeCount);
        feedbackMessage.showMultipleActivities(getView(), multipleActivities, multipleActivitiesAction, new View.OnClickListener() {
            @Override public void onClick(View view) {
                navigateToActivity();
            }
        });
    }


    private void setToolbarClickListener(final UserModel userModel) {
        toolbarDecorator.setTitleClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = ProfileContainerActivity.getIntent(view.getContext(), userModel.getIdUser());
                startActivity(intent);
            }
        });
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return FavoritesFragment.newInstance();
                case 1:
                    return StreamsListFragment.newInstance();
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
                    return getString(R.string.drawer_favorites_title).toUpperCase(l);
                case 1:
                    return getString(R.string.drawer_streams_title).toUpperCase(l);
                case 2:
                    return getString(R.string.drawer_friends_title).toUpperCase(l);
                default:
                    throw new IllegalStateException(String.format("Item title for position %d doesn't exists",
                      position));
            }
        }
    }
}
