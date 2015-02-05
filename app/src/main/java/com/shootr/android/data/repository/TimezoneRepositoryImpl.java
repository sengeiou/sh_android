package com.shootr.android.data.repository;

import com.shootr.android.domain.repository.TimezoneRepository;
import java.util.TimeZone;
import javax.inject.Inject;

public class TimezoneRepositoryImpl implements TimezoneRepository {

    @Inject public TimezoneRepositoryImpl() {
    }

    @Override public TimeZone getCurrentTimezone() {
        return TimeZone.getDefault();
    }
}
