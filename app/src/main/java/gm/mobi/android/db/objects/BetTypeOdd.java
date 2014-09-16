package gm.mobi.android.db.model.objects;

/**
 * Created by InmaculadaAlcon on 14/08/2014.
 */
public class BetTypeOdd extends Synchronized {
    protected Long mIdBetTypeOdd;
    protected Long mIdMatchBetType;
    protected String mName;
    protected String mComment;
    protected Double mValue;
    protected String mUrl;

    public Long getIdBetTypeOdd() {
        return mIdBetTypeOdd;
    }

    public void setIdBetTypeOdd(Long idBetTypeOdd) {
        mIdBetTypeOdd = idBetTypeOdd;
    }

    public Long getIdMatchBetType() {
        return mIdMatchBetType;
    }

    public void setIdMatchBetType(Long idMatchBetType) {
        mIdMatchBetType = idMatchBetType;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getComment() {
        return mComment;
    }

    public void setComment(String comment) {
        mComment = comment;
    }

    public Double getValue() {
        return mValue;
    }

    public void setValue(Double value) {
        mValue = value;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BetTypeOdd)) return false;
        if (!super.equals(o)) return false;

        BetTypeOdd that = (BetTypeOdd) o;

        if (mComment != null ? !mComment.equals(that.mComment) : that.mComment != null)
            return false;
        if (mIdBetTypeOdd != null ? !mIdBetTypeOdd.equals(that.mIdBetTypeOdd) : that.mIdBetTypeOdd != null)
            return false;
        if (mIdMatchBetType != null ? !mIdMatchBetType.equals(that.mIdMatchBetType) : that.mIdMatchBetType != null)
            return false;
        if (mName != null ? !mName.equals(that.mName) : that.mName != null) return false;
        if (mUrl != null ? !mUrl.equals(that.mUrl) : that.mUrl != null) return false;
        if (mValue != null ? !mValue.equals(that.mValue) : that.mValue != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (mIdBetTypeOdd != null ? mIdBetTypeOdd.hashCode() : 0);
        result = 31 * result + (mIdMatchBetType != null ? mIdMatchBetType.hashCode() : 0);
        result = 31 * result + (mName != null ? mName.hashCode() : 0);
        result = 31 * result + (mComment != null ? mComment.hashCode() : 0);
        result = 31 * result + (mValue != null ? mValue.hashCode() : 0);
        result = 31 * result + (mUrl != null ? mUrl.hashCode() : 0);
        return result;
    }
}

