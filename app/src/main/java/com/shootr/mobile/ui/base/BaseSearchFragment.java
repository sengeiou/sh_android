package com.shootr.mobile.ui.base;


public abstract class BaseSearchFragment extends BaseFragment {

  public abstract void search(String query);

  public abstract void searchChanged(String query);
}
