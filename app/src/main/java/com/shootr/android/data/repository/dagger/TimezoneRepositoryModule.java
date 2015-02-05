package com.shootr.android.data.repository.dagger;

import com.shootr.android.data.repository.TimezoneRepositoryImpl;
import com.shootr.android.domain.repository.TimezoneRepository;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
  injects = {
    TimezoneRepositoryImpl.class,
  },
  complete = false,
  library = true)
public class TimezoneRepositoryModule {

    @Provides @Singleton TimezoneRepository provideTimezoneRepository(TimezoneRepositoryImpl timezoneRepository) {
        return timezoneRepository;
    }
}
