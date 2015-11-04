package com.shootr.mobile.stetho;

import com.shootr.mobile.ShootrDebugApplication;
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
