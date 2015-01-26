package com.shootr.android.data.entity;


import java.util.Date;

public class Synchronized {

    public static final String SYNC_NEW = "N";
    public static final String SYNC_DELETED = "D";
    public static final String SYNC_UPDATED = "U";
    public static final String SYNC_SYNCHRONIZED = "S";

    protected Date csysBirth;
    protected Date csysModified;
    protected Date csysDeleted;
    protected Integer csysRevision;
    protected String csysSynchronized;

    public Synchronized(){

    }

    public Date getCsysBirth() {
        return csysBirth;
    }

    public void setCsysBirth(Date csysBirth) {
        this.csysBirth = csysBirth;
    }

    public Date getCsysModified() {
        return csysModified;
    }

    public void setCsysModified(Date csysModified) {
        this.csysModified = csysModified;
    }

    public Date getCsysDeleted() {
        return csysDeleted;
    }

    public void setCsysDeleted(Date csysDeleted) {
        this.csysDeleted = csysDeleted;
    }

    public Integer getCsysRevision() {
        return csysRevision;
    }

    public void setCsysRevision(Integer csysRevision) {
        this.csysRevision = csysRevision;
    }

    public String getCsysSynchronized() {
        return csysSynchronized;
    }

    public void setCsysSynchronized(String csysSynchronized) {
        this.csysSynchronized = csysSynchronized;
    }
}
