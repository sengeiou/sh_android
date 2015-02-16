package com.shootr.android.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import com.shootr.android.R;
import com.shootr.android.ui.adapters.BindableAdapter;
import com.shootr.android.ui.base.BaseSignedInActivity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class TimezonePickerActivity extends BaseSignedInActivity {

    public static final String KEY_TIMEZONE = "timezone";

    @InjectView(android.R.id.list) ListView list;
    private TimezoneAdapter adapter;
    private Toast toast;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerContent(R.layout.activity_timezone_picker);
        initializeViews();
        setupActionbar();
        initializeContent();
    }

    private void initializeViews() {
        ButterKnife.inject(this);
        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        list.setOnScrollListener(new AbsListView.OnScrollListener() {
            int lastFirstVisibleItem = 0;
            Date now = new Date();
            @Override public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
              int totalItemCount) {
                if (adapter != null && firstVisibleItem != lastFirstVisibleItem && firstVisibleItem > 0) {
                    TimezoneItem item = adapter.getItem(firstVisibleItem);
                    toast.setText(item.getGTM(now));
                    toast.show();
                    lastFirstVisibleItem = firstVisibleItem;
                }
            }

            @Override public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
        });
    }

    private void setupActionbar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
    }

    private void initializeContent() {
        adapter = constructTimezoneAdapter();
        list.setAdapter(adapter);
        final int defaultIndex = getIntentTimeZoneIndex();
        if (defaultIndex >= 0) {
            list.setSelection(defaultIndex);
        }
    }

    @OnItemClick(android.R.id.list)
    public void onTimezoneSelected(int position) {
        TimezoneItem tz = adapter.getItem(position);
        setResult(RESULT_OK, getIntent().putExtra(KEY_TIMEZONE, tz.getOlsonId()));
        finish();
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private TimezoneAdapter constructTimezoneAdapter() {
        final List<TimezoneItem> sortedList = getTimezoneItems();
        return new TimezoneAdapter(this, sortedList);
    }

    // Could be soooooo much clear with Java 8 streams... :(
    private List<TimezoneItem> getTimezoneItems() {
        List<TimezoneItem> datas = new ArrayList<>();
        String[] availableIDs = TimeZone.getAvailableIDs();
        for (String tzId : availableIDs) {
            TimeZone timeZone = TimeZone.getTimeZone(tzId);
            if (timeZone != null) {
                datas.add(new TimezoneItem(timeZone));
            }
        }
        Collections.sort(datas, new TimezoneOffsetComparator());
        return datas;
    }

    public int getIntentTimeZoneIndex() {
        String defaultId = getIntent().getStringExtra(KEY_TIMEZONE);
        if (defaultId == null) {
            defaultId = TimeZone.getDefault().getID();
        }
        final int listSize = adapter.getCount();
        for (int i = 0; i < listSize; i++) {
            final TimezoneItem item = adapter.getItem(i);
            final String id = item.getOlsonId();
            if (defaultId.equals(id)) {
                return i;
            }
        }
        return -1;
    }

    private static class TimezoneOffsetComparator implements Comparator<TimezoneItem> {

        private final Date now;

        private TimezoneOffsetComparator() {
            now = new Date();
        }

        public int compare(TimezoneItem tz1, TimezoneItem tz2) {
            return tz1.getOffset(now).compareTo(tz2.getOffset(now));
        }
    }

    static class TimezoneItem {

        private TimeZone timeZone;

        public TimezoneItem(TimeZone timeZone) {
            this.timeZone = timeZone;
        }

        public String getOlsonId() {
            return timeZone.getID();
        }

        public String getDisplayName() {
            return timeZone.getID();
        }

        public String getGTM(Date now) {
            SimpleDateFormat gmtFormatter = new SimpleDateFormat("ZZZZ");
            gmtFormatter.setTimeZone(timeZone);
            return gmtFormatter.format(now);
        }

        public Integer getOffset(Date now) {
            return timeZone.getOffset(now.getTime());
        }
    }

    static class TimezoneAdapter extends BindableAdapter<TimezoneItem> {

        private final List<TimezoneItem> timezoneItems;
        private final Date now = new Date();

        public TimezoneAdapter(Context context, List<TimezoneItem> timezoneItems) {
            super(context);
            this.timezoneItems = timezoneItems;
        }

        @Override public int getCount() {
            return timezoneItems.size();
        }

        @Override public TimezoneItem getItem(int position) {
            return timezoneItems.get(position);
        }

        @Override public long getItemId(int position) {
            return position;
        }

        @Override public View newView(LayoutInflater inflater, int position, ViewGroup container) {
            return inflater.inflate(R.layout.item_list_timezone_two_line, container, false);
        }

        @Override public void bindView(TimezoneItem item, int position, View view) {
            TextView displayName = (TextView) view.findViewById(android.R.id.text1);
            TextView gtm = (TextView) view.findViewById(android.R.id.text2);

            displayName.setText(item.getDisplayName());
            gtm.setText(item.getGTM(now));
        }
    }
}
