package gm.mobi.android.db.model.objects;

/**
 * Created by InmaculadaAlcon on 14/08/2014.
 */
public class ModeTournament extends Synchronized{
    protected Long mIdModeTournament;
    protected Long mIdTournament;
    protected Long mIdMode;

    public Long getIdModeTournament() {
        return mIdModeTournament;
    }

    public void setIdModeTournament(Long idModeTournament) {
        mIdModeTournament = idModeTournament;
    }

    public Long getIdTournament() {
        return mIdTournament;
    }

    public void setIdTournament(Long idTournament) {
        mIdTournament = idTournament;
    }

    public Long getIdMode() {
        return mIdMode;
    }

    public void setIdMode(Long idMode) {
        mIdMode = idMode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ModeTournament)) return false;
        if (!super.equals(o)) return false;

        ModeTournament that = (ModeTournament) o;

        if (mIdMode != null ? !mIdMode.equals(that.mIdMode) : that.mIdMode != null) return false;
        if (mIdModeTournament != null ? !mIdModeTournament.equals(that.mIdModeTournament) : that.mIdModeTournament != null)
            return false;
        if (mIdTournament != null ? !mIdTournament.equals(that.mIdTournament) : that.mIdTournament != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (mIdModeTournament != null ? mIdModeTournament.hashCode() : 0);
        result = 31 * result + (mIdTournament != null ? mIdTournament.hashCode() : 0);
        result = 31 * result + (mIdMode != null ? mIdMode.hashCode() : 0);
        return result;
    }
}
