package gm.mobi.android.util;

import hugo.weaving.DebugLog;

public class FacebookUtils {

    @DebugLog
    public static String getAvatarUrl(String id){
        return String.format("https://graph.facebook.com/%s/picture?type=large", id);
    }
}
