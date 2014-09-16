package gm.mobi.android.db.model.objects;

/**
 * Created by InmaculadaAlcon on 14/08/2014.
 */
public class Subscription extends Synchronized {
    protected Long mIdDevice;
    protected Long mIdMatch;
    protected Long mIdTeam;
    protected Long mIdSubscription;
    protected Long mIdAllEvents;
    protected Long mIdSML;
    protected Long mNegation;

    public Long getIdDevice() {
        return mIdDevice;
    }

    public void setIdDevice(Long idDevice) {
        mIdDevice = idDevice;
    }

    public Long getIdMatch() {
        return mIdMatch;
    }

    public void setIdMatch(Long idMatch) {
        mIdMatch = idMatch;
    }

    public Long getIdTeam() {
        return mIdTeam;
    }

    public void setIdTeam(Long idTeam) {
        mIdTeam = idTeam;
    }

    public Long getIdSubscription() {
        return mIdSubscription;
    }

    public void setIdSubscription(Long idSubscription) {
        mIdSubscription = idSubscription;
    }

    public Long getIdAllEvents() {
        return mIdAllEvents;
    }

    public void setIdAllEvents(Long idAllEvents) {
        mIdAllEvents = idAllEvents;
    }

    public Long getIdSML() {
        return mIdSML;
    }

    public void setIdSML(Long idSML) {
        mIdSML = idSML;
    }

    public Long getNegation() {
        return mNegation;
    }

    public void setNegation(Long negation) {
        mNegation = negation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Subscription)) return false;
        if (!super.equals(o)) return false;

        Subscription that = (Subscription) o;

        if (mIdAllEvents != null ? !mIdAllEvents.equals(that.mIdAllEvents) : that.mIdAllEvents != null)
            return false;
        if (mIdDevice != null ? !mIdDevice.equals(that.mIdDevice) : that.mIdDevice != null)
            return false;
        if (mIdMatch != null ? !mIdMatch.equals(that.mIdMatch) : that.mIdMatch != null)
            return false;
        if (mIdSML != null ? !mIdSML.equals(that.mIdSML) : that.mIdSML != null) return false;
        if (mIdSubscription != null ? !mIdSubscription.equals(that.mIdSubscription) : that.mIdSubscription != null)
            return false;
        if (mIdTeam != null ? !mIdTeam.equals(that.mIdTeam) : that.mIdTeam != null) return false;
        if (mNegation != null ? !mNegation.equals(that.mNegation) : that.mNegation != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (mIdDevice != null ? mIdDevice.hashCode() : 0);
        result = 31 * result + (mIdMatch != null ? mIdMatch.hashCode() : 0);
        result = 31 * result + (mIdTeam != null ? mIdTeam.hashCode() : 0);
        result = 31 * result + (mIdSubscription != null ? mIdSubscription.hashCode() : 0);
        result = 31 * result + (mIdAllEvents != null ? mIdAllEvents.hashCode() : 0);
        result = 31 * result + (mIdSML != null ? mIdSML.hashCode() : 0);
        result = 31 * result + (mNegation != null ? mNegation.hashCode() : 0);
        return result;
    }
}

