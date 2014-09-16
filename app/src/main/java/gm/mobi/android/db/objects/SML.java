package gm.mobi.android.db.model.objects;

/**
 * Created by InmaculadaAlcon on 14/08/2014.
 */
public class SML extends Synchronized{
    protected Long mIdSml;
    protected Long mSound;
    protected Long mMessage;
    protected Long mLanguage;

    public Long getIdSml() {
        return mIdSml;
    }

    public void setIdSml(Long idSml) {
        mIdSml = idSml;
    }

    public Long getSound() {
        return mSound;
    }

    public void setSound(Long sound) {
        mSound = sound;
    }

    public Long getMessage() {
        return mMessage;
    }

    public void setMessage(Long message) {
        mMessage = message;
    }

    public Long getLanguage() {
        return mLanguage;
    }

    public void setLanguage(Long language) {
        mLanguage = language;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SML)) return false;
        if (!super.equals(o)) return false;

        SML sml = (SML) o;

        if (mIdSml != null ? !mIdSml.equals(sml.mIdSml) : sml.mIdSml != null) return false;
        if (mLanguage != null ? !mLanguage.equals(sml.mLanguage) : sml.mLanguage != null)
            return false;
        if (mMessage != null ? !mMessage.equals(sml.mMessage) : sml.mMessage != null) return false;
        if (mSound != null ? !mSound.equals(sml.mSound) : sml.mSound != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (mIdSml != null ? mIdSml.hashCode() : 0);
        result = 31 * result + (mSound != null ? mSound.hashCode() : 0);
        result = 31 * result + (mMessage != null ? mMessage.hashCode() : 0);
        result = 31 * result + (mLanguage != null ? mLanguage.hashCode() : 0);
        return result;
    }
}
