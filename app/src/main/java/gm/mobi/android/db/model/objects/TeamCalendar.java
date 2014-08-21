package gm.mobi.android.db.model.objects;

import java.util.Date;

/**
 * Created by InmaculadaAlcon on 14/08/2014.
 */
public class TeamCalendar {
    protected Long mIdTeamLocal;
    protected Long mIdTeamVisitor;
    protected String mLocalNameShort;
    protected String mLocalNameTiny;
    protected String mVisitorNameShort;
    protected String mVisitorNameTiny;
    protected Long mIdRound;
    protected String mRoundDescritpion;
    protected Date mRoundStartDate;
    protected Date mRoundEndDate;
    protected Long mIdMatch;
    protected Date mDateMatch;
    protected Long mDateMatchNotConfirmed;
    protected String mListTV;
    protected Long mScoreLocal;
    protected Long mScoreVisitor;
    protected Long mMatchState;
    protected Long mIdTournament;
    protected String mTournamentName;
    protected Long mTournamentStatus;
    protected Long mIdMode;
    protected String mModeName;

    public Long getIdTeamLocal() {
        return mIdTeamLocal;
    }

    public void setIdTeamLocal(Long idTeamLocal) {
        mIdTeamLocal = idTeamLocal;
    }

    public Long getIdTeamVisitor() {
        return mIdTeamVisitor;
    }

    public void setIdTeamVisitor(Long idTeamVisitor) {
        mIdTeamVisitor = idTeamVisitor;
    }

    public String getLocalNameShort() {
        return mLocalNameShort;
    }

    public void setLocalNameShort(String localNameShort) {
        mLocalNameShort = localNameShort;
    }

    public String getLocalNameTiny() {
        return mLocalNameTiny;
    }

    public void setLocalNameTiny(String localNameTiny) {
        mLocalNameTiny = localNameTiny;
    }

    public String getVisitorNameShort() {
        return mVisitorNameShort;
    }

    public void setVisitorNameShort(String visitorNameShort) {
        mVisitorNameShort = visitorNameShort;
    }

    public String getVisitorNameTiny() {
        return mVisitorNameTiny;
    }

    public void setVisitorNameTiny(String visitorNameTiny) {
        mVisitorNameTiny = visitorNameTiny;
    }

    public Long getIdRound() {
        return mIdRound;
    }

    public void setIdRound(Long idRound) {
        mIdRound = idRound;
    }

    public String getRoundDescritpion() {
        return mRoundDescritpion;
    }

    public void setRoundDescritpion(String roundDescritpion) {
        mRoundDescritpion = roundDescritpion;
    }

    public Date getRoundStartDate() {
        return mRoundStartDate;
    }

    public void setRoundStartDate(Date roundStartDate) {
        mRoundStartDate = roundStartDate;
    }

    public Date getRoundEndDate() {
        return mRoundEndDate;
    }

    public void setRoundEndDate(Date roundEndDate) {
        mRoundEndDate = roundEndDate;
    }

    public Long getIdMatch() {
        return mIdMatch;
    }

    public void setIdMatch(Long idMatch) {
        mIdMatch = idMatch;
    }

    public Date getDateMatch() {
        return mDateMatch;
    }

    public void setDateMatch(Date dateMatch) {
        mDateMatch = dateMatch;
    }

    public Long getDateMatchNotConfirmed() {
        return mDateMatchNotConfirmed;
    }

    public void setDateMatchNotConfirmed(Long dateMatchNotConfirmed) {
        mDateMatchNotConfirmed = dateMatchNotConfirmed;
    }

    public String getListTV() {
        return mListTV;
    }

    public void setListTV(String listTV) {
        mListTV = listTV;
    }

    public Long getScoreLocal() {
        return mScoreLocal;
    }

    public void setScoreLocal(Long scoreLocal) {
        mScoreLocal = scoreLocal;
    }

    public Long getScoreVisitor() {
        return mScoreVisitor;
    }

    public void setScoreVisitor(Long scoreVisitor) {
        mScoreVisitor = scoreVisitor;
    }

    public Long getMatchState() {
        return mMatchState;
    }

