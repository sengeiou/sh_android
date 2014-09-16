package gm.mobi.android.db.model.objects;

/**
 * Created by InmaculadaAlcon on 14/08/2014.
 */
public class TVMatch extends Synchronized {
    protected Long mIdMatch;
    protected Long mIdTV;

    public Long getIdMatch() {
        return mIdMatch;
    }

    public void setIdMatch(Long idMatch) {
        mIdMatch = idMatch;
    }

    public Long getIdTV() {
        return mIdTV;
    }

    public void setIdTV(Long idTV) {
        mIdTV = idTV;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TVMatch)) return false;
        if (!super.equals(o)) return false;

        TVMatch tvMatch = (TVMatch) o;

        if (mIdMatch != null ? !mIdMatch.equals(tvMatch.mIdMatch) : tvMatch.mIdMatch != null)
            return false;
        if (mIdTV != null ? !mIdTV.equals(tvMatch.mIdTV) : tvMatch.mIdTV != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (mIdMatch != null ? mIdMatch.hashCode() : 0);
        result = 31 * result + (mIdTV != null ? mIdTV.hashCode() : 0);
        return result;
    }
}
