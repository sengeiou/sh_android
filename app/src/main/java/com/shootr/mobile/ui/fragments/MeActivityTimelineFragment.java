package com.shootr.mobile.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shootr.mobile.R;
import com.shootr.mobile.ui.base.BaseFragment;

import butterknife.ButterKnife;
import dagger.ObjectGraph;

public class MeActivityTimelineFragment extends BaseFragment {

    //region Fields

    //endregion

    public static MeActivityTimelineFragment newInstance() {
        return new MeActivityTimelineFragment();
    }

    //region Lifecycle methods
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_me_activity_timeline, container, false);
        ButterKnife.bind(this, fragmentView);
        return fragmentView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializePresenter();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void initializePresenter() {

    }
    //endregion

    @Override
    protected ObjectGraph getObjectGraph() {
        return super.getObjectGraph();
    }
}
