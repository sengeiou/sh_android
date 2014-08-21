package gm.mobi.android.db.model.objects;

import java.util.Date;

/**
 * Created by InmaculadaAlcon on 14/08/2014.
 */
public class Synchronized {
    protected Date mCsys_birth;
    protected Date mCsys_modified;
    protected Date mCsys_deleted;
    protected Integer mCsys_revision;
    protected String mCsys_synchronized;

    public Date getCsys_birth() {
        return mCsys_birth;
    }

    public void setCsys_birth(Date csys_birth) {
        mCsys_birth = csys_birth;
    }

    public Date getCsys_modified() {
        return mCsys_modified;
    }

    public void setCsys_modified(Date csys_modified) {
        mCsys_modified = csys_modified;
    }

    public Date getCsys_deleted() {
        return mCsys_deleted;
    }

    public void setCsys_deleted(Date csys_deleted) {
        mCsys_deleted = csys_deleted;
    }

    public Integer getCsys_revision() {
        return mCsys_revision;
    }

    public void setCsys_revision(Integer csys_revision) {
        mCsys_revision = csys_revision;
    }

    public String getCsys_synchronized() {
        return mCsys_synchronized;
    }

    public void setCsys_synchronized(String csys_synchronized) {
        mCsys_synchronized = csys_synchronized;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Synchronized)) return false;

        Synchronized that = (Synchronized) o;

        if (mCsys_birth != null ? !mCsys_birth.equals(that.mCsys_birth) : that.mCsys_birth != null)
            return false;
        if (mCsys_deleted != null ? !mCsys_deleted.equals(that.mCsys_deleted) : that.mCsys_deleted != null)
            return false;
        if (mCsys_modified != null ? !mCsys_modified.equals(that.mCsys_modified) : that.mCsys_modified != null)
            return false;
        if (mCsys_revision != null ? !mCsys_revision.equals(that.mCsys_revision) : that.mCsys_revision != null)
            return false;
        if (mCsys_synchronized != null ? !mCsys_synchronized.equals(that.mCsys_synchronized) : that.mCsys_synchronized != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = mCsys_birth != null ? mCsys_birth.hashCode() : 0;
        result = 31 * result + (mCsys_modified != null ? mCsys_modified.hashCode() : 0);
        result = 31 * result + (mCsys_deleted != null ? mCsys_deleted.hashCode() : 0);
        result = 31 * result + (mCsys_revision != null ? mCsys_revision.hashCode() : 0);
        result = 31 * result + (mCsys_synchronized != null ? mCsys_synchronized.hashCode() : 0);
        return result;
    }
}

