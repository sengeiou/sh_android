package com.shootr.android.ui.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.shootr.android.R;
import com.shootr.android.ui.ToolbarDecorator;
import com.shootr.android.ui.adapters.SupportAdapter;
import java.util.Arrays;
import java.util.List;

public class SupportActivity extends BaseToolbarDecoratedActivity {

    private static List<String> SUPPORT_LIST = Arrays.asList("FAQ", "Privacy Policy", "Terms of Service", "Version");

    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;

    @Bind(R.id.support_recycler_view) RecyclerView supportView;

    @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
        /* no-op */
    }

    @Override protected int getLayoutResource() {
        return R.layout.activity_support;
    }

    @Override protected void initializeViews(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        supportView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        supportView.setLayoutManager(layoutManager);
        adapter = new SupportAdapter(this, SUPPORT_LIST);
        supportView.setAdapter(adapter);
    }

    @Override protected void initializePresenter() {
        // TODO if really procceed
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
