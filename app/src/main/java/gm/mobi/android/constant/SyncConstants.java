package gm.mobi.android.constant;


public class SyncConstants {

    // The authority for the sync adapter's content provider
    public static final String AUTHORITY = "gm.mobi.android.db.provider";

    // An account type, in the form of a domain name
    public static final String ACCOUNT_TYPE = "shootr.mobi.android";

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
    public static final long SYNC_INTERVAL_FOR_REMOVE_SHOTS =24*60*60;//Sync frequency in Seconds - Every 24 hours


    //Sync callTypes
    public static String CALL_TYPE = "callType";


    public static int GET_SHOTS_CALL_TYPE = 300;
    public static int REMOVE_OLD_SHOTS = 301;




}
