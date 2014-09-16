package gm.mobi.android.db.model.objects;

import java.util.Date;

/**
 * Created by InmaculadaAlcon on 14/08/2014.
 */
public class Match extends Synchronized {
    protected Long mIdMatch;
    protected Long mIdLocalTeam;
    protected Long mIdVisitorTeam;
    protected Long mIdRound;
    protected Date mMatchDate;
    protected Long mNotConfirmedMatchDate;
    protected Long mLocalScore;
    protected Long mVisitorScore;
    protected Long mMatchType;
    protected Long mScorePenaltiesLocalTeam;
    protected Long mScorePenaltiesVisitorTeam;
    protected Date mEndTime;
    protected Long mMatchState;
    protected Long mIdPreviousMatch;
    protected Long mIdWinnerTeam;
    protected Date mOvertimeStartDate;
    protected Date mSecondHalfStartDate;
    protected Date mStartDate;

    public Long getIdMatch() {
        return mIdMatch;
    }

    public void setIdMatch(Long idMatch) {
        mIdMatch = idMatch;
    }

    public Long getIdLocalTeam() {
        return mIdLocalTeam;
    }

    public void setIdLocalTeam(Long idLocalTeam) {
        mIdLocalTeam = idLocalTeam;
    }

    public Long getIdVisitorTeam() {
        return mIdVisitorTeam;
    }

    public void setIdVisitorTeam(Long idVisitorTeam) {
        mIdVisitorTeam = idVisitorTeam;
    }

    public Long getIdRound() {
        return mIdRound;
    }

    public void setIdRound(Long idRound) {
        mIdRound = idRound;
    }

    public Date getMatchDate() {
        return mMatchDate;
    }

    public void setMatchDate(Date matchDate) {
        mMatchDate = matchDate;
    }

    public Long getNotConfirmedMatchDate() {
        return mNotConfirmedMatchDate;
    }

    public void setNotConfirmedMatchDate(Long notConfirmedMatchDate) {
        mNotConfirmedMatchDate = notConfirmedMatchDate;
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

    public Long getMatchType() {
        return mMatchType;
    }

    public void setMatchType(Long matchType) {
        mMatchType = matchType;
    }

    public Long getScorePenaltiesLocalTeam() {
        return mScorePenaltiesLocalTeam;
    }

    public void setScorePenaltiesLocalTeam(Long scorePenaltiesLocalTeam) {
        mScorePenaltiesLocalTeam = scorePenaltiesLocalTeam;
    }

    public Long getScorePenaltiesVisitorTeam() {
        return mScorePenaltiesVisitorTeam;
    }

    public void setScorePenaltiesVisitorTeam(Long scorePenaltiesVisitorTeam) {
        mScorePenaltiesVisitorTeam = scorePenaltiesVisitorTeam;
    }

    public Date getEndTime() {
        return mEndTime;
    }

    public void setEndTime(Date endTime) {
        mEndTime = endTime;
    }

    public Long getMatchState() {
        return mMatchState;
    }

    public void setMatchState(Long matchState) {
        mMatchState = matchState;
    }

    public Long getIdPreviousMatch() {
        return mIdPreviousMatch;
    }

    public void setIdPreviousMatch(Long idPreviousMatch) {
        mIdPreviousMatch = idPreviousMatch;
    }

    public Long getIdWinnerTeam() {
        return mIdWinnerTeam;
    }

    public void setIdWinnerTeam(Long idWinnerTeam) {
        mIdWinnerTeam = idWinnerTeam;
    }

    public Date getOvertimeStartDate() {
        return mOvertimeStartDate;
    }

    public void setOvertimeStartDate(Date overtimeStartDate) {
        mOvertimeStartDate = overtimeStartDate;
    }

    public Date getSecondHalfStartDate() {
        return mSecondHalfStartDate;
    }

    public void setSecondHalfStartDate(Date secondHalfStartDate) {
        mSecondHalfStartDate = secondHalfStartDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Match)) return false;
        if (!super.equals(o)) return false;

        Match match = (Match) o;

