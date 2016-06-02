package com.shootr.mobile.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.base.BaseFragment;
import java.util.Locale;

public class ActivityTimelineContainerFragment extends BaseFragment {

  @Bind(R.id.activity_pager_timelines) ViewPager viewPager;
  @Bind(R.id.activity_tab_layout) TabLayout tabLayout;

  public static ActivityTimelineContainerFragment newInstance() {
    return new ActivityTimelineContainerFragment();
  }

  //region Lifecycle methods
  @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View fragmentView = inflater.inflate(R.layout.fragment_activity_timeline_container, container, false);
    ButterKnife.bind(this, fragmentView);
    initializeViews();
    return fragmentView;
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    ButterKnife.unbind(this);
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    initializePresenter();
  }

  @Override public void onResume() {
    super.onResume();
  }

  @Override public void onPause() {
    super.onPause();
  }

  private void initializePresenter() {
  }
  //endregion

  protected void initializeViews() {
    SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getActivity().getSupportFragmentManager());
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
        Fragment currentPage = getFragmentManager().findFragmentByTag("android:switcher:"
            + R.id.activity_pager_timelines
            + ":"
            + viewPager.getCurrentItem());
        scrollToTop(currentPage, viewPager.getCurrentItem());
      }
    });
  }

  private void scrollToTop(Fragment currentPage, int currentItem) {
    if (currentPage != null && currentItem == 0) {
      ((ActivityTimelineFragment) currentPage).scrollListToTop();
    } else if (currentPage != null && currentItem == 1) {
      ((MeActivityTimelineFragment) currentPage).scrollListToTop();
    }
  }

  public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
      super(fm);
    }

    @Override public Fragment getItem(int position) {
      switch (position) {
        case 0:
          return ActivityTimelineFragment.newInstance();
        case 1:
          return MeActivityTimelineFragment.newInstance();
        default:
          throw new IllegalStateException(String.format("Item for position %d doesn't exists", position));
      }
    }

    @Override public int getCount() {
      return 2;
    }

    @Override public CharSequence getPageTitle(int position) {
      Locale l = Locale.getDefault();
      switch (position) {
        case 0:
          return getString(R.string.drawer_following_activity_title).toUpperCase(l);
        case 1:
          return getString(R.string.drawer_activity_me).toUpperCase(l);
        default:
          throw new IllegalStateException(String.format("Item title for position %d doesn't exists",
              position));
      }
    }
  }
}
