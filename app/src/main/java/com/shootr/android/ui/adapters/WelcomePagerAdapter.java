package com.shootr.android.ui.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.shootr.android.R;


public class WelcomePagerAdapter extends PagerAdapter {

    public static final int COUNT = 4;

    private final LayoutInflater mLayoutInflater;
    private SparseArray<View> mItems = new SparseArray<View>(COUNT);

    public WelcomePagerAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return COUNT;
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View page = mItems.get(position);
        if (page == null) {
            page = mLayoutInflater.inflate(R.layout.include_welcome_text, container, false);
            mItems.put(position, page);
        }

        TextView title = (TextView) page.findViewById(R.id.welcome_title);
        TextView subtitle = (TextView) page.findViewById(R.id.welcome_subtitle);

        int resTitle;
        int resSubtitle;
        Integer resIcon;
        switch (position) {
            case 0:
            default:
                resTitle = R.string.welcome_title_0;
                resSubtitle = R.string.welcome_subtitle_0;
                resIcon = R.drawable.ic_welcome_1;
                break;
            case 1:
                resTitle = R.string.welcome_title_1;
                resSubtitle = R.string.welcome_subtitle_1;
                resIcon = R.drawable.ic_welcome_1;
                break;
            case 2:
                resTitle = R.string.welcome_title_2;
                resSubtitle = R.string.welcome_subtitle_2;
                resIcon = R.drawable.ic_welcome_2;
                break;
            case 3:
                resTitle = R.string.welcome_title_3;
                resSubtitle = R.string.welcome_subtitle_3;
                resIcon = R.drawable.ic_welcome_3;
                break;
        }

        title.setText(resTitle);
        subtitle.setText(resSubtitle);
        page.setTag(resIcon); // For the listener

        container.addView(page, 0);
        return page;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }


    public View getItem(int position) {
        return mItems.get(position);

    }

    public boolean isFirst(int position) {
        return position == 0;
    }

    public boolean isLast(int position) {
        return position == getCount() - 1;
    }


}