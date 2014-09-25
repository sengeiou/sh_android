package gm.mobi.android.ui.activities;

import android.os.Bundle;

import butterknife.InjectView;
import gm.mobi.android.R;
import gm.mobi.android.ui.base.BaseActivity;
import gm.mobi.android.ui.base.BaseSignedInActivity;

public class NewShotActivity extends BaseSignedInActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!restoreSessionOrLogin()) {
            return;
        }
        setContentView(R.layout.activity_new_shot);
    }
}
