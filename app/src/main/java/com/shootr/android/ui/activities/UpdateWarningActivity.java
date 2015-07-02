package com.shootr.android.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import com.shootr.android.R;
import com.shootr.android.ui.base.BaseNoToolbarActivity;

public class UpdateWarningActivity extends BaseNoToolbarActivity {

    public static Intent newIntent(@NonNull Context context) {
        return new Intent(context, UpdateWarningActivity.class);
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerContent(R.layout.activity_update_warning);
    }
}
