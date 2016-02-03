package com.shootr.mobile.ui.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.ToolbarDecorator;

public class StreamDataInfoActivity extends BaseToolbarDecoratedActivity {

    @Bind(R.id.stream_data_info_participants_number) TextView participantsNumber;
    @Bind(R.id.stream_data_info_shots_number) TextView shotsNumber;
    @Bind(R.id.stream_data_info_favorites_number) TextView favoritesNumber;

    @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
        /* no-op */
    }

    @Override protected int getLayoutResource() {
        return R.layout.activity_stream_data_info;
    }

    @Override protected void initializeViews(Bundle savedInstanceState) {
        ButterKnife.bind(this);
    }

    @Override protected void initializePresenter() {
        /* no-op */
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
