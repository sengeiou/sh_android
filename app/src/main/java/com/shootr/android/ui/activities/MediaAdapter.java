package com.shootr.android.ui.activities;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import com.shootr.android.R;
import com.shootr.android.ui.model.ShotModel;
import com.squareup.picasso.Picasso;
import java.util.List;

public class MediaAdapter extends BaseAdapter {

    private Context context;
    private List<ShotModel> shotsWithMedia;

    public MediaAdapter(Context context, List<ShotModel> shotsWithMedia) {
        this.context = context;
        this.shotsWithMedia = shotsWithMedia;
    }

    public int getCount() {
        return shotsWithMedia.size();
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
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) view;
        }
        ShotModel shotModel = shotsWithMedia.get(i);
        Picasso.with(context).load(shotModel.getImage()).into(imageView);
        return imageView;
    }
}
