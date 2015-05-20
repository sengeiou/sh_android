package com.shootr.android.ui.activities;

import android.os.Bundle;
import android.widget.GridView;
import butterknife.InjectView;
import com.shootr.android.R;
import com.shootr.android.ui.ToolbarDecorator;
import com.shootr.android.ui.views.EventMediaView;

public class EventMediaActivity extends BaseToolbarDecoratedActivity implements EventMediaView {

    @InjectView(R.id.event_media_grid_view) GridView mediaGridView;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_media);
        mediaGridView.setAdapter(new MediaAdapter(this));
    }

    @Override protected int getLayoutResource() {
        return 0;
    }

    @Override protected void initializeViews(Bundle savedInstanceState) {

    }

    @Override protected void initializePresenter() {

    }

    @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {

    }
}
