package com.shootr.android.ui.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.shootr.android.R;
import com.shootr.android.domain.utils.LocaleProvider;
import com.shootr.android.ui.ToolbarDecorator;
import com.shootr.android.ui.adapters.SupportAdapter;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;

public class SupportActivity extends BaseToolbarDecoratedActivity {

    public static final String FAQ = "FAQ";
    public static final String PRIVACY_POLICY = "Privacy Policy";
    public static final String TERMS_OF_SERVICE = "Terms of Service";
    public static final String VERSION = "Version";
    private static List<String> SUPPORT_LIST = Arrays.asList(FAQ, PRIVACY_POLICY, TERMS_OF_SERVICE, VERSION);

    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;

    @Inject LocaleProvider localeProvider;

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
        adapter = new SupportAdapter(this, SUPPORT_LIST, localeProvider);
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