    public void setMatchState(Long matchState) {
        mMatchState = matchState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TeamCalendar)) return false;

        TeamCalendar that = (TeamCalendar) o;

        if (mDateMatch != null ? !mDateMatch.equals(that.mDateMatch) : that.mDateMatch != null)
            return false;
        if (mDateMatchNotConfirmed != null ? !mDateMatchNotConfirmed.equals(that.mDateMatchNotConfirmed) : that.mDateMatchNotConfirmed != null)
            return false;
        if (mIdMatch != null ? !mIdMatch.equals(that.mIdMatch) : that.mIdMatch != null)
            return false;
        if (mIdMode != null ? !mIdMode.equals(that.mIdMode) : that.mIdMode != null) return false;
        if (mIdRound != null ? !mIdRound.equals(that.mIdRound) : that.mIdRound != null)
            return false;
        if (mIdTeamLocal != null ? !mIdTeamLocal.equals(that.mIdTeamLocal) : that.mIdTeamLocal != null)
            return false;
        if (mIdTeamVisitor != null ? !mIdTeamVisitor.equals(that.mIdTeamVisitor) : that.mIdTeamVisitor != null)
            return false;
        if (mIdTournament != null ? !mIdTournament.equals(that.mIdTournament) : that.mIdTournament != null)
            return false;
        if (mListTV != null ? !mListTV.equals(that.mListTV) : that.mListTV != null) return false;
        if (mLocalNameShort != null ? !mLocalNameShort.equals(that.mLocalNameShort) : that.mLocalNameShort != null)
            return false;
        if (mLocalNameTiny != null ? !mLocalNameTiny.equals(that.mLocalNameTiny) : that.mLocalNameTiny != null)
            return false;
        if (mMatchState != null ? !mMatchState.equals(that.mMatchState) : that.mMatchState != null)
            return false;
        if (mModeName != null ? !mModeName.equals(that.mModeName) : that.mModeName != null)
            return false;
        if (mRoundDescritpion != null ? !mRoundDescritpion.equals(that.mRoundDescritpion) : that.mRoundDescritpion != null)
            return false;
        if (mRoundEndDate != null ? !mRoundEndDate.equals(that.mRoundEndDate) : that.mRoundEndDate != null)
            return false;
        if (mRoundStartDate != null ? !mRoundStartDate.equals(that.mRoundStartDate) : that.mRoundStartDate != null)
            return false;
        if (mScoreLocal != null ? !mScoreLocal.equals(that.mScoreLocal) : that.mScoreLocal != null)
            return false;
        if (mScoreVisitor != null ? !mScoreVisitor.equals(that.mScoreVisitor) : that.mScoreVisitor != null)
            return false;
        if (mTournamentName != null ? !mTournamentName.equals(that.mTournamentName) : that.mTournamentName != null)
            return false;
        if (mTournamentStatus != null ? !mTournamentStatus.equals(that.mTournamentStatus) : that.mTournamentStatus != null)
            return false;
        if (mVisitorNameShort != null ? !mVisitorNameShort.equals(that.mVisitorNameShort) : that.mVisitorNameShort != null)
            return false;
        if (mVisitorNameTiny != null ? !mVisitorNameTiny.equals(that.mVisitorNameTiny) : that.mVisitorNameTiny != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = mIdTeamLocal != null ? mIdTeamLocal.hashCode() : 0;
        result = 31 * result + (mIdTeamVisitor != null ? mIdTeamVisitor.hashCode() : 0);
        result = 31 * result + (mLocalNameShort != null ? mLocalNameShort.hashCode() : 0);
        result = 31 * result + (mLocalNameTiny != null ? mLocalNameTiny.hashCode() : 0);
        result = 31 * result + (mVisitorNameShort != null ? mVisitorNameShort.hashCode() : 0);
        result = 31 * result + (mVisitorNameTiny != null ? mVisitorNameTiny.hashCode() : 0);
        result = 31 * result + (mIdRound != null ? mIdRound.hashCode() : 0);
        result = 31 * result + (mRoundDescritpion != null ? mRoundDescritpion.hashCode() : 0);
        result = 31 * result + (mRoundStartDate != null ? mRoundStartDate.hashCode() : 0);
        result = 31 * result + (mRoundEndDate != null ? mRoundEndDate.hashCode() : 0);
        result = 31 * result + (mIdMatch != null ? mIdMatch.hashCode() : 0);
        result = 31 * result + (mDateMatch != null ? mDateMatch.hashCode() : 0);
        result = 31 * result + (mDateMatchNotConfirmed != null ? mDateMatchNotConfirmed.hashCode() : 0);
        result = 31 * result + (mListTV != null ? mListTV.hashCode() : 0);
        result = 31 * result + (mScoreLocal != null ? mScoreLocal.hashCode() : 0);
        result = 31 * result + (mScoreVisitor != null ? mScoreVisitor.hashCode() : 0);
        result = 31 * result + (mMatchState != null ? mMatchState.hashCode() : 0);
        result = 31 * result + (mIdTournament != null ? mIdTournament.hashCode() : 0);
        result = 31 * result + (mTournamentName != null ? mTournamentName.hashCode() : 0);
        result = 31 * result + (mTournamentStatus != null ? mTournamentStatus.hashCode() : 0);
        result = 31 * result + (mIdMode != null ? mIdMode.hashCode() : 0);
        result = 31 * result + (mModeName != null ? mModeName.hashCode() : 0);
        return result;
    }
}
