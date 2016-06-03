package com.shootr.mobile.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.shootr.mobile.R;

public class StreamReadWriteModeAdapter extends ArrayAdapter {

  private static final Integer[] STREAM_READ_WRITE_MODE_RESOURCE = {
      R.string.read_write_mode_public_title, R.string.read_write_mode_view_only_title
  };
  private static final int HEIGHT_WITHOUT_DESCRIPTION = 100;

  private Context context;
  private Integer selectedItem = 0;

  public StreamReadWriteModeAdapter(Context context, int textViewResourceId) {
    super(context, textViewResourceId, STREAM_READ_WRITE_MODE_RESOURCE);
    this.context = context;
  }

  public View getCustomView(int position, View convertView, boolean descriptionVisibility,
      ViewGroup parent) {
    LayoutInflater inflater =
        (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View layout = inflater.inflate(R.layout.item_spinner_read_write_mode, parent, false);

    TextView streamReadWriteMode = (TextView) layout.findViewById(R.id.stream_read_write_mode);
    TextView streamReadWriteModeDescription =
        (TextView) layout.findViewById(R.id.stream_read_write_mode_description);
    ImageView streamReadModeDrawable = (ImageView) layout.findViewById(R.id.spinner_drawable);

    streamReadWriteMode.setText(STREAM_READ_WRITE_MODE_RESOURCE[position]);
    setupDrawable(position, streamReadModeDrawable);
    setupDescriptionVisibility(descriptionVisibility, streamReadWriteModeDescription);
    setupDescription(position, streamReadWriteModeDescription);
    setupSelectedItemBackground(position, descriptionVisibility, layout);
    setupLayoutHeight(descriptionVisibility, layout);
    return layout;
  }

  private void setupLayoutHeight(boolean descriptionVisibility, View layout) {
    if (!descriptionVisibility) {
      ViewGroup.LayoutParams params = layout.getLayoutParams();
      params.height = HEIGHT_WITHOUT_DESCRIPTION;
      layout.setLayoutParams(params);
    }
  }

  private void setupSelectedItemBackground(int position, boolean descriptionVisibility,
      View layout) {
    if (descriptionVisibility && position == selectedItem) {
      layout.setBackgroundColor(context.getResources().getColor(R.color.gray_92));
    }
  }

  private void setupDescription(int position, TextView streamReadWriteModeDescription) {
    if (position == 0) {
      streamReadWriteModeDescription.setText(
          context.getResources().getString(R.string.read_write_mode_public_description));
    } else {
      streamReadWriteModeDescription.setText(
          context.getResources().getString(R.string.read_write_mode_view_only_description));
    }
  }

  private void setupDescriptionVisibility(boolean descriptionVisibility,
      TextView streamReadWriteModeDescription) {
    if (descriptionVisibility) {
      streamReadWriteModeDescription.setVisibility(View.VISIBLE);
    } else {
      streamReadWriteModeDescription.setVisibility(View.GONE);
    }
  }

  private void setupDrawable(int position, ImageView streamReadModeDrawable) {
    if (position == 0) {
      streamReadModeDrawable.setImageDrawable(
          context.getResources().getDrawable(R.drawable.ic_stream_public));
    } else {
      streamReadModeDrawable.setImageDrawable(
          context.getResources().getDrawable(R.drawable.ic_stream_read_only));
    }
  }

  public void setSelectedItem(Integer selectedItem) {
    this.selectedItem = selectedItem;
  }

  @Override public View getDropDownView(int position, View convertView, ViewGroup parent) {
    return getCustomView(position, convertView, true, parent);
  }

  @Override public View getView(int position, View convertView, ViewGroup parent) {
    return getCustomView(position, convertView, false, parent);
  }
}
