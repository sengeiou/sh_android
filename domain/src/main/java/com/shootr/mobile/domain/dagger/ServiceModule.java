package com.shootr.mobile.domain.dagger;

import com.shootr.mobile.domain.service.shot.ShootrShotService;
import com.shootr.mobile.domain.service.user.ShootrUserService;
import dagger.Module;

@Module(
  injects = {
    ShootrShotService.class, ShootrUserService.class,
  },
  complete = false) public class ServiceModule {

}
