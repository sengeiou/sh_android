package gm.mobi.android.task.jobs.loginregister;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.squareup.otto.Bus;
import gm.mobi.android.data.prefs.GCMAppVersion;
import gm.mobi.android.data.prefs.GCMRegistrationId;
import gm.mobi.android.data.prefs.IntPreference;
import gm.mobi.android.data.prefs.StringPreference;
import gm.mobi.android.gcm.GCMConstants;
import gm.mobi.android.task.events.loginregister.PushTokenResult;
import gm.mobi.android.task.jobs.BagdadBaseJob;
import gm.mobi.android.util.VersionUtils;
import java.io.IOException;
import java.sql.SQLException;
import javax.inject.Inject;
import timber.log.Timber;

public class GCMRegistrationJob extends BagdadBaseJob<PushTokenResult> {

    public static final int PRIORITY = 3;

    private StringPreference registrationId;
    private IntPreference registeredAppVersion;
    private final GoogleCloudMessaging gcm;

    //TODO check for play services apk and version!

    @Inject protected GCMRegistrationJob(Application application, Bus bus, NetworkUtil networkUtil,
      GoogleCloudMessaging gcm, @GCMRegistrationId StringPreference registrationId,
      @GCMAppVersion IntPreference registeredAppVersion) {
        super(new Params(PRIORITY), application, bus, networkUtil);
        this.registrationId = registrationId;
        this.registeredAppVersion = registeredAppVersion;
        this.gcm = gcm;
    }

    public void init() {

    }

    @Override protected void run() throws SQLException, IOException {
        String regId = getRegistrationId(getContext());
        if (regId.isEmpty()) {
            Timber.d("Registration id not found or invalid. Retrieving a new one from GCM server");
            registerGCM();
        }
    }

    private void registerGCM() throws IOException {
        String regId = gcm.register(GCMConstants.GCM_SENDER_ID);
        Timber.d("Received GCM registration id: %s", regId);
        storeRegistrationId(regId);
        //TODO send to server
    }

    private void storeRegistrationId(String regId) {
        registrationId.set(regId);
        registeredAppVersion.set(VersionUtils.getVersionCode(getContext()));
    }

    @Override protected void createDatabase() {
    }

    @Override protected void setDatabaseToManagers(SQLiteDatabase db) {
    }

    @Override protected boolean isNetworkRequired() {
        return true;
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    private String getRegistrationId(Context context) {
        if (!registrationId.isSet()) {
            Timber.i("Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = registeredAppVersion.get();
        int currentVersion = VersionUtils.getVersionCode(context);
        if (registeredVersion != currentVersion) {
            Timber.i("App version changed. Needs new GCM registration id");
            return "";
        }
        return registrationId.get();
    }
}
