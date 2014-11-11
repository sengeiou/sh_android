package com.shootr.android.db.objects;


import java.util.Date;

public class Synchronized {

    protected Date csysBirth;
    protected Date csysModified;
    protected Date csysDeleted;
    protected Integer csysRevision;
    protected String csysSynchronized;

    public Synchronized(){}

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
