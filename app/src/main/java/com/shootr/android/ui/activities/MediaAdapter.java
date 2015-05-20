package com.shootr.android.ui.activities;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import com.shootr.android.R;

public class MediaAdapter extends BaseAdapter {

    private Context context;

    public MediaAdapter(Context context) {
        this.context = context;
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    @Override public View getView(int i, View view, ViewGroup viewGroup) {
        ImageView imageView;
        if (view == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) view;
        }

        imageView.setImageResource(mThumbIds[i]);
        return imageView;
    }

    // references to our images
    private Integer[] mThumbIds = {
      R.drawable.ab_solid_goles, R.drawable.ab_solid_goles,
      R.drawable.ab_solid_goles, R.drawable.ab_solid_goles,
      R.drawable.ab_solid_goles, R.drawable.ab_solid_goles,
      R.drawable.ab_solid_goles, R.drawable.ab_solid_goles,
      R.drawable.ab_solid_goles, R.drawable.ab_solid_goles,
      R.drawable.ab_solid_goles, R.drawable.ab_solid_goles,
      R.drawable.ab_solid_goles, R.drawable.ab_solid_goles,
      R.drawable.ab_solid_goles, R.drawable.ab_solid_goles,
      R.drawable.ab_solid_goles, R.drawable.ab_solid_goles,
      R.drawable.ab_solid_goles, R.drawable.ab_solid_goles,
      R.drawable.ab_solid_goles, R.drawable.ab_solid_goles,
    };

}
