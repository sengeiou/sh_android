package com.shootr.mobile.util;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import com.shootr.mobile.FacebookController;
import com.shootr.mobile.data.prefs.BooleanPreference;
import com.shootr.mobile.data.prefs.IntPreference;
import com.shootr.mobile.db.ShootrDbOpenHelper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DatabaseVersionUtilsTest {

    public static final int NO_VERSION = 0;
    public static final int VERSION_1 = 1;
    private static final int VERSION_2 = 2;

    @Mock IntPreference preferencesDatabaseVersion;
    @Mock Context context;
    @Mock Version version;
    @Mock SQLiteOpenHelper dbOpenHelper;
    @Mock FacebookController facebookController;
    @Mock BooleanPreference shouldShowIntro;

    private DatabaseVersionUtils databaseVersionUtils;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        databaseVersionUtils = new DatabaseVersionUtils(context, preferencesDatabaseVersion, version,
          dbOpenHelper, shouldShowIntro);
    }

    @Test
    public void shouldUpdateDatabaseVersionIfTheresNoVersion(){
        when(preferencesDatabaseVersion.get()).thenReturn(NO_VERSION);
        when(version.getDatabaseVersion()).thenReturn(VERSION_1);

        databaseVersionUtils.clearDataOnNewerVersion();

        verify(preferencesDatabaseVersion).set(anyInt());
    }

    @Test
    public void shouldUpdateDatabaseVersionIfVersionIsInferior(){
        when(preferencesDatabaseVersion.get()).thenReturn(VERSION_1);
        when(version.getDatabaseVersion()).thenReturn(VERSION_2);

        databaseVersionUtils.clearDataOnNewerVersion();

        verify(preferencesDatabaseVersion).set(anyInt());
    }

    @Test
    public void shouldNotUpdateDatabaseVersionIfVersionIsTheSame(){
        when(preferencesDatabaseVersion.get()).thenReturn(VERSION_1);
        when(version.getDatabaseVersion()).thenReturn(VERSION_1);

        databaseVersionUtils.clearDataOnNewerVersion();

        verify(preferencesDatabaseVersion, never()).set(anyInt());
    }

    @Test public void shouldClearDatabaseWhenVersionIsNewer() throws Exception {
        when(preferencesDatabaseVersion.get()).thenReturn(VERSION_1);
        when(version.getDatabaseVersion()).thenReturn(VERSION_2);

        databaseVersionUtils.clearDataOnNewerVersion();

        verify(context).deleteDatabase(ShootrDbOpenHelper.DATABASE_NAME);
    }


}
