package gm.mobi.android.constant;

import gm.mobi.android.BuildConfig;

public class SyncConstants {

    // The authority for the sync adapter's content provider
    public static final String AUTHORITY = BuildConfig.PACKAGE_NAME+".sync.provider";

    // An account type, in the form of a domain name
    public static final String ACCOUNT_TYPE = "gm.mobi.android";

    // The account name

//    private static Account newAccount= new Account("default_account", ACCOUNT_TYPE);
    //TODO usar un nombre de cuenta real, ya que tenemos login
    public static final String ACCOUNT_NAME = "Shootr";


    //Sync Interval Constants
    public static final long MILLISECONDS_PER_SECOND = 1000L;
    public static final long SECONDS_PER_MINUTE = 60L;
    public static final long SYNC_INTERVAL_IN_MINUTES = 1L;
    public static final long SYNC_INTERVAL_SHOTS = 10L;
    //Sync Interval
    public static final long SYNC_INTERVAL_FOR_SHOTS = 10000L;


    //Sync callTypes
    public static String CALL_TYPE = "callType";


    public static int GET_SHOTS_CALL_TYPE = 300;




}
