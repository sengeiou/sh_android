package com.shootr.mobile.data.entity;

public class LocalSynchronized {

    public static final String SYNC_NEW = "N";
    public static final String SYNC_DELETED = "D";
    public static final String SYNC_UPDATED = "U";
    public static final String SYNC_SYNCHRONIZED = "S";
    protected String synchronizedStatus;

    public String getSynchronizedStatus() {
        return synchronizedStatus;
    }

    public void setSynchronizedStatus(String synchronizedStatus) {
        this.synchronizedStatus = synchronizedStatus;
    }
}
