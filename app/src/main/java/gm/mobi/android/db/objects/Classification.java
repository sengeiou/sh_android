package gm.mobi.android.db.model.objects;

/**
 * Created by InmaculadaAlcon on 14/08/2014.
 */
public class Classification extends Synchronized {
    protected Long mIdTournament;
    protected Long mIdTeam;
    protected Long mPl;
    protected Long mWl;
    protected Long mLl;
    protected Long mDl;
    protected Long mGfl;
    protected Long mGal;
    protected Long mPv;
    protected Long mWv;
    protected Long mLv;
    protected Long mDv;
    protected Long mGfv;
    protected Long mGav;
    protected Long mPoints;
    protected Long mWeight;

    public Long getIdTournament() {
        return mIdTournament;
    }

    public void setIdTournament(Long idTournament) {
        mIdTournament = idTournament;
    }

    public Long getIdTeam() {
        return mIdTeam;
    }

    public void setIdTeam(Long idTeam) {
        mIdTeam = idTeam;
    }

    public Long getPl() {
        return mPl;
    }

    public void setPl(Long pl) {
        mPl = pl;
    }

    public Long getWl() {
        return mWl;
    }

    public void setWl(Long wl) {
        mWl = wl;
    }

    public Long getLl() {
        return mLl;
    }

    public void setLl(Long ll) {
        mLl = ll;
    }

    public Long getDl() {
        return mDl;
    }

    public void setDl(Long dl) {
        mDl = dl;
    }

    public Long getGfl() {
        return mGfl;
    }

    public void setGfl(Long gfl) {
        mGfl = gfl;
    }

    public Long getGal() {
        return mGal;
    }

    public void setGal(Long gal) {
        mGal = gal;
    }

    public Long getPv() {
        return mPv;
    }

    public void setPv(Long pv) {
        mPv = pv;
    }

    public Long getWv() {
        return mWv;
    }

    public void setWv(Long wv) {
        mWv = wv;
    }

    public Long getLv() {
        return mLv;
    }

    public void setLv(Long lv) {
        mLv = lv;
    }

    public Long getDv() {
        return mDv;
    }

    public void setDv(Long dv) {
        mDv = dv;
    }

    public Long getGfv() {
        return mGfv;
    }

    public void setGfv(Long gfv) {
        mGfv = gfv;
    }

    public Long getGav() {
        return mGav;
    }

    public void setGav(Long gav) {
        mGav = gav;
    }

    public Long getPoints() {
        return mPoints;
    }

    public void setPoints(Long points) {
        mPoints = points;
    }

    public Long getWeight() {
        return mWeight;
    }

    public void setWeight(Long weight) {
        mWeight = weight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Classification)) return false;
        if (!super.equals(o)) return false;

        Classification that = (Classification) o;

        if (mDl != null ? !mDl.equals(that.mDl) : that.mDl != null) return false;
        if (mDv != null ? !mDv.equals(that.mDv) : that.mDv != null) return false;
        if (mGal != null ? !mGal.equals(that.mGal) : that.mGal != null) return false;
        if (mGav != null ? !mGav.equals(that.mGav) : that.mGav != null) return false;
        if (mGfl != null ? !mGfl.equals(that.mGfl) : that.mGfl != null) return false;
        if (mGfv != null ? !mGfv.equals(that.mGfv) : that.mGfv != null) return false;
        if (mIdTeam != null ? !mIdTeam.equals(that.mIdTeam) : that.mIdTeam != null) return false;
        if (mIdTournament != null ? !mIdTournament.equals(that.mIdTournament) : that.mIdTournament != null)
            return false;
        if (mLl != null ? !mLl.equals(that.mLl) : that.mLl != null) return false;
        if (mLv != null ? !mLv.equals(that.mLv) : that.mLv != null) return false;
        if (mPl != null ? !mPl.equals(that.mPl) : that.mPl != null) return false;
        if (mPoints != null ? !mPoints.equals(that.mPoints) : that.mPoints != null) return false;
        if (mPv != null ? !mPv.equals(that.mPv) : that.mPv != null) return false;
        if (mWeight != null ? !mWeight.equals(that.mWeight) : that.mWeight != null) return false;
        if (mWl != null ? !mWl.equals(that.mWl) : that.mWl != null) return false;
        if (mWv != null ? !mWv.equals(that.mWv) : that.mWv != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (mIdTournament != null ? mIdTournament.hashCode() : 0);
        result = 31 * result + (mIdTeam != null ? mIdTeam.hashCode() : 0);
        result = 31 * result + (mPl != null ? mPl.hashCode() : 0);
        result = 31 * result + (mWl != null ? mWl.hashCode() : 0);
        result = 31 * result + (mLl != null ? mLl.hashCode() : 0);
        result = 31 * result + (mDl != null ? mDl.hashCode() : 0);
        result = 31 * result + (mGfl != null ? mGfl.hashCode() : 0);
        result = 31 * result + (mGal != null ? mGal.hashCode() : 0);
        result = 31 * result + (mPv != null ? mPv.hashCode() : 0);
        result = 31 * result + (mWv != null ? mWv.hashCode() : 0);
        result = 31 * result + (mLv != null ? mLv.hashCode() : 0);
        result = 31 * result + (mDv != null ? mDv.hashCode() : 0);
        result = 31 * result + (mGfv != null ? mGfv.hashCode() : 0);
        result = 31 * result + (mGav != null ? mGav.hashCode() : 0);
        result = 31 * result + (mPoints != null ? mPoints.hashCode() : 0);
        result = 31 * result + (mWeight != null ? mWeight.hashCode() : 0);
        return result;
    }
}
