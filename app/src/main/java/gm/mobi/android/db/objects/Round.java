package gm.mobi.android.db.model.objects;

import java.util.Date;

/**
 * Created by InmaculadaAlcon on 14/08/2014.
 */
public class Round {
    protected Long mIdRound;
    protected Long mIdTournament;
    protected Date mStartDate;
    protected Date mEndDate;
    protected Long mRoundType;
    protected String mName;

    public Long getIdRound() {
        return mIdRound;
    }

    public void setIdRound(Long idRound) {
        mIdRound = idRound;
    }

    public Long getIdTournament() {
        return mIdTournament;
    }

    public void setIdTournament(Long idTournament) {
        mIdTournament = idTournament;
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

    public Long getRoundType() {
        return mRoundType;
    }

    public void setRoundType(Long roundType) {
        mRoundType = roundType;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Round)) return false;

        Round round = (Round) o;

        if (mEndDate != null ? !mEndDate.equals(round.mEndDate) : round.mEndDate != null)
            return false;
        if (mIdRound != null ? !mIdRound.equals(round.mIdRound) : round.mIdRound != null)
            return false;
        if (mIdTournament != null ? !mIdTournament.equals(round.mIdTournament) : round.mIdTournament != null)
            return false;
        if (mName != null ? !mName.equals(round.mName) : round.mName != null) return false;
        if (mRoundType != null ? !mRoundType.equals(round.mRoundType) : round.mRoundType != null)
            return false;
        if (mStartDate != null ? !mStartDate.equals(round.mStartDate) : round.mStartDate != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = mIdRound != null ? mIdRound.hashCode() : 0;
        result = 31 * result + (mIdTournament != null ? mIdTournament.hashCode() : 0);
        result = 31 * result + (mStartDate != null ? mStartDate.hashCode() : 0);
        result = 31 * result + (mEndDate != null ? mEndDate.hashCode() : 0);
        result = 31 * result + (mRoundType != null ? mRoundType.hashCode() : 0);
        result = 31 * result + (mName != null ? mName.hashCode() : 0);
        return result;
    }
}
