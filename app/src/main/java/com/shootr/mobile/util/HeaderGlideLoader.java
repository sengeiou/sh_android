package com.shootr.mobile.util;

import android.content.Context;
import com.bumptech.glide.load.model.Headers;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader;

public class HeaderGlideLoader extends BaseGlideUrlLoader<String> {

  private static final String ACCEPT = "image/jpeg, image/png";
  private static final Headers REQUEST_HEADERS = new LazyHeaders.Builder()
      .addHeader("Accept", ACCEPT)
      .build();

  public HeaderGlideLoader(Context context) {
    super(context);
  }

  @Override protected String getUrl(String model, int width, int height) {
    return model;
  }

  @Override
  protected Headers getHeaders(String model, int width, int height) {
    return REQUEST_HEADERS;
  }
}
