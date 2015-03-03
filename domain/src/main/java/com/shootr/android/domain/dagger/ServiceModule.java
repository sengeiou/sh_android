package com.shootr.android.domain.dagger;

import com.shootr.android.domain.service.shot.ShootrShotService;
import dagger.Module;

@Module(
  injects = {
    ShootrShotService.class,
  },
  complete = false
)
public class ServiceModule {

}
