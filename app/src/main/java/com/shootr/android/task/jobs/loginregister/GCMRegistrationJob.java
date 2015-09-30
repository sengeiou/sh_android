package com.shootr.android.task.jobs.loginregister;

import android.app.Application;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.shootr.android.constant.Constants;
import com.shootr.android.data.bus.Main;
import com.shootr.android.data.entity.DeviceEntity;
import com.shootr.android.data.prefs.GCMAppVersion;
import com.shootr.android.data.prefs.GCMRegistrationId;
import com.shootr.android.data.prefs.IntPreference;
import com.shootr.android.data.prefs.StringPreference;
import com.shootr.android.db.manager.DeviceManager;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.notifications.gcm.GCMConstants;
import com.shootr.android.service.ShootrService;
import com.shootr.android.task.events.loginregister.PushTokenResult;
import com.shootr.android.task.jobs.ShootrBaseJob;
import com.shootr.android.util.VersionUtils;
import com.squareup.otto.Bus;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import javax.inject.Inject;
import timber.log.Timber;

public class GCMRegistrationJob extends ShootrBaseJob<PushTokenResult> {

    public static final int PRIORITY = 3;

    private StringPreference registrationId;
    private IntPreference registeredAppVersion;
    private final GoogleCloudMessaging gcm;
    private ShootrService service;
    private SessionRepository sessionRepository;
    private DeviceManager deviceManager;

    @Inject protected GCMRegistrationJob(Application application, @Main Bus bus, NetworkUtil networkUtil,
      GoogleCloudMessaging gcm, @GCMRegistrationId StringPreference registrationId,
      @GCMAppVersion IntPreference registeredAppVersion, ShootrService service, DeviceManager deviceManager,
      SessionRepository sessionRepository) {
        super(new Params(PRIORITY), application, bus, networkUtil);
        this.registrationId = registrationId;
        this.registeredAppVersion = registeredAppVersion;
        this.gcm = gcm;
        this.service = service;
        this.deviceManager = deviceManager;
        this.sessionRepository = sessionRepository;
    }

    @Override protected void run() throws Exception {
        String regId = getRegistrationId(getContext());
        if (regId.isEmpty()) {
            Timber.d("Registration id not found or invalid. Retrieving a new one from GCM server");
            registerGCM();
        } else {
            Timber.d("Registration id found, skipping registration process.");
        }
    }

    private void registerGCM() throws Exception {
        String regId = gcm.register(GCMConstants.GCM_SENDER_ID);
        Timber.d("Received GCM registration id: %s", regId);
        sendTokenToServer(regId);
        storeRegistrationId(regId);
    }

    private void storeRegistrationId(String regId) {
        registrationId.set(regId);
        registeredAppVersion.set(VersionUtils.getVersionCode(getContext()));
    }

    private void sendTokenToServer(String regId) throws  Exception {
        String uniqueDeviceId = getUniqueDeviceId(getContext());
        DeviceEntity existingDevice = getCurrentDevice(uniqueDeviceId);

        if (existingDevice == null) {
            existingDevice = new DeviceEntity();
            existingDevice.setPlatform(Constants.ANDROID_PLATFORM.intValue());
            existingDevice.setOsVer("Android");
            existingDevice.setModel("Mío");
            existingDevice.setUniqueDevideID(uniqueDeviceId);
            existingDevice.setToken(regId);
            //existingDevice.setBirth(new Date());
            //existingDevice.setRevision(0);
        } else {
            //existingDevice.setRevision(existingDevice.getRevision() + 1);
        }

        existingDevice.setToken(regId);
        //existingDevice.setModified(new Date());

        DeviceEntity deviceEntity = service.updateDevice(existingDevice);
        deviceManager.saveDevice(deviceEntity);
    }


    @Nullable private DeviceEntity getCurrentDevice(String uniqueDeviceId) throws IOException {
        DeviceEntity currentDevice = deviceManager.getDeviceByUniqueId(uniqueDeviceId);
        if (currentDevice == null) {
            currentDevice = service.getDeviceByUniqueId(uniqueDeviceId);
        }
        return currentDevice;
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

    public static String getUniqueDeviceId(Context context) throws Exception {
        String result = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        // If ANDROID is null or is the same ID reported by the emulator
        // (http://code.google.com/p/android/issues/detail?id=10603)
        if (result == null || "9774d56d682e549c".equals(result)) {
            // Get the IMEI (Requires READ_PHONE_STATE)
            TelephonyManager telephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String imei = null;
            if (telephonyMgr != null){
                imei = telephonyMgr.getDeviceId();
            }
            // Get IDs from the current build, extracted from system properties
            String m_szDevIDShort = "35"
              + // we make this look like a valid IMEI
              Build.BOARD.length() % 10 + Build.BRAND.length() % 10 + Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 + Build.DISPLAY.length()
              % 10 + Build.HOST.length() % 10 + Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 + Build.MODEL.length() % 10
              + Build.PRODUCT.length() % 10 + Build.TAGS.length() % 10 + Build.TYPE.length() % 10 + Build.USER.length() % 10; // 13
            // digits
            String wlanmac = null;
            // If WIFI is disabled, some devices returns null when try to get
            // the MAC Address
            // So, only If IMEI is null
            if (imei == null) {
                // Get the Wifi MAC Address (Requires ACCESS_WIFI_STATE)
                try {
                    WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                    if (wm != null){
                        wlanmac = wm.getConnectionInfo().getMacAddress();
                    }
                } catch (Exception e) {
                    Timber.e(e.getMessage(),e);
                }
            }
            String longId = imei + m_szDevIDShort + wlanmac;
            // compute md5
            MessageDigest m = null;
            try {
                m = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                Timber.e(e.getMessage(),e);
            }
            assert m != null;
            m.update(longId.getBytes(), 0, longId.length());
            // get md5 bytes
            byte[] md5Data = m.digest();
            // create a hex string
            String uniqueID = new String();
            for (int i = 0; i < md5Data.length; i++) {
                int b = 0xFF & md5Data[i];
                // if it is a single digit, make sure it have 0 in front (proper
                // padding)
                if (b <= 0xF){
                    uniqueID += "0";
                }
                // add number to string
                uniqueID += Integer.toHexString(b);
            }
            // hex string to uppercase
            result = uniqueID.toUpperCase();
        }
        return result;
    }
}