        if (mEndTime != null ? !mEndTime.equals(match.mEndTime) : match.mEndTime != null)
            return false;
        if (mIdLocalTeam != null ? !mIdLocalTeam.equals(match.mIdLocalTeam) : match.mIdLocalTeam != null)
            return false;
        if (mIdMatch != null ? !mIdMatch.equals(match.mIdMatch) : match.mIdMatch != null)
            return false;
        if (mIdPreviousMatch != null ? !mIdPreviousMatch.equals(match.mIdPreviousMatch) : match.mIdPreviousMatch != null)
            return false;
        if (mIdRound != null ? !mIdRound.equals(match.mIdRound) : match.mIdRound != null)
            return false;
        if (mIdVisitorTeam != null ? !mIdVisitorTeam.equals(match.mIdVisitorTeam) : match.mIdVisitorTeam != null)
            return false;
        if (mIdWinnerTeam != null ? !mIdWinnerTeam.equals(match.mIdWinnerTeam) : match.mIdWinnerTeam != null)
            return false;
        if (mLocalScore != null ? !mLocalScore.equals(match.mLocalScore) : match.mLocalScore != null)
            return false;
        if (mMatchDate != null ? !mMatchDate.equals(match.mMatchDate) : match.mMatchDate != null)
            return false;
        if (mMatchState != null ? !mMatchState.equals(match.mMatchState) : match.mMatchState != null)
            return false;
        if (mMatchType != null ? !mMatchType.equals(match.mMatchType) : match.mMatchType != null)
            return false;
        if (mNotConfirmedMatchDate != null ? !mNotConfirmedMatchDate.equals(match.mNotConfirmedMatchDate) : match.mNotConfirmedMatchDate != null)
            return false;
        if (mOvertimeStartDate != null ? !mOvertimeStartDate.equals(match.mOvertimeStartDate) : match.mOvertimeStartDate != null)
            return false;
        if (mScorePenaltiesLocalTeam != null ? !mScorePenaltiesLocalTeam.equals(match.mScorePenaltiesLocalTeam) : match.mScorePenaltiesLocalTeam != null)
            return false;
        if (mScorePenaltiesVisitorTeam != null ? !mScorePenaltiesVisitorTeam.equals(match.mScorePenaltiesVisitorTeam) : match.mScorePenaltiesVisitorTeam != null)
            return false;
        if (mSecondHalfStartDate != null ? !mSecondHalfStartDate.equals(match.mSecondHalfStartDate) : match.mSecondHalfStartDate != null)
            return false;
        if (mStartDate != null ? !mStartDate.equals(match.mStartDate) : match.mStartDate != null)
            return false;
        if (mVisitorScore != null ? !mVisitorScore.equals(match.mVisitorScore) : match.mVisitorScore != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (mIdMatch != null ? mIdMatch.hashCode() : 0);
        result = 31 * result + (mIdLocalTeam != null ? mIdLocalTeam.hashCode() : 0);
        result = 31 * result + (mIdVisitorTeam != null ? mIdVisitorTeam.hashCode() : 0);
        result = 31 * result + (mIdRound != null ? mIdRound.hashCode() : 0);
        result = 31 * result + (mMatchDate != null ? mMatchDate.hashCode() : 0);
        result = 31 * result + (mNotConfirmedMatchDate != null ? mNotConfirmedMatchDate.hashCode() : 0);
        result = 31 * result + (mLocalScore != null ? mLocalScore.hashCode() : 0);
        result = 31 * result + (mVisitorScore != null ? mVisitorScore.hashCode() : 0);
        result = 31 * result + (mMatchType != null ? mMatchType.hashCode() : 0);
        result = 31 * result + (mScorePenaltiesLocalTeam != null ? mScorePenaltiesLocalTeam.hashCode() : 0);
        result = 31 * result + (mScorePenaltiesVisitorTeam != null ? mScorePenaltiesVisitorTeam.hashCode() : 0);
        result = 31 * result + (mEndTime != null ? mEndTime.hashCode() : 0);
        result = 31 * result + (mMatchState != null ? mMatchState.hashCode() : 0);
        result = 31 * result + (mIdPreviousMatch != null ? mIdPreviousMatch.hashCode() : 0);
        result = 31 * result + (mIdWinnerTeam != null ? mIdWinnerTeam.hashCode() : 0);
        result = 31 * result + (mOvertimeStartDate != null ? mOvertimeStartDate.hashCode() : 0);
        result = 31 * result + (mSecondHalfStartDate != null ? mSecondHalfStartDate.hashCode() : 0);
        result = 31 * result + (mStartDate != null ? mStartDate.hashCode() : 0);
        return result;
    }
}
