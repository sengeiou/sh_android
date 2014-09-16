package gm.mobi.android.db.model.objects;

/**
 * Created by InmaculadaAlcon on 14/08/2014.
 */
public class BetType extends Synchronized {
    protected Long mIdBetType;
    protected String mUniqueKey;
    protected String mName;
    protected String mComment;
    protected Long mAlwaysVisible;
    protected Long mWeight;
    protected String mTitle;

    public Long getIdBetType() {
        return mIdBetType;
    }

    public void setIdBetType(Long idBetType) {
        mIdBetType = idBetType;
    }

    public String getUniqueKey() {
        return mUniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        mUniqueKey = uniqueKey;
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

    public Long getAlwaysVisible() {
        return mAlwaysVisible;
    }

    public void setAlwaysVisible(Long alwaysVisible) {
        mAlwaysVisible = alwaysVisible;
    }

    public Long getWeight() {
        return mWeight;
    }

    public void setWeight(Long weight) {
        mWeight = weight;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BetType)) return false;
        if (!super.equals(o)) return false;

        BetType betType = (BetType) o;

        if (mAlwaysVisible != null ? !mAlwaysVisible.equals(betType.mAlwaysVisible) : betType.mAlwaysVisible != null)
            return false;
        if (mComment != null ? !mComment.equals(betType.mComment) : betType.mComment != null)
            return false;
        if (mIdBetType != null ? !mIdBetType.equals(betType.mIdBetType) : betType.mIdBetType != null)
            return false;
        if (mName != null ? !mName.equals(betType.mName) : betType.mName != null) return false;
        if (mTitle != null ? !mTitle.equals(betType.mTitle) : betType.mTitle != null) return false;
        if (mUniqueKey != null ? !mUniqueKey.equals(betType.mUniqueKey) : betType.mUniqueKey != null)
            return false;
        if (mWeight != null ? !mWeight.equals(betType.mWeight) : betType.mWeight != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (mIdBetType != null ? mIdBetType.hashCode() : 0);
        result = 31 * result + (mUniqueKey != null ? mUniqueKey.hashCode() : 0);
        result = 31 * result + (mName != null ? mName.hashCode() : 0);
        result = 31 * result + (mComment != null ? mComment.hashCode() : 0);
        result = 31 * result + (mAlwaysVisible != null ? mAlwaysVisible.hashCode() : 0);
        result = 31 * result + (mWeight != null ? mWeight.hashCode() : 0);
        result = 31 * result + (mTitle != null ? mTitle.hashCode() : 0);
        return result;
    }
}
