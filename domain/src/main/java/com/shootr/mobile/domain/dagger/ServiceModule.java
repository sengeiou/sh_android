package com.shootr.mobile.domain.dagger;

import dagger.Module;

@Module(
  injects = {
    com.shootr.mobile.domain.service.shot.ShootrShotService.class, com.shootr.mobile.domain.service.user.ShootrUserService.class,
  },
  complete = false
)
public class ServiceModule {

}
