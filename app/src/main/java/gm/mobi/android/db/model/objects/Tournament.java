package gm.mobi.android.db.model.objects;

import java.util.Date;

/**
 * Created by InmaculadaAlcon on 14/08/2014.
 */
public class Tournament extends Synchronized {
    protected Long mIdTournament;
    protected Long mYear;
    protected Long mStatus;
    protected Date mStartDate;
    protected Date mEndDate;
    protected String mImageName;
    protected Long mIsLeague;
    protected String mName;
    protected Long mOrderBy;
    protected Long mFirstC;
    protected Long mSecondC;
    protected Long mThirdC;
    protected Long mFourthC;
    protected Long mVisibleApp;

    public Long getIdTournament() {
        return mIdTournament;
    }

    public void setIdTournament(Long idTournament) {
        mIdTournament = idTournament;
    }

    public Long getYear() {
        return mYear;
    }

    public void setYear(Long year) {
        mYear = year;
    }

    public Long getStatus() {
        return mStatus;
    }

    public void setStatus(Long status) {
        mStatus = status;
    }

    public Date getStartDate() {
        return mStartDate;
    }

    public void setStartDate(Date startDate) {
        mStartDate = startDate;
    }

    public Date getEndDate() {
        return mEndDate;
    }

    public void setEndDate(Date endDate) {
        mEndDate = endDate;
    }

    public String getImageName() {
        return mImageName;
    }

    public void setImageName(String imageName) {
        mImageName = imageName;
    }

    public Long getIsLeague() {
        return mIsLeague;
    }

    public void setIsLeague(Long isLeague) {
        mIsLeague = isLeague;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public Long getOrderBy() {
        return mOrderBy;
    }

    public void setOrderBy(Long orderBy) {
        mOrderBy = orderBy;
    }

    public Long getFirstC() {
        return mFirstC;
    }

    public void setFirstC(Long firstC) {
        mFirstC = firstC;
    }

    public Long getSecondC() {
        return mSecondC;
    }

    public void setSecondC(Long secondC) {
        mSecondC = secondC;
    }

    public Long getThirdC() {
        return mThirdC;
    }

    public void setThirdC(Long thirdC) {
        mThirdC = thirdC;
    }

    public Long getFourthC() {
        return mFourthC;
    }

    public void setFourthC(Long fourthC) {
        mFourthC = fourthC;
    }

    public Long getVisibleApp() {
        return mVisibleApp;
    }

    public void setVisibleApp(Long visibleApp) {
        mVisibleApp = visibleApp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tournament)) return false;
        if (!super.equals(o)) return false;

        Tournament that = (Tournament) o;

        if (mEndDate != null ? !mEndDate.equals(that.mEndDate) : that.mEndDate != null)
            return false;
        if (mFirstC != null ? !mFirstC.equals(that.mFirstC) : that.mFirstC != null) return false;
        if (mFourthC != null ? !mFourthC.equals(that.mFourthC) : that.mFourthC != null)
            return false;
        if (mIdTournament != null ? !mIdTournament.equals(that.mIdTournament) : that.mIdTournament != null)
            return false;
        if (mImageName != null ? !mImageName.equals(that.mImageName) : that.mImageName != null)
            return false;
        if (mIsLeague != null ? !mIsLeague.equals(that.mIsLeague) : that.mIsLeague != null)
            return false;
        if (mName != null ? !mName.equals(that.mName) : that.mName != null) return false;
        if (mOrderBy != null ? !mOrderBy.equals(that.mOrderBy) : that.mOrderBy != null)
            return false;
        if (mSecondC != null ? !mSecondC.equals(that.mSecondC) : that.mSecondC != null)
            return false;
        if (mStartDate != null ? !mStartDate.equals(that.mStartDate) : that.mStartDate != null)
            return false;
        if (mStatus != null ? !mStatus.equals(that.mStatus) : that.mStatus != null) return false;
        if (mThirdC != null ? !mThirdC.equals(that.mThirdC) : that.mThirdC != null) return false;
        if (mVisibleApp != null ? !mVisibleApp.equals(that.mVisibleApp) : that.mVisibleApp != null)
            return false;
        if (mYear != null ? !mYear.equals(that.mYear) : that.mYear != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (mIdTournament != null ? mIdTournament.hashCode() : 0);
        result = 31 * result + (mYear != null ? mYear.hashCode() : 0);
        result = 31 * result + (mStatus != null ? mStatus.hashCode() : 0);
        result = 31 * result + (mStartDate != null ? mStartDate.hashCode() : 0);
        result = 31 * result + (mEndDate != null ? mEndDate.hashCode() : 0);
        result = 31 * result + (mImageName != null ? mImageName.hashCode() : 0);
        result = 31 * result + (mIsLeague != null ? mIsLeague.hashCode() : 0);
        result = 31 * result + (mName != null ? mName.hashCode() : 0);
        result = 31 * result + (mOrderBy != null ? mOrderBy.hashCode() : 0);
        result = 31 * result + (mFirstC != null ? mFirstC.hashCode() : 0);
        result = 31 * result + (mSecondC != null ? mSecondC.hashCode() : 0);
        result = 31 * result + (mThirdC != null ? mThirdC.hashCode() : 0);
        result = 31 * result + (mFourthC != null ? mFourthC.hashCode() : 0);
        result = 31 * result + (mVisibleApp != null ? mVisibleApp.hashCode() : 0);
        return result;
    }
}
