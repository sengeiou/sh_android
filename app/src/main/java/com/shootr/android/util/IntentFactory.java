package com.shootr.android.util;

import android.content.Intent;
import android.net.Uri;

/** Creates {@link Intent}s for launching into external applications. */
public interface IntentFactory {
  Intent createUrlIntent(String url);

  IntentFactory REAL = new IntentFactory() {
    @Override public Intent createUrlIntent(String url) {
      Intent intent = new Intent(Intent.ACTION_VIEW);
      intent.setData(Uri.parse(url));
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

      return intent;
    }
  };
}
