package gm.mobi.android.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import gm.mobi.android.Constants;


/**
 * Created by InmaculadaAlcon on 20/08/2014.
 */
public class Utils {

    public static String getRetrieveAliasByEntity(int mEntity){
        switch (mEntity){
            case Constants.APPADVICE_ENTITY:
                return Constants.RETRIEVE_APPADVICE_ALIAS;
            case Constants.BETTYPE_ENTITY:
                return Constants.RETRIEVE_BETTYPE_ALIAS;
            case Constants.BETTYPEODD_ENTITY:
                return Constants.RETRIEVE_BETTYPEODD_ALIAS;
            case Constants.CAMPAIGN_ENTITY:
                return Constants.RETRIEVE_CAMPAIGN_ALIAS;
            case Constants.CAMPAIGNMESSAGE_ENTITY:
                return Constants.RETRIEVE_CAMPAIGNMESSAGE_ALIAS;
            case Constants.CLASSIFICATION_ENTITY:
                return Constants.RETRIEVE_CLASSIFICATION_ALIAS;
            case Constants.EVENTOFMATCH_ENTITY:
                return Constants.RETRIEVE_EVENTOFMATCH_ALIAS;
            case Constants.LINEUP_ENTITY:
                return Constants.RETRIEVE_LINEUP_ALIAS;
            case Constants.MATCH_ENTITY:
                return Constants.RETRIEVE_MATCH_ALIAS;
            case Constants.MESSAGE_ENTITY:
                return Constants.RETRIEVE_MESSAGE_ALIAS;
            case Constants.MATCHBETTYPE_ENTITY:
                return Constants.RETRIEVE_MATCHBETTYPE_ALIAS;
            case Constants.MODE_ENTITY:
                return Constants.RETRIEVE_MODE_ALIAS;
            case Constants.MODETOURNAMENT_ENTITY:
                return Constants.RETRIEVE_MODETOURNAMENT_ALIAS;
            case Constants.PROVIDER_ENTITY:
                return Constants.RETRIEVE_PROVIDER_ALIAS;
            case Constants.TV_MATCH_ENTITY:
                return Constants.RETRIEVE_TVMATCH_ALIAS;
            case Constants.TV_ENTITY:
                return  Constants.RETRIEVE_TV_ALIAS;
            case Constants.SML_ENTITY:
                return Constants.RETRIEVE_SML_ALIAS;
            case Constants.ROUND_ENTITY:
                return Constants.RETRIEVE_ROUND_ALIAS;
            case Constants.TOURNAMENT_ENTITY:
                return Constants.RETRIEVE_TOURNAMENT_ALIAS;
            case Constants.SUBSCRITPION_ENTITY:
                return Constants.RETRIEVE_SUBSCRIPTION_ALIAS;
        }
        return null;
    }

    public static Long getAppVersionForService(Context mContext){
        PackageInfo pInfo;
        String version = "";
        try{
            pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(),0);
            version = pInfo.versionName;
            String[] versionSplit = version.split("\\.");
            String partOne = versionSplit[0];
            for(int i = partOne.length();i<3;i++){
                partOne+="0";
            }
            String partTwo = versionSplit[1];
            for(int i = partTwo.length();i<3;i++){
               partTwo+="0";
            }
            version = partOne + partTwo;
            if(versionSplit.length>2){
                version += versionSplit[2];
            }
        }catch(PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }
        return Long.valueOf(version);
    }
}
