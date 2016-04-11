package com.shootr.mobile.ui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import com.shootr.mobile.ShootrApplication;
import dagger.ObjectGraph;

public class BaseFragment extends Fragment {

    private boolean mFistAttach = true;

    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        injectDependenciesOnFirstAttach();
    }

    private void injectDependenciesOnFirstAttach() {
        if (mFistAttach) {
            this.getObjectGraph().inject(this);
            mFistAttach = false;
        }
    }

    protected ObjectGraph getObjectGraph() {
        if (getActivity() instanceof BaseActivity) {
            return ((BaseActivity) getActivity()).getObjectGraph();
        } else {
            //TODO delete this case when all activities have been migrated to BaseActivity
            return ShootrApplication.get(getActivity()).getObjectGraph();
        }
    }
}
