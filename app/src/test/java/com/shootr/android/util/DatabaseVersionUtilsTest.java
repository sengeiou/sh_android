package com.shootr.android.util;

import android.content.SharedPreferences;
import com.shootr.android.data.prefs.IntPreference;
import com.shootr.android.db.ShootrDbOpenHelper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DatabaseVersionUtilsTest {

    public static final int NO_VERSION_STORED = 0;
    public static final int VERSION_1 = 1;
    private static final int VERSION_2 = 2;

    @Mock IntPreference preferencesDatabaseVersion;
    private DatabaseVersionUtils databaseVersionUtils;
    @Mock ShootrDbOpenHelper shootrDbOpenHelper;
    @Mock Version version;

    SharedPreferences sharedPreferences = mock(SharedPreferences.class, RETURNS_DEEP_STUBS);

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        databaseVersionUtils = new DatabaseVersionUtils(preferencesDatabaseVersion, version, shootrDbOpenHelper, sharedPreferences);
    }

    @Test
    public void shouldUpdateDatabaseVersionIfTheresNoVersion(){
        when(preferencesDatabaseVersion.get()).thenReturn(NO_VERSION_STORED);
        when(version.getDatabaseVersion()).thenReturn(VERSION_1);

        databaseVersionUtils.clearDataOnDatabaseVersionUpdated();

        verify(preferencesDatabaseVersion).set(anyInt());
    }

    @Test
    public void shouldUpdateDatabaseVersionIfVersionIsInferior(){
        when(preferencesDatabaseVersion.get()).thenReturn(VERSION_1);
        when(version.getDatabaseVersion()).thenReturn(VERSION_2);

        databaseVersionUtils.clearDataOnDatabaseVersionUpdated();

        verify(preferencesDatabaseVersion).set(anyInt());
    }

    @Test
    public void shouldNotUpdateDatabaseVersionIfVersionIsTheSame(){
        when(preferencesDatabaseVersion.get()).thenReturn(VERSION_1);
        when(version.getDatabaseVersion()).thenReturn(VERSION_1);

        databaseVersionUtils.clearDataOnDatabaseVersionUpdated();

        verify(preferencesDatabaseVersion, never()).set(anyInt());
    }
}
