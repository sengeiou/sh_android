package com.shootr.android.domain.dagger;

import com.shootr.android.domain.service.shot.ShootrShotService;
import com.shootr.android.domain.service.user.ShootrUserService;
import dagger.Module;

@Module(
  injects = {
    ShootrShotService.class, ShootrUserService.class,
  },
  complete = false
)
public class ServiceModule {

}
