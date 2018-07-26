package com.shootr.mobile.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.activities.ChannelListFragment;
import com.shootr.mobile.ui.activities.FollowingChannelListFragment;
import com.shootr.mobile.ui.base.BaseFragment;
import com.shootr.mobile.util.AnalyticsTool;
import javax.inject.Inject;

public class ChannelsContainerFragment extends BaseFragment {

  @Inject AnalyticsTool analyticsTool;

  @BindString(R.string.analytics_screen_channel_list) String analyticsChannelList;

  @BindView(R.id.activity_pager) ViewPager viewPager;
  @BindView(R.id.activity_tab_layout) TabLayout tabLayout;

  private Unbinder unbinder;

  public static ChannelsContainerFragment newInstance() {
    return new ChannelsContainerFragment();
  }

  //region Lifecycle methods
  @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View fragmentView = inflater.inflate(R.layout.activity_channel_container, container, false);
    unbinder = ButterKnife.bind(this, fragmentView);
    initializeViews();
    return fragmentView;
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
  }

  @Override public void onResume() {
    super.onResume();
  }

  @Override public void onPause() {
    super.onPause();
  }

  private void initializeViews() {
    SectionsPagerAdapter sectionsPagerAdapter =
        new SectionsPagerAdapter(getChildFragmentManager());
    viewPager.setAdapter(sectionsPagerAdapter);
    viewPager.setPageMargin(getResources().getDimensionPixelOffset(R.dimen.view_pager_margin));
    viewPager.setPageMarginDrawable(R.drawable.page_margin);
    viewPager.setOffscreenPageLimit(1);

    tabLayout.setupWithViewPager(viewPager);
    setupTabLayoutListener();
    viewPager.setCurrentItem(0);
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
        /* no-op */
      }
    });
  }

  public void setTabTitle(Fragment fragment, int unreads) {
    if (isAdded()) {
      if (fragment instanceof ChannelListFragment) {
        String resource = setupPluralResource(unreads, R.string.all_messages_title,
            R.string.all_messages_title_plural);
        setupTabTitle(0, resource);
      } else {
        String resource = setupPluralResource(unreads, R.string.following_messages_title,
            R.string.following_messages_title_plural);
        setupTabTitle(1, resource);
      }
    }
  }

  @NonNull private String setupPluralResource(int unreads, int resource, int pluralResource) {
    String title;
    if (unreads == 0) {
      title = getString(resource);
    } else {
      title = getString(pluralResource, unreads);
    }
    return title;
  }

  private void setupTabTitle(int index, String text) {
    if (tabLayout.getTabAt(index) != null) {
      tabLayout.getTabAt(index).setText(text);
    }
  }

  private class SectionsPagerAdapter extends FragmentStatePagerAdapter {

    SectionsPagerAdapter(FragmentManager fm) {
      super(fm);
    }

    @Override public Fragment getItem(int position) {
      switch (position) {
        case 0:
          return ChannelListFragment.newInstance();
        case 1:
          return FollowingChannelListFragment.newInstance();
        default:
          throw new IllegalStateException(
              String.format("Item for position %d doesn't exists", position));
      }
    }

    @Override public int getCount() {
      return 2;
    }

    @Override public CharSequence getPageTitle(int position) {
      switch (position) {
        case 0:
          return getString(R.string.all_messages_title);
        case 1:
          return getString(R.string.following_messages_title);
        default:
          throw new IllegalStateException(
              String.format("Item title for position %d doesn't exists", position));
      }
    }
  }

  @Override public void onStart() {
    super.onStart();
    analyticsTool.analyticsStart(getContext(), analyticsChannelList);
  }

}
