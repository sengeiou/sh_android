package com.shootr.android.util;

/**
 * Created by arturo on 4/05/15.
 */
public class LongIdUtils {

    public LongIdUtils(){

    }

    public Long transformIdFromStringtoLong(String id){
        return id != null? Long.valueOf(id) : null;
    }

}
