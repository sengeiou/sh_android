package com.shootr.android.ui.activities;

import android.os.Bundle;
import com.shootr.android.R;
import com.shootr.android.ui.base.BaseNoToolbarActivity;

public class UpdateWarningActivity extends BaseNoToolbarActivity {

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerContent(R.layout.activity_update_warning);
    }
}
