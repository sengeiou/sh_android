package gm.mobi.android.db.objects;

public class Team extends Synchronized{
    Long mIdTeam;
    String mOfficialName;
    String mClubName;
    String mShortName;
    String mTlaName;

    public Team(){}

    public Long getIdTeam() {
        return mIdTeam;
    }

    public void setIdTeam(Long idTeam) {
        mIdTeam = idTeam;
    }

    public String getOfficialName() {
        return mOfficialName;
    }

    public void setOfficialName(String officialName) {
        mOfficialName = officialName;
    }

    public String getClubName() {
        return mClubName;
    }

    public void setClubName(String clubName) {
        mClubName = clubName;
    }

    public String getShortName() {
        return mShortName;
    }

    public void setShortName(String shortName) {
        mShortName = shortName;
    }

    public String getTlaName() {
        return mTlaName;
    }

    public void setTlaName(String tlaName) {
        mTlaName = tlaName;
    }
}
