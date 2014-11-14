package com.shootr.android.util;

import android.text.TextUtils;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import timber.log.Timber;

/**
 * A gravatar is a dynamic image resource that is requested from the
 * gravatar.com server. This class calculates the gravatar url and fetches
 * gravatar images. See http://en.gravatar.com/site/implement/url .
 *
 * This class is thread-safe, Gravatar objects can be shared.
 *
 * Usage example:
 *
 * <code>
 * Gravatar gravatar = new Gravatar();
 * gravatar.setSize(128);
 * gravatar.setRating(GravatarRating.GENERAL_AUDIENCES);
 * gravatar.setDefaultImage(GravatarDefaultImage.IDENTICON);
 * String url = gravatar.getUrl("yg@wareninja.com");
 *
 * -> pass request listener to get callback response (as in example app!)
 * gravatar.download("yg@wareninja.com", new GenericRequestListener() {
 *
 * 		});
 * </code>
 *
 * Original base source from https://github.com/ralfebert/jgravatar
 * Adapted/extended for ANDROID by yg@wareninja.com
 */
public final class Gravatar {

    public final static int DEFAULT_SIZE = 150;
    public final static String API_GRAVATAR_BASE_URL = "http://www.gravatar.com";
    public final static String API_GRAVATAR_AVATAR = "/avatar/";

    protected static final String TAG = "Gravatar";

    private int size = DEFAULT_SIZE;

    public Gravatar () {
    }

    /**
     * Specify a gravatar size between 1 and 512 pixels. If you omit this, a
     * default size of 80 pixels is used.
     */
    public Gravatar setSize(int sizeInPixels) {
        if(sizeInPixels >= 1 && sizeInPixels <= 512){
            this.size = sizeInPixels;
        }
        return this;
    }


    /**
     * Returns the Gravatar URL for the given email address.
     */
    public String getImageUrl(String email) {
        if(TextUtils.isEmpty(email)){
            return null;
        }

        // hexadecimal MD5 hash of the requested user's lowercased email address
        // with all whitespace trimmed
        String emailHash = md5Hex(email.toLowerCase().trim());
        String params = formatUrlParameters();
        return API_GRAVATAR_BASE_URL+API_GRAVATAR_AVATAR
                + emailHash //+ ".jpg"
                + params;
    }

    public static String md5Hex (String message) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return hex (md.digest(message.getBytes("CP1252")));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            Timber.e(e.getMessage());
        }
        return null;
    }

    public static String hex(byte[] array) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < array.length; ++i) {
            sb.append(Integer.toHexString((array[i]& 0xFF) | 0x100).substring(1,3));
        }
        return sb.toString();
    }

    public String getProfileUrl(String email) {
        if(TextUtils.isEmpty(email))return null;

        // hexadecimal MD5 hash of the requested user's lowercased email address
        // with all whitespace trimmed
        String emailHash = md5Hex(email.toLowerCase().trim());
        return API_GRAVATAR_BASE_URL + "/"+ emailHash + ".json";
    }

    private String formatUrlParameters() {
        List<String> params = new ArrayList<String>();

        params.add("d=404");
        if (size != DEFAULT_SIZE){
            params.add("s=" + size);
        }


        if (params.isEmpty()) {
            return "";
        }else {
            return "?" + TextUtils.join("&", params.toArray());
        }
    }

}