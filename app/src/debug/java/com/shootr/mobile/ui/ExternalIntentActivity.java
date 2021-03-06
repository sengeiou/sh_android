package com.shootr.mobile.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.style.StyleSpan;
import android.view.MenuItem;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.shootr.mobile.R;
import com.shootr.mobile.util.Intents;
import com.shootr.mobile.util.Truss;
import java.lang.reflect.Field;
import java.util.Arrays;
import timber.log.Timber;

public final class ExternalIntentActivity extends Activity implements Toolbar.OnMenuItemClickListener {

    public static final String ACTION = "com.shootr.mobile.intent.EXTERNAL_INTENT";
    public static final String EXTRA_BASE_INTENT = "debug_base_intent";

    public static Intent createIntent(Intent baseIntent) {
        Intent intent = new Intent();
        intent.setAction(ACTION);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(EXTRA_BASE_INTENT, baseIntent);
        return intent;
    }

    @BindView(R.id.toolbar) Toolbar toolbarView;
    @BindView(R.id.action) TextView actionView;
    @BindView(R.id.data) TextView dataView;
    @BindView(R.id.extras) TextView extrasView;
    @BindView(R.id.flags) TextView flagsView;

    private Intent baseIntent;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.debug_external_intent_activity);
        ButterKnife.bind(this);

        toolbarView.inflateMenu(R.menu.debug_external_intent);
        toolbarView.setOnMenuItemClickListener(this);

        baseIntent = getIntent().getParcelableExtra(EXTRA_BASE_INTENT);
        fillAction();
        fillData();
        fillExtras(baseIntent.getExtras());
        fillFlags();
    }

    @Override public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.debug_launch:
                if (Intents.maybeStartActivity(this, baseIntent)) {
                    finish();
                }
                return true;
            default:
                return false;
        }
    }

    private void fillAction() {
        String action = baseIntent.getAction();
        actionView.setText(action == null ? "None!" : action);
    }

    private void fillData() {
        Uri data = baseIntent.getData();
        dataView.setText(data == null ? "None!" : data.toString());
    }

    private void fillExtras(Bundle extras) {
        if (extras == null) {
            extrasView.setText("None!");
        } else {
            extrasView.setText(parseBundle(extras));
        }
    }

    private CharSequence parseBundle(Bundle extras) {
        Truss truss = new Truss();
        for (String key : extras.keySet()) {
            Object value = extras.get(key);
            if (value == null) {
                continue;
            }

            CharSequence valueString;
            if (value.getClass().isArray()) {
                valueString = Arrays.toString((Object[]) value);
            } else if (value instanceof Intent) {
                valueString = parseBundle(((Intent) value).getExtras());
            } else {
                valueString = value.toString();
            }

            truss.pushSpan(new StyleSpan(Typeface.BOLD));
            truss.append(key).append(":\n");
            truss.popSpan();
            truss.append(valueString).append("\n\n");
        }

        return truss.build();
    }

    private void fillFlags() {
        int flags = baseIntent.getFlags();

        StringBuilder builder = new StringBuilder();
        for (Field field : Intent.class.getDeclaredFields()) {
            try {
                if (field.getName().startsWith("FLAG_")
                  && field.getType() == Integer.TYPE
                  && (flags & field.getInt(null)) != 0) {
                    builder.append(field.getName()).append('\n');
                }
            } catch (IllegalAccessException e) {
                Timber.e(e, "Couldn't read value for: " + field.getName());
            }
        }

        flagsView.setText(builder.length() == 0 ? "None!" : builder.toString());
    }
}