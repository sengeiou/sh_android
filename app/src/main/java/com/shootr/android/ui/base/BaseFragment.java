package com.shootr.android.ui.base;

import android.app.Activity;
import android.support.v4.app.Fragment;
import com.shootr.android.ShootrApplication;

public class BaseFragment extends Fragment {

    private boolean mFistAttach = true;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (mFistAttach) {
            ShootrApplication.get(getActivity()).inject(this);
            mFistAttach = false;
        }
    }

}
