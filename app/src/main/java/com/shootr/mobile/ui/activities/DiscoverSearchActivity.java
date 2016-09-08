package com.shootr.mobile.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import butterknife.BindView;
import butterknife.BindString;
import butterknife.ButterKnife;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.ToolbarDecorator;
import com.shootr.mobile.ui.base.BaseSearchFragment;
import com.shootr.mobile.ui.fragments.FindFriendsFragment;
import com.shootr.mobile.ui.fragments.FindStreamsFragment;

public class DiscoverSearchActivity extends BaseToolbarDecoratedActivity {

  public static final String EXTRA_QUERY = "query";

  @BindView(R.id.pager) ViewPager viewPager;
  @BindView(R.id.tab_layout) TabLayout tabLayout;
  @BindString(R.string.drawer_streams_title) String streamsTitle;
  @BindString(R.string.drawer_users_title) String usersTitle;

  private SearchView searchView;
  private String currentSearchQuery;
  private Bundle savedInstanceState;

  private BaseSearchFragment[] fragments = new BaseSearchFragment[2];

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  private void setupPager() {
    SectionsPagerAdapter sectionsPagerAdapter =
        new SectionsPagerAdapter(getSupportFragmentManager());
    viewPager.setAdapter(sectionsPagerAdapter);
    viewPager.setPageMargin(getResources().getDimensionPixelOffset(R.dimen.view_pager_margin));
    viewPager.setPageMarginDrawable(R.drawable.page_margin);
    viewPager.setBackgroundColor(getResources().getColor(R.color.white));

    tabLayout.setupWithViewPager(viewPager);
    viewPager.setCurrentItem(0);

    viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        /* no-pop */
      }

      @Override public void onPageSelected(int position) {
        changeSearchViewHint(position);
        if (currentSearchQuery != null) {
          fragments[position].search(currentSearchQuery);
        }
      }

      @Override public void onPageScrollStateChanged(int state) {
        /* no-op */
      }
    });
  }

  private void changeSearchViewHint(int position) {
    if (position == 1) {
      searchView.setQueryHint(getResources().getString(R.string.activity_find_streams_hint));
    } else {
      searchView.setQueryHint(getResources().getString(R.string.search_users_hint));
    }
  }

  @Override protected int getLayoutResource() {
    return R.layout.activity_discover_search;
  }

  @Override protected void initializeViews(Bundle savedInstanceState) {
    ButterKnife.bind(this);
    setupActionBar();
    setupPager();
    this.savedInstanceState = savedInstanceState;
  }

  @Override protected void initializePresenter() {

  }

  @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
    /* no-op */
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.search, menu);
    MenuItem searchItem = menu.findItem(R.id.menu_search);
    createSearchView(searchItem);
    setupQuery();

    SearchView.SearchAutoComplete searchAutoComplete =
        (SearchView.SearchAutoComplete) searchView.findViewById(
            android.support.v7.appcompat.R.id.search_src_text);
    searchAutoComplete.setHintTextColor(getResources().getColor(R.color.hint_black));

    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      finish();
      return true;
    } else {
      return super.onOptionsItemSelected(item);
    }
  }

  @Override public void finish() {
    super.finish();
    overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
  }

  private void setupQuery() {
    if (currentSearchQuery != null) {
      searchView.setQuery(currentSearchQuery, false);
      searchView.clearFocus();
    }
  }

  private void createSearchView(MenuItem searchItem) {
    searchView = (SearchView) searchItem.getActionView();

    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
      @Override public boolean onQueryTextSubmit(String queryText) {
        BaseSearchFragment currentFragment = fragments[viewPager.getCurrentItem()];
        if (currentFragment != null) {
          currentFragment.search(queryText);
          hideKeyboard();
        }
        return true;
      }

      @Override public boolean onQueryTextChange(String query) {
        BaseSearchFragment currentFragment = fragments[viewPager.getCurrentItem()];
        if (currentFragment != null) {
          currentFragment.searchChanged(query);
          currentSearchQuery = query;
        }
        return false;
      }
    });
    searchView.setQueryHint(getResources().getString(R.string.search_users_hint));
    searchView.setIconifiedByDefault(false);
    searchView.setIconified(false);
    if (savedInstanceState != null) {
      searchView.setQuery(savedInstanceState.getCharSequence(EXTRA_QUERY), false);
    }
  }

  public void hideKeyboard() {
    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
  }

  private void setupActionBar() {
    ActionBar actionBar = getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);
    actionBar.setDisplayShowHomeEnabled(false);
  }

  public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
      super(fm);
    }

    @Override public Fragment getItem(int position) {
      switch (position) {
        case 0:
          FindFriendsFragment findFriendsFragment = new FindFriendsFragment();
          fragments[0] = findFriendsFragment;
          return findFriendsFragment;
        case 1:
          FindStreamsFragment findStreamsFragment = new FindStreamsFragment();
          fragments[1] = findStreamsFragment;
          return findStreamsFragment;
        default:
          return null;
      }
    }

    @Override public int getCount() {
      return 2;
    }

    @Override public CharSequence getPageTitle(int position) {
      switch (position) {
        case 0:
          return usersTitle;
        case 1:
          return streamsTitle;
        default:
          throw new IllegalStateException(
              String.format("Item title for position %d doesn't exists", position));
      }
    }
  }

  @Override protected void onResume() {
    super.onResume();
    if (searchView != null) {
      searchView.clearFocus();
    }
  }
}
