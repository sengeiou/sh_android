package com.shootr.android.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.shootr.android.R;
import com.shootr.android.ui.ToolbarDecorator;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.presenter.EventMediaPresenter;
import com.shootr.android.ui.views.EventMediaView;
import java.util.List;
import javax.inject.Inject;

public class EventMediaActivity extends BaseToolbarDecoratedActivity implements EventMediaView {

    @InjectView(R.id.event_media_grid_view) GridView mediaGridView;
    @Inject EventMediaPresenter presenter;

    private static final String EXTRA_EVENT_ID = "event";

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_media);
        Intent intent = new Intent(getBaseContext(), EventMediaActivity.class);
        String idEvent = intent.getStringExtra(EXTRA_EVENT_ID);
        presenter.retrieveMedia(idEvent);
    }

    @Override protected int getLayoutResource() {
        return R.layout.activity_event_media;
    }

    @Override protected void initializeViews(Bundle savedInstanceState) {
        ButterKnife.inject(this);
    }

    @Override protected void initializePresenter() {
        presenter.initialize(this);
    }

    @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
        /* no-op */
    }

    @Override public void setMedia(List<ShotModel> shotsWithMedia) {
        //mediaGridView.setVisibility(View.VISIBLE);
        mediaGridView.setAdapter(new MediaAdapter(this, shotsWithMedia));
        final Context context = this;
        mediaGridView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Toast.makeText(context, "guayaco", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
