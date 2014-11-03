package gm.mobi.android.util;

public class FacebookUtils {
    public static String getAvatarUrl(String id){
        return String.format("https://graph.facebook.com/%s/picture?type=large", id);
    }
}
