package gm.mobi.android;

import dagger.Module;
import gm.mobi.android.integrationtests.ShotDtoFactoryTest;
import gm.mobi.android.integrationtests.UserDtoFactoryTest;

@Module(
  includes = GolesModule.class,
  overrides = true,
  injects = { ShotDtoFactoryTest.class, TestGolesApplication.class, UserDtoFactoryTest.class}
)
public class GolesTestModule {

}
