package com.shootr.android.ui.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.shootr.android.R;
import com.shootr.android.ui.base.BaseActivity;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.views.StreamDetailView;
import java.util.List;

public class StreamDetailActivityDraft extends BaseActivity {

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.cat_avatar) ImageView streamPhoto;
    @Bind(R.id.cat_title) TextView streamTitle;
    @Bind(R.id.stream_detail_subtitle) TextView streamSubtitle;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_stream_detail_draft;
    }

    @Override
    protected void initializeViews(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void initializePresenter() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_shot_detail, menu);
        return true;
    }
}
