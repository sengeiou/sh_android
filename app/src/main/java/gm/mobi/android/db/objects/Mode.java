package gm.mobi.android.db.model.objects;

/**
 * Created by InmaculadaAlcon on 14/08/2014.
 */
public class Mode{
    protected Long mIdMode;
    protected Long mIdLanguage;
    protected Long mOrderBy;
    protected String mName;

    public Long getIdMode() {
        return mIdMode;
    }

    public void setIdMode(Long idMode) {
        mIdMode = idMode;
    }

    public Long getIdLanguage() {
        return mIdLanguage;
    }

    public void setIdLanguage(Long idLanguage) {
        mIdLanguage = idLanguage;
    }

    public Long getOrderBy() {
        return mOrderBy;
    }

    public void setOrderBy(Long orderBy) {
        mOrderBy = orderBy;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Mode)) return false;

        Mode mode = (Mode) o;

        if (mIdLanguage != null ? !mIdLanguage.equals(mode.mIdLanguage) : mode.mIdLanguage != null)
            return false;
        if (mIdMode != null ? !mIdMode.equals(mode.mIdMode) : mode.mIdMode != null) return false;
        if (mName != null ? !mName.equals(mode.mName) : mode.mName != null) return false;
        if (mOrderBy != null ? !mOrderBy.equals(mode.mOrderBy) : mode.mOrderBy != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = mIdMode != null ? mIdMode.hashCode() : 0;
        result = 31 * result + (mIdLanguage != null ? mIdLanguage.hashCode() : 0);
        result = 31 * result + (mOrderBy != null ? mOrderBy.hashCode() : 0);
        result = 31 * result + (mName != null ? mName.hashCode() : 0);
        return result;
    }
}
