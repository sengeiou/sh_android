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
            if (getActivity() instanceof BaseActivity) {
                ((BaseActivity) getActivity()).inject(this);
            } else {
                //TODO delete this case when all activities have been migrated to BaseActivity
                ShootrApplication.get(getActivity()).inject(this);
            }
            mFistAttach = false;
        }
    }
}
