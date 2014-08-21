package gm.mobi.android.db.model.objects;

/**
 * Created by InmaculadaAlcon on 14/08/2014.
 */
public class Provider extends Synchronized {
    protected Long mIdProvider;
    protected String mName;
    protected String mUniqueKey;
    protected Long mActive;
    protected Long mVisibleIOS;
    protected Long mVisibleAndroid;
    protected Long mVisibleWP;
    protected Long mWeight;
    protected String mDisclaimer;
    protected String mComment;
    protected String mRegistryUrl;

    public Long getIdProvider() {
        return mIdProvider;
    }

    public void setIdProvider(Long idProvider) {
        mIdProvider = idProvider;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getUniqueKey() {
        return mUniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        mUniqueKey = uniqueKey;
    }

    public Long getActive() {
        return mActive;
    }

    public void setActive(Long active) {
        mActive = active;
    }

    public Long getVisibleIOS() {
        return mVisibleIOS;
    }

    public void setVisibleIOS(Long visibleIOS) {
        mVisibleIOS = visibleIOS;
    }

    public Long getVisibleAndroid() {
        return mVisibleAndroid;
    }

    public void setVisibleAndroid(Long visibleAndroid) {
        mVisibleAndroid = visibleAndroid;
    }

    public Long getVisibleWP() {
        return mVisibleWP;
    }

    public void setVisibleWP(Long visibleWP) {
        mVisibleWP = visibleWP;
    }

    public Long getWeight() {
        return mWeight;
    }

    public void setWeight(Long weight) {
        mWeight = weight;
    }

    public String getDisclaimer() {
        return mDisclaimer;
    }

    public void setDisclaimer(String disclaimer) {
        mDisclaimer = disclaimer;
    }

    public String getComment() {
        return mComment;
    }

    public void setComment(String comment) {
        mComment = comment;
    }

    public String getRegistryUrl() {
        return mRegistryUrl;
    }

    public void setRegistryUrl(String registryUrl) {
        mRegistryUrl = registryUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Provider)) return false;
        if (!super.equals(o)) return false;

        Provider provider = (Provider) o;

        if (mActive != null ? !mActive.equals(provider.mActive) : provider.mActive != null)
            return false;
        if (mComment != null ? !mComment.equals(provider.mComment) : provider.mComment != null)
            return false;
        if (mDisclaimer != null ? !mDisclaimer.equals(provider.mDisclaimer) : provider.mDisclaimer != null)
            return false;
        if (mIdProvider != null ? !mIdProvider.equals(provider.mIdProvider) : provider.mIdProvider != null)
            return false;
        if (mName != null ? !mName.equals(provider.mName) : provider.mName != null) return false;
        if (mRegistryUrl != null ? !mRegistryUrl.equals(provider.mRegistryUrl) : provider.mRegistryUrl != null)
            return false;
        if (mUniqueKey != null ? !mUniqueKey.equals(provider.mUniqueKey) : provider.mUniqueKey != null)
            return false;
        if (mVisibleAndroid != null ? !mVisibleAndroid.equals(provider.mVisibleAndroid) : provider.mVisibleAndroid != null)
            return false;
        if (mVisibleIOS != null ? !mVisibleIOS.equals(provider.mVisibleIOS) : provider.mVisibleIOS != null)
            return false;
        if (mVisibleWP != null ? !mVisibleWP.equals(provider.mVisibleWP) : provider.mVisibleWP != null)
            return false;
        if (mWeight != null ? !mWeight.equals(provider.mWeight) : provider.mWeight != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (mIdProvider != null ? mIdProvider.hashCode() : 0);
        result = 31 * result + (mName != null ? mName.hashCode() : 0);
        result = 31 * result + (mUniqueKey != null ? mUniqueKey.hashCode() : 0);
        result = 31 * result + (mActive != null ? mActive.hashCode() : 0);
        result = 31 * result + (mVisibleIOS != null ? mVisibleIOS.hashCode() : 0);
        result = 31 * result + (mVisibleAndroid != null ? mVisibleAndroid.hashCode() : 0);
        result = 31 * result + (mVisibleWP != null ? mVisibleWP.hashCode() : 0);
        result = 31 * result + (mWeight != null ? mWeight.hashCode() : 0);
        result = 31 * result + (mDisclaimer != null ? mDisclaimer.hashCode() : 0);
        result = 31 * result + (mComment != null ? mComment.hashCode() : 0);
        result = 31 * result + (mRegistryUrl != null ? mRegistryUrl.hashCode() : 0);
        return result;
    }
}
