package com.shootr.mobile.util;

import android.content.Context;
import com.bumptech.glide.load.model.GenericLoaderFactory;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import java.io.InputStream;

public class HeaderGlideLoaderFactory implements ModelLoaderFactory<String, InputStream> {
  @Override
  public ModelLoader<String, InputStream> build(Context context, GenericLoaderFactory factories) {
    return new HeaderGlideLoader(context);
  }

  @Override public void teardown() {
    /* no-op */
  }
}
