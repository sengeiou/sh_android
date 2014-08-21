package gm.mobi.android.db.model.objects;

import java.util.Date;

/**
 * Created by InmaculadaAlcon on 14/08/2014.
 */
public class EventOfMatch extends Synchronized {
    protected Long mIdEventOfMatch;
    protected Long mIdMatch;
    protected Long mIdEvent;
    protected Date mDateIn;
    protected Long mMatchMinute;
    protected Long mStatus;
    protected Long mIdPeriod;
    protected Long mIdTeam;
    protected Long mLocalScore;
    protected Long mVisitorScore;
    protected String mActorTransmitterName;
    protected String mActorInTransmitterName;
    protected String mActorReceptroName;
    protected Long mIsOwnGoal;
    protected Long mIsPenaltyGoal;
    protected String mPenaltiesScore;

    public Long getIdEventOfMatch() {
        return mIdEventOfMatch;
    }

    public void setIdEventOfMatch(Long idEventOfMatch) {
        mIdEventOfMatch = idEventOfMatch;
    }

    public Long getIdMatch() {
        return mIdMatch;
    }

    public void setIdMatch(Long idMatch) {
        mIdMatch = idMatch;
    }

    public Long getIdEvent() {
        return mIdEvent;
    }

    public void setIdEvent(Long idEvent) {
        mIdEvent = idEvent;
    }

    public Date getDateIn() {
        return mDateIn;
    }

    public void setDateIn(Date dateIn) {
        mDateIn = dateIn;
    }

    public Long getMatchMinute() {
        return mMatchMinute;
    }

    public void setMatchMinute(Long matchMinute) {
        mMatchMinute = matchMinute;
    }

    public Long getStatus() {
        return mStatus;
    }

    public void setStatus(Long status) {
        mStatus = status;
    }

    public Long getIdPeriod() {
        return mIdPeriod;
    }

    public void setIdPeriod(Long idPeriod) {
        mIdPeriod = idPeriod;
    }

    public Long getIdTeam() {
        return mIdTeam;
    }

    public void setIdTeam(Long idTeam) {
        mIdTeam = idTeam;
    }

    public Long getLocalScore() {
        return mLocalScore;
    }

    public void setLocalScore(Long localScore) {
        mLocalScore = localScore;
    }

    public Long getVisitorScore() {
        return mVisitorScore;
    }

    public void setVisitorScore(Long visitorScore) {
        mVisitorScore = visitorScore;
    }

    public String getActorTransmitterName() {
        return mActorTransmitterName;
    }

    public void setActorTransmitterName(String actorTransmitterName) {
        mActorTransmitterName = actorTransmitterName;
    }

    public String getActorInTransmitterName() {
        return mActorInTransmitterName;
    }

    public void setActorInTransmitterName(String actorInTransmitterName) {
        mActorInTransmitterName = actorInTransmitterName;
    }

    public String getActorReceptroName() {
        return mActorReceptroName;
    }

    public void setActorReceptroName(String actorReceptroName) {
        mActorReceptroName = actorReceptroName;
    }

    public Long getIsOwnGoal() {
        return mIsOwnGoal;
    }

    public void setIsOwnGoal(Long isOwnGoal) {
        mIsOwnGoal = isOwnGoal;
    }

    public Long getIsPenaltyGoal() {
        return mIsPenaltyGoal;
    }

    public void setIsPenaltyGoal(Long isPenaltyGoal) {
        mIsPenaltyGoal = isPenaltyGoal;
    }

    public String getPenaltiesScore() {
        return mPenaltiesScore;
    }

    public void setPenaltiesScore(String penaltiesScore) {
        mPenaltiesScore = penaltiesScore;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventOfMatch)) return false;
        if (!super.equals(o)) return false;

        EventOfMatch that = (EventOfMatch) o;

        if (mActorInTransmitterName != null ? !mActorInTransmitterName.equals(that.mActorInTransmitterName) : that.mActorInTransmitterName != null)
            return false;
        if (mActorReceptroName != null ? !mActorReceptroName.equals(that.mActorReceptroName) : that.mActorReceptroName != null)
            return false;
        if (mActorTransmitterName != null ? !mActorTransmitterName.equals(that.mActorTransmitterName) : that.mActorTransmitterName != null)
            return false;
        if (mDateIn != null ? !mDateIn.equals(that.mDateIn) : that.mDateIn != null) return false;
        if (mIdEvent != null ? !mIdEvent.equals(that.mIdEvent) : that.mIdEvent != null)
            return false;
        if (mIdEventOfMatch != null ? !mIdEventOfMatch.equals(that.mIdEventOfMatch) : that.mIdEventOfMatch != null)
            return false;
        if (mIdMatch != null ? !mIdMatch.equals(that.mIdMatch) : that.mIdMatch != null)
            return false;
        if (mIdPeriod != null ? !mIdPeriod.equals(that.mIdPeriod) : that.mIdPeriod != null)
            return false;
        if (mIdTeam != null ? !mIdTeam.equals(that.mIdTeam) : that.mIdTeam != null) return false;
        if (mIsOwnGoal != null ? !mIsOwnGoal.equals(that.mIsOwnGoal) : that.mIsOwnGoal != null)
            return false;
        if (mIsPenaltyGoal != null ? !mIsPenaltyGoal.equals(that.mIsPenaltyGoal) : that.mIsPenaltyGoal != null)
            return false;
        if (mLocalScore != null ? !mLocalScore.equals(that.mLocalScore) : that.mLocalScore != null)
            return false;
        if (mMatchMinute != null ? !mMatchMinute.equals(that.mMatchMinute) : that.mMatchMinute != null)
            return false;
        if (mPenaltiesScore != null ? !mPenaltiesScore.equals(that.mPenaltiesScore) : that.mPenaltiesScore != null)
            return false;
        if (mStatus != null ? !mStatus.equals(that.mStatus) : that.mStatus != null) return false;
        if (mVisitorScore != null ? !mVisitorScore.equals(that.mVisitorScore) : that.mVisitorScore != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (mIdEventOfMatch != null ? mIdEventOfMatch.hashCode() : 0);
        result = 31 * result + (mIdMatch != null ? mIdMatch.hashCode() : 0);
        result = 31 * result + (mIdEvent != null ? mIdEvent.hashCode() : 0);
        result = 31 * result + (mDateIn != null ? mDateIn.hashCode() : 0);
        result = 31 * result + (mMatchMinute != null ? mMatchMinute.hashCode() : 0);
        result = 31 * result + (mStatus != null ? mStatus.hashCode() : 0);
        result = 31 * result + (mIdPeriod != null ? mIdPeriod.hashCode() : 0);
        result = 31 * result + (mIdTeam != null ? mIdTeam.hashCode() : 0);
        result = 31 * result + (mLocalScore != null ? mLocalScore.hashCode() : 0);
        result = 31 * result + (mVisitorScore != null ? mVisitorScore.hashCode() : 0);
        result = 31 * result + (mActorTransmitterName != null ? mActorTransmitterName.hashCode() : 0);
        result = 31 * result + (mActorInTransmitterName != null ? mActorInTransmitterName.hashCode() : 0);
        result = 31 * result + (mActorReceptroName != null ? mActorReceptroName.hashCode() : 0);
        result = 31 * result + (mIsOwnGoal != null ? mIsOwnGoal.hashCode() : 0);
        result = 31 * result + (mIsPenaltyGoal != null ? mIsPenaltyGoal.hashCode() : 0);
        result = 31 * result + (mPenaltiesScore != null ? mPenaltiesScore.hashCode() : 0);
        return result;
    }
}
