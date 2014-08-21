package gm.mobi.android.db.model.objects;

/**
 * Created by InmaculadaAlcon on 14/08/2014.
 */
public class MatchBetType extends Synchronized{
    protected Long mIdMatchBetType;
    protected Long mIdProvider;
    protected Long mIdMatch;
    protected Long mIdBetType;

    public Long getIdMatchBetType() {
        return mIdMatchBetType;
    }

    public void setIdMatchBetType(Long idMatchBetType) {
        mIdMatchBetType = idMatchBetType;
    }

    public Long getIdProvider() {
        return mIdProvider;
    }

    public void setIdProvider(Long idProvider) {
        mIdProvider = idProvider;
    }

    public Long getIdMatch() {
        return mIdMatch;
    }

    public void setIdMatch(Long idMatch) {
        mIdMatch = idMatch;
    }

    public Long getIdBetType() {
        return mIdBetType;
    }

    public void setIdBetType(Long idBetType) {
        mIdBetType = idBetType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MatchBetType)) return false;
        if (!super.equals(o)) return false;

        MatchBetType that = (MatchBetType) o;

        if (mIdBetType != null ? !mIdBetType.equals(that.mIdBetType) : that.mIdBetType != null)
            return false;
        if (mIdMatch != null ? !mIdMatch.equals(that.mIdMatch) : that.mIdMatch != null)
            return false;
        if (mIdMatchBetType != null ? !mIdMatchBetType.equals(that.mIdMatchBetType) : that.mIdMatchBetType != null)
            return false;
        if (mIdProvider != null ? !mIdProvider.equals(that.mIdProvider) : that.mIdProvider != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (mIdMatchBetType != null ? mIdMatchBetType.hashCode() : 0);
        result = 31 * result + (mIdProvider != null ? mIdProvider.hashCode() : 0);
        result = 31 * result + (mIdMatch != null ? mIdMatch.hashCode() : 0);
        result = 31 * result + (mIdBetType != null ? mIdBetType.hashCode() : 0);
        return result;
    }
}
