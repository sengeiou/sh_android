package com.shootr.android.ui.activities;

import android.os.Bundle;
import android.view.MenuItem;
import com.shootr.android.R;
import com.shootr.android.ui.ToolbarDecorator;

public class AllParticipantsActivity extends BaseToolbarDecoratedActivity {

    @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
        //TODO
    }

    @Override protected int getLayoutResource() {
        return R.layout.activity_all_participants;
    }

    @Override protected void initializeViews(Bundle savedInstanceState) {
        //TODO
    }

    @Override protected void initializePresenter() {
        //TODO
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }else{
            return super.onOptionsItemSelected(item);
        }
    }
}
