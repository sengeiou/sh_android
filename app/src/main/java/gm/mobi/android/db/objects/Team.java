package gm.mobi.android.db.model.objects;

/**
 * Created by InmaculadaAlcon on 14/08/2014.
 */
public class Team extends Synchronized{
    protected Long mIdTeam;
    protected String mName;
    protected String mNameShort;
    protected String mNameTiny;
    protected String mUrlImage;
    protected Long mIdMode;
    protected Long mIsNationalTeam;

    public Long getIdTeam() {
        return mIdTeam;
    }

    public void setIdTeam(Long idTeam) {
        mIdTeam = idTeam;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getNameShort() {
        return mNameShort;
    }

    public void setNameShort(String nameShort) {
        mNameShort = nameShort;
    }

    public String getNameTiny() {
        return mNameTiny;
    }

    public void setNameTiny(String nameTiny) {
        mNameTiny = nameTiny;
    }

    public String getUrlImage() {
        return mUrlImage;
    }

    public void setUrlImage(String urlImage) {
        mUrlImage = urlImage;
    }

    public Long getIdMode() {
        return mIdMode;
    }

    public void setIdMode(Long idMode) {
        mIdMode = idMode;
    }

    public Long getIsNationalTeam() {
        return mIsNationalTeam;
    }

    public void setIsNationalTeam(Long isNationalTeam) {
        mIsNationalTeam = isNationalTeam;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Team)) return false;
        if (!super.equals(o)) return false;

        Team team = (Team) o;

        if (mIdMode != null ? !mIdMode.equals(team.mIdMode) : team.mIdMode != null) return false;
        if (mIdTeam != null ? !mIdTeam.equals(team.mIdTeam) : team.mIdTeam != null) return false;
        if (mIsNationalTeam != null ? !mIsNationalTeam.equals(team.mIsNationalTeam) : team.mIsNationalTeam != null)
            return false;
        if (mName != null ? !mName.equals(team.mName) : team.mName != null) return false;
        if (mNameShort != null ? !mNameShort.equals(team.mNameShort) : team.mNameShort != null)
            return false;
        if (mNameTiny != null ? !mNameTiny.equals(team.mNameTiny) : team.mNameTiny != null)
            return false;
        if (mUrlImage != null ? !mUrlImage.equals(team.mUrlImage) : team.mUrlImage != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (mIdTeam != null ? mIdTeam.hashCode() : 0);
        result = 31 * result + (mName != null ? mName.hashCode() : 0);
        result = 31 * result + (mNameShort != null ? mNameShort.hashCode() : 0);
        result = 31 * result + (mNameTiny != null ? mNameTiny.hashCode() : 0);
        result = 31 * result + (mUrlImage != null ? mUrlImage.hashCode() : 0);
        result = 31 * result + (mIdMode != null ? mIdMode.hashCode() : 0);
        result = 31 * result + (mIsNationalTeam != null ? mIsNationalTeam.hashCode() : 0);
        return result;
    }
}
