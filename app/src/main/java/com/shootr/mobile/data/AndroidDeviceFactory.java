package com.shootr.mobile.data;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.shootr.mobile.constant.Constants;
import com.shootr.mobile.data.dagger.ApplicationContext;
import com.shootr.mobile.domain.Device;
import com.shootr.mobile.domain.exception.GCMNotAvailableException;
import com.shootr.mobile.domain.utils.DeviceFactory;
import com.shootr.mobile.domain.utils.LocaleProvider;
import com.shootr.mobile.notifications.gcm.GCMConstants;
import com.shootr.mobile.util.Version;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.inject.Inject;

import timber.log.Timber;

public class AndroidDeviceFactory implements DeviceFactory {

    private final GoogleCloudMessaging gcm;
    private final Context context;
    private final Version version;
    private final LocaleProvider localeProvider;

    @Inject
    public AndroidDeviceFactory(GoogleCloudMessaging gcm,
      @ApplicationContext Context context,
      Version version,
      LocaleProvider localeProvider) {
        this.gcm = gcm;
        this.context = context;
        this.version = version;
        this.localeProvider = localeProvider;
    }

    @Override
    public Device createDevice() {
        Device device = new Device();
        device.setUniqueDevideID(generateUniqueDeviceId());
        device.setToken(getNewGcmToken());
        device.setPlatform(Constants.ANDROID_PLATFORM.intValue());
        device.setOsVer(Build.VERSION.RELEASE);
        device.setModel(Build.MODEL);
        device.setAppVer(version.getVersionName());
        device.setLocale(localeProvider.getLocale());
        return device;
    }

    @Override
    public Device updateDevice(Device device) {
        if (needsNewGcmToken(device)) {
            device.setToken(getNewGcmToken());
        }
        device.setUniqueDevideID(generateUniqueDeviceId());
        device.setPlatform(Constants.ANDROID_PLATFORM.intValue());
        device.setOsVer(Build.VERSION.RELEASE);
        device.setModel(Build.MODEL);
        device.setAppVer(version.getVersionName());
        device.setLocale(localeProvider.getLocale());
        return device;
    }

    private boolean needsNewGcmToken(Device device) {
        String registeredVersion = device.getAppVer();
        String currentVersion = version.getVersionName();
        return !currentVersion.equals(registeredVersion);
    }

    private String getNewGcmToken() {
        try {
            return gcm.register(GCMConstants.GCM_SENDER_ID);
        } catch (IOException | NullPointerException e) {
            Timber.e(e, "Error registering GCM");
            throw new GCMNotAvailableException(e);
        }
    }

    // Welcome to Mordor
    private String generateUniqueDeviceId() {
        String result = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        // If ANDROID is null or is the same ID reported by the emulator
        // (http://code.google.com/p/android/issues/detail?id=10603)
        if (result == null || "9774d56d682e549c".equals(result)) {
            // Get the IMEI (Requires READ_PHONE_STATE)
            TelephonyManager telephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String imei = null;
            if (telephonyMgr != null) {
                imei = telephonyMgr.getDeviceId();
            }
            // Get IDs from the current build, extracted from system properties
            String m_szDevIDShort = "35"
              +
              // we make this look like a valid IMEI
              Build.BOARD.length() % 10
              + Build.BRAND.length() % 10
              + Build.CPU_ABI.length() % 10
              + Build.DEVICE.length() % 10
              + Build.DISPLAY.length() % 10
              + Build.HOST.length() % 10
              + Build.ID.length() % 10
              + Build.MANUFACTURER.length() % 10
              + Build.MODEL.length() % 10
              + Build.PRODUCT.length() % 10
              + Build.TAGS.length() % 10
              + Build.TYPE.length() % 10
              + Build.USER.length() % 10; // 13
            // digits
            String wlanmac = null;
            // If WIFI is disabled, some devices returns null when try to get
            // the MAC Address
            // So, only If IMEI is null
            if (imei == null) {
                // Get the Wifi MAC Address (Requires ACCESS_WIFI_STATE)
                try {
                    WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                    if (wm != null) {
                        wlanmac = wm.getConnectionInfo().getMacAddress();
                    }
                } catch (Exception e) {
                    Timber.e(e.getMessage(), e);
                }
            }
            String longId = imei + m_szDevIDShort + wlanmac;
            // compute md5
            MessageDigest m = null;
            try {
                m = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                Timber.e(e.getMessage(), e);
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
                if (b <= 0xF) {
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
