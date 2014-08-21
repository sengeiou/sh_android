package gm.mobi.android.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

import gm.mobi.android.constant.Constants;

/**
 * Created by InmaculadaAlcon on 11/08/2014.
 */
public class GMAccountGenerator {

    private static AccountManager mAccountManager;
    private static Account mAccount;

    public static Account createSyncAccount(Context context){
        //Create account type and default account
        //Get an instance of Android Account Manager
        //Add the account name and type, no password or user data. If ok, return the Account object, otherwise report an error
        mAccount = new Account(Constants.ACCOUNT_NAME,Constants.ACCOUNT_TYPE);
        mAccountManager = (AccountManager)context.getSystemService(Context.ACCOUNT_SERVICE);
        if(mAccountManager.addAccountExplicitly(mAccount,null,null)){
            //If don't set android:syncable="true" in <provider> manifest's tag, then call.setIsSyncable(account,AUTHORITY,1) here.
        }else{
            //The account exists or some other error occurred. Log this, or handle it internally.
        }
        return mAccount;
    }

    public static Account getAccount() {
        return mAccount;
    }
}
