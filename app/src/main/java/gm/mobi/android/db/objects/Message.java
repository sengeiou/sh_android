package gm.mobi.android.db.model.objects;

/**
 * Created by InmaculadaAlcon on 14/08/2014.
 */
public class Message extends Synchronized {
    protected Long mIdMessage;
    protected Long mPlatform;
    protected String mLocale;
    protected String mMessage;

    public Long getIdMessage() {
        return mIdMessage;
    }

    public void setIdMessage(Long idMessage) {
        mIdMessage = idMessage;
    }

    public Long getPlatform() {
        return mPlatform;
    }

    public void setPlatform(Long platform) {
        mPlatform = platform;
    }

    public String getLocale() {
        return mLocale;
    }

    public void setLocale(String locale) {
        mLocale = locale;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message)) return false;
        if (!super.equals(o)) return false;

        Message message = (Message) o;

        if (mIdMessage != null ? !mIdMessage.equals(message.mIdMessage) : message.mIdMessage != null)
            return false;
        if (mLocale != null ? !mLocale.equals(message.mLocale) : message.mLocale != null)
            return false;
        if (mMessage != null ? !mMessage.equals(message.mMessage) : message.mMessage != null)
            return false;
        if (mPlatform != null ? !mPlatform.equals(message.mPlatform) : message.mPlatform != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (mIdMessage != null ? mIdMessage.hashCode() : 0);
        result = 31 * result + (mPlatform != null ? mPlatform.hashCode() : 0);
        result = 31 * result + (mLocale != null ? mLocale.hashCode() : 0);
        result = 31 * result + (mMessage != null ? mMessage.hashCode() : 0);
        return result;
    }
}
