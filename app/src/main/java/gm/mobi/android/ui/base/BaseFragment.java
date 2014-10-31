package gm.mobi.android.ui.base;

import android.app.Activity;
import android.support.v4.app.Fragment;
import gm.mobi.android.GolesApplication;

public class BaseFragment extends Fragment {

    private boolean mFistAttach = true;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (mFistAttach) {
            GolesApplication.get(getActivity()).inject(this);
            mFistAttach = false;
        }
    }

}
