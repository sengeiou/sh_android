package com.shootr.android.stetho;

import com.shootr.android.ShootrDebugApplication;
import dagger.Module;

@Module(
  injects = {
    ShootrDebugApplication.class,
  },
  complete = false,
  library = true
)
public class StethoModule {

}
