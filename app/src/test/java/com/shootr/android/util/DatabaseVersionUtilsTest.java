package com.shootr.android.util;

import android.content.SharedPreferences;
import com.shootr.android.data.prefs.IntPreference;
import com.shootr.android.db.ShootrDbOpenHelper;
import java.util.Set;
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
        when(preferencesDatabaseVersion.get()).thenReturn(0);
        when(version.getDatabaseVersion()).thenReturn(1);
        setupSharedPreferencesClear();
        databaseVersionUtils.clearDataOnDatabaseVersionUpdated();
        verify(preferencesDatabaseVersion).set(anyInt());
    }

    @Test
    public void shouldUpdateDatabaseVersionIfVersionIsInferior(){
        when(preferencesDatabaseVersion.get()).thenReturn(1);
        when(version.getDatabaseVersion()).thenReturn(1);
        setupSharedPreferencesClear();
        databaseVersionUtils.clearDataOnDatabaseVersionUpdated();
        verify(preferencesDatabaseVersion).set(anyInt());
    }

    @Test
    public void shouldNotUpdateDatabaseVersionIfVersionIsTheSame(){
        when(preferencesDatabaseVersion.get()).thenReturn(1);
        when(version.getDatabaseVersion()).thenReturn(1);
        databaseVersionUtils.clearDataOnDatabaseVersionUpdated();
        verify(preferencesDatabaseVersion, never()).set(anyInt());
    }

    private void setupSharedPreferencesClear() {
        when(sharedPreferences.edit().clear()).thenReturn(new SharedPreferences.Editor() {
            @Override public SharedPreferences.Editor putString(String s, String s1) {
                return null;
            }

            @Override public SharedPreferences.Editor putStringSet(String s, Set<String> set) {
                return null;
            }

            @Override public SharedPreferences.Editor putInt(String s, int i) {
                return null;
            }

            @Override public SharedPreferences.Editor putLong(String s, long l) {
                return null;
            }

            @Override public SharedPreferences.Editor putFloat(String s, float v) {
                return null;
            }

            @Override public SharedPreferences.Editor putBoolean(String s, boolean b) {
                return null;
            }

            @Override public SharedPreferences.Editor remove(String s) {
                return null;
            }

            @Override public SharedPreferences.Editor clear() {
                return null;
            }

            @Override public boolean commit() {
                return false;
            }

            @Override public void apply() {

            }
        });
    }
}
