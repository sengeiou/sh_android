package com.shootr.android.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.shootr.android.ui.base.BaseFragment;

public class DummyFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        TextView tv = new TextView(container.getContext());
        tv.setText("I'm a dummy fragment. And my name is Richard");
        tv.setGravity(Gravity.CENTER);
        return tv;
    }
}
