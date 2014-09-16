package gm.mobi.android.db.model.objects;

/**
 * Created by InmaculadaAlcon on 14/08/2014.
 */
public class LineUp extends Synchronized {
    protected Long mIdLineUp;
    protected Long mIdTeam;
    protected String mLineUp;
    protected String mLineUpExtraInfo;

    public Long getIdLineUp() {
        return mIdLineUp;
    }

    public void setIdLineUp(Long idLineUp) {
        mIdLineUp = idLineUp;
    }

    public Long getIdTeam() {
        return mIdTeam;
    }

    public void setIdTeam(Long idTeam) {
        mIdTeam = idTeam;
    }

    public String getLineUp() {
        return mLineUp;
    }

    public void setLineUp(String lineUp) {
        mLineUp = lineUp;
    }

    public String getLineUpExtraInfo() {
        return mLineUpExtraInfo;
    }

    public void setLineUpExtraInfo(String lineUpExtraInfo) {
        mLineUpExtraInfo = lineUpExtraInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LineUp)) return false;
        if (!super.equals(o)) return false;

        LineUp lineUp = (LineUp) o;

        if (mIdLineUp != null ? !mIdLineUp.equals(lineUp.mIdLineUp) : lineUp.mIdLineUp != null)
            return false;
        if (mIdTeam != null ? !mIdTeam.equals(lineUp.mIdTeam) : lineUp.mIdTeam != null)
            return false;
        if (mLineUp != null ? !mLineUp.equals(lineUp.mLineUp) : lineUp.mLineUp != null)
            return false;
        if (mLineUpExtraInfo != null ? !mLineUpExtraInfo.equals(lineUp.mLineUpExtraInfo) : lineUp.mLineUpExtraInfo != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (mIdLineUp != null ? mIdLineUp.hashCode() : 0);
        result = 31 * result + (mIdTeam != null ? mIdTeam.hashCode() : 0);
        result = 31 * result + (mLineUp != null ? mLineUp.hashCode() : 0);
        result = 31 * result + (mLineUpExtraInfo != null ? mLineUpExtraInfo.hashCode() : 0);
        return result;
    }
}
