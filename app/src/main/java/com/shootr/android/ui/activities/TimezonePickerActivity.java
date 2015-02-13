package com.shootr.android.ui.activities;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.BidiFormatter;
import android.text.TextDirectionHeuristics;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import com.shootr.android.R;
import com.shootr.android.ui.adapters.BindableAdapter;
import com.shootr.android.ui.base.BaseSignedInActivity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import org.xmlpull.v1.XmlPullParserException;

public class TimezonePickerActivity extends BaseSignedInActivity {

    private static final String KEY_ID = "id";  // value: String
    private static final String KEY_DISPLAYNAME = "name";  // value: String
    private static final String KEY_GMT = "gmt";  // value: String
    private static final String KEY_OFFSET = "offset";  // value: int (Integer)
    private static final String XMLTAG_TIMEZONE = "timezone";

    public static final String KEY_TIMEZONE = "timezone";

    @InjectView(android.R.id.list) ListView list;
    private TimezoneAdapter adapter;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerContent(android.R.layout.list_content);
        initializeViews();
        setupActionbar();
        initializePresenter();
    }

    private void initializeViews() {
        ButterKnife.inject(this);
    }

    private void setupActionbar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_action_navigation_close);
    }

    private void initializePresenter() {
        adapter = constructTimezoneAdapter();
        list.setAdapter(adapter);
        final int defaultIndex = getTimeZoneIndex(adapter, TimeZone.getDefault());
        if (defaultIndex >= 0) {
            list.setSelection(defaultIndex);
        }
    }

    @OnItemClick(android.R.id.list)
    public void onItemClick(int position) {
        TimezoneData tz = adapter.getItem(position);
        setResult(RESULT_OK, getIntent().putExtra(KEY_TIMEZONE, tz.getOlsonId()));
        finish();
    }

    private TimezoneAdapter constructTimezoneAdapter() {
        final List<TimezoneData> sortedList = getTimezonesFromJava();
        return new TimezoneAdapter(this, sortedList);
    }

    private List<TimezoneData> getTimezonesFromXml() {
        //final MyComparator comparator = new MyComparator(sortKey);
        ZoneGetter zoneGetter = new ZoneGetter();
        //Collections.sort(sortedList, comparator);
        return zoneGetter.getZones(this);
    }

    private List<TimezoneData> getTimezonesFromJava() {
        List<TimezoneData> datas = new ArrayList<>();
        String[] availableIDs = TimeZone.getAvailableIDs();
        for (String tzId : availableIDs) {
            TimeZone timeZone = TimeZone.getTimeZone(tzId);
            if (timeZone != null) {
                datas.add(new TimezoneData(timeZone));
            }
        }
        Collections.sort(datas, new TimezoneOffsetComparator());
        return datas;
    }

    public static int getTimeZoneIndex(TimezoneAdapter adapter, TimeZone tz) {
        final String defaultId = tz.getID();
        final int listSize = adapter.getCount();
        for (int i = 0; i < listSize; i++) {
            // Using HashMap<String, Object> induces unnecessary warning.
            final TimezoneData item = adapter.getItem(i);
            final String id = item.getOlsonId();
            if (defaultId.equals(id)) {
                // If current timezone is in this list, move focus to it
                return i;
            }
        }
        return -1;
    }

    static class ZoneGetter {

        private final List<TimezoneData> mZones =
          new ArrayList<>();

        private final Date mNow = Calendar.getInstance().getTime();
        private final SimpleDateFormat mZoneNameFormatter = new SimpleDateFormat("zzzz");

        private List<TimezoneData> getZones(Context context) {
            try {
                XmlResourceParser xrp = context.getResources().getXml(R.xml.timezones);
                while (xrp.next() != XmlResourceParser.START_TAG) {
                    continue;
                }
                xrp.next();
                while (xrp.getEventType() != XmlResourceParser.END_TAG) {
                    while (xrp.getEventType() != XmlResourceParser.START_TAG) {
                        if (xrp.getEventType() == XmlResourceParser.END_DOCUMENT) {
                            return mZones;
                        }
                        xrp.next();
                    }
                    if (xrp.getName().equals(XMLTAG_TIMEZONE)) {
                        String olsonId = xrp.getAttributeValue(0);
                        addTimeZone(olsonId);
                    }
                    while (xrp.getEventType() != XmlResourceParser.END_TAG) {
                        xrp.next();
                    }
                    xrp.next();
                }
                xrp.close();
            } catch (XmlPullParserException | java.io.IOException xppe) {
                //TODO
            }
            return mZones;
        }

        private void addTimeZone(String olsonId) {
            // We always need the "GMT-07:00" string.
            final TimeZone tz = TimeZone.getTimeZone(olsonId);
            final TimezoneData timezoneData = new TimezoneData(tz);
            mZones.add(timezoneData);
        }
    }

    public static String getTimeZoneText(TimeZone tz, boolean includeName) {
        Date now = new Date();

        // Use SimpleDateFormat to format the GMT+00:00 string.
        SimpleDateFormat gmtFormatter = new SimpleDateFormat("ZZZZ");
        gmtFormatter.setTimeZone(tz);
        String gmtString = gmtFormatter.format(now);

        // Ensure that the "GMT+" stays with the "00:00" even if the digits are RTL.
        BidiFormatter bidiFormatter = BidiFormatter.getInstance();
        Locale l = Locale.getDefault();
        boolean isRtl = TextUtils.getLayoutDirectionFromLocale(l) == View.LAYOUT_DIRECTION_RTL;
        gmtString =
          bidiFormatter.unicodeWrap(gmtString, isRtl ? TextDirectionHeuristics.RTL : TextDirectionHeuristics.LTR);

        if (!includeName) {
            return gmtString;
        }

        // Optionally append the time zone name.
        SimpleDateFormat zoneNameFormatter = new SimpleDateFormat("zzzz");
        zoneNameFormatter.setTimeZone(tz);
        String zoneNameString = zoneNameFormatter.format(now);

        // We don't use punctuation here to avoid having to worry about localizing that too!
        return gmtString + " " + zoneNameString;
    }

    private static class TimezoneOffsetComparator implements Comparator<TimezoneData> {

        private final Date now;

        private TimezoneOffsetComparator() {
            now = new Date();
        }

        public int compare(TimezoneData tz1, TimezoneData tz2) {
            return tz1.getOffset(now).compareTo(tz2.getOffset(now));
        }
    }

    static class TimezoneData {

        private TimeZone timeZone;

        TimezoneData(TimeZone timeZone) {
            this.timeZone = timeZone;
        }

        public TimeZone getTimeZone() {
            return timeZone;
        }

        public void setTimeZone(TimeZone timeZone) {
            this.timeZone = timeZone;
        }

        public String getOlsonId() {
            return timeZone.getID();
        }

        public String getDisplayName() {
            return timeZone.getID();
        }

        public String getGTM() {
            //TODO
            return getTimeZoneText(timeZone, false);
        }

        public Integer getOffset(Date now) {
            return timeZone.getOffset(now.getTime());
        }
    }

    static class TimezoneAdapter extends BindableAdapter<TimezoneData> {

        private final List<TimezoneData> timezoneDatas;

        public TimezoneAdapter(Context context, List<TimezoneData> timezoneDatas) {
            super(context);
            this.timezoneDatas = timezoneDatas;
        }

        @Override public int getCount() {
            return timezoneDatas.size();
        }

        @Override public TimezoneData getItem(int position) {
            return timezoneDatas.get(position);
        }

        @Override public long getItemId(int position) {
            return position;
        }

        @Override public View newView(LayoutInflater inflater, int position, ViewGroup container) {
            return inflater.inflate(android.R.layout.simple_list_item_2, container, false);
        }

        @Override public void bindView(TimezoneData item, int position, View view) {
            TextView displayName = (TextView) view.findViewById(android.R.id.text1);
            TextView gtm = (TextView) view.findViewById(android.R.id.text2);

            displayName.setText(item.getDisplayName());
            gtm.setText(item.getGTM());
        }
    }
}
