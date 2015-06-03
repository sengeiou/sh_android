package com.shootr.android.ui.base;

import android.app.Activity;
import android.support.v4.app.Fragment;
import com.shootr.android.ShootrApplication;
import dagger.ObjectGraph;

public class BaseFragment extends Fragment {

    private boolean mFistAttach = true;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
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
