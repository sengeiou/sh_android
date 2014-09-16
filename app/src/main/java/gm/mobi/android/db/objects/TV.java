package gm.mobi.android.db.model.objects;

/**
 * Created by InmaculadaAlcon on 14/08/2014.
 */
public class TV extends Synchronized{
    protected Long mIdTV;
    protected String mName;
    protected Long mTvType;

    public Long getIdTV() {
        return mIdTV;
    }

    public void setIdTV(Long idTV) {
        mIdTV = idTV;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public Long getTvType() {
        return mTvType;
    }

    public void setTvType(Long tvType) {
        mTvType = tvType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TV)) return false;
        if (!super.equals(o)) return false;

        TV tv = (TV) o;

        if (mIdTV != null ? !mIdTV.equals(tv.mIdTV) : tv.mIdTV != null) return false;
        if (mName != null ? !mName.equals(tv.mName) : tv.mName != null) return false;
        if (mTvType != null ? !mTvType.equals(tv.mTvType) : tv.mTvType != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (mIdTV != null ? mIdTV.hashCode() : 0);
        result = 31 * result + (mName != null ? mName.hashCode() : 0);
        result = 31 * result + (mTvType != null ? mTvType.hashCode() : 0);
        return result;
    }
}
