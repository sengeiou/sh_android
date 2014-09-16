package gm.mobi.android.db.objects;


import java.util.Date;

public abstract class Synchronized {

    protected Date csys_birth;
    protected Date csys_modified;
    protected Date csys_deleted;
    protected Integer csys_revision;
    protected String csys_synchronized;

    public Date getCsys_birth() {
        return csys_birth;
    }

    public void setCsys_birth(Date csys_birth) {
        this.csys_birth = csys_birth;
    }

    public Date getCsys_modified() {
        return csys_modified;
    }

    public void setCsys_modified(Date csys_modified) {
        this.csys_modified = csys_modified;
    }

    public Date getCsys_deleted() {
        return csys_deleted;
    }

    public void setCsys_deleted(Date csys_deleted) {
        this.csys_deleted = csys_deleted;
    }

    public Integer getCsys_revision() {
        return csys_revision;
    }

    public void setCsys_revision(Integer csys_revision) {
        this.csys_revision = csys_revision;
    }

    public String getCsys_synchronized() {
        return csys_synchronized;
    }

    public void setCsys_synchronized(String csys_synchronized) {
        this.csys_synchronized = csys_synchronized;
    }
}
