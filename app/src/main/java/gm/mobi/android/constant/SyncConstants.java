package gm.mobi.android.constant;


import gm.mobi.android.BuildConfig;

public class SyncConstants {

    // The authority for the sync adapter's content provider
    public static final String AUTHORITY = BuildConfig.PACKAGE_NAME+".sync.provider";

    // An account type, in the form of a domain name
    public static final String ACCOUNT_TYPE = "gm.mobi.android";

    // The account name
    //TODO usar un nombre de cuenta real, ya que tenemos login
    public static final String ACCOUNT_NAME = "Goles";
}
