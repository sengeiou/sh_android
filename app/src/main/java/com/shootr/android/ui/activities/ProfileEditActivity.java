package com.shootr.android.ui.activities;

import android.os.Bundle;
import com.shootr.android.R;
import com.shootr.android.ui.base.BaseSignedInActivity;

public class ProfileEditActivity extends BaseSignedInActivity{

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!restoreSessionOrLogin()) {
            return;
        }
        setContainerContent(R.layout.activity_profile_edit);

    }
}
