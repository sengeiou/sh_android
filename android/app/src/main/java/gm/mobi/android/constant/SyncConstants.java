package gm.mobi.android.constant;


public class SyncConstants {

    // The authority for the sync adapter's content provider
    public static final String AUTHORITY = "gm.mobi.android.db.provider";

    // An account type, in the form of a domain name
    public static final String ACCOUNT_TYPE = "gm.mobi.android";

    // The account name

//    private static Account newAccount= new Account("default_account", ACCOUNT_TYPE);
    //TODO usar un nombre de cuenta real, ya que tenemos login
    public static final String ACCOUNT_NAME = "Shootr";

    //Sync Interval
    public static final long SYNC_INTERVAL_FOLLOWINGS = 24*60*60; //Sync frequency in Seconds - Every 24 hours
    public static final long SYNC_INTERVAL_FOR_REMOVE_SHOTS =24*60*60;//Sync frequency in Seconds - Every 24 hours
    public static final long SYNC_INTERVAL_FOR_NEW_SHOTS = 10*60;


    //Sync callTypes
    public static final String CALL_TYPE = "callType";


    public static final int GET_NEW_SHOTS_CALLTYPE = 300;
    public static final int REMOVE_OLD_SHOTS_CALLTYPE = 301;
    public static final int GET_FOLLOWINGS_CALLTYPE = 302;



}
